// cdw by 'The Atari Team' 2020
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler.statement;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import lla.privat.atarixl.compiler.Symbol;
import lla.privat.atarixl.compiler.SymbolEnum;
import lla.privat.atarixl.compiler.expression.Type;
import lla.privat.atarixl.compiler.source.Source;

public class TestFor {
  @Test
  public void testForBYTEToDo() {
    Source source = new Source("for i:=0 to 10 do begin end").setVerboseLevel(2);
    source.addVariable("I", Type.BYTE);
    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new For(source).statement(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    List<String> code = source.getCode();

    int n=-1;
    Assert.assertEquals("; (5)", code.get(++n));
    Assert.assertEquals(" LDY #<0", code.get(++n));
    Assert.assertEquals(" STY I", code.get(++n));

    Assert.assertEquals("; (5)", code.get(++n));
    Assert.assertEquals(" LDY #<10", code.get(++n));
    Assert.assertEquals(" STY ?FOR1", code.get(++n));

    Assert.assertEquals(" LDY ?FOR1", code.get(++n)); // check ob nicht schon am Ende
    Assert.assertEquals(" CPY I", code.get(++n));
    Assert.assertEquals(" BCS ?GO1", code.get(++n));
    Assert.assertEquals(" JMP ?EXIT1", code.get(++n));
    Assert.assertEquals("?GO1", code.get(++n));
    // statement in for loop

    Assert.assertEquals(" LDA I", code.get(++n));     // check end
    Assert.assertEquals(" CMP ?FOR1", code.get(++n));
    Assert.assertEquals(" BCS ?EXIT1", code.get(++n));
//    Assert.assertEquals("?NEXT1", code.get(++n)); // typ=word only!
    Assert.assertEquals(" INC I", code.get(++n));
    Assert.assertEquals(" JMP ?GO1", code.get(++n));
    Assert.assertEquals("?EXIT1", code.get(++n));
  }

  @Test
  public void testForWORDToDo() {
    Source source = new Source("for i:=0 to 10 do begin end").setVerboseLevel(2);
    source.addVariable("I", Type.WORD);
    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new For(source).statement(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    List<String> code = source.getCode();

    int n=-1;
    Assert.assertEquals("; (5)", code.get(++n));
    Assert.assertEquals(" LDY #<0", code.get(++n));
    Assert.assertEquals(" STY I", code.get(++n));
    Assert.assertEquals(" LDX #0", code.get(++n));
    Assert.assertEquals(" STX I+1", code.get(++n));

    Assert.assertEquals("; (5)", code.get(++n));
    Assert.assertEquals(" LDY #<10", code.get(++n));
    Assert.assertEquals(" STY ?FOR1", code.get(++n));
    Assert.assertEquals(" LDX #0", code.get(++n));
    Assert.assertEquals(" STX ?FOR1+1", code.get(++n));

    Assert.assertEquals(" LDY ?FOR1", code.get(++n)); // check ob nicht schon am Ende
    Assert.assertEquals(" CPY I", code.get(++n));
    Assert.assertEquals(" LDA ?FOR1+1", code.get(++n));
    Assert.assertEquals(" SBC I+1", code.get(++n));
    Assert.assertEquals(" BCS ?GO1", code.get(++n));
    Assert.assertEquals(" JMP ?EXIT1", code.get(++n));
    Assert.assertEquals("?GO1", code.get(++n));
    // statement in for loop

    Assert.assertEquals(" LDA I+1", code.get(++n));     // check end
    Assert.assertEquals(" CMP ?FOR1+1", code.get(++n));
    Assert.assertEquals(" BNE ?NEXT1", code.get(++n));
    Assert.assertEquals(" LDA I", code.get(++n));
    Assert.assertEquals(" CMP ?FOR1", code.get(++n));
    Assert.assertEquals(" BCS ?EXIT1", code.get(++n));
    Assert.assertEquals("?NEXT1", code.get(++n)); // typ=word only!
    Assert.assertEquals(" INC I", code.get(++n));
    Assert.assertEquals(" BNE ?LOOP1", code.get(++n));
    Assert.assertEquals(" INC I+1", code.get(++n));
    Assert.assertEquals("?LOOP1", code.get(++n));
    Assert.assertEquals(" JMP ?GO1", code.get(++n));
    Assert.assertEquals("?EXIT1", code.get(++n));
  }

  @Test
  public void testForBYTEDownToDo() {
    Source source = new Source("for i:=10 downto 0 do begin end").setVerboseLevel(2);
    source.addVariable("I", Type.BYTE);

    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new For(source).statement(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());
    List<String> code = source.getCode();

    int n=-1;
    Assert.assertEquals("; (5)", code.get(++n));
    Assert.assertEquals(" LDY #<10", code.get(++n));
    Assert.assertEquals(" STY I", code.get(++n));
    Assert.assertEquals("; (5)", code.get(++n));
    Assert.assertEquals(" LDY #<0", code.get(++n));
    Assert.assertEquals(" STY ?FOR1", code.get(++n));
    Assert.assertEquals(" LDY I", code.get(++n));
    Assert.assertEquals(" CPY ?FOR1", code.get(++n));
    Assert.assertEquals(" BCS ?GO1", code.get(++n));
    Assert.assertEquals(" JMP ?EXIT1", code.get(++n));
    Assert.assertEquals("?GO1", code.get(++n));
    Assert.assertEquals(" LDA ?FOR1", code.get(++n));
    Assert.assertEquals(" CMP I", code.get(++n));
    Assert.assertEquals(" BCS ?EXIT1", code.get(++n));
    Assert.assertEquals(" DEC I", code.get(++n));
    Assert.assertEquals(" JMP ?GO1", code.get(++n));
    Assert.assertEquals("?EXIT1", code.get(++n));
  }

  @Test
  public void testForWORDDownToDo() {
    Source source = new Source("for i:=10 downto 0 do begin end").setVerboseLevel(2);
    source.addVariable("I", Type.WORD);

    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new For(source).statement(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());
    List<String> code = source.getCode();

    int n=-1;
    Assert.assertEquals("; (5)", code.get(++n));
    Assert.assertEquals(" LDY #<10", code.get(++n));
    Assert.assertEquals(" STY I", code.get(++n));
    Assert.assertEquals(" LDX #0", code.get(++n));
    Assert.assertEquals(" STX I+1", code.get(++n));

    Assert.assertEquals("; (5)", code.get(++n));
    Assert.assertEquals(" LDY #<0", code.get(++n));
    Assert.assertEquals(" STY ?FOR1", code.get(++n));
    Assert.assertEquals(" LDX #0", code.get(++n));
    Assert.assertEquals(" STX ?FOR1+1", code.get(++n));

    Assert.assertEquals(" LDY I", code.get(++n));
    Assert.assertEquals(" CPY ?FOR1", code.get(++n));
    Assert.assertEquals(" LDA I+1", code.get(++n));
    Assert.assertEquals(" SBC ?FOR1+1", code.get(++n));
    Assert.assertEquals(" BCS ?GO1", code.get(++n));
    Assert.assertEquals(" JMP ?EXIT1", code.get(++n));
    Assert.assertEquals("?GO1", code.get(++n));

    Assert.assertEquals(" LDA I+1", code.get(++n));
    Assert.assertEquals(" CMP ?FOR1+1", code.get(++n));
    Assert.assertEquals(" BNE ?NEXT1", code.get(++n));
    Assert.assertEquals(" LDA ?FOR1", code.get(++n));
    Assert.assertEquals(" CMP I", code.get(++n));
    Assert.assertEquals(" BCS ?EXIT1", code.get(++n));

    Assert.assertEquals("?NEXT1", code.get(++n));
    Assert.assertEquals(" LDA I", code.get(++n));
    Assert.assertEquals(" BNE ?LOOP1", code.get(++n));
    Assert.assertEquals(" DEC I+1", code.get(++n));
    Assert.assertEquals("?LOOP1", code.get(++n));
    Assert.assertEquals(" DEC I", code.get(++n));
    Assert.assertEquals(" JMP ?GO1", code.get(++n));
    Assert.assertEquals("?EXIT1", code.get(++n));
  }


  @Test(expected = IllegalStateException.class)
  public void testForNotDefinedBYTEToDo() {
    Source source = new Source("for notdefined:=0 to 10 do begin end").setVerboseLevel(2);
    Symbol symbol = source.nextElement();

    /* Symbol nextSymbol = */ new For(source).statement(symbol).build();
  }
}
