package ru.getvoid;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.SimpleStatement;
import com.datastax.driver.core.Statement;
import com.google.common.base.Stopwatch;
import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.getvoid.common.CassandraInit;
import ru.getvoid.dao.CustomerDao;
import ru.getvoid.entity.Customer;

import java.util.Collection;
import java.util.concurrent.ExecutionException;

/**
 * Hello world!
 *
 */
public class App 
{
    final static Logger logger = LoggerFactory.getLogger(App.class);

    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );

        CassandraInit cassandraInit = new CassandraInit();

        CustomerDao customerDao = new CustomerDao(cassandraInit.getSession(), 10);

        /*
        getByName(customerDao);
        getSyncNoPaging(customerDao);
        getSyncPaging(cassandraInit);
        */

        getAsyncPaging(cassandraInit);
    }

    private static void getByName(CustomerDao customerDao) {
        final Stopwatch stopwatch = Stopwatch.createStarted();

        Collection<Customer> result = customerDao.getCustomerByFisrtname("Cromaractron");
        stopwatch.stop();

        logger.info("Loaded {} in {}", result.size(), stopwatch.toString());
    }

    private static void getSyncNoPaging(CustomerDao customerDao) {
        final Stopwatch stopwatch = Stopwatch.createStarted();
        Collection<Customer> result;
        stopwatch.start();
        result = customerDao.getAll();
        logger.info("Loaded {} in {}", result.size(), stopwatch.toString());
    }

    private static void getSyncPaging(CassandraInit cassandraInit) {
        final Stopwatch stopwatch = Stopwatch.createStarted();
        ResultSet rs = cassandraInit.getSession().execute("select * from customer");
        int i = 0;
        for (Row row : rs) {
            if (rs.getAvailableWithoutFetching() == 100 && !rs.isFullyFetched())
                rs.fetchMoreResults(); // this is asynchronous
            // Process the row ...
            i++;
        }

        stopwatch.stop();
        logger.info("Loaded {} in {}", i, stopwatch.toString());
    }

    private static void getAsyncPaging(CassandraInit cassandraInit) {
        final Stopwatch stopwatch = Stopwatch.createStarted();
        Statement statement = new SimpleStatement("select * from customer").setFetchSize(10000);
        ListenableFuture<ResultSet> future = Futures.transform(
                cassandraInit.getSession().executeAsync(statement),
                iterate(1));

        try {
            System.out.println(future.get().all().size());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        stopwatch.stop();
        logger.info("Loaded in {}",stopwatch.toString());
    }


    private static AsyncFunction<ResultSet, ResultSet> iterate(final int page) {
        return new AsyncFunction<ResultSet, ResultSet>() {
            @Override
            public ListenableFuture<ResultSet> apply(ResultSet rs) throws Exception {

                // How far we can go without triggering the blocking fetch:
                int remainingInPage = rs.getAvailableWithoutFetching();

                System.out.printf("Starting page %d (%d rows)%n", page, remainingInPage);

                for (Row row : rs) {
                    //System.out.printf("[page %d - %d] row = %s%n", page, remainingInPage, row);
                    if (--remainingInPage == 0)
                        break;
                }
                System.out.printf("Done page %d%n", page);

                boolean wasLastPage = rs.getExecutionInfo().getPagingState() == null;
                if (wasLastPage) {
                    System.out.println("Done iterating");
                    return Futures.immediateFuture(rs);
                } else {
                    ListenableFuture<ResultSet> future = rs.fetchMoreResults();
                    return Futures.transform(future, iterate(page + 1));
                }
            }
        };
    }
}
