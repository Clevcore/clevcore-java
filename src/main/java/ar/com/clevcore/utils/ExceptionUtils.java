package ar.com.clevcore.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ar.com.clevcore.exceptions.ClevcoreException;
import ar.com.clevcore.exceptions.ClevcoreException.Severity;

public final class ExceptionUtils {

    private static final Logger LOG = LoggerFactory.getLogger(ExceptionUtils.class);

    private ExceptionUtils() {
        throw new AssertionError();
    }

    // TODO: We must create a log, We can use log4j
    public static void treateException(Exception e, Object object) throws ClevcoreException {
        LOG.error("[E] Exception Received with object {} ", object.getClass().getName(),  e);
        throw new ClevcoreException(Severity.FATAL, e.getMessage(), -1, e.getCause(), object);
    }

}
