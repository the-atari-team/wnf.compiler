// cdw by 'The Atari Team' 2020
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
  }

  @Test
  public void testExpressionString() {
    Source source = new Source("'Hallo Welt' ");
    source.addVariable("X", Type.WORD);
    source.addVariable("'Hallo Welt'", Type.STRING);
    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();

    int n = -1;
    Assert.assertEquals("; (15)", code.get(++n));
    Assert.assertEquals(" LDY #<?STRING1", code.get(++n));
    Assert.assertEquals(" LDX #>?STRING1", code.get(++n));
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
    Source source = new Source("@printf('%s%d\\n', 123) ");
    source.addVariable("@PRINTF", Type.FUNCTION);
    source.addVariable("'%s%d\\n'", Type.STRING);

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
    Source source = new Source("@printf('Hallo',x(1))");
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
    Assert.assertEquals(" ADD_TO_HEAP_PTR 3", code.get(++n));

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
    Assert.assertEquals(" SUB_FROM_HEAP_PTR 3", code.get(++n));

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
    Assert.assertEquals(" ADD_TO_HEAP_PTR 5", code.get(++n));

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
    Assert.assertEquals(" ADD_TO_HEAP_PTR 3", code.get(++n));

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
    Assert.assertEquals(" SUB_FROM_HEAP_PTR 3", code.get(++n));

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
    Assert.assertEquals(" SUB_FROM_HEAP_PTR 5", code.get(++n));

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
}
