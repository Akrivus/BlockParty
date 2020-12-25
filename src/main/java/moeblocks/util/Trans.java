package moeblocks.util;

import net.minecraft.util.text.LanguageMap;

public class Trans {

    private static LanguageMap map() {
        return LanguageMap.getInstance();
    }

    public static boolean lationNotFound(String key) {
        return !map().func_230506_b_(key);
    }

    public static String late(String key) {
        return map().func_230503_a_(key);
    }

    public static String late(String key, String alt) {
        key = lationNotFound(key) ? alt : key;
        return late(key);
    }
}
