package org.achacha.base.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonReader;
import org.achacha.base.global.Global;
import org.achacha.base.i18n.UIMessageHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class JsonHelper {
    private static final Logger LOGGER = LogManager.getLogger(JsonHelper.class);

    // Success status
    public static final String SUCCESS = "success";

    // AUTHORIZED status
    public static final String AUTHORIZED = "authorized";

    // Optional message
    public static final String MESSAGE = "message";

    // Json payload
    public static final String DATA = "data";
    public static final String DATA_CLASS = "dataClass";

    /**
     * @return Simple success object
     */
    public static JsonObject getSuccessObject() {
        return getSuccessObject(null, null);
    }

    /**
     * @param data Object converted to data
     * @return Success object
     */
    public static JsonObject getSuccessObject(Object data) {
        return getSuccessObject(null, data);
    }

    /**
     * Standard success object
     * success:true
     *
     * @param key String to add as 'key' (null if none)
     * @param data Object to serialize to JSON (null if none)
     * @return JsonObject
     */
    public static JsonObject getSuccessObject(String key, Object data) {
        JsonObject jobj = new JsonObject();

        jobj.addProperty(SUCCESS, true);
        if (null != key) {
            String message = UIMessageHelper.getInstance().getLocalizedMsg(key);
            jobj.addProperty(MESSAGE, message);
        }

        // Serialize to JsonObject
        if (data != null) {
            JsonElement je = Global.getInstance().getGson().toJsonTree(data);
            jobj.add(DATA, je);
            jobj.addProperty(DATA_CLASS, data.getClass().getName());
        }

        return jobj;
    }

    /**
     * Standard fail object with data added using String::toString
     * success:false
     *
     * @param key String to add as 'key' (null if none)
     * @param data String to stringify to JSON (null if none)
     * @return JsonObject
     */
    public static JsonObject getFailObject(String key, String data) {

        JsonObject jobj = new JsonObject();

        jobj.addProperty(SUCCESS, false);

        if (null != key) {
            String message = UIMessageHelper.getInstance().getLocalizedMsg(key);
            jobj.addProperty(MESSAGE, message);
        }

        // Serialize to JsonObject
        if (data != null) {
            jobj.addProperty(DATA, data);
            jobj.addProperty(DATA_CLASS, data.getClass().getName());
        }

        return jobj;
    }

    /**
     * Standard fail object with data serialized into a JSON tree
     * success:false
     *
     * @param key String to add as 'key' (null if none)
     * @param data Object to serialize to JSON (null if none)
     * @return JsonObject
     */
    public static JsonObject getFailObjectEscaped(String key, Object data) {

        JsonObject jobj = new JsonObject();

        jobj.addProperty(SUCCESS, false);

        if (null != key) {
            String message = UIMessageHelper.getInstance().getLocalizedMsg(key);
            jobj.addProperty(MESSAGE, message);
        }

        // Serialize to JsonObject
        if (data != null) {
            JsonElement je = Global.getInstance().getGson().toJsonTree(data);
            jobj.add(DATA, je);
            jobj.addProperty(DATA_CLASS, data.getClass().getName());
        }

        return jobj;
    }

    /**
     * Given a ResultSet, will convert active row to JsonObject
     * Using metadata to get column name and use it as the name
     * Does not alter the ResultSet position
     *
     * @param rs ResultSet
     * @return JsonObject
     */
    public static JsonObject toJsonObject(ResultSet rs) {
        JsonObject obj = new JsonObject();
        try {
            ResultSetMetaData rsMetaData = rs.getMetaData();
            int numberOfColumns = rsMetaData.getColumnCount();
            for (int col = 1; col <= numberOfColumns; ++col) {
                String name = rsMetaData.getColumnName(col);
                Object v = rs.getObject(col);
                if (null != v) {
                    obj.addProperty(name, v.toString());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to build JSON object", e);
        }
        return obj;
    }

    /**
     * Given a ResultSet, will convert active row to JsonObject
     * Using metadata to get column name and use it as the name
     * Will replace lookup value for any column that matches, so plan_id if provided will generate a field called plan with lookup value
     * If lookup name does not end in _id, a _value will be appended with looked up value
     * NOTE: Does not alter the ResultSet position
     *
     * @param rs ResultSet
     * @param lookupMaps to lookup columns and replace with values
     * @param useLabels if true will use column label in the database for name instead of column name
     * @return JsonObject
     */
    public static JsonObject toJsonObject(ResultSet rs, Map<String,Map<String,String>> lookupMaps, boolean useLabels) {
        JsonObject obj = new JsonObject();
        try {
            ResultSetMetaData rsMetaData = rs.getMetaData();
            int numberOfColumns = rsMetaData.getColumnCount();
            for (int col = 1; col <= numberOfColumns; ++col) {
                String columnName;
                if (useLabels) {
                    columnName = rsMetaData.getColumnLabel(col);
                } else {
                    columnName = rsMetaData.getColumnName(col);
                }
                Object v = rs.getObject(col);
                if (null != v) {
                    if (null != lookupMaps) {
                        // Lookup requested
                        Map<String,String> lookupMap = lookupMaps.get(columnName);
                        if (null != lookupMap) {
                            String lookedUpValue = lookupMap.get(v.toString());
                            if (null == lookedUpValue) {
                                LOGGER.warn("Lookup not found for columnName={} and value={}", columnName, v);
                            }
                            else {
                                obj.addProperty(columnName, v.toString());
                                v = lookedUpValue;
                                if (columnName.endsWith("_id")) {
                                    columnName = columnName.substring(0, columnName.length()-3);
                                }
                                else {
                                    columnName = columnName+"_value";
                                }
                            }
                        }
                    }
                    obj.addProperty(columnName, v.toString());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to build JSON object", e);
        }
        return obj;
    }

    public static JsonArray toArrayOfObjects(ResultSet rs, int n) {
        return toArrayOfObjects(rs, n, null);
    }
    public static JsonArray toArrayOfObjects(ResultSet rs, int n, Map<String,Map<String,String>> lookupMaps) { return toArrayOfObjects(rs, n, lookupMaps, null); }

    /**
     * Given a ResultSet convert it to JSON array of JSON Object
     * e.g. [ {name0:val00, name1:val01}, {name0:val10, name1:val11}, {name0:val20, name1:val21} ]
     *
     * @param rs ResultSet
     * @param lookupMaps column to Map used to do lookup/replace on the data
     * @param n  use first n elements (-1 for everything)
     * @param exclusions list of columns to exclude value (usually sensitive data to omit from client transmission)
     * @return JsonArray
     */
    public static JsonArray toArrayOfObjects(ResultSet rs, int n, Map<String,Map<String,String>> lookupMaps, List<String> exclusions) {
        JsonArray ary = new JsonArray();

        boolean noLimit = false;
        if (n == -1) {
            n = 1;
            noLimit = true;
        }

        try {
            // Build a list of column names
            ResultSetMetaData rsMetaData = rs.getMetaData();
            int numberOfColumns = rsMetaData.getColumnCount();
            ArrayList<String> columnNames = new ArrayList<>();
            for (int col = 1; col <= numberOfColumns; ++col) {
                columnNames.add(rsMetaData.getColumnLabel(col));
            }

            // Iterate over the data
            while (rs.next() && n > 0) {
                JsonObject obj = new JsonObject();
                for (int col = 1; col <= numberOfColumns; ++col) {
                    Object v;
                    try {
                        v = rs.getObject(col);
                    }catch(SQLException ex){
                        // If we fail to load out the object such as date type 0000-00-00
                        // Then lets load it out of the database as a null.
                        v = null;
                    }
                    if (v != null) {
                        String columnName = columnNames.get(col - 1);
                        if (null != lookupMaps) {
                            // Lookup requested
                            Map<String,String> lookupMap = lookupMaps.get(columnName);
                            if (null != lookupMap) {
                                String lookedUpValue = lookupMap.get(v.toString());
                                if (null == lookedUpValue) {
                                    LOGGER.warn("Lookup not found for columnName={} and value={}", columnName, v);
                                }
                                else {
                                    v = lookedUpValue;
                                }
                            }
                        }

                        if(exclusions != null && exclusions.contains(columnName.toLowerCase())){
                            v = null;
                        }

                        obj.addProperty(columnNames.get(col - 1), v != null ? v.toString() : "null");
                    }
                }
                ary.add(obj);

                // If no limit don't decrement, we wants n to always be >0
                if (!noLimit) --n;
            }
        } catch (SQLException e) {
            throw new RuntimeException("SQL Exception", e);
        }
        return ary;
    }

    /**
     * Given a ResultSet convert it to JSON array of arrays
     * e.g. [ [val00, val01], [val10, val11], [val20,val21] ]
     * In this form the names are ignored and only values are put in
     *
     * @param rs ResultSet
     * @param n  int use first n elements
     * @return JsonArray
     */
    public static JsonArray toArrayOfArrays(ResultSet rs, int n) {
        JsonArray ary = new JsonArray();

        boolean noLimit = false;
        if (n == -1) {
            n = 1;
            noLimit = true;
        }

        try {
            ResultSetMetaData rsMetaData = rs.getMetaData();
            int numberOfColumns = rsMetaData.getColumnCount();

            while (rs.next() && n > 0) {
                JsonArray subary = new JsonArray();
                for (int col = 1; col <= numberOfColumns; ++col) {
                    Object v;
                    try {
                        v = rs.getObject(col);
                    }catch(SQLException ex){
                        // If we fail to load out the object such as date type 0000-00-00
                        // Then lets load it out of the database as a null.
                        v = null;
                    }
                    if (null != v) {
                        String value = v.toString();
                        subary.add(new JsonPrimitive(value));
                    }
                }
                ary.add(subary);

                // If no limit don't decrement, we wants n to always be >0
                if (!noLimit) --n;
            }
        } catch (SQLException e) {
            throw new RuntimeException("SQL Exception", e);
        }
        return ary;
    }

    /**
     * Put data value object expected by Ajax (agreed upon)
     *
     * @param obj JsonObject
     * @param ex Throwable, if null, nothing done
     */
    public static void putException(JsonObject obj, Throwable ex) {
        if (null == obj) {
            throw new RuntimeException("Object must not be null");
        }
        if (null == ex) {
            return;
        }

        obj.addProperty("exception_message", ex.getLocalizedMessage());
        if (LOGGER.isDebugEnabled()) {
            // Add stack trace when in DEBUG mode only
            JsonArray ary = new JsonArray();
            for (String frame : ExceptionUtils.getStackFrames(ex)) {
                ary.add(frame);
            }

            obj.add("exception_stack", ary);
        }
    }

    /**
     * Compare if two JSON objects are equal
     *
     * @param one JsonObject
     * @param two JsonObject
     * @return true if objects are equal
     */
    public static boolean equals(JsonObject one, JsonObject two) {
        return one.toString().equals(two.toString());
    }

    /**
     * String to JsonElement
     * @param data String
     * @return JsonElement or null if failed
     */
    public static JsonElement fromString(String data) {
        return new JsonParser().parse(data);
    }

    /**
     * Read input stream and parse to JsonElement
     * @param is InputStream
     * @return JsonElement or null if failed
     */
    public static JsonElement fromInputStream(InputStream is) {
        JsonReader reader = new JsonReader(new InputStreamReader(is));
        return new JsonParser().parse(reader);
    }

    /**
     * Read reader and parse to JsonElement
     * @param reader Reader
     * @return JsonElement or null if failed or root element is null
     */
    @Nullable
    public static JsonElement fromReader(Reader reader) {
        return new JsonParser().parse(reader);
    }

    /**
     * Build JsonObject from query string data provided in {@link HttpServletRequest#getParameterMap}
     * Data is viewed as a flattened representation with `.` denoting object boundary
     *
     * @param parameterMap Map of String to String[]
     * @return JsonObject
     */
    @Nonnull
    public static JsonObject fromParameterMap(Map<String, String[]> parameterMap) {
        final JsonObject obj = new JsonObject();
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            LinkedList<String> paths = new LinkedList<>(Arrays.asList(StringUtils.split(entry.getKey(), '.')));

            // If array is >1 using [,,] notation, else just a String
            String value = entry.getValue().length > 1 ? Arrays.toString(entry.getValue()) : entry.getValue()[0];
            if (paths.size() > 0)
                addToJsonObject(obj, paths, value);
        }
        return obj;
    }

    /**
     * Recursively add objects for paths
     * @param obj JsobObject current root
     * @param paths List of String paths to add
     * @param value Value to add when we get to the end of paths
     */
    private static void addToJsonObject(JsonObject obj, LinkedList<String> paths, String value) {
        String path = paths.pop();
        if (paths.size() > 0) {
            // More in path
            if (!obj.has(path)) {
                JsonObject subObj = new JsonObject();
                obj.add(path, subObj);
                addToJsonObject(subObj, paths, value);
            }
            else {
                // Object exists
                JsonObject subObj = obj.getAsJsonObject(path);
                addToJsonObject(subObj, paths, value);
            }
        }
        else {
            // Value of this object
            obj.addProperty(path, value);
        }
    }

    /**
     * Convert a list of JsonEmittable types to JsonArray
     * @param emittables Collection of JsonEmittable
     * @param <T> extends JsonEmittable
     * @return JsonArray from collection
     */
    public static <T extends JsonEmittable> JsonArray toJsonArray(Collection<T> emittables) {
        JsonArray ary = new JsonArray();
        for(JsonEmittable emittable : emittables) {
            ary.add(emittable.toJsonObject());
        }
        return ary;
    }

    /**
     * Escapes toString of an Object to valid JSON value
     * @param obj Object
     * @return String value or null if obj is null
     */
    public static String toJsonValue(Object obj) {
        if (null == obj)
            return null;

        String value = obj.toString();
        if (value == null || value.length() == 0) {
            return "\"\"";
        }

        char c;
        int i;
        int len = value.length();
        StringBuilder sb = new StringBuilder(len + 4);
        String t;

        sb.append('"');
        for (i = 0; i < len; i += 1) {
            c = value.charAt(i);
            switch (c) {
                case '\\':
                case '"':
                    sb.append('\\');
                    sb.append(c);
                    break;
                case '/':
                    sb.append('\\');
                    sb.append(c);
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                default:
                    if (c < ' ') {
                        t = "000" + Integer.toHexString(c);
                        sb.append("\\u").append(t.substring(t.length() - 4));
                    } else {
                        sb.append(c);
                    }
            }
        }
        sb.append('"');
        return sb.toString();
    }

    /**
     * Clone JsonElement
     * NOTE: This is meant as a way to deepCopy JsonElement when using source as a base template
     * Gson developers have not provided a way to do this yet, when they do this method will be deprecated
     *   since it is not efficient and not meant to be used in performance sensitive code
     *
     * @param source JsonElement
     * @return new JsonElement cloned from source
     */
    public static JsonElement clone(JsonElement source) {
        return JsonHelper.fromString(source.toString());
    }

    public static String toStringPrettyPrint(JsonElement e) {
        return Global.getInstance().getGsonPretty().toJson(e);
    }

    public static String dquote(String s){return "\"" + s + "\"";}

    public static String squote(String s){return "'" + s + "'";}

    /**
     * Merge extra JsonObject INTO base JsonObject
     * Any element in extra will overwrite the base if already exists
     *
     * @param base JsonObject
     * @param extras JsonObject(s) that will all get merged into base
     * @return base JsobObject with extra merged into it
     */
    public static JsonObject merge(JsonObject base, JsonObject... extras) {
        for (JsonObject extra : extras)
            extra.entrySet().forEach(e -> base.add(e.getKey(), e.getValue()));

        return base;
    }

    /**
     * Convert Properties to JsonObject
     * @param properties Properties
     * @return JsonObject
     */
    public static JsonObject toJsonObject(Properties properties) {
        JsonObject jobj = new JsonObject();

        for (String key : properties.stringPropertyNames()) {
            String value = properties.getProperty(key);
            jobj.addProperty(key, value);
        }

        return jobj;
    }

    /**
     * Add data to JsonObject used as return
     * @param obj JsonObject
     * @param jsonElement JsonElement
     * @see JsonHelper#DATA
     */
    public static void addData(JsonObject obj, JsonElement jsonElement) {
        obj.add(DATA, jsonElement);
    }
}
