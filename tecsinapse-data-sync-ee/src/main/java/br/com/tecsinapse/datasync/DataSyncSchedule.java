package br.com.tecsinapse.datasync;

import br.com.tecsinapse.datasync.util.ConnectionFactory;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.sql.SQLException;

@Startup
@Singleton
public class DataSyncSchedule {

    @Inject
    private DataSyncScanner scanner;

    @Schedule(hour = "*", minute = "*/1")
    public void scan() throws SQLException {
        scanner.run(ConnectionFactory.getConnection());
    }

}
