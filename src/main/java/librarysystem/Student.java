//Student class where the basic details of student are stored and initialized


package librarysystem; //Calling the library system package

// Stores details of a student
public class Student {
    private int studentId;
    private String name;
    private String department;
    private String course;

    //Constructor
    public Student(int studentId, String name, String department, String course) {
        this.studentId = studentId;
        this.name = name;
        this.department = department;
        this.course = course;
    }
    // Getters
    public int getStudentId() { return studentId; }
    public String getName() { return name; }
    public String getDepartment() { return department; }
    public String getCourse() { return course; }


    //toString() is used
    public String toString() {
        return "Student[ID=" + studentId + ", Name=" + name +
                ", Dept=" + department + ", Course=" + course + "]";
    }
}



