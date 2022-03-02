// cdw by 'The Atari Team' 2021
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler;

import java.io.File;
import java.io.IOException;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import lla.privat.atarixl.compiler.source.Source;

public class ITMain {

  private static String tempPath;

  @BeforeClass
  public static void setUpClass() {
    String OS = System.getProperty("os.name");
    if (OS.startsWith("Windows")) {
      tempPath = "C:/temp/atari";
    }
    else {
      tempPath = "/tmp/atari";
    }
    File directory = new File(tempPath);
    directory.mkdir();

    File file = new File(tempPath, "SIMPLE.ASM");
    file.delete();
  }

  @AfterClass
  public static void tearDown() {
    File file = new File(tempPath + "/TESTCRC.INC");
    file.delete();

    file = new File(tempPath + "/TSTSIEVE.ASM");
    file.delete();
  }

  @Test
  public void testSimple() throws IOException {
    String simple = "src/test/resources/lla/privat/atarixl/compiler/simple.wnf";
    int optimize = 1;
    int verbose = 3;
    Main main = new Main(simple, optimize, verbose).readFile().compile().optimize(optimize).setOutputPath(tempPath)
        .write();
    File file = new File(tempPath, "SIMPLE.ASM");
    Assert.assertTrue(file.exists());

    Assert.assertEquals(1, main.getUsedOptimisations());
    Assert.assertTrue(main.getSource().hasVariable("HEADER_VARIABLE"));
  }

  @Test
  public void testCrcHash() throws IOException {
    String simple = "src/test/resources/lla/privat/atarixl/compiler/test-crc-hash.wnf";
    int optimize = 1;
    new Main(simple).readFile().compile().optimize(optimize).setOutputPath(tempPath).write();

    File file = new File(tempPath + "/TESTCRC.INC");
    Assert.assertTrue(file.exists());
  }

  @Test
  public void testSieve() throws IOException {
    String simple = "src/test/resources/lla/privat/atarixl/compiler/test-sieve.wnf";
    int optimize = 2;
    Main main = new Main(simple).readFile().compile().optimize(optimize).setOutputPath(tempPath).write();

    File file = new File(tempPath + "/TSTSIEVE.ASM");
    Assert.assertTrue(file.exists());

    Assert.assertEquals(68, main.getUsedOptimisations());
  }

  @Test
  public void testPlayer() throws IOException {
    String testPlayer = "src/test/resources/lla/privat/atarixl/compiler/test-player.wnf";
    int optimize = 0;
    int verbose = 3;
    new Main(testPlayer, optimize, verbose).readFile().compile().optimize(optimize).setOutputPath(tempPath).write();

    File file = new File(tempPath + "/TESTPM1.ASM");
    Assert.assertTrue(file.exists());
  }

  @Test
  public void testSprite() throws IOException {
    String testPlayer = "src/test/resources/lla/privat/atarixl/compiler/test-sprite.wnf";
    int optimize = 0;
    int verbose = 3;
    new Main(testPlayer, optimize, verbose).readFile().compile().optimize(optimize).setOutputPath(tempPath).write();

    File file = new File(tempPath + "/SPRITE.ASM");
    Assert.assertTrue(file.exists());
  }


  @Ignore
  @Test
  public void testOxygeneBe() throws IOException {
    String testOxygeneBe = "src/test/resources/lla/privat/atarixl/compiler/test-player-missile.wnf";
    int optimize = 2;
    int verbose = 1;
    Main main = new Main(testOxygeneBe, optimize, verbose).readFile().compile().optimize(optimize)
        .setOutputPath(tempPath).write();
    File file = new File(tempPath + "/OXYGENBE.ASM");
    Assert.assertTrue(file.exists());

    Assert.assertEquals(1027, main.getUsedOptimisations());
  }

  @Test
  public void testProcedure() {
    Source source = new Source("program name procedure name() begin end begin end");
    new Main(source).compile();

    Assert.assertTrue(source.hasVariable("NAME"));
  }

  @Test
  public void testProcedureParameter() {
    Source source = new Source("program name byte x,y procedure name(x, y) begin end begin end");
    new Main(source).compile();

    Assert.assertTrue(source.hasVariable("NAME"));
    Assert.assertTrue(source.hasVariable("X"));
    Assert.assertTrue(source.hasVariable("Y"));
    Assert.assertEquals(3, source.countOfVariables());
  }

  @Test
  public void testFunction() {
    Source source = new Source("program name function name() begin end begin end");
    new Main(source).compile();

    Assert.assertTrue(source.hasVariable("NAME"));
  }

  @Test
  public void testFunctionWithLocalVariable() {
    Source source = new Source("program name byte x word y function name() local x,y begin end begin end");
    new Main(source).compile();

    Assert.assertTrue(source.hasVariable("X"));
    Assert.assertTrue(source.hasVariable("Y"));
    Assert.assertTrue(source.hasVariable("NAME"));
    // Assert.assertTrue(source.hasVariable("HEADER_VARIABLE"));
  }

  @Test
  public void testArrayAssignment() {
    Source source = new Source("program name byte array x[2] x := 1");
    new Main(source).compile();

    Assert.assertTrue(source.hasVariable("X"));
  }

  @Test
  public void testReadWriteAccess() {
    Source source = new Source("program name byte x,y begin x := 1 y := x end");
    new Main(source).compile();

    Assert.assertTrue(source.hasVariable("X"));
    Assert.assertTrue(source.hasVariable("Y"));

    Assert.assertTrue(source.getVariable("X").hasWriteAccess());
    Assert.assertTrue(source.getVariable("Y").hasWriteAccess());
    Assert.assertTrue(source.getVariable("X").hasReadAccess());
    Assert.assertTrue(source.getVariable("X").hasAllAccess());
    Assert.assertTrue(source.getVariable("X").hasAnyAccess());
  }

  @Test
  public void testFor() {
    Source source = new Source("program name byte a begin for a:=1 to 10 do begin end end");
    new Main(source).compile();

    Assert.assertTrue(source.hasVariable("A__"));
  }

  @Test
  public void testwhile() {
    Source source = new Source("program name byte i begin while i < 1 do begin end end");
    new Main(source).compile();

    Assert.assertTrue(source.hasVariable("I"));
  }

  @Test
  public void testRepeat() {
    Source source = new Source("program name byte i begin repeat i:=i+1 until i == 1 end");
    new Main(source).compile();

    Assert.assertTrue(source.hasVariable("I"));
  }

  @Test
  public void testIf() {
    Source source = new Source("program name byte i begin if i==0 then i:=1 else i:= 2 end");
    new Main(source).compile();

    Assert.assertTrue(source.hasVariable("I"));
  }

  @Test
  public void testAssert() {
    Source source = new Source("program name byte i begin assert(i==0,'show if false') end");
    new Main(source).compile();

    Assert.assertTrue(source.hasVariable("I"));
  }

  @Test(expected = IllegalStateException.class)
  public void testError() {
    Source source = new Source("program name\n byte i\n { \n assert(i==0,'show if false')\n }\n");
    new Main(source).compile();
  }
}
