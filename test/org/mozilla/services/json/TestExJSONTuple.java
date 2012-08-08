package org.mozilla.services.json;
import org.junit.Test;
import static org.junit.Assert.assertEquals;


import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.mozilla.services.json.ExJSONTuple;
import org.apache.hadoop.hive.ql.udf.generic.UDTFCollector;
import org.apache.hadoop.hive.ql.exec.UDTFOperator;
import java.util.ArrayList;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;

public class TestExJSONTuple
{
    public TestExJSONTuple() {};

    public ArrayList run_jsontuple(String[] user_args) throws Exception {
        int i = 0;

        ArrayList<String> result = new ArrayList<String>();

        ExJSONTuple func = new ExJSONTuple();
        func.setCollector(new UDTFCollector(new UDTFOperator()));

        ArrayList<ObjectInspector> args = new ArrayList<ObjectInspector>();

        for (i = 0; i < user_args.length; i++) {
            args.add(PrimitiveObjectInspectorFactory.writableStringObjectInspector);
        }
        ObjectInspector[] args_arr = new ObjectInspector[args.size()];
        args_arr = args.toArray(args_arr);
        func.initialize(args_arr);

        ArrayList<Object> func_args = new ArrayList<Object>();
        for (i = 0; i < user_args.length; i++)
        {
            func_args.add(PrimitiveObjectInspectorFactory.writableStringObjectInspector.create(user_args[i]));
        }

        func.process(func_args.toArray());

        for (i = 0; i < func.retCols.length;i++) {
            result.add(func.retCols[i].toString());
        }
        return result;
    }

    @Test
    public void test_exjsontuple() throws Exception
    {

        String args[] = new String[]{"{\"foo\": 42, \"bar\": 55}", "foo", "bar"};

        ArrayList result = run_jsontuple(args);
        assertEquals(2, result.size());
        assertEquals("42", result.get(0).toString());
        assertEquals("55", result.get(1).toString());
    }

}


