package com.sun.facelets.bean;

import java.util.ArrayList;
import java.util.List;

public final class Department {

    private String name;
    private List employees;
    private Employee director;
    
    public Department() {
        super();
        this.employees = new ArrayList();
    }

    public Employee getDirector() {
        return director;
    }

    public void setDirector(Employee director) {
        this.director = director;
    }

    public List getEmployees() {
        return employees;
    }

    public void setEmployees(List employees) {
        this.employees = employees;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
