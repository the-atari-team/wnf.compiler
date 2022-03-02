package lla.privat.atarixl.compiler.linker;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class TestIncludeGenerator {

//   IncludeGenerator includeGeneratorSUT;
  
//  @Before
//  public void setUp() {
//    includeGeneratorSUT = new IncludeGenerator(null, null);
//  }
  
  @Test
  public void testIncludeGenerator() {
    IncludeGenerator includeGeneratorSUT = new IncludeGenerator(null, null);
    Assert.assertEquals(0, includeGeneratorSUT.build().size());
  }

  @Test
  public void testPaths() {

    List<String> includes = new ArrayList<>();

    List<String> functionsToSearchFor = new ArrayList<>();
    functionsToSearchFor.add("@LONG");

    includes.add("src/test/resources/lla/privat/atarixl/compiler/inc1");
    
    IncludeGenerator includeGenerator = new IncludeGenerator(includes, functionsToSearchFor);
    List<String> includeList = includeGenerator.build();
    Assert.assertEquals(1, includeList.size());
  }

  @Test
  public void testPathsWithAmbiguous() {

    List<String> includes = new ArrayList<>();

    List<String> functionsToSearchFor = new ArrayList<>();
    functionsToSearchFor.add("@LONG");

    includes.add("src/test/resources/lla/privat/atarixl/compiler/inc1");
    includes.add("src/test/resources/lla/privat/atarixl/compiler/inc2");
    
    IncludeGenerator includeGenerator = new IncludeGenerator(includes, functionsToSearchFor);
    List<String> includeList = includeGenerator.build();
    Assert.assertEquals(1, includeList.size());
  }

  @Test
  public void testRemark() {
    String line = "@test ; hallo";
    int remark = line.indexOf(";");
    line = line.substring(0, remark);
    Assert.assertFalse(line.contains(";"));
  }
}
