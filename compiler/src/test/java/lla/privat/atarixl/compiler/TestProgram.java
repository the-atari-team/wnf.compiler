// cdw by 'The Atari Team' 2020
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler;

import org.junit.Assert;
import org.junit.Test;

import lla.privat.atarixl.compiler.source.Source;

public class TestProgram {

  @Test
  public void testProgram() {
    Source source = new Source("PROGRAM name");
    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Program(source).program(symbol).build();

    Assert.assertEquals("NAME", source.getProgramOrIncludeName());

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());
    Assert.assertEquals("$4000", source.getLomem());
  }

  @Test
  public void testProgramAndLomem() {
    Source source = new Source("PROGRAM name lomem=$4001");
    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Program(source).program(symbol).build();

    Assert.assertEquals("NAME", source.getProgramOrIncludeName());

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());
    Assert.assertEquals("$4001", source.getLomem());
    Assert.assertTrue(source.isProgram());
  }

  @Test
  public void testInclude() {
    Source source = new Source("INCLUDE prefix:name");
    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Program(source).program(symbol).build();

    Assert.assertEquals("NAME", source.getProgramOrIncludeName());
    Assert.assertEquals("PREFIX", source.getPrefix());

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());
    Assert.assertFalse(source.isProgram());
  }
}
