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
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class TestFastPath
{
    public TestFastPath(){ };

    @Test
    public void test_fastpath()
    {
        String result;

        FastPath response = new FastPath("{\"severity\":6}");
        result = response.get("severity");
        assertEquals("6", result);

        response = new FastPath("{\"severity\":6, \"foo\": [1, 2, 3]}");
        result = response.get("foo");
        assertEquals("[1,2,3]", result);

        response = new FastPath("{\"severity\":6, \"foo\": [1, 2, 3]}");
        result = response.get("foo[2]");
        assertEquals("3", result);

        response = new FastPath("{\"severity\":6, \"foo\": [1, {\"dog\": 42, \"cat\": 77}, 3]}");
        result = response.get("foo[1].dog");
        assertEquals("42", result);


        response = new FastPath("{\"severity\":6, \"foo\": [1, {\"dog\": 42, \"cat\": 77}, 3]}");
        result = response.get("bar");
        assertEquals(null, result);

        result = response.get("bar.bat.dog");
        assertEquals(null, result);

        result = response.get("foo[99]");
        assertEquals(null, result);

    }
}
