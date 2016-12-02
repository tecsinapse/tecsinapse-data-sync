package br.com.tecsinapse.datasync;

import br.com.tecsinapse.datasync.exception.DataSyncStartupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Singleton
@Startup
public class DataSyncStartup {

    private static final Logger LOG = LoggerFactory.getLogger(DataSyncStartup.class);

    @Inject
    private DataSync dataSync;

    @PostConstruct
    private void init() {
        dataSync.init(getConnection());
    }

    private Connection getConnection() {
        try {
            DataSource ds = (DataSource) new InitialContext().lookup("java:jboss/datasources/HelpdeskDS");// TODO: Properties DS name.
            return ds.getConnection().getMetaData().getConnection();
        } catch (NamingException | SQLException e) {
            LOG.error("Error trying create a connection with database. ", e);
            throw new DataSyncStartupException("Error trying create a connection with database.");
        }
    }

}
