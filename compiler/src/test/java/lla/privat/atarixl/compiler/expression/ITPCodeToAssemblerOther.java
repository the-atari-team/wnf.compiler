// cdw by 'The Atari Team' 2022
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler.expression;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import lla.privat.atarixl.compiler.Symbol;
import lla.privat.atarixl.compiler.SymbolEnum;
import lla.privat.atarixl.compiler.source.Source;

public class ITPCodeToAssemblerOther {
  private List<Integer> getPCodeOf(Source source) {
    if (source.getVerboseLevel() < 2) {
      source.setVerboseLevel(2);
    }
    
    Expression expression = new Expression(source);

    Symbol symbol = source.nextElement();
    symbol = expression.expression(symbol).getLastSymbol();

    Assert.assertEquals(SymbolEnum.noSymbol, symbol.getId());

    System.out.println(expression.joinedPCode());

    expression.optimisation();
    System.out.println();
    System.out.println(expression.joinedPCode());

    return expression.getPCode();
  }

  @Test
  public void testExpressionWordAddress() {
    Source source = new Source("adr:x ");
    source.addVariable("X", Type.WORD);
    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();

    int n = -1;
    Assert.assertEquals("; (13)", code.get(++n));
    Assert.assertEquals(" LDY #<X", code.get(++n));
    Assert.assertEquals(" LDX #>X", code.get(++n));
    Assert.assertEquals(3, code.size());
  }

  @Test
  public void testExpressionByteToWord() {
    Source source = new Source("x ");
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
  public void testExpressionInt8ToWord() {
    Source source = new Source("x ");
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
  public void testExpression1PlusInt8XToWord() {
    Source source = new Source("1+x ");
    source.addVariable("X", Type.INT8);
    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();

    int n = -1;
    Assert.assertEquals("; (3)", code.get(++n));
    Assert.assertEquals(" CLC", code.get(++n));
    Assert.assertEquals(" LDA #<1", code.get(++n));
    Assert.assertEquals(" ADC X", code.get(++n));
    Assert.assertEquals(" TAY", code.get(++n));
    Assert.assertEquals(" LDA #>1", code.get(++n));
    Assert.assertEquals(" ADC #0", code.get(++n));
    Assert.assertEquals(" TAX", code.get(++n));

    Assert.assertEquals(8, code.size());
  }

  @Test
  public void testExpressionWordYPlusInt8XToWord() {
    Source source = new Source("y+x ");
    source.addVariable("X", Type.INT8);
    source.addVariable("Y", Type.WORD);
    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();

    int n = -1;
    Assert.assertEquals("; (3)", code.get(++n));
    Assert.assertEquals(" CLC", code.get(++n));
    Assert.assertEquals(" LDA Y", code.get(++n));
    Assert.assertEquals(" ADC X", code.get(++n));
    Assert.assertEquals(" TAY", code.get(++n));
    Assert.assertEquals(" LDA Y+1", code.get(++n));
    Assert.assertEquals(" ADC #0", code.get(++n));
    Assert.assertEquals(" TAX", code.get(++n));

    Assert.assertEquals(8, code.size());
  }

  @Test
  public void testExpressionInt8XPlusYx2ToWord() {
    Source source = new Source("y + x + y*2 ");
    source.addVariable("X", Type.INT8);
    source.addVariable("Y", Type.WORD);
    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();

    int n = -1;
    Assert.assertEquals("; (3)", code.get(++n));
    Assert.assertEquals(" CLC", code.get(++n));
    Assert.assertEquals(" LDA Y", code.get(++n));
    Assert.assertEquals(" ADC X", code.get(++n));
    Assert.assertEquals(" TAY", code.get(++n));
    Assert.assertEquals(" LDA Y+1", code.get(++n));
    Assert.assertEquals(" ADC #0", code.get(++n));
    Assert.assertEquals(" TAX", code.get(++n));

    Assert.assertEquals("; (10)", code.get(++n));
    Assert.assertEquals(" TYA", code.get(++n));
    Assert.assertEquals(" PHA", code.get(++n));
    Assert.assertEquals(" TXA", code.get(++n));
    Assert.assertEquals(" PHA", code.get(++n));

    Assert.assertEquals("; (4)", code.get(++n));
    Assert.assertEquals(" LDY Y", code.get(++n));
    Assert.assertEquals(" LDX Y+1", code.get(++n));

    Assert.assertEquals(" TYA", code.get(++n));
    Assert.assertEquals(" STX @OP+1", code.get(++n));
    Assert.assertEquals(" ASL A", code.get(++n));
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

    Assert.assertEquals(36, code.size());
  }

  @Test
  public void testExpressionADRPPlusInt8XPlusYx2ToWord() {
    Source source = new Source("adr:p + x ");
    source.addVariable("X", Type.INT8);
    source.addVariable("P", Type.WORD);
    source.setVariableAddress("P", "addr");
    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();

    int n = -1;
    Assert.assertEquals("; (13)", code.get(++n));
    Assert.assertEquals(" LDY #<P", code.get(++n));
    Assert.assertEquals(" LDX #>P", code.get(++n));

    Assert.assertEquals("; (8)", code.get(++n));
    Assert.assertEquals(" CLC", code.get(++n));
    Assert.assertEquals(" TYA", code.get(++n));
    Assert.assertEquals(" ADC X", code.get(++n));
    Assert.assertEquals(" TAY", code.get(++n));
    Assert.assertEquals(" TXA", code.get(++n));
    Assert.assertEquals(" ADC #0", code.get(++n));
    Assert.assertEquals(" TAX", code.get(++n));

    Assert.assertEquals(11, code.size());
  }

  @Test
  public void testExpressionWordFunctionOhneParameter() {
    Source source = new Source("x() ");
    source.addVariable("X", Type.FUNCTION);
    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();

    int n = -1;
    Assert.assertEquals("; (14)", code.get(++n));
    Assert.assertEquals(" JSR X", code.get(++n));
    Assert.assertEquals(2, code.size());
  }

  @Test
  public void testExpressionWordFunctionMitZahlParameter() {
    Source source = new Source("x(42) ");
    source.addVariable("X", Type.FUNCTION);
    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();

    int n = -1;
    Assert.assertEquals("; (5)", code.get(++n));
    Assert.assertEquals(" LDY #<42", code.get(++n));
    Assert.assertEquals(" LDX #>42", code.get(++n));

    Assert.assertEquals("; (16)", code.get(++n));
    Assert.assertEquals(" TYA", code.get(++n));
    Assert.assertEquals(" LDY #1", code.get(++n));
    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));
    Assert.assertEquals(" TXA", code.get(++n));
    Assert.assertEquals(" INY", code.get(++n));
    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));

    Assert.assertEquals("; (14)", code.get(++n));
    Assert.assertEquals(" JSR X_I", code.get(++n));
  }

  @Test
  public void testExpressionWordFunctionMitVariableParameter() {
    Source source = new Source("x(n) ");
    source.addVariable("X", Type.FUNCTION);
    source.addVariable("N", Type.WORD);

    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();

    int n = -1;
    Assert.assertEquals("; (6)", code.get(++n));
    Assert.assertEquals(" LDY N", code.get(++n));
    Assert.assertEquals(" LDX N+1", code.get(++n));

    Assert.assertEquals("; (16)", code.get(++n));
    Assert.assertEquals(" TYA", code.get(++n));
    Assert.assertEquals(" LDY #1", code.get(++n));
    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));
    Assert.assertEquals(" TXA", code.get(++n));
    Assert.assertEquals(" INY", code.get(++n));
    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));

    Assert.assertEquals("; (14)", code.get(++n));
    Assert.assertEquals(" JSR X_I", code.get(++n));
  }

  @Test
  public void testExpressionWordFunctionMitVariableWordArrayParameter() {
    Source source = new Source("x(n[2]) ");
    source.addVariable("X", Type.FUNCTION);
    source.addVariable("N", Type.WORD_ARRAY);

    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();

    int n = -1;
    Assert.assertEquals("; (5)", code.get(++n));
    Assert.assertEquals(" LDY #<2", code.get(++n));
    Assert.assertEquals(" LDX #>2", code.get(++n));

    Assert.assertEquals("; (11)", code.get(++n));
    Assert.assertEquals(" TYA", code.get(++n));
    Assert.assertEquals(" GETARRAYW N", code.get(++n));
    Assert.assertEquals(" TAY", code.get(++n));

    Assert.assertEquals("; (16)", code.get(++n));
    Assert.assertEquals(" TYA", code.get(++n));
    Assert.assertEquals(" LDY #1", code.get(++n));
    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));
    Assert.assertEquals(" TXA", code.get(++n));
    Assert.assertEquals(" INY", code.get(++n));
    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));

    Assert.assertEquals("; (14)", code.get(++n));
    Assert.assertEquals(" JSR X_I", code.get(++n));

  }

  @Test
  public void testExpressionWordFunctionMitVariableByteArrayParameter() {
    Source source = new Source("x(m[4]) ");
    source.addVariable("X", Type.FUNCTION);
    source.addVariable("M", Type.BYTE_ARRAY);

    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();

    int n = -1;
    Assert.assertEquals("; (5)", code.get(++n));
    Assert.assertEquals(" LDY #<4", code.get(++n));
    Assert.assertEquals(" LDX #>4", code.get(++n));

    Assert.assertEquals("; (12)", code.get(++n));
    Assert.assertEquals(" LDA M,Y", code.get(++n));
    Assert.assertEquals(" TAY", code.get(++n));
    Assert.assertEquals(" LDX #0", code.get(++n));

    Assert.assertEquals("; (16)", code.get(++n));
    Assert.assertEquals(" TYA", code.get(++n));
    Assert.assertEquals(" LDY #1", code.get(++n));
    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));
    Assert.assertEquals(" TXA", code.get(++n));
    Assert.assertEquals(" INY", code.get(++n));
    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));

    Assert.assertEquals("; (14)", code.get(++n));
    Assert.assertEquals(" JSR X_I", code.get(++n));

  }

  @Test
  public void testExpressionWordFunctionMitVariableWordSplitArrayParameter() {
    Source source = new Source("x(n[2]) ");
    source.addVariable("X", Type.FUNCTION);
    source.addVariable("N", Type.WORD_SPLIT_ARRAY);

    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();

    int n = -1;
    Assert.assertEquals("; (5)", code.get(++n));
    Assert.assertEquals(" LDY #<2", code.get(++n));
    Assert.assertEquals(" LDX #>2", code.get(++n));

    Assert.assertEquals("; (11.2)", code.get(++n));
    Assert.assertEquals(" LDX N_HIGH,Y", code.get(++n));
    Assert.assertEquals(" LDA N_LOW,Y", code.get(++n));
    Assert.assertEquals(" TAY", code.get(++n));

    Assert.assertEquals("; (16)", code.get(++n));
    Assert.assertEquals(" TYA", code.get(++n));
    Assert.assertEquals(" LDY #1", code.get(++n));
    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));
    Assert.assertEquals(" TXA", code.get(++n));
    Assert.assertEquals(" INY", code.get(++n));
    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));

    Assert.assertEquals("; (14)", code.get(++n));
    Assert.assertEquals(" JSR X_I", code.get(++n));
  }

  @Test
  public void testExpressionPrintf2Parameter() {
    Source source = new Source("@printf(\"%s%d\\n\", 123) ");
    source.addVariable("@PRINTF", Type.FUNCTION);
    source.addVariable("\"%s%d\\n\"", Type.STRING_ANONYM);

    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();

    int n = -1;
    Assert.assertEquals("; (15)", code.get(++n));
    Assert.assertEquals(" LDY #<?STRING1", code.get(++n));
    Assert.assertEquals(" LDX #>?STRING1", code.get(++n));

    Assert.assertEquals("; (16)", code.get(++n));
    Assert.assertEquals(" TYA", code.get(++n));
    Assert.assertEquals(" LDY #1", code.get(++n)); // 1. Parameter
    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));
    Assert.assertEquals(" TXA", code.get(++n));
    Assert.assertEquals(" INY", code.get(++n));
    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));

    Assert.assertEquals("; (5)", code.get(++n));
    Assert.assertEquals(" LDY #<123", code.get(++n));
    Assert.assertEquals(" LDX #>123", code.get(++n));

    Assert.assertEquals("; (16)", code.get(++n));
    Assert.assertEquals(" TYA", code.get(++n));
    Assert.assertEquals(" LDY #3", code.get(++n)); // 2. Parameter
    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));
    Assert.assertEquals(" TXA", code.get(++n));
    Assert.assertEquals(" INY", code.get(++n));
    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));

    Assert.assertEquals("; (14)", code.get(++n));
    Assert.assertEquals(" JSR @PRINTF_II", code.get(++n));

  }

  @Test
  public void testFunctionInFunction() {
    Source source = new Source("@printf(\"Hallo\",x(1))");
    source.addVariable("@PRINTF", Type.FUNCTION);
    source.addVariable("X", Type.FUNCTION);

    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();

    int n = -1;
    Assert.assertEquals("; (15)", code.get(++n));
    Assert.assertEquals(" LDY #<?STRING2", code.get(++n));
    Assert.assertEquals(" LDX #>?STRING2", code.get(++n));

    Assert.assertEquals("; (16)", code.get(++n));
    Assert.assertEquals(" TYA", code.get(++n));
    Assert.assertEquals(" LDY #1", code.get(++n)); // 1. Parameter
    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));
    Assert.assertEquals(" TXA", code.get(++n));
    Assert.assertEquals(" INY", code.get(++n));
    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));

    Assert.assertEquals("; (17)", code.get(++n)); // heap ptr += 2
//    Assert.assertEquals(" ADD_TO_HEAP_PTR 3", code.get(++n));
    Assert.assertEquals(" CLC", code.get(++n));
    Assert.assertEquals(" LDA @HEAP_PTR", code.get(++n));
    Assert.assertEquals(" ADC #3", code.get(++n));
    Assert.assertEquals(" STA @HEAP_PTR", code.get(++n));
    Assert.assertEquals(" BCC *+4", code.get(++n));
    Assert.assertEquals(" INC @HEAP_PTR+1", code.get(++n));

    Assert.assertEquals("; (5)", code.get(++n));
    Assert.assertEquals(" LDY #<1", code.get(++n));
    Assert.assertEquals(" LDX #>1", code.get(++n));

    Assert.assertEquals("; (16)", code.get(++n));
    Assert.assertEquals(" TYA", code.get(++n));
    Assert.assertEquals(" LDY #1", code.get(++n)); // 1. Parameter X
    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));
    Assert.assertEquals(" TXA", code.get(++n));
    Assert.assertEquals(" INY", code.get(++n));
    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));

    Assert.assertEquals("; (14)", code.get(++n));
    Assert.assertEquals(" JSR X_I", code.get(++n));

    Assert.assertEquals("; (18)", code.get(++n)); // heap ptr -= 2
//    Assert.assertEquals(" SUB_FROM_HEAP_PTR 3", code.get(++n));
    Assert.assertEquals(" SEC", code.get(++n));
    Assert.assertEquals(" LDA @HEAP_PTR", code.get(++n));
    Assert.assertEquals(" SBC #3", code.get(++n));
    Assert.assertEquals(" STA @HEAP_PTR", code.get(++n));
    Assert.assertEquals(" BCS *+4", code.get(++n));
    Assert.assertEquals(" DEC @HEAP_PTR+1", code.get(++n));

    Assert.assertEquals("; (16)", code.get(++n));
    Assert.assertEquals(" TYA", code.get(++n));
    Assert.assertEquals(" LDY #3", code.get(++n)); // 2. Parameter
    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));
    Assert.assertEquals(" TXA", code.get(++n));
    Assert.assertEquals(" INY", code.get(++n));
    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));

    Assert.assertEquals("; (14)", code.get(++n));
    Assert.assertEquals(" JSR @PRINTF_II", code.get(++n));

  }

  @Test
  public void testFunctionInFunctionWithAnonymFunctionName() {
    Source source = new Source("@exit(@getAsserts())");

    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();

    int n = -1;
    Assert.assertEquals("; (14)", code.get(++n));
    Assert.assertEquals(" JSR @GETASSERTS", code.get(++n));
    Assert.assertEquals("; (16)", code.get(++n));
    Assert.assertEquals(" TYA", code.get(++n));
    Assert.assertEquals(" LDY #1", code.get(++n));
    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));
    Assert.assertEquals(" TXA", code.get(++n));
    Assert.assertEquals(" INY", code.get(++n));
    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));

    Assert.assertEquals("; (14)", code.get(++n));
    Assert.assertEquals(" JSR @EXIT_I", code.get(++n));
  }


  @Test
  public void testFunctionInFunctionInFunction() {
    Source source = new Source("@A(1,2,@B(3,@C(4)))");

    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();

    int n = -1;
    Assert.assertEquals("; (5)", code.get(++n));           // 1. parameter for @A
    Assert.assertEquals(" LDY #<1", code.get(++n));
    Assert.assertEquals(" LDX #>1", code.get(++n));

    Assert.assertEquals("; (16)", code.get(++n));
    Assert.assertEquals(" TYA", code.get(++n));
    Assert.assertEquals(" LDY #1", code.get(++n));
    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));
    Assert.assertEquals(" TXA", code.get(++n));
    Assert.assertEquals(" INY", code.get(++n));
    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));

    Assert.assertEquals("; (5)", code.get(++n));           // 2. parameter for @A
    Assert.assertEquals(" LDY #<2", code.get(++n));
    Assert.assertEquals(" LDX #>2", code.get(++n));

    Assert.assertEquals("; (16)", code.get(++n));
    Assert.assertEquals(" TYA", code.get(++n));
    Assert.assertEquals(" LDY #3", code.get(++n));
    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));
    Assert.assertEquals(" TXA", code.get(++n));
    Assert.assertEquals(" INY", code.get(++n));
    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));

    Assert.assertEquals("; (17)", code.get(++n));          // move heap_ptr + 5
//    Assert.assertEquals(" ADD_TO_HEAP_PTR 5", code.get(++n));
    Assert.assertEquals(" CLC", code.get(++n));
    Assert.assertEquals(" LDA @HEAP_PTR", code.get(++n));
    Assert.assertEquals(" ADC #5", code.get(++n));
    Assert.assertEquals(" STA @HEAP_PTR", code.get(++n));
    Assert.assertEquals(" BCC *+4", code.get(++n));
    Assert.assertEquals(" INC @HEAP_PTR+1", code.get(++n));

    Assert.assertEquals("; (5)", code.get(++n));           // 1. parameter for @B
    Assert.assertEquals(" LDY #<3", code.get(++n));
    Assert.assertEquals(" LDX #>3", code.get(++n));

    Assert.assertEquals("; (16)", code.get(++n));
    Assert.assertEquals(" TYA", code.get(++n));
    Assert.assertEquals(" LDY #1", code.get(++n));
    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));
    Assert.assertEquals(" TXA", code.get(++n));
    Assert.assertEquals(" INY", code.get(++n));
    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));

    Assert.assertEquals("; (17)", code.get(++n));          // move heap_ptr + 3
//    Assert.assertEquals(" ADD_TO_HEAP_PTR 3", code.get(++n));
    Assert.assertEquals(" CLC", code.get(++n));
    Assert.assertEquals(" LDA @HEAP_PTR", code.get(++n));
    Assert.assertEquals(" ADC #3", code.get(++n));
    Assert.assertEquals(" STA @HEAP_PTR", code.get(++n));
    Assert.assertEquals(" BCC *+4", code.get(++n));
    Assert.assertEquals(" INC @HEAP_PTR+1", code.get(++n));

    Assert.assertEquals("; (5)", code.get(++n));           // 1. parameter for @C
    Assert.assertEquals(" LDY #<4", code.get(++n));
    Assert.assertEquals(" LDX #>4", code.get(++n));

    Assert.assertEquals("; (16)", code.get(++n));
    Assert.assertEquals(" TYA", code.get(++n));
    Assert.assertEquals(" LDY #1", code.get(++n));
    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));
    Assert.assertEquals(" TXA", code.get(++n));
    Assert.assertEquals(" INY", code.get(++n));
    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));

    Assert.assertEquals("; (14)", code.get(++n));         // call @C
    Assert.assertEquals(" JSR @C_I", code.get(++n));

    Assert.assertEquals("; (18)", code.get(++n));         // move back heap ptr -= 3
//    Assert.assertEquals(" SUB_FROM_HEAP_PTR 3", code.get(++n));
    Assert.assertEquals(" SEC", code.get(++n));
    Assert.assertEquals(" LDA @HEAP_PTR", code.get(++n));
    Assert.assertEquals(" SBC #3", code.get(++n));
    Assert.assertEquals(" STA @HEAP_PTR", code.get(++n));
    Assert.assertEquals(" BCS *+4", code.get(++n));
    Assert.assertEquals(" DEC @HEAP_PTR+1", code.get(++n));

    Assert.assertEquals("; (16)", code.get(++n));         // result is 2. parameter for @B
    Assert.assertEquals(" TYA", code.get(++n));
    Assert.assertEquals(" LDY #3", code.get(++n));
    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));
    Assert.assertEquals(" TXA", code.get(++n));
    Assert.assertEquals(" INY", code.get(++n));
    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));

    Assert.assertEquals("; (14)", code.get(++n));         // call @B
    Assert.assertEquals(" JSR @B_II", code.get(++n));

    Assert.assertEquals("; (18)", code.get(++n));         // move back heap ptr -= 5
//    Assert.assertEquals(" SUB_FROM_HEAP_PTR 5", code.get(++n));
    Assert.assertEquals(" SEC", code.get(++n));
    Assert.assertEquals(" LDA @HEAP_PTR", code.get(++n));
    Assert.assertEquals(" SBC #5", code.get(++n));
    Assert.assertEquals(" STA @HEAP_PTR", code.get(++n));
    Assert.assertEquals(" BCS *+4", code.get(++n));
    Assert.assertEquals(" DEC @HEAP_PTR+1", code.get(++n));

    Assert.assertEquals("; (16)", code.get(++n));         // result is 3. parameter for @A
    Assert.assertEquals(" TYA", code.get(++n));
    Assert.assertEquals(" LDY #5", code.get(++n));
    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));
    Assert.assertEquals(" TXA", code.get(++n));
    Assert.assertEquals(" INY", code.get(++n));
    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));

    Assert.assertEquals("; (14)", code.get(++n));         // call @A
    Assert.assertEquals(" JSR @A_III", code.get(++n));
  }


  @Test
  public void testExpressionWordFunctionPtrOhneParameter() {
    Source source = new Source("@(x)() ");
    source.addVariable("X", Type.WORD);
    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();

    int n = -1;
    Assert.assertEquals("; (14 ptr)", code.get(++n));
    Assert.assertEquals(" LDY X", code.get(++n));
    Assert.assertEquals(" LDX X+1", code.get(++n));
    Assert.assertEquals(" JSR @FUNCTION_POINTER", code.get(++n));
  }

  @Test
  public void testExpressionWordFunctionPtrMitZahlParameter() {
    Source source = new Source("@(x)(43) ");
    source.addVariable("X", Type.FUNCTION);
    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();

    int n = -1;
    Assert.assertEquals("; (5)", code.get(++n));
    Assert.assertEquals(" LDY #<43", code.get(++n));
    Assert.assertEquals(" LDX #>43", code.get(++n));

    Assert.assertEquals("; (16)", code.get(++n));
    Assert.assertEquals(" TYA", code.get(++n));
    Assert.assertEquals(" LDY #1", code.get(++n));
    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));
    Assert.assertEquals(" TXA", code.get(++n));
    Assert.assertEquals(" INY", code.get(++n));
    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));

    Assert.assertEquals("; (14 ptr)", code.get(++n));
    Assert.assertEquals(" LDY X", code.get(++n));
    Assert.assertEquals(" LDX X+1", code.get(++n));
    Assert.assertEquals(" JSR @FUNCTION_POINTER", code.get(++n));
  }

  @Test
  public void testExpressionFunctionCall() {
    Source source = new Source("@paintbomb(oldxpos, 159 - oldypos)");
    source.addVariable("OLDXPOS", Type.WORD);
    source.addVariable("OLDYPOS", Type.WORD);
    source.addVariable("@PAINTBOMB", Type.PROCEDURE);

    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();

    int n = -1;
    Assert.assertEquals("; (6)", code.get(++n));
    Assert.assertEquals(" LDY OLDXPOS", code.get(++n));
    Assert.assertEquals(" LDX OLDXPOS+1", code.get(++n));
    Assert.assertEquals("; (16)", code.get(++n));
    Assert.assertEquals(" TYA", code.get(++n));
    Assert.assertEquals(" LDY #1", code.get(++n));
    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));
    Assert.assertEquals(" TXA", code.get(++n));
    Assert.assertEquals(" INY", code.get(++n));
    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));
    Assert.assertEquals("; (3)", code.get(++n));
    Assert.assertEquals(" SEC", code.get(++n));
    Assert.assertEquals(" LDA #<159", code.get(++n));
    Assert.assertEquals(" SBC OLDYPOS", code.get(++n));
    Assert.assertEquals(" TAY", code.get(++n));
    Assert.assertEquals(" LDA #>159", code.get(++n));
    Assert.assertEquals(" SBC OLDYPOS+1", code.get(++n));
    Assert.assertEquals(" TAX", code.get(++n));
    Assert.assertEquals("; (16)", code.get(++n));
    Assert.assertEquals(" TYA", code.get(++n));
    Assert.assertEquals(" LDY #3", code.get(++n));
    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));
    Assert.assertEquals(" TXA", code.get(++n));
    Assert.assertEquals(" INY", code.get(++n));
    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));
    Assert.assertEquals("; (14)", code.get(++n));
    Assert.assertEquals(" JSR @PAINTBOMB_II", code.get(++n));
  }


  @Test
  public void testExpressionFunctionCallWithByteVariableParameter() {
    Source source = new Source("x(n) ");
    source.addVariable("X", Type.FUNCTION);
    source.addVariable("N", Type.BYTE);

    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();

    int n = -1;
    Assert.assertEquals("; (6)", code.get(++n));
    Assert.assertEquals(" LDY N", code.get(++n));
    Assert.assertEquals(" LDX #0", code.get(++n));

    Assert.assertEquals("; (16)", code.get(++n));
    Assert.assertEquals(" TYA", code.get(++n));
    Assert.assertEquals(" LDY #1", code.get(++n));
    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));
    Assert.assertEquals(" TXA", code.get(++n));
    Assert.assertEquals(" INY", code.get(++n));
    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));

    Assert.assertEquals("; (14)", code.get(++n));
    Assert.assertEquals(" JSR X_I", code.get(++n));
  }

  @Test
  public void testExpressionFunctionCallWithInt8VariableParameter() {
    Source source = new Source("x(n) ");
    source.addVariable("X", Type.FUNCTION);
    source.addVariable("N", Type.INT8);

    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();

    int n = -1;
    Assert.assertEquals("; (6)", code.get(++n));
    Assert.assertEquals(" LDY N", code.get(++n));
    Assert.assertEquals(" CPY #$80", code.get(++n));
    Assert.assertEquals(" LDX #0", code.get(++n));
    Assert.assertEquals(" BCC *+4", code.get(++n));
    Assert.assertEquals(" LDX #$FF", code.get(++n));

    Assert.assertEquals("; (16)", code.get(++n));
    Assert.assertEquals(" TYA", code.get(++n));
    Assert.assertEquals(" LDY #1", code.get(++n));
    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));
    Assert.assertEquals(" TXA", code.get(++n));
    Assert.assertEquals(" INY", code.get(++n));
    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));

    Assert.assertEquals("; (14)", code.get(++n));
    Assert.assertEquals(" JSR X_I", code.get(++n));
  }

  @Test
  public void testExpressionFunctionCallUnknownParameter() {
    Source source = new Source("@fillbyte(0, adr:vertical, 52) ");
    source.addVariable("VERTICAL", Type.WORD_SPLIT_ARRAY);
    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();

    int n = -1;
    Assert.assertEquals("; (5)", code.get(++n));
    Assert.assertEquals(" LDY #<0", code.get(++n));
    Assert.assertEquals(" LDX #>0", code.get(++n));

    Assert.assertEquals("; (16)", code.get(++n));
    Assert.assertEquals(" TYA", code.get(++n));
    Assert.assertEquals(" LDY #1", code.get(++n));
    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));
    Assert.assertEquals(" TXA", code.get(++n));
    Assert.assertEquals(" INY", code.get(++n));
    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));

    Assert.assertEquals("; (13)", code.get(++n));
    Assert.assertEquals(" LDY #<VERTICAL", code.get(++n));
    Assert.assertEquals(" LDX #>VERTICAL", code.get(++n));

    Assert.assertEquals("; (16)", code.get(++n));
    Assert.assertEquals(" TYA", code.get(++n));
    Assert.assertEquals(" LDY #3", code.get(++n));
    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));
    Assert.assertEquals(" TXA", code.get(++n));
    Assert.assertEquals(" INY", code.get(++n));
    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));

    Assert.assertEquals("; (5)", code.get(++n));
    Assert.assertEquals(" LDY #<52", code.get(++n));
    Assert.assertEquals(" LDX #>52", code.get(++n));

    Assert.assertEquals("; (16)", code.get(++n));
    Assert.assertEquals(" TYA", code.get(++n));
    Assert.assertEquals(" LDY #5", code.get(++n));
    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));
    Assert.assertEquals(" TXA", code.get(++n));
    Assert.assertEquals(" INY", code.get(++n));
    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));

    Assert.assertEquals("; (14)", code.get(++n));
    Assert.assertEquals(" JSR @FILLBYTE_III", code.get(++n));

    Assert.assertEquals(n + 1, code.size());
  }


  @Test
  public void testExpressionPrintf2ParameterStringUint16() {
    Source source = new Source("@printf(\"%s%D\\n\", u16) ");
    source.addVariable("@PRINTF", Type.FUNCTION);
    source.addVariable("\"%s%D\\n\"", Type.STRING_ANONYM);
    source.addVariable("U16", Type.UINT16);

    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();

    int n = -1;
    Assert.assertEquals("; (15)", code.get(++n));
    Assert.assertEquals(" LDY #<?STRING1", code.get(++n));
    Assert.assertEquals(" LDX #>?STRING1", code.get(++n));

    Assert.assertEquals("; (16)", code.get(++n));
    Assert.assertEquals(" TYA", code.get(++n));
    Assert.assertEquals(" LDY #1", code.get(++n)); // 1. Parameter
    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));
    Assert.assertEquals(" TXA", code.get(++n));
    Assert.assertEquals(" INY", code.get(++n));
    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));

    Assert.assertEquals("; (6)", code.get(++n));
    Assert.assertEquals(" LDY U16", code.get(++n));
    Assert.assertEquals(" LDX U16+1", code.get(++n));

    Assert.assertEquals("; (16)", code.get(++n));
    Assert.assertEquals(" TYA", code.get(++n));
    Assert.assertEquals(" LDY #3", code.get(++n)); // 2. Parameter
    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));
    Assert.assertEquals(" TXA", code.get(++n));
    Assert.assertEquals(" INY", code.get(++n));
    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));

    Assert.assertEquals("; (14)", code.get(++n));
    Assert.assertEquals(" JSR @PRINTF_II", code.get(++n));

  }

  @Test
  public void testExpressionAbsoluteWord() {
    Source source = new Source("abs:x ");
    source.addVariable("X", Type.WORD);
    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();

    int n = -1;
    Assert.assertEquals("; (19b)", code.get(++n));
    Assert.assertEquals(" LDX X+1", code.get(++n));
    Assert.assertEquals(" BPL ?ABS_POSITIVE1", code.get(++n));
    Assert.assertEquals(" SEC", code.get(++n));
    Assert.assertEquals(" LDA #0", code.get(++n));
    Assert.assertEquals(" SBC X", code.get(++n));
    Assert.assertEquals(" TAY", code.get(++n));
    Assert.assertEquals(" LDA #0", code.get(++n));
    Assert.assertEquals(" SBC X+1", code.get(++n));
    Assert.assertEquals(" TAX", code.get(++n));
    Assert.assertEquals(" JMP ?ABS_WAS_NEGATIVE1", code.get(++n));
    Assert.assertEquals("?ABS_POSITIVE1", code.get(++n));
    Assert.assertEquals(" LDY X", code.get(++n));
    Assert.assertEquals("?ABS_WAS_NEGATIVE1", code.get(++n));

    Assert.assertEquals(14, code.size());
  }

  @Test
  public void testExpressionAbsoluteInt8() {
    Source source = new Source("abs:x ");
    source.addVariable("X", Type.INT8);
    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.INT8;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();

    int n = -1;
    Assert.assertEquals("; (19c)", code.get(++n));
    Assert.assertEquals(" LDY X", code.get(++n));
    Assert.assertEquals(" BPL ?ABS_POSITIVE1", code.get(++n));
    Assert.assertEquals(" SEC", code.get(++n));
    Assert.assertEquals(" LDA #0", code.get(++n));
    Assert.assertEquals(" SBC X", code.get(++n));
    Assert.assertEquals(" TAY", code.get(++n));
    Assert.assertEquals("?ABS_POSITIVE1", code.get(++n));

    Assert.assertEquals(8, code.size());
  }

  @Test
  public void testExpressionHiByteOfWordAccess() {
    Source source = new Source("hi:x ");
    source.addVariable("X", Type.WORD);
    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.BYTE;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();

    int n = -1;
    Assert.assertEquals("; (20)", code.get(++n));
    Assert.assertEquals(" LDY X+1", code.get(++n));
//    Assert.assertEquals(" LDX #0", code.get(++n));
    Assert.assertEquals(2, code.size());
  }

  @Test
  public void testExpressionHiByteOfConstAccess() {
    Source source = new Source("hi:CONST_VALUE ");
    source.addVariable("CONST_VALUE", Type.CONST);
    source.setVariableAddress("CONST_VALUE", "123");

    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.BYTE;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();

    int n = -1;
    Assert.assertEquals("; (20)", code.get(++n));
    Assert.assertEquals(" LDY #>CONST_VALUE", code.get(++n));
//    Assert.assertEquals(" LDX #0", code.get(++n));
    Assert.assertEquals(2, code.size());
  }

  @Test
  public void testExpressionHiByteOfUint16Access() {
    Source source = new Source("hi:x ");
    source.addVariable("X", Type.UINT16);
    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.BYTE;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();

    int n = -1;
    Assert.assertEquals("; (20)", code.get(++n));
    Assert.assertEquals(" LDY X+1", code.get(++n));
//    Assert.assertEquals(" LDX #0", code.get(++n));
    Assert.assertEquals(2, code.size());
  }

  @Test(expected = IllegalStateException.class)
  public void testExpressionHiByteOfByteAccess() {
    Source source = new Source("hi:x ");
    source.addVariable("X", Type.BYTE);
    /*List<Integer> p_code = */ getPCodeOf(source); // Must throw here due to wrong size of variable
  }

  @Test(expected = IllegalStateException.class)
  public void testExpressionHSPFunctionCallWithoutParameter() {
    Source source = new Source("@hsp_noParameter() ");
    List<Integer> p_code = getPCodeOf(source);

//    Type ergebnis = Type.WORD;
//    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);
//
//    pcodeGenerator.build();
  }

  @Test
  public void testExpressionHSPFunctionCallWithByteVariableParameter() {
    Source source = new Source("@hsp_x(n) ");
    source.addVariable("DUMP1", Type.BYTE);
    source.addVariable("DUMP2", Type.WORD);

    source.addVariable("@HSP_X", Type.FUNCTION);
    source.addVariable("N", Type.BYTE);

    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();

    int n = -1;
    Assert.assertEquals("; (6)", code.get(++n));
    Assert.assertEquals(" LDY N", code.get(++n));
    Assert.assertEquals(" LDX #0", code.get(++n));

    Assert.assertEquals("; (16b)", code.get(++n));
    Assert.assertEquals(" STY @HSP_PARAM+1", code.get(++n));
    Assert.assertEquals(" TXA", code.get(++n));
    Assert.assertEquals(" STA @HSP_PARAM+2", code.get(++n));

    Assert.assertEquals("; (14)", code.get(++n));
    Assert.assertEquals(" JSR @HSP_X_I", code.get(++n));
  }

  @Test(expected = IllegalStateException.class)
  public void testExpressionHSPFunctionCallWithInnerHSPFunctionCall() {
    Source source = new Source("@hsp_noParameter(@hsp_innerCall(b)) ");
    source.addVariable("B", Type.BYTE);

    List<Integer> p_code = getPCodeOf(source);

//    Type ergebnis = Type.WORD;
//    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);
//
//    pcodeGenerator.build();
  }

@Test
  public void testExpressionWordFunctionMitVariableParameterStoreHeapPtrFunction() {
    Source source = new Source("x(n) ");
    source.getOptions().setUseStoreHeapPtrFunction(true);

    source.addVariable("X", Type.FUNCTION);
    source.addVariable("N", Type.WORD);

    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();

    int n = -1;
    Assert.assertEquals("; (6)", code.get(++n));
    Assert.assertEquals(" LDY N", code.get(++n));
    Assert.assertEquals(" LDX N+1", code.get(++n));

    Assert.assertEquals("; (16c)", code.get(++n));
    Assert.assertEquals(" JSR @STORETOHEAP1", code.get(++n));

    Assert.assertEquals("; (14)", code.get(++n));
    Assert.assertEquals(" JSR X_I", code.get(++n));
  }

@Test
public void testFunctionInFunctionInFunctionStoreHeapPtrFunction() {
  Source source = new Source("@A(1,2,@B(3,@C(4)))");
  source.getOptions().setUseStoreHeapPtrFunction(true);
  List<Integer> p_code = getPCodeOf(source);

  Type ergebnis = Type.WORD;
  PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

  pcodeGenerator.build();
  List<String> code = source.getCode();

  int n = -1;
  Assert.assertEquals("; (5)", code.get(++n));           // 1. parameter for @A
  Assert.assertEquals(" LDY #<1", code.get(++n));
  Assert.assertEquals(" LDX #>1", code.get(++n));

  Assert.assertEquals("; (16c)", code.get(++n));
  Assert.assertEquals(" JSR @STORETOHEAP1", code.get(++n));

  Assert.assertEquals("; (5)", code.get(++n));           // 2. parameter for @A
  Assert.assertEquals(" LDY #<2", code.get(++n));
  Assert.assertEquals(" LDX #>2", code.get(++n));

  Assert.assertEquals("; (16c)", code.get(++n));
  Assert.assertEquals(" JSR @STORETOHEAP3", code.get(++n));

  Assert.assertEquals("; (17)", code.get(++n));          // move heap_ptr + 5
//  Assert.assertEquals(" ADD_TO_HEAP_PTR 5", code.get(++n));
  Assert.assertEquals(" CLC", code.get(++n));
  Assert.assertEquals(" LDA @HEAP_PTR", code.get(++n));
  Assert.assertEquals(" ADC #5", code.get(++n));
  Assert.assertEquals(" STA @HEAP_PTR", code.get(++n));
  Assert.assertEquals(" BCC *+4", code.get(++n));
  Assert.assertEquals(" INC @HEAP_PTR+1", code.get(++n));

  Assert.assertEquals("; (5)", code.get(++n));           // 1. parameter for @B
  Assert.assertEquals(" LDY #<3", code.get(++n));
  Assert.assertEquals(" LDX #>3", code.get(++n));

  Assert.assertEquals("; (16c)", code.get(++n));
  Assert.assertEquals(" JSR @STORETOHEAP1", code.get(++n));

  Assert.assertEquals("; (17)", code.get(++n));          // move heap_ptr + 3
//  Assert.assertEquals(" ADD_TO_HEAP_PTR 3", code.get(++n));
  Assert.assertEquals(" CLC", code.get(++n));
  Assert.assertEquals(" LDA @HEAP_PTR", code.get(++n));
  Assert.assertEquals(" ADC #3", code.get(++n));
  Assert.assertEquals(" STA @HEAP_PTR", code.get(++n));
  Assert.assertEquals(" BCC *+4", code.get(++n));
  Assert.assertEquals(" INC @HEAP_PTR+1", code.get(++n));

  Assert.assertEquals("; (5)", code.get(++n));           // 1. parameter for @C
  Assert.assertEquals(" LDY #<4", code.get(++n));
  Assert.assertEquals(" LDX #>4", code.get(++n));

  Assert.assertEquals("; (16c)", code.get(++n));
  Assert.assertEquals(" JSR @STORETOHEAP1", code.get(++n));

  Assert.assertEquals("; (14)", code.get(++n));         // call @C
  Assert.assertEquals(" JSR @C_I", code.get(++n));

  Assert.assertEquals("; (18)", code.get(++n));         // move back heap ptr -= 3
//  Assert.assertEquals(" SUB_FROM_HEAP_PTR 3", code.get(++n));
  Assert.assertEquals(" SEC", code.get(++n));
  Assert.assertEquals(" LDA @HEAP_PTR", code.get(++n));
  Assert.assertEquals(" SBC #3", code.get(++n));
  Assert.assertEquals(" STA @HEAP_PTR", code.get(++n));
  Assert.assertEquals(" BCS *+4", code.get(++n));
  Assert.assertEquals(" DEC @HEAP_PTR+1", code.get(++n));

  Assert.assertEquals("; (16c)", code.get(++n));         // result is 2. parameter for @B
  Assert.assertEquals(" JSR @STORETOHEAP3", code.get(++n));

  Assert.assertEquals("; (14)", code.get(++n));         // call @B
  Assert.assertEquals(" JSR @B_II", code.get(++n));

  Assert.assertEquals("; (18)", code.get(++n));         // move back heap ptr -= 5
//  Assert.assertEquals(" SUB_FROM_HEAP_PTR 5", code.get(++n));
  Assert.assertEquals(" SEC", code.get(++n));
  Assert.assertEquals(" LDA @HEAP_PTR", code.get(++n));
  Assert.assertEquals(" SBC #5", code.get(++n));
  Assert.assertEquals(" STA @HEAP_PTR", code.get(++n));
  Assert.assertEquals(" BCS *+4", code.get(++n));
  Assert.assertEquals(" DEC @HEAP_PTR+1", code.get(++n));

  Assert.assertEquals("; (16c)", code.get(++n));         // result is 3. parameter for @A
  Assert.assertEquals(" JSR @STORETOHEAP5", code.get(++n));

  Assert.assertEquals("; (14)", code.get(++n));         // call @A
  Assert.assertEquals(" JSR @A_III", code.get(++n));
}


}
