package org.achacha.base.db.provider;

import java.util.Properties;

/**
 * Unit test connection provider
 * Used by integration tests for embedded tomcat
 */
public class UnitTestDbPoolConnectionProvider extends DbPoolConnectionProvider {

    public UnitTestDbPoolConnectionProvider(Properties properties) {
        super(properties);
    }
}
