// cdw by 'The Atari Team' 2021
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import lla.privat.atarixl.compiler.expression.Type;
import lla.privat.atarixl.compiler.source.Source;

public class TestConditionSigned {


  @Test
  public void testNotSmallerEqualInt8() {
    Source source = new Source("x<=1");
    Symbol symbol = source.nextElement();
    source.addVariable("X", Type.INT8);

    Symbol nextSymbol = new Condition(source, "?TRUE").condition(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    List<String> code = source.getCode();
    int n=-1;
    Assert.assertEquals(" LDY X", code.get(++n));
    Assert.assertEquals(" STY @ERG", code.get(++n));
    Assert.assertEquals(" LDY #<1", code.get(++n));
    Assert.assertEquals("; Bedingung (a<=b)", code.get(++n));
    Assert.assertEquals(" TYA", code.get(++n));
    Assert.assertEquals(" SEC", code.get(++n));
    Assert.assertEquals(" SBC @ERG", code.get(++n));
    Assert.assertEquals(" BVC *+4", code.get(++n));
    Assert.assertEquals(" EOR #$80", code.get(++n));
    Assert.assertEquals(" BMI ?FA1", code.get(++n));
    Assert.assertEquals(" JMP ?TRUE", code.get(++n));
    Assert.assertEquals("?FA1", code.get(++n));
  }

  @Test
  public void testNotSmallerThanInt8() {
    Source source = new Source("x<1");
    Symbol symbol = source.nextElement();
    source.addVariable("X", Type.INT8);

    Symbol nextSymbol = new Condition(source, "?TRUE").condition(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    List<String> code = source.getCode();
    int n=-1;
    Assert.assertEquals(" LDY X", code.get(++n));
    Assert.assertEquals(" STY @ERG", code.get(++n));
    Assert.assertEquals(" LDY #<1", code.get(++n));
    Assert.assertEquals("; Bedingung (a<b)", code.get(++n));
    Assert.assertEquals(" TYA", code.get(++n));
    Assert.assertEquals(" CLC", code.get(++n));
    Assert.assertEquals(" SBC @ERG", code.get(++n));
    Assert.assertEquals(" BVC *+4", code.get(++n));
    Assert.assertEquals(" EOR #$80", code.get(++n));
    Assert.assertEquals(" BMI ?FA1", code.get(++n));
    Assert.assertEquals(" JMP ?TRUE", code.get(++n));
    Assert.assertEquals("?FA1", code.get(++n));

  }

  @Test
  public void testNotGreaterEqualsInt8() {
    Source source = new Source("x>=1");
    Symbol symbol = source.nextElement();
    source.addVariable("X", Type.INT8);

    Symbol nextSymbol = new Condition(source, "?TRUE").condition(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    List<String> code = source.getCode();
    int n=-1;
    Assert.assertEquals(" LDY X", code.get(++n));
    Assert.assertEquals(" STY @ERG", code.get(++n));
    Assert.assertEquals(" LDY #<1", code.get(++n));
    Assert.assertEquals("; Bedingung (a>=b)", code.get(++n));
    Assert.assertEquals(" TYA", code.get(++n));
    Assert.assertEquals(" CLC", code.get(++n));
    Assert.assertEquals(" SBC @ERG", code.get(++n));
    Assert.assertEquals(" BVC *+4", code.get(++n));
    Assert.assertEquals(" EOR #$80", code.get(++n));
    Assert.assertEquals(" BPL ?FA1", code.get(++n));
    Assert.assertEquals(" JMP ?TRUE", code.get(++n));
    Assert.assertEquals("?FA1", code.get(++n));


  }

  @Test
  public void testNotGreaterThanInt8() {
    Source source = new Source("x>1");
    Symbol symbol = source.nextElement();
    source.addVariable("X", Type.INT8);

    Symbol nextSymbol = new Condition(source, "?TRUE").condition(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    List<String> code = source.getCode();
    int n=-1;
    Assert.assertEquals(" LDY X", code.get(++n));
    Assert.assertEquals(" STY @ERG", code.get(++n));
    Assert.assertEquals(" LDY #<1", code.get(++n));
    Assert.assertEquals("; Bedingung (a>b)", code.get(++n));
    Assert.assertEquals(" TYA", code.get(++n));
    Assert.assertEquals(" SEC", code.get(++n));
    Assert.assertEquals(" SBC @ERG", code.get(++n));
    Assert.assertEquals(" BVC *+4", code.get(++n));
    Assert.assertEquals(" EOR #$80", code.get(++n));
    Assert.assertEquals(" BPL ?FA1", code.get(++n));
    Assert.assertEquals(" JMP ?TRUE", code.get(++n));
    Assert.assertEquals("?FA1", code.get(++n));
  }

  @Test
  public void testNotSmallerEqualInt8AndByte() {
    Source source = new Source("x<=y");
    Symbol symbol = source.nextElement();
    source.addVariable("X", Type.INT8);
    source.addVariable("Y", Type.BYTE);

    Symbol nextSymbol = new Condition(source, "?TRUE").condition(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());
  }
}
