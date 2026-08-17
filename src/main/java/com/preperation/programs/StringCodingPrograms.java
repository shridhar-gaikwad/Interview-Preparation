package com.preperation.programs;

import java.util.HashMap;
import java.util.Map;

public class StringCodingPrograms {

//    find first non repeating character in a string
    public static void findFirstNonRepeatingCharacter(String str) {
//      str = str.toLowerCase();      // In case you want to ignore case sensitivity
        for(int i=0; i<str.length(); i++){
            char c = str.charAt(i);
            // It will consider uppercase and lowercase letters as different characters
            if(str.indexOf(c) == str.lastIndexOf(c)){
                System.out.println("First non-repeating character is: " + c);
                break;
            }
        }
    }

    public static void findNonRepeatingCharacterUsingMap(String str) {
        Map<Character, Integer> charCountMap = new HashMap<>();
        for (char c : str.toCharArray()) {
            charCountMap.put(c, charCountMap.getOrDefault(c, 0) + 1);
        }
        for (char c : str.toCharArray()) {
            if (charCountMap.get(c) == 1) {
                System.out.println("First non-repeating character is: " + c);
                break;
            }
        }
    }

    public static void isPalindromeString(String str) {
        int i=0, j=str.length()-1;
        boolean status = true;
        while (i<j){
            if(str.charAt(i++) != str.charAt(j--)) status = false;
        }
        if (status) System.out.println("String is palindrome.");
        else System.out.println("String is not palindrome.");
    }
}
