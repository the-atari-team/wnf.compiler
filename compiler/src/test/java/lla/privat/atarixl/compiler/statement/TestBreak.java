package lla.privat.atarixl.compiler.statement;

import org.junit.Assert;
import org.junit.Test;

import lla.privat.atarixl.compiler.Symbol;
import lla.privat.atarixl.compiler.SymbolEnum;
import lla.privat.atarixl.compiler.source.Source;

public class TestBreak {

  @Test(expected = IllegalStateException.class)
  public void testBreakOutsideBreakableStage() {
    Source source = new Source("break");
    Symbol symbol = source.nextElement();
    
    /*Symbol nextSymbol =*/ new Break(source).statement(symbol).build();       
  }

  @Test
  public void testBreakInsideBreakableStage() {
    Source source = new Source("break");
    source.addBreakVariable("hallo");
    Symbol symbol = source.nextElement();
    
    Symbol nextSymbol = new Break(source).statement(symbol).build();
       
    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());    
  }

}
