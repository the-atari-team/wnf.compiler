// cdw by 'The Atari Team' 2020
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler.source;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestCode {

  private Code codeSUT;
  private Source source;

  @Before
  public void setUp() {
    source = new Source("");
    codeSUT = new Code(source) {
      @Override
      public void code(final String codeline) {
        codeGen(codeline);
      }
    };
  }

  @Test
  public void addRealCodeLine() {
    codeSUT.code(" test");

    Assert.assertEquals(" TEST", source.getCode().get(0));
  }

  @Test
  public void testSourceVerboseLevelIs0() {
    Assert.assertEquals(0, source.getVerboseLevel());
  }

  @Test
  public void addCommentLineLevel1() {
    codeSUT.code(";#1 test");

    Assert.assertEquals(0, source.getCode().size());
  }

  @Test
  public void addCommentLineVerboseLevel1wish1() {
    source.setVerboseLevel(1);
    codeSUT.code(";#1 test");

    Assert.assertEquals("; test", source.getCode().get(0));
  }

  @Test
  public void addCommentLineVerboseLevel2wish1() {
    source.setVerboseLevel(2);
    codeSUT.code(";#1 test");

    Assert.assertEquals("; test", source.getCode().get(0));
  }

  @Test
  public void addCommentLineVerboseLevel3wish1() {
    source.setVerboseLevel(3);
    codeSUT.code(";#1 test");

    Assert.assertEquals("; test", source.getCode().get(0));
  }

  @Test
  public void addCommentLineVerboseLevel3wish2() {
    source.setVerboseLevel(3);
    codeSUT.code(";#2 test");

    Assert.assertEquals("; test", source.getCode().get(0));
  }
  @Test
  public void addCommentLineVerboseLevel3wish3() {
    source.setVerboseLevel(3);
    codeSUT.code(";#3 test");

    Assert.assertEquals("; test", source.getCode().get(0));
  }
}
