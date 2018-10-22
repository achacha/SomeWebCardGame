package org.achacha.base.json;

import com.google.gson.JsonObject;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.core.Is.is;

public class JsonHelperTest {
    @Test
    public void testGetSuccessObject() {
        JsonObject obj = JsonHelper.getSuccessObject();
        Assert.assertThat(obj.toString(), is("{\"success\":true}"));
    }

    @Test
    public void testGetSuccessObjectWithMessage() {
        JsonObject obj = JsonHelper.getSuccessObject("Engage!");
        Assert.assertThat(
                obj.toString(),
                is("{\"success\":true,\"data\":\"Engage!\"}"));
    }

    @Test
    public void testGetFailObject() {
        JsonObject obj = JsonHelper.getFailObject(null, "Cantdoit...");
        Assert.assertThat(
                obj.toString(),
                is("{\"success\":false,\"data\":\"Cantdoit...\"}"));
    }

    @Test
    public void testFromParameterMap() {
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

        Assert.assertEquals(ja.toString(), "{\"a\":\"1\",\"b\":{\"c\":\"2\"}}");

        JsonObject jobj = JsonHelper.fromParameterMap(pmap);
        Assert.assertEquals(jobj.toString(), "{\"a\":\"1\",\"b\":{\"c\":\"2\"}}");
        Assert.assertEquals(jobj.toString(), ja.toString());

        // Empty parameter map results in {}
        Assert.assertEquals(JsonHelper.fromParameterMap(new HashMap<>()).toString(), "{}");
    }

    @Test
    public void testFromParameterMapInvalid() {
        Map<String, String[]> pmap = new HashMap<>();
        pmap.put("..a..", new String[]{""});
        pmap.put("..b...c..", new String[]{"0"});
        Assert.assertEquals(JsonHelper.fromParameterMap(pmap).toString(), "{\"b\":{\"c\":\"0\"},\"a\":\"\"}");
    }
}
