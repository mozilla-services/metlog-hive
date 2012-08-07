package org.mozilla.services.json;
import com.jointhegrid.hive_test.HiveTestService;
import java.io.IOException;


public class RankTest extends HiveTestService {

  public RankTest() throws IOException {
    super();
  }

  public void testRank() throws Exception {
    String jarFile;
    jarFile = ExJSONTuple.class.getProtectionDomain().getCodeSource().getLocation().getFile();
    System.out.println("Using JAR: " + jarFile);
    client.execute("CREATE TABLE test_metlog_json (json_data STRING)");
    client.execute("drop table test_metlog_json");
  }
}
