// cdw by 'The Atari Team' 2020
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler.source;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import lla.privat.atarixl.compiler.Symbol;
import lla.privat.atarixl.compiler.SymbolEnum;
import lla.privat.atarixl.compiler.expression.Type;

public class TestSource {

  @Before
  public void setUp() {
  }

  @Test
  public void testGetSymbolEmpty() {
    String program = "";
    Source sourceSUT = new Source(program);

    Symbol symbol = sourceSUT.nextElement();
    Assert.assertEquals("", symbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, symbol.getId());
  }

  @Test
  public void testVariable() {
    String program = "";
    Source sourceSUT = new Source(program);

    sourceSUT.addVariable("hallo", Type.BYTE);

    Assert.assertTrue(sourceSUT.hasVariable("hallo"));

    Assert.assertEquals(0, sourceSUT.getVariablePosition("hallo"));

    Assert.assertEquals("hallo", sourceSUT.getVariableAt(0));

    Assert.assertEquals(1, sourceSUT.countOfVariables());

    Assert.assertEquals(Type.BYTE, sourceSUT.getVariableType("hallo"));
  }

  @Test
  public void testUnknownVariable() {
    String program = "";
    Source sourceSUT = new Source(program);

    Assert.assertFalse(sourceSUT.hasVariable("unknown"));

    Assert.assertEquals(-1, sourceSUT.getVariablePosition("unknown"));

    Assert.assertEquals(0, sourceSUT.countOfVariables());

    Assert.assertEquals(Type.UNKNOWN, sourceSUT.getVariableType("unknown"));

  }

  @Test
  public void testGenerateVariables_byte() {
    String program = "";
    Source sourceSUT = new Source(program);

    sourceSUT.addVariable("HALLO", Type.BYTE);

    sourceSUT.generateVariables();

    List<String> code = sourceSUT.getCode();

    int n = -1;
    Assert.assertEquals("HALLO .BYTE 0", code.get(++n));
  }

  @Test
  public void testGenerateVariables_byteWithAddress() {
    String program = "";
    Source sourceSUT = new Source(program);

    sourceSUT.addVariable("HALLO", Type.BYTE);
    sourceSUT.setVariableAddress("HALLO", "123");

    sourceSUT.generateVariables();

    List<String> code = sourceSUT.getCode();

    int n = -1;
    Assert.assertEquals("HALLO = 123", code.get(++n));
  }

  @Test
  public void testGenerateVariables_byteArrayWithLength() {
    String program = "";
    Source sourceSUT = new Source(program);

    sourceSUT.addVariable("HALLO", Type.BYTE_ARRAY, 15);

    sourceSUT.generateVariables();
//    sourceSUT.generateVariable(new VariableDefinition("HALLO", Type.BYTE_ARRAY, 15));

    List<String> code = sourceSUT.getCode();

    int n = -1;
    Assert.assertEquals("HALLO", code.get(++n));
    Assert.assertEquals(" *=*+15", code.get(++n));
  }

  @Test
  public void testGenerateVariables_byteArrayWithAddress() {
    String program = "";
    Source sourceSUT = new Source(program);

    sourceSUT.addVariable("HALLO", Type.BYTE_ARRAY, 10);
    sourceSUT.setVariableAddress("HALLO", "123");

    sourceSUT.generateVariables();

    List<String> code = sourceSUT.getCode();

    int n = -1;
    Assert.assertEquals("HALLO = 123", code.get(++n));
  }

  @Test
  public void testGenerateVariables_byteArrayWithValues() {
    String program = "";
    Source sourceSUT = new Source(program);

    sourceSUT.addVariable("HALLO", Type.BYTE_ARRAY, 10);
    List<String> arrayValues = Arrays.asList("1", "-1", "255");
    sourceSUT.setVariableArray("HALLO", arrayValues);

    sourceSUT.generateVariables();

    List<String> code = sourceSUT.getCode();

    int n = -1;
    Assert.assertEquals("HALLO", code.get(++n));
    Assert.assertEquals(" .BYTE 1,-1,255", code.get(++n));
  }

  @Test
  public void testGenerateVariables_byteArrayWithString() {
    String program = "";
    Source sourceSUT = new Source(program);

    sourceSUT.addVariable("HALLO", Type.BYTE_ARRAY, 10);
    List<String> arrayValues = new ArrayList<>();
    arrayValues.add("'Dies ist ein String'");
    arrayValues.add("255");
    sourceSUT.setVariableArray("HALLO", arrayValues);

    sourceSUT.generateVariables();

    List<String> code = sourceSUT.getCode();

    int n = -1;
    Assert.assertEquals("HALLO", code.get(++n));
    Assert.assertEquals(" .BYTE \"Dies ist ein String\",255", code.get(++n));
  }

  @Test
  public void testGenerateVariables_StringWithString() {
    String program = "";
    Source sourceSUT = new Source(program);

    sourceSUT.addVariable("'Dies ist ein String'", Type.STRING);

    sourceSUT.generateVariables();

    List<String> code = sourceSUT.getCode();

    int n = -1;
    Assert.assertEquals("?STRING0", code.get(++n));
    Assert.assertEquals(" .BYTE \"Dies ist ein String\"," + Source.STRING_END_MARK, code.get(++n));
  }

  @Test
  public void testGenerateVariables_word() {
    String program = "";
    Source sourceSUT = new Source(program);

    sourceSUT.addVariable("HALLO", Type.WORD);

    sourceSUT.generateVariables();

    List<String> code = sourceSUT.getCode();

    int n = -1;
    Assert.assertEquals("HALLO .WORD 0", code.get(++n));
  }

  @Test
  public void testGenerateVariables_wordWithAddress() {
    String program = "";
    Source sourceSUT = new Source(program);

    sourceSUT.addVariable("HALLO", Type.WORD);
    sourceSUT.setVariableAddress("HALLO", "123");

    sourceSUT.generateVariables();

    List<String> code = sourceSUT.getCode();

    int n = -1;
    Assert.assertEquals("HALLO = 123", code.get(++n));
  }

  @Test
  public void testGenerateVariables_wordArrayWithLength() {
    String program = "";
    Source sourceSUT = new Source(program);

    sourceSUT.addVariable("HALLO", Type.WORD_ARRAY, 15);

    sourceSUT.generateVariables();

    List<String> code = sourceSUT.getCode();

    int n = -1;
    Assert.assertEquals("HALLO", code.get(++n));
    Assert.assertEquals(" *=*+30", code.get(++n));
  }

  @Test
  public void testGenerateVariables_wordArrayWithAddress() {
    String program = "";
    Source sourceSUT = new Source(program);

    sourceSUT.addVariable("HALLO", Type.WORD_ARRAY, 10);
    sourceSUT.setVariableAddress("HALLO", "123");

    sourceSUT.generateVariables();

    List<String> code = sourceSUT.getCode();

    int n = -1;
    Assert.assertEquals("HALLO = 123", code.get(++n));
  }

  @Test
  public void testGenerateVariables_wordArrayWithValues() {
    String program = "";
    Source sourceSUT = new Source(program);

    sourceSUT.addVariable("HALLO", Type.WORD_ARRAY, 10);
    List<String> arrayValues = Arrays.asList("1", "-1", "255");
    sourceSUT.setVariableArray("HALLO", arrayValues);

    sourceSUT.generateVariables();

    List<String> code = sourceSUT.getCode();

    int n = -1;
    Assert.assertEquals("HALLO", code.get(++n));
    Assert.assertEquals(" .WORD 1,-1,255", code.get(++n));
  }

}
