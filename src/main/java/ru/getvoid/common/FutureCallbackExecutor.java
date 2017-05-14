package ru.getvoid.common;

import java.util.concurrent.Semaphore;

/**
 * Created by Igor.Shalaru on 26.11.2016.
 */
public abstract class FutureCallbackExecutor {
    protected Semaphore available;
    private int size;


    public FutureCallbackExecutor(int size) {
        this.available = new Semaphore(size, true);
        this.size = size;
    }

    public boolean cleanup() {
        try {
            available.acquire(this.size);
            return true;
        } catch (InterruptedException e) {
            return false;
        } finally {
            available.release(this.size);
        }
    }
}
