package ar.com.clevcore.utils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class StringUtils {

    private static final Logger log = LoggerFactory.getLogger(StringUtils.class);

    private static final String SECRET_KEY = "Clevcore";

    private StringUtils() {
        throw new AssertionError();
    }

    public static String encrypt(String value) {
        String base64EncryptedString = "";

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digestOfPassword = md.digest(SECRET_KEY.getBytes("utf-8"));
            byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);

            SecretKey key = new SecretKeySpec(keyBytes, "DESede");
            Cipher cipher = Cipher.getInstance("DESede");
            cipher.init(Cipher.ENCRYPT_MODE, key);

            byte[] plainTextBytes = value.getBytes("utf-8");
            byte[] buf = cipher.doFinal(plainTextBytes);
            byte[] base64Bytes = Base64.encodeBase64(buf);
            base64EncryptedString = new String(base64Bytes);
        } catch (InvalidKeyException | UnsupportedEncodingException | IllegalBlockSizeException | BadPaddingException
                | NoSuchAlgorithmException | NoSuchPaddingException e) {
            log.error("[E] IOException occurred in [encrypt]", e);
        }
        return base64EncryptedString;
    }

    public static String decrypt(String value) {
        String base64EncryptedString = "";

        try {
            byte[] message = Base64.decodeBase64(value.getBytes("utf-8"));
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digestOfPassword = md.digest(SECRET_KEY.getBytes("utf-8"));
            byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);
            SecretKey key = new SecretKeySpec(keyBytes, "DESede");

            Cipher decipher = Cipher.getInstance("DESede");
            decipher.init(Cipher.DECRYPT_MODE, key);

            byte[] plainText = decipher.doFinal(message);

            base64EncryptedString = new String(plainText, "UTF-8");
        } catch (InvalidKeyException | UnsupportedEncodingException | IllegalBlockSizeException | BadPaddingException
                | NoSuchAlgorithmException | NoSuchPaddingException e) {
            log.error("[E] IOException occurred in [decrypt]", e);
        }
        return base64EncryptedString;
    }

    public static int ordinalIndexOf(String value, String searchValue, int ordinal) {
        return ordinalIndexOf(value, searchValue, ordinal, false);
    }

    public static int lastOrdinalIndexOf(String value, String searchValue, int ordinal) {
        return ordinalIndexOf(value, searchValue, ordinal, true);
    }

    private static int ordinalIndexOf(String value, String searchValue, int ordinal, boolean lastIndex) {
        if (value == null || searchValue == null || ordinal <= 0) {
            return -1;
        }
        if (searchValue.length() == 0) {
            return lastIndex ? value.length() : 0;
        }
        int found = 0;
        int index = lastIndex ? value.length() : -1;
        do {
            if (lastIndex) {
                index = value.lastIndexOf(searchValue, index - 1);
            } else {
                index = value.indexOf(searchValue, index + 1);
            }
            if (index < 0) {
                return index;
            }
            found++;
        } while (found < ordinal);
        return index;
    }

    public static String removeWhitespace(String value) {
        if (value != null) {
            return value.trim().replaceAll("\\s+", "");
        }
        return value;
    }

    public static String splitFirst(String value, String regex) {
        if (value != null) {
            String[] values = value.split(regex);
            if (values.length > 0) {
                return values[0];
            }
        }
        return value;
    }

    public static String splitLast(String value, String regex) {
        if (value != null) {
            String[] values = value.split(regex);
            if (values.length > 0) {
                return values[values.length - 1];
            }
        }
        return value;
    }

    public static String trimAll(String value) {
        if (value != null) {
            return value.trim().replaceAll("\\s+", " ");
        }
        return value;
    }

    public static String upperCaseFirst(String value) {
        if (value != null) {
            return value.substring(0, 1).toUpperCase() + value.substring(1);
        }
        return value;
    }

    public static String byteToBase64(byte[] bytes) {
        return new String(Base64.encodeBase64(bytes));
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuffer result = new StringBuffer();

        for (byte byt : bytes) {
            result.append(Integer.toString((byt & 0xff) + 0x100, 16).substring(1));
        }

        return result.toString();
    }

    public static String prepareToSearch(String value) {
        value = value.toUpperCase();
        value = removeAccentedCharacters(value);
        value = removeSpecialCharacters(value);

        return value;
    }

    public static String removeAccentedCharacters(String value) {
        Map<String, String> characterMap = new HashMap<String, String>();
        characterMap.put("a", "[á]");
        characterMap.put("A", "[Á]");
        characterMap.put("e", "[é]");
        characterMap.put("E", "[É]");
        characterMap.put("i", "[í]");
        characterMap.put("I", "[Í]");
        characterMap.put("o", "[ó]");
        characterMap.put("O", "[Ó]");
        characterMap.put("u", "[ú]");
        characterMap.put("U", "[Ú]");

        for (Map.Entry<String, String> entry : characterMap.entrySet()) {
            value = value.replaceAll(entry.getValue(), entry.getKey());
        }

        return value;
    }

    public static String removeSpecialCharacters(String value) {
        return value.replaceAll("[^a-zA-Z0-9 ]", "");
    }

    public static String stripAccents(String value) {
        value = Normalizer.normalize(value, Normalizer.Form.NFD);
        value = value.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return value;
    }

    public static String camelcaseToHyphens(String value) {
        return String.join("-", value.split("(?=[A-Z])")).toLowerCase();
    }

    public static String camelcaseToUnderscore(String value) {
        return String.join("_", value.split("(?=[A-Z])")).toLowerCase();
    }

    public static String hyphensToCamelcase(String value) {
        String array[] = value.toLowerCase().split("-");

        for (int i = 1; i < array.length; i++) {
            array[i] = upperCaseFirst(array[i]);
        }

        return String.join("", array);
    }

    public static String hyphensToUnderscore(String value) {
        return value.toLowerCase().replace("-", "_");
    }

    public static String underscoreToCamelcase(String value) {
        String array[] = value.toLowerCase().split("_");

        for (int i = 1; i < array.length; i++) {
            array[i] = upperCaseFirst(array[i]);
        }

        return String.join("", array);
    }

    public static String underscoreToHyphens(String value) {
        return value.toLowerCase().replace("_", "-");
    }

}
