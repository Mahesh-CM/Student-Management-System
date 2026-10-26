# Student Management System

A console application built with **Java, JDBC and MySQL** to manage students, faculty, courses and examination records.

## Features
- Full CRUD for students, faculty, courses and exams (JDBC `PreparedStatement`s, try-with-resources)
- Course enrollment (student <-> course, many-to-many)
- Exam scheduling, marks entry and result reports per exam / per student
- Search students by name
- Input validation and graceful handling of SQL errors

## OOP design
- **Abstraction**: `CrudDao<T>` interface implemented by every DAO
- **Inheritance**: `Student` and `Faculty` extend abstract `Person`
- **Polymorphism**: `Person.getRole()` overridden by subclasses; DAOs programmed to the interface
- **Encapsulation**: private fields with getters/setters in all model classes
- **Layering**: `model` -> `dao` -> `ui` / `Main`, with `util.DBConnection` for connectivity

## Project structure
```
schema.sql          MySQL tables + sample data
db.properties       JDBC url / user / password
src/com/sms/
  Main.java         menu-driven application
  model/            Person, Student, Faculty, Course, Exam, ExamResult
  dao/              CrudDao, StudentDao, FacultyDao, CourseDao, ExamDao
  ui/Console.java   validated console input
  util/DBConnection.java
```

## Setup
1. Install JDK 17+ and MySQL 8+.
2. Download the MySQL Connector/J jar (https://dev.mysql.com/downloads/connector/j/) and place it in a `lib/` folder.
3. Create the database: `mysql -u root -p < schema.sql`
4. Edit `db.properties` with your MySQL password.

## Build and run (from the project folder)
Linux / macOS:
```
mkdir -p out
javac -d out $(find src -name "*.java")
java -cp "out:lib/*" com.sms.Main
```
Windows (PowerShell):
```
mkdir out
javac -d out (Get-ChildItem -Recurse src -Filter *.java).FullName
java -cp "out;lib/*" com.sms.Main
```
The app must be run from the project folder so it can find `db.properties`.

## Database schema
`faculty`, `course`, `student`, `enrollment`, `exam`, `exam_result` with foreign keys
(deleting a student/course cascades to enrollments/results; deleting a faculty member leaves their courses unassigned).
