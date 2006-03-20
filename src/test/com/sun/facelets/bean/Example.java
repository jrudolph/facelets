package com.sun.facelets.bean;

public class Example {
    
    private static String[] Departments = new String[] { "HR", "RD" };

    public Example() {
        super();
    }
    
    public static Company createCompany() {
        Company c = new Company();
        c.setName("Enverio");
        c.setPresident(new Employee(1, "Hookom", "Jacob", true));
        c.getDepartments().add(createHR());
        c.getDepartments().add(createRD());
        return c;
    }
    
    public static Department createDepartment() {
        return createRD();
    }
    
    public static Department createHR() {
        Department d = new Department();
        d.setDirector(new Employee(2, "Ashenbrener", "Aubrey", true));
        d.setName("HR");
        d.getEmployees().add(new Employee(3, "Ellen", "Sue", false));
        d.getEmployees().add(new Employee(4, "Scooner", "Mary", false));
        return d;
    }
    
    public static Department createRD() {
        Department d = new Department();
        d.setDirector(new Employee(5, "Winer", "Adam", true));
        d.setName("RD");
        d.getEmployees().add(new Employee(6, "Burns", "Ed", false));
        d.getEmployees().add(new Employee(7, "Lubke", "Ryan", false));
        d.getEmployees().add(new Employee(8, "Kitain", "Roger", false));
        return d;
    }

}
