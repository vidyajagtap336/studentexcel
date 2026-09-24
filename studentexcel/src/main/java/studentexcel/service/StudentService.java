package studentexcel.service;
import java.util.*;
import java.io.InputStream;

import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import studentexcel.entity.Student;
import studentexcel.repository.StudentRepository;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Map<String, Object> uploadExcel(MultipartFile file) {

        Map<String, Object> response = new LinkedHashMap<>();

        int insertedRows = 0;
        int failedRows = 0;

        List<Map<String, Object>> errors = new ArrayList<>();

        try {

            if (file == null || file.isEmpty()) {
                throw new IllegalArgumentException(
                        "File is missing or empty");
            }

            String fileName = file.getOriginalFilename();

            if (fileName == null ||
                    !fileName.toLowerCase().endsWith(".xlsx")) {

                throw new IllegalArgumentException(
                        "Only .xlsx Excel file is allowed");
            }

            InputStream inputStream = file.getInputStream();

            Workbook workbook = WorkbookFactory.create(inputStream);

            Sheet sheet = workbook.getSheetAt(0);

            DataFormatter formatter = new DataFormatter();

            Row header = sheet.getRow(0);

            if (header == null) {
                throw new IllegalArgumentException(
                        "Excel header is missing");
            }

            Map<String, Integer> columns = new HashMap<>();

            for (Cell cell : header) {

                String columnName =
                        formatter.formatCellValue(cell)
                                .trim()
                                .toLowerCase();

                columns.put(columnName, cell.getColumnIndex());
            }

            String[] requiredColumns = {
                    "student_name",
                    "email",
                    "mobile",
                    "city",
                    "course",
                    "fees"
            };

            for (String column : requiredColumns) {

                if (!columns.containsKey(column)) {
                    throw new IllegalArgumentException(
                            "Missing Excel column: " + column);
                }
            }

            Set<String> excelEmails = new HashSet<>();
            Set<String> excelMobiles = new HashSet<>();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {

                Row row = sheet.getRow(i);

                if (row == null) {
                    continue;
                }

                int excelRowNumber = i + 1;

                List<String> rowErrors = new ArrayList<>();

                String studentName = getCellValue(
                        row, columns.get("student_name"), formatter);

                String email = getCellValue(
                        row, columns.get("email"), formatter);

                String mobile = getCellValue(
                        row, columns.get("mobile"), formatter);

                String city = getCellValue(
                        row, columns.get("city"), formatter);

                String course = getCellValue(
                        row, columns.get("course"), formatter);

                String feesText = getCellValue(
                        row, columns.get("fees"), formatter);

                // Student Name validation
                if (studentName.isBlank()) {
                    rowErrors.add("Student name is blank");
                }

                // Email validation
                if (email.isBlank()) {

                    rowErrors.add("Email is blank");

                } else if (!email.matches(
                        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {

                    rowErrors.add("Invalid email");

                } else {

                    if (studentRepository.existsByEmail(email)) {
                        rowErrors.add(
                                "Email already exists in database");
                    }

                    if (excelEmails.contains(email)) {
                        rowErrors.add(
                                "Duplicate email in Excel");
                    }
                }

                // Mobile validation
                if (mobile.isBlank()) {

                    rowErrors.add("Mobile is blank");

                } else if (!mobile.matches("\\d{10}")) {

                    rowErrors.add(
                            "Mobile number must contain exactly 10 digits");

                } else {

                    if (studentRepository.existsByMobile(mobile)) {
                        rowErrors.add(
                                "Mobile already exists in database");
                    }

                    if (excelMobiles.contains(mobile)) {
                        rowErrors.add(
                                "Duplicate mobile in Excel");
                    }
                }

                // City validation
                if (city.isBlank()) {
                    rowErrors.add("City is blank");
                }

                // Course validation
                if (course.isBlank()) {

                    rowErrors.add("Course is blank");

                } else {

                    Set<String> validCourses = Set.of(
                            "Java",
                            "Spring Boot",
                            "Python",
                            "Django"
                    );

                    if (!validCourses.contains(course)) {
                        rowErrors.add(
                                "Invalid course: " + course);
                    }
                }

                // Fees validation
                Double fees = null;

                if (feesText.isBlank()) {

                    rowErrors.add("Fees is blank");

                } else {

                    try {

                        fees = Double.parseDouble(feesText);

                        if (fees <= 0) {
                            rowErrors.add(
                                    "Fees must be greater than 0");
                        }

                    } catch (NumberFormatException e) {

                        rowErrors.add("Invalid fees value");
                    }
                }

                // Insert valid row
                if (rowErrors.isEmpty()) {

                    Student student = new Student();

                    student.setStudentName(studentName);
                    student.setEmail(email);
                    student.setMobile(mobile);
                    student.setCity(city);
                    student.setCourse(course);
                    student.setFees(fees);

                    studentRepository.save(student);

                    excelEmails.add(email);
                    excelMobiles.add(mobile);

                    insertedRows++;

                } else {

                    failedRows++;

                    Map<String, Object> errorData =
                            new LinkedHashMap<>();

                    errorData.put("row", excelRowNumber);
                    errorData.put("errors", rowErrors);

                    errors.add(errorData);
                }
            }

            workbook.close();

            response.put("message",
                    "Excel processed successfully");

            response.put("totalRows",
                    insertedRows + failedRows);

            response.put("insertedRows",
                    insertedRows);

            response.put("failedRows",
                    failedRows);

            response.put("errors", errors);

            return response;

        } catch (Exception e) {

            throw new RuntimeException(e.getMessage());
        }
    }

    private String getCellValue(
            Row row,
            Integer columnIndex,
            DataFormatter formatter) {

        if (columnIndex == null) {
            return "";
        }

        Cell cell = row.getCell(columnIndex);

        if (cell == null) {
            return "";
        }

        return formatter.formatCellValue(cell).trim();
    }
}