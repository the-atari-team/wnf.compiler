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
    registerOptimizerSUT = new RegisterOptimizer(source, 4);
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

  @Test
  public void testLoad0() {
    List<String> list = new ArrayList<>();
    list.add(" LDA #<0");
    list.add(" STA A");
    list.add(" LDA #>0"); // This should be optimized
    list.add(" STA A+1");
    list.add(" ...");
    source.resetCode(list);
    
    registerOptimizerSUT.optimize();
    
    Assert.assertEquals(1, registerOptimizerSUT.getUsedOptimisations());
  }

  @Test
  public void testStoreAsName_loadAImmidiatly() {
    List<String> list = new ArrayList<>();
    list.add(" LDA #<1");
    list.add(" STA A");
    list.add(" LDA #<1"); // This should be optimized
    list.add(" STA B");
    list.add(" ...");
    source.resetCode(list);
    
    registerOptimizerSUT.optimize();
    
    Assert.assertEquals(1, registerOptimizerSUT.getUsedOptimisations());
  }

  @Test
  public void testStoreAsName_loadAIndirectIndexed() {
    List<String> list = new ArrayList<>();
    list.add(" LDY #1");
    list.add(" LDA (HEAP_PTR),Y");
    list.add(" LDY #2");
    list.add(" LDA (HEAP_PTR),Y"); // must not optimized
    list.add(" STA B");
    list.add(" ...");
    source.resetCode(list);
    
    registerOptimizerSUT.optimize();
    
    Assert.assertEquals(0, registerOptimizerSUT.getUsedOptimisations());
  }

  @Test
  public void testStoreAsName_storeToHeapPtr() {
    List<String> list = new ArrayList<>();
    list.add(" LDY #1");
    list.add(" LDA #0");
    list.add(" STA (HEAP_PTR),Y");
    list.add(" LDY #2");
    list.add(" LDA (HEAP_PTR),Y"); // must not optimized
    list.add(" STA B");
    list.add(" ...");
    source.resetCode(list);
    
    registerOptimizerSUT.optimize();
    
    Assert.assertEquals(0, registerOptimizerSUT.getUsedOptimisations());
  }

  @Test
  public void testStoreAsName_loadA() {
    List<String> list = new ArrayList<>();
    list.add(" LDA #<1");
    list.add(" STA A");
    list.add(" LDA A"); // This should be optimized
    list.add(" STA B");
    list.add(" ...");
    source.resetCode(list);
    
    registerOptimizerSUT.optimize();
    
    Assert.assertEquals(1, registerOptimizerSUT.getUsedOptimisations());
  }
  
  @Test
  public void testStoreAsName_loadXImmidiatly() {
    List<String> list = new ArrayList<>();
    list.add(" LDX #<1");
    list.add(" STX A");
    list.add(" LDX #<1"); // This should be optimized
    list.add(" STX B");
    list.add(" ...");
    source.resetCode(list);
    
    registerOptimizerSUT.optimize();
    
    Assert.assertEquals(1, registerOptimizerSUT.getUsedOptimisations());
  }
  
  @Test
  public void testStoreAsName_loadYImmidiatly() {
    List<String> list = new ArrayList<>();
    list.add(" LDY #<1");
    list.add(" STY A");
    list.add(" LDY #<1"); // This should be optimized
    list.add(" STY B");
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
//    int optimize = 2;

    File file = new File(inputFilename);
    Assert.assertTrue(file.exists());
    List<String> lines = readLines(file);

    Assert.assertEquals(4115, lines.size());
    
    source.resetCode(lines);

    registerOptimizerSUT.optimize().build();
    
    Assert.assertEquals(65, registerOptimizerSUT.getUsedOptimisations());

    List<String> optimizedCode = source.getCode();
    
    File outputFile = new File(tempPath, "ENEMY.INC");
    writeLines(outputFile, optimizedCode);
  }
  
  @Test
  public void testDudeO2() throws IOException {
    String inputFilename = "src/test/resources/lla/privat/atarixl/compiler/DUDE.INC";
//    int optimize = 2;

    File file = new File(inputFilename);
    Assert.assertTrue(file.exists());
    List<String> lines = readLines(file);

    Assert.assertEquals(3178, lines.size());
    
    source.resetCode(lines);

    registerOptimizerSUT.optimize().build();
    
    Assert.assertEquals(41, registerOptimizerSUT.getUsedOptimisations());

    List<String> optimizedCode = source.getCode();
    
    File outputFile = new File(tempPath, "DUDE.INC");
    writeLines(outputFile, optimizedCode);
  }

  /*
 LDY X1
 LDA X1+1 ; (48)
 LDX SP ; (38c)
 STA POINTSHOW_HIGH,X
 TYA
 STA POINTSHOW_LOW,X
;
; [187]  sp:=sp+1
;
 INC SP ; (17a)
;
; [189]  pointshow[sp] := y1
;
 LDY Y1
 LDA Y1+1 ; (48)
;  LDX SP ; (38c) ; (100)
 STA POINTSHOW_HIGH,X
 TYA
 STA POINTSHOW_LOW,X
*/
  @Test
  public void testINC_loadAgain() {
    List<String> list = new ArrayList<>();

    list.add(" LDY X1");
    list.add(" LDA X1+1");
    list.add(" LDX SP");
    list.add(" STA POINTSHOW_HIGH,X");
    list.add(" TYA");
    list.add(" STA POINTSHOW_LOW,X");
    list.add(" INC SP"); // SP is changed
    list.add(" LDY Y1");
    list.add(" LDA Y1+1");
    list.add(" LDX SP"); // MUST NOT OPTIMIZE
    list.add(" STA POINTSHOW_HIGH,X");
    list.add(" TYA");
    list.add(" STA POINTSHOW_LOW,X");
    list.add(" ...");
    
    source.resetCode(list);
    
    registerOptimizerSUT.optimize();
    
    Assert.assertEquals(0, registerOptimizerSUT.getUsedOptimisations());
  }

  @Test
  public void testStoreLoadOtherRegister() {
    List<String> list = new ArrayList<>();

    list.add(" LDX memory");
    list.add(" LDA #1");
    list.add(" STA memory");
    list.add(" LDX memory"); // must not optimized
    list.add(" ...");
    
    source.resetCode(list);
    
    registerOptimizerSUT.optimize();
    
    Assert.assertEquals(0, registerOptimizerSUT.getUsedOptimisations());
  }
  

  @Test
  public void testLoadModifyStoreINC_loadAgain() {
    List<String> list = new ArrayList<>();

    list.add(" LDA memory");
    list.add(" INC memory");
    list.add(" LDA memory"); // must not optimized
    list.add(" ...");
    
    source.resetCode(list);
    
    registerOptimizerSUT.optimize();
    
    Assert.assertEquals(0, registerOptimizerSUT.getUsedOptimisations());
  }
  
  @Test
  public void testLoadModifyStoreINCX_loadAgain() {
    List<String> list = new ArrayList<>();

    list.add(" LDA memory");
    list.add(" INC memory,x");
    list.add(" LDA memory"); // must not optimized
    list.add(" ...");
    
    source.resetCode(list);
    
    registerOptimizerSUT.optimize();
    
    Assert.assertEquals(0, registerOptimizerSUT.getUsedOptimisations());
  }

  @Test
  public void testLoadModifyStoreDEC_loadAgain() {
    List<String> list = new ArrayList<>();

    list.add(" LDA memory");
    list.add(" INC memory");
    list.add(" LDA memory"); // must not optimized
    list.add(" ...");
    
    source.resetCode(list);
    
    registerOptimizerSUT.optimize();
    
    Assert.assertEquals(0, registerOptimizerSUT.getUsedOptimisations());
  }
  @Test
  public void testLoadModifyStoreASL_loadAgain() {
    List<String> list = new ArrayList<>();

    list.add(" LDA memory");
    list.add(" ASL memory");
    list.add(" LDA memory"); // must not optimized
    list.add(" ...");
    
    source.resetCode(list);
    
    registerOptimizerSUT.optimize();
    
    Assert.assertEquals(0, registerOptimizerSUT.getUsedOptimisations());
  }

  @Test
  public void testLoadModifyStoreASL_A_loadAgain() {
    List<String> list = new ArrayList<>();

    list.add(" LDA memory");
    list.add(" ASL A");
    list.add(" LDA memory"); // must not optimized
    list.add(" ...");
    
    source.resetCode(list);
    
    registerOptimizerSUT.optimize();
    
    Assert.assertEquals(0, registerOptimizerSUT.getUsedOptimisations());
  }
  
  @Test
  public void testLoadModifyStoreLSR_loadAgain() {
    List<String> list = new ArrayList<>();

    list.add(" LDA memory");
    list.add(" LSR memory");
    list.add(" LDA memory"); // must not optimized
    list.add(" ...");
    
    source.resetCode(list);
    
    registerOptimizerSUT.optimize();
    
    Assert.assertEquals(0, registerOptimizerSUT.getUsedOptimisations());
  }
  @Test
  public void testLoadModifyStoreROR_loadAgain() {
    List<String> list = new ArrayList<>();

    list.add(" LDA memory");
    list.add(" ROR memory");
    list.add(" LDA memory"); // must not optimized
    list.add(" ...");
    
    source.resetCode(list);
    
    registerOptimizerSUT.optimize();
    
    Assert.assertEquals(0, registerOptimizerSUT.getUsedOptimisations());
  }
  @Test
  public void testLoadModifyStoreROL_loadAgain() {
    List<String> list = new ArrayList<>();

    list.add(" LDA memory");
    list.add(" ROL memory");
    list.add(" LDA memory"); // must not optimized
    list.add(" ...");
    
    source.resetCode(list);
    
    registerOptimizerSUT.optimize();
    
    Assert.assertEquals(0, registerOptimizerSUT.getUsedOptimisations());
  }
  @Test
  public void testLoadStoreROL_loadAgain() {
    List<String> list = new ArrayList<>();

    list.add(" LDA memory");
    list.add(" STA memory");
    list.add(" LDA memory"); // should optimized
    list.add(" ...");
    
    source.resetCode(list);
    
    registerOptimizerSUT.optimize();
    
    Assert.assertEquals(1, registerOptimizerSUT.getUsedOptimisations());
  }
  
  @Test
  public void testLoadModifyX_loadAgain() {
    List<String> list = new ArrayList<>();

    list.add(" LDX memory");
    list.add(" INX");
    list.add(" LDX memory"); // must not optimized
    list.add(" ...");
    
    source.resetCode(list);
    
    registerOptimizerSUT.optimize();
    
    Assert.assertEquals(0, registerOptimizerSUT.getUsedOptimisations());
  }

  
  @Test
  public void testLoadModifyX_loadAgain2() {
    List<String> list = new ArrayList<>();

    list.add(" LDX memory");
    list.add(" LDY EM_INDEX");
    list.add(" STY @PUTARRAY");
    list.add(" LDY #<0");
    list.add(" TYA");
    list.add(" LDX @PUTARRAY");
    list.add(" STA EM_ARROW_IN_MOVE,X");
    list.add(";");
    list.add("; [76]  fly_count[index] := 1     // slowdown for fly_index increment");
    list.add(";");
    list.add(" LDY EM_INDEX");
    list.add(" STY @PUTARRAY");
    list.add(" LDY #<1");
    list.add(" TYA");
    list.add(" LDX @PUTARRAY");
    list.add(" STA EM_FLY_COUNT,X");
    list.add(" ...");
 
    source.resetCode(list);
  
    registerOptimizerSUT.optimize();
  
    Assert.assertEquals(0, registerOptimizerSUT.getUsedOptimisations());
  }

  @Test
  public void testLoadXYindirect() {
    List<String> list = new ArrayList<>();

    list.add(";");
    list.add("; [131]  x1 := pointshow[x1]");
    list.add(";");
    list.add(" LDY X1");
    list.add(" LDX POINTSHOW_HIGH,Y ; (45)");
    list.add(" LDA POINTSHOW_LOW,Y");
    list.add(" STA X1 ; (11)");
    list.add(" STX X1+1");
    list.add(";");
    list.add("; [132]  y1 := pointshow[y1]");
    list.add(";");
    list.add(" LDY Y1");
    list.add(" LDX POINTSHOW_HIGH,Y ; (45)"); // Das darf nicht optimiert werden, da Y-Register anders sein kann!
    list.add(" LDA POINTSHOW_LOW,Y");
    list.add(" STA Y1 ; (11)");
    list.add(" STX Y1+1");
    list.add(" ...");
 
    source.resetCode(list);
  
    registerOptimizerSUT.optimize();
  
    Assert.assertEquals(0, registerOptimizerSUT.getUsedOptimisations());
  }

}
