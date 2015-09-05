package ar.com.clevcore.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ar.com.clevcore.exceptions.ClevcoreException;
import ar.com.clevcore.exceptions.ClevcoreException.Severity;

public final class ExceptionUtils {

    private static final Logger log = LoggerFactory.getLogger(ExceptionUtils.class);

    private ExceptionUtils() {
        throw new AssertionError();
    }

    // TODO: We must create a log, We can use log4j
    public static void treateException(Exception e, Object object) throws ClevcoreException {
        log.error("[E] Exception Received with object {} ", object.getClass().getName(),  e);
        
        System.out.println("Message: " + e.getMessage());
        System.out.println("Severity: " + Severity.FATAL);
        System.out.println("errorCode: " + -1);
        System.out.println("Cause: " + e.getCause());
        System.out.println("Object: " + object.toString());

        throw new ClevcoreException(Severity.FATAL, e.getMessage(), -1, e.getCause(), object);
    }

}
