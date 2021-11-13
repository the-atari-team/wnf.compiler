// cdw by 'The Atari Team' 2021
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler.statement;

import org.junit.Assert;
import org.junit.Test;

import lla.privat.atarixl.compiler.Symbol;
import lla.privat.atarixl.compiler.SymbolEnum;
import lla.privat.atarixl.compiler.expression.Type;
import lla.privat.atarixl.compiler.source.Source;

public class TestAssert {

  @Test
  public void testAssertWithText() {
    Source source = new Source("assert(i==1,'false String')");
    source.addVariable("I", Type.BYTE);
    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new lla.privat.atarixl.compiler.statement.Assert(source).statement(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());
  }

  @Test
  public void testAssert() {
    Source source = new Source("assert(i==1)");
    source.addVariable("I", Type.BYTE);
    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new lla.privat.atarixl.compiler.statement.Assert(source).statement(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());
  }

}
