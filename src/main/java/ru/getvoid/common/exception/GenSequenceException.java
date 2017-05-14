package ru.getvoid.common.exception;

/**
 * Created by Mikhail.Burinov on 30.11.2016.
 */
//TODO Временно объявляем как unchecked исключение (в дальнейшем исправим на extends DAOException)
public class GenSequenceException extends RuntimeException {
    public GenSequenceException() {
    }
    
    public GenSequenceException(String message) {
        super(message);
    }

    public GenSequenceException(String message, Throwable cause) {
        super(message, cause);
    }

    public GenSequenceException(Throwable cause) {
        super(cause);
    }

    public GenSequenceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
