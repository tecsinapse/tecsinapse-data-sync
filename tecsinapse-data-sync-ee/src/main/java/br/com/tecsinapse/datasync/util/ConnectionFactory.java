package br.com.tecsinapse.datasync.util;

import br.com.tecsinapse.datasync.DataSyncProp;
import br.com.tecsinapse.datasync.exception.DataSyncStartupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public final class ConnectionFactory {

    private static final Logger LOG = LoggerFactory.getLogger(ConnectionFactory.class);

    public static Connection getConnection() {
        try {
            DataSource ds = (DataSource) new InitialContext().lookup(DataSyncProp.INSTANCE.datasource());
            return ds.getConnection().getMetaData().getConnection();
        } catch (NamingException | SQLException e) {
            LOG.error("Error trying create a connection with database. ", e);
            throw new DataSyncStartupException("Error trying create a connection with database.");
        }
    }

}
