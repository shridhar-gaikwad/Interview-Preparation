package com.preperation;

class Employee {
    int id;
    String name;

    Employee(String name, int id) {
        this.name = name;
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof Employee)) return false;

        return this.hashCode() == obj.hashCode() && this.id == ((Employee) obj).id;

    }

    @Override
    public int hashCode() {
        return Integer.hashCode(name.hashCode());
    }
}
