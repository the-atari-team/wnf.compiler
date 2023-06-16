// cdw by 'The Atari Team' 2022
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler.statement;

import org.junit.Test;

import java.util.List;

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
  public void testIfAeq1ThenElse() {
    Source source = new Source("if x==1 then a:=2 else a:=3");
    Symbol symbol = source.nextElement();
    source.addVariable("X", Type.BYTE);
    source.addVariable("A__", Type.BYTE);

    Symbol nextSymbol = new IfThenElse(source).statement(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    List<String> code = source.getCode();
    int n=-1;
    Assert.assertEquals(" LDY X", code.get(++n));
    Assert.assertEquals(" STY @ERG", code.get(++n));
    Assert.assertEquals(" LDY #<1", code.get(++n));
    Assert.assertEquals("; Bedingung (a==b)", code.get(++n));
    Assert.assertEquals(" CPY @ERG", code.get(++n));

    // TODO: Ersetzen durch "JNE ?ELSE1"
    Assert.assertEquals(" BNE ?FA1", code.get(++n));
    Assert.assertEquals(" JMP ?THEN1", code.get(++n));
    Assert.assertEquals("?FA1", code.get(++n));
    Assert.assertEquals(" JMP ?ELSE1", code.get(++n));
    
    Assert.assertEquals("?THEN1", code.get(++n));
    Assert.assertEquals(";", code.get(++n));
    Assert.assertEquals("; [1]  if x==1 then a:=2 else a:=3", code.get(++n));
    Assert.assertEquals(";", code.get(++n));
    Assert.assertEquals(" LDY #<2", code.get(++n));
    Assert.assertEquals(" STY A__", code.get(++n));
    Assert.assertEquals(" JMP ?ENDIF1", code.get(++n));
    Assert.assertEquals("?ELSE1", code.get(++n));
    Assert.assertEquals(";", code.get(++n));
    Assert.assertEquals("; [1]  if x==1 then a:=2 else a:=3", code.get(++n));
    Assert.assertEquals(";", code.get(++n));
    Assert.assertEquals(" LDY #<3", code.get(++n));
    Assert.assertEquals(" STY A__", code.get(++n));
    Assert.assertEquals("?ENDIF1", code.get(++n));

    Assert.assertEquals(22, n);
  }
  
  @Test
  public void testIfAne1ThenElse() {
    Source source = new Source("if x!=1 then a:=2 else a:=3");
    Symbol symbol = source.nextElement();
    source.addVariable("X", Type.BYTE);
    source.addVariable("A__", Type.BYTE);

    Symbol nextSymbol = new IfThenElse(source).statement(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    List<String> code = source.getCode();
    int n=-1;
    Assert.assertEquals(" LDY X", code.get(++n));
    Assert.assertEquals(" STY @ERG", code.get(++n));
    Assert.assertEquals(" LDY #<1", code.get(++n));
    Assert.assertEquals("; Bedingung (a!=b)", code.get(++n));
    Assert.assertEquals(" CPY @ERG", code.get(++n));

    // TODO: Ersetzen durch "JNE ?ELSE1"
    Assert.assertEquals(" BEQ ?FA1", code.get(++n));
    Assert.assertEquals(" JMP ?THEN1", code.get(++n));
    Assert.assertEquals("?FA1", code.get(++n));
    Assert.assertEquals(" JMP ?ELSE1", code.get(++n));
    
    Assert.assertEquals("?THEN1", code.get(++n));
    Assert.assertEquals(";", code.get(++n));
    Assert.assertEquals("; [1]  if x!=1 then a:=2 else a:=3", code.get(++n));
    Assert.assertEquals(";", code.get(++n));
    Assert.assertEquals(" LDY #<2", code.get(++n));
    Assert.assertEquals(" STY A__", code.get(++n));
    Assert.assertEquals(" JMP ?ENDIF1", code.get(++n));
    Assert.assertEquals("?ELSE1", code.get(++n));
    Assert.assertEquals(";", code.get(++n));
    Assert.assertEquals("; [1]  if x!=1 then a:=2 else a:=3", code.get(++n));
    Assert.assertEquals(";", code.get(++n));
    Assert.assertEquals(" LDY #<3", code.get(++n));
    Assert.assertEquals(" STY A__", code.get(++n));
    Assert.assertEquals("?ENDIF1", code.get(++n));

    Assert.assertEquals(22, n);
  }

  @Test
  public void testIfAorBThenElse() {
    Source source = new Source("if x==1 OR x==2 then a:=2 else a:=3");
    Symbol symbol = source.nextElement();
    source.addVariable("X", Type.BYTE);
    source.addVariable("A__", Type.BYTE);

    Symbol nextSymbol = new IfThenElse(source).statement(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    List<String> code = source.getCode();
    int n=-1;
    Assert.assertEquals(" LDY X", code.get(++n));
    Assert.assertEquals(" STY @ERG", code.get(++n));
    Assert.assertEquals(" LDY #<1", code.get(++n));
    Assert.assertEquals("; Bedingung (a==b)", code.get(++n));
    Assert.assertEquals(" CPY @ERG", code.get(++n));

    // TODO: Ersetzen durch JEQ ?THEN1
    Assert.assertEquals(" BNE ?FA1", code.get(++n));
    Assert.assertEquals(" JMP ?THEN1", code.get(++n));
    Assert.assertEquals("?FA1", code.get(++n));

    Assert.assertEquals(" LDY X", code.get(++n));
    Assert.assertEquals(" STY @ERG", code.get(++n));
    Assert.assertEquals(" LDY #<2", code.get(++n));
    Assert.assertEquals("; Bedingung (a==b)", code.get(++n));
    Assert.assertEquals(" CPY @ERG", code.get(++n));

    // TODO: Ersetzen durch "JNE ?ELSE1"
    Assert.assertEquals(" BNE ?FA2", code.get(++n));
    Assert.assertEquals(" JMP ?THEN1", code.get(++n));
    Assert.assertEquals("?FA2", code.get(++n));
    Assert.assertEquals(" JMP ?ELSE1", code.get(++n));
    
    Assert.assertEquals("?THEN1", code.get(++n));
    Assert.assertEquals(";", code.get(++n));
    Assert.assertEquals("; [1]  if x==1 OR x==2 then a:=2 else a:=3", code.get(++n));
    Assert.assertEquals(";", code.get(++n));
    Assert.assertEquals(" LDY #<2", code.get(++n));
    Assert.assertEquals(" STY A__", code.get(++n));
    Assert.assertEquals(" JMP ?ENDIF1", code.get(++n));
    Assert.assertEquals("?ELSE1", code.get(++n));
    Assert.assertEquals(";", code.get(++n));
    Assert.assertEquals("; [1]  if x==1 OR x==2 then a:=2 else a:=3", code.get(++n));
    Assert.assertEquals(";", code.get(++n));
    Assert.assertEquals(" LDY #<3", code.get(++n));
    Assert.assertEquals(" STY A__", code.get(++n));
    Assert.assertEquals("?ENDIF1", code.get(++n));

    Assert.assertEquals(30, n);
  }

}
