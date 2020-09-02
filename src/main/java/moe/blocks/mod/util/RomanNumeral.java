package moe.blocks.mod.util;

import java.util.TreeMap;

public class RomanNumeral {
    private final static TreeMap<Integer, String> MAP = new TreeMap<>();

    static {
        MAP.put(1000, "M");
        MAP.put(900, "CM");
        MAP.put(500, "D");
        MAP.put(400, "CD");
        MAP.put(100, "C");
        MAP.put(90, "XC");
        MAP.put(50, "L");
        MAP.put(40, "XL");
        MAP.put(10, "X");
        MAP.put(9, "IX");
        MAP.put(5, "V");
        MAP.put(4, "IV");
        MAP.put(1, "I");
        MAP.put(0, "");
    }

    public final static String toRoman(int n) {
        if (MAP.containsKey(n)) {
            return MAP.get(n);
        } else {
            int f = MAP.floorKey(n);
            return MAP.get(f) + toRoman(n - f);
        }
    }
}
