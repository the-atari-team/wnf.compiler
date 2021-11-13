// cdw by 'The Atari Team' 2021
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import lla.privat.atarixl.compiler.expression.Type;

public class TestVariableDefinition {

  private VariableDefinition variableDefinitionSUT;

  @Before
  public void setUp() {
  }

  @Test
  public void testArtOfUsage() {
    variableDefinitionSUT = new VariableDefinition("NAME", Type.BYTE);

    Assert.assertFalse(variableDefinitionSUT.hasAnyAccess());
    Assert.assertFalse(variableDefinitionSUT.hasAllAccess());
    Assert.assertFalse(variableDefinitionSUT.hasReadAccess());
    Assert.assertFalse(variableDefinitionSUT.hasWriteAccess());

    variableDefinitionSUT.setRead();
    Assert.assertTrue(variableDefinitionSUT.hasReadAccess());

    variableDefinitionSUT.setWrite();
    Assert.assertTrue(variableDefinitionSUT.hasWriteAccess());

    Assert.assertTrue(variableDefinitionSUT.hasAllAccess());
  }


  @Test
  public void testSizeOfArray() {
    variableDefinitionSUT = new VariableDefinition("NAME", Type.BYTE_ARRAY, 42);

    Assert.assertEquals(42, variableDefinitionSUT.getSizeOfArray());

    List<String> arrayContent = Arrays.asList("Hallo");
    variableDefinitionSUT.setArray(arrayContent);
    Assert.assertEquals(1, variableDefinitionSUT.getSizeOfArray());
  }
}
