package com.sms;

import com.sms.dao.CourseDao;
import com.sms.dao.ExamDao;
import com.sms.dao.FacultyDao;
import com.sms.dao.StudentDao;
import com.sms.model.*;
import com.sms.ui.Console;
import com.sms.util.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class Main {

    private static final StudentDao students = new StudentDao();
    private static final FacultyDao faculty = new FacultyDao();
    private static final CourseDao courses = new CourseDao();
    private static final ExamDao exams = new ExamDao();

    @FunctionalInterface
    private interface SqlAction { void run() throws SQLException; }

    /** Runs a database action and reports SQL problems without crashing the app. */
    private static void safely(SqlAction action) {
        try {
            action.run();
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try (Connection c = DBConnection.getConnection()) {
            System.out.println("Connected to " + c.getMetaData().getURL());
        } catch (SQLException e) {
            System.out.println("Could not connect to MySQL: " + e.getMessage());
            System.out.println("Check db.properties and that the MySQL server is running.");
            return;
        }

        while (true) {
            System.out.println("\n===== STUDENT MANAGEMENT SYSTEM =====");
            System.out.println("1. Students");
            System.out.println("2. Faculty");
            System.out.println("3. Courses");
            System.out.println("4. Examinations");
            System.out.println("0. Exit");
            switch (Console.readInt("Choose")) {
                case 1 -> studentMenu();
                case 2 -> facultyMenu();
                case 3 -> courseMenu();
                case 4 -> examMenu();
                case 0 -> { System.out.println("Goodbye!"); return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    // ------------------------------------------------------------ students

    private static void studentMenu() {
        while (true) {
            System.out.println("\n--- Students ---");
            System.out.println("1. Add  2. List all  3. Find by ID  4. Search by name");
            System.out.println("5. Update  6. Delete  7. Enroll in course  8. View student's courses");
            System.out.println("0. Back");
            int choice = Console.readInt("Choose");
            if (choice == 0) return;
            safely(() -> {
                switch (choice) {
                    case 1 -> {
                        Student s = new Student(0, Console.readRequired("Name"), Console.readRequired("Email"),
                            Console.readRequired("Phone"), Console.readDate("Date of birth"),
                            Console.readRequired("Department"));
                        System.out.println("Student added with ID " + students.add(s));
                    }
                    case 2 -> printAll(students.findAll());
                    case 3 -> {
                        Optional<Student> s = students.findById(Console.readInt("Student ID"));
                        System.out.println(s.map(Object::toString).orElse("Not found."));
                    }
                    case 4 -> printAll(students.searchByName(Console.readRequired("Name contains")));
                    case 5 -> {
                        Optional<Student> found = students.findById(Console.readInt("Student ID to update"));
                        if (found.isEmpty()) { System.out.println("Not found."); return; }
                        Student s = found.get();
                        s.setName(Console.readOptional("Name", s.getName()));
                        s.setEmail(Console.readOptional("Email", s.getEmail()));
                        s.setPhone(Console.readOptional("Phone", s.getPhone()));
                        s.setDob(Console.readDate("Date of birth", s.getDob()));
                        s.setDepartment(Console.readOptional("Department", s.getDepartment()));
                        System.out.println(students.update(s) ? "Updated." : "Nothing updated.");
                    }
                    case 6 -> {
                        int id = Console.readInt("Student ID to delete");
                        if (Console.confirm("Delete student " + id + " and their enrollments/results?"))
                            System.out.println(students.delete(id) ? "Deleted." : "Not found.");
                    }
                    case 7 -> {
                        int sid = Console.readInt("Student ID");
                        int cid = Console.readInt("Course ID");
                        System.out.println(courses.enroll(sid, cid) ? "Enrolled." : "Not enrolled.");
                    }
                    case 8 -> printAll(courses.findByStudent(Console.readInt("Student ID")));
                    default -> System.out.println("Invalid choice.");
                }
            });
        }
    }

    // ------------------------------------------------------------- faculty

    private static void facultyMenu() {
        while (true) {
            System.out.println("\n--- Faculty ---");
            System.out.println("1. Add  2. List all  3. Find by ID  4. Update  5. Delete  0. Back");
            int choice = Console.readInt("Choose");
            if (choice == 0) return;
            safely(() -> {
                switch (choice) {
                    case 1 -> {
                        Faculty f = new Faculty(0, Console.readRequired("Name"), Console.readRequired("Email"),
                            Console.readRequired("Phone"), Console.readRequired("Department"));
                        System.out.println("Faculty added with ID " + faculty.add(f));
                    }
                    case 2 -> printAll(faculty.findAll());
                    case 3 -> {
                        Optional<Faculty> f = faculty.findById(Console.readInt("Faculty ID"));
                        System.out.println(f.map(Object::toString).orElse("Not found."));
                    }
                    case 4 -> {
                        Optional<Faculty> found = faculty.findById(Console.readInt("Faculty ID to update"));
                        if (found.isEmpty()) { System.out.println("Not found."); return; }
                        Faculty f = found.get();
                        f.setName(Console.readOptional("Name", f.getName()));
                        f.setEmail(Console.readOptional("Email", f.getEmail()));
                        f.setPhone(Console.readOptional("Phone", f.getPhone()));
                        f.setDepartment(Console.readOptional("Department", f.getDepartment()));
                        System.out.println(faculty.update(f) ? "Updated." : "Nothing updated.");
                    }
                    case 5 -> {
                        int id = Console.readInt("Faculty ID to delete");
                        if (Console.confirm("Delete faculty " + id + "? (their courses become unassigned)"))
                            System.out.println(faculty.delete(id) ? "Deleted." : "Not found.");
                    }
                    default -> System.out.println("Invalid choice.");
                }
            });
        }
    }

    // ------------------------------------------------------------- courses

    private static void courseMenu() {
        while (true) {
            System.out.println("\n--- Courses ---");
            System.out.println("1. Add  2. List all  3. Find by ID  4. Update  5. Delete");
            System.out.println("6. Students in a course  7. Unenroll student  0. Back");
            int choice = Console.readInt("Choose");
            if (choice == 0) return;
            safely(() -> {
                switch (choice) {
                    case 1 -> {
                        String code = Console.readRequired("Course code");
                        String title = Console.readRequired("Title");
                        int credits = Console.readInt("Credits");
                        int fid = Console.readInt("Faculty ID (0 for none)");
                        Course c = new Course(0, code, title, credits, fid == 0 ? null : fid);
                        System.out.println("Course added with ID " + courses.add(c));
                    }
                    case 2 -> printAll(courses.findAll());
                    case 3 -> {
                        Optional<Course> c = courses.findById(Console.readInt("Course ID"));
                        System.out.println(c.map(Object::toString).orElse("Not found."));
                    }
                    case 4 -> {
                        Optional<Course> found = courses.findById(Console.readInt("Course ID to update"));
                        if (found.isEmpty()) { System.out.println("Not found."); return; }
                        Course c = found.get();
                        c.setCode(Console.readOptional("Course code", c.getCode()));
                        c.setTitle(Console.readOptional("Title", c.getTitle()));
                        c.setCredits(Console.readInt("Credits", c.getCredits()));
                        int fid = Console.readInt("Faculty ID (0 for none)",
                            c.getFacultyId() == null ? 0 : c.getFacultyId());
                        c.setFacultyId(fid == 0 ? null : fid);
                        System.out.println(courses.update(c) ? "Updated." : "Nothing updated.");
                    }
                    case 5 -> {
                        int id = Console.readInt("Course ID to delete");
                        if (Console.confirm("Delete course " + id + " with its exams and enrollments?"))
                            System.out.println(courses.delete(id) ? "Deleted." : "Not found.");
                    }
                    case 6 -> printAll(courses.findStudentsInCourse(Console.readInt("Course ID")));
                    case 7 -> {
                        int sid = Console.readInt("Student ID");
                        int cid = Console.readInt("Course ID");
                        System.out.println(courses.unenroll(sid, cid) ? "Unenrolled." : "Enrollment not found.");
                    }
                    default -> System.out.println("Invalid choice.");
                }
            });
        }
    }

    // --------------------------------------------------------------- exams

    private static void examMenu() {
        while (true) {
            System.out.println("\n--- Examinations ---");
            System.out.println("1. Schedule exam  2. List exams  3. Update exam  4. Delete exam");
            System.out.println("5. Record/update marks  6. Results of an exam  7. Results of a student  0. Back");
            int choice = Console.readInt("Choose");
            if (choice == 0) return;
            safely(() -> {
                switch (choice) {
                    case 1 -> {
                        Exam e = new Exam(0, Console.readInt("Course ID"), Console.readRequired("Exam name"),
                            Console.readDate("Exam date"), Console.readInt("Maximum marks"));
                        System.out.println("Exam scheduled with ID " + exams.add(e));
                    }
                    case 2 -> printAll(exams.findAll());
                    case 3 -> {
                        Optional<Exam> found = exams.findById(Console.readInt("Exam ID to update"));
                        if (found.isEmpty()) { System.out.println("Not found."); return; }
                        Exam e = found.get();
                        e.setCourseId(Console.readInt("Course ID", e.getCourseId()));
                        e.setName(Console.readOptional("Exam name", e.getName()));
                        e.setDate(Console.readDate("Exam date", e.getDate()));
                        e.setMaxMarks(Console.readInt("Maximum marks", e.getMaxMarks()));
                        System.out.println(exams.update(e) ? "Updated." : "Nothing updated.");
                    }
                    case 4 -> {
                        int id = Console.readInt("Exam ID to delete");
                        if (Console.confirm("Delete exam " + id + " and its results?"))
                            System.out.println(exams.delete(id) ? "Deleted." : "Not found.");
                    }
                    case 5 -> {
                        int examId = Console.readInt("Exam ID");
                        Optional<Exam> exam = exams.findById(examId);
                        if (exam.isEmpty()) { System.out.println("Exam not found."); return; }
                        int studentId = Console.readInt("Student ID");
                        double marks = Console.readDouble("Marks obtained (max " + exam.get().getMaxMarks() + ")");
                        if (marks < 0 || marks > exam.get().getMaxMarks()) {
                            System.out.println("Marks must be between 0 and " + exam.get().getMaxMarks() + ".");
                            return;
                        }
                        exams.saveResult(examId, studentId, marks);
                        System.out.println("Result saved.");
                    }
                    case 6 -> printAll(exams.findResultsByExam(Console.readInt("Exam ID")));
                    case 7 -> printAll(exams.findResultsByStudent(Console.readInt("Student ID")));
                    default -> System.out.println("Invalid choice.");
                }
            });
        }
    }

    private static <T> void printAll(List<T> items) {
        if (items.isEmpty()) {
            System.out.println("No records.");
            return;
        }
        items.forEach(System.out::println);
    }
}
