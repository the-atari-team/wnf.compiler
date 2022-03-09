// cdw by 'The Atari Team' 2021
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

    Assert.assertEquals(2, source.getVariable("I").getWrites());
    
    List<String> code = source.getCode();

    int n=-1;
    Assert.assertEquals("; (5)", code.get(++n));
    Assert.assertEquals(" LDY #<0", code.get(++n));
    Assert.assertEquals(" STY I", code.get(++n));

    Assert.assertEquals("; (5)", code.get(++n));
    Assert.assertEquals(" LDY #<10", code.get(++n));
    Assert.assertEquals(" STY ?FOR1", code.get(++n));

    Assert.assertEquals("?FORLOOP1", code.get(++n));
    
    Assert.assertEquals(" LDY ?FOR1", code.get(++n)); // check ob nicht schon am Ende
    Assert.assertEquals(" CPY I", code.get(++n));
    Assert.assertEquals(" BCS ?GO1", code.get(++n));
    Assert.assertEquals(" JMP ?EXIT1", code.get(++n));
    Assert.assertEquals("?GO1", code.get(++n));
    // statement in for loop

//    Assert.assertEquals(" LDA I", code.get(++n));     // check end
//    Assert.assertEquals(" CMP ?FOR1", code.get(++n));
//    Assert.assertEquals(" BCS ?EXIT1", code.get(++n));
//    Assert.assertEquals("?NEXT1", code.get(++n)); // typ=word only!
    Assert.assertEquals(" INC I", code.get(++n));
    Assert.assertEquals(" JMP ?FORLOOP1", code.get(++n));
    Assert.assertEquals("?EXIT1", code.get(++n));
  }

  @Test
  public void testForINT8ToDo() {
    Source source = new Source("for i:=0 to 10 do begin end").setVerboseLevel(2);
    source.addVariable("I", Type.INT8);
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

    Assert.assertEquals("?FORLOOP1", code.get(++n));

    Assert.assertEquals(" LDA ?FOR1", code.get(++n)); // check ob nicht schon am Ende
    Assert.assertEquals(" SEC", code.get(++n));
    Assert.assertEquals(" SBC I", code.get(++n));
    Assert.assertEquals(" BVC *+4", code.get(++n));
    Assert.assertEquals(" EOR #$80", code.get(++n));
    Assert.assertEquals(" BPL ?GO1", code.get(++n));
    Assert.assertEquals(" JMP ?EXIT1", code.get(++n));
    Assert.assertEquals("?GO1", code.get(++n));
    // statement in for loop

//    Assert.assertEquals(" LDA I", code.get(++n));     // check end
//    Assert.assertEquals(" CMP ?FOR1", code.get(++n));
//    Assert.assertEquals(" BCS ?EXIT1", code.get(++n));
//    Assert.assertEquals("?NEXT1", code.get(++n)); // typ=word only!
    Assert.assertEquals(" INC I", code.get(++n));
    Assert.assertEquals(" JMP ?FORLOOP1", code.get(++n));
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
    Assert.assertEquals(" LDX #>0", code.get(++n));
    Assert.assertEquals(" STY I", code.get(++n));

//    Assert.assertEquals("; assignment", source.getCode().get(++n));
//    Assert.assertEquals(" CPY #$80", source.getCode().get(++n));
//    Assert.assertEquals(" LDX #0", source.getCode().get(++n));
//    Assert.assertEquals(" BCC *+4", source.getCode().get(++n));
//    Assert.assertEquals(" LDX #$FF", source.getCode().get(++n));

    Assert.assertEquals(" STX I+1", code.get(++n));

    Assert.assertEquals("; (5)", code.get(++n));
    Assert.assertEquals(" LDY #<10", code.get(++n));
    Assert.assertEquals(" LDX #>10", code.get(++n));
    Assert.assertEquals(" STY ?FOR1", code.get(++n));

//    Assert.assertEquals("; for", source.getCode().get(++n));
//    Assert.assertEquals(" CPY #$80", source.getCode().get(++n));
//    Assert.assertEquals(" LDX #0", source.getCode().get(++n));
//    Assert.assertEquals(" BCC *+4", source.getCode().get(++n));
//    Assert.assertEquals(" LDX #$FF", source.getCode().get(++n));

    Assert.assertEquals(" STX ?FOR1+1", code.get(++n));

    Assert.assertEquals("?FORLOOP1", code.get(++n));

    Assert.assertEquals(" LDA ?FOR1", code.get(++n)); // check ob nicht schon am Ende
    Assert.assertEquals(" CMP I", code.get(++n));
    Assert.assertEquals(" LDA ?FOR1+1", code.get(++n));
    Assert.assertEquals(" SBC I+1", code.get(++n));
    Assert.assertEquals(" BVC *+4", code.get(++n));
    Assert.assertEquals(" EOR #$80", code.get(++n));
    Assert.assertEquals(" BPL ?GO1", code.get(++n));
    Assert.assertEquals(" JMP ?EXIT1", code.get(++n));
    Assert.assertEquals("?GO1", code.get(++n));
    // statement in for loop

//    Assert.assertEquals(" LDA I+1", code.get(++n));     // check end
//    Assert.assertEquals(" CMP ?FOR1+1", code.get(++n));
//    Assert.assertEquals(" BNE ?NEXT1", code.get(++n));
//    Assert.assertEquals(" LDA I", code.get(++n));
//    Assert.assertEquals(" CMP ?FOR1", code.get(++n));
//    Assert.assertEquals(" BCS ?EXIT1", code.get(++n));
//    Assert.assertEquals("?NEXT1", code.get(++n)); // typ=word only!
    Assert.assertEquals(" INC I", code.get(++n));
    Assert.assertEquals(" BNE ?LOOP1", code.get(++n));
    Assert.assertEquals(" INC I+1", code.get(++n));
    Assert.assertEquals("?LOOP1", code.get(++n));
    Assert.assertEquals(" JMP ?FORLOOP1", code.get(++n));
    Assert.assertEquals("?EXIT1", code.get(++n));
  }

  @Test
  public void testForUINT16ToDo() {
    Source source = new Source("for i:=65530 to 65534 do begin end").setVerboseLevel(2);
    source.addVariable("I", Type.UINT16);
    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new For(source).statement(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    List<String> code = source.getCode();

    int n=-1;
    Assert.assertEquals("; (5)", code.get(++n));
    Assert.assertEquals(" LDY #<65530", code.get(++n));
    Assert.assertEquals(" LDX #>65530", code.get(++n));
    Assert.assertEquals(" STY I", code.get(++n));

    Assert.assertEquals(" STX I+1", code.get(++n));

    Assert.assertEquals("; (5)", code.get(++n));
    Assert.assertEquals(" LDY #<65534", code.get(++n));
    Assert.assertEquals(" LDX #>65534", code.get(++n));
    Assert.assertEquals(" STY ?FOR1", code.get(++n));

    Assert.assertEquals(" STX ?FOR1+1", code.get(++n));

    Assert.assertEquals("?FORLOOP1", code.get(++n));

    Assert.assertEquals(" LDY ?FOR1", code.get(++n)); // check ob nicht schon am Ende
    Assert.assertEquals(" CPY I", code.get(++n));
    Assert.assertEquals(" LDA ?FOR1+1", code.get(++n));
    Assert.assertEquals(" SBC I+1", code.get(++n));
    Assert.assertEquals(" BCS ?GO1", code.get(++n));
    Assert.assertEquals(" JMP ?EXIT1", code.get(++n));
    Assert.assertEquals("?GO1", code.get(++n));

    // statement in for loop

    Assert.assertEquals(" INC I", code.get(++n));
    Assert.assertEquals(" BNE ?LOOP1", code.get(++n));
    Assert.assertEquals(" INC I+1", code.get(++n));
    Assert.assertEquals("?LOOP1", code.get(++n));
    Assert.assertEquals(" JMP ?FORLOOP1", code.get(++n));
    Assert.assertEquals("?EXIT1", code.get(++n));
  }

  @Test
  public void testForINT8DownToDo() {
    Source source = new Source("for i:=10 downto 0 do begin end").setVerboseLevel(2);
    source.addVariable("I", Type.INT8);

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

    Assert.assertEquals("?FORLOOP1", code.get(++n));

    Assert.assertEquals(" LDA I", code.get(++n)); // check ob nicht schon am Ende
    Assert.assertEquals(" SEC", code.get(++n));
    Assert.assertEquals(" SBC ?FOR1", code.get(++n));
    Assert.assertEquals(" BVC *+4", code.get(++n));
    Assert.assertEquals(" EOR #$80", code.get(++n));
    Assert.assertEquals(" BPL ?GO1", code.get(++n));

    Assert.assertEquals(" JMP ?EXIT1", code.get(++n));
    Assert.assertEquals("?GO1", code.get(++n));
//    Assert.assertEquals(" LDA ?FOR1", code.get(++n));
//    Assert.assertEquals(" CMP I", code.get(++n));
//    Assert.assertEquals(" BCS ?EXIT1", code.get(++n));
    Assert.assertEquals(" DEC I", code.get(++n));
    Assert.assertEquals(" JMP ?FORLOOP1", code.get(++n));
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

    Assert.assertEquals("?FORLOOP1", code.get(++n));

    Assert.assertEquals(" LDY I", code.get(++n)); // check ob nicht schon am Ende
    Assert.assertEquals(" CPY ?FOR1", code.get(++n));
    Assert.assertEquals(" BCS ?GO1", code.get(++n));

    Assert.assertEquals(" JMP ?EXIT1", code.get(++n));
    Assert.assertEquals("?GO1", code.get(++n));
//    Assert.assertEquals(" LDA ?FOR1", code.get(++n));
//    Assert.assertEquals(" CMP I", code.get(++n));
//    Assert.assertEquals(" BCS ?EXIT1", code.get(++n));
    Assert.assertEquals(" DEC I", code.get(++n));
    Assert.assertEquals(" JMP ?FORLOOP1", code.get(++n));
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
    Assert.assertEquals(" LDX #>10", code.get(++n));
    Assert.assertEquals(" STY I", code.get(++n));

//    Assert.assertEquals("; assignment", source.getCode().get(++n));
//    Assert.assertEquals(" CPY #$80", source.getCode().get(++n));
//    Assert.assertEquals(" LDX #0", source.getCode().get(++n));
//    Assert.assertEquals(" BCC *+4", source.getCode().get(++n));
//    Assert.assertEquals(" LDX #$FF", source.getCode().get(++n));

    Assert.assertEquals(" STX I+1", code.get(++n));

    Assert.assertEquals("; (5)", code.get(++n));
    Assert.assertEquals(" LDY #<0", code.get(++n));
    Assert.assertEquals(" LDX #>0", code.get(++n));
    Assert.assertEquals(" STY ?FOR1", code.get(++n));

//    Assert.assertEquals("; for", source.getCode().get(++n));
//    Assert.assertEquals(" CPY #$80", source.getCode().get(++n));
//    Assert.assertEquals(" LDX #0", source.getCode().get(++n));
//    Assert.assertEquals(" BCC *+4", source.getCode().get(++n));
//    Assert.assertEquals(" LDX #$FF", source.getCode().get(++n));

    Assert.assertEquals(" STX ?FOR1+1", code.get(++n));

    Assert.assertEquals("?FORLOOP1", code.get(++n));

    Assert.assertEquals(" LDA I", code.get(++n)); // check ob nicht schon am Ende
    Assert.assertEquals(" CMP ?FOR1", code.get(++n));
    Assert.assertEquals(" LDA I+1", code.get(++n));
    Assert.assertEquals(" SBC ?FOR1+1", code.get(++n));
    Assert.assertEquals(" BVC *+4", code.get(++n));
    Assert.assertEquals(" EOR #$80", code.get(++n));
    Assert.assertEquals(" BPL ?GO1", code.get(++n));

    Assert.assertEquals(" JMP ?EXIT1", code.get(++n));
    Assert.assertEquals("?GO1", code.get(++n));

//    Assert.assertEquals(" LDA I+1", code.get(++n));
//    Assert.assertEquals(" CMP ?FOR1+1", code.get(++n));
//    Assert.assertEquals(" BNE ?NEXT1", code.get(++n));
//    Assert.assertEquals(" LDA ?FOR1", code.get(++n));
//    Assert.assertEquals(" CMP I", code.get(++n));
//    Assert.assertEquals(" BCS ?EXIT1", code.get(++n));
//
//    Assert.assertEquals("?NEXT1", code.get(++n));
    Assert.assertEquals(" LDA I", code.get(++n));
    Assert.assertEquals(" BNE ?LOOP1", code.get(++n));
    Assert.assertEquals(" DEC I+1", code.get(++n));
    Assert.assertEquals("?LOOP1", code.get(++n));
    Assert.assertEquals(" DEC I", code.get(++n));
    Assert.assertEquals(" JMP ?FORLOOP1", code.get(++n));
    Assert.assertEquals("?EXIT1", code.get(++n));
  }


  @Test(expected = IllegalStateException.class)
  public void testForNotDefinedBYTEToDo() {
    Source source = new Source("for notdefined:=0 to 10 do begin end").setVerboseLevel(2);
    Symbol symbol = source.nextElement();

    /* Symbol nextSymbol = */ new For(source).statement(symbol).build();
  }

  @Test
  public void testForBYTEToDoWithBreak() {
    Source source = new Source("for i:=0 to 10 do begin break end").setVerboseLevel(2);
    source.addVariable("I", Type.BYTE);
    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new For(source).statement(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());
  }

  @Test
  public void testForBYTEToDoForBYTETODoWithBreak() {
    Source source = new Source("for i:=0 to 10 do begin for j:=0 to 10 do begin break end break end").setVerboseLevel(2);
    source.addVariable("I", Type.BYTE);
    source.addVariable("J", Type.BYTE);
    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new For(source).statement(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());
  }

  @Test
  public void testForBYTEToStep2Do() {
    Source source = new Source("for i:=0 to 10 step 2 do begin end").setVerboseLevel(2);
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
    Assert.assertEquals(" LDY #<2", code.get(++n));
    Assert.assertEquals(" STY ?FORSTEP1", code.get(++n));
    
    Assert.assertEquals("; (5)", code.get(++n));
    Assert.assertEquals(" LDY #<10", code.get(++n));
    Assert.assertEquals(" STY ?FOR1", code.get(++n));


    Assert.assertEquals("?FORLOOP_AFTERSTEP1", code.get(++n));
    
    Assert.assertEquals(" LDY ?FOR1", code.get(++n)); // check ob nicht schon am Ende
    Assert.assertEquals(" CPY I", code.get(++n));
    Assert.assertEquals(" BCS ?GO1", code.get(++n));
    Assert.assertEquals(" JMP ?EXIT1", code.get(++n));
    Assert.assertEquals("?GO1", code.get(++n));

    // statement in for loop

//    Assert.assertEquals(" LDA I", code.get(++n));     // check end
//    Assert.assertEquals(" CMP ?FOR1", code.get(++n));
//    Assert.assertEquals(" BCS ?EXIT1", code.get(++n));
//    Assert.assertEquals("?NEXT1", code.get(++n)); // typ=word only!
//    Assert.assertEquals(" INC I", code.get(++n));

    Assert.assertEquals(" CLC", code.get(++n));
    Assert.assertEquals(" LDA I", code.get(++n));
    Assert.assertEquals(" ADC ?FORSTEP1", code.get(++n));
    Assert.assertEquals(" STA I", code.get(++n));
    
    Assert.assertEquals(" JMP ?FORLOOP_AFTERSTEP1", code.get(++n));
    Assert.assertEquals("?EXIT1", code.get(++n));
  }

}
