package block_party.regression;

final class TestSupport {
    private TestSupport() {
    }

    static <T extends Enum<T>> void assertTrait(T expected, T actual, String message) {
        assertEquals(expected, actual, message);
        assertEquals(expected.name(), actual.name(), message + " value");
    }

    static void assertEquals(Object expected, Object actual, String message) {
        if (expected == null ? actual != null : !expected.equals(actual)) {
            throw new AssertionError(message + ": expected <" + expected + "> but was <" + actual + ">");
        }
    }

    static void assertEquals(int expected, int actual, String message) {
        if (expected != actual) {
            throw new AssertionError(message + ": expected <" + expected + "> but was <" + actual + ">");
        }
    }

    static void assertTrue(boolean value, String message) {
        if (!value) {
            throw new AssertionError(message);
        }
    }

    static void assertFalse(boolean value, String message) {
        if (value) {
            throw new AssertionError(message);
        }
    }

    static void assertNull(Object value, String message) {
        if (value != null) {
            throw new AssertionError(message + ": expected <null> but was <" + value + ">");
        }
    }

    static void assertNotNull(Object value, String message) {
        if (value == null) {
            throw new AssertionError(message + ": expected a non-null value");
        }
    }

    static void assertThrows(Class<? extends Throwable> expected, ThrowingRunnable runnable, String message) {
        try {
            runnable.run();
        } catch (Throwable actual) {
            if (expected.isInstance(actual)) {
                return;
            }
            throw new AssertionError(message + ": expected <" + expected.getName() + "> but was <" + actual.getClass().getName() + ">", actual);
        }
        throw new AssertionError(message + ": expected <" + expected.getName() + "> but nothing was thrown");
    }

    interface ThrowingRunnable {
        void run() throws Throwable;
    }

    static Object getField(Object target, String name) {
        Class<?> type = target.getClass();
        while (type != null) {
            try {
                java.lang.reflect.Field field = type.getDeclaredField(name);
                field.setAccessible(true);
                return field.get(target);
            } catch (NoSuchFieldException ignored) {
                type = type.getSuperclass();
            } catch (IllegalAccessException e) {
                throw new AssertionError("Could not read field " + name, e);
            }
        }
        throw new AssertionError("Missing field " + name + " on " + target.getClass().getName());
    }

    static void setField(Object target, String name, Object value) {
        Class<?> type = target.getClass();
        while (type != null) {
            try {
                java.lang.reflect.Field field = type.getDeclaredField(name);
                field.setAccessible(true);
                field.set(target, value);
                return;
            } catch (NoSuchFieldException ignored) {
                type = type.getSuperclass();
            } catch (IllegalAccessException e) {
                throw new AssertionError("Could not write field " + name, e);
            }
        }
        throw new AssertionError("Missing field " + name + " on " + target.getClass().getName());
    }
}
