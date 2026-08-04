package com.preperation.java.designPatterns;

import java.util.Date;
import java.util.List;

public final class ImmutableClassMutableObject {    // Rule 1: final class

    // Rule 2: private final fields
    private final String name;
    private final int age;
    private final Date birthDate;
    private final List<String> hobbies;

    // Rule 3: Constructor uses defensive copying
    public ImmutableClassMutableObject(String name, int age, Date birthDate, List<String> hobbies) {
        this.name = name;
        this.age = age;
        this.birthDate = new Date(birthDate.getTime()); // Defensive copy of Date object
        this.hobbies = List.copyOf(hobbies); // Defensive copy of list object
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

}