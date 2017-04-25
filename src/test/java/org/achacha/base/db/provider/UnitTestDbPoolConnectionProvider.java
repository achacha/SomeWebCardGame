package org.achacha.base.db.provider;

import java.util.Properties;

/**
 * Unit test connection provider
 */
public class UnitTestDbPoolConnectionProvider extends DbPoolConnectionProvider {

    public UnitTestDbPoolConnectionProvider(String jdbcUrl, Properties properties) {
        super(jdbcUrl, properties);
    }
}
