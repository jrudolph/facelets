package com.sun.facelets.bean;

import java.util.ArrayList;
import java.util.List;

public final class Company {

    private List departments;
    private String name;
    private Employee president;
    
    public Company() {
        super();
        this.departments = new ArrayList();
    }

    public List getDepartments() {
        return departments;
    }

    public void setDepartments(List departments) {
        this.departments = departments;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Employee getPresident() {
        return president;
    }

    public void setPresident(Employee president) {
        this.president = president;
    }

}
