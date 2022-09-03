package lla.privat.atarixl.compiler.linker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    IncludeGenerator includeGeneratorSUT = new IncludeGenerator(null, null, null);
    Assert.assertEquals(0, includeGeneratorSUT.build().size());
  }

  // Was wollen wir testen?
  // Sind alle unbekannten Funktionen durch includes abgedeckt
  // Gibt es doppelte Ambiguous funktionen (@PLOT als Beispiel)
  // Gibt es unnoetige includes?
  // Welche includes fehlen?
  @Test
  public void testPaths() {

    List<String> functionsToSearchFor = new ArrayList<>();

    List<String> includePaths = new ArrayList<>();
    includePaths.add("src/test/resources/lla/privat/atarixl/compiler/inc1");
    
    List<String> currentIncludes = new ArrayList<>();
    
    IncludeGenerator includeGenerator = new IncludeGenerator(includePaths, currentIncludes, functionsToSearchFor);
    includeGenerator.collectIncludeFiles();
    List<String> names = includeGenerator.getIncludeFilenames();
    
    Assert.assertEquals(4, names.size());
  }

  @Test
  public void test2Paths() {

    List<String> functionsToSearchFor = new ArrayList<>();

    List<String> includePaths = new ArrayList<>();
    includePaths.add("src/test/resources/lla/privat/atarixl/compiler/inc1");
    includePaths.add("src/test/resources/lla/privat/atarixl/compiler/inc2");
    
    List<String> currentIncludes = new ArrayList<>();
    
    IncludeGenerator includeGenerator = new IncludeGenerator(includePaths, currentIncludes, functionsToSearchFor);
    includeGenerator.collectIncludeFiles();
    List<String> names = includeGenerator.getIncludeFilenames();
    
    Assert.assertEquals(5, names.size());
  }
  
  @Test
  public void test2PathsAllFunctions() {

    List<String> functionsToSearchFor = new ArrayList<>();

    List<String> includePaths = new ArrayList<>();
    includePaths.add("src/test/resources/lla/privat/atarixl/compiler/inc1");
    includePaths.add("src/test/resources/lla/privat/atarixl/compiler/inc2");
    
    List<String> currentIncludes = new ArrayList<>();
    
    IncludeGenerator includeGenerator = new IncludeGenerator(includePaths, currentIncludes, functionsToSearchFor);
    includeGenerator.collectIncludeFiles();
    Map<String, String> names = includeGenerator.collectAllFunctionsOutOfFiles();
    
    Assert.assertEquals(4, names.size());
    
    // jeder Funktionsname ist nur einmal(!) vorhanden, aber ggf. enthaelt der Value ein ';'
    Assert.assertTrue(names.containsKey("@PLOT"));
    Assert.assertTrue(names.containsKey("@PLOT_II"));
    Assert.assertTrue(names.containsKey("@LONG"));
    Assert.assertTrue(names.containsKey("@LONGER"));
  }
  
  @Test
  public void test2PathsRemoveAllFunctionsWeAlreadyInclude() {

    List<String> functionsToSearchFor = new ArrayList<>();

    List<String> includePaths = new ArrayList<>();
    includePaths.add("src/test/resources/lla/privat/atarixl/compiler/inc1");
    includePaths.add("src/test/resources/lla/privat/atarixl/compiler/inc2");
    
    List<String> currentIncludes = new ArrayList<>();
    currentIncludes.add("src/test/resources/lla/privat/atarixl/compiler/inc1/GFX1BIT.INC");
    
    IncludeGenerator includeGenerator = new IncludeGenerator(includePaths, currentIncludes, functionsToSearchFor);
    includeGenerator.collectIncludeFiles();
    Map<String, String> names = includeGenerator.collectAllFunctionsLeaveOutAlreadyIncluded();
    
    Assert.assertEquals(2, names.size());
    
    // jeder Funktionsname ist nur einmal(!) vorhanden, aber ggf. enthaelt der Value ein ';'
    Assert.assertTrue(names.containsKey("@LONG"));
    Assert.assertTrue(names.containsKey("@LONGER"));
  }

  @Test
  public void testShowAllUnknownFunctionsButIncludeExistsForIt() {
    List<String> functionsToSearchFor = new ArrayList<>();
    functionsToSearchFor.add("@long");    // we want to find the include for this
    functionsToSearchFor.add("@PLOTTER"); // this is unknown, not handled here!
    
    List<String> includePaths = new ArrayList<>();
    includePaths.add("src/test/resources/lla/privat/atarixl/compiler/inc1");
    includePaths.add("src/test/resources/lla/privat/atarixl/compiler/inc2");
    
    List<String> currentIncludes = new ArrayList<>();
    currentIncludes.add("src/test/resources/lla/privat/atarixl/compiler/inc1/GFX1BIT.INC");
    
    IncludeGenerator includeGenerator = new IncludeGenerator(includePaths, currentIncludes, functionsToSearchFor);
    includeGenerator.collectIncludeFiles();
    Map<String, String> names = includeGenerator.getAllUnknownFunctionsButIncludeExistsForIt();
    
    Assert.assertEquals(1, names.size());
    
    // jeder Funktionsname ist nur einmal(!) vorhanden, aber ggf. enthaelt der Value ein ';'
    Assert.assertTrue(names.containsKey("@LONG"));
    includeGenerator.showAllUnknownFunctionsButIncludeExistsForIt();
  }

  @Test
  public void testGetAllUnknownFunctions() {
    List<String> functionsToSearchFor = new ArrayList<>();
    functionsToSearchFor.add("@PLOTTER");
    functionsToSearchFor.add("@PLOT_II");
    
    List<String> includePaths = new ArrayList<>();
    includePaths.add("src/test/resources/lla/privat/atarixl/compiler/inc1");
    includePaths.add("src/test/resources/lla/privat/atarixl/compiler/inc2");
    
    List<String> currentIncludes = new ArrayList<>();
    currentIncludes.add("src/test/resources/lla/privat/atarixl/compiler/inc1/GFX1BIT.INC");
    
    IncludeGenerator includeGenerator = new IncludeGenerator(includePaths, currentIncludes, functionsToSearchFor);
    includeGenerator.collectIncludeFiles();
    List<String> names = includeGenerator.getAllUnknownFunctions();
    
    Assert.assertEquals(1, names.size());
    
    // jeder Funktionsname ist nur einmal(!) vorhanden, aber ggf. enthaelt der Value ein ';'
    Assert.assertTrue(names.contains("@PLOTTER"));
    includeGenerator.showAllUnknownFunctions();
  }

//  @Test
//  public void testPathsWithAmbiguous() {
//
//    List<String> functionsToSearchFor = new ArrayList<>();
//    functionsToSearchFor.add("@LONG");
//
//    List<String> includePaths = new ArrayList<>();
//    includePaths.add("src/test/resources/lla/privat/atarixl/compiler/inc1");
//    includePaths.add("src/test/resources/lla/privat/atarixl/compiler/inc2");
//    
//    List<String> currentIncludes = new ArrayList<>();
//
//    IncludeGenerator includeGenerator = new IncludeGenerator(includePaths, currentIncludes, functionsToSearchFor);
//    List<String> includeList = includeGenerator.build();
//    Assert.assertEquals(1, includeList.size());
//  }

  @Test
  public void testRemark() {
    String line = "@test ; hallo";
    int remark = line.indexOf(";");
    line = line.substring(0, remark);
    Assert.assertFalse(line.contains(";"));
  }
}
