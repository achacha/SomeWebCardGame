package org.achacha.base.db.provider;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Unit test connection provider
 */
public class UnitTestDbPoolConnectionProvider extends DbPoolConnectionProvider {

    public UnitTestDbPoolConnectionProvider(String jdbcUrl, Properties properties) {
        super(jdbcUrl, properties);
    }

    public DataSource getDataSource() {
        return dataSource;
    }
}
