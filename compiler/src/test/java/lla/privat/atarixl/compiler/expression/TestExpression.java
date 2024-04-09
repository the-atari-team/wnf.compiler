// cdw by 'The Atari Team' 2022
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler.expression;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import lla.privat.atarixl.compiler.Symbol;
import lla.privat.atarixl.compiler.SymbolEnum;
import lla.privat.atarixl.compiler.source.Source;

public class TestExpression {

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

  private void setupExpression(Source source, Type type) {
    expressionSUT = new Expression(source);
    Symbol symbol = source.nextElement();
    symbol = expressionSUT.setType(type).expression(symbol).getLastSymbol();

    Assert.assertEquals(SymbolEnum.noSymbol, symbol.getId());
  }

  @Test
  public void testExpression() {
    Source source = new Source("123 ");

    setupWordExpression(source);

    Assert.assertEquals("160 123", expressionSUT.joinedPCode());

    expressionSUT.optimisation();
    Assert.assertEquals("167 123 9999999", expressionSUT.joinedPCode());
  }

  @Test
  public void testExpression160() {
    Source source = new Source("160 ");

    setupWordExpression(source);

    Assert.assertEquals("160 160", expressionSUT.joinedPCode());

    expressionSUT.optimisation();
    Assert.assertEquals("167 160 9999999", expressionSUT.joinedPCode());
  }

  @Test
  public void testExpressionHexValue() {
    Source source = new Source("$AF ");

    setupWordExpression(source);

    Assert.assertEquals("160 175", expressionSUT.joinedPCode());

    expressionSUT.optimisation();
    Assert.assertEquals("167 175 9999999", expressionSUT.joinedPCode());
    Assert.assertEquals(0, expressionSUT.getCountArithmeticSymbols());
  }

  @Test
  public void testExpressionBinValue() {
    Source source = new Source("%10111000 ");

    setupWordExpression(source);

    Assert.assertEquals("160 184", expressionSUT.joinedPCode());

    expressionSUT.optimisation();
    Assert.assertEquals("167 184 9999999", expressionSUT.joinedPCode());
  }

  @Test
  public void testExpressionHashValue() {
    Source source = new Source("#2320 ");

    setupWordExpression(source);

    Assert.assertEquals("160 184", expressionSUT.joinedPCode());

    expressionSUT.optimisation();
    Assert.assertEquals("167 184 9999999", expressionSUT.joinedPCode());
  }

  @Test
  public void testExpressionWithVariable() {
    Source source = new Source("x");
    source.addVariable("X", Type.BYTE);

    setupWordExpression(source);
    Assert.assertEquals("168 0", expressionSUT.joinedPCode());

    expressionSUT.optimisation();
    Assert.assertEquals("168 0 9999999", expressionSUT.joinedPCode());
  }

  @Test
  public void testExpressionWithNegativeVariable() {
    Source source = new Source("-x");
    source.addVariable("X", Type.BYTE);

    setupWordExpression(source);
    Assert.assertEquals("160 0 162 168 0 163 9", expressionSUT.joinedPCode());

    expressionSUT.optimisation();
    Assert.assertEquals("167 0 17 168 0 9999999", expressionSUT.joinedPCode());
  }

  @Test
  public void testExpressionWithVariableWord() {
    Source source = new Source("x");
    source.addVariable("X", Type.WORD);

    setupWordExpression(source);
    Assert.assertEquals("168 0", expressionSUT.joinedPCode());

    expressionSUT.optimisation();
    Assert.assertEquals("168 0 9999999", expressionSUT.joinedPCode());
  }

  @Test
  public void testByteExpressionWithVariableConst() {
    Source source = new Source("b+x");
    source.addVariable("B", Type.BYTE);
    source.addVariable("X", Type.CONST);
    source.setVariableAddress("X", "123");

    setupExpression(source, Type.BYTE);
    Assert.assertEquals("168 0 162 160 123 163 8", expressionSUT.joinedPCode());

    expressionSUT.optimisation();
    Assert.assertEquals("168 0 16 167 123 9999999", expressionSUT.joinedPCode());
    Assert.assertEquals(Type.BYTE, source.getTypeOfLastExpression());
  }

  @Test
  public void testWordExpressionWithVariableConst() {
    Source source = new Source("x");
    source.addVariable("B", Type.WORD);
    source.addVariable("X", Type.CONST);
    source.setVariableAddress("X", "B");

    setupWordExpression(source);
    Assert.assertEquals("254 171 0", expressionSUT.joinedPCode());

    expressionSUT.optimisation();
    Assert.assertEquals("254 171 0 9999999", expressionSUT.joinedPCode());
  }

  @Test
  public void testExpressionWithByteArrayVariable() {
    Source source = new Source("x[$01]");
    source.addVariable("X", Type.BYTE_ARRAY);

    setupWordExpression(source);
    Assert.assertEquals("160 1 170 0", expressionSUT.joinedPCode());

    expressionSUT.optimisation();
    Assert.assertEquals("167 1 170 0 9999999", expressionSUT.joinedPCode());
  }

  @Test
  public void testExpressionWithWordArrayVariable() {
    Source source = new Source("x[$01]");
    source.addVariable("X", Type.WORD_ARRAY);

    setupWordExpression(source);
    Assert.assertEquals("160 1 169 0", expressionSUT.joinedPCode());

    expressionSUT.optimisation();
    Assert.assertEquals("167 1 169 0 9999999", expressionSUT.joinedPCode());
  }

  @Test
  public void testExpressionWithArrayVariableAndExpression() {
    Source source = new Source("x[1 + n]");
    source.addVariable("N", Type.BYTE);
    source.addVariable("X", Type.BYTE_ARRAY);

    setupWordExpression(source);
    Assert.assertEquals("160 1 162 168 0 163 8 170 1", expressionSUT.joinedPCode());

    expressionSUT.optimisation();
    Assert.assertEquals("167 1 16 168 0 170 1 9999999", expressionSUT.joinedPCode());
  }

  @Test
  public void testExpressionWithArrayVariableAndAddExpression() {
    Source source = new Source("x[1] + n");
    source.addVariable("N", Type.BYTE);
    source.addVariable("X", Type.BYTE_ARRAY);

    setupWordExpression(source);
    Assert.assertEquals("160 1 170 1 162 168 0 163 8", expressionSUT.joinedPCode());

    expressionSUT.optimisation();
    Assert.assertEquals("167 1 170 1 16 168 0 9999999", expressionSUT.joinedPCode());
  }

  @Test
  public void testAddInExpression() {
    Source source = new Source("123 + 876");

    setupWordExpression(source);

    // zahl <value> push zahl <value> pull add
    Assert.assertEquals("160 123 162 160 876 163 8", expressionSUT.joinedPCode());

    expressionSUT.optimisation();
    Assert.assertEquals("167 123 16 167 876 9999999", expressionSUT.joinedPCode());
  }

  @Test
  public void testAndInExpression() {
    Source source = new Source("123 & 876");

    setupWordExpression(source);

    // zahl <value> push zahl <value> pull add
    Assert.assertEquals("160 123 162 160 876 163 14", expressionSUT.joinedPCode());

    expressionSUT.optimisation();
    Assert.assertEquals("167 123 22 167 876 9999999", expressionSUT.joinedPCode());
  }

  @Test
  public void testOrInExpression() {
    Source source = new Source("123 ! 876");

    setupWordExpression(source);

    // zahl <value> push zahl <value> pull add
    Assert.assertEquals("160 123 162 160 876 163 12", expressionSUT.joinedPCode());

    expressionSUT.optimisation();
    Assert.assertEquals("167 123 20 167 876 9999999", expressionSUT.joinedPCode());
    Assert.assertEquals(1, expressionSUT.getCountArithmeticSymbols());
  }

  @Test
  public void testExOrInExpression() {
    Source source = new Source("123 xor 876");

    setupWordExpression(source);

    // zahl <value> push zahl <value> pull add
    Assert.assertEquals("160 123 162 160 876 163 13", expressionSUT.joinedPCode());

    expressionSUT.optimisation();
    Assert.assertEquals("167 123 21 167 876 9999999", expressionSUT.joinedPCode());
    Assert.assertEquals(1, expressionSUT.getCountArithmeticSymbols());
  }

  @Test
  public void testModuloInExpression() {
    Source source = new Source("123 mod 876");

    setupWordExpression(source);

    // zahl <value> push zahl <value> pull add
    Assert.assertEquals("160 123 162 160 876 163 15", expressionSUT.joinedPCode());

    expressionSUT.optimisation();
    Assert.assertEquals("167 123 23 167 876 9999999", expressionSUT.joinedPCode());
    Assert.assertEquals(1, expressionSUT.getCountArithmeticSymbols());
  }

  @Test
  public void testAddSubInExpression() {
    Source source = new Source("2 + 3 - 4");

    setupWordExpression(source);

    Assert.assertEquals("160 2 162 160 3 163 8 162 160 4 163 9", expressionSUT.joinedPCode());

    expressionSUT.optimisation();
    Assert.assertEquals("167 2 16 167 3 17 167 4 9999999", expressionSUT.joinedPCode());

  }

  @Test
  public void testSubstractInExpression() {
    Source source = new Source("999 - 876");

    setupWordExpression(source);

    Assert.assertEquals("160 999 162 160 876 163 9", expressionSUT.joinedPCode());

    expressionSUT.optimisation();
    Assert.assertEquals("167 999 17 167 876 9999999", expressionSUT.joinedPCode());
  }

  @Test
  public void testAddMulInExpression() {
    Source source = new Source("2 + 3 * 4");

    setupWordExpression(source);

    Assert.assertEquals("160 2 162 160 3 162 160 4 163 10 163 8", expressionSUT.joinedPCode());

    expressionSUT.optimisation();
    Assert.assertEquals("167 2 162 167 3 18 167 4 163 8 9999999", expressionSUT.joinedPCode());
    Assert.assertEquals(2, expressionSUT.getCountArithmeticSymbols());
  }

  @Test
  public void testDivSubInExpression() {
    Source source = new Source("8 / 2 - 4");

    setupWordExpression(source);

    Assert.assertEquals("160 8 162 160 2 163 11 162 160 4 163 9", expressionSUT.joinedPCode());

    expressionSUT.optimisation();
    Assert.assertEquals("167 8 19 167 2 17 167 4 9999999", expressionSUT.joinedPCode());
    Assert.assertEquals(2, expressionSUT.getCountArithmeticSymbols());
  }

  @Test
  public void testExpressionNegativOne() {
    Source source = new Source("-1 ");

    setupWordExpression(source);

    Assert.assertEquals("160 -1", expressionSUT.joinedPCode());

    expressionSUT.optimisation();
    Assert.assertEquals("167 -1 9999999", expressionSUT.joinedPCode());
  }

  @Test
  public void testMultiplyWithNegativInExpression() {
    Source source = new Source("2 * -1");

    setupWordExpression(source);

    // zahl <value> push zahl <value> pull add
    // besser: 0-1
    Assert.assertEquals("160 2 162 160 -1 163 10", expressionSUT.joinedPCode());

    expressionSUT.optimisation();
    Assert.assertEquals("167 2 18 167 -1 9999999", expressionSUT.joinedPCode());
    Assert.assertEquals(1, expressionSUT.getCountArithmeticSymbols());
  }

  @Test(expected = IllegalStateException.class)
  public void testFunctionCallUndefinedFunction() {
    Source source = new Source("x()");

    setupWordExpression(source);
  }

  @Test
  public void testFunctionCallUndefinedATFunction() {
    Source source = new Source("@x()");

    setupWordExpression(source);
  }

  @Test
  public void testFunctionCallInExpression() {
    Source source = new Source("x()");
    source.addVariable("X", Type.FUNCTION);

    setupWordExpression(source);

    // zahl <value> push zahl <value> pull add
    Assert.assertEquals("180 0 64 0 0", expressionSUT.joinedPCode());

    expressionSUT.optimisation();
    Assert.assertEquals("180 0 64 0 0 9999999", expressionSUT.joinedPCode());
  }

  @Test
  public void testFunctionCallInExpressionWithOneParameter() {
    Source source = new Source("48 + @random(123)");
    source.addVariable("@RANDOM", Type.FUNCTION);

    setupWordExpression(source);

    // zahl <value> push zahl <value> pull add
    Assert.assertEquals("160 48 162 180 0 160 123 181 0 64 0 1 163 8", expressionSUT.joinedPCode());

    expressionSUT.optimisation();
    Assert.assertEquals("167 48 162 180 0 167 123 181 0 64 0 1 163 8 9999999", expressionSUT.joinedPCode());
  }

  @Test
  public void testFunctionCallInExpressionWithOneParameterExpression() {
    Source source = new Source("48 + @random(123-2)");
    source.addVariable("@RANDOM", Type.FUNCTION);

    setupWordExpression(source);

    // zahl <value> push zahl <value> pull add
    Assert.assertEquals("160 48 162 180 0 160 123 162 160 2 163 9 181 0 64 0 1 163 8", expressionSUT.joinedPCode());

    expressionSUT.optimisation();
    Assert.assertEquals("167 48 162 180 0 167 123 17 167 2 181 0 64 0 1 163 8 9999999", expressionSUT.joinedPCode());
  }

  // innerhalb von Ausdrücken wollen wir keine Strings, das sollten wir nur in
  // Aufrufen haben
  @Test
  public void testFunctionCallInExpressionWithMoreThanOneParameterExpression() {
    Source source = new Source("48 + @printf(\"%s %d\n\",\"Hallo World\", 2+x*4)");
    source.addVariable("X", Type.BYTE);
    source.addVariable("@PRINTF", Type.FUNCTION);

    setupWordExpression(source);

    // zahl <value> push zahl <value> pull add
    System.out.println(expressionSUT.joinedPCode());
    Assert.assertEquals(
        "160 48 162 180 0 172 2 181 0 172 3 181 1 160 2 162 168 0 162 160 4 163 10 163 8 181 2 64 1 3 163 8",
        expressionSUT.joinedPCode());

    expressionSUT.optimisation();
    System.out.println(expressionSUT.joinedPCode());
    Assert.assertEquals(
        "167 48 162 180 0 172 2 181 0 172 3 181 1 167 2 162 168 0 18 167 4 163 8 181 2 64 1 3 163 8 9999999",
        expressionSUT.joinedPCode());
  }

  @Test
  public void testFunctionCallInExpressionTwoSamePArameter() {
    Source source = new Source("@printf(\"Hallo\",\"Hallo\")");
    source.addVariable("@PRINTF", Type.FUNCTION);

    setupWordExpression(source);

    // zahl <value> push zahl <value> pull add
    Assert.assertEquals("180 0 172 1 181 0 172 1 181 1 64 0 2", expressionSUT.joinedPCode());

    expressionSUT.optimisation();
    Assert.assertEquals("180 0 172 1 181 0 172 1 181 1 64 0 2 9999999", expressionSUT.joinedPCode());
  }

  @Test
  public void testFunctionCallInFunctionCall() {
    Source source = new Source("@printf(\"Hallo\",x(1))");
    source.addVariable("@PRINTF", Type.FUNCTION);
    source.addVariable("X", Type.FUNCTION);

    setupWordExpression(source);

    // zahl <value> push zahl <value> pull add
    System.out.println(expressionSUT.joinedPCode());
    Assert.assertEquals("180 0 172 2 181 0 180 1 160 1 181 0 64 1 1 182 1 181 1 64 0 2", expressionSUT.joinedPCode());

    expressionSUT.optimisation();
    System.out.println(expressionSUT.joinedPCode());
    Assert.assertEquals("180 0 172 2 181 0 180 1 167 1 181 0 64 1 1 182 1 181 1 64 0 2 9999999",
        expressionSUT.joinedPCode());
  }

  @Test
  public void testExpressionAddressOfWordVariable() {
    Source source = new Source("adr:x");
    source.addVariable("X", Type.WORD);

    setupWordExpression(source);
    Assert.assertEquals("254 171 0", expressionSUT.joinedPCode());

    expressionSUT.optimisation();
    Assert.assertEquals("254 171 0 9999999", expressionSUT.joinedPCode());
  }

  
  @Test
  public void testExpressionAddressOfWordVariableButAtVariableUnknown() {
    Source source = new Source("adr:@x");
    // source.addVariable("@X", Type.WORD);

    // Variablen die mit @ beginnen, brauchen nicht initialisiert werden
    // die werden evtl. einfach mit angelegt und es wird angenommen das
    // der Assembler die Variable kennt und dann weiter macht.

    setupWordExpression(source);
    Assert.assertEquals("254 171 0", expressionSUT.joinedPCode());

    Assert.assertEquals("@X", source.getVariableAt(0));

    expressionSUT.optimisation();
    Assert.assertEquals("254 171 0 9999999", expressionSUT.joinedPCode());
  }

  @Test(expected = IllegalStateException.class)
  public void testExpressionAddressOfWordVariableButVariableUnknown() {
    Source source = new Source("adr:x");

    // Variablen die nicht mit @ beginnen, müssen initialisiert worden sein!

    setupWordExpression(source);
  }

  @Test
  public void testExpressionAbsoluteValueWordVariable() {
    Source source = new Source("abs:x");
    source.addVariable("X", Type.WORD);
//TODO: add test with int8 and byte
    setupWordExpression(source);
    Assert.assertEquals("254 175 0", expressionSUT.joinedPCode());

    expressionSUT.optimisation();
    Assert.assertEquals("254 175 0 9999999", expressionSUT.joinedPCode());
  }

  
  @Test
  public void testExpressionAbsoluteValueInt8Variable() {
    Source source = new Source("abs:x");
    source.addVariable("X", Type.INT8);
    setupWordExpression(source);
    Assert.assertEquals("254 177 0", expressionSUT.joinedPCode());

    expressionSUT.optimisation();
    Assert.assertEquals("254 177 0 9999999", expressionSUT.joinedPCode());
  }

  @Test
  public void testExpressionToWordValueByteVariable() {
    Source source = new Source("b2w:x");
    source.addVariable("X", Type.BYTE);

    setupWordExpression(source);
    Assert.assertEquals("254 176 0", expressionSUT.joinedPCode());

    expressionSUT.optimisation();
    Assert.assertEquals("254 176 0 9999999", expressionSUT.joinedPCode());
  }

  @Test(expected = IllegalStateException.class)
  public void testExpressionToWordValueWordVariable() {
    Source source = new Source("w:x");
    source.addVariable("X", Type.WORD);

    setupWordExpression(source);
  }

  @Test
  public void testAddWithParenthesis() {
    Source source = new Source("2 + (3 + ( 4 + ( 5 + 6 )))");

    setupWordExpression(source);

    Assert.assertEquals("160 2 162 160 3 162 160 4 162 160 5 162 160 6 163 8 163 8 163 8 163 8",
        expressionSUT.joinedPCode());

    expressionSUT.optimisation();
    Assert.assertEquals("167 2 162 167 3 162 167 4 162 167 5 16 167 6 163 8 163 8 163 8 9999999",
        expressionSUT.joinedPCode());
  }

  @Test
  public void testExpressionZahlSubZahl() {
    Source source = new Source("$ff - 16");

    setupWordExpression(source);
    Assert.assertEquals("160 255 162 160 16 163 9", expressionSUT.joinedPCode());

    expressionSUT.optimisation();
    Assert.assertEquals("167 255 17 167 16 9999999", expressionSUT.joinedPCode());
    Assert.assertEquals(1, expressionSUT.getCountArithmeticSymbols());
  }

  @Test
  public void testFunctionPointerCallInExpression() {
    Source source = new Source("@(x)()");
    source.addVariable("X", Type.WORD);

    setupWordExpression(source);

    // zahl <value> push zahl <value> pull add
    Assert.assertEquals("180 0 65 0 0", expressionSUT.joinedPCode());

    expressionSUT.optimisation();
    Assert.assertEquals("180 0 65 0 0 9999999", expressionSUT.joinedPCode());
  }

  @Test
  public void testAddAddInExpression() {
    Source source = new Source("2 + 3 + 4");

    setupWordExpression(source);

    Assert.assertEquals("160 2 162 160 3 163 8 162 160 4 163 8", expressionSUT.joinedPCode());

    expressionSUT.optimisation();
    Assert.assertEquals("167 2 16 167 3 16 167 4 9999999", expressionSUT.joinedPCode());
    Assert.assertEquals(2, expressionSUT.getCountArithmeticSymbols());
  }

  @Test
  public void testMulDivInExpression() {
    // Mathematisch richtig!
    // Es wird hier leider(?) zuerst die Division ausgeführt,
    // dann wird multipliziert.
    // Soll es in einer anderen Reihenfolge passieren, ist zu klammern.
    Source source = new Source("2 * 3 / 4");

    setupWordExpression(source);

    String joinedPCode = expressionSUT.joinedPCode();
    Assert.assertEquals("160 2 162 160 3 162 160 4 163 11 163 10", joinedPCode);

    expressionSUT.optimisation();
    joinedPCode = expressionSUT.joinedPCode();
    Assert.assertEquals("167 2 162 167 3 19 167 4 163 10 9999999", joinedPCode);
  }

  @Test
  public void testMulDivWithBracketInExpression() {
    Source source = new Source("(2 * 3) / 4");

    setupWordExpression(source);

    String joinedPCode = expressionSUT.joinedPCode();
    // 160 := load# 2
    // 162 := push
    // 160 := load# 3
    // 163 := pull
    //  10 := mul
    // 162 := push
    // 160 := load# 4
    // 163 := pull
    //  11 := div
    Assert.assertEquals("160 2 162 160 3 163 10 162 160 4 163 11", joinedPCode);

    expressionSUT.optimisation();
    joinedPCode = expressionSUT.joinedPCode();
    Assert.assertEquals("167 2 18 167 3 19 167 4 9999999", joinedPCode);
    Assert.assertEquals(2, expressionSUT.getCountArithmeticSymbols());
  }

  @Test
  public void testFunctionCall() {
    Source source = new Source("@paintbomb(oldxpos, 159 - oldypos)");
    source.addVariable("OLDXPOS", Type.WORD);
    source.addVariable("OLDYPOS", Type.WORD);
    source.addVariable("@PAINTBOMB", Type.PROCEDURE);
    setupWordExpression(source);

    String joinedPCode = expressionSUT.joinedPCode();
    Assert.assertEquals("180 0 168 0 181 0 160 159 162 168 1 163 9 181 1 64 2 2", joinedPCode);
  }

  @Test
  public void testWordExpression() {
    Source source = new Source("1 ");

    setupWordExpression(source);

    Assert.assertEquals("160 1", expressionSUT.joinedPCode());
    expressionSUT.optimisation();
    Assert.assertEquals(Type.BYTE, source.getTypeOfLastExpression());
    Assert.assertEquals("167 1 9999999", expressionSUT.joinedPCode());
  }

  @Test
  public void testWordExpression_neg129() {
    Source source = new Source("-129 ");

    setupExpression(source, Type.BYTE);

    Assert.assertEquals("160 -129", expressionSUT.joinedPCode());
    expressionSUT.optimisation();
    Assert.assertEquals(Type.WORD, source.getTypeOfLastExpression());
    Assert.assertEquals("167 -129 9999999", expressionSUT.joinedPCode());
  }

  @Test
  public void testWordExpression_256() {
    Source source = new Source("256 ");

    setupExpression(source, Type.BYTE);

    Assert.assertEquals("160 256", expressionSUT.joinedPCode());
    expressionSUT.optimisation();
    Assert.assertEquals(Type.WORD, source.getTypeOfLastExpression());
    Assert.assertEquals("167 256 9999999", expressionSUT.joinedPCode());
  }

  @Test
  public void testByteExpression() {
    Source source = new Source("1 ");

    setupExpression(source, Type.BYTE);

    Assert.assertEquals("160 1", expressionSUT.joinedPCode());
    expressionSUT.optimisation();
    Assert.assertEquals(Type.BYTE, source.getTypeOfLastExpression());
    Assert.assertEquals("167 1 9999999", expressionSUT.joinedPCode());
  }

  @Test
  public void testByteExpression_255() {
    Source source = new Source("255 ");

    setupExpression(source, Type.BYTE);

    Assert.assertEquals("160 255", expressionSUT.joinedPCode());
    expressionSUT.optimisation();
    Assert.assertEquals(Type.BYTE, source.getTypeOfLastExpression());
    Assert.assertEquals("167 255 9999999", expressionSUT.joinedPCode());
    Assert.assertEquals(0, expressionSUT.getCountArithmeticSymbols());
  }

  @Test
  public void testInt8Expression() {
    Source source = new Source("-1 ");

    setupExpression(source, Type.INT8);

    Assert.assertEquals("160 -1", expressionSUT.joinedPCode());
    expressionSUT.optimisation();
    Assert.assertEquals(Type.INT8, source.getTypeOfLastExpression());
    Assert.assertEquals("167 -1 9999999", expressionSUT.joinedPCode());
    Assert.assertEquals(0, expressionSUT.getCountArithmeticSymbols());
  }

  @Test
  public void testInt8Expression_127() {
    Source source = new Source("127 ");

    setupExpression(source, Type.INT8);

    Assert.assertEquals("160 127", expressionSUT.joinedPCode());
    expressionSUT.optimisation();
    Assert.assertEquals(Type.INT8, source.getTypeOfLastExpression());
    Assert.assertEquals("167 127 9999999", expressionSUT.joinedPCode());
    Assert.assertEquals(0, expressionSUT.getCountArithmeticSymbols());
  }

  @Test
  public void testExpressionWithVariableAAddVariableB() {
    Source source = new Source("a + b ");
    source.addVariable("A__", Type.BYTE);
    source.addVariable("B", Type.WORD);

    setupExpression(source, Type.BYTE);
    Assert.assertEquals("168 0 162 168 1 163 8", expressionSUT.joinedPCode());

    expressionSUT.optimisation();
    Assert.assertEquals(Type.WORD, source.getTypeOfLastExpression());
    Assert.assertEquals("168 0 16 168 1 9999999", expressionSUT.joinedPCode());
    Assert.assertEquals(1, expressionSUT.getCountArithmeticSymbols());
  }

  @Test
  public void testExpressionWithVariableAAddConstB() {
    Source source = new Source("a + b ");
    source.addVariable("A__", Type.WORD);
    source.addVariable("B", Type.CONST);
    source.setVariableAddress("B", "123");

    setupExpression(source, Type.BYTE);
    Assert.assertEquals("168 0 162 160 123 163 8", expressionSUT.joinedPCode());

    expressionSUT.optimisation();
    Assert.assertEquals(Type.WORD, source.getTypeOfLastExpression());
    Assert.assertEquals("168 0 16 167 123 9999999", expressionSUT.joinedPCode());
    Assert.assertEquals(1, expressionSUT.getCountArithmeticSymbols());
  }

  @Test(expected = IllegalStateException.class)
  public void testExpressionCAllFunctionWithOtherThanByteOrWord() {
    Source source = new Source("@F(adr:BA, BA)");
    source.addVariable("@F", Type.FUNCTION);
    source.addVariable("BA", Type.BYTE_ARRAY);

    setupWordExpression(source);
  }

  @Test
  public void testExpressionHiByteOfWordVariable() {
    Source source = new Source("hi:x");
    source.addVariable("X", Type.WORD);

    setupWordExpression(source);
    Assert.assertEquals("254 178 0", expressionSUT.joinedPCode());

    expressionSUT.optimisation();
    Assert.assertEquals("254 178 0 9999999", expressionSUT.joinedPCode());
  }

  @Test
  public void testExpressionNegativeWordVariable() {
    Source source = new Source("neg:x");
    source.addVariable("X", Type.WORD);

    setupWordExpression(source);
    Assert.assertEquals("254 179 0", expressionSUT.joinedPCode());

    expressionSUT.optimisation();
    Assert.assertEquals("254 179 0 9999999", expressionSUT.joinedPCode());
  }

  @Test
  public void testExpressionNegativeByteVariable() {
    Source source = new Source("neg:y");
    source.addVariable("Y", Type.BYTE);

    setupWordExpression(source);
    Assert.assertEquals("254 179 0", expressionSUT.joinedPCode());

    expressionSUT.optimisation();
    Assert.assertEquals("254 179 0 9999999", expressionSUT.joinedPCode());
  }
  
  @Test
  public void testExpressionNegativeInt8Variable() {
    Source source = new Source("neg:z");
    source.addVariable("Z", Type.INT8);

    setupWordExpression(source);
    Assert.assertEquals("254 179 0", expressionSUT.joinedPCode());

    expressionSUT.optimisation();
    Assert.assertEquals("254 179 0 9999999", expressionSUT.joinedPCode());
  }

}
