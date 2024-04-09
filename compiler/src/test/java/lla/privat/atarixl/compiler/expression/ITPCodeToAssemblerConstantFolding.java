package lla.privat.atarixl.compiler.expression;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import lla.privat.atarixl.compiler.Symbol;
import lla.privat.atarixl.compiler.SymbolEnum;
import lla.privat.atarixl.compiler.source.Source;

public class ITPCodeToAssemblerConstantFolding {

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

  // Test when Parameter is byte
  @Test
  public void testExpressionAddTwoIntValues() {
    Source source = new Source("1 + 2 ");
    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();

    List<String> code = source.getCode();
    int n = -1;

    if (source.useCFOptimisation()) {
      Assert.assertEquals("; (5)", code.get(++n));
      Assert.assertEquals(" LDY #<3", code.get(++n));
      Assert.assertEquals(" LDX #>3", code.get(++n));
    }
    else {
      Assert.assertEquals("; (3)", code.get(++n));
      Assert.assertEquals(" CLC", code.get(++n));
      Assert.assertEquals(" LDA #<1", code.get(++n));
      Assert.assertEquals(" ADC #<2", code.get(++n));
      Assert.assertEquals(" TAY", code.get(++n));
      Assert.assertEquals(" LDA #>1", code.get(++n));
      Assert.assertEquals(" ADC #>2", code.get(++n));
      Assert.assertEquals(" TAX", code.get(++n));
    }

  }

  @Test
  public void testExpressionAddTreeIntValues() {
    Source source = new Source("1 + 2 +3 ");
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
      // TODO: this is wrong code!
      Assert.assertEquals("; (3)", code.get(++n));
      Assert.assertEquals(" CLC", code.get(++n));
      Assert.assertEquals(" LDA #<1", code.get(++n));
      Assert.assertEquals(" ADC #<2", code.get(++n));
      Assert.assertEquals(" TAY", code.get(++n));
      Assert.assertEquals(" LDA #>1", code.get(++n));
      Assert.assertEquals(" ADC #>2", code.get(++n));
      Assert.assertEquals(" TAX", code.get(++n));
    }

  }


  @Test
  public void testExpressionAddSubTreeIntValues() {
    Source source = new Source("1 - 2 + 3 ");
    source.setVerboseLevel(3);
    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();

    List<String> code = source.getCode();
    int n = -1;

    if (source.useCFOptimisation()) {
      Assert.assertEquals("; (5)", code.get(++n));
      Assert.assertEquals(" LDY #<2", code.get(++n));
      Assert.assertEquals(" LDX #>2", code.get(++n));
    }
    else {
      // TODO: this is wrong code!
      Assert.assertEquals("; (3)", code.get(++n));
      Assert.assertEquals(" CLC", code.get(++n));
      Assert.assertEquals(" LDA #<1", code.get(++n));
      Assert.assertEquals(" ADC #<2", code.get(++n));
      Assert.assertEquals(" TAY", code.get(++n));
      Assert.assertEquals(" LDA #>1", code.get(++n));
      Assert.assertEquals(" ADC #>2", code.get(++n));
      Assert.assertEquals(" TAX", code.get(++n));
    }

  }

  @Test
  public void testExpressionSixMinusTwoPlusThreeIntValues() {
    Source source = new Source("6 - (2 + 3) ");
    source.setVerboseLevel(3);
    
    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();

    List<String> code = source.getCode();
    int n = -1;

    if (source.useCFOptimisation()) {
      Assert.assertEquals("; (5)", code.get(++n));
      Assert.assertEquals(" LDY #<1", code.get(++n));
      Assert.assertEquals(" LDX #>1", code.get(++n));      
    }
    else {
      // TODO: this is wrong code!
      Assert.assertEquals("; (3)", code.get(++n));
      Assert.assertEquals(" CLC", code.get(++n));
      Assert.assertEquals(" LDA #<1", code.get(++n));
      Assert.assertEquals(" ADC #<2", code.get(++n));
      Assert.assertEquals(" TAY", code.get(++n));
      Assert.assertEquals(" LDA #>1", code.get(++n));
      Assert.assertEquals(" ADC #>2", code.get(++n));
      Assert.assertEquals(" TAX", code.get(++n));
    }

  }

  @Test
  public void testExpressionAddSubFourIntValues() {
    Source source = new Source("210 -1 -2 -3 -4 -5-6-7-8-9-10-11-12-13-14-15-16-17-18-19-20 ");
    source.setVerboseLevel(3);
    List<Integer> p_code = getPCodeOf(source);

    Type ergebnis = Type.WORD;
    PCodeToAssembler pcodeGenerator = new PCodeToAssembler(source, p_code, ergebnis);

    pcodeGenerator.build();

    List<String> code = source.getCode();
    int n = -1;

    if (source.useCFOptimisation()) {
      Assert.assertEquals("; (5)", code.get(++n));
      Assert.assertEquals(" LDY #<0", code.get(++n));
      Assert.assertEquals(" LDX #>0", code.get(++n));
    }
    else {
      // TODO: this is wrong code!
      Assert.assertEquals("; (3)", code.get(++n));
      Assert.assertEquals(" CLC", code.get(++n));
      Assert.assertEquals(" LDA #<1", code.get(++n));
      Assert.assertEquals(" ADC #<2", code.get(++n));
      Assert.assertEquals(" TAY", code.get(++n));
      Assert.assertEquals(" LDA #>1", code.get(++n));
      Assert.assertEquals(" ADC #>2", code.get(++n));
      Assert.assertEquals(" TAX", code.get(++n));
    }
  }


}
