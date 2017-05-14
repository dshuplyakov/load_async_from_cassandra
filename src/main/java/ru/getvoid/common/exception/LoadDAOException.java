package ru.getvoid.common.exception;

/**
 * Created by Igor.Shalaru on 28.10.2016.
 */
public class LoadDAOException extends DAOException {
    public LoadDAOException() {
    }

    public LoadDAOException(String message) {
        super(message);
    }

    public LoadDAOException(String message, Throwable cause) {
        super(message, cause);
    }

    public LoadDAOException(Throwable cause) {
        super(cause);
    }

    public LoadDAOException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
