package ru.getvoid.dao;

import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import ru.getvoid.accessor.CustomerAccessor;
import ru.getvoid.entity.Customer;
import ru.getvoid.common.FutureLoadCallbackExecutor;
import ru.getvoid.common.exception.LoadDAOException;

import java.util.Collection;

public class CustomerDao {

    private final CustomerAccessor accessor;
    /**
     * Кол-во одновременных асинхронных операций с кассандра
     */
    private final int countReadConcurrentAsync;
    /**
     * Дао объект для работы с бд кассандра
     */
    private final Mapper<Customer> mapper;
    private Session session;

    public CustomerDao(Session session, int countReadConcurrentAsync) {
        this.session = session;
        this.countReadConcurrentAsync = countReadConcurrentAsync;
        MappingManager mappingManager = new MappingManager(session);
        accessor = mappingManager.createAccessor(CustomerAccessor.class);
        mapper = mappingManager.mapper(Customer.class);
    }

    public Collection<Customer> getCustomerByFisrtname(String firstname) throws LoadDAOException {
        FutureLoadCallbackExecutor<Customer> loader = new FutureLoadCallbackExecutor<>(countReadConcurrentAsync);
        if (!loader.add(accessor.getByFirstname(firstname))) {
            throw new LoadDAOException("Error create db request for element: " + firstname);
        }
        return loader.getResults();
    }




}


