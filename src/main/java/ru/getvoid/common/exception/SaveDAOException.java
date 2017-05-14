package ru.getvoid.common.exception;

/**
 * Created by Igor.Shalaru on 28.10.2016.
 */
public class SaveDAOException extends DAOException {
    public SaveDAOException() {
    }

    public SaveDAOException(String message) {
        super(message);
    }

    public SaveDAOException(String message, Throwable cause) {
        super(message, cause);
    }

    public SaveDAOException(Throwable cause) {
        super(cause);
    }

    public SaveDAOException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
