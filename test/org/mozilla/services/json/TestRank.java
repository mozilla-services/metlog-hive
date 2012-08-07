package org.mozilla.services.json;

import com.jointhegrid.hive_test.HiveTestService;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.MiniDFSCluster;

import org.apache.hadoop.fs.FileSystem;


public class TestRank extends HiveTestService {


  public TestRank() throws IOException {
    super();
  }

  public void testRank() throws Exception {
      /*
    String jarFile;
    jarFile = ExJSONTuple.class.getProtectionDomain().getCodeSource().getLocation().getFile();
    System.out.println("Using JAR: " + jarFile);
    try
    {
        client.execute("CREATE TABLE test_metlog_json (json_data int)");
        client.execute("drop table test_metlog_json");
    } catch (Exception e) {
        e.printStackTrace();
    }
    */
  }
}
