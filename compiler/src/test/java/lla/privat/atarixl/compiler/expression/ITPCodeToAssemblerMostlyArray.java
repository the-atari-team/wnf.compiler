// cdw by 'The Atari Team' 2020
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler.expression;

import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import lla.privat.atarixl.compiler.Symbol;
import lla.privat.atarixl.compiler.SymbolEnum;
import lla.privat.atarixl.compiler.source.Source;

public class ITPCodeToAssemblerMostlyArray {

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
  public void testExpressionByteArray() {
    Source source = new Source("x[1] ");
    source.addVariable("X", Type.BYTE_ARRAY);
    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    new PCodeToAssembler(source, p_code, ergebnis).build();

    List<String> code = source.getCode();

    int n=-1;
    Assert.assertEquals("; (5)", code.get(++n));
    Assert.assertEquals(" LDY #<1", code.get(++n));
    Assert.assertEquals(" LDX #>1", code.get(++n));
    Assert.assertEquals("; (12)", code.get(++n));
    Assert.assertEquals(" LDA X,Y", code.get(++n));
    Assert.assertEquals(" TAY", code.get(++n));
    Assert.assertEquals(" LDX #0", code.get(++n));
  }

  @Test
  public void testExpressionWordArray() {
    Source source = new Source("x[1] ");
    source.addVariable("X", Type.WORD_ARRAY);
    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();

    int n=-1;
    Assert.assertEquals("; (5)", code.get(++n));
    Assert.assertEquals(" LDY #<1", code.get(++n));
    Assert.assertEquals(" LDX #>1", code.get(++n));
    Assert.assertEquals("; (11)", code.get(++n));
    Assert.assertEquals(" TYA", code.get(++n));
    Assert.assertEquals(" GETARRAYW X", code.get(++n));
  }

  @Test
  public void testExpressionMitVariableByteArray() {
    Source source = new Source("m[4]*16 ");
    source.addVariable("M", Type.BYTE_ARRAY);

    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code =source.getCode();

    int n=-1;
    Assert.assertEquals("; (5)", code.get(++n));
    Assert.assertEquals(" LDY #<4", code.get(++n));
    Assert.assertEquals(" LDX #>4", code.get(++n));

    Assert.assertEquals("; (12)", code.get(++n));
    Assert.assertEquals(" LDA M,Y", code.get(++n));
    Assert.assertEquals(" TAY", code.get(++n));
    Assert.assertEquals(" LDX #0", code.get(++n));

    Assert.assertEquals("; (9)", code.get(++n));
    Assert.assertEquals(" TYA", code.get(++n));
    Assert.assertEquals(" STX @OP", code.get(++n));
    Assert.assertEquals(" ASL A", code.get(++n));   // * 2
    Assert.assertEquals(" ROL @OP", code.get(++n));
    Assert.assertEquals(" ASL A", code.get(++n));   // * 2
    Assert.assertEquals(" ROL @OP", code.get(++n));
    Assert.assertEquals(" ASL A", code.get(++n));   // * 2
    Assert.assertEquals(" ROL @OP", code.get(++n));
    Assert.assertEquals(" ASL A", code.get(++n));   // * 2
    Assert.assertEquals(" ROL @OP", code.get(++n));
    Assert.assertEquals(" TAY", code.get(++n));
    Assert.assertEquals(" LDX @OP", code.get(++n));
  }

  @Test
  public void testExpressionMitVariableByteArray2() {
    Source source = new Source("m[4]*X ");
    source.addVariable("M", Type.BYTE_ARRAY);
    source.addVariable("X", Type.WORD);

    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code =source.getCode();

    int n=-1;
    Assert.assertEquals("; (5)", code.get(++n));
    Assert.assertEquals(" LDY #<4", code.get(++n));
    Assert.assertEquals(" LDX #>4", code.get(++n));

    Assert.assertEquals("; (12)", code.get(++n));
    Assert.assertEquals(" LDA M,Y", code.get(++n));
    Assert.assertEquals(" TAY", code.get(++n));
    Assert.assertEquals(" LDX #0", code.get(++n));

    Assert.assertEquals("; (9)", code.get(++n));
    Assert.assertEquals(" LDA X", code.get(++n));
    Assert.assertEquals(" STA @OP", code.get(++n));
    Assert.assertEquals(" LDA X+1", code.get(++n));   // * 2
    Assert.assertEquals(" STA @OP+1", code.get(++n));
    Assert.assertEquals(" JSR @IMULT", code.get(++n));   // * 2
  }

  @Ignore("TODO: function calls not ready yet!")
  @Test
  public void testExpressionFunctionCalls() {
    Source source = new Source("f(x(2), y(3)) ");
    source.addVariable("F", Type.FUNCTION);
    source.addVariable("X", Type.FUNCTION);
    source.addVariable("Y", Type.FUNCTION);

    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code =source.getCode();

    int n=-1;
    Assert.assertEquals("; (5)", code.get(++n));
    Assert.assertEquals(" LDY #<2", code.get(++n));
    Assert.assertEquals(" LDX #>2", code.get(++n));
    Assert.assertEquals("; (14)", code.get(++n));
    Assert.assertEquals(" TYA", code.get(++n));
    Assert.assertEquals(" LDY #1", code.get(++n));
    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));
    Assert.assertEquals(" TXA", code.get(++n));
    Assert.assertEquals(" INY", code.get(++n));
    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));
    Assert.assertEquals(" JSR X", code.get(++n));
    Assert.assertEquals("; (5)", code.get(++n));
    Assert.assertEquals(" LDY #<3", code.get(++n));
    Assert.assertEquals(" LDX #>3", code.get(++n));
    Assert.assertEquals("; (14)", code.get(++n));
    Assert.assertEquals(" TYA", code.get(++n));
    Assert.assertEquals(" LDY #1", code.get(++n));
    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));
    Assert.assertEquals(" TXA", code.get(++n));
    Assert.assertEquals(" INY", code.get(++n));
    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));
    Assert.assertEquals(" JSR Y", code.get(++n));
    Assert.assertEquals("; (14)", code.get(++n));
    Assert.assertEquals(" TYA", code.get(++n));
    Assert.assertEquals(" LDY #1", code.get(++n));
    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));
    Assert.assertEquals(" TXA", code.get(++n));
    Assert.assertEquals(" INY", code.get(++n));
    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));
    Assert.assertEquals(" JSR F", code.get(++n));

  }
}
