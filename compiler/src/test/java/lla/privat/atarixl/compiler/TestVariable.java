// cdw by 'The Atari Team' 2020
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler;

import org.junit.Assert;
import org.junit.Test;

import lla.privat.atarixl.compiler.expression.Type;
import lla.privat.atarixl.compiler.source.Source;

public class TestVariable {

  @Test
  public void testVariableAssignment() {
    Source source = new Source("byte x");
    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Variable(source).variable(symbol).build();

    Assert.assertTrue(source.hasVariable("X"));

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
  public void testVariableByteWithUnknownAddress() {
    Source source = new Source("byte x=@");
    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Variable(source).variable(symbol).build();
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertTrue(source.hasVariable("X"));
    Assert.assertEquals("@", source.getVariableAddress("X"));
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
  public void testVariableByteArrayWithEmptyArray() {
    Source source = new Source("byte array x[2]");
    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Variable(source).variable(symbol).build();
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertTrue(source.hasVariable("X"));
    Assert.assertEquals(2, source.getVariableArraySize("X"));
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
    Source source = new Source(" byte array x[2] = [1,2,3,$af,%...11...,-1, variable]");
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
    Source source = new Source(" byte array x[256] = [1,2]");

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
  public void testVariableAssignmentString() {
    Source source = new Source(" string hallo = ['Hallo']");

    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Variable(source).variable(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertTrue(source.hasVariable("HALLO"));
    Assert.assertEquals(Type.BYTE_ARRAY, source.getVariableType("HALLO"));
    Assert.assertEquals(2, source.getVariableArraySize("HALLO"));
  }

  @Test(expected = IllegalStateException.class)
  public void testVariableIllegalType() {
    Source source = new Source(" long hallo");

    Symbol symbol = source.nextElement();

    /*Symbol nextSymbol =*/ new Variable(source).variable(symbol).build();
  }

  @Test
  public void testVariableWord() {
    Source source = new Source("word x");
    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Variable(source).variable(symbol).build();

    Assert.assertTrue(source.hasVariable("X"));

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

  @Test
  public void testVariableAssignmentWordArray() {
    Source source = new Source(" word array y[2] = ['Hallo', 'Welt', 1, variable]");
    source.addVariable("VARIABLE", Type.WORD);

    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Variable(source).variable(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertTrue(source.hasVariable("Y"));
    Assert.assertEquals(Type.WORD_ARRAY, source.getVariableType("Y"));
    Assert.assertEquals(4, source.getVariableArraySize("Y"));
  }

  @Test
  public void testVariableAssignmentWordArrayWithADR() {
    Source source = new Source(" word array y[2] = [adr:godmode]");
    source.addVariable("GODMODE", Type.STRING);

    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Variable(source).variable(symbol).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    Assert.assertTrue(source.hasVariable("Y"));
    Assert.assertEquals(Type.WORD_ARRAY, source.getVariableType("Y"));
    Assert.assertEquals(1, source.getVariableArraySize("Y"));
  }
}
