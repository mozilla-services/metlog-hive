package org.mozilla.services.json;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class TestFastPath
{
    public TestFastPath(){ };

    @Test
    public void test_fastpath()
    {
        Object result;

        FastPath response = new FastPath("{\"severity\":6}");
        result = response.get("severity");
        assertEquals("6", result.toString());

        response = new FastPath("{\"severity\":6, \"foo\": [1, 2, 3]}");
        result = response.get("foo");
        assertEquals("[1,2,3]", result.toString());

        response = new FastPath("{\"severity\":6, \"foo\": [1, 2, 3]}");
        result = response.get("foo[2]");
        assertEquals("3", result.toString());

        response = new FastPath("{\"severity\":6, \"foo\": [1, {\"dog\": 42, \"cat\": 77}, 3]}");
        result = response.get("foo[1].dog");
        assertEquals("42", result.toString());

    }
}
