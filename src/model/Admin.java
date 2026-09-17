package model;

import Status.AdminLevel;

public class Admin extends Users {
    private AdminLevel adminLevel;

    @Override
    public void display() {
        System.out.println("ID: " + getId());
        System.out.println("Name: " + getName());
        System.out.println("Address: " + getAddress());
        System.out.println("Admin level: " + adminLevel);
    }

    public AdminLevel getAdminLevel() { return adminLevel; }
    public void setAdminLevel(AdminLevel adminLevel) { this.adminLevel = adminLevel; }
}