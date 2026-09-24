\# Student Excel Upload API



A Spring Boot REST API for Excel-based student data management with row-level validation, duplicate detection, error handling, and MySQL database integration.



\## Features



\- Upload student data through Excel file

\- Validate each Excel row

\- Validate email and mobile number

\- Detect duplicate email and mobile numbers

\- Validate course names

\- Validate fees

\- Insert valid records into MySQL

\- Skip invalid rows

\- Return row-wise validation errors



\## Technologies Used



\- Java 21

\- Spring Boot

\- Spring Data JPA

\- MySQL

\- Apache POI

\- Maven

\- Postman



\## API Endpoint



\### Upload Excel



\*\*POST\*\*

`/students/upload`



Example:



`http://localhost:8080/students/upload`



\### Request



Use `multipart/form-data`.



Parameter:



| Key | Type |

|---|---|

| file | File |



\## Excel Format



The Excel file must contain these columns:



| student\_name | email | mobile | city | course | fees |

|---|---|---|---|---|---|



Supported courses:



\- Java

\- Spring Boot

\- Python

\- Django



\## Validation



The API validates:



\- Student name

\- Email format

\- Duplicate email

\- Mobile number with exactly 10 digits

\- Duplicate mobile number

\- City

\- Course

\- Fees greater than zero



Invalid rows are skipped and their errors are returned in the response.



\## Sample Response



\## Sample Response



```json

{

&#x20; "message": "Excel processed successfully",

&#x20; "totalRows": 5,

&#x20; "insertedRows": 5,

&#x20; "failedRows": 0,

&#x20; "errors": 

}

