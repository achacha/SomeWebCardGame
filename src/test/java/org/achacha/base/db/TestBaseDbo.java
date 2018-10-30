package org.achacha.base.db;

import org.achacha.base.json.JsonHelper;
import org.achacha.test.BaseInitializedTest;
import org.achacha.test.TestSimpleDbo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestBaseDbo extends BaseInitializedTest {
    @Test
    void testToJsonObject() {
        // Also test read-only property of LocalizedKey
        TestSimpleDbo dbo = new TestSimpleDbo(1, "Seven", "unittest.key");
        assertEquals("{\"id\":1,\"name\":\"Seven\",\"key\":\"Key\"}", dbo.toJsonObject().toString());
    }

    @Test
    void testFromJsonObject() {
        TestSimpleDbo dbo = BaseDbo.from(JsonHelper.fromString("{\"id\":1,\"name\":\"Seven\"}").getAsJsonObject(), TestSimpleDbo.class);
        assertEquals("{\"id\":1,\"name\":\"Seven\"}", dbo.toJsonObject().toString());

        dbo = BaseDbo.from(JsonHelper.fromString("{\"name\":\"Five\"}").getAsJsonObject(), TestSimpleDbo.class);
        assertEquals("{\"id\":0,\"name\":\"Five\"}", dbo.toJsonObject().toString());
    }
}
