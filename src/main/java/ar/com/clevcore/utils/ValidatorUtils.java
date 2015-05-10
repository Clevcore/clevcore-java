package ar.com.clevcore.utils;

import java.util.regex.Pattern;

public final class ValidatorUtils {

    private ValidatorUtils() {
        throw new AssertionError();
    }

    public static boolean emailValidator(String value) {
        return Pattern.compile("^[a-zA-Z0-9][+a-zA-Z0-9._-]*@[a-zA-Z0-9][a-zA-Z0-9._-]*[a-zA-Z0-9]*\\.[a-zA-Z]{2,4}$")
                .matcher(value).matches();
    }

    public static boolean emptyValidator(String value) {
        return "".equals(StringUtils.trimAll(value));
    }

    public static boolean yearValidator(int year, int maxYear, int minYear) {
        return (year <= maxYear && year >= minYear);
    }

}
