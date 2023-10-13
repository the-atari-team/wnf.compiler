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
import org.junit.Test;

import lla.privat.atarixl.compiler.source.Source;

public class ITOptimizer {

  private PeepholeOptimizer peepholeOptimizerSUT;
  private LongJumpOptimizer longJumpOptimizerSUT;
  private RegisterOptimizer registerOptimizerSUT;

  private Source source;

  @Before
  public void setUp() {
    source = new Source("");
    peepholeOptimizerSUT = new PeepholeOptimizer(source, 4);
    registerOptimizerSUT = new RegisterOptimizer(source, 4);
    longJumpOptimizerSUT = new LongJumpOptimizer(source, 4);
  }

  /*
   * Helper Function to read a given File line by line
   * 
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
  public void testSimpleOptimize2() throws IOException {
    String inputFilename = "src/test/resources/lla/privat/atarixl/compiler/ENEMY_O0.INC";
    int optimize = 4;

    File file = new File(inputFilename);
    Assert.assertTrue(file.exists());
    List<String> lines = readLines(file);

    Assert.assertEquals(3121, lines.size());

    source.resetCode(lines);

    // This is the correct order to optimize the code
    // peephole
    // register
    // long jump
    peepholeOptimizerSUT.setLevel(optimize).optimize().build();
    registerOptimizerSUT.optimize().build();
    longJumpOptimizerSUT.optimize().build();

    Assert.assertEquals(410, peepholeOptimizerSUT.getUsedOptimisations());
    Assert.assertEquals(80, registerOptimizerSUT.getUsedOptimisations());
    Assert.assertEquals(72, longJumpOptimizerSUT.getUsedOptimisations());

    List<String> optimizedCode = source.getCode();
    Assert.assertEquals(2271, optimizedCode.size());

//    String outputFilename = "C:/temp/ENEMY_2.INC";
//    File outputFile = new File(outputFilename);
//    writeLines(outputFile, optimizedCode);
  }

}
