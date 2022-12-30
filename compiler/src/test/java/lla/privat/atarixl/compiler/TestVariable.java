// cdw by 'The Atari Team' 2022
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler;

import org.junit.Assert;
import org.junit.Test;

import lla.privat.atarixl.compiler.expression.Type;
import lla.privat.atarixl.compiler.source.Source;

public class TestVariable {

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
  public void testVariableByteDefinition() {
    Source source = new Source("byte x");
    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Variable(source).variable(symbol).build();

    Assert.assertTrue(source.hasVariable("X"));
    Assert.assertFalse(source.getVariableType("X").isSigned());

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());
  }

  @Test
  public void testVariableAssignment2Variables() {
    Source source = new Source("byte x,y");
    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Variable(source).variable(symbol).build();

    Assert.assertTrue(source.hasVariable("X"));
    Assert.assertTrue(source.hasVariable("Y"));

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());
  }

  @Test
  public void testVariableByteWithAddress() {
    Source source = new Source("byte x=710");
    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Variable(source).variable(symbol).build();
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertTrue(source.hasVariable("X"));
    Assert.assertEquals("710", source.getVariableAddress("X"));
  }

  @Test
  public void testVariableByteWithUnknownAddress() {
    Source source = new Source("byte x=@");
    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Variable(source).variable(symbol).build();
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertTrue(source.hasVariable("X"));
    Assert.assertEquals("@", source.getVariableAddress("X"));
  }

//
//  OO              OO    OOOOOO
//                  OO   OO    OO
// OOO  OOOOOOOO  OOOOOO OO    OO
//  OO  OO     OO   OO    OOOOOO
//  OO  OO     OO   OO   OO    OO
//  OO  OO     OO   OO   OO    OO
// OOOO OO     OO    OOO  OOOOOO
//
// int8 is a single signed byte or 8 bit

  @Test
  public void testVariableInt8Definition() {
    Source source = new Source("int8 x");
    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Variable(source).variable(symbol).build();

    Assert.assertTrue(source.hasVariable("X"));
    Assert.assertTrue(source.getVariableType("X").isSigned());
    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());
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
  public void testVariableByteArrayWithEmptyArray() {
    Source source = new Source("byte array x[2]");
    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Variable(source).variable(symbol).build();
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertTrue(source.hasVariable("X"));
    Assert.assertEquals(2, source.getVariableArraySize("X"));
    Assert.assertTrue(source.hasVariable("X_LENGTH"));
  }

  @Test
  public void testVariableFatByteArrayWithEmptyArray() {
    Source source = new Source("byte array x[512]");
    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Variable(source).variable(symbol).build();
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertTrue(source.hasVariable("X"));
    Assert.assertEquals(512, source.getVariableArraySize("X"));
    Assert.assertEquals(Type.FAT_BYTE_ARRAY, source.getVariableType("X"));
  }

  @Test
  public void testVariableFatByteArrayWithEmptyArrayFromConst() {
    Source source = new Source("byte array x[VALUE]");
    source.addVariable("VALUE", Type.CONST);
    source.setVariableAddress("VALUE", "512");

    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Variable(source).variable(symbol).build();
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertTrue(source.hasVariable("X"));
    Assert.assertEquals(512, source.getVariableArraySize("X"));
    Assert.assertEquals(Type.FAT_BYTE_ARRAY, source.getVariableType("X"));
  }

  @Test
  public void testVariableByteArrayWithAddress() {
    Source source = new Source("byte array x[2] = 123");
    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Variable(source).variable(symbol).build();
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertTrue(source.hasVariable("X"));
    Assert.assertEquals(2, source.getVariableArraySize("X"));
    Assert.assertEquals("123", source.getVariableAddress("X"));
  }

  @Test
  public void testVariableByteArrayWithAddressAsName() {
    Source source = new Source("byte array x[2] = hallo");
    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Variable(source).variable(symbol).build();
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertTrue(source.hasVariable("X"));
    Assert.assertEquals(2, source.getVariableArraySize("X"));
    Assert.assertEquals("HALLO", source.getVariableAddress("X"));
  }

  @Test
  public void testVariableAssignmentByteArray() {
    Source source = new Source("byte array x[2] = [1,2,3,$af,%...11...,-1, variable]");
    source.addVariable("VARIABLE", Type.BYTE);

    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Variable(source).variable(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertTrue(source.hasVariable("X"));
    Assert.assertEquals(1, source.getVariableSize("X"));
    Assert.assertEquals(7, source.getVariableArraySize("X"));
  }

  @Test
  public void testVariableAssignmentFatByteArray() {
    Source source = new Source("byte array x[257] = [1,2]");

    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Variable(source).variable(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertTrue(source.hasVariable("X"));
    Assert.assertEquals(1, source.getVariableSize("X"));
    Assert.assertEquals(Type.FAT_BYTE_ARRAY, source.getVariableType("X"));
    Assert.assertEquals(2, source.getVariableArraySize("X"));
  }

  @Test
  public void testVariableAssignmentByteArrayWithChars() {
    Source source = new Source("byte array x[2] = ['1','2']");
    source.addVariable("VARIABLE", Type.BYTE);

    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Variable(source).variable(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertTrue(source.hasVariable("X"));
    Assert.assertEquals(1, source.getVariableSize("X"));
    Assert.assertEquals(2, source.getVariableArraySize("X"));
  }

  @Test(expected = IllegalStateException.class)
  public void testByteArrayWrongCount() {
    Source source = new Source("byte array a[] = ['Hallo', 'Welt']");

    Symbol symbol = source.nextElement();

    /*Symbol nextSymbol =*/ new Variable(source).variable(symbol).build();
  }

//
//             OO              OO
//             OO
//  OOOOOOO  OOOOOO OOOOOOOO  OOO  OOOOOOOO   OOOOOOOO
// OO          OO   OO     OO  OO  OO     OO OO     OO
//  OOOOOOO    OO   OO         OO  OO     OO OO     OO
//        OO   OO   OO         OO  OO     OO  OOOOOOOO
//  OOOOOOO     OOO OO        OOOO OO     OO        OO
//                                           OOOOOOOO
//
// string is like byte array is a list of bytes with a defined end of $ff

  @Test
  public void testVariableAssignmentString() {
    Source source = new Source("string hallo = ['Hallo']");

    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Variable(source).variable(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertTrue(source.hasVariable("HALLO"));
    Assert.assertEquals(Type.STRING, source.getVariableType("HALLO"));
    Assert.assertEquals(2, source.getVariableArraySize("HALLO"));

    Assert.assertTrue(source.hasVariable("HALLO_LENGTH"));
    Assert.assertTrue(source.hasVariable("HALLO_ELEMENTS"));
  }


//
//  OO              OO    OOOOOO
//                  OO   OO    OO
// OOO  OOOOOOOO  OOOOOO OO    OO          OOOOOOOO OOOOOOOO  OOOOOOOO   OOOOOOOO OO     OO
//  OO  OO     OO   OO    OOOOOO          OO     OO OO     OO OO     OO OO     OO OO     OO
//  OO  OO     OO   OO   OO    OO         OO     OO OO        OO        OO     OO OO     OO
//  OO  OO     OO   OO   OO    OO         OO     OO OO        OO        OO     OO  OOOOOOOO
// OOOO OO     OO    OOO  OOOOOO           OOOOOOOO OO        OO         OOOOOOOO        OO
//                                                                                OOOOOOOO

  @Test(expected = IllegalStateException.class)
  public void testVariableInt8ArrayWithEmptyArray() {
    Source source = new Source("int8 array x[2]");
    Symbol symbol = source.nextElement();

    /* Symbol nextSymbol = */ new Variable(source).variable(symbol).build();
  }

//
//            OO              OO      OO     OOOOOO
//                            OO     OOO    OO
// OO     OO OOO  OOOOOOOO  OOOOOO  OOOO    OO                OOOOOOOO OOOOOOOO  OOOOOOOO   OOOOOOOO OO     OO
// OO     OO  OO  OO     OO   OO      OO    OOOOOOO          OO     OO OO     OO OO     OO OO     OO OO     OO
// OO     OO  OO  OO     OO   OO      OO    OO    OO         OO     OO OO        OO        OO     OO OO     OO
// OO     OO  OO  OO     OO   OO      OO    OO    OO         OO     OO OO        OO        OO     OO  OOOOOOOO
//  OOOOOOOO OOOO OO     OO    OOO OOOOOOOO  OOOOOO           OOOOOOOO OO        OO         OOOOOOOO        OO
//                                                                                                   OOOOOOOO

  @Test(expected = IllegalStateException.class)
  public void testVariableUInt16ArrayWithEmptyArray() {
    Source source = new Source("uint16 array x[2]");
    Symbol symbol = source.nextElement();

    /* Symbol nextSymbol = */ new Variable(source).variable(symbol).build();
  }

  @Test(expected = IllegalStateException.class)
  public void testVariableIllegalType() {
    Source source = new Source("long hallo");

    Symbol symbol = source.nextElement();

    /*Symbol nextSymbol =*/ new Variable(source).variable(symbol).build();
  }

//
//  OO              OO      OO     OOOOOO
//                  OO     OOO    OO
// OOO  OOOOOOOO  OOOOOO  OOOO    OO
//  OO  OO     OO   OO      OO    OOOOOOO
//  OO  OO     OO   OO      OO    OO    OO
//  OO  OO     OO   OO      OO    OO    OO
// OOOO OO     OO    OOO OOOOOOOO  OOOOOO
//
// int16 is a double byte (2 bytes) signed

  @Test
  public void testVariableInt16Definition() {
    Source source = new Source("int16 x");
    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Variable(source).variable(symbol).build();

    Assert.assertTrue(source.hasVariable("X"));
    Assert.assertTrue(source.getVariableType("X").isSigned());
    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());
  }


  //
//            OO              OO      OO     OOOOOO
//                            OO     OOO    OO
// OO     OO OOO  OOOOOOOO  OOOOOO  OOOO    OO
// OO     OO  OO  OO     OO   OO      OO    OOOOOOO
// OO     OO  OO  OO     OO   OO      OO    OO    OO
// OO     OO  OO  OO     OO   OO      OO    OO    OO
//  OOOOOOOO OOOO OO     OO    OOO OOOOOOOO  OOOOOO
//
// uint16 is a double byte (2 bytes) unsigned

  @Test
  public void testVariableUInt16Definition() {
    Source source = new Source("uint16 x");
    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Variable(source).variable(symbol).build();

    Assert.assertTrue(source.hasVariable("X"));
    Assert.assertFalse(source.getVariableType("X").isSigned());
    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());
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
  public void testVariableWord() {
    Source source = new Source("word x");
    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Variable(source).variable(symbol).build();

    Assert.assertTrue(source.hasVariable("X"));
    Assert.assertTrue(source.getVariableType("X").isSigned());

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());
    Assert.assertEquals(Type.WORD, source.getVariableType("X"));
  }

  @Test
  public void testVariableWord2Variables() {
    Source source = new Source("word x,y");
    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Variable(source).variable(symbol).build();

    Assert.assertTrue(source.hasVariable("X"));
    Assert.assertTrue(source.hasVariable("Y"));

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertEquals(Type.WORD, source.getVariableType("X"));
    Assert.assertEquals(Type.WORD, source.getVariableType("Y"));
  }

  @Test
  public void testVariableWordWithAddress() {
    Source source = new Source("word x=1");
    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Variable(source).variable(symbol).build();
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertTrue(source.hasVariable("X"));
    Assert.assertEquals(Type.WORD, source.getVariableType("X"));
  }

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
  public void testWordArraySizeTooBig() {
    Source source = new Source("word array y[257]");

    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Variable(source).variable(symbol).build();
    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertTrue(source.hasVariable("Y"));
    Assert.assertEquals(Type.FAT_WORD_ARRAY, source.getVariableType("Y"));
    Assert.assertEquals(257, source.getVariableArraySize("Y"));
  }

  @Test
  public void testWordArrayWithVariable() {
    Source source = new Source("word array y[257] = variable");
    source.addVariable("VARIABLE", Type.WORD);

    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Variable(source).variable(symbol).build();
    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertTrue(source.hasVariable("Y"));
    Assert.assertTrue(source.hasVariable("Y_LENGTH"));
    Assert.assertEquals(Type.FAT_WORD_ARRAY, source.getVariableType("Y"));
    Assert.assertEquals(257, source.getVariableArraySize("Y"));
  }

  @Test
  public void testWordArrayWithParameter() {
    Source source = new Source("word array args[1] = @parameter");
    source.addVariable("@PARAMETER", Type.WORD);

    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Variable(source).variable(symbol).build();
    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertTrue(source.hasVariable("ARGS"));
    Assert.assertEquals(Type.FAT_WORD_ARRAY, source.getVariableType("ARGS"));
    Assert.assertEquals(1, source.getVariableArraySize("ARGS"));
  }

  @Test
  public void testWordSplitArrayWithAddress() {
    Source source = new Source("word array y[2] = 123");

    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Variable(source).variable(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertTrue(source.hasVariable("Y"));
    Assert.assertEquals(Type.FAT_WORD_ARRAY, source.getVariableType("Y"));
    Assert.assertEquals(2, source.getVariableArraySize("Y"));

    Assert.assertTrue(source.hasVariable("Y_LENGTH"));
  }

  @Test(expected = IllegalStateException.class)
  public void testWordSplitArrayForcedWithAddress() {
    Source source = new Source("word array y[@split] = 123");

    Symbol symbol = source.nextElement();

    /* Symbol nextSymbol = */ new Variable(source).variable(symbol).build();
  }

  @Test
  public void testWordSplitArrayForcedWithVariable() {
    Source source = new Source("word array y[@SPLIT] = @variable");

    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Variable(source).variable(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertTrue(source.hasVariable("Y"));
    Assert.assertEquals(Type.WORD_SPLIT_ARRAY, source.getVariableType("Y"));
    Assert.assertEquals(-1, source.getVariableArraySize("Y"));

    Assert.assertTrue(source.hasVariable("Y_LENGTH"));
  }


  @Test
  public void testVariableAssignmentWordArray() {
    Source source = new Source("word array y[2] = ['Hallo', 'Welt', 1, variable]");
    source.addVariable("VARIABLE", Type.WORD);

    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Variable(source).variable(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertTrue(source.hasVariable("Y"));
    Assert.assertEquals(Type.WORD_SPLIT_ARRAY, source.getVariableType("Y"));
    Assert.assertEquals(4, source.getVariableArraySize("Y"));

    Assert.assertTrue(source.hasVariable("Y_LENGTH"));
    Assert.assertTrue(source.hasVariable("Y_ELEMENTS"));
  }

  @Test
  public void testVariableAssignmentWordArrayWithAtVariable() {
    Source source = new Source("word array arrayname[257] = [@variable, @variable2]");

    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Variable(source).variable(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertTrue(source.hasVariable("ARRAYNAME"));
    Assert.assertEquals(Type.FAT_WORD_ARRAY, source.getVariableType("ARRAYNAME"));
    Assert.assertEquals(2, source.getVariableArraySize("ARRAYNAME"));

    Assert.assertTrue(source.hasVariable("ARRAYNAME_LENGTH"));
    Assert.assertTrue(source.hasVariable("ARRAYNAME_ELEMENTS"));
  }

  @Test
  public void testVariableAssignmentWordArrayWithADR() {
    Source source = new Source("word array y[2] = [adr:godmode]");
    source.addVariable("GODMODE", Type.STRING);

    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Variable(source).variable(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertTrue(source.hasVariable("Y"));
    Assert.assertEquals(Type.WORD_SPLIT_ARRAY, source.getVariableType("Y"));
    Assert.assertEquals(1, source.getVariableArraySize("Y"));

    Assert.assertTrue(source.hasVariable("Y_LENGTH"));
    Assert.assertTrue(source.hasVariable("Y_ELEMENTS"));
  }

  @Test
  public void testWordSplitArray() {
    Source source = new Source("word array y[2]");

    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Variable(source).variable(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertTrue(source.hasVariable("Y"));
    Assert.assertEquals(Type.WORD_SPLIT_ARRAY, source.getVariableType("Y"));
    Assert.assertEquals(2, source.getVariableArraySize("Y"));
  }

  @Test
  public void testStringAssignmentWordSplitArray() {
    Source source = new Source("word array y[2] = ['Hello','World']");

    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Variable(source).variable(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertTrue(source.hasVariable("Y"));
    Assert.assertEquals(Type.WORD_SPLIT_ARRAY, source.getVariableType("Y"));
    Assert.assertEquals(2, source.getVariableArraySize("Y"));
  }

  @Test
  public void testVariableAssignmentWordSplitArray() {
    Source source = new Source("word array y[2] = [0, 1, variable]");
    source.addVariable("VARIABLE", Type.WORD);

    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Variable(source).variable(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertTrue(source.hasVariable("Y"));
    Assert.assertEquals(Type.WORD_SPLIT_ARRAY, source.getVariableType("Y"));
    Assert.assertEquals(3, source.getVariableArraySize("Y"));
  }

  @Test
  public void testVariableAssignmentWordSplitArrayWithFloatValue() {
    Source source = new Source("word array y[2] = [1.0, 1.49, 1.50, 1.51, 1.99]");
    source.addVariable("VARIABLE", Type.WORD);

    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Variable(source).variable(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertTrue(source.hasVariable("Y"));
    Assert.assertEquals(Type.WORD_SPLIT_ARRAY, source.getVariableType("Y"));
    Assert.assertEquals(5, source.getVariableArraySize("Y"));
    
    
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

  @Test
  public void testVariableConstWithNumber() {
    Source source = new Source("const color=710");
    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Variable(source).variable(symbol).build();
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertTrue(source.hasVariable("COLOR"));
    Assert.assertEquals("710", source.getVariableAddress("COLOR"));
  }

  @Test
  public void testVariableConstWithVariable() {
    Source source = new Source("const color=@color");
    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Variable(source).variable(symbol).build();
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertTrue(source.hasVariable("COLOR"));
    Assert.assertEquals("@COLOR", source.getVariableAddress("COLOR"));
  }

  // a const MUST follow by '='

  @Test(expected = IllegalStateException.class)
  public void testVariableConstNoNumber() {
    Source source = new Source("const color");
    Symbol symbol = source.nextElement();

    /*Symbol nextSymbol =*/ new Variable(source).variable(symbol).build();
  }
  
  @Test
  public void testReadonlyByteArray() {
    Source source = new Source("byte readonly array y[2] = 123");

    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Variable(source).variable(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertTrue(source.hasVariable("Y"));
    Assert.assertEquals(Type.BYTE_ARRAY, source.getVariableType("Y"));
    Assert.assertEquals(2, source.getVariableArraySize("Y"));
    Assert.assertTrue(source.isVariableReadOnly("Y"));
  }
  
  
}
