package com.lt;

public class test {
    public static void main(String[] args) {
        System.out.println(test1());
    }

    public static int test1() {
        int i = 0;
        int j = 0;
        try {
            j = 1;
            return i + j;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            i = 2;
            return i + j;
        }
    }

}
