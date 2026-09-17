package model;
public class Student extends Users {
    private float gpa;

    @Override
    public void display() {
        System.out.println("ID: " + getId());
        System.out.println("Name: " + getName());
        System.out.println("Address: " + getAddress());
        System.out.println("GPA: " + gpa);
    }

    public float getGpa() { return gpa; }
    public void setGpa(float gpa) { this.gpa = gpa; }
}