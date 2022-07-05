// cdw by 'The Atari Team' 2022
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
  public void testExpressionResultByte() {
    Source source = new Source("123 ");
    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.BYTE;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();

    int n=-1;
    Assert.assertEquals("; (5)", code.get(++n));
    Assert.assertEquals(" LDY #<123", code.get(++n));
    Assert.assertEquals(2, code.size());
  }

  @Test
  public void testExpressionResultInt8() {
    Source source = new Source("123 ");
    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.INT8;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();

    int n=-1;
    Assert.assertEquals("; (5)", code.get(++n));
    Assert.assertEquals(" LDY #<123", code.get(++n));
    Assert.assertEquals(2, code.size());
  }

  @Test
  public void testExpressionResultByteNegativeOne() {
    Source source = new Source("-1 ");
    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.BYTE;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();

    int n=-1;
    Assert.assertEquals("; (5)", code.get(++n));
    Assert.assertEquals(" LDY #<-1", code.get(++n));
    Assert.assertEquals(2, code.size());
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
    source.setStarChainMult(true);

    source.addVariable("X", Type.BYTE);
    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.BYTE;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();

    int n=-1;
    Assert.assertEquals("; (4)", code.get(++n));
    Assert.assertEquals(" LDY X", code.get(++n)); // this is right!
    Assert.assertEquals(" TYA", code.get(++n));   // the peephole optimizer will switch this to LDA X!
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

  @Test
  public void testExpressionModXZahlInt8() {
    Source source = new Source("X mod 3 ");
    source.addVariable("X", Type.BYTE);
    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();

    int n=-1;
    Assert.assertEquals("; (4)", code.get(++n));
    Assert.assertEquals(" LDY X", code.get(++n));
    Assert.assertEquals(" LDX #0", code.get(++n));
    Assert.assertEquals(" LDA #<3", code.get(++n));
    Assert.assertEquals(" STA @OP", code.get(++n));
    Assert.assertEquals(" LDA #>3", code.get(++n));
    Assert.assertEquals(" STA @OP+1", code.get(++n));

    Assert.assertEquals(" JSR @IMOD", code.get(++n));
    Assert.assertEquals(8, code.size());
  }

  @Test
  public void testExpressionXMulY() {
    Source source = new Source("X * Y ");
    source.addVariable("X", Type.BYTE);
    source.addVariable("Y", Type.BYTE);
    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();

    int n=-1;
    Assert.assertEquals("; (4)", code.get(++n));
    Assert.assertEquals(" LDY X", code.get(++n));
    Assert.assertEquals(" LDX #0", code.get(++n));
    Assert.assertEquals(" LDA Y", code.get(++n));
    Assert.assertEquals(" STA @OP", code.get(++n));
    Assert.assertEquals(" LDA #0", code.get(++n));
    Assert.assertEquals(" STA @OP+1", code.get(++n));
    Assert.assertEquals(" JSR @IMULT", code.get(++n));
    Assert.assertEquals(8, code.size());
  }

  @Test
  public void testExpressionByteXMulInt8Y() {
    Source source = new Source("X * Y ");
    source.addVariable("X", Type.BYTE);
    source.addVariable("Y", Type.INT8);
    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();

    int n=-1;
    Assert.assertEquals("; (4)", code.get(++n));
    Assert.assertEquals(" LDY X", code.get(++n));
    Assert.assertEquals(" LDX #0", code.get(++n));
    Assert.assertEquals(" LDA Y", code.get(++n));
    Assert.assertEquals(" STA @OP", code.get(++n));
    Assert.assertEquals(" CMP #$80", code.get(++n));
    Assert.assertEquals(" LDA #0", code.get(++n));
    Assert.assertEquals(" BCC *+4", code.get(++n));
    Assert.assertEquals(" LDA #$FF", code.get(++n));
    Assert.assertEquals(" STA @OP+1", code.get(++n));
    Assert.assertEquals(" JSR @IMULT", code.get(++n));
    Assert.assertEquals(11, code.size());
  }

  @Test
  public void testExpressionInt8XMulByteY() {
    Source source = new Source("X * Y ");
    source.addVariable("X", Type.INT8);
    source.addVariable("Y", Type.BYTE);
    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();

    int n=-1;
    Assert.assertEquals("; (4)", code.get(++n));
    Assert.assertEquals(" LDY X", code.get(++n));
    Assert.assertEquals(" CPY #$80", code.get(++n));
    Assert.assertEquals(" LDX #0", code.get(++n));
    Assert.assertEquals(" BCC *+4", code.get(++n));
    Assert.assertEquals(" LDX #$FF", code.get(++n));
    Assert.assertEquals(" LDA Y", code.get(++n));
    Assert.assertEquals(" STA @OP", code.get(++n));
    Assert.assertEquals(" LDA #0", code.get(++n));
    Assert.assertEquals(" STA @OP+1", code.get(++n));
    Assert.assertEquals(" JSR @IMULT", code.get(++n));
    Assert.assertEquals(11, code.size());
  }

  @Test
  public void testExpressionInt8XMulInt8Y() {
    Source source = new Source("X * Y ");
    source.addVariable("X", Type.INT8);
    source.addVariable("Y", Type.INT8);
    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();

    int n=-1;
    Assert.assertEquals("; (4)", code.get(++n));
    Assert.assertEquals(" LDY X", code.get(++n));
    Assert.assertEquals(" CPY #$80", code.get(++n));
    Assert.assertEquals(" LDX #0", code.get(++n));
    Assert.assertEquals(" BCC *+4", code.get(++n));
    Assert.assertEquals(" LDX #$FF", code.get(++n));
    Assert.assertEquals(" LDA Y", code.get(++n));
    Assert.assertEquals(" STA @OP", code.get(++n));
    Assert.assertEquals(" CMP #$80", code.get(++n));
    Assert.assertEquals(" LDA #0", code.get(++n));
    Assert.assertEquals(" BCC *+4", code.get(++n));
    Assert.assertEquals(" LDA #$FF", code.get(++n));
    Assert.assertEquals(" STA @OP+1", code.get(++n));
    Assert.assertEquals(" JSR @IMULT", code.get(++n));
    Assert.assertEquals(14, code.size());
  }


  @Test
  public void testExpressionXMul2ExponentByte() {
    Source source = new Source("X * 16 ");
    source.setStarChainMult(true);

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
    Assert.assertEquals(" ASL A", code.get(++n));
    Assert.assertEquals(" ASL A", code.get(++n));
    Assert.assertEquals(" ASL A", code.get(++n));
    Assert.assertEquals(" ASL A", code.get(++n));
    Assert.assertEquals(" TAY", code.get(++n));

    Assert.assertEquals(8, code.size());
  }

  @Test
  public void testExpressionXEorY() {
    Source source = new Source("X xor Y ");
    source.addVariable("X", Type.BYTE);
    source.addVariable("Y", Type.BYTE);
    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.BYTE;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();

    int n=-1;
    Assert.assertEquals("; (3)", code.get(++n));
    Assert.assertEquals(" LDA X", code.get(++n));
    Assert.assertEquals(" EOR Y", code.get(++n));
    Assert.assertEquals(" TAY", code.get(++n));

    Assert.assertEquals(4, code.size());
  }

  @Test
  public void testExpressionXDiv2ExponentByte() {
    Source source = new Source("X / 32 ");
    source.setStarChainMult(true);

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
    Assert.assertEquals(" LSR A", code.get(++n));
    Assert.assertEquals(" LSR A", code.get(++n));
    Assert.assertEquals(" LSR A", code.get(++n));
    Assert.assertEquals(" LSR A", code.get(++n));
    Assert.assertEquals(" TAY", code.get(++n));

    Assert.assertEquals(9, code.size());
  }

}
