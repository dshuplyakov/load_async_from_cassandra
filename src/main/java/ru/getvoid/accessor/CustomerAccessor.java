package ru.getvoid.accessor;

import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Query;
import com.google.common.util.concurrent.ListenableFuture;
import ru.getvoid.entity.Customer;

/**
 * Created by Igor.Shalaru on 15.11.2016.
 */
@Accessor
public interface CustomerAccessor {
    @Query("select * from customer where firstname = ?")
    ListenableFuture<Result<Customer>> getByFirstname(String uid);

    @Query("select * from customer ALLOW FILTERING")
    ListenableFuture<Result<Customer>> getAll();
}
