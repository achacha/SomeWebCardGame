package org.achacha.base.db.provider;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class ResourceSqlProviderTest {

    private ResourceSqlProvider resourceSqlProvider = new ResourceSqlProvider();

    @Test
    void testResourceLoading() {
        assertEquals("select * from public.GLOBAL_PROPERTIES", resourceSqlProvider.get("/sql/Test/TestText.sql"));
    }

    @Test
    void resourceNotFound() {
        try {
            resourceSqlProvider.get("/does/not/Exist.sql");
        }
        catch(Exception e) {
            // Expected
            return;
        }

        // Not expected
        fail("Exception should have been thrown when resource not found");
    }
}