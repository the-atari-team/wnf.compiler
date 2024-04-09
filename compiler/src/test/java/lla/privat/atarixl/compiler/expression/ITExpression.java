// cdw by 'The Atari Team' 2022
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler.expression;

import org.junit.Assert;
import org.junit.Test;

import lla.privat.atarixl.compiler.Symbol;
import lla.privat.atarixl.compiler.SymbolEnum;
import lla.privat.atarixl.compiler.source.Source;

public class ITExpression {
  private Expression expressionSUT;

  @Test
  public void testExpressionSingleInteger() {
    Source source = new Source("123 ");

    expressionSUT = new Expression(source);

    Symbol symbol = source.nextElement();
    symbol = expressionSUT.expression(symbol).build();

    Assert.assertEquals(SymbolEnum.noSymbol, symbol.getId());

  }

  @Test
  public void testExpressionAdditionWithByTwoSingleIntegers() {
    Source source = new Source("123+1 ");

    expressionSUT = new Expression(source);

    Symbol symbol = source.nextElement();
    symbol = expressionSUT.expression(symbol).build();

    Assert.assertEquals(SymbolEnum.noSymbol, symbol.getId());
  }

  @Test
  public void testExpressionAdditionWithByThreeSingleIntegers() {
    Source source = new Source("1+2+3 ");

    expressionSUT = new Expression(source);

    Symbol symbol = source.nextElement();
    symbol = expressionSUT.expression(symbol).build();

    Assert.assertEquals(SymbolEnum.noSymbol, symbol.getId());
  }

  @Test
  public void testExpressionSingleVariable() {
    Source source = new Source("x ");
    source.addVariable("X", Type.BYTE);

    expressionSUT = new Expression(source);

    Symbol symbol = source.nextElement();
    symbol = expressionSUT.expression(symbol).build();

    Assert.assertEquals(SymbolEnum.noSymbol, symbol.getId());
  }
  
  @Test
  public void testExpressionAdditionWithSingleIntegerAndVariable() {
    Source source = new Source("123+x ");
    source.addVariable("X", Type.BYTE);

    expressionSUT = new Expression(source);

    Symbol symbol = source.nextElement();
    symbol = expressionSUT.expression(symbol).build();

    Assert.assertEquals(SymbolEnum.noSymbol, symbol.getId());
  }

}
