// cdw by 'The Atari Team' 2020
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import lla.privat.atarixl.compiler.expression.Type;
import lla.privat.atarixl.compiler.source.Source;

public class TestCondition {

  @Test
  public void testEquals() {
    Source source = new Source("x==1").setVerboseLevel(2);
    Symbol symbol = source.nextElement();
    source.addVariable("X", Type.BYTE);

    Symbol nextSymbol = new Condition(source, "?TRUE").condition(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    List<String> code = source.getCode();
    int n=-1;
    Assert.assertEquals("; (6)", code.get(++n));
    Assert.assertEquals(" LDY X", code.get(++n));
    Assert.assertEquals(" STY @ERG", code.get(++n));
    Assert.assertEquals("; (5)", code.get(++n));
    Assert.assertEquals(" LDY #<1", code.get(++n));
    Assert.assertEquals("; Bedingung (a==b)", code.get(++n));
    Assert.assertEquals(" CPY @ERG", code.get(++n));
    Assert.assertEquals(" BNE ?FA1", code.get(++n));
    Assert.assertEquals(" JMP ?TRUE", code.get(++n));
    Assert.assertEquals("?FA1", code.get(++n));

    // 16bit
//    Assert.assertEquals("; (6)", code.get(++n));
//    Assert.assertEquals(" LDY X", code.get(++n));
//    Assert.assertEquals(" LDX #0", code.get(++n));
//    Assert.assertEquals(" STY @ERG", code.get(++n));
//    Assert.assertEquals(" STX @ERG+1", code.get(++n));
//    Assert.assertEquals("; (5)", code.get(++n));
//    Assert.assertEquals(" LDY #<1", code.get(++n));
//    Assert.assertEquals(" LDX #>1", code.get(++n));
//    Assert.assertEquals("; Bedingung (a==b)", code.get(++n));
//    Assert.assertEquals(" CPY @ERG", code.get(++n));
//    Assert.assertEquals(" BNE ?FA1", code.get(++n));
//    Assert.assertEquals(" CPX @ERG+1", code.get(++n));
//    Assert.assertEquals(" BNE ?FA1", code.get(++n));
//    Assert.assertEquals(" JMP ?TRUE", code.get(++n));
//    Assert.assertEquals("?FA1", code.get(++n));
  }

  @Test
  public void testNotEquals() {
    Source source = new Source("x<>1");
    Symbol symbol = source.nextElement();
    source.addVariable("X", Type.BYTE);

    Symbol nextSymbol = new Condition(source, "?TRUE").condition(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());
  }

  @Test
  public void testNotEqualsModern() {
    Source source = new Source("x!=1");
    Symbol symbol = source.nextElement();
    source.addVariable("X", Type.BYTE);

    Symbol nextSymbol = new Condition(source, "?TRUE").condition(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());
  }

  @Test
  public void testNotSmallerEqual() {
    Source source = new Source("x<=1");
    Symbol symbol = source.nextElement();
    source.addVariable("X", Type.BYTE);

    Symbol nextSymbol = new Condition(source, "?TRUE").condition(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());
  }

  @Test
  public void testNotSmallerThan() {
    Source source = new Source("x<1");
    Symbol symbol = source.nextElement();
    source.addVariable("X", Type.BYTE);

    Symbol nextSymbol = new Condition(source, "?TRUE").condition(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());
  }

  @Test
  public void testNotGreaterEquals() {
    Source source = new Source("x>=1");
    Symbol symbol = source.nextElement();
    source.addVariable("X", Type.BYTE);

    Symbol nextSymbol = new Condition(source, "?TRUE").condition(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());
  }

  @Test
  public void testNotGreaterThan() {
    Source source = new Source("x>1");
    Symbol symbol = source.nextElement();
    source.addVariable("X", Type.BYTE);

    Symbol nextSymbol = new Condition(source, "?TRUE").condition(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());
  }

  @Test
  public void testOr() {
    Source source = new Source("x==1 or y==1");
    Symbol symbol = source.nextElement();
    source.addVariable("X", Type.BYTE);
    source.addVariable("Y", Type.BYTE);

    Symbol nextSymbol = new Condition(source, "?TRUE").condition(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    List<String> code = source.getCode();
    int n=-1;
    Assert.assertEquals(" LDY X", code.get(++n));
    Assert.assertEquals(" STY @ERG", code.get(++n));
    Assert.assertEquals(" LDY #<1", code.get(++n));
    Assert.assertEquals("; Bedingung (a==b)", code.get(++n));
    Assert.assertEquals(" CPY @ERG", code.get(++n));
    Assert.assertEquals(" BNE ?FA1", code.get(++n));
    Assert.assertEquals(" JMP ?TRUE", code.get(++n));
    Assert.assertEquals("?FA1", code.get(++n));
    Assert.assertEquals(" LDY Y", code.get(++n));
    Assert.assertEquals(" STY @ERG", code.get(++n));
    Assert.assertEquals(" LDY #<1", code.get(++n));
    Assert.assertEquals(" CPY @ERG", code.get(++n));
    Assert.assertEquals(" BNE ?FA2", code.get(++n));
    Assert.assertEquals(" JMP ?TRUE", code.get(++n));
    Assert.assertEquals("?FA2", code.get(++n));
    // 16 bit
//    Assert.assertEquals(" LDY X", code.get(++n));
//    Assert.assertEquals(" LDX #0", code.get(++n));
//    Assert.assertEquals(" STY @ERG", code.get(++n));
//    Assert.assertEquals(" STX @ERG+1", code.get(++n));
//    Assert.assertEquals(" LDY #<1", code.get(++n));
//    Assert.assertEquals(" LDX #>1", code.get(++n));
//    Assert.assertEquals(" CPY @ERG", code.get(++n));
//    Assert.assertEquals(" BNE ?FA1", code.get(++n));
//    Assert.assertEquals(" CPX @ERG+1", code.get(++n));
//    Assert.assertEquals(" BNE ?FA1", code.get(++n));
//    Assert.assertEquals(" JMP ?TRUE", code.get(++n));
//    Assert.assertEquals("?FA1", code.get(++n));
//    Assert.assertEquals(" LDY Y", code.get(++n));
//    Assert.assertEquals(" LDX #0", code.get(++n));
//    Assert.assertEquals(" STY @ERG", code.get(++n));
//    Assert.assertEquals(" STX @ERG+1", code.get(++n));
//    Assert.assertEquals(" LDY #<1", code.get(++n));
//    Assert.assertEquals(" LDX #>1", code.get(++n));
//    Assert.assertEquals(" CPY @ERG", code.get(++n));
//    Assert.assertEquals(" BNE ?FA2", code.get(++n));
//    Assert.assertEquals(" CPX @ERG+1", code.get(++n));
//    Assert.assertEquals(" BNE ?FA2", code.get(++n));
//    Assert.assertEquals(" JMP ?TRUE", code.get(++n));
//    Assert.assertEquals("?FA2", code.get(++n));
  }

  @Test
  public void testAnd() {
    Source source = new Source("x==1 and y==1");
    Symbol symbol = source.nextElement();
    source.addVariable("X", Type.BYTE);
    source.addVariable("Y", Type.BYTE);

    Symbol nextSymbol = new Condition(source, "?TRUE").condition(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());
  }


  @Test
  public void testEqualsWithFatByteArray() {
    Source source = new Source("fat[i] == 'T'").setVerboseLevel(2);
    Symbol symbol = source.nextElement();
    source.addVariable("FAT", Type.FAT_BYTE_ARRAY);
    source.addVariable("I", Type.WORD);

    Symbol nextSymbol = new Condition(source, "?TRUE").condition(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    List<String> code = source.getCode();
    int n=-1;
    Assert.assertEquals("; (6)", code.get(++n));
    Assert.assertEquals(" LDY I", code.get(++n));
    Assert.assertEquals(" LDX I+1", code.get(++n));
    Assert.assertEquals("; (12.2)", code.get(++n));
    Assert.assertEquals(" TYA", code.get(++n));
    Assert.assertEquals(" GETARRAYB FAT", code.get(++n));
    Assert.assertEquals(" TAY", code.get(++n));
    Assert.assertEquals(" STY @ERG", code.get(++n));
//    Assert.assertEquals(" STX @ERG+1", code.get(++n));
    Assert.assertEquals("; (5)", code.get(++n));
    Assert.assertEquals(" LDY #<84", code.get(++n));
    Assert.assertEquals("; Bedingung (a==b)", code.get(++n));
//    Assert.assertEquals(" LDX #0", code.get(++n));
    Assert.assertEquals(" CPY @ERG", code.get(++n));
    Assert.assertEquals(" BNE ?FA1", code.get(++n));
//    Assert.assertEquals(" CPX @ERG+1", code.get(++n));
//    Assert.assertEquals(" BNE ?FA1", code.get(++n));
    Assert.assertEquals(" JMP ?TRUE", code.get(++n));
    Assert.assertEquals("?FA1", code.get(++n));
  }
}
