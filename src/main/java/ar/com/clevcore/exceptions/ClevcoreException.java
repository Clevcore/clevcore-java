package ar.com.clevcore.exceptions;

public class ClevcoreException extends Exception {
    private static final long serialVersionUID = 1L;

    public static enum Severity {
        ERROR, FATAL
    }

    private final Severity severity;
    private final String message;

    private final int errorCode;
    private final Throwable cause;
    private final Object object;

    public ClevcoreException(Severity severity, String message, int errorCode, Throwable cause, Object object) {
        super();
        this.severity = severity;
        this.message = message;
        this.errorCode = errorCode;
        this.cause = cause;
        this.object = object;
    }

    // GETTER & SETTER
    @Override
    public String getMessage() {
        return message;
    }

    public Severity getSeverity() {
        return severity;
    }

    public int getErrorCode() {
        return errorCode;
    }
    @Override
    public Throwable getCause() {
        return cause;
    }

    public Object getObject() {
        return object;
    }
}
