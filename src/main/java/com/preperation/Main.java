package com.preperation;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args){
        String str = "Java";
        StringBuilder sb = new StringBuilder();
        int times = 3;
        String updatedString = str.repeat(3);
        System.out.println(updatedString);
        for(int i=0; i<times; i++){
            sb.append(str).append("\n");
        }

//        System.out.println(sb.toString());

    }
}