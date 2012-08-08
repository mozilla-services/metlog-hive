import os
import sys
import StringIO

for file in os.listdir('.'):
    sys.path.append(file)
sys.path.append('../dist/lib/MetlogHive.jar')


from java.util import ArrayList
from org.apache.hadoop.io import Text
from org.apache.hadoop.hive.serde2.objectinspector.primitive import *
from org.apache.hadoop.hive.serde2.objectinspector import *
from org.mozilla.services.json import *
from org.apache.hadoop.hive.ql.udf.generic import UDTFCollector
from org.apache.hadoop.hive.ql.exec import UDTFOperator

func = ExJSONTuple()
op = UDTFOperator()
collector = UDTFCollector(op)
func.setCollector(collector)

values = []
values.append(PrimitiveObjectInspectorFactory.writableStringObjectInspector)
values.append(PrimitiveObjectInspectorFactory.writableStringObjectInspector)
values.append(PrimitiveObjectInspectorFactory.writableStringObjectInspector)

#op.initialize(func.conf, values)

func.initialize(values)
args = []

json_txt = PrimitiveObjectInspectorFactory.writableStringObjectInspector.create('{"foo": 42, "bar": 55}')
foo_txt = PrimitiveObjectInspectorFactory.writableStringObjectInspector.create('foo')
bar_txt = PrimitiveObjectInspectorFactory.writableStringObjectInspector.create('bar')

stderr = sys.stderr
stdout = sys.stdout

mock_stderr = StringIO.StringIO()
mock_stdout = StringIO.StringIO()

sys.stdout = mock_stdout
sys.stderr = mock_stderr

try:
    func.process([json_txt, foo_txt, bar_txt])
except:
    print '========== Std Err ========='
    print mock_stderr.getvalue()
    print '========== Std Out ========='
    print mock_stdout.getvalue()
    print '-----------'
finally:
    sys.stderr = stderr
    sys.stdout = stdout

print 'Done!!!!!'

