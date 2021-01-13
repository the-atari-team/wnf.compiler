// cdw by 'The Atari Team' 2020
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler;

import org.junit.Assert;
import org.junit.Test;

import lla.privat.atarixl.compiler.expression.Type;
import lla.privat.atarixl.compiler.source.Source;

public class TestAssignment {
  @Test
  public void testAssignmentXisByte() {
    Source source = new Source("x:=1").setVerboseLevel(2);
    source.addVariable("X", Type.BYTE);
    
    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Assignment(source).assign(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertTrue(source.hasVariable("X"));
    Assert.assertEquals(Type.BYTE, source.getErgebnis());

    int n=-1;
    Assert.assertEquals("; (5)", source.getCode().get(++n));
    Assert.assertEquals(" LDY #<1", source.getCode().get(++n));
    Assert.assertEquals(" STY X", source.getCode().get(++n));
  }
  
  @Test
  public void testAssignmentXisByteResultWord() {
    Source source = new Source("x:=256").setVerboseLevel(2);
    source.addVariable("X", Type.BYTE);
    
    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Assignment(source).assign(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertEquals(Type.WORD, source.getErgebnis());

    int n=-1;
    Assert.assertEquals("; (5)", source.getCode().get(++n));
    Assert.assertEquals(" LDY #<256", source.getCode().get(++n));
    Assert.assertEquals(" LDX #>256", source.getCode().get(++n));
    Assert.assertEquals(" STY X", source.getCode().get(++n));
  }

  @Test
  public void testAssignmentXisByteYisByteArray() {
    Source source = new Source("x:=y[1]").setVerboseLevel(2);
    source.addVariable("X", Type.BYTE);
    source.addVariable("Y", Type.BYTE_ARRAY);
    
    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Assignment(source).assign(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertTrue(source.hasVariable("X"));
    Assert.assertEquals(Type.BYTE, source.getErgebnis());

    int n=-1;
    Assert.assertEquals("; (5)", source.getCode().get(++n));
    Assert.assertEquals(" LDY #<1", source.getCode().get(++n));

    Assert.assertEquals("; (12)", source.getCode().get(++n));
    Assert.assertEquals(" LDA Y,Y", source.getCode().get(++n));
    Assert.assertEquals(" TAY", source.getCode().get(++n));
    Assert.assertEquals(" STY X", source.getCode().get(++n));
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
    Assert.assertEquals(Type.BYTE, source.getErgebnis());

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
    Assert.assertEquals(Type.WORD, source.getErgebnis());

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
  public void testAssignmentXisWord() {
    Source source = new Source("x:=1").setVerboseLevel(2);
    source.addVariable("X", Type.WORD);
    
    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Assignment(source).assign(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertTrue(source.hasVariable("X"));
    int n=-1;
    Assert.assertEquals("; (5)", source.getCode().get(++n));
    Assert.assertEquals(" LDY #<1", source.getCode().get(++n));
    Assert.assertEquals(" STY X", source.getCode().get(++n));
    Assert.assertEquals(" LDX #0", source.getCode().get(++n));
    Assert.assertEquals(" STX X+1", source.getCode().get(++n));
  }

  @Test
  public void testAssignmentXisString() {
    Source source = new Source("x:='Hallo'").setVerboseLevel(2);
    source.addVariable("X", Type.WORD);
    
    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Assignment(source).assign(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertTrue(source.hasVariable("X"));

    int n=-1;
    Assert.assertEquals("; (15)", source.getCode().get(++n));
    Assert.assertEquals(" LDY #<?STRING1", source.getCode().get(++n));
    Assert.assertEquals(" LDX #>?STRING1", source.getCode().get(++n));
    Assert.assertEquals(" STY X", source.getCode().get(++n));
    Assert.assertEquals(" STX X+1", source.getCode().get(++n));
  }

  @Test
  public void testAssignmentXisWordYisByteArray() {
    Source source = new Source("x:=y[1]").setVerboseLevel(2);
    source.addVariable("X", Type.WORD);
    source.addVariable("Y", Type.BYTE_ARRAY);
    
    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Assignment(source).assign(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertTrue(source.hasVariable("X"));

    int n=-1;
    Assert.assertEquals("; (5)", source.getCode().get(++n));
    Assert.assertEquals(" LDY #<1", source.getCode().get(++n));

    Assert.assertEquals("; (12)", source.getCode().get(++n));
    Assert.assertEquals(" LDA Y,Y", source.getCode().get(++n));
    Assert.assertEquals(" TAY", source.getCode().get(++n));
    Assert.assertEquals(" STY X", source.getCode().get(++n));
    Assert.assertEquals(" LDX #0", source.getCode().get(++n));
    Assert.assertEquals(" STX X+1", source.getCode().get(++n));
  }

  @Test
  public void testAssignmentToByteArray() {
    Source source = new Source("y[1]:=x").setVerboseLevel(2);
    source.addVariable("X", Type.WORD);
    source.addVariable("Y", Type.BYTE_ARRAY);
    
    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Assignment(source).assign(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertTrue(source.hasVariable("X"));

    int n=-1;
    Assert.assertEquals("; (5)", source.getCode().get(++n));
    Assert.assertEquals(" LDY #<1", source.getCode().get(++n));
    Assert.assertEquals(" STY @PUTARRAY", source.getCode().get(++n));

    Assert.assertEquals("; (6)", source.getCode().get(++n));
    Assert.assertEquals(" LDY X", source.getCode().get(++n));
    Assert.assertEquals(" LDX X+1", source.getCode().get(++n));
    Assert.assertEquals(" TYA", source.getCode().get(++n));
    Assert.assertEquals(" LDX @PUTARRAY", source.getCode().get(++n));
    Assert.assertEquals(" STA Y,X", source.getCode().get(++n));
  }

  @Test
  public void testAssignmentToFatByteArray() {
    Source source = new Source("big[1]:=x").setVerboseLevel(2);
    source.addVariable("X", Type.WORD);
    source.addVariable("BIG", Type.FAT_BYTE_ARRAY);
    
    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Assignment(source).assign(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertEquals(Type.WORD, source.getErgebnis());

    int n=-1;
    Assert.assertEquals("; (5)", source.getCode().get(++n));
    Assert.assertEquals(" LDY #<1", source.getCode().get(++n));
    Assert.assertEquals(" LDX #0", source.getCode().get(++n));
    Assert.assertEquals(" TYA", source.getCode().get(++n));
    Assert.assertEquals(" PUTARRAYB BIG", source.getCode().get(++n));

    Assert.assertEquals("; (6)", source.getCode().get(++n));
    Assert.assertEquals(" LDY X", source.getCode().get(++n));
    Assert.assertEquals(" LDX X+1", source.getCode().get(++n));
    
    Assert.assertEquals(" TYA", source.getCode().get(++n));
    Assert.assertEquals(" LDY #0", source.getCode().get(++n));
    Assert.assertEquals(" STA (@PUTARRAY),Y", source.getCode().get(++n));
  }


  @Test
  public void testAssignmentToWordArray() {
    Source source = new Source("y[1]:=x").setVerboseLevel(2);
    source.addVariable("X", Type.WORD);
    source.addVariable("Y", Type.WORD_ARRAY);
    
    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Assignment(source).assign(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertTrue(source.hasVariable("X"));

    int n=-1;
    Assert.assertEquals("; (5)", source.getCode().get(++n));
    Assert.assertEquals(" LDY #<1", source.getCode().get(++n));
    Assert.assertEquals(" LDX #0", source.getCode().get(++n));
    Assert.assertEquals(" TYA", source.getCode().get(++n));
    Assert.assertEquals(" PUTARRAYW Y", source.getCode().get(++n));

    Assert.assertEquals("; (6)", source.getCode().get(++n));
    Assert.assertEquals(" LDY X", source.getCode().get(++n));
    Assert.assertEquals(" LDX X+1", source.getCode().get(++n));
    Assert.assertEquals(" TYA", source.getCode().get(++n));
    Assert.assertEquals(" LDY #0", source.getCode().get(++n));
    Assert.assertEquals(" STA (@PUTARRAY),Y", source.getCode().get(++n));
    Assert.assertEquals(" INY", source.getCode().get(++n));
    Assert.assertEquals(" TXA", source.getCode().get(++n));
    Assert.assertEquals(" STA (@PUTARRAY),Y", source.getCode().get(++n));
  }
  @Test
  public void testAssignmentToByteWithFunctionSomething() {
    Source source = new Source("n := @something()").setVerboseLevel(2);
    source.addVariable("N", Type.BYTE);
    source.addVariable("@SOMETHING", Type.FUNCTION);
    
    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Assignment(source).assign(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertEquals(Type.WORD, source.getErgebnis());

    int n=-1;
    Assert.assertEquals("; (14)", source.getCode().get(++n));
    Assert.assertEquals(" JSR @SOMETHING", source.getCode().get(++n));
    Assert.assertEquals(" STY N", source.getCode().get(++n));
  }
  
  @Test
  public void testAssignmentToByteWithFunctionCall() {
    Source source = new Source("n := @strcmp(adr:men, 'John Doe')").setVerboseLevel(2);
    source.addVariable("N", Type.BYTE);
    source.addVariable("MEN", Type.WORD);
    source.addVariable("'John Doe'", Type.STRING);
    source.addVariable("@STRCMP", Type.FUNCTION);
    
    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Assignment(source).assign(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

//     Assert.assertTrue(source.hasVariable("N"));
    Assert.assertEquals(Type.WORD, source.getErgebnis());

    int n=-1;
    Assert.assertEquals("; (13)", source.getCode().get(++n));
    Assert.assertEquals(" LDY #<MEN", source.getCode().get(++n));
    Assert.assertEquals(" LDX #>MEN", source.getCode().get(++n));
    Assert.assertEquals("; (16)", source.getCode().get(++n));
    Assert.assertEquals(" TYA", source.getCode().get(++n));
    Assert.assertEquals(" LDY #1", source.getCode().get(++n));
    Assert.assertEquals(" STA (@HEAP_PTR),Y", source.getCode().get(++n));
    Assert.assertEquals(" TXA", source.getCode().get(++n));
    Assert.assertEquals(" INY", source.getCode().get(++n));
    Assert.assertEquals(" STA (@HEAP_PTR),Y", source.getCode().get(++n));
    Assert.assertEquals("; (15)", source.getCode().get(++n));
    Assert.assertEquals(" LDY #<?STRING2", source.getCode().get(++n));
    Assert.assertEquals(" LDX #>?STRING2", source.getCode().get(++n));
    Assert.assertEquals("; (16)", source.getCode().get(++n));
    Assert.assertEquals(" TYA", source.getCode().get(++n));
    Assert.assertEquals(" LDY #3", source.getCode().get(++n));
    Assert.assertEquals(" STA (@HEAP_PTR),Y", source.getCode().get(++n));
    Assert.assertEquals(" TXA", source.getCode().get(++n));
    Assert.assertEquals(" INY", source.getCode().get(++n));
    Assert.assertEquals(" STA (@HEAP_PTR),Y", source.getCode().get(++n));
    Assert.assertEquals("; (14)", source.getCode().get(++n));
    Assert.assertEquals(" JSR @STRCMP", source.getCode().get(++n));
    Assert.assertEquals(" STY N", source.getCode().get(++n));
  }

}
