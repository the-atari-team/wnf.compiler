// cdw by 'The Atari Team' 2022
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler.statement;

import org.junit.Test;
import org.junit.Assert;

import lla.privat.atarixl.compiler.Symbol;
import lla.privat.atarixl.compiler.SymbolEnum;
import lla.privat.atarixl.compiler.expression.Type;
import lla.privat.atarixl.compiler.source.Source;

public class TestIfThenElse {

  @Test
  public void testIfThen() {
    Source source = new Source("if x==1 then a:=2");
    Symbol symbol = source.nextElement();
    source.addVariable("X", Type.BYTE);
    source.addVariable("A__", Type.BYTE);

    Symbol nextSymbol = new IfThenElse(source).statement(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

  }

  @Test
  public void testIfThenElse() {
    Source source = new Source("if x==1 then a:=2 else a:=3");
    Symbol symbol = source.nextElement();
    source.addVariable("X", Type.BYTE);
    source.addVariable("A__", Type.BYTE);

    Symbol nextSymbol = new IfThenElse(source).statement(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

  }
}
