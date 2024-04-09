// cdw by 'The Atari Team' 2022
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler.expression;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import lla.privat.atarixl.compiler.Symbol;
import lla.privat.atarixl.compiler.SymbolEnum;
import lla.privat.atarixl.compiler.source.Source;

public class ITPCodeToAssemblerMostlyWord {

  private List<Integer> getPCodeOf(Source source) {
    if (source.getVerboseLevel() < 2) {
      source.setVerboseLevel(2);
    }
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
  public void testExpressionResultWord() {
    Source source = new Source("128 ");
    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();

    int n=-1;
    Assert.assertEquals("; (5)", code.get(++n));
    Assert.assertEquals(" LDY #<128", code.get(++n));
    Assert.assertEquals(" LDX #>128", code.get(++n));
    Assert.assertEquals(3, code.size());
  }

  @Test
  public void testExpressionResultUint16() {
    Source source = new Source("1 ");
    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.UINT16;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();

    int n=-1;
    Assert.assertEquals("; (5)", code.get(++n));
    Assert.assertEquals(" LDY #<1", code.get(++n));
    Assert.assertEquals(" LDX #>1", code.get(++n));
    Assert.assertEquals(3, code.size());
  }

  @Test
  public void testExpressionAdd() {
    Source source = new Source("123 + 124 ");
    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();
    int n = -1;
    if (source.useCFOptimisation()) {
      Assert.assertEquals("; (5)", code.get(++n));
      Assert.assertEquals(" LDY #<247", code.get(++n));
      Assert.assertEquals(" LDX #>247", code.get(++n));
    }
    else {
      Assert.assertEquals("; (3)", code.get(++n));
      
      Assert.assertEquals(" CLC", code.get(++n));
      Assert.assertEquals(" LDA #<123", code.get(++n));
      Assert.assertEquals(" ADC #<124", code.get(++n));
      Assert.assertEquals(" TAY", code.get(++n));   
      Assert.assertEquals(" LDA #>123", code.get(++n));
      Assert.assertEquals(" ADC #>124", code.get(++n));
      Assert.assertEquals(" TAX", code.get(++n));   
    }
  }

  @Test
  public void testExpressionAddWithNegativ() {
    Source source = new Source("-1 + -2 ");
    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();
    int n = -1;
    if (source.useCFOptimisation()) {
      Assert.assertEquals("; (5)", code.get(++n));
      Assert.assertEquals(" LDY #<-3", code.get(++n));
      Assert.assertEquals(" LDX #>-3", code.get(++n));
    }
    else {
      Assert.assertEquals("; (3)", code.get(++n));
      Assert.assertEquals(" CLC", code.get(++n));
      Assert.assertEquals(" LDA #<-1", code.get(++n));
      Assert.assertEquals(" ADC #<-2", code.get(++n));
      Assert.assertEquals(" TAY", code.get(++n));   
      Assert.assertEquals(" LDA #>-1", code.get(++n));
      Assert.assertEquals(" ADC #>-2", code.get(++n));
      Assert.assertEquals(" TAX", code.get(++n));   
    }
  }

  @Test
  public void testExpressionByteVariableX() {
    Source source = new Source("X ");
    source.addVariable("X", Type.BYTE);
    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();

    int n = -1;
    Assert.assertEquals("; (6)", code.get(++n));
    Assert.assertEquals(" LDY X", code.get(++n));
    Assert.assertEquals(" LDX #0", code.get(++n));
    Assert.assertEquals(3, code.size());
  }

  @Test
  public void testExpressionInt8VariableX() {
    Source source = new Source("X ");
    source.addVariable("X", Type.INT8);
    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();

    int n = -1;
    Assert.assertEquals("; (6)", code.get(++n));
    Assert.assertEquals(" LDY X", code.get(++n));
    Assert.assertEquals(" CPY #$80", code.get(++n));
    Assert.assertEquals(" LDX #0", code.get(++n));
    Assert.assertEquals(" BCC *+4", code.get(++n));
    Assert.assertEquals(" LDX #$FF", code.get(++n));
    Assert.assertEquals(6, code.size());
  }


  @Test
  public void testExpressionZahlAddByteX() {
    Source source = new Source("123 + X ");
    source.addVariable("X", Type.BYTE);
    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();

    int n = -1;
    Assert.assertEquals("; (3)", code.get(++n));
    Assert.assertEquals(" CLC", code.get(++n));
    Assert.assertEquals(" LDA #<123", code.get(++n));
    Assert.assertEquals(" ADC X", code.get(++n));
    Assert.assertEquals(" TAY", code.get(++n));
    Assert.assertEquals(" LDA #>123", code.get(++n));
    Assert.assertEquals(" ADC #0", code.get(++n));
    Assert.assertEquals(" TAX", code.get(++n));
  }

  @Test
  public void testExpressionZahlAddXAddY() {
    Source source = new Source("123 + (X - Y) ");
    source.addVariable("X", Type.BYTE);
    source.addVariable("Y", Type.WORD);
    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();

    int n = -1;
    Assert.assertEquals("; (1)", code.get(++n));
    Assert.assertEquals(" LDA #<123", code.get(++n));
    Assert.assertEquals(" PHA", code.get(++n));
    Assert.assertEquals(" LDA #>123", code.get(++n));
    Assert.assertEquals(" PHA", code.get(++n));

    Assert.assertEquals("; (3)", code.get(++n));
    Assert.assertEquals(" SEC", code.get(++n));
    Assert.assertEquals(" LDA X", code.get(++n));
    Assert.assertEquals(" SBC Y", code.get(++n));
    Assert.assertEquals(" TAY", code.get(++n));
    Assert.assertEquals(" LDA #0", code.get(++n));
    Assert.assertEquals(" SBC Y+1", code.get(++n));
    Assert.assertEquals(" TAX", code.get(++n));

    Assert.assertEquals("; (7)", code.get(++n));
    Assert.assertEquals(" STY @OP", code.get(++n));
    Assert.assertEquals(" STX @OP+1", code.get(++n));
    Assert.assertEquals(" PLA", code.get(++n));
    Assert.assertEquals(" TAX", code.get(++n));
    Assert.assertEquals(" PLA", code.get(++n));
    Assert.assertEquals(" TAY", code.get(++n));
    Assert.assertEquals(" CLC", code.get(++n));
    Assert.assertEquals(" TYA", code.get(++n));
    Assert.assertEquals(" ADC @OP", code.get(++n));
    Assert.assertEquals(" TAY", code.get(++n));
    Assert.assertEquals(" TXA", code.get(++n));
    Assert.assertEquals(" ADC @OP+1", code.get(++n));
    Assert.assertEquals(" TAX", code.get(++n));
  }

  @Test
  public void testExpressionXAddZahl() {
    Source source = new Source("X + 123 ");
    source.addVariable("X", Type.BYTE);
    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();

    int n = -1;
    Assert.assertEquals("; (3)", code.get(++n));
    Assert.assertEquals(" CLC", code.get(++n));
    Assert.assertEquals(" LDA X", code.get(++n));
    Assert.assertEquals(" ADC #<123", code.get(++n));
    Assert.assertEquals(" TAY", code.get(++n));
    Assert.assertEquals(" LDA #0", code.get(++n));
    Assert.assertEquals(" ADC #>123", code.get(++n));
    Assert.assertEquals(" TAX", code.get(++n));
  }

  @Test
  public void testExpressionXMulZahl() {
    Source source = new Source("X * 123 ");
    source.setStarChainMult(true);
    source.addVariable("X", Type.BYTE);
    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();

    int n = -1;
    Assert.assertEquals("; (4)", code.get(++n));
    Assert.assertEquals(" LDY X", code.get(++n));
    Assert.assertEquals(" LDX #0", code.get(++n));

//    Assert.assertEquals(" STY @OP", code.get(++n));    // Rw = R1
    Assert.assertEquals(" TYA", code.get(++n));    // Rw = R1
    Assert.assertEquals(" STX @OP+1", code.get(++n));
    Assert.assertEquals(" STA @PRODUKT", code.get(++n));
    Assert.assertEquals(" STX @PRODUKT+1", code.get(++n));

    Assert.assertEquals(" ASL A", code.get(++n));
    Assert.assertEquals(" ROL @OP+1", code.get(++n));
    Assert.assertEquals(" ASL A", code.get(++n));
    Assert.assertEquals(" ROL @OP+1", code.get(++n));
    Assert.assertEquals(" ASL A", code.get(++n));
    Assert.assertEquals(" ROL @OP+1", code.get(++n));
    Assert.assertEquals(" ASL A", code.get(++n));
    Assert.assertEquals(" ROL @OP+1", code.get(++n));
    Assert.assertEquals(" ASL A", code.get(++n));
    Assert.assertEquals(" ROL @OP+1", code.get(++n)); // Rw *= 32

    Assert.assertEquals(" SEC", code.get(++n));
//    Assert.assertEquals(" LDA @OP", code.get(++n));
    Assert.assertEquals(" SBC @PRODUKT", code.get(++n));
//    Assert.assertEquals(" STA @OP", code.get(++n));
    Assert.assertEquals(" TAY", code.get(++n));
    Assert.assertEquals(" LDA @OP+1", code.get(++n));
    Assert.assertEquals(" SBC @PRODUKT+1", code.get(++n));
    Assert.assertEquals(" STA @OP+1", code.get(++n));   // Rw -= R1
    Assert.assertEquals(" TYA", code.get(++n));

    Assert.assertEquals(" ASL A", code.get(++n));
    Assert.assertEquals(" ROL @OP+1", code.get(++n));
    Assert.assertEquals(" ASL A", code.get(++n));
    Assert.assertEquals(" ROL @OP+1", code.get(++n)); // * 4

    Assert.assertEquals(" SEC", code.get(++n));
//    Assert.assertEquals(" LDA @OP", code.get(++n));
    Assert.assertEquals(" SBC @PRODUKT", code.get(++n));
//    Assert.assertEquals(" STA @OP", code.get(++n));
    Assert.assertEquals(" TAY", code.get(++n));
    Assert.assertEquals(" LDA @OP+1", code.get(++n));
    Assert.assertEquals(" SBC @PRODUKT+1", code.get(++n));
    Assert.assertEquals(" STA @OP+1", code.get(++n));   // Rw -= R1
    Assert.assertEquals(" TYA", code.get(++n));

//    Assert.assertEquals(" LDY @OP", code.get(++n));
    Assert.assertEquals(" TAY", code.get(++n));
    Assert.assertEquals(" LDX @OP+1", code.get(++n));

//    Assert.assertEquals(" LDA #<123", code.get(++n));
//    Assert.assertEquals(" STA @OP", code.get(++n));
//    Assert.assertEquals(" LDA #>123", code.get(++n));
//    Assert.assertEquals(" STA @OP+1", code.get(++n));
//    Assert.assertEquals(" JSR @IMULT", code.get(++n));
  }

  @Test
  public void testExpressionXMulZahl2erKomplement() {
    Source source = new Source("X * 8 ");
    source.setStarChainMult(true);

    source.addVariable("X", Type.BYTE);
    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();

    int n = -1;
    Assert.assertEquals("; (4)", code.get(++n));
    Assert.assertEquals(" LDY X", code.get(++n));
    Assert.assertEquals(" LDX #0", code.get(++n));

    Assert.assertEquals(" TYA", code.get(++n));
    Assert.assertEquals(" STX @OP+1", code.get(++n));

    Assert.assertEquals(" ASL A", code.get(++n));
    Assert.assertEquals(" ROL @OP+1", code.get(++n));
    Assert.assertEquals(" ASL A", code.get(++n));
    Assert.assertEquals(" ROL @OP+1", code.get(++n));
    Assert.assertEquals(" ASL A", code.get(++n));
    Assert.assertEquals(" ROL @OP+1", code.get(++n));
    Assert.assertEquals(" TAY", code.get(++n));
    Assert.assertEquals(" LDX @OP+1", code.get(++n));
  }

  @Test
  public void testExpressionXMulZahl7StarChain() {
    Source source = new Source("X * 7 ");
    source.setStarChainMult(true);

    source.addVariable("X", Type.BYTE);
    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();

    int n = -1;
    Assert.assertEquals("; (4)", code.get(++n));
    Assert.assertEquals(" LDY X", code.get(++n));
    Assert.assertEquals(" LDX #0", code.get(++n));

    Assert.assertEquals(" TYA", code.get(++n));
    Assert.assertEquals(" STX @OP+1", code.get(++n));

    Assert.assertEquals(" STA @PRODUKT", code.get(++n));
    Assert.assertEquals(" STX @PRODUKT+1", code.get(++n));

    Assert.assertEquals(" ASL A", code.get(++n));
    Assert.assertEquals(" ROL @OP+1", code.get(++n));
    Assert.assertEquals(" ASL A", code.get(++n));
    Assert.assertEquals(" ROL @OP+1", code.get(++n));
    Assert.assertEquals(" ASL A", code.get(++n));
    Assert.assertEquals(" ROL @OP+1", code.get(++n));

//    LDA %1+0       ;Test if the LSB is zero
//    BNE SKIP       ;If it isn't we can skip the next instruction
//    DEC %1+1       ;Decrement the MSB when the LSB will underflow
//SKIP  DEC %1+0       ;Decrement the LSB

    Assert.assertEquals(" SEC", code.get(++n));
    Assert.assertEquals(" SBC @PRODUKT", code.get(++n));
    Assert.assertEquals(" TAY", code.get(++n));
    Assert.assertEquals(" LDA @OP+1", code.get(++n));
    Assert.assertEquals(" SBC @PRODUKT+1", code.get(++n));
    Assert.assertEquals(" STA @OP+1", code.get(++n));   // Rw -= R1
    Assert.assertEquals(" TYA", code.get(++n));

    Assert.assertEquals(" TAY", code.get(++n));
    Assert.assertEquals(" LDX @OP+1", code.get(++n));

    Assert.assertEquals(21, n);
  }

  @Test
  public void testExpressionXMulZahl5StarChain() {
    Source source = new Source("X * 5 ");
    source.setStarChainMult(true);

    source.addVariable("X", Type.BYTE);
    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();

    int n = -1;
    Assert.assertEquals("; (4)", code.get(++n));
    Assert.assertEquals(" LDY X", code.get(++n));
    Assert.assertEquals(" LDX #0", code.get(++n));

    Assert.assertEquals(" TYA", code.get(++n));
    Assert.assertEquals(" STX @OP+1", code.get(++n));

    Assert.assertEquals(" STA @PRODUKT", code.get(++n));
    Assert.assertEquals(" STX @PRODUKT+1", code.get(++n));

    Assert.assertEquals(" ASL A", code.get(++n));
    Assert.assertEquals(" ROL @OP+1", code.get(++n));
    Assert.assertEquals(" ASL A", code.get(++n));
    Assert.assertEquals(" ROL @OP+1", code.get(++n));

//    LDA %1+0       ;Test if the LSB is zero
//    BNE SKIP       ;If it isn't we can skip the next instruction
//    DEC %1+1       ;Decrement the MSB when the LSB will underflow
//SKIP  DEC %1+0       ;Decrement the LSB

    Assert.assertEquals(" CLC", code.get(++n));
    Assert.assertEquals(" ADC @PRODUKT", code.get(++n));
    Assert.assertEquals(" TAY", code.get(++n));
    Assert.assertEquals(" LDA @OP+1", code.get(++n));
    Assert.assertEquals(" ADC @PRODUKT+1", code.get(++n));
    Assert.assertEquals(" STA @OP+1", code.get(++n));
    Assert.assertEquals(" TYA", code.get(++n));

    Assert.assertEquals(" TAY", code.get(++n));
    Assert.assertEquals(" LDX @OP+1", code.get(++n));

    Assert.assertEquals(19, n);
  }

  @Test
  public void testExpressionXMul256Zahl2erKomplement() {
    Source source = new Source("X * 256 ");
    source.setStarChainMult(true);

    source.addVariable("X", Type.WORD);
    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();

    int n = -1;
    Assert.assertEquals("; (4)", code.get(++n));
    Assert.assertEquals(" LDY X", code.get(++n));
    Assert.assertEquals(" LDX X+1", code.get(++n));

    // Wir tauschen einfach das lowbyte mit dem highbyte
    Assert.assertEquals(" TYA", code.get(++n));
    Assert.assertEquals(" TAX", code.get(++n));
    Assert.assertEquals(" LDY #0", code.get(++n));
  }

  @Test
  public void testExpressionXDiv2Zahl2erKomplement() {
    Source source = new Source("X / 2 ");
    source.setStarChainMult(true);

    source.addVariable("X", Type.WORD);
    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();

    int n = -1;
    Assert.assertEquals("; (4)", code.get(++n));
    Assert.assertEquals(" LDY X", code.get(++n));
    Assert.assertEquals(" LDX X+1", code.get(++n));

    Assert.assertEquals(" STY @OP", code.get(++n));
    Assert.assertEquals(" TXA", code.get(++n));
    Assert.assertEquals(" CMP #$80", code.get(++n));
    Assert.assertEquals(" ROR A", code.get(++n));
    Assert.assertEquals(" ROR @OP", code.get(++n));
    Assert.assertEquals(" LDY @OP", code.get(++n));
    Assert.assertEquals(" TAX", code.get(++n));
  }

  @Test
  public void testExpressionZahlMult2ByShift() {
    Source source = new Source("2 * 4 ");

    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();
    int n = -1;
    if (source.useCFOptimisation()) {
      Assert.assertEquals("; (5)", code.get(++n));
      Assert.assertEquals(" LDY #<8", code.get(++n));
      Assert.assertEquals(" LDX #>8", code.get(++n));      
    }
    else {
      Assert.assertEquals("; (4)", code.get(++n));
      Assert.assertEquals(" LDY #<2", code.get(++n));
      Assert.assertEquals(" LDX #>2", code.get(++n));
  
      Assert.assertEquals(" TYA", code.get(++n));
      Assert.assertEquals(" STX @OP+1", code.get(++n));
      Assert.assertEquals(" ASL A", code.get(++n)); // * 2
      Assert.assertEquals(" ROL @OP+1", code.get(++n));
      Assert.assertEquals(" ASL A", code.get(++n)); // * 2
      Assert.assertEquals(" ROL @OP+1", code.get(++n));
      Assert.assertEquals(" TAY", code.get(++n));
      Assert.assertEquals(" LDX @OP+1", code.get(++n));
    }
  }
  
  @Test
  public void testExpressionZahlMult2ByMult() {
    Source source = new Source("2 * 4 ");
    source.setShiftMultDiv(false);
    source.setStarChainMult(false);

    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();
    int n = -1;
    if (source.useCFOptimisation()) {
      Assert.assertEquals("; (5)", code.get(++n));
      Assert.assertEquals(" LDY #<8", code.get(++n));
      Assert.assertEquals(" LDX #>8", code.get(++n));      
    }
    else {
      Assert.assertEquals("; (4)", code.get(++n));
      Assert.assertEquals(" LDY #<2", code.get(++n));
      Assert.assertEquals(" LDX #>2", code.get(++n));
  
      Assert.assertEquals(" LDA #<4", code.get(++n));
      Assert.assertEquals(" STA @OP", code.get(++n));
      Assert.assertEquals(" LDA #>4", code.get(++n));
      Assert.assertEquals(" STA @OP+1", code.get(++n));
      Assert.assertEquals(" JSR @IMULT", code.get(++n));
    }
  }
  
  @Test
  public void testExpressionXDiv256Zahl2erKomplement() {
    Source source = new Source("X / 256 ");
    source.setStarChainMult(true);

    source.addVariable("X", Type.WORD);
    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();

    int n = -1;
    Assert.assertEquals("; (4)", code.get(++n));
    Assert.assertEquals(" LDY X", code.get(++n));
    Assert.assertEquals(" LDX X+1", code.get(++n));

    // Wir tauschen einfach das lowbyte mit dem highbyte
    Assert.assertEquals(" TXA", code.get(++n));
    Assert.assertEquals(" TAY", code.get(++n));
//    Assert.assertEquals(" CPY #$80", code.get(++n));
    Assert.assertEquals(" LDX #0", code.get(++n));
//    Assert.assertEquals(" BCC *+4", code.get(++n));
//    Assert.assertEquals(" LDX #$FF", code.get(++n));
  }

  @Test
  public void testExpressionZahlMulX() {
    Source source = new Source("123 * X ");
    source.addVariable("X", Type.WORD);
    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();

    int n = -1;
    Assert.assertEquals("; (4)", code.get(++n));
    Assert.assertEquals(" LDY #<123", code.get(++n));
    Assert.assertEquals(" LDX #>123", code.get(++n));
    Assert.assertEquals(" LDA X", code.get(++n));
    Assert.assertEquals(" STA @OP", code.get(++n));
    Assert.assertEquals(" LDA X+1", code.get(++n));
    Assert.assertEquals(" STA @OP+1", code.get(++n));
    Assert.assertEquals(" JSR @IMULT", code.get(++n));
  }

  @Test
  public void testExpressionXMulZahlNoStarChain() {
    Source source = new Source("X * 123 ");
    source.setStarChainMult(false);

    source.addVariable("X", Type.WORD);
    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();

    int n = -1;
    Assert.assertEquals("; (4)", code.get(++n));
    Assert.assertEquals(" LDY X", code.get(++n));
    Assert.assertEquals(" LDX X+1", code.get(++n));
    Assert.assertEquals(" LDA #<123", code.get(++n));
    Assert.assertEquals(" STA @OP", code.get(++n));
    Assert.assertEquals(" LDA #>123", code.get(++n));
    Assert.assertEquals(" STA @OP+1", code.get(++n));
    Assert.assertEquals(" JSR @IMULT", code.get(++n));
  }


  @Test
  public void testExpressionZahlAddZahlMulZahl() {
    Source source = new Source("2 + 2 * 2 ");
    source.setStarChainMult(true);

    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();
    int n = -1;
    if (source.useCFOptimisation()) {
      Assert.assertEquals("; (5)", code.get(++n));
      Assert.assertEquals(" LDY #<6", code.get(++n));
      Assert.assertEquals(" LDX #>6", code.get(++n));
    }
    else {
      Assert.assertEquals("; (1)", code.get(++n));
      Assert.assertEquals(" LDA #<2", code.get(++n));
      Assert.assertEquals(" PHA", code.get(++n));
      Assert.assertEquals(" LDA #>2", code.get(++n));
      Assert.assertEquals(" PHA", code.get(++n));
  
      Assert.assertEquals("; (4)", code.get(++n));
      Assert.assertEquals(" LDY #<2", code.get(++n));
      Assert.assertEquals(" LDX #>2", code.get(++n));
      Assert.assertEquals(" TYA", code.get(++n));
      Assert.assertEquals(" STX @OP+1", code.get(++n));
      Assert.assertEquals(" ASL A", code.get(++n)); // * 2
      Assert.assertEquals(" ROL @OP+1", code.get(++n));
      Assert.assertEquals(" TAY", code.get(++n));
      Assert.assertEquals(" LDX @OP+1", code.get(++n));
  
      Assert.assertEquals("; (7)", code.get(++n));
      Assert.assertEquals(" STY @OP", code.get(++n));
      Assert.assertEquals(" STX @OP+1", code.get(++n));
      Assert.assertEquals(" PLA", code.get(++n));
      Assert.assertEquals(" TAX", code.get(++n));
      Assert.assertEquals(" PLA", code.get(++n));
      Assert.assertEquals(" TAY", code.get(++n));
      Assert.assertEquals(" CLC", code.get(++n));
      Assert.assertEquals(" TYA", code.get(++n));
      Assert.assertEquals(" ADC @OP", code.get(++n));
      Assert.assertEquals(" TAY", code.get(++n));
      Assert.assertEquals(" TXA", code.get(++n));
      Assert.assertEquals(" ADC @OP+1", code.get(++n));
      Assert.assertEquals(" TAX", code.get(++n));
    }
  }
  @Test
  public void testExpressionVariableAddZahlMulZahl() {
    Source source = new Source("X + 2 * 3 ");
    
    source.setStarChainMult(true);

    source.addVariable("X", Type.BYTE);
    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();
    int n = -1;
    if (source.useCFOptimisation()) {
      Assert.assertEquals("; (3)", code.get(++n));
      Assert.assertEquals(" CLC", code.get(++n));
      Assert.assertEquals(" LDA X", code.get(++n));
      Assert.assertEquals(" ADC #<6", code.get(++n));
      Assert.assertEquals(" TAY", code.get(++n));
      Assert.assertEquals(" LDA #0", code.get(++n));
      Assert.assertEquals(" ADC #>6", code.get(++n));
      Assert.assertEquals(" TAX", code.get(++n));
    }
    else {
      Assert.assertEquals("; (2)", code.get(++n));
      Assert.assertEquals(" LDA X", code.get(++n));
      Assert.assertEquals(" PHA", code.get(++n));
      Assert.assertEquals(" LDA #0", code.get(++n));
      Assert.assertEquals(" PHA", code.get(++n));
  
      Assert.assertEquals("; (4)", code.get(++n));
      Assert.assertEquals(" LDY #<2", code.get(++n));
      Assert.assertEquals(" LDX #>2", code.get(++n));
  
      Assert.assertEquals(" TYA", code.get(++n));
      Assert.assertEquals(" STX @OP+1", code.get(++n));
      Assert.assertEquals(" STA @PRODUKT", code.get(++n));
      Assert.assertEquals(" STX @PRODUKT+1", code.get(++n));
  
      Assert.assertEquals(" ASL A", code.get(++n));
      Assert.assertEquals(" ROL @OP+1", code.get(++n));
      Assert.assertEquals(" ASL A", code.get(++n));
      Assert.assertEquals(" ROL @OP+1", code.get(++n));
  
      Assert.assertEquals(" SEC", code.get(++n));
      Assert.assertEquals(" SBC @PRODUKT", code.get(++n));
      Assert.assertEquals(" TAY", code.get(++n));
      Assert.assertEquals(" LDA @OP+1", code.get(++n));
      Assert.assertEquals(" SBC @PRODUKT+1", code.get(++n));
      Assert.assertEquals(" STA @OP+1", code.get(++n));   // Rw -= R1
      Assert.assertEquals(" TYA", code.get(++n));
  
      Assert.assertEquals(" TAY", code.get(++n));
      Assert.assertEquals(" LDX @OP+1", code.get(++n));
  
  //    Assert.assertEquals(" LDA #<3", code.get(++n));
  //    Assert.assertEquals(" STA @OP", code.get(++n));
  //    Assert.assertEquals(" LDA #>3", code.get(++n));
  //    Assert.assertEquals(" STA @OP+1", code.get(++n));
  //    Assert.assertEquals(" JSR @IMULT", code.get(++n));
  
      
       // ; Wuerde voraussetzen, das die Daten rückwärts auf den Stack kommen
      // sty @op ;3
      // stx @op+1 ;3
      // clc ;2
      // pla ;4
      // adc @op ;3
      // tay ;2 
      // pla ;4
      // adc @op+1 ;3
      // tax ;2 = 26 (13 bytes)
       
      Assert.assertEquals("; (7)", code.get(++n));
      Assert.assertEquals(" STY @OP", code.get(++n));
      Assert.assertEquals(" STX @OP+1", code.get(++n));
  
      Assert.assertEquals(" PLA", code.get(++n));
      Assert.assertEquals(" TAX", code.get(++n));
      Assert.assertEquals(" PLA", code.get(++n));
      Assert.assertEquals(" TAY", code.get(++n));
      Assert.assertEquals(" CLC", code.get(++n));
      Assert.assertEquals(" TYA", code.get(++n));
      Assert.assertEquals(" ADC @OP", code.get(++n));
      Assert.assertEquals(" TAY", code.get(++n));
      Assert.assertEquals(" TXA", code.get(++n));
      Assert.assertEquals(" ADC @OP+1", code.get(++n));
      Assert.assertEquals(" TAX", code.get(++n)); // 32 takte (17 bytes)
    }
  }

  @Test
  public void testExpressionGeradengleichung() {
    Source source = new Source("M * X + B");
    source.setVerboseLevel(3);

    source.addVariable("M", Type.WORD);
    source.addVariable("X", Type.WORD);
    source.addVariable("B", Type.WORD);
    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();

    int n = -1;
    Assert.assertEquals("; (4)", code.get(++n));
    Assert.assertEquals(" LDY M", code.get(++n));
    Assert.assertEquals(" LDX M+1", code.get(++n));
    Assert.assertEquals(" LDA X", code.get(++n));
    Assert.assertEquals(" STA @OP", code.get(++n));
    Assert.assertEquals(" LDA X+1", code.get(++n));
    Assert.assertEquals(" STA @OP+1", code.get(++n));
    Assert.assertEquals(" JSR @IMULT", code.get(++n));

    Assert.assertEquals("; (8)", code.get(++n));
    Assert.assertEquals(" CLC", code.get(++n));
    Assert.assertEquals(" TYA", code.get(++n));
    Assert.assertEquals(" ADC B", code.get(++n));
    Assert.assertEquals(" TAY", code.get(++n));
    Assert.assertEquals(" TXA", code.get(++n));
    Assert.assertEquals(" ADC B+1", code.get(++n));
    Assert.assertEquals(" TAX", code.get(++n));

  }

  @Test
  public void testExpressionGeradengleichungINC() {
    Source source = new Source("M * X + 1").setVerboseLevel(2);
    source.addVariable("M", Type.WORD);
    source.addVariable("X", Type.WORD);
    source.addVariable("B", Type.WORD);
    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();

    int n = -1;
    Assert.assertEquals("; (4)", code.get(++n));
    Assert.assertEquals(" LDY M", code.get(++n));
    Assert.assertEquals(" LDX M+1", code.get(++n));
    Assert.assertEquals(" LDA X", code.get(++n));
    Assert.assertEquals(" STA @OP", code.get(++n));
    Assert.assertEquals(" LDA X+1", code.get(++n));
    Assert.assertEquals(" STA @OP+1", code.get(++n));
    Assert.assertEquals(" JSR @IMULT", code.get(++n));

    Assert.assertEquals("; (8)", code.get(++n));
    Assert.assertEquals(" INY", code.get(++n));
    Assert.assertEquals(" BNE *+3", code.get(++n));
    Assert.assertEquals(" INX", code.get(++n));

  }

  @Test
  public void testExpressionGeradengleichungAdd2() {
    Source source = new Source("M * X + 2");
    source.addVariable("M", Type.WORD);
    source.addVariable("X", Type.WORD);
    source.addVariable("B", Type.WORD);
    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();

    int n = -1;
    Assert.assertEquals("; (4)", code.get(++n));
    Assert.assertEquals(" LDY M", code.get(++n));
    Assert.assertEquals(" LDX M+1", code.get(++n));
    Assert.assertEquals(" LDA X", code.get(++n));
    Assert.assertEquals(" STA @OP", code.get(++n));
    Assert.assertEquals(" LDA X+1", code.get(++n));
    Assert.assertEquals(" STA @OP+1", code.get(++n));
    Assert.assertEquals(" JSR @IMULT", code.get(++n));

    Assert.assertEquals("; (8)", code.get(++n));
    Assert.assertEquals(" CLC", code.get(++n));
    Assert.assertEquals(" TYA", code.get(++n));
    Assert.assertEquals(" ADC #<2", code.get(++n));
    Assert.assertEquals(" TAY", code.get(++n));
    Assert.assertEquals(" TXA", code.get(++n));
    Assert.assertEquals(" ADC #>2", code.get(++n));
    Assert.assertEquals(" TAX", code.get(++n));
  }

  @Test
  public void testExpressionNummstellen() {
    Source source = new Source("(X + 2) * (X + 2) ");
    source.addVariable("X", Type.BYTE);
    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();

    int n = -1;
    Assert.assertEquals("; (3)", code.get(++n));
    Assert.assertEquals(" CLC", code.get(++n));
    Assert.assertEquals(" LDA X", code.get(++n));
    Assert.assertEquals(" ADC #<2", code.get(++n));
    Assert.assertEquals(" TAY", code.get(++n));
    Assert.assertEquals(" LDA #0", code.get(++n));
    Assert.assertEquals(" ADC #>2", code.get(++n));
    Assert.assertEquals(" TAX", code.get(++n));

    Assert.assertEquals("; (10)", code.get(++n));
    Assert.assertEquals(" TYA", code.get(++n));
    Assert.assertEquals(" PHA", code.get(++n));
    Assert.assertEquals(" TXA", code.get(++n));
    Assert.assertEquals(" PHA", code.get(++n));

    Assert.assertEquals("; (3)", code.get(++n));
    Assert.assertEquals(" CLC", code.get(++n));
    Assert.assertEquals(" LDA X", code.get(++n));
    Assert.assertEquals(" ADC #<2", code.get(++n));
    Assert.assertEquals(" TAY", code.get(++n));
    Assert.assertEquals(" LDA #0", code.get(++n));
    Assert.assertEquals(" ADC #>2", code.get(++n));
    Assert.assertEquals(" TAX", code.get(++n));

    Assert.assertEquals("; (7)", code.get(++n));
    Assert.assertEquals(" STY @OP", code.get(++n));
    Assert.assertEquals(" STX @OP+1", code.get(++n));
    Assert.assertEquals(" PLA", code.get(++n));
    Assert.assertEquals(" TAX", code.get(++n));
    Assert.assertEquals(" PLA", code.get(++n));
    Assert.assertEquals(" TAY", code.get(++n));
    Assert.assertEquals(" JSR @IMULT", code.get(++n));
  }

  @Test
  public void testExpressionNummstellenVars() {
    Source source = new Source("(X + Y) * (X + Y) ");
    source.addVariable("X", Type.WORD);
    source.addVariable("Y", Type.WORD);
    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();
    int n = -1;
    Assert.assertEquals("; (3)", code.get(++n));
    Assert.assertEquals(" CLC", code.get(++n));
    Assert.assertEquals(" LDA X", code.get(++n));
    Assert.assertEquals(" ADC Y", code.get(++n));
    Assert.assertEquals(" TAY", code.get(++n));
    Assert.assertEquals(" LDA X+1", code.get(++n));
    Assert.assertEquals(" ADC Y+1", code.get(++n));
    Assert.assertEquals(" TAX", code.get(++n));

    Assert.assertEquals("; (10)", code.get(++n));
    Assert.assertEquals(" TYA", code.get(++n));
    Assert.assertEquals(" PHA", code.get(++n));
    Assert.assertEquals(" TXA", code.get(++n));
    Assert.assertEquals(" PHA", code.get(++n));

    Assert.assertEquals("; (3)", code.get(++n));
    Assert.assertEquals(" CLC", code.get(++n));
    Assert.assertEquals(" LDA X", code.get(++n));
    Assert.assertEquals(" ADC Y", code.get(++n));
    Assert.assertEquals(" TAY", code.get(++n));
    Assert.assertEquals(" LDA X+1", code.get(++n));
    Assert.assertEquals(" ADC Y+1", code.get(++n));
    Assert.assertEquals(" TAX", code.get(++n));

    Assert.assertEquals("; (7)", code.get(++n));
    Assert.assertEquals(" STY @OP", code.get(++n));
    Assert.assertEquals(" STX @OP+1", code.get(++n));
    Assert.assertEquals(" PLA", code.get(++n));
    Assert.assertEquals(" TAX", code.get(++n));
    Assert.assertEquals(" PLA", code.get(++n));
    Assert.assertEquals(" TAY", code.get(++n));
    Assert.assertEquals(" JSR @IMULT", code.get(++n));

  }

  @Test
  public void testExpressionWordAAddByteB() {
    Source source = new Source("A + B");
    source.addVariable("A__", Type.WORD);
    source.addVariable("B", Type.BYTE);
    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();

    int n = -1;
    Assert.assertEquals("; (3)", code.get(++n));
    Assert.assertEquals(" CLC", code.get(++n));
    Assert.assertEquals(" LDA A__", code.get(++n));
    Assert.assertEquals(" ADC B", code.get(++n));
    Assert.assertEquals(" TAY", code.get(++n));
    Assert.assertEquals(" LDA A__+1", code.get(++n));
    Assert.assertEquals(" ADC #0", code.get(++n));
    Assert.assertEquals(" TAX", code.get(++n));
  }

  @Test
  public void testExpressionWordAAddInt8B() {
    Source source = new Source("A + B");
    source.addVariable("A__", Type.WORD);
    source.addVariable("B", Type.INT8);
    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();

    int n = -1;
    Assert.assertEquals("; (3)", code.get(++n));
    Assert.assertEquals(" CLC", code.get(++n));
    Assert.assertEquals(" LDA A__", code.get(++n));
    Assert.assertEquals(" ADC B", code.get(++n));
    Assert.assertEquals(" TAY", code.get(++n));
    Assert.assertEquals(" LDA A__+1", code.get(++n));
    Assert.assertEquals(" ADC #0", code.get(++n));
    Assert.assertEquals(" TAX", code.get(++n));
  }

  @Test
  public void testExpressionXMul2ExponentWord() {
    Source source = new Source("X * 8 ");
    source.setStarChainMult(true);

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
    Assert.assertEquals(" TYA", code.get(++n));
    Assert.assertEquals(" STX @OP+1", code.get(++n));
    Assert.assertEquals(" ASL A", code.get(++n));
    Assert.assertEquals(" ROL @OP+1", code.get(++n));
    Assert.assertEquals(" ASL A", code.get(++n));
    Assert.assertEquals(" ROL @OP+1", code.get(++n));
    Assert.assertEquals(" ASL A", code.get(++n));
    Assert.assertEquals(" ROL @OP+1", code.get(++n));
    Assert.assertEquals(" TAY", code.get(++n));
    Assert.assertEquals(" LDX @OP+1", code.get(++n));

    Assert.assertEquals(13, code.size());
  }

  @Test
  public void testExpressionAWordAddConstB() {
    Source source = new Source("a + b ");
    source.addVariable("A__", Type.WORD);
    source.addVariable("B", Type.CONST);
    source.setVariableAddress("B", "123");

    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();

    int n=-1;
    Assert.assertEquals("; (3)", code.get(++n));
    Assert.assertEquals(" CLC", code.get(++n));
    Assert.assertEquals(" LDA A__", code.get(++n));
    Assert.assertEquals(" ADC #<123", code.get(++n));
    Assert.assertEquals(" TAY", code.get(++n));
    Assert.assertEquals(" LDA A__+1", code.get(++n));
    Assert.assertEquals(" ADC #>123", code.get(++n));
    Assert.assertEquals(" TAX", code.get(++n));
    Assert.assertEquals(8, code.size());
  }

  @Test
  public void testExpressionWordAAddWordB() {
    Source source = new Source("A + B + C");
    source.addVariable("A__", Type.WORD);
    source.addVariable("B", Type.WORD);
    source.addVariable("C", Type.WORD);
    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();

    int n = -1;
    Assert.assertEquals("; (3)", code.get(++n));
    Assert.assertEquals(" CLC", code.get(++n));
    Assert.assertEquals(" LDA A__", code.get(++n));
    Assert.assertEquals(" ADC B", code.get(++n));
    Assert.assertEquals(" TAY", code.get(++n));
    Assert.assertEquals(" LDA A__+1", code.get(++n));
    Assert.assertEquals(" ADC B+1", code.get(++n));
    Assert.assertEquals(" TAX", code.get(++n));
    
    Assert.assertEquals("; (8)", code.get(++n));
    Assert.assertEquals(" CLC", code.get(++n));
    Assert.assertEquals(" TYA", code.get(++n));
    Assert.assertEquals(" ADC C", code.get(++n));
    Assert.assertEquals(" TAY", code.get(++n));
    Assert.assertEquals(" TXA", code.get(++n));
    Assert.assertEquals(" ADC C+1", code.get(++n));
    Assert.assertEquals(" TAX", code.get(++n));
    
    Assert.assertEquals(16, code.size());
  }

  @Test
  public void testExpressionAIntAddWordB() {
    Source source = new Source("1 + b ");
    source.addVariable("B", Type.WORD);

    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();

    int n=-1;
    Assert.assertEquals("; (3)", code.get(++n));
    Assert.assertEquals(" CLC", code.get(++n));
    Assert.assertEquals(" LDA #<1", code.get(++n));
    Assert.assertEquals(" ADC B", code.get(++n));
    Assert.assertEquals(" TAY", code.get(++n));
    Assert.assertEquals(" LDA #>1", code.get(++n));
    Assert.assertEquals(" ADC B+1", code.get(++n));
    Assert.assertEquals(" TAX", code.get(++n));
    Assert.assertEquals(8, code.size());
  }


}
