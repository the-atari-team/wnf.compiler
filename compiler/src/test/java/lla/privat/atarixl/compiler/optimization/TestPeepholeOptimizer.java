// cdw by 'The Atari Team' 2022
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler.optimization;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import lla.privat.atarixl.compiler.Symbol;
import lla.privat.atarixl.compiler.SymbolEnum;
import lla.privat.atarixl.compiler.expression.Type;
import lla.privat.atarixl.compiler.source.Source;
import lla.privat.atarixl.compiler.statement.For;

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
    list.add(" ...");
    list.add(" LDX A");
    list.add(" STY B");
    list.add(" TXA");
    list.add(" ...");
    source.resetCode(list);

    peepholeOptimizerSUT.optimize().build();
    Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

    Assert.assertEquals(4, source.getCode().size());

    List<String> code = source.getCode();
    int n = -1;
    Assert.assertEquals(" ...", code.get(++n));
    Assert.assertEquals(" STY B", code.get(++n));
    Assert.assertEquals(" LDA A ; (6.2)", code.get(++n));
    Assert.assertEquals(" ...", code.get(++n));
//    Assert.assertEquals(" ...", code.get(++n));
  }


  @Test
  public void testLdxnTxaSta() {
    List<String> list = new ArrayList<>();
    list.add(" LDY X");
    list.add(" CPY #$80");
    list.add(" LDX #0");
    list.add(" BCC *+4");
    list.add(" LDX #$FF");
    list.add(" STY @HSP_PARAM+1");
    list.add(" TXA");
    list.add(" STA @HSP_PARAM+2");
    list.add(" ...");
    source.resetCode(list);

    peepholeOptimizerSUT.optimize().build();
    Assert.assertEquals(0, peepholeOptimizerSUT.getUsedOptimisations());

    Assert.assertEquals(9, source.getCode().size());

    List<String> code = source.getCode();
    int n = -1;
    Assert.assertEquals(" LDY X", code.get(++n));
    Assert.assertEquals(" CPY #$80", code.get(++n));
    Assert.assertEquals(" LDX #0", code.get(++n));
    Assert.assertEquals(" BCC *+4", code.get(++n));
    Assert.assertEquals(" LDX #$FF", code.get(++n));
    Assert.assertEquals(" STY @HSP_PARAM+1", code.get(++n));
    Assert.assertEquals(" TXA", code.get(++n));
    Assert.assertEquals(" STA @HSP_PARAM+2", code.get(++n));
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
    Assert.assertEquals(" INC A ; (17a)", source.getCode().get(0));
  }
  
  @Test
  public void testNoInc() {
    List<String> list = new ArrayList<>();
    list.add(" CLC");
    list.add(" LDA TIMERCOUNT");
    list.add(" ADC #<1");
    list.add(" STA TIMER");
    list.add(" ...");
    source.resetCode(list);

    peepholeOptimizerSUT.optimize().build();
    Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

    int n = -1;
    List<String> code = source.getCode();
    Assert.assertEquals(" LDY TIMERCOUNT ; (17b)", code.get(++n));
    Assert.assertEquals(" INY", code.get(++n));
    Assert.assertEquals(" STY TIMER", code.get(++n));
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
  public void testWDec2() {
    List<String> list = new ArrayList<>();
    list.add(" SEC");
    list.add(" TYA");
    list.add(" SBC #<1");
    list.add(" TAY");
    list.add(" TXA");
    list.add(" SBC #>1");
    list.add(" TAX");
    list.add(" ...");
    source.resetCode(list);

    peepholeOptimizerSUT.optimize().build();
    Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

    Assert.assertEquals(6, source.getCode().size());
  }

  @Test
  public void testDec() {
    List<String> list = new ArrayList<>();
    list.add(" SEC");
    list.add(" LDA A");
    list.add(" SBC #<1");
    list.add(" STA A");
    list.add(" ...");
    list.add(" ...");
    source.resetCode(list);

    peepholeOptimizerSUT.optimize().build();
    Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

    Assert.assertEquals(3, source.getCode().size());
  }

  @Test
  public void testNotDec() {
    List<String> list = new ArrayList<>();
    list.add(" SEC");
    list.add(" LDA TIMERCOUNT");
    list.add(" SBC #<1");
    list.add(" STA TIMER");
    list.add(" ...");
    list.add(" ...");
    source.resetCode(list);

    peepholeOptimizerSUT.optimize().build();
    Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

    Assert.assertEquals(5, source.getCode().size());
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
  public void testCompareLdyStyLdyCpy_equal() {
    List<String> list = new ArrayList<>();
    list.add(" LDY first");
    list.add(" STY @ERG");
    list.add(" LDY second");
    list.add(" CPY @ERG");
    list.add(" BNE ?FA1");
    list.add(" JMP ?THEN1");
    list.add("?FA1");
    source.resetCode(list);

    peepholeOptimizerSUT.optimize().build();
    Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

//    Assert.assertEquals(3, source.getCode().size());
    
    int n = -1;
    List<String> code = source.getCode();
    Assert.assertEquals(" LDY first ; (14b) a==b", code.get(++n));
    Assert.assertEquals(" CPY second", code.get(++n));
    Assert.assertEquals(" BEQ ?THEN1", code.get(++n));
  }

  @Test
  public void testCompareLdyStyLdyCpy_not_equal() {
    List<String> list = new ArrayList<>();
    list.add(" LDY first");
    list.add(" STY @ERG");
    list.add(" LDY second");
    list.add(" CPY @ERG");
    list.add(" BEQ ?FA1");
    list.add(" JMP ?THEN1");
    list.add("?FA1");
    source.resetCode(list);

    peepholeOptimizerSUT.optimize().build();
    Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

//    Assert.assertEquals(3, source.getCode().size());
    
    int n = -1;
    List<String> code = source.getCode();
    Assert.assertEquals(" LDY first ; (14c) a!=b", code.get(++n));
    Assert.assertEquals(" CPY second", code.get(++n));
    Assert.assertEquals(" BNE ?THEN1", code.get(++n));
  }

  @Test
  public void testCompareLdyStyLdyCpy_a_less_b() {
    List<String> list = new ArrayList<>();
    list.add(" LDY first");
    list.add(" STY @ERG");
    list.add(" LDY second");
    list.add(" CPY @ERG");
    list.add(" BEQ ?FA1");
    list.add(" BCC ?FA1");
    list.add(" JMP ?THEN1");
    list.add("?FA1");
    source.resetCode(list);

    peepholeOptimizerSUT.optimize().build();
    Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

//    Assert.assertEquals(3, source.getCode().size());
    
    int n = -1;
    List<String> code = source.getCode();
    Assert.assertEquals(" LDY first ; (14d) a<b", code.get(++n));
    Assert.assertEquals(" CPY second", code.get(++n));
    Assert.assertEquals(" BCC ?THEN1", code.get(++n));
  }

  @Test
  public void testCompareLdyStyLdyCpy_a_greater_equal_b() {
    List<String> list = new ArrayList<>();
    list.add(" LDY first");
    list.add(" STY @ERG");
    list.add(" LDY second");
    list.add(" CPY @ERG");
    list.add(" BEQ ?TR1");
    list.add(" BCS ?FA1");
    list.add("?TR1");
    list.add(" JMP ?THEN1");
    list.add("?FA1");
    source.resetCode(list);

    peepholeOptimizerSUT.optimize().build();
    Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

//    Assert.assertEquals(3, source.getCode().size());
    
    int n = -1;
    List<String> code = source.getCode();
    Assert.assertEquals(" LDY first ; (14e) a>=b", code.get(++n));
    Assert.assertEquals(" CPY second", code.get(++n));
    Assert.assertEquals(" BCS ?THEN1", code.get(++n));
  }

  @Test
  public void testCompareLdyStyLdyCpy_a_less_equal_b() {
    List<String> list = new ArrayList<>();
    list.add(" LDY first");
    list.add(" STY @ERG");
    list.add(" LDY second");
    list.add(" CPY @ERG");
    list.add(" BCC ?FA1");
    list.add(" JMP ?THEN1");
    list.add("?FA1");
    source.resetCode(list);

    peepholeOptimizerSUT.optimize().build();
    Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

//    Assert.assertEquals(3, source.getCode().size());
    
    int n = -1;
    List<String> code = source.getCode();
    Assert.assertEquals(" LDY second ; (14f) a<=b", code.get(++n));
    Assert.assertEquals(" CPY first", code.get(++n));
    Assert.assertEquals(" BCS ?THEN1", code.get(++n));
  }

  @Test
  public void testCompareLdyStyLdyCpy_a_greater_b() {
    List<String> list = new ArrayList<>();
    list.add(" LDY first");
    list.add(" STY @ERG");
    list.add(" LDY second");
    list.add(" CPY @ERG");
    list.add(" BCS ?FA1");
    list.add(" JMP ?THEN1");
    list.add("?FA1");
    source.resetCode(list);

    peepholeOptimizerSUT.optimize().build();
    Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

//    Assert.assertEquals(3, source.getCode().size());
    
    int n = -1;
    List<String> code = source.getCode();
    Assert.assertEquals(" LDY second ; (14g) a>b", code.get(++n));
    Assert.assertEquals(" CPY first", code.get(++n));
    Assert.assertEquals(" BCC ?THEN1", code.get(++n));
  }

  @Test
  public void testCompareLdyStyLdyCpy() {
    List<String> list = new ArrayList<>();
    list.add(" LDY first");
    list.add(" STY @ERG");
    list.add(" LDY second");
    list.add(" CPY @ERG");
    list.add(" ...");
    source.resetCode(list);

    peepholeOptimizerSUT.optimize().build();
    Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

    Assert.assertEquals(3, source.getCode().size());
    
    int n = -1;
    List<String> code = source.getCode();
    Assert.assertEquals(" LDY second", code.get(++n));
    Assert.assertEquals(" CPY first ; (14)", code.get(++n));
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
    Assert.assertEquals(" STY ?FOR", source.getCode().get(0));
    Assert.assertEquals(" ...", source.getCode().get(1));
  }

  @Test
  public void testword_index_sub() {
    List<String> list = new ArrayList<>();
    list.add(" SEC");
    list.add(" LDA ADR");
    list.add(" SBC #<1");
    list.add(" TAY");
    list.add(" LDA ADR+1");
    list.add(" SBC #");
    list.add(" TAX");
    list.add(" TYA");
    list.add(" ...");
    source.resetCode(list);

    peepholeOptimizerSUT.setLevel(2).optimize().build();
    Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

    Assert.assertEquals(8, source.getCode().size());
    Assert.assertEquals(" LDX ADR+1", source.getCode().get(0));
    Assert.assertEquals(" LDY ADR", source.getCode().get(1));
    Assert.assertEquals(" BNE ?WORD_INDEX_SUB0", source.getCode().get(2));
    Assert.assertEquals(" DEX", source.getCode().get(3));
    Assert.assertEquals("?WORD_INDEX_SUB0", source.getCode().get(4));
    Assert.assertEquals(" DEY ; (22)", source.getCode().get(5));
    Assert.assertEquals(" TYA", source.getCode().get(6));
    Assert.assertEquals(" ...", source.getCode().get(7));
  }

  @Test
  public void testByte_index_sub() {
    List<String> list = new ArrayList<>();
    list.add(" SEC");
    list.add(" LDA ADR");
    list.add(" SBC #<1");
    list.add(" TAY");
    list.add(" LDA ADR+1");
    list.add(" ...");
    list.add(" ...");
    source.resetCode(list);

    peepholeOptimizerSUT.setLevel(2).optimize().build();
    Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

    Assert.assertEquals(5, source.getCode().size());
    Assert.assertEquals(" LDY ADR", source.getCode().get(0));
    Assert.assertEquals(" DEY ; (21)", source.getCode().get(1));
    Assert.assertEquals(" LDA ADR+1", source.getCode().get(2));
    Assert.assertEquals(" ...", source.getCode().get(3));
    Assert.assertEquals(" ...", source.getCode().get(4));

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
    Assert.assertEquals(" JMP BLAH ; (39)", source.getCode().get(0));
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
    Assert.assertEquals(" JMP BLAH ; (39)", source.getCode().get(0));
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
    Assert.assertEquals(" JMP BLAH ; (39)", source.getCode().get(0));
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
    Assert.assertEquals(" JMP BLAH ; (39)", source.getCode().get(0));
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
    Assert.assertEquals(" JMP BLAH ; (39)", source.getCode().get(0));
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
    Assert.assertEquals(" CPY #<low ;  (34)", source.getCode().get(0));
    Assert.assertEquals(" BNE ?FA123", source.getCode().get(1));
    Assert.assertEquals(" CPX #high ; (34)", source.getCode().get(2));
    Assert.assertEquals(" BEQ ?THEN2", source.getCode().get(3));
    Assert.assertEquals("?FA123", source.getCode().get(4));
    Assert.assertEquals(" ...", source.getCode().get(5));
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
    Assert.assertEquals(" CMP #<low ; (35)", source.getCode().get(0));
    Assert.assertEquals(" BNE ?FA123", source.getCode().get(1));
    Assert.assertEquals(" CPX #high ; (35)", source.getCode().get(2));
    Assert.assertEquals(" BEQ ?THEN2", source.getCode().get(3));
    Assert.assertEquals("?FA123", source.getCode().get(4));
    Assert.assertEquals(" ...", source.getCode().get(5));
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
    Assert.assertEquals(" CMP #low ; (32)", source.getCode().get(0));
    Assert.assertEquals(" BEQ ?THEN2", source.getCode().get(1));
    Assert.assertEquals("?FA123", source.getCode().get(2));
    Assert.assertEquals(" ...", source.getCode().get(3));
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
    Assert.assertEquals(" CMP #low ; (33)", source.getCode().get(0));
    Assert.assertEquals(" BNE ?THEN2", source.getCode().get(1));
    Assert.assertEquals("?FA123", source.getCode().get(2));
    Assert.assertEquals(" ...", source.getCode().get(3));
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
    Assert.assertEquals(" CMP #low ; (36)", source.getCode().get(0));
    Assert.assertEquals(" BNE ?FA2", source.getCode().get(1));
    Assert.assertEquals(" ...", source.getCode().get(2));
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
    Assert.assertEquals(" CMP #low ; (37)", source.getCode().get(0));
    Assert.assertEquals(" BEQ ?FA2", source.getCode().get(1));
    Assert.assertEquals(" ...", source.getCode().get(2));
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
    Assert.assertEquals(" LDA schnubbel", source.getCode().get(0));
    Assert.assertEquals(" LDX #<low ; (38a)", source.getCode().get(1));
    Assert.assertEquals(" STA array,X", source.getCode().get(2));
    Assert.assertEquals(" ...", source.getCode().get(3));
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
    Assert.assertEquals(" LDY J ; (17b)", source.getCode().get(0));
    Assert.assertEquals(" INY", source.getCode().get(1));
    Assert.assertEquals(" STY J1", source.getCode().get(2));
    Assert.assertEquals(" ...", source.getCode().get(3));
  }

  @Test
  public void testclc_lda_x_adc_1_sta_y_not_byte() {
    List<String> list = new ArrayList<>();
    list.add(" CLC");
    list.add(" LDA #<63");
    list.add(" ADC #<1");
    list.add(" STA J1");
    list.add(" LDA #>63");
    list.add(" ADC #>1");
    list.add(" STA J1+1");
    list.add(" ...");
    source.resetCode(list);

    peepholeOptimizerSUT.setLevel(2).optimize().build();
    Assert.assertEquals(0, peepholeOptimizerSUT.getUsedOptimisations());

    List<String> code = source.getCode();
    int n=-1;
    
    Assert.assertEquals(" CLC", code.get(++n));
    Assert.assertEquals(" LDA #<63", code.get(++n));
    Assert.assertEquals(" ADC #<1", code.get(++n));
    Assert.assertEquals(" STA J1", code.get(++n));
    Assert.assertEquals(" LDA #>63", code.get(++n));
    Assert.assertEquals(" ADC #>1", code.get(++n));
    Assert.assertEquals(" STA J1+1", code.get(++n));
    Assert.assertEquals(" ...", code.get(++n));

    Assert.assertEquals(code.size(), n+1);

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

  // if a[n] < b then
//  LDY EM_INDEX
//  LDA EM_YPOS,Y
//  STA @ERG ; (11)
//  LDY #<160
//  CPY @ERG
//  BEQ ?FA32
//  BCS ?THEN23 ; (29)
// ?FA32

  @Test
  public void testCompare_a_array_less_b() {
    List<String> list = new ArrayList<>();
    list.add(" LDY INDEX");
    list.add(" LDA first,y");
    list.add(" STA @ERG");
    list.add(" LDY second");
    list.add(" CPY @ERG");
    list.add(" BEQ ?FA1");
    list.add(" BCS ?THEN1");
    list.add("?FA1");
    source.resetCode(list);

    peepholeOptimizerSUT.setLevel(2).optimize().build();
    Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

//    Assert.assertEquals(3, source.getCode().size());
    
    int n = -1;
    List<String> code = source.getCode();
    Assert.assertEquals(" LDY INDEX", code.get(++n));
    Assert.assertEquals(" LDA first,y ; (49) a[i]<b", code.get(++n));
    Assert.assertEquals(" CMP second", code.get(++n));
    Assert.assertEquals(" BCC ?THEN1", code.get(++n));
  }

  @Test
  public void testCompare_a_array_greater_b() {
    List<String> list = new ArrayList<>();
    list.add(" LDY INDEX");
    list.add(" LDA first,y");
    list.add(" STA @ERG");
    list.add(" LDY second");
    list.add(" CPY @ERG");
    list.add(" BCC ?THEN1");
    list.add("?FA1");
    source.resetCode(list);

    peepholeOptimizerSUT.setLevel(2).optimize().build();
    Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

//    Assert.assertEquals(3, source.getCode().size());
    
    int n = -1;
    List<String> code = source.getCode();
    Assert.assertEquals(" LDY INDEX", code.get(++n));
    Assert.assertEquals(" LDA second ; (49c) a[i]>b", code.get(++n));
    Assert.assertEquals(" CMP first,y", code.get(++n));
    Assert.assertEquals(" BCC ?THEN1", code.get(++n));
  }

//LDY EM_INDEX
//LDA EM_YPOS,Y
//STA @ERG ; (11)
//LDY #<160
//CPY @ERG
//BEQ ?TR51
//BCS ?FA51
//?TR51
//JMP ?THEN35
//?FA51
  @Test
  public void testCompare_a_array_greater_equal_b() {
    List<String> list = new ArrayList<>();
    list.add(" LDY INDEX");
    list.add(" LDA first,y");
    list.add(" STA @ERG");
    list.add(" LDY second");
    list.add(" CPY @ERG");
    list.add(" BEQ ?TR1");
    list.add(" BCS ?FA1");
    list.add("?TR1");
    list.add(" JMP ?THEN1");
    list.add("?FA1");
    source.resetCode(list);

    peepholeOptimizerSUT.setLevel(2).optimize().build();
    Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

//    Assert.assertEquals(3, source.getCode().size());
    
    int n = -1;
    List<String> code = source.getCode();
    Assert.assertEquals(" LDY INDEX", code.get(++n));
    Assert.assertEquals(" LDA first,y ; (49b) a[i]>=b", code.get(++n));
    Assert.assertEquals(" CMP second", code.get(++n));
    Assert.assertEquals(" BCS ?THEN1", code.get(++n));
  }

//LDY #<3
//LDA VARIABLE,Y
//STA @ERG ; (11)
//LDY #<4
//CPY @ERG
//BCS ?THEN4 ; (29)
//?FA4

  @Test
  public void testCompare_a_array_less_equal_b() {
    List<String> list = new ArrayList<>();
    list.add(" LDY INDEX");
    list.add(" LDA first,y");
    list.add(" STA @ERG");
    list.add(" LDY second");
    list.add(" CPY @ERG");
    list.add(" BCS ?THEN1");
    list.add("?FA1");
    source.resetCode(list);

    peepholeOptimizerSUT.setLevel(2).optimize().build();
    Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

//    Assert.assertEquals(3, source.getCode().size());
    
    int n = -1;
    List<String> code = source.getCode();
    Assert.assertEquals(" LDY INDEX", code.get(++n));
    Assert.assertEquals(" LDA second ; (49d) a[i]<=b", code.get(++n));
    Assert.assertEquals(" CMP first,y", code.get(++n));
    Assert.assertEquals(" BCS ?THEN1", code.get(++n));
  }

// Array Assignment x[i] := 1
  @Test
  public void testAssignmentByteArrayI_equal_value() {
    List<String> list = new ArrayList<>();
    list.add(" LDY INDEX");
    list.add(" STY @PUTARRAY");
    list.add(" LDA #>1");
    list.add(" LDX @PUTARRAY");
    list.add(" STA X,X");
    list.add(" ...");
    source.resetCode(list);

    peepholeOptimizerSUT.setLevel(2).optimize().build();
    Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

//    Assert.assertEquals(3, source.getCode().size());
    
    int n = -1;
    List<String> code = source.getCode();
    Assert.assertEquals(" LDA #>1", code.get(++n));
    Assert.assertEquals(" LDX INDEX ; (38a)", code.get(++n));
    Assert.assertEquals(" STA X,X", code.get(++n));
  }

//  LDY EM_INDEX
//  STY @PUTARRAY
//  LDY EM_INDEX
//  LDA EM_JUMP_ANIMATION,Y
//  CLC ; (31)
//  ADC #1
//  LDX @PUTARRAY
//  STA EM_JUMP_ANIMATION,X

//Array Assignment jump_animation[index] := jump_animation[index]+1
 @Test
 public void testAssignmentByteArrayPlusByteArray() {
   List<String> list = new ArrayList<>();
   list.add(";");
   list.add("; [298]  ypos[index] := ypos[index] + ystep");
   list.add(";");
   list.add(" LDY INDEX");
   list.add(" STY @PUTARRAY");
   list.add(" LDY INDEX");
   list.add(" LDA YPOS,Y");
   list.add(" CLC");
   list.add(" ADC YSTEP");
   list.add(" LDX @PUTARRAY");
   list.add(" STA YPOS,X");
   list.add(";");
   list.add("; [299]  jump_animation[index] := jump_animation[index] + 1");
   list.add(";");
   list.add(" LDY INDEX");
   list.add(" STY @PUTARRAY");
   list.add(" LDY INDEX");
   list.add(" LDA JUMP_ANIMATION,Y");
   list.add(" CLC ; (31)");
   list.add(" ADC #1");
   list.add(" LDX @PUTARRAY");
   list.add(" STA JUMP_ANIMATION,X");
   list.add("; ...");
   source.resetCode(list);

   peepholeOptimizerSUT.setLevel(2).optimize().build();
   Assert.assertEquals(3, peepholeOptimizerSUT.getUsedOptimisations());

//   Assert.assertEquals(3, source.getCode().size());
   
   int n = -1;
   List<String> code = source.getCode();
   Assert.assertEquals(";", code.get(++n));
   Assert.assertEquals("; [298]  ypos[index] := ypos[index] + ystep", code.get(++n));
   Assert.assertEquals(";", code.get(++n));

   Assert.assertEquals(" LDY INDEX", code.get(++n));
   Assert.assertEquals(" LDA YPOS,Y", code.get(++n));
   Assert.assertEquals(" CLC", code.get(++n));
   Assert.assertEquals(" ADC YSTEP", code.get(++n));
   Assert.assertEquals(" LDX INDEX ; (38d)", code.get(++n));
   Assert.assertEquals(" STA YPOS,X", code.get(++n));

   Assert.assertEquals(";", code.get(++n));
   Assert.assertEquals("; [299]  jump_animation[index] := jump_animation[index] + 1", code.get(++n));
   Assert.assertEquals(";", code.get(++n));
   
   Assert.assertEquals(" LDX INDEX", code.get(++n));
   Assert.assertEquals(" INC JUMP_ANIMATION,X ; (50)", code.get(++n));
   Assert.assertEquals("; ...", code.get(++n));
 }
 
//Array Assignment with increment x[i] := x[i] + 1

 @Test
 public void testAssignmentByteArrayWithIncrement() {
   List<String> list = new ArrayList<>();
   list.add(" LDY INDEX");
   list.add(" STY @PUTARRAY");
   list.add(" LDY INDEX");
   list.add(" LDA X,Y");
   list.add(" CLC");
   list.add(" ADC #1");
   list.add(" LDX @PUTARRAY");
   list.add(" STA X,X");
   list.add(" ...");
   source.resetCode(list);

   peepholeOptimizerSUT.setLevel(2).optimize().build();
   Assert.assertEquals(2, peepholeOptimizerSUT.getUsedOptimisations());

// Assert.assertEquals(3, source.getCode().size());

   int n = -1;
   List<String> code = source.getCode();
   Assert.assertEquals(" LDX INDEX", code.get(++n));
   Assert.assertEquals(" INC X,X ; (50)", code.get(++n));
 }

//Array Assignment with decrement x[i] := x[i] - 1

 @Test
 public void testAssignmentByteArrayWithDecrement() {
   List<String> list = new ArrayList<>();
   list.add(" LDY INDEX");
   list.add(" LDA X,Y");
   list.add(" SEC");
   list.add(" SBC #<1");
   list.add(" LDX INDEX");
   list.add(" STA X,X");
   list.add(" ...");
   source.resetCode(list);

   peepholeOptimizerSUT.setLevel(2).optimize().build();
   Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

//Assert.assertEquals(3, source.getCode().size());

   int n = -1;
   List<String> code = source.getCode();
   Assert.assertEquals(" LDX INDEX", code.get(++n));
   Assert.assertEquals(" DEC X,X ; (51)", code.get(++n));
 }

 @Test
 public void testAssignmentMem() {
   // Source source = new Source("@mem[710]:=$41").setVerboseLevel(2);

   List<String> list = new ArrayList<>();

   list.add("; (5)");
   list.add(" LDY #<710");
   list.add(" LDX #>710");
   list.add(" STY @PUTARRAY");
   list.add(" STX @PUTARRAY+1");

   list.add("; (5)");
   list.add(" LDY #<65");
   list.add(" TYA");
   list.add(" LDY #0");
   // ldy @putarray_byteindex
   list.add(" STA (@PUTARRAY),Y");
   list.add(" ...");
   source.resetCode(list);

   peepholeOptimizerSUT.setLevel(2).optimize().build();
   Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

//Assert.assertEquals(3, source.getCode().size());

   int n = -1;
   // List<String> code = source.getCode();

   // ldx #>710
   // sta $0+1
   // lda #<65
   // ldy #<710
   // sta ($00),y
   // 15 cycles 10 bytes ; setzt voraus, das $00 IMMER 0 ist

   Assert.assertEquals(" LDY #<710", source.getCode().get(++n));
   Assert.assertEquals(" LDX #>710", source.getCode().get(++n));
   Assert.assertEquals(" STY @PUTARRAY", source.getCode().get(++n));
   Assert.assertEquals(" STX @PUTARRAY+1", source.getCode().get(++n));

   Assert.assertEquals(" LDA #<65 ; (10)", source.getCode().get(++n));
   Assert.assertEquals(" LDY #0", source.getCode().get(++n));
   // ldy @putarray_byteindex
   Assert.assertEquals(" STA (@PUTARRAY),Y", source.getCode().get(++n));
   // 20 cycles 14 bytes
 }

 @Test
 public void testArrayIMinusArrayJ() {
   // Source source = new Source("n := xpos[i]-ypos[j]").setVerboseLevel(2);

   List<String> list = new ArrayList<>();

   list.add(" LDY I");
   list.add(" LDX YPOS_HIGH,Y ; (45)");
   list.add(" LDA YPOS_LOW,Y");
   list.add(" PHA");
   list.add(" TXA");
   list.add(" PHA");
   list.add(" LDY J");
   list.add(" LDX YPOS_HIGH,Y ; (45)");
   list.add(" LDA YPOS_LOW,Y");
   list.add(" STA @OP ; (11)");
   list.add(" STX @OP+1");
   list.add(" PLA");
   list.add(" TAX");
   list.add(" PLA");
   list.add(" SEC");
   list.add(" SBC @OP");
   list.add(" TAY");
   list.add(" TXA");
   list.add(" SBC @OP+1");
   list.add(" TAX");

   list.add(" ...");
   source.resetCode(list);

   peepholeOptimizerSUT.setLevel(3).optimize().build();
   Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

//Assert.assertEquals(3, source.getCode().size());

   int n = -1;
   List<String> code = source.getCode();

// LDY I
// LDX J
// SEC
// LDA YPOS_LOW,X
// SBC YPOS_LOW,Y
// STA @OP
// LDA YPOS_HIGH,X
// SBC YPOS_HIGH,Y
// TAX
// LDY @OP

   Assert.assertEquals(" LDY I ; (60)", code.get(++n));
   Assert.assertEquals(" LDX J", code.get(++n));
   Assert.assertEquals(" SEC", code.get(++n));
   Assert.assertEquals(" LDA YPOS_LOW,X", code.get(++n));
   Assert.assertEquals(" SBC YPOS_LOW,Y", code.get(++n));
   Assert.assertEquals(" STA @OP", code.get(++n));
   Assert.assertEquals(" LDA YPOS_HIGH,X ; (45)", code.get(++n));
   Assert.assertEquals(" SBC YPOS_HIGH,Y ; (45)", code.get(++n));
   Assert.assertEquals(" LDY @OP", code.get(++n));
   Assert.assertEquals(" TAX", code.get(++n));
   // 32 cycles
 }

 @Test
 public void testArrayIPlusArrayJ() {
   // Source source = new Source("n := xpos[i]+ypos[i]").setVerboseLevel(2);

   List<String> list = new ArrayList<>();

   list.add(" LDY I");
   list.add(" LDX YPOS_HIGH,Y ; (45)");
   list.add(" LDA YPOS_LOW,Y");
   list.add(" PHA");
   list.add(" TXA");
   list.add(" PHA");
   list.add(" LDY J");
   list.add(" LDX YPOS_HIGH,Y ; (45)");
   list.add(" LDA YPOS_LOW,Y");
   list.add(" STA @OP ; (11)");
   list.add(" STX @OP+1");
   list.add(" PLA");
   list.add(" TAX");
   list.add(" PLA");
   list.add(" CLC");
   list.add(" ADC @OP");
   list.add(" TAY");
   list.add(" TXA");
   list.add(" ADC @OP+1");
   list.add(" TAX");

   list.add(" ...");
   source.resetCode(list);

   peepholeOptimizerSUT.setLevel(3).optimize().build();
   Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

//Assert.assertEquals(3, source.getCode().size());

   int n = -1;
   List<String> code = source.getCode();

// LDY I
// LDX J
// SEC
// LDA YPOS_LOW,X
// SBC YPOS_LOW,Y
// STA @OP
// LDA YPOS_HIGH,X
// SBC YPOS_HIGH,Y
// TAX
// LDY @OP

   Assert.assertEquals(" LDY I ; (61)", code.get(++n));
   Assert.assertEquals(" LDX J", code.get(++n));
   Assert.assertEquals(" CLC", code.get(++n));
   Assert.assertEquals(" LDA YPOS_LOW,X", code.get(++n));
   Assert.assertEquals(" ADC YPOS_LOW,Y", code.get(++n));
   Assert.assertEquals(" STA @OP", code.get(++n));
   Assert.assertEquals(" LDA YPOS_HIGH,X ; (45)", code.get(++n));
   Assert.assertEquals(" ADC YPOS_HIGH,Y ; (45)", code.get(++n));
   Assert.assertEquals(" LDY @OP", code.get(++n));
   Assert.assertEquals(" TAX", code.get(++n));
   // 32 cycles
 }

 @Test
 public void testLdyOp_tax_txa() {
   List<String> list = new ArrayList<>();

   list.add(" LDY @OP");
   list.add(" TAX");
   list.add(" TXA");

   list.add(" ...");
   source.resetCode(list);

   peepholeOptimizerSUT.setLevel(3).optimize().build();
   Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

//Assert.assertEquals(3, source.getCode().size());

   int n = -1;
   List<String> code = source.getCode();

// LDY @OP

   Assert.assertEquals(" LDY @OP ; (62)", code.get(++n));
 }

 @Test
 public void testArrayIAssignArrayI() {
   List<String> list = new ArrayList<>();

   list.add(" LDY DB_I");
   list.add(" LDX DB_MULT40_HIGH,Y ; (45)");
   list.add(" LDA DB_MULT40_LOW,Y");
   list.add(" TAY");
   list.add(" TXA");
   list.add(" LDX DB_I ; (38f)");
   list.add(" STA DB_SCREEN1_HIGH,X");
   list.add(" TYA");
   list.add(" STA DB_SCREEN1_LOW,X");

   list.add(" ...");
   source.resetCode(list);

   peepholeOptimizerSUT.setLevel(3).optimize().build();
   Assert.assertEquals(1, peepholeOptimizerSUT.getUsedOptimisations());

//Assert.assertEquals(3, source.getCode().size());

   int n = -1;
   List<String> code = source.getCode();

   Assert.assertEquals(" LDY DB_I ; (63)", code.get(++n));
   Assert.assertEquals(" LDA DB_MULT40_LOW,Y", code.get(++n));
   Assert.assertEquals(" STA DB_SCREEN1_LOW,Y", code.get(++n));
   Assert.assertEquals(" LDA DB_MULT40_HIGH,Y ; (45)", code.get(++n));
   Assert.assertEquals(" STA DB_SCREEN1_HIGH,Y", code.get(++n));
 }

 /*
 TAY
 SEC
 TYA
 SBC #<1
 TAY
 TYA
 ; sum 12 cycles
 
 => good
 1.:
 TAY
 DEY
 TYA
 ; sum 6 cycles
 
 => better!
 SEC
 SBC #<1 
 ; sum 4 cycles
  */

 @Test
 public void testSub1FromAccuByteValue() {
   List<String> list = new ArrayList<>();

   list.add(";  LDY EM_INDEX ; (100)");
   list.add(" LDA EM_ARROW_IN_MOVE,Y");
   list.add(" TAY");
   list.add(" SEC");
   list.add(" TYA");
   list.add(" SBC #<1");
   list.add(" TAY");
   list.add(" TYA");
   list.add(" LDX @PUTARRAY");
   list.add(" STA EM_ARROW_IN_MOVE,X");
   list.add(" ...");
   source.resetCode(list);

   peepholeOptimizerSUT.setLevel(2).optimize().build();
   Assert.assertEquals(2, peepholeOptimizerSUT.getUsedOptimisations());

//Assert.assertEquals(3, source.getCode().size());
   List<String> status = peepholeOptimizerSUT.getStatus();
   Assert.assertEquals(1, status.size());
   Assert.assertEquals("TAY_TYA", status.get(0));
   
   int n = -1;
   List<String> code = source.getCode();

   Assert.assertEquals(";  LDY EM_INDEX ; (100)", code.get(++n));
   Assert.assertEquals(" LDA EM_ARROW_IN_MOVE,Y", code.get(++n));

   Assert.assertEquals(" SEC", code.get(++n));
   Assert.assertEquals(" SBC #<1", code.get(++n));

   Assert.assertEquals(" LDX @PUTARRAY", code.get(++n));
   Assert.assertEquals(" STA EM_ARROW_IN_MOVE,X", code.get(++n));
}

 
 /* fix value replacement?
 SEC
 LDA #<16
 SBC #<1
 TAY
 STY ?FOR1
 
 =>
 LDY #15
 STY ?FOR1
*/
 
 
  /*
   * for i:=0 to v_byte do begin end
   */ 
 
 @Test
 public void testForWORDToByteDo() {
   Source source = new Source("for i:=0 to v_byte-1 do begin end").setVerboseLevel(2);
   source.addVariable("I", Type.WORD);
   source.addVariable("V_WORD", Type.WORD);
   source.addVariable("V_BYTE", Type.BYTE);
   Symbol symbol = source.nextElement();

   Symbol nextSymbol = new For(source).statement(symbol).build();

   Assert.assertEquals("", nextSymbol.get());
   Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

   PeepholeOptimizer peepholeOptimizerSUT = new PeepholeOptimizer(source, 1);

   peepholeOptimizerSUT.setLevel(3).optimize().build();
   Assert.assertEquals(2, peepholeOptimizerSUT.getUsedOptimisations());

//Assert.assertEquals(3, source.getCode().size());
   List<String> status = peepholeOptimizerSUT.getStatus();
   Assert.assertEquals(2, status.size());

   
   List<String> code = source.getCode();

   int n=-1;
   // Assert.assertEquals("; (5)", code.get(++n));
   Assert.assertEquals(" LDY #<0", code.get(++n));
   Assert.assertEquals(" LDX #>0", code.get(++n));
   Assert.assertEquals(" STY I", code.get(++n));
   Assert.assertEquals(" STX I+1", code.get(++n));

   // Assert.assertEquals("; (6)", code.get(++n));
   Assert.assertEquals(" SEC", code.get(++n));       // das lässt sich so nicht weiter optimieren
   Assert.assertEquals(" LDA V_BYTE", code.get(++n));
   Assert.assertEquals(" SBC #<1", code.get(++n));
   Assert.assertEquals(" STA ?FOR1 ; (5)", code.get(++n));
   Assert.assertEquals(" LDA #0", code.get(++n));
   Assert.assertEquals(" SBC #>1", code.get(++n));
   Assert.assertEquals(" STA ?FOR1+1 ; (4)", code.get(++n));

   Assert.assertEquals("?FORLOOP1", code.get(++n));

   Assert.assertEquals(" LDA ?FOR1", code.get(++n)); // check ob nicht schon am Ende
   Assert.assertEquals(" CMP I", code.get(++n));
   Assert.assertEquals(" LDA ?FOR1+1", code.get(++n));
   Assert.assertEquals(" SBC I+1", code.get(++n));
   Assert.assertEquals(" BVC *+4", code.get(++n));
   Assert.assertEquals(" EOR #$80", code.get(++n));
   Assert.assertEquals(" BPL ?GO1", code.get(++n));
   Assert.assertEquals(" JMP ?EXIT1", code.get(++n));
   Assert.assertEquals("?GO1", code.get(++n));
   // statement in for loop

   Assert.assertEquals(" INC I", code.get(++n));
   Assert.assertEquals(" BNE ?LOOP1", code.get(++n));
   Assert.assertEquals(" INC I+1", code.get(++n));
   Assert.assertEquals("?LOOP1", code.get(++n));
   Assert.assertEquals(" JMP ?FORLOOP1", code.get(++n));
   Assert.assertEquals("?EXIT1", code.get(++n));
 }
 
}
