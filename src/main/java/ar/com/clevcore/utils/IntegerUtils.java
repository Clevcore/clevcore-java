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

}
