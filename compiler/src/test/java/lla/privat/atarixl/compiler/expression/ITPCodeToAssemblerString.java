// cdw by 'The Atari Team' 2022
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler.expression;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import lla.privat.atarixl.compiler.Symbol;
import lla.privat.atarixl.compiler.SymbolEnum;
import lla.privat.atarixl.compiler.source.Source;

public class ITPCodeToAssemblerString {
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
    Assert.assertEquals(3, code.size());
  }

  // Test: adr:'Hallo Welt' returns the address of the given String
  // TODO: Warning should shown, 'adr:' is überflüssig
  @Test
  public void testExpressionWordAddressString() {
    Source source = new Source("adr:'Hallo Welt' ");
    source.addVariable("n", Type.WORD);
    source.addVariable("'Hallo Welt'", Type.STRING_ANONYM);
    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    List<String> code = source.getCode();

    int n = -1;
    Assert.assertEquals("; (13)", code.get(++n));
    Assert.assertEquals(" LDY #<?STRING1", code.get(++n));
    Assert.assertEquals(" LDX #>?STRING1", code.get(++n));
    Assert.assertEquals(3, code.size());
  }


  @Test
  public void testExpressionString() {
    Source source = new Source("\"Hallo Welt\" ");
    source.addVariable("n", Type.WORD);
    source.addVariable("\"Hallo Welt\"", Type.STRING_ANONYM);
    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    source.generateVariables();
    List<String> code = source.getCode();

    int n = -1;
    Assert.assertEquals("; (15)", code.get(++n));
    Assert.assertEquals(" LDY #<?STRING1", code.get(++n));
    Assert.assertEquals(" LDX #>?STRING1", code.get(++n));
    
    Assert.assertEquals("n .WORD 0", code.get(++n));
    Assert.assertEquals("?STRING1", code.get(++n));
    Assert.assertEquals(" .BYTE \"Hallo Welt\",255", code.get(++n));
    
    Assert.assertEquals(6, code.size());    
  }
  
  @Test
  public void testExpressionStringWithEnter() {
    Source source = new Source("\"ABC\n\" ");
    source.addVariable("\"ABC\n\"", Type.STRING_ANONYM);
    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    source.generateVariables();
    List<String> code = source.getCode();

    int n = -1;
    Assert.assertEquals("; (15)", code.get(++n));
    Assert.assertEquals(" LDY #<?STRING0", code.get(++n));
    Assert.assertEquals(" LDX #>?STRING0", code.get(++n));
    
    Assert.assertEquals("?STRING0", code.get(++n));
    Assert.assertEquals(" .BYTE \"ABC\n\",255", code.get(++n)); // \n will NOT interpreted here!
    
    Assert.assertEquals(5, code.size());
  }

  @Test
  public void testExpressionSingleSignSingleQuoteString() {
    Source source = new Source("'A' ");
    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    source.generateVariables();
    List<String> code = source.getCode();

    int n = -1;
    Assert.assertEquals("; (5)", code.get(++n));
    Assert.assertEquals(" LDY #<65", code.get(++n));
    Assert.assertEquals(" LDX #>65", code.get(++n));  // Weil ergebnis = Type.WORD !  
    Assert.assertEquals(3, code.size());
  }
  
  @Test
  public void testExpressionSingleSignSingleQuoteStringAsByte() {
    Source source = new Source("'A' ");
    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.BYTE;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    source.generateVariables();
    List<String> code = source.getCode();

    int n = -1;
    Assert.assertEquals("; (5)", code.get(++n));
    Assert.assertEquals(" LDY #<65", code.get(++n));
    Assert.assertEquals(2, code.size());
  }

  // TODO:
  @Test
  public void testExpressionSingleSignDoubleQuoteString() {
    Source source = new Source("\"H\" ");
    source.addVariable("\"H\"", Type.STRING_ANONYM);
    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();
    source.generateVariables();
    List<String> code = source.getCode();

    int n = -1;
    Assert.assertEquals("; (15)", code.get(++n));
    Assert.assertEquals(" LDY #<?STRING0", code.get(++n));
    Assert.assertEquals(" LDX #>?STRING0", code.get(++n));
    
    Assert.assertEquals("?STRING0", code.get(++n));
    Assert.assertEquals(" .BYTE \"H\",255", code.get(++n));
    
    Assert.assertEquals(5, code.size());
  }

}
