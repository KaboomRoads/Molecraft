package com.kaboomroads.molecraft.util;

import java.util.TreeMap;

public class RomanNumber {
    private final static TreeMap<Integer, String> NUMERALS = new TreeMap<>();

    static {
        NUMERALS.put(1000, "M");
        NUMERALS.put(900, "CM");
        NUMERALS.put(500, "D");
        NUMERALS.put(400, "CD");
        NUMERALS.put(100, "C");
        NUMERALS.put(90, "XC");
        NUMERALS.put(50, "L");
        NUMERALS.put(40, "XL");
        NUMERALS.put(10, "X");
        NUMERALS.put(9, "IX");
        NUMERALS.put(5, "V");
        NUMERALS.put(4, "IV");
        NUMERALS.put(1, "I");

    }

    public static String toRoman(int number) {
        int l = NUMERALS.floorKey(number);
        if (number == l) return NUMERALS.get(number);
        return NUMERALS.get(l) + toRoman(number - l);
    }
}