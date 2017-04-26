package org.achacha.base.db.provider;

import org.achacha.test.BaseInitializedTest;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class TestSqlResourceProvider extends BaseInitializedTest {

    private ResourceSqlProvider resourceSqlProvider = new ResourceSqlProvider();
    @Test
    public void testResourceLoading() throws IOException {
        Assert.assertEquals("select * from public.GLOBAL_PROPERTIES", resourceSqlProvider.get("/sql/Test/TestText.sql"));
    }

    @Test
    public void resourceNotFound() throws IOException {
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
