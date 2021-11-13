// cdw by 'The Atari Team' 2021
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler;

import org.junit.Assert;
import org.junit.Test;

import lla.privat.atarixl.compiler.expression.Type;
import lla.privat.atarixl.compiler.source.Source;

public class TestAssignmentInt8 {
  @Test
  public void testAssignmentXisByte_1() {
    Source source = new Source("x:=1").setVerboseLevel(2);
    source.addVariable("X", Type.INT8);

    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Assignment(source).assign(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertTrue(source.hasVariable("X"));
    Assert.assertEquals(Type.INT8, source.getTypeOfLastExpression());

    int n=-1;
    Assert.assertEquals("; (5)", source.getCode().get(++n));
    Assert.assertEquals(" LDY #<1", source.getCode().get(++n));
    Assert.assertEquals(" STY X", source.getCode().get(++n));
  }

  @Test
  public void testAssignmentXisByte_neg1() {
    Source source = new Source("x:=-1").setVerboseLevel(2);
    source.addVariable("X", Type.INT8);

    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Assignment(source).assign(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertTrue(source.hasVariable("X"));
    Assert.assertEquals(Type.INT8, source.getTypeOfLastExpression());

    int n=-1;
    Assert.assertEquals("; (5)", source.getCode().get(++n));
    Assert.assertEquals(" LDY #<-1", source.getCode().get(++n));
    Assert.assertEquals(" STY X", source.getCode().get(++n));
  }

  @Test
  public void testAssignmentXisByte_neg128() {
    Source source = new Source("x:=-128").setVerboseLevel(2);
    source.addVariable("X", Type.INT8);

    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Assignment(source).assign(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertTrue(source.hasVariable("X"));
    Assert.assertEquals(Type.INT8, source.getTypeOfLastExpression());

    int n=-1;
    Assert.assertEquals("; (5)", source.getCode().get(++n));
    Assert.assertEquals(" LDY #<-128", source.getCode().get(++n));
    Assert.assertEquals(" STY X", source.getCode().get(++n));
  }

  @Test
  public void testAssignmentXisByte_neg256() {
    Source source = new Source("x:=-256").setVerboseLevel(2);
    source.addVariable("X", Type.INT8);

    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Assignment(source).assign(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertTrue(source.hasVariable("X"));
    Assert.assertEquals(Type.WORD, source.getTypeOfLastExpression());

    int n=-1;
    Assert.assertEquals("; (5)", source.getCode().get(++n));
    Assert.assertEquals(" LDY #<-256", source.getCode().get(++n));
    Assert.assertEquals(" LDX #>-256", source.getCode().get(++n));
    Assert.assertEquals(" STY X", source.getCode().get(++n));
    Assert.assertEquals(4, source.getCode().size());
  }

  @Test
  public void testAssignmentXisByteResultWord() {
    Source source = new Source("x:=256").setVerboseLevel(2);
    source.addVariable("X", Type.INT8);

    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Assignment(source).assign(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertEquals(Type.WORD, source.getTypeOfLastExpression());

    int n=-1;
    Assert.assertEquals("; (5)", source.getCode().get(++n));
    Assert.assertEquals(" LDY #<256", source.getCode().get(++n));
    Assert.assertEquals(" LDX #>256", source.getCode().get(++n));
    Assert.assertEquals(" STY X", source.getCode().get(++n));
  }

  @Test
  public void testAssignmentXisByteYisByteArray() {
    Source source = new Source("x:=y[1]").setVerboseLevel(2);
    source.addVariable("X", Type.INT8);
    source.addVariable("Y", Type.BYTE_ARRAY);

    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Assignment(source).assign(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertTrue(source.hasVariable("X"));
    Assert.assertEquals(Type.BYTE, source.getTypeOfLastExpression());

    int n=-1;
    Assert.assertEquals("; (5)", source.getCode().get(++n));
    Assert.assertEquals(" LDY #<1", source.getCode().get(++n));

    Assert.assertEquals("; (12)", source.getCode().get(++n));
    Assert.assertEquals(" LDA Y,Y", source.getCode().get(++n));
    Assert.assertEquals(" TAY", source.getCode().get(++n));
    Assert.assertEquals(" STY X", source.getCode().get(++n));
    Assert.assertEquals(6, source.getCode().size());
  }

  @Test
  public void testAssignmentXisByteYisFatByteArray() {
    Source source = new Source("x:=y[256]").setVerboseLevel(2);
    source.addVariable("X", Type.BYTE);
    source.addVariable("Y", Type.FAT_BYTE_ARRAY);

    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Assignment(source).assign(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertTrue(source.hasVariable("X"));
    Assert.assertEquals(Type.BYTE, source.getTypeOfLastExpression());

    int n=-1;
    Assert.assertEquals("; (5)", source.getCode().get(++n));
    Assert.assertEquals(" LDY #<256", source.getCode().get(++n));
    Assert.assertEquals(" LDX #>256", source.getCode().get(++n));

    Assert.assertEquals("; (12.2)", source.getCode().get(++n));
    Assert.assertEquals(" TYA", source.getCode().get(++n));
    Assert.assertEquals(" GETARRAYB Y", source.getCode().get(++n));
    Assert.assertEquals(" TAY", source.getCode().get(++n));
    Assert.assertEquals(" STY X", source.getCode().get(++n));
  }

  @Test
  public void testAssignmentXisByteYisWordArray() {
    Source source = new Source("x:=y[1]").setVerboseLevel(2);
    source.addVariable("X", Type.BYTE);
    source.addVariable("Y", Type.WORD_ARRAY);

    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Assignment(source).assign(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertTrue(source.hasVariable("X"));
    Assert.assertEquals(Type.WORD, source.getTypeOfLastExpression());

    int n=-1;
    Assert.assertEquals("; (5)", source.getCode().get(++n));
    Assert.assertEquals(" LDY #<1", source.getCode().get(++n));
    Assert.assertEquals(" LDX #>1", source.getCode().get(++n));

    Assert.assertEquals("; (11)", source.getCode().get(++n));
    Assert.assertEquals(" TYA", source.getCode().get(++n));
    Assert.assertEquals(" GETARRAYW Y", source.getCode().get(++n));
    Assert.assertEquals(" TAY", source.getCode().get(++n));
    Assert.assertEquals(" STY X", source.getCode().get(++n));
  }

  @Test
  public void testAssignmentX_WORD_equals_Y_INT8() {
    Source source = new Source("x:=y").setVerboseLevel(2);
    source.addVariable("Y", Type.INT8);
    source.addVariable("X", Type.WORD);

    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Assignment(source).assign(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertTrue(source.hasVariable("X"));
    Assert.assertEquals(Type.INT8, source.getTypeOfLastExpression());

    int n=-1;
    Assert.assertEquals("; (6)", source.getCode().get(++n));
    Assert.assertEquals(" LDY Y", source.getCode().get(++n));
    Assert.assertEquals(" STY X", source.getCode().get(++n));
    Assert.assertEquals(" CPY #$80", source.getCode().get(++n));
    Assert.assertEquals(" LDX #0", source.getCode().get(++n));
    Assert.assertEquals(" BCC *+4", source.getCode().get(++n));
    Assert.assertEquals(" LDX #$FF", source.getCode().get(++n));
    Assert.assertEquals(" STX X+1", source.getCode().get(++n));
    Assert.assertEquals(8, source.getCode().size());
  }

  @Test
  public void testAssignmentX_INT8_equals_Y_WORD() {
    Source source = new Source("x:=y").setVerboseLevel(2);
    source.addVariable("Y", Type.WORD);
    source.addVariable("X", Type.INT8);

    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Assignment(source).assign(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertTrue(source.hasVariable("X"));
    Assert.assertEquals(Type.WORD, source.getTypeOfLastExpression());

    int n=-1;
    Assert.assertEquals("; (6)", source.getCode().get(++n));
    Assert.assertEquals(" LDY Y", source.getCode().get(++n));
    Assert.assertEquals(" LDX Y+1", source.getCode().get(++n));
    Assert.assertEquals(" STY X", source.getCode().get(++n));
    Assert.assertEquals(4, source.getCode().size());
  }

}
