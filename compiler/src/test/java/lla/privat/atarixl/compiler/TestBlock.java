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

    List<String> code = source.getCode();
    Assert.assertEquals(17, code.size());
  }

  // The Variable =@ makes the variable visible in the inner variable list
  // but only with a well known memory representation from variables.inc or hardware.inc
  @Test
  public void testProgramBlockWithVariable() {

    Source source = new Source("PROGRAM name byte col_bk=@ begin col_bk:=1 end"); // no real code
    blockSUT = new Block(source);

    Symbol build = blockSUT.start(null).build();

    Assert.assertEquals("", build.get());

    List<String> code = source.getCode();
    Assert.assertEquals(22, code.size());
  }


  // TODO: support byte variable=@ in include files
  // This could be more complex, due to the fact we do not include the variables.inc file
  // also the variable will renamed by PREFIX_<variable>
  @Test
  public void testIncludeBlockWithVariable() {

    Source source = new Source("INCLUDE prefix:name byte var=@ procedure name() begin var:=1 end"); // no real code

    source.setVariableAddress("PREFIX_VAR", "53248");
    blockSUT = new Block(source);

    Symbol build = blockSUT.start(null).build();

    Assert.assertEquals("", build.get());

    List<String> code = source.getCode();
//     Assert.assertEquals(11, code.size());
    int n=3;
    
    Assert.assertEquals(" .LOCAL", code.get(n++));
    
    Assert.assertEquals(" LDY #<1", code.get(12));
    Assert.assertEquals(" STY PREFIX_VAR", code.get(13));

    Assert.assertEquals(" RTS", code.get(15));

  }

}
