package org.mozilla.services.json;
import org.junit.Test;

public class TestFastPath 
{
    public TestFastPath(){ };

    @Test
    public void test_fastpath()
    {
        Object result;
        FastPath response = new FastPath("{\"severity\":6}");
        System.out.println(response);
        result = response.get("severity");
        System.out.println(result.toString());

    }
}
