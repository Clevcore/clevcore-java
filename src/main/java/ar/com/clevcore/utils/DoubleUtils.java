package ar.com.clevcore.utils;

public final class DoubleUtils {

    private DoubleUtils() {
        throw new AssertionError();
    }

    public static Double round(Double value, Integer nDecimals) {
        Double aux = Math.pow(10, nDecimals);

        return Math.round(aux * value) / aux;
    }

}
