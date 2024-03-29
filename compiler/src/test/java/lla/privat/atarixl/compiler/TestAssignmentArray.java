// cdw by 'The Atari Team' 2022
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import lla.privat.atarixl.compiler.expression.Type;
import lla.privat.atarixl.compiler.source.Source;

public class TestAssignmentArray {

  
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
    source.addVariable("Y", Type.BYTE_ARRAY, 2, ReadOnly.NO);
    
    source.getOptions().setBoundsCheck(true);
    source.setFilename("assignment-test");
    
    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Assignment(source).assign(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertTrue(source.hasVariable("X"));

    int n=-1;
    Assert.assertEquals("; (5)", source.getCode().get(++n));
    Assert.assertEquals(" LDY #<1", source.getCode().get(++n));

    // BoundsCheck
    Assert.assertEquals(";", source.getCode().get(++n));
    Assert.assertEquals("; WRITE BOUNDS CHECK", source.getCode().get(++n));
    Assert.assertEquals(";", source.getCode().get(++n));
    Assert.assertEquals("; (y+256*x) must be less than 2", source.getCode().get(++n));
    Assert.assertEquals(";", source.getCode().get(++n));
    Assert.assertEquals(" LDX #0", source.getCode().get(++n));
    Assert.assertEquals(" CPY #<2", source.getCode().get(++n));
    Assert.assertEquals(" TXA", source.getCode().get(++n));
    Assert.assertEquals(" SBC #>2", source.getCode().get(++n));
    Assert.assertEquals(" BCC __BOUNDS_OK0", source.getCode().get(++n));
    Assert.assertEquals(" JSR @PANIC_BOUNDS_CHECK", source.getCode().get(++n));
    Assert.assertEquals(" .WORD 2", source.getCode().get(++n));
    Assert.assertEquals(" .WORD 1", source.getCode().get(++n));
    Assert.assertEquals(" .WORD ?STRING3", source.getCode().get(++n));
    
    Assert.assertEquals("__BOUNDS_OK0", source.getCode().get(++n));
    Assert.assertEquals(" STY @PUTARRAY", source.getCode().get(++n));
    
    Assert.assertEquals("; (6)", source.getCode().get(++n));
    Assert.assertEquals(" LDY X", source.getCode().get(++n));
    Assert.assertEquals(" LDX X+1", source.getCode().get(++n));
    Assert.assertEquals(" TYA", source.getCode().get(++n));
    Assert.assertEquals(" LDX @PUTARRAY", source.getCode().get(++n));
    Assert.assertEquals(" STA Y,X", source.getCode().get(++n));
  }

  @Test
  public void testAssignmentToByteArrayWithIndex() {
    Source source = new Source("y[index]:=1").setVerboseLevel(2);
    source.addVariable("INDEX", Type.BYTE);
    source.addVariable("Y", Type.BYTE_ARRAY);

    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Assignment(source).assign(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertTrue(source.hasVariable("INDEX"));

    int n=-1;
    Assert.assertEquals("; (6)", source.getCode().get(++n));
    Assert.assertEquals(" LDY INDEX", source.getCode().get(++n));
    Assert.assertEquals(" STY @PUTARRAY", source.getCode().get(++n));

    Assert.assertEquals("; (5)", source.getCode().get(++n));
    Assert.assertEquals(" LDY #<1", source.getCode().get(++n));
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
    Assert.assertEquals(" ADC #<BIG", source.getCode().get(++n));
    Assert.assertEquals(" STA @PUTARRAY0", source.getCode().get(++n));
    Assert.assertEquals(" TXA", source.getCode().get(++n));
    Assert.assertEquals(" ADC #>BIG", source.getCode().get(++n));
    Assert.assertEquals(" STA @PUTARRAY0+1", source.getCode().get(++n));

    Assert.assertEquals("; (6)", source.getCode().get(++n));
    Assert.assertEquals(" LDY X", source.getCode().get(++n));
    Assert.assertEquals(" LDX X+1", source.getCode().get(++n));

    Assert.assertEquals(" TYA", source.getCode().get(++n));
    Assert.assertEquals(" LDY #0", source.getCode().get(++n));
 // ldy @putarray_byteindex
    Assert.assertEquals(" STA (@PUTARRAY0),Y", source.getCode().get(++n));
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
 
  @Test
  public void testAssignmentSmallByteListToFatByteArrayMem() {
    Source source = new Source("@mem[710]:=[$01, $02]").setVerboseLevel(2);
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
    Assert.assertEquals(" LDY #<1", source.getCode().get(++n));
    Assert.assertEquals(" TYA", source.getCode().get(++n));
    Assert.assertEquals(" LDY #0", source.getCode().get(++n));
 // ldy @putarray_byteindex
    Assert.assertEquals(" STA (@PUTARRAY),Y", source.getCode().get(++n));
    
    Assert.assertEquals("; (5)", source.getCode().get(++n));
    Assert.assertEquals(" LDY #<2", source.getCode().get(++n));
    Assert.assertEquals(" TYA", source.getCode().get(++n));
    Assert.assertEquals(" LDY #1", source.getCode().get(++n));
 // ldy @putarray_byteindex
    Assert.assertEquals(" STA (@PUTARRAY),Y", source.getCode().get(++n));
  }

  @Test
  public void testAssignmentFromFatByteArrayMem() {
    Source source = new Source("color[1] := @mem[710]").setVerboseLevel(2);
    source.addVariable("COLOR", Type.BYTE_ARRAY);

    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Assignment(source).assign(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertEquals(Type.BYTE, source.getTypeOfLastExpression());

    List<String> code = source.getCode();
    int n=-1;
    Assert.assertEquals("; (5)", code.get(++n));
    Assert.assertEquals(" LDY #<1", code.get(++n));
    Assert.assertEquals(" STY @PUTARRAY", code.get(++n));

    Assert.assertEquals("; (5)", code.get(++n));
    Assert.assertEquals(" LDY #<710", code.get(++n));
    Assert.assertEquals(" LDX #>710", code.get(++n));
    Assert.assertEquals("; (12.2)", code.get(++n));
    Assert.assertEquals(" STY @GETARRAY", code.get(++n));
    Assert.assertEquals(" STX @GETARRAY+1", code.get(++n));
    Assert.assertEquals(" LDY #0", code.get(++n));
    Assert.assertEquals(" LDA (@GETARRAY),Y", code.get(++n));
    Assert.assertEquals(" TAY", code.get(++n));
    Assert.assertEquals(" TYA", code.get(++n));
    Assert.assertEquals(" LDX @PUTARRAY", code.get(++n));
    Assert.assertEquals(" STA COLOR,X", code.get(++n));
    
    Assert.assertEquals(code.size(), n+1);
  }
  
  @Test
  public void testAssignmentFromFatByteArrayMemIndexVar() {
    Source source = new Source("color[1] := @mem[design_addr+1]").setVerboseLevel(2);
    source.addVariable("COLOR", Type.BYTE_ARRAY);
    source.addVariable("DESIGN_ADDR", Type.WORD);

    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Assignment(source).assign(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertEquals(Type.WORD, source.getTypeOfLastExpression());

    List<String> code = source.getCode();
    int n=-1;
    Assert.assertEquals("; (5)", code.get(++n));
    Assert.assertEquals(" LDY #<1", code.get(++n));
    Assert.assertEquals(" STY @PUTARRAY", code.get(++n));

    Assert.assertEquals("; (3)", code.get(++n));
    Assert.assertEquals(" CLC", code.get(++n));
    Assert.assertEquals(" LDA DESIGN_ADDR", code.get(++n));
    Assert.assertEquals(" ADC #<1", code.get(++n));
    Assert.assertEquals(" TAY", code.get(++n));
    Assert.assertEquals(" LDA DESIGN_ADDR+1", code.get(++n));
    Assert.assertEquals(" ADC #>1", code.get(++n));
    Assert.assertEquals(" TAX", code.get(++n));
    
    Assert.assertEquals("; (12.2)", code.get(++n));
    Assert.assertEquals(" STY @GETARRAY", code.get(++n));
    Assert.assertEquals(" STX @GETARRAY+1", code.get(++n));
    Assert.assertEquals(" LDY #0", code.get(++n));
    Assert.assertEquals(" LDA (@GETARRAY),Y", code.get(++n));
    Assert.assertEquals(" LDX #0", code.get(++n));
    Assert.assertEquals(" TAY", code.get(++n));
    Assert.assertEquals(" TYA", code.get(++n));
    Assert.assertEquals(" LDX @PUTARRAY", code.get(++n));
    Assert.assertEquals(" STA COLOR,X", code.get(++n));
    
    Assert.assertEquals(code.size(), n+1);
  }
 
  
  @Test(expected = IllegalStateException.class)
  public void testAssignmentWrongAssignToByteArray() {
    Source source = new Source("big:=x").setVerboseLevel(2);
    source.addVariable("X", Type.WORD);
    source.addVariable("BIG", Type.FAT_BYTE_ARRAY);

    Symbol symbol = source.nextElement();

    /* Symbol nextSymbol =*/ new Assignment(source).assign(symbol).build();

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

  
  @Test
  public void testAssignmentToFatByteArrayWithUint16Variable() {
    Source source = new Source("big[u]:=x").setVerboseLevel(2);
    source.addVariable("X", Type.WORD);
    source.addVariable("U", Type.UINT16);
    source.addVariable("BIG", Type.FAT_BYTE_ARRAY);

    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Assignment(source).assign(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertEquals(Type.WORD, source.getTypeOfLastExpression());

    int n=-1;
    Assert.assertEquals("; (6)", source.getCode().get(++n));
    Assert.assertEquals(" LDY U", source.getCode().get(++n));
    Assert.assertEquals(" LDX U+1", source.getCode().get(++n));
// sty @putarray_byteindex
// clc
// txa
// adc #>big
// sta @putarray+1
    
// old: 16 Takte    
    Assert.assertEquals(" TYA", source.getCode().get(++n));
//    Assert.assertEquals(" PUTARRAYB BIG", source.getCode().get(++n));
    Assert.assertEquals(" CLC", source.getCode().get(++n));
    Assert.assertEquals(" ADC #<BIG", source.getCode().get(++n));
    Assert.assertEquals(" STA @PUTARRAY0", source.getCode().get(++n));
    Assert.assertEquals(" TXA", source.getCode().get(++n));
    Assert.assertEquals(" ADC #>BIG", source.getCode().get(++n));
    Assert.assertEquals(" STA @PUTARRAY0+1", source.getCode().get(++n));

    Assert.assertEquals("; (6)", source.getCode().get(++n));
    Assert.assertEquals(" LDY X", source.getCode().get(++n));
    Assert.assertEquals(" LDX X+1", source.getCode().get(++n));

    Assert.assertEquals(" TYA", source.getCode().get(++n));
    Assert.assertEquals(" LDY #0", source.getCode().get(++n));
 // ldy @putarray_byteindex
    Assert.assertEquals(" STA (@PUTARRAY0),Y", source.getCode().get(++n));
  }
  
}
