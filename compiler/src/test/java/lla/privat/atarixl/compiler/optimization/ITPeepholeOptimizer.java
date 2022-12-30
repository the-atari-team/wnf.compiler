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

public class ITPeepholeOptimizer {
 
  private PeepholeOptimizer peepholeOptimizerSUT;

  private Source source;

  @Before
  public void setUp() {
    source = new Source("");
    peepholeOptimizerSUT = new PeepholeOptimizer(source, 1);
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

    for (int i=0;i<lines.size();i++) {
      final String strLine = lines.get(i);
      bwr.write(strLine);
      bwr.newLine();
    }
    bwr.close();
    
    fstream.close();
  }

  @Test
  public void testSimple() throws IOException {
    String inputFilename = "src/test/resources/lla/privat/atarixl/compiler/ENEMY.INC";
    int optimize = 2;

    File file = new File(inputFilename);
    Assert.assertTrue(file.exists());
    List<String> lines = readLines(file);

    Assert.assertEquals(4115, lines.size());
    
    source.resetCode(lines);

    peepholeOptimizerSUT.setLevel(optimize).optimize().build();
    Assert.assertEquals(481, peepholeOptimizerSUT.getUsedOptimisations());
    
    List<String> optimizedCode = source.getCode();
    Assert.assertEquals(3195, optimizedCode.size());
    
//    String outputFilename = "/tmp/ENEMY.INC";
//    File outputFile = new File(outputFilename);
//    writeLines(outputFile, optimizedCode);
  }

}
