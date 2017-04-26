package org.achacha.base.db;

import org.achacha.base.json.JsonHelper;
import org.achacha.test.BaseInitializedTest;
import org.achacha.test.TestSimpleDbo;
import org.junit.Assert;
import org.junit.Test;

public class TestBaseDbo extends BaseInitializedTest {
    @Test
    public void testToJsonObject() {
        // Also test read-only property of LocalizedKey
        TestSimpleDbo dbo = new TestSimpleDbo(1, "Seven", "unittest.key");
        Assert.assertEquals("{\"id\":1,\"name\":\"Seven\",\"key\":\"Key\"}", dbo.toJsonObject().toString());
    }

    @Test
    public void testFromJsonObject() {
        TestSimpleDbo dbo = BaseDbo.from(JsonHelper.fromString("{\"id\":1,\"name\":\"Seven\"}").getAsJsonObject(), TestSimpleDbo.class);
        Assert.assertEquals("{\"id\":1,\"name\":\"Seven\"}", dbo.toJsonObject().toString());

        dbo = BaseDbo.from(JsonHelper.fromString("{\"name\":\"Five\"}").getAsJsonObject(), TestSimpleDbo.class);
        Assert.assertEquals("{\"id\":0,\"name\":\"Five\"}", dbo.toJsonObject().toString());
    }
}
