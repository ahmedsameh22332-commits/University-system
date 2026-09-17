package model;

import Status.Role;
import Status.RegistrationStatus;

public class Registration {
    private int requestId;
    private String name;
    private String address;
    private Role role;
    private String password;
    private RegistrationStatus status;
    private String reason;
    private Integer assignedId;
    private String degree;
    private Integer courseId;

    public int getRequestId() { return requestId; }
    public void setRequestId(int requestId) { this.requestId = requestId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public RegistrationStatus getStatus() { return status; }
    public void setStatus(RegistrationStatus status) { this.status = status; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public Integer getAssignedId() { return assignedId; }
    public void setAssignedId(Integer assignedId) { this.assignedId = assignedId; }

    public String getDegree() { return degree; }
    public void setDegree(String degree) { this.degree = degree; }

    public Integer getCourseId() { return courseId; }
    public void setCourseId(Integer courseId) { this.courseId = courseId; }
}