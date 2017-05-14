package ru.getvoid.common;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.getvoid.common.exception.SaveDAOException;

import java.util.Collection;
import java.util.Collections;

/**
 * Created by Igor.Shalaru on 24.10.2016.
 */
public abstract class Provider<V> {
    private static final Logger log = LoggerFactory.getLogger(Provider.class);
    protected Session session;
    private static final int DEFAULT_COUNT_CONCURRENT_ASYNC = 100;
    protected int countConcurrentAsync = DEFAULT_COUNT_CONCURRENT_ASYNC;
    
    // TODO Требует рефакторинга
    protected abstract Statement convertToSave(V element);

    public void save(V element) throws SaveDAOException {
        save(Collections.singletonList(element));
    }

    public void save(Collection<V> collection) throws SaveDAOException {
        FutureSaveCallbackExecutor<ResultSet, V> saver = new FutureSaveCallbackExecutor<>(countConcurrentAsync);
        for (V v : collection) {
            if (!saver.add(session.executeAsync(convertToSave(v)), v)) {
                throw new SaveDAOException("Error create db request for element: " + v);
            }
        }
        saver.cleanup(); //ожидаем завершения всех асинхронных операций

        saver.getResults();
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public void setCountConcurrentAsync(int countConcurrentAsync) {
        this.countConcurrentAsync = countConcurrentAsync;
    }
}
