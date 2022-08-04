// cdw by 'The Atari Team' 2022
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import lla.privat.atarixl.compiler.expression.Type;
import lla.privat.atarixl.compiler.source.Source;

public class TestAssignment {

//
// OO                    OO
// OO                    OO
// OOOOOOOO  OO     OO OOOOOO  OOOOOOO
// OO     OO OO     OO   OO   OO     OO
// OO     OO OO     OO   OO   OOOOOOOOO
// OO     OO  OOOOOOOO   OO   OO
// OOOOOOOO         OO    OOO  OOOOOOO
//           OOOOOOOO
//
// byte is a single unsigned byte or 8 bit

  @Test
  public void testAssignmentXisByte_1() {
    Source source = new Source("x:=1").setVerboseLevel(2);
    source.addVariable("X", Type.BYTE);

    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Assignment(source).assign(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertTrue(source.hasVariable("X"));
    Assert.assertEquals(Type.BYTE, source.getTypeOfLastExpression());

    int n=-1;
    Assert.assertEquals("; (5)", source.getCode().get(++n));
    Assert.assertEquals(" LDY #<1", source.getCode().get(++n));
    Assert.assertEquals(" STY X", source.getCode().get(++n));
  }

  @Test
  public void testAssignmentXisByte_neg1() {
    Source source = new Source("x:=-1").setVerboseLevel(2);
    source.addVariable("X", Type.BYTE);

    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Assignment(source).assign(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertTrue(source.hasVariable("X"));
    Assert.assertEquals(Type.WORD, source.getTypeOfLastExpression());

    int n=-1;
    Assert.assertEquals("; (5)", source.getCode().get(++n));
    Assert.assertEquals(" LDY #<-1", source.getCode().get(++n));
    Assert.assertEquals(" LDX #>-1", source.getCode().get(++n));
    Assert.assertEquals(" STY X", source.getCode().get(++n));
    Assert.assertEquals(4, source.getCode().size());
  }

  @Test
  public void testAssignmentXisByte_neg128() {
    Source source = new Source("x:=-128").setVerboseLevel(2);
    source.addVariable("X", Type.BYTE);

    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Assignment(source).assign(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertTrue(source.hasVariable("X"));
    Assert.assertEquals(Type.WORD, source.getTypeOfLastExpression());

    int n=-1;
    Assert.assertEquals("; (5)", source.getCode().get(++n));
    Assert.assertEquals(" LDY #<-128", source.getCode().get(++n));
    Assert.assertEquals(" LDX #>-128", source.getCode().get(++n));
    Assert.assertEquals(" STY X", source.getCode().get(++n));
    Assert.assertEquals(4, source.getCode().size());
  }

  @Test
  public void testAssignmentXisByte_neg256() {
    Source source = new Source("x:=-256").setVerboseLevel(2);
    source.addVariable("X", Type.BYTE);

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
  public void testAssignmentX_Byte_equal_Y_word() {
    Source source = new Source("x:=y").setVerboseLevel(2);
    source.addVariable("X", Type.BYTE);
    source.addVariable("Y", Type.WORD);

    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Assignment(source).assign(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertTrue(source.hasVariable("X"));
    int n=-1;
    Assert.assertEquals("; (6)", source.getCode().get(++n));
    Assert.assertEquals(" LDY Y", source.getCode().get(++n));
    Assert.assertEquals(" LDX Y+1", source.getCode().get(++n));
    Assert.assertEquals(" STY X", source.getCode().get(++n));
    Assert.assertEquals(4, source.getCode().size());
  }

  @Test
  public void testAssignmentXisByteResultWord() {
    Source source = new Source("x:=256").setVerboseLevel(2);
    source.addVariable("X", Type.BYTE);

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
    source.addVariable("X", Type.BYTE);
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
  public void testAssignmentXisByteYisString() {
    Source source = new Source("x:=y[1]").setVerboseLevel(2);
    source.addVariable("X", Type.BYTE);
    source.addVariable("Y", Type.STRING);

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
    Source source = new Source("x:=fat[256]").setVerboseLevel(2);
    source.addVariable("X", Type.BYTE);
    source.addVariable("FAT", Type.FAT_BYTE_ARRAY);

    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Assignment(source).assign(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertTrue(source.hasVariable("X"));
    Assert.assertEquals(Type.BYTE, source.getTypeOfLastExpression());

    int n=-1;
    List<String> code = source.getCode();
    Assert.assertEquals("; (5)", code.get(++n));
    Assert.assertEquals(" LDY #<256", code.get(++n));
    Assert.assertEquals(" LDX #>256", code.get(++n));

    Assert.assertEquals("; (12.2)", code.get(++n));
    Assert.assertEquals(" TYA", code.get(++n));
//  Assert.assertEquals(" GETARRAYB FAT", code.get(++n));
    Assert.assertEquals(" CLC", code.get(++n));
    Assert.assertEquals(" ADC # <FAT", code.get(++n));
    Assert.assertEquals(" STA @GETARRAY", code.get(++n));
    Assert.assertEquals(" TXA", code.get(++n));
    Assert.assertEquals(" ADC # >FAT", code.get(++n));
    Assert.assertEquals(" STA @GETARRAY+1", code.get(++n));
    Assert.assertEquals(" LDY #0", code.get(++n));
    Assert.assertEquals(" LDA (@GETARRAY),Y", code.get(++n));
    Assert.assertEquals(" LDX #0", code.get(++n));
    
    Assert.assertEquals(" TAY", code.get(++n));
    Assert.assertEquals(" STY X", code.get(++n));
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
  public void testAssignmentToByteWithFunctionSomething() {
    Source source = new Source("n := @something()").setVerboseLevel(2);
    source.addVariable("N", Type.BYTE);
    source.addVariable("@SOMETHING", Type.FUNCTION);

    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Assignment(source).assign(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertEquals(Type.WORD, source.getTypeOfLastExpression());

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
//    source.addVariable("'John Doe'", Type.STRING_ANONYM);
    source.addVariable("@STRCMP", Type.FUNCTION);

    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Assignment(source).assign(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

//     Assert.assertTrue(source.hasVariable("N"));
    Assert.assertEquals(Type.WORD, source.getTypeOfLastExpression());

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
    Assert.assertEquals(" LDY #<?STRING3", source.getCode().get(++n));
    Assert.assertEquals(" LDX #>?STRING3", source.getCode().get(++n));
    Assert.assertEquals("; (16)", source.getCode().get(++n));
    Assert.assertEquals(" TYA", source.getCode().get(++n));
    Assert.assertEquals(" LDY #3", source.getCode().get(++n));
    Assert.assertEquals(" STA (@HEAP_PTR),Y", source.getCode().get(++n));
    Assert.assertEquals(" TXA", source.getCode().get(++n));
    Assert.assertEquals(" INY", source.getCode().get(++n));
    Assert.assertEquals(" STA (@HEAP_PTR),Y", source.getCode().get(++n));
    Assert.assertEquals("; (14)", source.getCode().get(++n));
    Assert.assertEquals(" JSR @STRCMP_II", source.getCode().get(++n));
    Assert.assertEquals(" STY N", source.getCode().get(++n));
  }

  @Test(expected = IllegalStateException.class)
  public void testAssignmentXisByteYisUnknown() {
    Source source = new Source("x:=y[1]+y[2]").setVerboseLevel(2);
    source.addVariable("X", Type.BYTE);
//    source.addVariable("Y", Type.BYTE_ARRAY);

    Symbol symbol = source.nextElement();

    /* Symbol nextSymbol = */ new Assignment(source).assign(symbol).build();
  }

  @Test
  public void testAssignmentXisByteYDiv4() {
    Source source = new Source("x:=y/4").setVerboseLevel(2);
    source.addVariable("X", Type.BYTE);
    source.addVariable("Y", Type.BYTE);

    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Assignment(source).assign(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertTrue(source.hasVariable("X"));
    // IMPORTANT:
    // Every div/mul/mod set the result to WORD.
    // This is could be ok
    Assert.assertEquals(Type.BYTE, source.getTypeOfLastExpression());
  }

  @Test
  public void testAssignmentXisByteYDiv5() {
    Source source = new Source("x:=y/5").setVerboseLevel(2);
    source.addVariable("X", Type.BYTE);
    source.addVariable("Y", Type.BYTE);

    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Assignment(source).assign(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertTrue(source.hasVariable("X"));
    // IMPORTANT:
    // Every div/mul/mod set the result to WORD.
    // This is could be ok
    Assert.assertEquals(Type.WORD, source.getTypeOfLastExpression());
  }

  @Test
  public void testAssignmentXisByteYDiv4ForceByteWithCast() {
    Source source = new Source("x:=byte(y/4)").setVerboseLevel(2);
    source.addVariable("X", Type.BYTE);
    source.addVariable("Y", Type.BYTE);

    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Assignment(source).assign(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertTrue(source.hasVariable("X"));
    Assert.assertEquals(Type.BYTE, source.getTypeOfLastExpression());

    int n=-1;
    Assert.assertEquals("; (4)", source.getCode().get(++n));
    Assert.assertEquals(" LDY Y", source.getCode().get(++n));
    Assert.assertEquals(" TYA", source.getCode().get(++n));
    Assert.assertEquals(" LSR A", source.getCode().get(++n));
    Assert.assertEquals(" LSR A", source.getCode().get(++n));
    Assert.assertEquals(" TAY", source.getCode().get(++n));
    Assert.assertEquals(" STY X", source.getCode().get(++n));
  }

  
  
  //
// OO                    OO
// OO                    OO
// OOOOOOOO  OO     OO OOOOOO  OOOOOOO           OOOOOOOO OOOOOOOO  OOOOOOOO   OOOOOOOO OO     OO
// OO     OO OO     OO   OO   OO     OO         OO     OO OO     OO OO     OO OO     OO OO     OO
// OO     OO OO     OO   OO   OOOOOOOOO         OO     OO OO        OO        OO     OO OO     OO
// OO     OO  OOOOOOOO   OO   OO                OO     OO OO        OO        OO     OO  OOOOOOOO
// OOOOOOOO         OO    OOO  OOOOOOO           OOOOOOOO OO        OO         OOOOOOOO        OO
//           OOOOOOOO                                                                   OOOOOOOO
//
// byte array is a list of bytes
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
  public void testWordAssignmentToString() {
    Source source = new Source("y[1]:=x").setVerboseLevel(2);
    source.addVariable("X", Type.WORD);
    source.addVariable("Y", Type.STRING);

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
  public void testValueAssignmentToString() {
    Source source = new Source("y[1]:=' '").setVerboseLevel(2);
    source.addVariable("Y", Type.STRING);

    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Assignment(source).assign(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertTrue(source.hasVariable("Y"));

    int n=-1;
    Assert.assertEquals("; (5)", source.getCode().get(++n));
    Assert.assertEquals(" LDY #<1", source.getCode().get(++n));
    Assert.assertEquals(" STY @PUTARRAY", source.getCode().get(++n));

    Assert.assertEquals("; (5)", source.getCode().get(++n));
    Assert.assertEquals(" LDY #<32", source.getCode().get(++n));
    Assert.assertEquals(" LDX #>32", source.getCode().get(++n));
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

    Assert.assertEquals(Type.WORD, source.getTypeOfLastExpression());

    int n=-1;
    Assert.assertEquals("; (5)", source.getCode().get(++n));
    Assert.assertEquals(" LDY #<1", source.getCode().get(++n));
    Assert.assertEquals(" LDX #>1", source.getCode().get(++n));
// sty @putarray_byteindex
// clc
// txa
// adc #>big
// sta @putarray+1
    
// old: 16 Takte    
    Assert.assertEquals(" TYA", source.getCode().get(++n));
//    Assert.assertEquals(" PUTARRAYB BIG", source.getCode().get(++n));
    Assert.assertEquals(" CLC", source.getCode().get(++n));
    Assert.assertEquals(" ADC # <BIG", source.getCode().get(++n));
    Assert.assertEquals(" STA @PUTARRAY", source.getCode().get(++n));
    Assert.assertEquals(" TXA", source.getCode().get(++n));
    Assert.assertEquals(" ADC # >BIG", source.getCode().get(++n));
    Assert.assertEquals(" STA @PUTARRAY+1", source.getCode().get(++n));

    Assert.assertEquals("; (6)", source.getCode().get(++n));
    Assert.assertEquals(" LDY X", source.getCode().get(++n));
    Assert.assertEquals(" LDX X+1", source.getCode().get(++n));

    Assert.assertEquals(" TYA", source.getCode().get(++n));
    Assert.assertEquals(" LDY #0", source.getCode().get(++n));
 // ldy @putarray_byteindex
    Assert.assertEquals(" STA (@PUTARRAY),Y", source.getCode().get(++n));
  }


  @Test
  public void testAssignmentToFatByteArrayMem() {
    Source source = new Source("@mem[710]:=$41").setVerboseLevel(2);
    // source.addVariable("BIG", Type.FAT_BYTE_ARRAY);

    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Assignment(source).assign(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertEquals(Type.BYTE, source.getTypeOfLastExpression());

    int n=-1;
    Assert.assertEquals("; (5)", source.getCode().get(++n));
    Assert.assertEquals(" LDY #<710", source.getCode().get(++n));
    Assert.assertEquals(" LDX #>710", source.getCode().get(++n));   
    Assert.assertEquals(" STY @PUTARRAY", source.getCode().get(++n));
    Assert.assertEquals(" STX @PUTARRAY+1", source.getCode().get(++n));

    Assert.assertEquals("; (5)", source.getCode().get(++n));
    Assert.assertEquals(" LDY #<65", source.getCode().get(++n));
    Assert.assertEquals(" TYA", source.getCode().get(++n));
    Assert.assertEquals(" LDY #0", source.getCode().get(++n));
 // ldy @putarray_byteindex
    Assert.assertEquals(" STA (@PUTARRAY),Y", source.getCode().get(++n));
  }
  
//
//                                      OO
//                                      OO
// OO     OO  OOOOOOO  OOOOOOOO   OOOOOOOO
// OO     OO OO     OO OO     OO OO     OO
// OO  O  OO OO     OO OO        OO     OO
// OO  O  OO OO     OO OO        OO     OO
//  OOO OOO   OOOOOOO  OO         OOOOOOOO
//
// word is a double byte (2 bytes) signed


  @Test
  public void testAssignmentXisWord_1() {
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
    Assert.assertEquals(" LDX #>1", source.getCode().get(++n));
    Assert.assertEquals(" STY X", source.getCode().get(++n));
    Assert.assertEquals(" STX X+1", source.getCode().get(++n));
  }

  @Test
  public void testAssignmentXisWord_256() {
    Source source = new Source("x:=256").setVerboseLevel(2);
    source.addVariable("X", Type.WORD);

    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Assignment(source).assign(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertTrue(source.hasVariable("X"));
    int n=-1;
    Assert.assertEquals("; (5)", source.getCode().get(++n));
    Assert.assertEquals(" LDY #<256", source.getCode().get(++n));
    Assert.assertEquals(" LDX #>256", source.getCode().get(++n));
    Assert.assertEquals(" STY X", source.getCode().get(++n));
    Assert.assertEquals(" STX X+1", source.getCode().get(++n));
  }

  @Test
  public void testAssignmentX_Word_equal_Y_byte() {
    Source source = new Source("x:=y").setVerboseLevel(2);
    source.addVariable("X", Type.WORD);
    source.addVariable("Y", Type.BYTE);

    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Assignment(source).assign(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertTrue(source.hasVariable("X"));
    int n=-1;
    Assert.assertEquals("; (6)", source.getCode().get(++n));
    Assert.assertEquals(" LDY Y", source.getCode().get(++n));
    Assert.assertEquals(" STY X", source.getCode().get(++n));
    Assert.assertEquals(" LDX #0", source.getCode().get(++n));
    Assert.assertEquals(" STX X+1", source.getCode().get(++n));
  }

  @Test
  public void testAssignmentX_Word_equal_Y_word() {
    Source source = new Source("x:=y").setVerboseLevel(2);
    source.addVariable("X", Type.WORD);
    source.addVariable("Y", Type.WORD);

    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Assignment(source).assign(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertTrue(source.hasVariable("X"));
    int n=-1;
    Assert.assertEquals("; (6)", source.getCode().get(++n));
    Assert.assertEquals(" LDY Y", source.getCode().get(++n));
    Assert.assertEquals(" LDX Y+1", source.getCode().get(++n));
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
  public void testAssignmentX_Word_equal_Y_const() {
    Source source = new Source("x:=y").setVerboseLevel(2);
    source.addVariable("X", Type.WORD);
    source.addVariable("Y", Type.CONST);
    source.setVariableAddress("Y", "123");

    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Assignment(source).assign(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertTrue(source.hasVariable("X"));
    int n=-1;
    Assert.assertEquals("; (5)", source.getCode().get(++n));
    Assert.assertEquals(" LDY #<123", source.getCode().get(++n));
    Assert.assertEquals(" LDX #>123", source.getCode().get(++n));
    Assert.assertEquals(" STY X", source.getCode().get(++n));
    Assert.assertEquals(" STX X+1", source.getCode().get(++n));
  }

//  @Ignore("Gibt scheinbar ein Problem mit const X=@Y")
  @Test
  public void testAssignmExentX_Word_equal_Y_constMinus2() {
    Source source = new Source("x:=y-2").setVerboseLevel(2);
    source.addVariable("X", Type.WORD);
    source.addVariable("Y", Type.CONST);
    source.setVariableAddress("Y", "@COLOR");

    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Assignment(source).assign(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertTrue(source.hasVariable("X"));
    int n=-1;
    Assert.assertEquals("; (13)", source.getCode().get(++n));
    Assert.assertEquals(" LDY #<@COLOR", source.getCode().get(++n));
    Assert.assertEquals(" LDX #>@COLOR", source.getCode().get(++n));

    Assert.assertEquals("; (8)", source.getCode().get(++n));
    Assert.assertEquals(" SEC", source.getCode().get(++n));
    Assert.assertEquals(" TYA", source.getCode().get(++n));
    Assert.assertEquals(" SBC #<2", source.getCode().get(++n));
    Assert.assertEquals(" TAY", source.getCode().get(++n));
    Assert.assertEquals(" TXA", source.getCode().get(++n));
    Assert.assertEquals(" SBC #>2", source.getCode().get(++n));
    Assert.assertEquals(" TAX", source.getCode().get(++n));

    Assert.assertEquals(" STY X", source.getCode().get(++n));
    Assert.assertEquals(" STX X+1", source.getCode().get(++n));
  }

  @Test
  public void testAssignmentX_Word_equal_Y_const_add_zahl_1() {
    Source source = new Source("x:=y+1").setVerboseLevel(2);
    source.addVariable("X", Type.WORD);
    source.addVariable("Y", Type.CONST);
    source.setVariableAddress("Y", "123");

    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Assignment(source).assign(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertTrue(source.hasVariable("X"));
    int n=-1;
    Assert.assertEquals("; (3)", source.getCode().get(++n));
    Assert.assertEquals(" CLC", source.getCode().get(++n));
    Assert.assertEquals(" LDA #<123", source.getCode().get(++n));
    Assert.assertEquals(" ADC #<1", source.getCode().get(++n));
    Assert.assertEquals(" TAY", source.getCode().get(++n));
    Assert.assertEquals(" LDA #>123", source.getCode().get(++n));
    Assert.assertEquals(" ADC #>1", source.getCode().get(++n));
    Assert.assertEquals(" TAX", source.getCode().get(++n));

    Assert.assertEquals(" STY X", source.getCode().get(++n));
    Assert.assertEquals(" STX X+1", source.getCode().get(++n));
  }

  @Test
  public void testAssignmentX_Word_equal_Y_const_add_zahl_2() {
    Source source = new Source("x:=y+2").setVerboseLevel(2);
    source.addVariable("X", Type.WORD);
    source.addVariable("Y", Type.CONST);
    source.setVariableAddress("Y", "123");

    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Assignment(source).assign(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertTrue(source.hasVariable("X"));
    int n=-1;
    Assert.assertEquals("; (3)", source.getCode().get(++n));
    Assert.assertEquals(" CLC", source.getCode().get(++n));
    Assert.assertEquals(" LDA #<123", source.getCode().get(++n));
    Assert.assertEquals(" ADC #<2", source.getCode().get(++n));
    Assert.assertEquals(" TAY", source.getCode().get(++n));
    Assert.assertEquals(" LDA #>123", source.getCode().get(++n));
    Assert.assertEquals(" ADC #>2", source.getCode().get(++n));
    Assert.assertEquals(" TAX", source.getCode().get(++n));

    Assert.assertEquals(" STY X", source.getCode().get(++n));
    Assert.assertEquals(" STX X+1", source.getCode().get(++n));
  }


  // TODO word from word array

//
//                                      OO
//                                      OO
// OO     OO  OOOOOOO  OOOOOOOO   OOOOOOOO          OOOOOOOO OOOOOOOO  OOOOOOOO   OOOOOOOO OO     OO
// OO     OO OO     OO OO     OO OO     OO         OO     OO OO     OO OO     OO OO     OO OO     OO
// OO  O  OO OO     OO OO        OO     OO         OO     OO OO        OO        OO     OO OO     OO
// OO  O  OO OO     OO OO        OO     OO         OO     OO OO        OO        OO     OO  OOOOOOOO
//  OOO OOO   OOOOOOO  OO         OOOOOOOO          OOOOOOOO OO        OO         OOOOOOOO        OO
//                                                                                         OOOOOOOO
// word array is a list of words


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
    Assert.assertEquals(" LDX #>1", source.getCode().get(++n));

/*
 * tya           ; 2
 * stx @reg+1    ; 3
 * asl a         ; 2
 * rol @reg+1    ; 5 sollte immer carry loeschen, sonst kommen wir in Teufels Kueche
 * adc #<Y       ; 2
 * sta @putarray ; 3
 * lda @reg+1    ; 3
 * adc #>Y       ; 2
 * sta @putarray+1 ; 3 25 Zyklen 16 Bytes
 * */

    Assert.assertEquals(" TYA", source.getCode().get(++n)); //          ; 2
//    Assert.assertEquals(" PUTARRAYW Y", source.getCode().get(++n));
//    Assert.assertEquals(" ASL A", source.getCode().get(++n)); //        ; 2 Mult (x,y)*2
//    Assert.assertEquals(" TAY", source.getCode().get(++n)); //          ; 2
//    Assert.assertEquals(" TXA", source.getCode().get(++n)); //          ; 2
//    Assert.assertEquals(" ROL A", source.getCode().get(++n)); //        ; 2
//    Assert.assertEquals(" TAX", source.getCode().get(++n)); //          ; 2
//    Assert.assertEquals(" CLC", source.getCode().get(++n)); //          ; 2 add %1 to the nth value
//    Assert.assertEquals(" TYA", source.getCode().get(++n)); //          ; 2
//    Assert.assertEquals(" ADC # <Y", source.getCode().get(++n)); //     ; 2
//    Assert.assertEquals(" STA @PUTARRAY", source.getCode().get(++n)); //; 3
//    Assert.assertEquals(" TXA", source.getCode().get(++n)); //          ; 2
//    Assert.assertEquals(" ADC # >Y", source.getCode().get(++n)); //     ; 2
//    Assert.assertEquals(" STA @PUTARRAY+1", source.getCode().get(++n)); //  ; 3 in Summe 28 zyklen 17 Bytes
    Assert.assertEquals(" STX @PUTARRAY+1", source.getCode().get(++n)); //          ; 2
    Assert.assertEquals(" ASL A", source.getCode().get(++n)); //          ; 2
    Assert.assertEquals(" ROL @PUTARRAY+1", source.getCode().get(++n)); //          ; 2
    Assert.assertEquals(" ADC #<Y", source.getCode().get(++n)); //          ; 2
    Assert.assertEquals(" STA @PUTARRAY", source.getCode().get(++n)); //          ; 2
    Assert.assertEquals(" LDA @PUTARRAY+1", source.getCode().get(++n)); //          ; 2
    Assert.assertEquals(" ADC #>Y", source.getCode().get(++n)); //          ; 2
    Assert.assertEquals(" STA @PUTARRAY+1", source.getCode().get(++n)); //          ; 2

    
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
  public void testAssignmentToSplitWordArray() {
    Source source = new Source("y[1]:=x").setVerboseLevel(2);
    source.addVariable("X", Type.WORD);
    source.addVariable("Y", Type.WORD_SPLIT_ARRAY);

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

//    Assert.assertEquals(" STX @PUTARRAY+1", source.getCode().get(++n));
//    Assert.assertEquals(" TYA", source.getCode().get(++n));
//    Assert.assertEquals(" LDX @PUTARRAY", source.getCode().get(++n));
//    Assert.assertEquals(" STA Y_LOW,X", source.getCode().get(++n));
//    Assert.assertEquals(" LDA @PUTARRAY+1", source.getCode().get(++n));
//    Assert.assertEquals(" STA Y_HIGH,X", source.getCode().get(++n));

    Assert.assertEquals(" TXA", source.getCode().get(++n));
    Assert.assertEquals(" LDX @PUTARRAY", source.getCode().get(++n));
    Assert.assertEquals(" STA Y_HIGH,X", source.getCode().get(++n));
    Assert.assertEquals(" TYA", source.getCode().get(++n));
    Assert.assertEquals(" STA Y_LOW,X", source.getCode().get(++n));
  }

  @Test
  public void testAssignmentNumberToSplitWordArray() {
    Source source = new Source("y[0]:=1").setVerboseLevel(2);
    source.addVariable("Y", Type.WORD_SPLIT_ARRAY);

    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Assignment(source).assign(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertEquals(Type.WORD, source.getTypeOfLastExpression());


    int n=-1;
    Assert.assertEquals("; (5)", source.getCode().get(++n));
    Assert.assertEquals(" LDY #<0", source.getCode().get(++n));
    Assert.assertEquals(" STY @PUTARRAY", source.getCode().get(++n));

    Assert.assertEquals("; (5)", source.getCode().get(++n));
    Assert.assertEquals(" LDY #<1", source.getCode().get(++n));
    Assert.assertEquals(" LDX #>1", source.getCode().get(++n));

//    Assert.assertEquals(" STX @PUTARRAY+1", source.getCode().get(++n));
//    Assert.assertEquals(" TYA", source.getCode().get(++n));
//    Assert.assertEquals(" LDX @PUTARRAY", source.getCode().get(++n));
//    Assert.assertEquals(" STA Y_LOW,X", source.getCode().get(++n));
//    Assert.assertEquals(" LDA @PUTARRAY+1", source.getCode().get(++n));
//    Assert.assertEquals(" STA Y_HIGH,X", source.getCode().get(++n));

    Assert.assertEquals(" TXA", source.getCode().get(++n));
    Assert.assertEquals(" LDX @PUTARRAY", source.getCode().get(++n));
    Assert.assertEquals(" STA Y_HIGH,X", source.getCode().get(++n));
    Assert.assertEquals(" TYA", source.getCode().get(++n));
    Assert.assertEquals(" STA Y_LOW,X", source.getCode().get(++n));
  }

//
//                                           OO
//                                           OO
//  OOOOOOO   OOOOOOO  OOOOOOOO   OOOOOOO  OOOOOO
// OO     OO OO     OO OO     OO OO          OO
// OO        OO     OO OO     OO  OOOOOOO    OO
// OO     OO OO     OO OO     OO        OO   OO
//  OOOOOOO   OOOOOOO  OO     OO  OOOOOOO     OOO
//
// is a named variable with a given value maybe byte, word, int8, ...


  @Test(expected = IllegalStateException.class)
  public void testAssignmentXisConst() {
    Source source = new Source("x:=1").setVerboseLevel(2);
    source.addVariable("X", Type.CONST);

    Symbol symbol = source.nextElement();

    /* Symbol nextSymbol = */ new Assignment(source).assign(symbol).build();
  }

  @Test
  public void testAssignmentX_Byte_equal_Y_length() {
    Source source = new Source("x:=y_length").setVerboseLevel(2);
    source.addVariable("X", Type.BYTE);

    // TODO: Y als Array anlegen, dann soll Y_LENGTH als const mit angelegt werden
    source.addVariable("Y", Type.BYTE_ARRAY, 123);
//    source.addVariable("Y_LENGTH", Type.CONST);
//    source.setVariableAddress("Y_LENGTH", "123");

    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Assignment(source).assign(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertTrue(source.hasVariable("X"));
    int n=-1;
    Assert.assertEquals("; (5)", source.getCode().get(++n));
    Assert.assertEquals(" LDY #<123", source.getCode().get(++n));
    Assert.assertEquals(" STY X", source.getCode().get(++n));
  }

}
