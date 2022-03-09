// cdw by 'The Atari Team' 2021
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
  public void testLdyTaxTya() {
    List<String> list = new ArrayList<>();
    list.add(" LDY");
    list.add(" TAX");
    list.add(" TYA");
    list.add(" ...");
    source.resetCode(list);

    peepholeOptimizerSUT.optimize().build();
    Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

    Assert.assertEquals(3, source.getCode().size());

    List<String> code = source.getCode();
    int n = -1;
    Assert.assertEquals(" TAX", code.get(++n));
    Assert.assertEquals(" LDA ; (6.1)", code.get(++n));
    Assert.assertEquals(" ...", code.get(++n));
  }

  @Test
  public void testLdxnTaxTxa() {
    List<String> list = new ArrayList<>();
    list.add(" LDX");
    list.add(" STY");
    list.add(" TXA");
    list.add(" ...");
    source.resetCode(list);

    peepholeOptimizerSUT.optimize().build();
    Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

    Assert.assertEquals(3, source.getCode().size());

    List<String> code = source.getCode();
    int n = -1;
    Assert.assertEquals(" STY", code.get(++n));
    Assert.assertEquals(" LDA ; (6.2)", code.get(++n));
    Assert.assertEquals(" ...", code.get(++n));
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
  public void testWinc() {
    List<String> list = new ArrayList<>();
    list.add(" CLC");
    list.add(" LDA A");
    list.add(" ADC #<1");
    list.add(" STA A ; (123)");
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
    Assert.assertEquals(" INC A ; (17)", source.getCode().get(0));
  }

  @Test
  public void testWDec() {
    List<String> list = new ArrayList<>();
    list.add(" SEC");
    list.add(" LDA A");
    list.add(" SBC #<1");
    list.add(" STA A");
    list.add(" LDA A+1");
    list.add(" SBC #>1");
    list.add(" STA A+1");
    list.add(" ...");
    source.resetCode(list);

    peepholeOptimizerSUT.optimize().build();
    Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

    Assert.assertEquals(2, source.getCode().size());
  }

  @Test
  public void testDec() {
    List<String> list = new ArrayList<>();
    list.add(" SEC");
    list.add(" LDA A");
    list.add(" SBC #<1");
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
    List<String> code = source.getCode();
    int n=-1;
    
    Assert.assertEquals(" LDY B", code.get(++n));
    Assert.assertEquals(" LDA B+1 ; (8)", code.get(++n));
    Assert.assertEquals(" CPY A ; (8)", code.get(++n));
    Assert.assertEquals(" SBC A+1 ; (8)", code.get(++n));
    Assert.assertEquals(" ...", code.get(++n));
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

  @Test
  public void teststy_ldy() {
    List<String> list = new ArrayList<>();
    list.add(" STY ?FOR");
    list.add(" LDY ?FOR");
    list.add(" ...");
    source.resetCode(list);

    peepholeOptimizerSUT.setLevel(2).optimize().build();
    Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

    Assert.assertEquals(2, source.getCode().size());
  }

  @Test
  public void testword_index_sub() {
    List<String> list = new ArrayList<>();
    list.add(" SEC");
    list.add(" LDA ");
    list.add(" SBC #<1");
    list.add(" TAY");
    list.add(" LDA ");
    list.add(" SBC #");
    list.add(" TAX");
    list.add(" TYA");
    list.add(" ...");
    source.resetCode(list);

    peepholeOptimizerSUT.setLevel(2).optimize().build();
    Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

    Assert.assertEquals(8, source.getCode().size());
  }

  @Test
  public void testByte_index_sub() {
    List<String> list = new ArrayList<>();
    list.add(" SEC");
    list.add(" LDA ");
    list.add(" SBC #<1");
    list.add(" TAY");
    list.add(" LDA ");
    list.add(" ...");
    list.add(" ...");
    source.resetCode(list);

    peepholeOptimizerSUT.setLevel(2).optimize().build();
    Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

    Assert.assertEquals(5, source.getCode().size());

  }

  @Test
  public void testJsrRts1() {
    List<String> list = new ArrayList<>();
    list.add(" JSR BLAH");
    list.add("?RETURN123");
    list.add(" RTS");
    list.add(" ...");
    source.resetCode(list);

    peepholeOptimizerSUT.setLevel(2).optimize().build();
    Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

    Assert.assertEquals(4, source.getCode().size());
  }

  @Test
  public void testJsrRts2() {
    List<String> list = new ArrayList<>();
    list.add(" JSR BLAH");
    list.add("?ENDIF");
    list.add("?RETURN123");
    list.add(" RTS");
    list.add(" ...");
    source.resetCode(list);

    peepholeOptimizerSUT.setLevel(2).optimize().build();
    Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

    Assert.assertEquals(5, source.getCode().size());
  }

  @Test
  public void testJsrRts3() {
    List<String> list = new ArrayList<>();
    list.add(" JSR BLAH");
    list.add("?ENDIF");
    list.add("?ENDIF");
    list.add("?RETURN123");
    list.add(" RTS");
    list.add(" ...");
    source.resetCode(list);

    peepholeOptimizerSUT.setLevel(2).optimize().build();
    Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

    Assert.assertEquals(6, source.getCode().size());
  }

  @Test
  public void testJsrRts4() {
    List<String> list = new ArrayList<>();
    list.add(" JSR BLAH");
    list.add("?ENDIF");
    list.add("?ENDIF");
    list.add("?ENDIF");
    list.add("?RETURN123");
    list.add(" RTS");
    list.add(" ...");
    source.resetCode(list);

    peepholeOptimizerSUT.setLevel(2).optimize().build();
    Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

    Assert.assertEquals(7, source.getCode().size());
  }

  @Test
  public void testJsrRts5() {
    List<String> list = new ArrayList<>();
    list.add(" JSR BLAH");
    list.add("?ENDIF");
    list.add("?ENDIF");
    list.add("?ENDIF");
    list.add("?ENDIF");
    list.add("?RETURN123");
    list.add(" RTS");
    list.add(" ...");
    source.resetCode(list);

    peepholeOptimizerSUT.setLevel(2).optimize().build();
    Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

    Assert.assertEquals(8, source.getCode().size());
  }

  @Test
  public void teststy_stx_erg_ldy_ldx_cpy_cpx_erg() {
    List<String> list = new ArrayList<>();
    list.add(" STY @ERG");
    list.add(" STX @ERG+1");
    list.add(" LDY #<low");
    list.add(" LDX #high");
    list.add(" CPY @ERG");
    list.add(" BNE ?FA123");
    list.add(" CPX @ERG+1");
    list.add(" BEQ ?THEN2");
    list.add("?FA123");
    list.add(" ...");
    source.resetCode(list);

    peepholeOptimizerSUT.setLevel(2).optimize().build();
    Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

    Assert.assertEquals(6, source.getCode().size());
  }

  @Test
  public void teststy_stx_erg_ldy_ldx_cpy_cpx_erg_b() {
    List<String> list = new ArrayList<>();
    list.add(" STY @ERG");
    list.add(" STX @ERG+1");
    list.add(" LDY #<0");
    list.add(" LDX #>0");
    list.add(" CPY @ERG");
    list.add(" BNE ?TR1");
    list.add(" CPX @ERG+1");
    list.add(" BEQ ?FA1");
    list.add("?TR1");
    list.add(" ...");
    source.resetCode(list);

    peepholeOptimizerSUT.setLevel(2).optimize().build();
    Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

    Assert.assertEquals(6, source.getCode().size());

    Assert.assertEquals(" TYA", source.getCode().get(0));
    Assert.assertEquals(" STX @ERG ; (34b)", source.getCode().get(1));
    Assert.assertEquals(" ORA @ERG", source.getCode().get(2));
    Assert.assertEquals(" BEQ ?FA1", source.getCode().get(3));
    Assert.assertEquals("?TR1", source.getCode().get(4));
    Assert.assertEquals(" ...", source.getCode().get(5));
    
  }


  @Test
  public void teststa_stx_erg_ldy_ldx_cpy_cpx_erg() {
    List<String> list = new ArrayList<>();
    list.add(" STA @ERG");
    list.add(" STX @ERG+1");
    list.add(" LDY #<low");
    list.add(" LDX #high");
    list.add(" CPY @ERG");
    list.add(" BNE ?FA123");
    list.add(" CPX @ERG+1");
    list.add(" BEQ ?THEN2");
    list.add("?FA123");
    list.add(" ...");
    source.resetCode(list);

    peepholeOptimizerSUT.setLevel(2).optimize().build();
    Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

    Assert.assertEquals(6, source.getCode().size());
  }

  @Test
  public void teststa_erg_ldy_cpy_erg_beq() {
    List<String> list = new ArrayList<>();
    list.add(" STA @ERG");
    list.add(" LDY #<low");
    list.add(" CPY @ERG");
    list.add(" BEQ ?THEN2");
    list.add("?FA123");
    list.add(" ...");
    source.resetCode(list);

    peepholeOptimizerSUT.setLevel(2).optimize().build();
    Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

    Assert.assertEquals(4, source.getCode().size());
  }

  @Test
  public void teststa_erg_ldy_cpy_erg_bne() {
    List<String> list = new ArrayList<>();
    list.add(" STA @ERG");
    list.add(" LDY #<low");
    list.add(" CPY @ERG");
    list.add(" BNE ?THEN2");
    list.add("?FA123");
    list.add(" ...");
    source.resetCode(list);

    peepholeOptimizerSUT.setLevel(2).optimize().build();
    Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

    Assert.assertEquals(4, source.getCode().size());
  }

  @Test
  public void teststa_erg_ldy_cpy_erg_bne_fa() {
    List<String> list = new ArrayList<>();
    list.add(" STA @ERG");
    list.add(" LDY #<low");
    list.add(" CPY @ERG");
    list.add(" BNE ?FA2");
    list.add(" ...");
    source.resetCode(list);

    peepholeOptimizerSUT.setLevel(2).optimize().build();
    Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

    Assert.assertEquals(3, source.getCode().size());
  }

  @Test
  public void teststa_erg_ldy_cpy_erg_beq_fa_bcc() {
    List<String> list = new ArrayList<>();
    list.add(" STA @ERG");
    list.add(" LDY #<low");
    list.add(" CPY @ERG");
    list.add(" BEQ ?FA2");
    list.add(" BCC ?FA2");
    list.add(" ...");
    source.resetCode(list);

    peepholeOptimizerSUT.setLevel(2).optimize().build();
    Assert.assertEquals(0, peepholeOptimizerSUT.getUsedOptimisations());
  }

  @Test
  public void teststa_erg_ldy_cpy_erg_beq_fa_no_bcc() {
    List<String> list = new ArrayList<>();
    list.add(" STA @ERG");
    list.add(" LDY #<low");
    list.add(" CPY @ERG");
    list.add(" BEQ ?FA2");
    list.add(" ...");
    source.resetCode(list);

    peepholeOptimizerSUT.setLevel(2).optimize().build();
    Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

    Assert.assertEquals(3, source.getCode().size());
  }

  @Test
  public void testldy_sty_putarray_lda_ldx_putarray_sta() {
    List<String> list = new ArrayList<>();
    list.add(" LDY #<low");
    list.add(" STY @PUTARRAY");
    list.add(" LDA schnubbel");
    list.add(" LDX @PUTARRAY");
    list.add(" STA array,X");
    list.add(" ...");
    source.resetCode(list);

    peepholeOptimizerSUT.setLevel(2).optimize().build();
    Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

    Assert.assertEquals(4, source.getCode().size());
  }

  @Test
  public void testTay_iny_tya() {
    List<String> list = new ArrayList<>();
    list.add(" TAY");
    list.add(" INY");
    list.add(" TYA");
    list.add(" ...");
    source.resetCode(list);

    peepholeOptimizerSUT.setLevel(2).optimize().build();
    Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

    Assert.assertEquals(3, source.getCode().size());
  }

  @Test
  public void testclc_lda_x_adc_1_sta_y() {
    List<String> list = new ArrayList<>();
    list.add(" CLC");
    list.add(" LDA J");
    list.add(" ADC #<1");
    list.add(" STA J1 ; (11)");
    list.add(" ...");
    source.resetCode(list);

    peepholeOptimizerSUT.setLevel(2).optimize().build();
    Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

    Assert.assertEquals(4, source.getCode().size());
    Assert.assertEquals(" LDY J ; (40)", source.getCode().get(0));
    Assert.assertEquals(" INY", source.getCode().get(1));
    Assert.assertEquals(" STY J1", source.getCode().get(2));
    Assert.assertEquals(" ...", source.getCode().get(3));
  }

  @Test
  public void testclc_lda_x_adc_13_sta_y() {
    List<String> list = new ArrayList<>();
    list.add(" CLC");
    list.add(" LDA J");
    list.add(" ADC #<13");
    list.add(" STA J1 ; (11)");
    list.add(" ...");
    source.resetCode(list);

    peepholeOptimizerSUT.setLevel(2).optimize().build();
    Assert.assertEquals(0, peepholeOptimizerSUT.getUsedOptimisations());

    Assert.assertEquals(5, source.getCode().size());
    Assert.assertEquals(" CLC", source.getCode().get(0));
    Assert.assertEquals(" LDA J", source.getCode().get(1));
    Assert.assertEquals(" ADC #<13", source.getCode().get(2));
    Assert.assertEquals(" STA J1 ; (11)", source.getCode().get(3));
    Assert.assertEquals(" ...", source.getCode().get(4));
  }


  @Test
  public void testParameterUebergabe() {
    List<String> list = new ArrayList<>();
    list.add(" LDY OLDXPOS");
    list.add(" LDX OLDXPOS+1");
    list.add(" TYA");
    list.add(" LDY #1");
    list.add(" STA (@HEAP_PTR),Y");
    list.add(" TXA");
    list.add(" INY");
    list.add(" STA (@HEAP_PTR),Y");

    list.add(" ...");
    source.resetCode(list);

    peepholeOptimizerSUT.setLevel(2).optimize().build();
    Assert.assertEquals(2, peepholeOptimizerSUT.getUsedOptimisations());

    List<String> code = source.getCode();

    int n = -1;
    Assert.assertEquals(" LDA OLDXPOS ; (6)", code.get(++n));
    Assert.assertEquals(" LDY #1", code.get(++n));
    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));
    Assert.assertEquals(" LDA OLDXPOS+1 ; (13)", code.get(++n));
    Assert.assertEquals(" INY", code.get(++n));
    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));
    Assert.assertEquals(" ...", code.get(++n));
  }

  @Test
  public void test_ldy_ldx_tya() {
    List<String> list = new ArrayList<>();
    list.add(" LDY A");
    list.add(" LDX A");
    list.add(" TYA");
    list.add(" ...");
    source.resetCode(list);

    peepholeOptimizerSUT.setLevel(2).optimize().build();
    Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

    Assert.assertEquals(3, source.getCode().size());
  }

  @Test
  public void test_ldx_ldx() {
    List<String> list = new ArrayList<>();
    list.add(" TYA");
    list.add(" LDX A");
    list.add(" LDX A");
    list.add(" ...");
    source.resetCode(list);

    peepholeOptimizerSUT.setLevel(2).optimize().build();
    Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

    Assert.assertEquals(3, source.getCode().size());
  }
  
  @Test
  public void test_bcc_ldx_ldx() {
    List<String> list = new ArrayList<>();
    list.add(" BCC *+4");
    list.add(" LDX #$ff");
    list.add(" LDX A");
    list.add(" ...");
    source.resetCode(list);

    peepholeOptimizerSUT.setLevel(2).optimize().build();
    Assert.assertEquals(0, peepholeOptimizerSUT.getUsedOptimisations());

    Assert.assertEquals(4, source.getCode().size());
  }

}
