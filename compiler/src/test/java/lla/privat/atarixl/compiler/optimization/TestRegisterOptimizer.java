package lla.privat.atarixl.compiler.optimization;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import lla.privat.atarixl.compiler.source.Source;

public class TestRegisterOptimizer {

  private RegisterOptimizer registerOptimizerSUT;
  
  private Source source;

  private static String tempPath;
  
  @BeforeClass
  public static void setUpClass() {
    String OS = System.getProperty("os.name");
    if (OS.startsWith("Windows")) {
      tempPath = "C:/temp/compiler";
    }
    else {
      tempPath = "/tmp/compiler";
    }
    File directory = new File(tempPath);
    directory.mkdir();

    File file = new File(tempPath, "ENEMY.INC");
    file.delete();
  }

  @Before
  public void setUp() {
    source = new Source("");
    registerOptimizerSUT = new RegisterOptimizer(source);
  }
 
  @Test
  public void testAssignmentByteArrayWithDecrement() {
    List<String> list = new ArrayList<>();
    list.add(" LDY INDEX");
    list.add(" LDA X,Y");
    list.add(" LDY INDEX"); // This should be optimized
    list.add(" STY EGAL");
    list.add(" ...");
    source.resetCode(list);
    
    registerOptimizerSUT.optimize();
    
    Assert.assertEquals(1, registerOptimizerSUT.getUsedOptimisations());
  }
  
  @Test
  public void testStoreAsName_loadAgain() {
    List<String> list = new ArrayList<>();
    list.add(" LDY A");
    list.add(" INY");
    list.add(" STY B");
    list.add(" LDY B"); // This should be optimized
    list.add(" ...");
    source.resetCode(list);
    
    registerOptimizerSUT.optimize();
    
    Assert.assertEquals(1, registerOptimizerSUT.getUsedOptimisations());
  }
  
  /*
   * Helper Function to read a given File line by line
   * @return List<String> of lines
   */
  private List<String> readLines(File file) throws IOException {
    List<String> lines = new ArrayList<>();
    FileInputStream fstream = new FileInputStream(file);
    BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

    String strLine;

    while ((strLine = br.readLine()) != null) {
      lines.add(strLine);
    }

    fstream.close();
    return lines;
  }

  private void writeLines(File file, List<String> lines) throws IOException {
    FileOutputStream fstream = new FileOutputStream(file);
    BufferedWriter bwr = new BufferedWriter(new OutputStreamWriter(fstream));

    for (int i = 0; i < lines.size(); i++) {
      final String strLine = lines.get(i);
      bwr.write(strLine);
      bwr.newLine();
    }
    bwr.close();

    fstream.close();
  }

  @Test
  public void testEnemyO0() throws IOException {
    String inputFilename = "src/test/resources/lla/privat/atarixl/compiler/ENEMY.INC";
    int optimize = 2;

    File file = new File(inputFilename);
    Assert.assertTrue(file.exists());
    List<String> lines = readLines(file);

    Assert.assertEquals(4115, lines.size());
    
    source.resetCode(lines);

    registerOptimizerSUT.optimize().build();
    
    Assert.assertEquals(41, registerOptimizerSUT.getUsedOptimisations());

    List<String> optimizedCode = source.getCode();
    
    File outputFile = new File(tempPath, "ENEMY.INC");
    writeLines(outputFile, optimizedCode);
  }
  
  @Test
  public void testDudeO2() throws IOException {
    String inputFilename = "src/test/resources/lla/privat/atarixl/compiler/DUDE.INC";
    int optimize = 2;

    File file = new File(inputFilename);
    Assert.assertTrue(file.exists());
    List<String> lines = readLines(file);

    Assert.assertEquals(3178, lines.size());
    
    source.resetCode(lines);

    registerOptimizerSUT.optimize().build();
    
    Assert.assertEquals(29, registerOptimizerSUT.getUsedOptimisations());

    List<String> optimizedCode = source.getCode();
    
    File outputFile = new File(tempPath, "DUDE.INC");
    writeLines(outputFile, optimizedCode);
  }

}
