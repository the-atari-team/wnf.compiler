// cdw by 'The Atari Team' 2020
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler.optimization;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import lla.privat.atarixl.compiler.source.Source;

public class TestPeepholeOptimizer {

  private PeepholeOptimizer peepholeOptimizerSUT;

  private Source source;

  @Before
  public void setUp() {
    source = new Source("");
    peepholeOptimizerSUT = new PeepholeOptimizer(source, 1);
  }

  @Test
  public void testStartsWith() {
    String a = "TEST";
    Assert.assertTrue(a.startsWith("TEST"));
  }

  @Test
  public void testTayClcTya() {
    List<String> list = new ArrayList<>();
    list.add(" TAY");
    list.add(" CLC");
    list.add(" TYA");
    list.add(" ...");
    source.resetCode(list);

    peepholeOptimizerSUT.optimize().build();
    Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

    Assert.assertEquals(2, source.getCode().size());
  }

  @Test
  public void testTayXClcTya() {
    List<String> list = new ArrayList<>();
    list.add(" TAY");
    list.add(" ...");
    list.add(" CLC");
    list.add(" TYA");
    list.add(" ...");
    source.resetCode(list);

    peepholeOptimizerSUT.optimize().build();
    Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

    Assert.assertEquals(3, source.getCode().size());
  }

  @Test
  public void testTaySecTya() {
    List<String> list = new ArrayList<>();
    list.add(" TAY");
    list.add(" SEC");
    list.add(" TYA");
    list.add(" ...");
    source.resetCode(list);

    peepholeOptimizerSUT.optimize().build();
    Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

    Assert.assertEquals(2, source.getCode().size());
  }

  @Test
  public void testTayTya() {
    List<String> list = new ArrayList<>();
    list.add(" TAY");
    list.add(" TYA");
    list.add(" ...");
    source.resetCode(list);

    peepholeOptimizerSUT.optimize().build();
    Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

    Assert.assertEquals(1, source.getCode().size());
  }

  @Test
  public void testTayxxStySta() {
    List<String> list = new ArrayList<>();
    list.add(" TAY");
    list.add(" ...");
    list.add(" ...");
    list.add(" ...");
    list.add(" STY");
    list.add(" STA");
    list.add(" ...");
    source.resetCode(list);

    peepholeOptimizerSUT.optimize().build();
    Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

    Assert.assertEquals(6, source.getCode().size());
  }

  @Test
  public void testLdyxTya() {
    List<String> list = new ArrayList<>();
    list.add(" LDY");
    list.add(" ...");
    list.add(" TYA");
    list.add(" ...");
    source.resetCode(list);

    peepholeOptimizerSUT.optimize().build();
    Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

    Assert.assertEquals(3, source.getCode().size());
  }

  @Test
  public void testLdyTya() {
    List<String> list = new ArrayList<>();
    list.add(" LDY");
    list.add(" TYA");
    list.add(" ...");
    source.resetCode(list);

    peepholeOptimizerSUT.optimize().build();
    Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

    Assert.assertEquals(2, source.getCode().size());
  }

  @Test
  public void testTaySty() {
    List<String> list = new ArrayList<>();
    list.add(" TAY");
    list.add(" STY");
    list.add(" ...");
    source.resetCode(list);

    peepholeOptimizerSUT.optimize().build();
    Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

    Assert.assertEquals(2, source.getCode().size());
  }

  @Test
  public void testTaxStyStx() {
    List<String> list = new ArrayList<>();
    list.add(" TAX");
    list.add(" STY");
    list.add(" STX");
    list.add(" ...");
    source.resetCode(list);

    peepholeOptimizerSUT.optimize().build();
    Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

    Assert.assertEquals(3, source.getCode().size());
  }

  @Test
  public void testGetarraybCompare() {
    List<String> list = new ArrayList<>();
    list.add(" GETARRAYB");
    list.add(" STY @ERG");
    list.add(" LDY ");
    list.add(" CPY @ERG");
    list.add(" BNE ?FA");
    list.add(" ...");
    source.resetCode(list);

    peepholeOptimizerSUT.optimize().build();
    Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

    Assert.assertEquals(4, source.getCode().size());
  }

  @Test
  public void testWinc() {
    List<String> list = new ArrayList<>();
    list.add(" CLC");
    list.add(" LDA A");
    list.add(" ADC #<1");
    list.add(" STA A");
    list.add(" LDA A+1");
    list.add(" ADC #>1");
    list.add(" STA A+1");
    list.add(" ...");
    source.resetCode(list);

    peepholeOptimizerSUT.optimize().build();
    Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

    Assert.assertEquals(2, source.getCode().size());
  }

  @Test
  public void testInc() {
    List<String> list = new ArrayList<>();
    list.add(" CLC");
    list.add(" LDA A");
    list.add(" ADC #<1");
    list.add(" STA A");
    list.add(" ...");
    source.resetCode(list);

    peepholeOptimizerSUT.optimize().build();
    Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

    Assert.assertEquals(2, source.getCode().size());
  }

  @Test
  public void testLdxClcAdcStaTxa() {
    List<String> list = new ArrayList<>();
    list.add(" LDX");
    list.add(" CLC");
    list.add(" ADC #<1");
    list.add(" STA");
    list.add(" TXA");
    list.add(" ...");
    source.resetCode(list);

    peepholeOptimizerSUT.optimize().build();
    Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

    Assert.assertEquals(5, source.getCode().size());
  }

  @Test
  public void testLdxLdyStaTxa() {
    List<String> list = new ArrayList<>();
    list.add(" LDX");
    list.add(" LDY #<1");
    list.add(" STA");
    list.add(" TXA");
    list.add(" ...");
    source.resetCode(list);

    peepholeOptimizerSUT.optimize().build();
    Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

    Assert.assertEquals(4, source.getCode().size());
  }

  @Test
  public void testCompareLdyStyLdyCpy() {
    List<String> list = new ArrayList<>();
    list.add(" LDY");
    list.add(" STY @ERG");
    list.add(" LDY");
    list.add(" CPY @ERG");
    list.add(" ...");
    source.resetCode(list);

    peepholeOptimizerSUT.optimize().build();
    Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

    Assert.assertEquals(3, source.getCode().size());
  }

  @Test
  public void testTayLdxTya() {
    List<String> list = new ArrayList<>();
    list.add(" TAY");
    list.add(" LDX");
    list.add(" TYA");
    list.add(" ...");
    source.resetCode(list);

    peepholeOptimizerSUT.optimize().build();
    Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

    Assert.assertEquals(2, source.getCode().size());
  }

  @Test
  public void testCompareLdyLdxStyStxLdyLdxCpyTxaSbc() {
    List<String> list = new ArrayList<>();
    list.add(" LDY A");
    list.add(" LDX A+1");
    list.add(" STY @ERG");
    list.add(" STX @ERG+1");
    list.add(" LDY B");
    list.add(" LDX B+1");
    list.add(" CPY @ERG");
    list.add(" TXA");
    list.add(" SBC @ERG+1");
    list.add(" ...");
    source.resetCode(list);

    peepholeOptimizerSUT.optimize().build();
    Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

    Assert.assertEquals(5, source.getCode().size());
  }

  @Test
  public void testByteIndexAdd() {
    List<String> list = new ArrayList<>();
    list.add(" CLC");
    list.add(" LDA INDEX");
    list.add(" ADC #<1");
    list.add(" TAY");
    list.add(" LDA Y,Y");
    list.add(" ...");
    source.resetCode(list);

    peepholeOptimizerSUT.optimize().build();
    Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

    // Now, code should be this
    // LDY INDEX
    // INY
    // LDA Y,Y
    // ...
    Assert.assertEquals(4, source.getCode().size());
  }

  @Test
  public void testWordIndexAdd() {
    List<String> list = new ArrayList<>();
    list.add(" CLC");
    list.add(" LDA INDEX");
    list.add(" ADC #<1");
    list.add(" TAY");
    list.add(" LDA INDEX+1");
    list.add(" ADC #>1");
    list.add(" TAX");
    list.add(" TYA");
    list.add(" ...");
    source.resetCode(list);

    peepholeOptimizerSUT.optimize().build();
    Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

    Assert.assertEquals(9, source.getCode().size());
  }

  @Test
  public void testBneJmp() {
    List<String> list = new ArrayList<>();
    list.add(" BNE ?FA");
    list.add(" JMP ?THEN");
    list.add(" ...");
    source.resetCode(list);

    peepholeOptimizerSUT.setLevel(2).optimize().build();
    Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

    Assert.assertEquals(2, source.getCode().size());
  }

  @Test
  public void testBeqJmp() {
    List<String> list = new ArrayList<>();
    list.add(" BEQ ?FA");
    list.add(" JMP ?THEN");
    list.add(" ...");
    source.resetCode(list);

    peepholeOptimizerSUT.setLevel(2).optimize().build();
    Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

    Assert.assertEquals(2, source.getCode().size());
  }

  @Test
  public void testBccJmp() {
    List<String> list = new ArrayList<>();
    list.add(" BCC ?FA");
    list.add(" JMP ?THEN");
    list.add(" ...");
    source.resetCode(list);

    peepholeOptimizerSUT.setLevel(2).optimize().build();
    Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

    Assert.assertEquals(2, source.getCode().size());
  }

  @Test
  public void testBcsJmp() {
    List<String> list = new ArrayList<>();
    list.add(" BCS ?FA");
    list.add(" JMP ?THEN");
    list.add(" ...");
    source.resetCode(list);

    peepholeOptimizerSUT.setLevel(2).optimize().build();
    Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

    Assert.assertEquals(2, source.getCode().size());
  }

  @Test
  public void testBmiJmp() {
    List<String> list = new ArrayList<>();
    list.add(" BMI ?FA");
    list.add(" JMP ?THEN");
    list.add(" ...");
    source.resetCode(list);

    peepholeOptimizerSUT.setLevel(2).optimize().build();
    Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

    Assert.assertEquals(2, source.getCode().size());
  }

  @Test
  public void testBplJmp() {
    List<String> list = new ArrayList<>();
    list.add(" BPL ?FA");
    list.add(" JMP ?THEN");
    list.add(" ...");
    source.resetCode(list);

    peepholeOptimizerSUT.setLevel(2).optimize().build();
    Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

    Assert.assertEquals(2, source.getCode().size());
  }

}
