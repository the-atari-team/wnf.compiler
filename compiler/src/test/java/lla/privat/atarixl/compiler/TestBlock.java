// cdw by 'The Atari Team' 2022
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import lla.privat.atarixl.compiler.source.Source;

public class TestBlock {

  private Block blockSUT;

  @Test
  public void testSimplestBlock() {

    Source source = new Source("PROGRAM name begin end"); // no real code
    blockSUT = new Block(source);

    Symbol build = blockSUT.start(null).build();

    Assert.assertEquals("", build.get());

    List<String> codelines = source.getCode();
    Assert.assertEquals(17, codelines.size());
  }

}
