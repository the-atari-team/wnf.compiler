// cdw by 'The Atari Team' 2022
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestSourceReader {

  private SourceReader readerSUT;

  @Before
  public void setUp() throws IOException {
    String simple = "src/test/resources/lla/privat/atarixl/compiler/simple.wnf";
    readerSUT = new SourceReader(simple);
  }

  @Test
  public void testSource() throws IOException {
    String program = readerSUT.readFile();

    Assert.assertTrue(program.startsWith("//"));
    Assert.assertTrue(program.length() > 0);
  }

}
