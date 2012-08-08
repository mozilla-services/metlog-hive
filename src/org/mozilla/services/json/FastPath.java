/*
***** BEGIN LICENSE BLOCK *****

This Source Code Form is subject to the terms of the Mozilla Public
License, v. 2.0. If a copy of the MPL was not distributed with this file,
You can obtain one at http://mozilla.org/MPL/2.0/.

The Initial Developer of the Original Code is the Mozilla Foundation.
Portions created by the Initial Developer are Copyright (C) 2012
the Initial Developer. All Rights Reserved.

Contributor(s):
    Victor Ng (vng@mozilla.com)

***** END LICENSE BLOCK *****
*/

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

    /*
     * Fetch a path from the JSON object
     *
     * @param path A path using dot notation to index into a JSON object.
     *             Square brackets can be used to index into arrays.
     * @return The stringified value at the keypath.  NULL if the
     *         keypath walks is an invalid path
     */
    public String get(String path) {
        Object result;
        if (StringUtils.isBlank(path)) {
            result = this;
        }

        result = iterateThrough(this, path);
        if (result == null) {
            return null;
        }
        return result.toString();
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

            // Short circuit return a null if we fall off the JSON
            // object
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

        if ((index < 0) || (index >= ((JSONArray) traverser.get(key)).size()))
        {
            return null;
        }

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
