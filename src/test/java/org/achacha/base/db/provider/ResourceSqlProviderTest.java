package org.achacha.base.db.provider;

import org.junit.Assert;
import org.junit.Test;

public class ResourceSqlProviderTest {

    private ResourceSqlProvider resourceSqlProvider = new ResourceSqlProvider();

    @Test
    public void testResourceLoading() {
        Assert.assertEquals("select * from public.GLOBAL_PROPERTIES", resourceSqlProvider.get("/sql/Test/TestText.sql"));
    }

    @Test
    public void resourceNotFound() {
        try {
            resourceSqlProvider.get("/does/not/Exist.sql");
        }
        catch(Exception e) {
            // Expected
            return;
        }

        // Not expected
        Assert.fail("Exception should have been thrown when resource not found");
    }
}