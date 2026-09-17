package model;

public class Course {
    private int courseId;
    private String courseName;
    private int professorId;
    private float minGpaRequired;
    private int creditHours;

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public int getProfessorId() { return professorId; }
    public void setProfessorId(int professorId) { this.professorId = professorId; }

    public float getMinGpaRequired() { return minGpaRequired; }
    public void setMinGpaRequired(float minGpaRequired) { this.minGpaRequired = minGpaRequired; }

    public int getCreditHours() { return creditHours; }
    public void setCreditHours(int creditHours) { this.creditHours = creditHours; }
}