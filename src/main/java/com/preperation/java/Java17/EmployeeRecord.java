package com.preperation.java.Java17;

/*
Java automatically generates:
    Private final fields
    Constructor
    Getter-like methods (id(), name())
    equals()
    hashCode()
    toString()
 */
public record EmployeeRecord(int id, String name, String role, double salary) {

//    Compact Constructor (Can add validation logic)
    public EmployeeRecord {
        if(id < 0 || role == null || name == null || salary < 0){
            throw new IllegalArgumentException("Invalid Input.");
        }
    }

    public String displayIDName(){
        return id + " " + name;
    }
}

/*
Features of Records
1. Immutable by Default
2. Automatic Methods (equals(), hashCode(), toString())
3. Compact Constructor
4. Additional Methods Allowed
*/
