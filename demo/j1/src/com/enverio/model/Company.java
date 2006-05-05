package com.enverio.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.digester.Digester;

import com.enverio.util.Predicate;
import com.enverio.util.Projections;
import com.enverio.util.Transformer;

public class Company {

    private List<Department> departments = new ArrayList<Department>();

    public Company() {
        this.loadFromXML();
    }

    public List<Department> getDepartments() {
        return departments;
    }

    public void setDepartments(List<Department> departments) {
        this.departments = departments;
    }

    public Collection<Employee> suggestEmployees(String input) {
        Collection<Employee> emps = Projections.in(this.getAllEmployees(),
                Employee.EmpNamePredicate(input));
        List<Employee> result = new ArrayList<Employee>(emps);
        Collections.sort(result, Employee.EmpNameSort);
        return result.subList(0, Math.min(result.size(), 5));
    }

    public List<Employee> getAllEmployees() {
        List<Employee> emps = new ArrayList<Employee>();
        for (Department d : this.departments) {
            for (Employee e : d.getEmployees()) {
                emps.add(e);
            }
        }
        return emps;
    }

    private void loadFromXML() {
        Digester d = new Digester();

        d.addObjectCreate("company/department", Department.class);
        d.addSetProperties("company/department");
        d.addSetNext("company/department", "add");
        d.addObjectCreate("company/department/employee", Employee.class);
        d.addSetProperties("company/department/employee");
        d.addSetNext("company/department/employee", "addEmployee");

        d.push(this.departments);

        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            d.parse(cl.getResourceAsStream("db.xml"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
