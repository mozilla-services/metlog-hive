package org.mozilla.services.json;

import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;

import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;
import org.json.simple.JSONAware;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;

public class FastPath {
    static JSONParser parser = new JSONParser();

    JSONAware coreObject;

    public FastPath(JSONAware aware) {
        this.coreObject = aware;
    }

    public FastPath(String json) {
        try {
            this.coreObject = (JSONAware) parser.parse(json);
        } catch (ParseException pe) {
            this.coreObject = null;
        }
    }

    /**
     * <b>FastPath</b><br>
     * JsonElement wrapper that adds path traverse. 
     * <br>
     * Ej:<br>
     * FastPath response = new FastPath( <b>someJsonElement</b> );<br>
     *	latitude = response.get("positions[0].geoLocation.latitude").getAsDouble();<br>
     * @param path to iterate
     */
    public Object get(String path){
        Object result;
        if (StringUtils.isBlank(path)) {
            return this;
        }

        // TODO: specialize this and try to cast to the proper type
        return iterateThrough(this, path);
    }

    private static Object iterateThrough (FastPath element, String path) {
        StringTokenizer st = new StringTokenizer(path, ".");

        Object traverser = element.coreObject;
        while (st.hasMoreTokens())
        { 
            String token = st.nextToken();
            if (isArray(token)) {
                traverser = getArrayElement((JSONObject)traverser, token);
            } else {
                traverser = ((JSONObject)traverser).get(token);
            }

            if (traverser == null) {
                return null;
            }
        }
        return traverser;
    }

    private static Object getArrayElement(JSONObject traverser,
            String token) {
        int index = Integer.valueOf(token.substring(token.indexOf("[") + 1,
                    token.indexOf("]")));
        String key = token.substring(0, token.indexOf("["));
        return ((JSONArray) traverser.get(key)).get(index);
    }

    private static boolean isArray(String token) {
        return token.contains("[")
            && token.contains("]")
            && (token.indexOf("[") < token.indexOf("]"))
            && StringUtils.isNumeric(token.substring(token.indexOf("[")+1,
                        token.indexOf("]")));
    }

    public String toString() {
        return this.coreObject.toString();
    }


}
