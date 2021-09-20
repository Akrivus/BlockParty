package block_party.utils;

import net.minecraft.locale.Language;

public class Trans {

    public static String late(String key, String alt) {
        key = lationNotFound(key) ? alt : key;
        return late(key);
    }

    public static boolean lationNotFound(String key) {
        return !map().has(key);
    }

    private static Language map() {
        return Language.getInstance();
    }

    public static String late(String key) {
        return map().getOrDefault(key);
    }
}
