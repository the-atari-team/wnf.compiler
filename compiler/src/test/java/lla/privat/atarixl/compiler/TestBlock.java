// cdw by 'The Atari Team' 2020
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import lla.privat.atarixl.compiler.source.Source;

public class TestBlock {

  private Block blockSUT;

  private List<String> includes;

  @Before
  public void setUp() {
    Source source = new Source("PROGRAM name");
    blockSUT = new Block(source);
    includes = new ArrayList<>();
  }

  @Test
  public void testPaths() {

    List<String> functions = new ArrayList<>();
    functions.add("@LONG");

    includes.add("src/test/resources/lla/privat/atarixl/compiler/inc1");
//    includes.add("src/test/resources/lla/privat/atarixl/compiler/inc2");

    List<String> includeList = blockSUT.createIncludeList(includes, functions);
    Assert.assertEquals(1, includeList.size());
  }

}
