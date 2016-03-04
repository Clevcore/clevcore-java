package ar.com.clevcore.utils;

public final class IntegerUtils {

    private IntegerUtils() {
        throw new AssertionError();
    }

    public static Integer[] createInteger(int begin, int end) {
        Integer[] array = null;

        if (begin < end) {
            array = new Integer[end - begin + 1];
            for (int i = 0; i < array.length; i++) {
                array[i] = begin + i;
            }
        } else if (begin > end) {
            array = new Integer[begin - end + 1];
            for (int i = 0; i < array.length; i++) {
                array[i] = begin - i;
            }
        } else {
            array = new Integer[1];
            array[0] = begin;
        }

        return array;
    }

    public static Boolean isInteger(String value) {
        if (value == null) {
            return false;
        }

        int length = value.length();

        if (length == 0) {
            return false;
        }

        if (value.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
        }

        for (int i = 1; i < length; i++) {
            if (!Character.isDigit(value.charAt(i))) {
                return false;
            }
        }

        return true;
    }

}
