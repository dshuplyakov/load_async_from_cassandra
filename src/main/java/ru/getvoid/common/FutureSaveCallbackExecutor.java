package ru.getvoid.common;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import ru.getvoid.common.exception.SaveDAOException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Igor.Shalaru on 21.11.2016.
 */
public class FutureSaveCallbackExecutor<K, V> extends FutureCallbackExecutor {
    private Map<V, K> results;
    private volatile Throwable exception = null;

    public FutureSaveCallbackExecutor(int size) {
        super(size);
        this.results = new ConcurrentHashMap<>();
    }

    public boolean add(ListenableFuture<K> future, V value) {
        try {
            available.acquire();
        } catch (InterruptedException e) {
            return false;
        }
        Futures.addCallback(future, new FutureCallback<K>() {
            @Override
            public void onSuccess(K result) {

                if (result != null) {
                    results.put(value, result);
                }
                available.release();
            }

            @Override
            public void onFailure(Throwable t) {
                exception = t;
                available.release();
            }
        });
        return true;
    }

    public Map<V, K> getResults() throws SaveDAOException {

        cleanup();
        if (exception != null) {
            throw new SaveDAOException(exception.getMessage(), exception);
        }

        return results;
    }

}
