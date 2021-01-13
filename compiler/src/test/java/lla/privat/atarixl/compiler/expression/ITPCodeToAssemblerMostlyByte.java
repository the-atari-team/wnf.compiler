// cdw by 'The Atari Team' 2020
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler.expression;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import lla.privat.atarixl.compiler.Symbol;
import lla.privat.atarixl.compiler.SymbolEnum;
import lla.privat.atarixl.compiler.source.Source;

public class ITPCodeToAssemblerMostlyByte {
  
  private List<Integer> getPCodeOf(Source source) {
    source.setVerboseLevel(2);
    
    Expression expression = new Expression(source);

    Symbol symbol = source.nextElement();
    symbol = expression.expression(symbol).getLastSymbol();
    
    Assert.assertEquals(SymbolEnum.noSymbol, symbol.getId());

    expression.optimisation();
    System.out.println();
    System.out.println(expression.joinedPCode());

    return expression.getPCode();
  }
  
  @Test
  public void testExpression() {
    Source source = new Source("123 ");
    List<Integer> p_code = getPCodeOf(source);
    
    Type ergebnis = Type.BYTE;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);
    
    pcodeGenerator.build();
    List<String> code = source.getCode();
    
    int n=-1;
    Assert.assertEquals("; (5)", code.get(++n));
    Assert.assertEquals(" LDY #<123", code.get(++n));
  }

  @Test
  public void testExpressionAdd() {
    Source source = new Source("123 + 124 ");
    List<Integer> p_code = getPCodeOf(source);
    
    Type ergebnis = Type.BYTE;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);
    
    pcodeGenerator.build();
    List<String> code = source.getCode();
    
    int n=-1;
    Assert.assertEquals("; (3)", code.get(++n));

    Assert.assertEquals(" CLC", code.get(++n));
    Assert.assertEquals(" LDA #<123", code.get(++n));
    Assert.assertEquals(" ADC #<124", code.get(++n));
    Assert.assertEquals(" TAY", code.get(++n));    
  }

  @Test
  public void testExpressionAddZahlX() {
    Source source = new Source("123 + X ");
    source.addVariable("X", Type.BYTE);
    List<Integer> p_code = getPCodeOf(source);
    
    Type ergebnis = Type.BYTE;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);
    
    pcodeGenerator.build();
    List<String> code = source.getCode();
    
    int n=-1;
    Assert.assertEquals("; (3)", code.get(++n));
    Assert.assertEquals(" CLC", code.get(++n));
    Assert.assertEquals(" LDA #<123", code.get(++n));
    Assert.assertEquals(" ADC X", code.get(++n));
    Assert.assertEquals(" TAY", code.get(++n));    
  }

  @Test
  public void testExpressionSubZahl() {
    Source source = new Source("$ff - 16 ");
    List<Integer> p_code = getPCodeOf(source);

    System.out.println(Expression.joinedPCode(p_code));
    Type ergebnis = Type.BYTE;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);
    
    pcodeGenerator.build();
    List<String> code = source.getCode();
    
    int n=-1;
    Assert.assertEquals("; (3)", code.get(++n));
    
    Assert.assertEquals(" SEC", code.get(++n));
    Assert.assertEquals(" LDA #<255", code.get(++n));
    Assert.assertEquals(" SBC #<16", code.get(++n));
    Assert.assertEquals(" TAY", code.get(++n));    
  }

  @Test
  public void testExpressionDivXZahl2erKomplement() {
    Source source = new Source("X / 2 ");
    source.addVariable("X", Type.BYTE);
    List<Integer> p_code = getPCodeOf(source);
    
    Type ergebnis = Type.BYTE;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);
    
    pcodeGenerator.build();
    List<String> code = source.getCode();
    
    int n=-1;
    Assert.assertEquals("; (4)", code.get(++n));
    Assert.assertEquals(" LDY X", code.get(++n));
    Assert.assertEquals(" TYA", code.get(++n));
    Assert.assertEquals(" LSR A", code.get(++n));
    Assert.assertEquals(" TAY", code.get(++n));
  }

  @Test
  public void testExpressionDivXZahl() {
    Source source = new Source("X / 3 ");
    source.addVariable("X", Type.BYTE);
    List<Integer> p_code = getPCodeOf(source);
    
    Type ergebnis = Type.BYTE;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);
    
    pcodeGenerator.build();
    List<String> code = source.getCode();
    
    int n=-1;
    Assert.assertEquals("; (4)", code.get(++n));
    Assert.assertEquals(" LDY X", code.get(++n));
    Assert.assertEquals(" LDA #<3", code.get(++n));
    Assert.assertEquals(" STA @OP", code.get(++n));
    Assert.assertEquals(" LDX #0", code.get(++n));
    Assert.assertEquals(" STX @OP+1", code.get(++n));
    Assert.assertEquals(" JSR @IDIV", code.get(++n));
  }

  @Test
  public void testExpressionModXZahl() {
    Source source = new Source("X mod 3 ");
    source.addVariable("X", Type.BYTE);
    List<Integer> p_code = getPCodeOf(source);
    
    Type ergebnis = Type.BYTE;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);
    
    pcodeGenerator.build();
    List<String> code = source.getCode();
    
    int n=-1;
    Assert.assertEquals("; (4)", code.get(++n));
    Assert.assertEquals(" LDY X", code.get(++n));
    Assert.assertEquals(" LDA #<3", code.get(++n));
    Assert.assertEquals(" STA @OP", code.get(++n));
    Assert.assertEquals(" LDX #0", code.get(++n));
    Assert.assertEquals(" STX @OP+1", code.get(++n));
    Assert.assertEquals(" JSR @IMOD", code.get(++n));
  }

}
