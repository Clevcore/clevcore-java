package ar.com.clevcore.exceptions;

public class ClevcoreException extends Exception {
    private static final long serialVersionUID = 1L;

    public static enum Severity {
        ERROR, FATAL
    }

    private Severity severity;
    private String message;

    private int errorCode;
    private Throwable cause;
    private Object object;

    public ClevcoreException() {
    }

    public ClevcoreException(Severity severity, String message) {
        super();
        this.severity = severity;
        this.message = message;
    }

    public ClevcoreException(Severity severity, String message, int errorCode, Throwable cause, Object object) {
        super();
        this.severity = severity;
        this.message = message;
        this.errorCode = errorCode;
        this.cause = cause;
        this.object = object;
    }

    // GETTER & SETTER
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public Throwable getCause() {
        return cause;
    }

    public void setCause(Throwable cause) {
        this.cause = cause;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

}
