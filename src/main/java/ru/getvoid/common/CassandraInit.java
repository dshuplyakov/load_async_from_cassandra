package ru.getvoid.common;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.QueryOptions;
import com.datastax.driver.core.Session;

/**
 * Date: 23.05.2017
 * Time: 22:53
 *
 * @author Dmitry Shuplyakov
 */
public class CassandraInit {

    private String cassandraList = "5.39.91.41";
    private String keySpace = "mykeyspace";
    private int readTimeout = 1000;
    private int port = 9062;
    private int connectionTimeout = 1000;
    private Session session;


    public CassandraInit() {
        Cluster.Builder builder = Cluster.builder();
        String[] contactPoints = cassandraList.split(",");
        for (String contactPoint : contactPoints) {
            builder.addContactPoint(contactPoint.trim());
        }
        builder.getConfiguration().getSocketOptions().setConnectTimeoutMillis(connectionTimeout);
        builder.getConfiguration().getSocketOptions().setReadTimeoutMillis(readTimeout);
        builder.withPort(port);
        builder.withQueryOptions(new QueryOptions().setFetchSize(2000));
        Cluster cluster = builder.build();
        cluster.getConfiguration().getQueryOptions().setConsistencyLevel(ConsistencyLevel.LOCAL_QUORUM);
        cluster.getConfiguration().getQueryOptions().setSerialConsistencyLevel(ConsistencyLevel.LOCAL_QUORUM);
        session = cluster.connect(keySpace);
    }

    public Session getSession() {
        return session;
    }
}
