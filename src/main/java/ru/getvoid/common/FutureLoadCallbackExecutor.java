package ru.getvoid.common;

import com.datastax.driver.mapping.Result;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import ru.getvoid.common.exception.LoadDAOException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class FutureLoadCallbackExecutor<K> extends FutureCallbackExecutor {
    private Collection<K> collections;
    private volatile Throwable exception = null;

    public FutureLoadCallbackExecutor(int size) {
        super(size);
        this.collections = Collections.synchronizedCollection(new ArrayList<>());
    }

    public boolean add(ListenableFuture<Result<K>> future) {
        try {
            available.acquire();
        } catch (InterruptedException e) {
            return false;
        }
        Futures.addCallback(future, new FutureCallback<Result<K>>() {
            @Override
            public void onSuccess(Result<K> result) {
                if (result != null) {
                    collections.addAll(result.all());
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

    public Collection<K> getResults() throws LoadDAOException {

        cleanup();
        if (exception != null) {
            throw new LoadDAOException(exception.getMessage(), exception);
        }
        return collections;
    }


}
