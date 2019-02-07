package org.achacha.base.json;

import com.google.gson.JsonObject;
import org.achacha.test.BaseInitializedTest;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonHelperTest extends BaseInitializedTest {
    @Test
    void testGetSuccessObject() {
        JsonObject obj = JsonHelper.getSuccessObject();
        assertEquals("{\"success\":true}", obj.toString());
    }

    @Test
    void testGetSuccessObjectWithMessage() {
        JsonObject obj = JsonHelper.getSuccessObject("Engage!");
        assertEquals("{\"success\":true,\"data\":\"Engage!\",\"dataClass\":\"java.lang.String\"}", obj.toString());
    }

    @Test
    void testGetFailObject() {
        JsonObject obj = JsonHelper.getFailObject(null, "Cantdoit...");
        assertEquals("{\"success\":false,\"data\":\"Cantdoit...\",\"dataClass\":\"java.lang.String\"}", obj.toString());
    }

    @Test
    void testFromParameterMap() {
        /*
        {
          "a":"1",
          "b":{
            "c":"2"
          }
        }
         */
        Map<String, String[]> pmap = new HashMap<>();
        pmap.put("a", new String[]{"1"});
        pmap.put("b.c", new String[]{"2"});

        JsonObject ja = new JsonObject();
        ja.addProperty("a", "1");
        JsonObject jb = new JsonObject();
        jb.addProperty("c", "2");
        ja.add("b", jb);

        assertEquals(ja.toString(), "{\"a\":\"1\",\"b\":{\"c\":\"2\"}}");

        JsonObject jobj = JsonHelper.fromParameterMap(pmap);
        assertEquals(jobj.toString(), "{\"a\":\"1\",\"b\":{\"c\":\"2\"}}");
        assertEquals(jobj.toString(), ja.toString());

        // Empty parameter map results in {}
        assertEquals(JsonHelper.fromParameterMap(new HashMap<>()).toString(), "{}");
    }

    @Test
    void testFromParameterMapInvalid() {
        Map<String, String[]> pmap = new HashMap<>();
        pmap.put("..a..", new String[]{""});
        pmap.put("..b...c..", new String[]{"0"});
        assertEquals(JsonHelper.fromParameterMap(pmap).toString(), "{\"b\":{\"c\":\"0\"},\"a\":\"\"}");
    }
}
