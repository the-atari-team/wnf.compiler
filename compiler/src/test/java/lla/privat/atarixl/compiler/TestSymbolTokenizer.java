// cdw by 'The Atari Team' 2022
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class TestSymbolTokenizer {

  @Test
  public void testGetSymbolProgram() {
    String program = "program hello begin end ";
    SymbolTokenizer symbolTokenizerSUT = new SymbolTokenizer(program);

    Symbol symbol = symbolTokenizerSUT.nextElement();
    Assert.assertEquals("PROGRAM", symbol.get());
    Assert.assertEquals(SymbolEnum.reserved_word, symbol.getId());

    symbol = symbolTokenizerSUT.nextElement();
    Assert.assertEquals("HELLO", symbol.get());
    Assert.assertEquals(SymbolEnum.variable_name, symbol.getId());

    symbol = symbolTokenizerSUT.nextElement();
    Assert.assertEquals("BEGIN", symbol.get());
    Assert.assertEquals(SymbolEnum.reserved_word, symbol.getId());

    symbol = symbolTokenizerSUT.nextElement();
    Assert.assertEquals("END", symbol.get());
    Assert.assertEquals(SymbolEnum.reserved_word, symbol.getId());

  }
  @Test
  public void testGetSymbolMarkable() {
    String program = " // test\n\r // 2. Test\n  program ";
    SymbolTokenizer symbolTokenizerSUT = new SymbolTokenizer(program);

    Symbol symbol = symbolTokenizerSUT.nextElement();
    Assert.assertEquals("PROGRAM", symbol.get());
    Assert.assertEquals(SymbolEnum.reserved_word, symbol.getId());
  }

  @Test
  public void testGetCodeLine() {
    String program = " // test\n\r // 2. Test\n  program name\nhello";
    SymbolTokenizer symbolTokenizerSUT = new SymbolTokenizer(program);

    Symbol symbol = symbolTokenizerSUT.nextElement();
    Assert.assertEquals("PROGRAM", symbol.get());

    Assert.assertEquals("program name", symbolTokenizerSUT.getCodeLine());
  }

  @Test
  public void testGetSymbolExpressionWithW() {
    String program = " w:=2+2*2 ";
    SymbolTokenizer symbolTokenizerSUT = new SymbolTokenizer(program);

    Symbol symbol = symbolTokenizerSUT.nextElement();
    Assert.assertEquals("W__", symbol.get());
    Assert.assertEquals(SymbolEnum.variable_name, symbol.getId());

    symbol = symbolTokenizerSUT.nextElement();
    Assert.assertEquals(":=", symbol.get());
    Assert.assertEquals(SymbolEnum.symbol, symbol.getId());

    symbol = symbolTokenizerSUT.nextElement();
    Assert.assertEquals("2", symbol.get());
    Assert.assertEquals(SymbolEnum.number, symbol.getId());
  }

  @Test
  public void testGetSymbolExpression() {
    String program = " a:=2+2*2 ";
    SymbolTokenizer symbolTokenizerSUT = new SymbolTokenizer(program);

    Symbol symbol = symbolTokenizerSUT.nextElement();
    Assert.assertEquals("A__", symbol.get());
    Assert.assertEquals(SymbolEnum.variable_name, symbol.getId());

    symbol = symbolTokenizerSUT.nextElement();
    Assert.assertEquals(":=", symbol.get());
    Assert.assertEquals(SymbolEnum.symbol, symbol.getId());

    symbol = symbolTokenizerSUT.nextElement();
    Assert.assertEquals("2", symbol.get());
    Assert.assertEquals(SymbolEnum.number, symbol.getId());

    symbol = symbolTokenizerSUT.nextElement();
    Assert.assertEquals("+", symbol.get());
    Assert.assertEquals(SymbolEnum.symbol, symbol.getId());

    symbol = symbolTokenizerSUT.nextElement();
    Assert.assertEquals("2", symbol.get());
    Assert.assertEquals(SymbolEnum.number, symbol.getId());

    symbol = symbolTokenizerSUT.nextElement();
    Assert.assertEquals("*", symbol.get());
    Assert.assertEquals(SymbolEnum.symbol, symbol.getId());

    symbol = symbolTokenizerSUT.nextElement();
    Assert.assertEquals("2", symbol.get());
    Assert.assertEquals(SymbolEnum.number, symbol.getId());
  }

  @Test
  public void testGetSymbolExpression2() {
    String program = "2/3*(2 xor 2) ";
    SymbolTokenizer symbolTokenizerSUT = new SymbolTokenizer(program);

    Symbol symbol = symbolTokenizerSUT.nextElement();
    Assert.assertEquals("2", symbol.get());
    Assert.assertEquals(SymbolEnum.number, symbol.getId());

    symbol = symbolTokenizerSUT.nextElement();
    Assert.assertEquals("/", symbol.get());
    Assert.assertEquals(SymbolEnum.symbol, symbol.getId());

    symbol = symbolTokenizerSUT.nextElement();
    Assert.assertEquals("3", symbol.get());
    Assert.assertEquals(SymbolEnum.number, symbol.getId());

    symbol = symbolTokenizerSUT.nextElement();
    Assert.assertEquals("*", symbol.get());
    Assert.assertEquals(SymbolEnum.symbol, symbol.getId());

    symbol = symbolTokenizerSUT.nextElement();
    Assert.assertEquals("(", symbol.get());
    Assert.assertEquals(SymbolEnum.symbol, symbol.getId());

    symbol = symbolTokenizerSUT.nextElement();
    Assert.assertEquals("2", symbol.get());
    Assert.assertEquals(SymbolEnum.number, symbol.getId());

    symbol = symbolTokenizerSUT.nextElement();
    Assert.assertEquals("XOR", symbol.get());
    Assert.assertEquals(SymbolEnum.reserved_word, symbol.getId());

    symbol = symbolTokenizerSUT.nextElement();
    Assert.assertEquals("2", symbol.get());
    Assert.assertEquals(SymbolEnum.number, symbol.getId());

    symbol = symbolTokenizerSUT.nextElement();
    Assert.assertEquals(")", symbol.get());
    Assert.assertEquals(SymbolEnum.symbol, symbol.getId());
  }

  @Test
  public void testGetSymbolConditions() {
    String program = "< > <> <= >= != == = ";
    SymbolTokenizer symbolTokenizerSUT = new SymbolTokenizer(program);

    Symbol symbol = symbolTokenizerSUT.nextElement();
    Assert.assertEquals("<", symbol.get());
    Assert.assertEquals(SymbolEnum.condition_symbol, symbol.getId());

    symbol = symbolTokenizerSUT.nextElement();
    Assert.assertEquals(">", symbol.get());
    Assert.assertEquals(SymbolEnum.condition_symbol, symbol.getId());

    symbol = symbolTokenizerSUT.nextElement();
    Assert.assertEquals("<>", symbol.get());
    Assert.assertEquals(SymbolEnum.condition_symbol, symbol.getId());

    symbol = symbolTokenizerSUT.nextElement();
    Assert.assertEquals("<=", symbol.get());
    Assert.assertEquals(SymbolEnum.condition_symbol, symbol.getId());

    symbol = symbolTokenizerSUT.nextElement();
    Assert.assertEquals(">=", symbol.get());
    Assert.assertEquals(SymbolEnum.condition_symbol, symbol.getId());

    symbol = symbolTokenizerSUT.nextElement();
    Assert.assertEquals("!=", symbol.get());
    Assert.assertEquals(SymbolEnum.condition_symbol, symbol.getId());

    symbol = symbolTokenizerSUT.nextElement();
    Assert.assertEquals("==", symbol.get());
    Assert.assertEquals(SymbolEnum.condition_symbol, symbol.getId());

    symbol = symbolTokenizerSUT.nextElement();
    Assert.assertEquals("=", symbol.get());
    Assert.assertEquals(SymbolEnum.condition_symbol, symbol.getId());

  }

  @Test
  public void testGetSymbolString() {
    String program = "'Dies ist ein String' ";
    SymbolTokenizer symbolTokenizerSUT = new SymbolTokenizer(program);

    Symbol symbol = symbolTokenizerSUT.nextElement();
    Assert.assertEquals("'Dies ist ein String'", symbol.get());
    Assert.assertEquals(SymbolEnum.string, symbol.getId());
  }

  @Test
  public void testGetSymbolStringValueString() {
    String program = "'first' hallo 'second' ";
    SymbolTokenizer symbolTokenizerSUT = new SymbolTokenizer(program);

    Symbol symbol = symbolTokenizerSUT.nextElement();
    Assert.assertEquals("'first'", symbol.get());
    Assert.assertEquals(SymbolEnum.string, symbol.getId());

    symbol = symbolTokenizerSUT.nextElement();
    Assert.assertEquals("HALLO", symbol.get());
    Assert.assertEquals(SymbolEnum.variable_name, symbol.getId());

    symbol = symbolTokenizerSUT.nextElement();
    Assert.assertEquals("'second'", symbol.get());
    Assert.assertEquals(SymbolEnum.string, symbol.getId());
  }

  @Test
  public void testGetSymbolStringWithQuote() {
    String program = "'String mit \\'Quote\\'' ";
    SymbolTokenizer symbolTokenizerSUT = new SymbolTokenizer(program);

    Symbol symbol = symbolTokenizerSUT.nextElement();
    Assert.assertEquals("'String mit \\'Quote\\''", symbol.get());
    Assert.assertEquals(SymbolEnum.string, symbol.getId());
  }

  @Test
  public void testGetSymbolStringWithDoubleQuote() {
    String program = "'String mit \"DoubleQuote\"' ";
    SymbolTokenizer symbolTokenizerSUT = new SymbolTokenizer(program);

    Symbol symbol = symbolTokenizerSUT.nextElement();
    Assert.assertEquals("'String mit \"DoubleQuote\"'", symbol.get());
    Assert.assertEquals(SymbolEnum.string, symbol.getId());
  }


  @Test
  public void testGetSymbolEmptyString() {
    String program = "'' ";
    SymbolTokenizer symbolTokenizerSUT = new SymbolTokenizer(program);

    Symbol symbol = symbolTokenizerSUT.nextElement();
    Assert.assertEquals("''", symbol.get());
    Assert.assertEquals(SymbolEnum.string, symbol.getId());

  }

  @Test
  public void testGetSymbolSingleCharString() {
    String program = "'a' ";
    SymbolTokenizer symbolTokenizerSUT = new SymbolTokenizer(program);

    Symbol symbol = symbolTokenizerSUT.nextElement();
    Assert.assertEquals("'a'", symbol.get());
    Assert.assertEquals(SymbolEnum.string, symbol.getId());

  }

  @Test
  public void testGetSymbolNumber123() {
    String program = " 123 ";
    SymbolTokenizer symbolTokenizerSUT = new SymbolTokenizer(program);

    Symbol symbol = symbolTokenizerSUT.nextElement();
    Assert.assertEquals("123", symbol.get());
    Assert.assertEquals(SymbolEnum.number, symbol.getId());

  }

  @Test
  public void testGetSymbolHexNumberAFFE() {
    String program = " $affE ";
    SymbolTokenizer symbolTokenizerSUT = new SymbolTokenizer(program);

    Symbol symbol = symbolTokenizerSUT.nextElement();
    Assert.assertEquals("$AFFE", symbol.get());
    Assert.assertEquals(SymbolEnum.number, symbol.getId());

  }

  @Test
  public void testGetSymbolBinNumber38() {
    String program = " %..111... ";
    SymbolTokenizer symbolTokenizerSUT = new SymbolTokenizer(program);

    Symbol symbol = symbolTokenizerSUT.nextElement();
    Assert.assertEquals("$38", symbol.get());
    Assert.assertEquals(SymbolEnum.number, symbol.getId());

  }

  @Test(expected = IllegalStateException.class)
  public void testGetSymbolBinNumberToLow() {
    String program = " %..111. ";
    SymbolTokenizer symbolTokenizerSUT = new SymbolTokenizer(program);

    symbolTokenizerSUT.nextElement();
  }

  @Test(expected = IllegalStateException.class)
  public void testGetSymbolBinNumberToLong() {
    String program = " %..111.... ";
    SymbolTokenizer symbolTokenizerSUT = new SymbolTokenizer(program);

    symbolTokenizerSUT.nextElement();
  }

  @Test
  public void testGetSymbolBinNumberFF() {
    String program = " %11111111 ";
    SymbolTokenizer symbolTokenizerSUT = new SymbolTokenizer(program);

    Symbol symbol = symbolTokenizerSUT.nextElement();
    Assert.assertEquals("$FF", symbol.get());
    Assert.assertEquals(SymbolEnum.number, symbol.getId());
  }

  @Test
  public void testGetSymbolQuadNumber0123() {
    String program = " #0123 ";
    SymbolTokenizer symbolTokenizerSUT = new SymbolTokenizer(program);

    Symbol symbol = symbolTokenizerSUT.nextElement();
    Assert.assertEquals("$1B", symbol.get());
    Assert.assertEquals(SymbolEnum.number, symbol.getId());
  }

  @Test
  public void testGetSymbolVariable() {
    String program = "  this_is_a_long_variable_name ";
    SymbolTokenizer symbolTokenizerSUT = new SymbolTokenizer(program);

    Symbol symbol = symbolTokenizerSUT.nextElement();
    Assert.assertEquals("THIS_IS_A_LONG_VARIABLE_NAME", symbol.get());
    Assert.assertEquals(SymbolEnum.variable_name, symbol.getId());
  }

  @Test
  public void testGetSymbolAdrVariable() {
    String program = "  adr:variable ";
    SymbolTokenizer symbolTokenizerSUT = new SymbolTokenizer(program);

    Symbol symbol = symbolTokenizerSUT.nextElement();
    Assert.assertEquals("ADR", symbol.get());
    Assert.assertEquals(SymbolEnum.variable_name, symbol.getId());

    symbol = symbolTokenizerSUT.nextElement();
    Assert.assertEquals(":", symbol.get());
    Assert.assertEquals(SymbolEnum.symbol, symbol.getId());

    symbol = symbolTokenizerSUT.nextElement();
    Assert.assertEquals("VARIABLE", symbol.get());
    Assert.assertEquals(SymbolEnum.variable_name, symbol.getId());
  }

  @Test
  public void testGetSymbolAdrVariableWithPrefix() {
    String program = "  adr:variable ";
    SymbolTokenizer symbolTokenizerSUT = new SymbolTokenizer(program);
    symbolTokenizerSUT.setPrefix("TEST");

    Symbol symbol = symbolTokenizerSUT.nextElement();
    Assert.assertEquals("ADR", symbol.get());
    Assert.assertEquals(SymbolEnum.variable_name, symbol.getId());

    symbol = symbolTokenizerSUT.nextElement();
    Assert.assertEquals(":", symbol.get());
    Assert.assertEquals(SymbolEnum.symbol, symbol.getId());

    symbol = symbolTokenizerSUT.nextElement();
    Assert.assertEquals("TEST_VARIABLE", symbol.get());
    Assert.assertEquals(SymbolEnum.variable_name, symbol.getId());
  }

  @Test
  public void testGetSymbolToWordVariable() {
    String program = "  b2w:variable ";
    SymbolTokenizer symbolTokenizerSUT = new SymbolTokenizer(program);

    Symbol symbol = symbolTokenizerSUT.nextElement();
    Assert.assertEquals("B2W", symbol.get());
    Assert.assertEquals(SymbolEnum.variable_name, symbol.getId());

    symbol = symbolTokenizerSUT.nextElement();
    Assert.assertEquals(":", symbol.get());
    Assert.assertEquals(SymbolEnum.symbol, symbol.getId());

    symbol = symbolTokenizerSUT.nextElement();
    Assert.assertEquals("VARIABLE", symbol.get());
    Assert.assertEquals(SymbolEnum.variable_name, symbol.getId());
  }

  @Test
  public void testGetSymbolToWordVariableWithPrefix() {
    String program = "  b2w:variable ";
    SymbolTokenizer symbolTokenizerSUT = new SymbolTokenizer(program);
    symbolTokenizerSUT.setPrefix("TEST");
    Symbol symbol = symbolTokenizerSUT.nextElement();
    Assert.assertEquals("B2W", symbol.get());
    Assert.assertEquals(SymbolEnum.variable_name, symbol.getId());

    symbol = symbolTokenizerSUT.nextElement();
    Assert.assertEquals(":", symbol.get());
    Assert.assertEquals(SymbolEnum.symbol, symbol.getId());

    symbol = symbolTokenizerSUT.nextElement();
    Assert.assertEquals("TEST_VARIABLE", symbol.get());
    Assert.assertEquals(SymbolEnum.variable_name, symbol.getId());
  }

  @Test
  public void testGetSymbolAbsoluteVariable() {
    String program = "  abs:variable ";
    SymbolTokenizer symbolTokenizerSUT = new SymbolTokenizer(program);

    Symbol symbol = symbolTokenizerSUT.nextElement();
    Assert.assertEquals("ABS", symbol.get());
    Assert.assertEquals(SymbolEnum.variable_name, symbol.getId());

    symbol = symbolTokenizerSUT.nextElement();
    Assert.assertEquals(":", symbol.get());
    Assert.assertEquals(SymbolEnum.symbol, symbol.getId());

    symbol = symbolTokenizerSUT.nextElement();
    Assert.assertEquals("VARIABLE", symbol.get());
    Assert.assertEquals(SymbolEnum.variable_name, symbol.getId());
  }

  @Ignore
  @Test
  public void testGetSymbolAbsoluteVariableWithPrefix() {
    String program = "  abs:variable ";
    SymbolTokenizer symbolTokenizerSUT = new SymbolTokenizer(program);
    symbolTokenizerSUT.setPrefix("TEST");
    Symbol symbol = symbolTokenizerSUT.nextElement();
    Assert.assertEquals("ABS", symbol.get());
    Assert.assertEquals(SymbolEnum.variable_name, symbol.getId());

    symbol = symbolTokenizerSUT.nextElement();
    Assert.assertEquals(":", symbol.get());
    Assert.assertEquals(SymbolEnum.symbol, symbol.getId());

    symbol = symbolTokenizerSUT.nextElement();
    Assert.assertEquals("TEST_VARIABLE", symbol.get());
    Assert.assertEquals(SymbolEnum.variable_name, symbol.getId());
  }


  @Test
  public void testGetSymbolFunctionPointerCall() {
    String functionPointerCall = "  @(fkt)()";
    SymbolTokenizer symbolTokenizerSUT = new SymbolTokenizer(functionPointerCall);

    Symbol symbol = symbolTokenizerSUT.nextElement();
    Assert.assertEquals("@(", symbol.get());
    Assert.assertEquals(SymbolEnum.symbol, symbol.getId());
  }

  @Test
  public void testGetSymbolQuestionmark() {
    String program = "  @ ";
    SymbolTokenizer symbolTokenizerSUT = new SymbolTokenizer(program);

    Symbol symbol = symbolTokenizerSUT.nextElement();
    Assert.assertEquals("@", symbol.get());
  }
}
