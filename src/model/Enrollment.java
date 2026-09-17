package model;

import Status.EnrollmentStatus;
public class Enrollment {
    private int studentId;
    private int courseId;
    private String term;
    private float grade;
    private EnrollmentStatus status;

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }

    public String getTerm() { return term; }
    public void setTerm(String term) { this.term = term; }

    public float getGrade() { return grade; }
    public void setGrade(float grade) { this.grade = grade; }

    public EnrollmentStatus getStatus() { return status; }
    public void setStatus(EnrollmentStatus status) { this.status = status; }
}