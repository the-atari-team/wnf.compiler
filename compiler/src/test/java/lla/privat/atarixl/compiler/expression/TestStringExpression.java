package lla.privat.atarixl.compiler.expression;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import lla.privat.atarixl.compiler.Symbol;
import lla.privat.atarixl.compiler.SymbolEnum;
import lla.privat.atarixl.compiler.source.Source;

public class TestStringExpression {

  private Expression expressionSUT;

  @Before
  public void setUp() {

  }

  private void setupWordExpression(Source source) {
    expressionSUT = new Expression(source);
    Symbol symbol = source.nextElement();
    symbol = expressionSUT.expression(symbol).getLastSymbol();

    Assert.assertEquals(SymbolEnum.noSymbol, symbol.getId());
  }
  
  
  // Test 'A', single length will replaced as an integer value
  // TODO: Maybe 'C-a' to support ctrl-a due to the fact, PC do not support Atari ATASCII Ctrl Chars.
  @Test
  public void testExpressionWithSingleQuoteString() {
    Source source = new Source("'A'");

    setupWordExpression(source);
    Assert.assertEquals("160 65", expressionSUT.joinedPCode());

    expressionSUT.optimisation();
    Assert.assertEquals("167 65 9999999", expressionSUT.joinedPCode());
    Assert.assertFalse(expressionSUT.isPrecalculationPossible());
  }
  
  
  // Test '\n' as a single Char
  @Test
  public void testExpressionWithSingleQuoteStringSpecialChars() {
    Source source = new Source("'\\n'");

    setupWordExpression(source);
    Assert.assertEquals("160 155", expressionSUT.joinedPCode());

    expressionSUT.optimisation();
    Assert.assertEquals("167 155 9999999", expressionSUT.joinedPCode());
    Assert.assertFalse(expressionSUT.isPrecalculationPossible());
  }

  // Test "A", single length double quoted string will not replaced as an integer value
  @Test
  public void testExpressionWithDoubleQuoteSingleString() {
    Source source = new Source("\"A\"");

    setupWordExpression(source);
    Assert.assertEquals("172 0", expressionSUT.joinedPCode());

    expressionSUT.optimisation();
    Assert.assertEquals("172 0 9999999", expressionSUT.joinedPCode());
    Assert.assertFalse(expressionSUT.isPrecalculationPossible());
  }

  
  // TODO: Mark this deprecated!
  // Test 'ABCD'
  @Test(expected = IllegalStateException.class)
  public void testExpressionWithSingleQuoteLongString() {
    Source source = new Source("'ABCD'");

    setupWordExpression(source);
  }

  
  // Test: "ABCDE" NEW String format
  // TODO: Support "" as empty String with length 0
  @Test
  public void testExpressionWithDoubleQuoteLongString() {
    Source source = new Source("\"ABC\n\"");

    setupWordExpression(source);
    Assert.assertEquals("172 0", expressionSUT.joinedPCode());

    expressionSUT.optimisation();
    Assert.assertEquals("172 0 9999999", expressionSUT.joinedPCode());
    Assert.assertFalse(expressionSUT.isPrecalculationPossible());
  }

  @Test
  public void testExpressionWithDoubleQuoteEmptyString() {
    Source source = new Source("\"\"");

    setupWordExpression(source);
    Assert.assertEquals("172 0", expressionSUT.joinedPCode());

    expressionSUT.optimisation();
    Assert.assertEquals("172 0 9999999", expressionSUT.joinedPCode());
    Assert.assertFalse(expressionSUT.isPrecalculationPossible());
  }


}
