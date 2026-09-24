package studentexcel.controller;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import studentexcel.service.StudentService;

@RestController
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadExcel(
            @RequestParam("file") MultipartFile file) {

        try {

            Map<String, Object> response =
                    studentService.uploadExcel(file);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "status", 400,
                            "message", e.getMessage()
                    ));

        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", 500,
                            "message",
                            "Unexpected server/database failure"
                    ));
        }
    }
}
