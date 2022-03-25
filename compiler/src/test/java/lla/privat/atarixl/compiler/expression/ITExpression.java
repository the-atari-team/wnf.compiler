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
  public void testExpression() {
    Source source = new Source("123 ");

    expressionSUT = new Expression(source);

    Symbol symbol = source.nextElement();
    symbol = expressionSUT.expression(symbol).build();

    Assert.assertEquals(SymbolEnum.noSymbol, symbol.getId());

    Assert.assertTrue(expressionSUT.isPrecalculationPossible());
  }

}
