// cdw by 'The Atari Team' 2020
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler.statement;

import org.junit.Assert;
import org.junit.Test;

import lla.privat.atarixl.compiler.Symbol;
import lla.privat.atarixl.compiler.SymbolEnum;
import lla.privat.atarixl.compiler.expression.Type;
import lla.privat.atarixl.compiler.source.Source;

public class TestStatement {

  @Test
  public void testFunctionCall() {
    
    Source source = new Source("@open(1,4,0)");
    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Statement(source).statement(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());    
  }
  
  @Test
  public void testTestAssignment() {
    
    Source source = new Source("x := 1");
    source.addVariable("X", Type.BYTE);
    
    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Statement(source).statement(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());
    
    Assert.assertTrue(source.getVariable("X").hasWriteAccess());
  }

  @Test
  public void testFunctionCall2() {
    
    Source source = new Source("@init_narrowfont(adr:@narrowfont_chicago, adr:@plot1bit)");
    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Statement(source).statement(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());    
  }
  

  
  
}
