package model;
import Status.Role;
public abstract class Users {
    private int id;
    private Role role;
    private String password;
    private String name;
    private String address;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public abstract void display();
}