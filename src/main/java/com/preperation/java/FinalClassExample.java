package com.preperation.java;

import java.util.Date;

public final class FinalClassExample {          // final class cannot be subclassed

    private final String name;                  // private final field
    private final int age;
    private final Date birthDate;

    private FinalClassExample(String name, int age, Date birthDate) {           // initializing final fields in constructor
        // private constructor to prevent instantiation
        this.name = name;
        this.age = age;
        this.birthDate = new Date(birthDate.getTime());                         // defensive copy
    }

}
