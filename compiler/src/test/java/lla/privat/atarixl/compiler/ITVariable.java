// cdw by 'The Atari Team' 2022
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import lla.privat.atarixl.compiler.source.Source;

public class ITVariable {
  private String tempPath;

  @Before
  public void setUp() {
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

  @Test(expected = IllegalStateException.class)
  public void testUnknownVariable() throws IOException {
    Source source = new Source("program name procedure name() begin end begin unknown:=1 end");
    new Main(source).compile();
  }

  @Test
  public void testknownVariable() throws IOException {
    Source source = new Source("program name byte known procedure name() begin end begin known:=1 end");
    new Main(source).compile();
  }

  @Test(expected = IllegalStateException.class)
  public void testUnknownParameter() throws IOException {
    Source source = new Source("program name procedure name(unknown) begin end begin unknown:=1 end");
    new Main(source).compile();
  }

  @Test(expected = IllegalStateException.class)
  public void testWriteReadOnlyVariable() throws IOException {
    Source source = new Source("program name byte readonly nowrite begin nowrite:=1 end");
    new Main(source).compile();
  }

}
