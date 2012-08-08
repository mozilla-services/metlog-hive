"""
This Jython script is a template that can be used to test other UDTF
functions
"""
import os
import sys

for file in os.listdir('.'):
    sys.path.append(file)
sys.path.append('../dist/lib/MetlogHive.jar')

from org.apache.hadoop.hive.serde2.objectinspector.primitive import PrimitiveObjectInspectorFactory
from org.mozilla.services.json import ExJSONTuple
from org.apache.hadoop.hive.ql.udf.generic import UDTFCollector
from org.apache.hadoop.hive.ql.exec import UDTFOperator      # NOQA


def run_jsontuple(user_args):

    func = ExJSONTuple()
    func.setCollector(UDTFCollector(UDTFOperator()))

    args = []
    for i in range(len(user_args)):
        args.append(PrimitiveObjectInspectorFactory.writableStringObjectInspector)

    func.initialize(args)

    str_inspector = PrimitiveObjectInspectorFactory.writableStringObjectInspector

    func_args = [str_inspector.create(x) for x in user_args]
    func.process(func_args)
    return [str(x) for x in func.retCols]

def test_exjson_tuple():
    user_args = ['{"foo": 42, "bar": 55}', 'foo', 'bar'] 
    result = run_jsontuple(user_args)

    assert 2 == len(result)
    assert '42' == result[0]
    assert '55' == result[1]

if __name__ == '__main__':
    test_exjson_tuple()
