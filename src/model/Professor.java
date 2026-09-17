package model;

public class Professor extends Users {
    private String degree;

    @Override
    public void display() {
        System.out.println("ID: " + getId());
        System.out.println("Name: " + getName());
        System.out.println("Address: " + getAddress());
        System.out.println("Degree: " + degree);
    }

    public String getDegree() { return degree; }
    public void setDegree(String degree) { this.degree = degree; }
}