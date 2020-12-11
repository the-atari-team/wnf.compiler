// cdw by 'The Atari Team' 2020
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler.optimization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lla.privat.atarixl.compiler.source.Source;

/**
 * Der Peephole Optimizer betrachtet nur kleine Code-Fragmente und versucht
 * diese durch "schnellere" zu ersetzen oder "unnoetige" zu entfernen. Der
 * WNF-Compiler erzeugt "schrecklichen" Code, der bewusst sehr einfach gehalten
 * ist. Der Original-WNF-Compiler ist in TurboBasic entstanden und mangels Platz
 * (34kb) gibt es nur eine sehr rudimentäre Algol ähnliche Sprache. In
 * 6502-Assembler ist es meist lade Y,X, mache damit etwas, Speichere Y,X wieder
 * ab. Da der 6502-Assembler aber mit Y,X Registern nicht rechnen kann, muss der
 * Wert immer erst in den Akku verschoben werden. Am Ende wird der Akku wieder
 * ins Register zurückgeschoben. Der Optimizer versucht dieses Verschieben von
 * Y,X Register zum Akku und zurück zu entfernen. Dabei kann auch das
 * Laden/Speichern der Register an anderer Code-Stelle passieren, was im
 * Compiler durch dessen Struktur so nicht so einfach möglich ist.
 *
 * Desweiteren wird x:=x+1 zu "winc x" umgeschrieben und die langen teuren
 * Vergleiche werden umgebaut zu etwas schnellerem.
 *
 * Aktuell diente nur der test-sieve.wnf als Optimierungstest, aber das brachte
 * beachtliche 21% schnelleren Code, von 1007 1/50s (~20,14s) auf 795 1/50s
 * (~15,86s) Damit ist der Code im Vergleich zu Action! nicht mehr 26% langsamer
 * sondern nur noch 6%.
 *
 * Dabei darf nicht außer acht gelassen werden, das eigentlich nur die innere
 * Schleife ab "Label ?GO3" einer stärkeren Betrachtung unterlag und dieser
 * PeepholeOptimizer darauf abgestimmt ist.
 *
 * Die Optimierung ist noch nicht abgeschlossen. Wird aber aktuell nicht weiter
 * verfolgt. War halt auch nur mal ein Test.
 *
 * @author lars
 *
 */
public class PeepholeOptimizer {

  private static final Logger LOGGER = LoggerFactory.getLogger(PeepholeOptimizer.class);

  private final Source source;
  private List<String> codeList;
  private int optimisationLevel;

  private int count = 0;

  private enum PeepholeType {
    // @formatter:off
    TAY_TYA,
    TAX_STX,
    TAY_STY,
    GETARRAYB,
    LDY_TYA,
    COMPARE_WORD,
    WINC,
    WDEC,
    INC,
    DEC,
    LDX_TXA,
    COMPARE_BYTE,
    STY_LDY,
    WORD_INDEX_ADD,
    WORD_INDEX_SUB,
    BYTE_INDEX_ADD,
    BYTE_INDEX_SUB,

    BNE_JMP_THEN,   // (25)
    BEQ_JMP_THEN,   // (26)
    BPL_JMP_THEN,   // (27)
    BMI_JMP_THEN,   // (28)
    BCC_JMP_THEN,   // (29)
    BCS_JMP_THEN;   // (30)

    // @formatter:on
  }

  private Map<PeepholeType, Integer> status;

  public PeepholeOptimizer(Source source, int optimisationLevel) {
    this.source = source;
    this.optimisationLevel = optimisationLevel;

    status = new HashMap<>();
    for (PeepholeType type : PeepholeType.values()) {
      status.put(type, 0);
    }
  }

  public void showStatus() {
    if (optimisationLevel > 0) {
      LOGGER.info("Peephole Status Usage:");
      for (PeepholeType type : PeepholeType.values()) {
        Integer used = status.get(type);
        LOGGER.info("{} = {}",used, type.name());
      }
      LOGGER.info("{} number of all used optimizations", count);
    }
  }

  public PeepholeOptimizer setLevel(int level) {
    this.optimisationLevel = level;
    return this;
  }

  public PeepholeOptimizer optimize() {
    if (optimisationLevel > 0) {

      // create a copy of current assembler-source
      this.codeList = new ArrayList<>();
      for (int i = 0; i < source.getCode().size(); i++) {
        codeList.add(source.getCode().get(i));
      }

      tay_clc_or_sec_tya(); // (1) (2) (7)
      removeComments();

      getarrayb_compares(); // (3)
      removeComments();

      tax_sty_stx(); // (4)
      tay_sty_sta(); // (5)
      removeComments();

      ldy_tya(); // (6) (10) (11)
      removeComments();

      compare_word_ldy_ldx_sty_stx_ldy_ldx_cpy_txa_sbc(); // (8)
      removeComments();

      winc(); // (9)

      ldx_clc_adc_sta_txa(); // (12)
      removeComments();

      ldx_ldy_sta_txa();
      compare_byte_ldy_sty_ldy_cpy(); // (8)
      removeComments();

      tay_tya(); // (15) (16)
      removeComments();

      inc(); // (17)
      removeComments();
      wdec(); // (23)
      removeComments();
      dec(); // (24)

      sty_ldy(); // (18)
      word_index_add(); // (19)
      word_index_sub(); // (22)
      byte_index_add(); // (20)
      byte_index_sub(); // (21)
      removeComments();

      if (optimisationLevel > 1) {

        bne_fa_jmp_then();
        beq_fa_jmp_then();
        bcc_fa_jmp_then();
        bcs_fa_jmp_then();
        bpl_fa_jmp_then();
        bmi_fa_jmp_then();
      }
      removeComments();

      LOGGER.info("Peephole Optimizer has {} optimizations used", count);
    }

    return this;
  }

  public int getUsedOptimisations() {
    return count;
  }

  private void removeComments() {
    List<String> newList = new ArrayList<>();
    for (int i = 0; i < codeList.size(); i++) {
      String line = codeList.get(i);
      if (!line.startsWith(";opt")) {
        newList.add(line);
      }
    }
    codeList = newList;
  }

  private void tay_clc_or_sec_tya() {
    for (int i = 0; i < codeList.size() - 2; i++) {
      tay_clc_tya(i, PeepholeType.TAY_TYA);
    }
    for (int i = 0; i < codeList.size() - 2; i++) {
      tay_sec_tya(i, PeepholeType.TAY_TYA);
    }
    for (int i = 0; i < codeList.size() - 3; i++) {
      tay_x_clc_tya(i, PeepholeType.TAY_TYA);
    }
  }

  private void incrementStatus(PeepholeType type) {
    ++count;

    final Integer value = status.get(type);
    status.put(type, value + 1);
  }

  private void tay_clc_tya(int index, PeepholeType type) {
    // @formatter:off
    if (codeList.get(index).startsWith(" TAY") &&
        codeList.get(index + 1).startsWith(" CLC") &&
        codeList.get(index + 2).startsWith(" TYA")) {
      LOGGER.debug("Peephole Optimization possible at Line: {}", index);
      codeList.set(index, ";opt TAY ; (1)");
      codeList.set(index+2, ";opt TYA ; (1)");
      incrementStatus(type);
    }
    // @formatter:on
  }

  private void tay_x_clc_tya(int index, PeepholeType type) {
    // @formatter:off
    if (codeList.get(index).startsWith(" TAY") &&
        codeList.get(index + 2).startsWith(" CLC") &&
        codeList.get(index + 3).startsWith(" TYA")) {
      LOGGER.debug("Peephole Optimization possible at Line: {}", index);
      codeList.set(index, ";opt TAY ; (7)");
      codeList.set(index+3, ";opt TYA ; (7)");
      incrementStatus(type);
    }
    // @formatter:on
  }

  private void tay_sec_tya(int index, PeepholeType type) {
    // @formatter:off
    if (codeList.get(index).startsWith(" TAY") &&
        codeList.get(index + 1).startsWith(" SEC") &&
        codeList.get(index + 2).startsWith(" TYA")) {
      LOGGER.debug("Peephole Optimization possible at Line: {}", index);
      codeList.set(index, ";opt TAY ; (2)");
      codeList.set(index+2, ";opt TYA ; (2)");
      incrementStatus(type);
    }
    // @formatter:on
  }

  private void tay_sty_sta() {
    for (int i = 0; i < codeList.size() - 5; i++) {
      tay_x_x_sty_sta(i, PeepholeType.TAY_STY);
    }
  }

  private void tay_x_x_sty_sta(int index, PeepholeType type) {
    // @formatter:off
    if (codeList.get(index).startsWith(" TAY") &&
        codeList.get(index + 4).startsWith(" STY") &&
        codeList.get(index + 5).startsWith(" STA")) {
      LOGGER.debug("Peephole Optimization possible at Line: {}", index);
      String newStore = codeList.get(index+4).replace("STY", "STA");
      codeList.set(index, newStore + " ; (5) optimized, was TAY");

      String oldStore = codeList.get(index+4).replace(" STY", ";opt STY");
      codeList.set(index+4, oldStore + " ; (5)");
      incrementStatus(type);
    }
    // @formatter:on
  }

  private void ldy_tya() {
    for (int i = 0; i < codeList.size() - 2; i++) {
      ldy_x_tya(i, PeepholeType.LDY_TYA);
    }
    for (int i = 0; i < codeList.size() - 2; i++) {
      ldy_tya(i, PeepholeType.LDY_TYA);
    }

    for (int i = 0; i < codeList.size() - 2; i++) {
      tay_sty(i, PeepholeType.LDY_TYA);
    }
  }

  private void ldy_x_tya(int index, PeepholeType type) {
    // @formatter:off
    if (codeList.get(index).startsWith(" LDY") &&
        codeList.get(index + 2).startsWith(" TYA")) {
      LOGGER.debug("Peephole Optimization possible at Line: {}", index);
      String newLoad = codeList.get(index).replace("LDY", "LDA");
      codeList.set(index, newLoad + " ; (6) optimized");

      codeList.set(index + 2, ";opt TYA ; (6)");
      incrementStatus(type);
    }
    // @formatter:on
  }

  private void ldy_tya(int index, PeepholeType type) {
    // @formatter:off
    if (codeList.get(index).startsWith(" LDY") &&
        codeList.get(index + 1).startsWith(" TYA")) {
      LOGGER.debug("Peephole Optimization possible at Line: {}", index);
      String newLoad = codeList.get(index).replace("LDY", "LDA");
      codeList.set(index, newLoad + " ; (10) optimized");

      codeList.set(index + 1, ";opt TYA ; (10)");
      incrementStatus(type);
    }
    // @formatter:on
  }

  private void tay_sty(int index, PeepholeType type) {
    // @formatter:off
    if (codeList.get(index).startsWith(" TAY") &&
        codeList.get(index + 1).startsWith(" STY")) {
      LOGGER.debug("Peephole Optimization possible at Line: {}", index);
      String newLoad = codeList.get(index+1).replace("STY", "STA");
      codeList.set(index+1, newLoad + " ; (11) optimized");

      codeList.set(index, ";opt TAY ; (11)");
      incrementStatus(type);
    }
    // @formatter:on
  }

  private void tax_sty_stx() {
    for (int i = 0; i < codeList.size() - 2; i++) {
      tax_sty_stx(i, PeepholeType.TAX_STX);
    }
  }

  private void tax_sty_stx(int index, PeepholeType type) {
    // @formatter:off
    if (codeList.get(index).startsWith(" TAX") &&
        codeList.get(index + 1).startsWith(" STY") &&
        codeList.get(index + 2).startsWith(" STX")) {
      LOGGER.debug("Peephole Optimization possible at Line: {}", index);
      codeList.set(index, ";opt TAX ; (4) optimized");
      String newStore = codeList.get(index+2).replace("STX", "STA");
      codeList.set(index+2, newStore + " ; (4)");
      incrementStatus(type);
    }
    // @formatter:on
  }

  private void getarrayb_compares() {
    for (int i = 0; i < codeList.size() - 4; i++) {
      getarrayb_compare(i, PeepholeType.GETARRAYB);
    }
  }

  // @formatter:off
  private void getarrayb_compare(int index, PeepholeType type) {
    if (codeList.get(index).startsWith(" GETARRAYB")) {

      if (codeList.get(index + 1).startsWith(" STY @ERG") &&
          codeList.get(index + 2).startsWith(" LDY ") &&
          codeList.get(index + 3).startsWith(" CPY @ERG") &&
          codeList.get(index + 4).startsWith(" BNE ?FA")) {

        LOGGER.debug("Peephole Optimization possible at Line: {}", index);
        codeList.set(index+1, ";opt STY @ERG ; (3) optimized");
        String newCompare = codeList.get(index+2).replace("LDY", "CPY");
        codeList.set(index+2, newCompare + " ; (3)");
        codeList.set(index+3, ";opt CPY @ERG ; (3)");
        incrementStatus(type);
      }
    }
  }
  // @formatter:on

  private void compare_word_ldy_ldx_sty_stx_ldy_ldx_cpy_txa_sbc() {
    for (int i = 0; i < codeList.size() - 9; i++) {
      compare_word_ldy_ldx_sty_stx_ldy_ldx_cpy_txa_sbc(i, PeepholeType.COMPARE_WORD);
    }
  }

  // @formatter:off
  private void compare_word_ldy_ldx_sty_stx_ldy_ldx_cpy_txa_sbc(int index, PeepholeType type) {

    if (codeList.get(index).startsWith(" LDY") &&
        codeList.get(index + 1).startsWith(" LDX") &&
        codeList.get(index + 2).startsWith(" STY @ERG") &&
        codeList.get(index + 3).startsWith(" STX @ERG+1") &&
        codeList.get(index + 4).startsWith(" LDY") &&
        codeList.get(index + 5).startsWith(" LDX") &&
        codeList.get(index + 6).startsWith(" CPY @ERG") &&
        codeList.get(index + 7).startsWith(" TXA") &&
        codeList.get(index + 8).startsWith(" SBC @ERG+1")
        ) {

      LOGGER.debug("Peephole Optimization possible at Line: {}", index);

      String newLoad = codeList.get(index).replace(" LDY", ";opt LDY");
      String newCompare = codeList.get(index).replace("LDY", "CPY");
      codeList.set(index, newLoad + " ; (8)");
      codeList.set(index+6, newCompare + " ; (8)");

      String newLoad2 = codeList.get(index+1).replace(" LDX", ";opt LDX");
      String newCompare2 = codeList.get(index+1).replace("LDX", "SBC");
      codeList.set(index+1, newLoad2 + " ; (8)");
      codeList.set(index+8, newCompare2 + " ; (8)");

      String newStore = codeList.get(index+2).replace(" STY", ";opt STY");
      codeList.set(index+2, newStore + " ; (8)");

      String newStore2 = codeList.get(index+3).replace(" STX", ";opt STX");
      codeList.set(index+3, newStore2 + " ; (8)");

      String newLoad3 = codeList.get(index+5).replace("LDX", "LDA");
      codeList.set(index+5, newLoad3 + " ; (8)");

      codeList.set(index+7, ";opt TXA ; (8)");
      incrementStatus(type);
    }
  }
  // @formatter:on

  private void winc() {
    for (int i = 0; i < codeList.size() - 7; i++) {
      winc(i, PeepholeType.WINC);
    }
  }

  // @formatter:off
  //  CLC
  //  LDA COUNT
  //  ADC #<1
  //  STA COUNT ; (5) optimized, was TAY
  //  LDA COUNT+1
  //  ADC #>1
  //  STA COUNT+1 ; (4)
  private void winc(int index, PeepholeType type) {

    if (codeList.get(index).startsWith(" CLC") &&
        codeList.get(index + 1).startsWith(" LDA") &&
        codeList.get(index + 2).startsWith(" ADC #<1") &&
        codeList.get(index + 3).startsWith(" STA") &&
        codeList.get(index + 4).startsWith(" LDA") &&
        codeList.get(index + 5).startsWith(" ADC #>1") &&
        codeList.get(index + 6).startsWith(" STA")
        ) {
      String value = codeList.get(index+2).replace(" ADC #<", "");
      String loadVariable = codeList.get(index+3).replace(" STA ", "");
      if (loadVariable.contains(" ")) {
        loadVariable = loadVariable.substring(0, loadVariable.indexOf(" "));
      }
      if (value.equals("1") &&
          codeList.get(index + 1).startsWith(" LDA " + loadVariable) &&
          codeList.get(index + 3).startsWith(" STA " + loadVariable) &&
          codeList.get(index + 4).startsWith(" LDA " + loadVariable + "+1") &&
          codeList.get(index + 6).startsWith(" STA " + loadVariable + "+1")
          ) {
        codeList.set(index, " WINC " + loadVariable + " ; (9)");
        codeList.set(index+1, ";opt (9)");
        codeList.set(index+2, ";opt (9)");
        codeList.set(index+3, ";opt (9)");
        codeList.set(index+4, ";opt (9)");
        codeList.set(index+5, ";opt (9)");
        codeList.set(index+6, ";opt (9)");
        incrementStatus(type);
      }
    }
  }

  private void wdec() {
    for (int i = 0; i < codeList.size() - 7; i++) {
      wdec(i, PeepholeType.WDEC);
    }
  }

  // @formatter:off
  //  SEC
  //  LDA COUNT
  //  SBC #<1
  //  STA COUNT ; (5) optimized, was TAY
  //  LDA COUNT+1
  //  SBC #>1
  //  STA COUNT+1 ; (4)
  private void wdec(int index, PeepholeType type) {

    if (codeList.get(index).startsWith(" SEC") &&
        codeList.get(index + 1).startsWith(" LDA") &&
        codeList.get(index + 2).startsWith(" SBC #<1") &&
        codeList.get(index + 3).startsWith(" STA") &&
        codeList.get(index + 4).startsWith(" LDA") &&
        codeList.get(index + 5).startsWith(" SBC #") &&
        codeList.get(index + 6).startsWith(" STA")
        ) {
      String value = codeList.get(index+2).replace(" SBC #<", "");
      String loadVariable = codeList.get(index+3).replace(" STA ", "");
      if (loadVariable.contains(" ")) {
        loadVariable = loadVariable.substring(0, loadVariable.indexOf(" "));
      }
      if (value.equals("1") &&
          codeList.get(index + 1).startsWith(" LDA " + loadVariable) &&
          codeList.get(index + 3).startsWith(" STA " + loadVariable) &&
          codeList.get(index + 4).startsWith(" LDA " + loadVariable + "+1") &&
          codeList.get(index + 6).startsWith(" STA " + loadVariable + "+1")
          ) {
        codeList.set(index, " WDEC " + loadVariable + " ; (23)");
        codeList.set(index+1, ";opt (23)");
        codeList.set(index+2, ";opt (23)");
        codeList.set(index+3, ";opt (23)");
        codeList.set(index+4, ";opt (23)");
        codeList.set(index+5, ";opt (23)");
        codeList.set(index+6, ";opt (23)");
        incrementStatus(type);
      }
    }
  }

  private void inc() {
    for (int i = 0; i < codeList.size() - 4; i++) {
      inc(i, PeepholeType.INC);
    }
  }

  private void inc(int index, PeepholeType type) {

    if (codeList.get(index).startsWith(" CLC") &&
        codeList.get(index + 1).startsWith(" LDA") &&
        codeList.get(index + 2).startsWith(" ADC #<1") &&
        codeList.get(index + 3).startsWith(" STA")
        ) {
      String value = codeList.get(index+2).replace(" ADC #<", "");
      String loadVariable = codeList.get(index+3).replace(" STA ", "");
      if (loadVariable.contains(" ")) {
        loadVariable = loadVariable.substring(0, loadVariable.indexOf(" "));
      }
      if (value.equals("1") &&
          codeList.get(index + 1).startsWith(" LDA " + loadVariable) &&
          codeList.get(index + 3).startsWith(" STA " + loadVariable)
          ) {
        codeList.set(index, " INC " + loadVariable + " ; (17)");
        codeList.set(index+1, ";opt (17)");
        codeList.set(index+2, ";opt (17)");
        codeList.set(index+3, ";opt (17)");
        incrementStatus(type);
      }
    }
  }

  private void dec() {
    for (int i = 0; i < codeList.size() - 4; i++) {
      dec(i, PeepholeType.DEC);
    }
  }

  private void dec(int index, PeepholeType type) {

    if (codeList.get(index).startsWith(" SEC") &&
        codeList.get(index + 1).startsWith(" LDA") &&
        codeList.get(index + 2).startsWith(" SBC #<1") &&
        codeList.get(index + 3).startsWith(" STA")
        ) {
      String value = codeList.get(index+2).replace(" SBC #<", "");
      String loadVariable = codeList.get(index+3).replace(" STA ", "");
      if (loadVariable.contains(" ")) {
        loadVariable = loadVariable.substring(0, loadVariable.indexOf(" "));
      }
      if (value.equals("1") &&
          codeList.get(index + 1).startsWith(" LDA " + loadVariable) &&
          codeList.get(index + 3).startsWith(" STA " + loadVariable)
          ) {
        codeList.set(index, " DEC " + loadVariable + " ; (24)");
        codeList.set(index+1, ";opt (24)");
        codeList.set(index+2, ";opt (24)");
        codeList.set(index+3, ";opt (24)");
        incrementStatus(type);
      }
    }
  }
  // @formatter:off
//LDX @OP
//CLC
//ADC #<3
//STA PRIME ; (5) optimized, was TAY
//TXA
private void ldx_clc_adc_sta_txa() {
  for (int i = 0; i < codeList.size() - 4; i++) {
    ldx_clc_adc_sta_txa(i, PeepholeType.LDX_TXA);
  }

}

private void ldx_clc_adc_sta_txa(int index, PeepholeType type) {
  if (codeList.get(index).startsWith(" LDX") &&
      codeList.get(index + 1).startsWith(" CLC") &&
      codeList.get(index + 2).startsWith(" ADC") &&
      codeList.get(index + 3).startsWith(" STA") &&
      codeList.get(index + 4).startsWith(" TXA")
      ) {

    LOGGER.debug("Peephole Optimization possible at Line: {}", index);

    String newLoad = codeList.get(index).replace("LDX", "LDA");
    codeList.set(index, ";opt was LDX (12)");
    codeList.set(index+4, newLoad + " ; (12)");
    incrementStatus(type);
  }
}
//@formatter:off
//tritt auf bei Parametern
//LDX @OP
//LDY #
//STA (@HEAP_PTR),Y
//TXA
  private void ldx_ldy_sta_txa() {
    for (int i = 0; i < codeList.size() - 4; i++) {
     ldx_ldy_sta_txa(i, PeepholeType.LDX_TXA);
    }
  }

  private void ldx_ldy_sta_txa(int index, PeepholeType type) {
    if (codeList.get(index).startsWith(" LDX") &&
       codeList.get(index + 1).startsWith(" LDY #") &&
       codeList.get(index + 2).startsWith(" STA") &&
       codeList.get(index + 3).startsWith(" TXA")
     ) {

     LOGGER.debug("Peephole Optimization possible at Line: {}", index);

     String newLoad = codeList.get(index).replace("LDX", "LDA");
     codeList.set(index, ";opt (13)");
     codeList.set(index+3, newLoad + " ; (13)");
     incrementStatus(type);
    }
  }
//@formatter:off
//tritt auf bei Byte-Vergleich == !=
// LDY C
// STY @ERG
// LDY #<155
// CPY @ERG
  private void compare_byte_ldy_sty_ldy_cpy() {
    for (int i = 0; i < codeList.size() - 4; i++) {
      compare_byte_ldy_sty_ldy_cpy(i, PeepholeType.COMPARE_BYTE);
    }
  }

  private void compare_byte_ldy_sty_ldy_cpy(int index, PeepholeType type) {
    if (codeList.get(index).startsWith(" LDY") &&
       codeList.get(index + 1).startsWith(" STY @ERG") &&
       codeList.get(index + 2).startsWith(" LDY") &&
       codeList.get(index + 3).startsWith(" CPY @ERG")
     ) {

     LOGGER.debug("Peephole Optimization possible at Line: {}", index);

     String newCompare = codeList.get(index).replace("LDY", "CPY");
     codeList.set(index, ";opt (14)");
     codeList.set(index+1, ";opt (14)");
     codeList.set(index+3, newCompare + " ; (14)");
     incrementStatus(type);
    }
  }

  private void tay_tya() {
    for (int i = 0; i < codeList.size() - 2; i++) {
      tay_tya(i, PeepholeType.TAY_TYA);
    }
    for (int i = 0; i < codeList.size() - 2; i++) {
      tay_ldx_tya(i, PeepholeType.TAY_TYA);
    }
  }

  private void tay_tya(int index, PeepholeType type) {
    if (codeList.get(index).startsWith(" TAY") &&
       codeList.get(index + 1).startsWith(" TYA")
     ) {

     LOGGER.debug("Peephole Optimization possible at Line: {}", index);

     codeList.set(index, ";opt TAY (15)");
     codeList.set(index+1, ";opt TYA (15)");
     incrementStatus(type);
    }
  }

  private void tay_ldx_tya(int index, PeepholeType type) {
    // @formatter:off
    if (codeList.get(index).startsWith(" TAY") &&
        codeList.get(index + 1).startsWith(" LDX") &&
        codeList.get(index + 2).startsWith(" TYA")) {
      LOGGER.debug("Peephole Optimization possible at Line: {}", index);
      codeList.set(index, ";opt TAY ; (16)");
      codeList.set(index+2, ";opt TYA ; (16)");
      incrementStatus(type);
    }
    // @formatter:on
  }

  private void sty_ldy() {
    for (int i = 0; i < codeList.size() - 2; i++) {
      sty_ldy(i, PeepholeType.STY_LDY);
    }
  }

  private void sty_ldy(int index, PeepholeType type) {
    // @formatter:off
    if (codeList.get(index).startsWith(" STY ?FOR") &&
        codeList.get(index + 1).startsWith(" LDY ?FOR")) {
      // @formatter:on

      LOGGER.debug("Peephole Optimization possible at Line: {}", index);

      codeList.set(index + 1, ";opt LDY ?FORx (18)");
      incrementStatus(type);
    }
  }

  private void word_index_add() {
    for (int i = 0; i < codeList.size() - 7; i++) {
      word_index_add(i, PeepholeType.WORD_INDEX_ADD);
    }
  }

  private void word_index_add(int index, PeepholeType type) {
    // @formatter:off
    if (codeList.get(index).startsWith(" CLC") &&
        codeList.get(index + 1).startsWith(" LDA ") &&
        codeList.get(index + 2).startsWith(" ADC #<1") &&
        codeList.get(index + 3).startsWith(" TAY") &&
        codeList.get(index + 4).startsWith(" LDA ") &&
        codeList.get(index + 5).startsWith(" ADC #") &&
        codeList.get(index + 6).startsWith(" TAX") &&
        codeList.get(index + 7).startsWith(" TYA")
        ) {
      // @formatter:on

      LOGGER.debug("Peephole Optimization possible at Line: {}", index);

      String value = codeList.get(index + 2).replace(" ADC #<", "");
      if (value.equals("1")) {
        String newLoad = codeList.get(index+1).replace(" LDA", " LDY");
        codeList.set(index + 1, newLoad);
        codeList.set(index + 2, " INY ; (19)");
        codeList.set(index + 3, " BNE ?WORD_INDEX_ADD" + count);
        String newLoad2 = codeList.get(index + 4).replace(" LDA", " LDX");
        codeList.set(index, newLoad2);
        String newLoad3 = codeList.get(index + 4).replace(" LDA", "; LDA");
        codeList.set(index + 4, newLoad3);
        codeList.set(index + 5, " INX ; (19)");
        codeList.set(index + 6, "?WORD_INDEX_ADD" + count);

        incrementStatus(type);
      }
    }
  }

  private void word_index_sub() {
    for (int i = 0; i < codeList.size() - 7; i++) {
      word_index_sub(i, PeepholeType.WORD_INDEX_SUB);
    }
  }

  private void word_index_sub(int index, PeepholeType type) {
    // @formatter:off
    if (codeList.get(index).startsWith(" SEC") &&
        codeList.get(index + 1).startsWith(" LDA ") &&
        codeList.get(index + 2).startsWith(" SBC #<1") &&
        codeList.get(index + 3).startsWith(" TAY") &&
        codeList.get(index + 4).startsWith(" LDA ") &&
        codeList.get(index + 5).startsWith(" SBC #") &&
        codeList.get(index + 6).startsWith(" TAX") &&
        codeList.get(index + 7).startsWith(" TYA")
        ) {
      // @formatter:on

      LOGGER.debug("Peephole Optimization possible at Line: {}", index);

      String value = codeList.get(index + 2).replace(" SBC #<", "");
      if (value.equals("1")) {
        String newLoad2 = codeList.get(index + 4).replace(" LDA", " LDX");
        codeList.set(index, newLoad2);
        String newLoad = codeList.get(index+1).replace(" LDA", " LDY");
        codeList.set(index + 1, newLoad);
        codeList.set(index + 2, " BNE ?WORD_INDEX_SUB" + count);
        codeList.set(index + 3, " DEX");
        codeList.set(index + 4, "?WORD_INDEX_SUB" + count);
        codeList.set(index + 5, " DEY ; (22)");
        codeList.set(index + 6, ";opt TAX");

        incrementStatus(type);
      }
    }
  }

  private void byte_index_add() {
    for (int i = 0; i < codeList.size() - 5; i++) {
      byte_index_add(i, PeepholeType.BYTE_INDEX_ADD);
    }
  }

  private void byte_index_add(int index, PeepholeType type) {
    // @formatter:off
    if (codeList.get(index).startsWith(" CLC") &&
        codeList.get(index + 1).startsWith(" LDA ") &&
        codeList.get(index + 2).startsWith(" ADC #<1") &&
        codeList.get(index + 3).startsWith(" TAY") &&
        codeList.get(index + 4).startsWith(" LDA ") &&
        !codeList.get(index + 5).startsWith(" ADC #")
        ) {
      // @formatter:on

      LOGGER.debug("Peephole Optimization possible at Line: {}", index);

      String value = codeList.get(index + 2).replace(" ADC #<", "");
      if (value.equals("1")) {
        codeList.set(index, ";opt (20)");
        String newLoad = codeList.get(index+1).replace(" LDA", " LDY");
        codeList.set(index + 1, newLoad);
        codeList.set(index + 2, " INY ; (20)");
        codeList.set(index + 3, ";opt (20)");

        incrementStatus(type);
      }
    }
  }

  private void byte_index_sub() {
    for (int i = 0; i < codeList.size() - 5; i++) {
      byte_index_sub(i, PeepholeType.BYTE_INDEX_SUB);
    }
  }

  private void byte_index_sub(int index, PeepholeType type) {
    // @formatter:off
    if (codeList.get(index).startsWith(" SEC") &&
        codeList.get(index + 1).startsWith(" LDA ") &&
        codeList.get(index + 2).startsWith(" SBC #<1") &&
        codeList.get(index + 3).startsWith(" TAY") &&
        codeList.get(index + 4).startsWith(" LDA ") &&
        !codeList.get(index + 5).startsWith(" SBC #")
        ) {
      // @formatter:on

      LOGGER.debug("Peephole Optimization possible at Line: {}", index);

      String value = codeList.get(index + 2).replace(" SBC #<", "");
      if (value.equals("1")) {
        codeList.set(index, ";opt (21)");
        String newLoad = codeList.get(index+1).replace(" LDA", " LDY");
        codeList.set(index + 1, newLoad);
        codeList.set(index + 2, " DEY ; (21)");
        codeList.set(index + 3, ";opt (21)");

        incrementStatus(type);
      }
    }
  }


  private void bpl_fa_jmp_then() {
    for (int i = 0; i < codeList.size() - 2; i++) {
      bpl_fa_jmp_then(i, PeepholeType.BPL_JMP_THEN);
    }
  }

  private void bpl_fa_jmp_then(int index, PeepholeType type) {
    // @formatter:off
    if (codeList.get(index).startsWith(" BPL ?FA") &&
        codeList.get(index + 1).startsWith(" JMP ?THEN")) {
      LOGGER.debug("Peephole Optimization possible at Line: {}", index);

      codeList.set(index, ";opt (27)");
      String oldJump = codeList.get(index+1).replace(" JMP", " BMI");
      codeList.set(index+1, oldJump + " ; (27)");

      incrementStatus(type);
    }
    // @formatter:on
  }

  private void bmi_fa_jmp_then() {
    for (int i = 0; i < codeList.size() - 2; i++) {
      bmi_fa_jmp_then(i, PeepholeType.BMI_JMP_THEN);
    }
  }

  private void bmi_fa_jmp_then(int index, PeepholeType type) {
    // @formatter:off
    if (codeList.get(index).startsWith(" BMI ?FA") &&
        codeList.get(index + 1).startsWith(" JMP ?THEN")) {
      LOGGER.debug("Peephole Optimization possible at Line: {}", index);

      codeList.set(index, ";opt (28)");
      String oldJump = codeList.get(index+1).replace(" JMP", " BPL");
      codeList.set(index+1, oldJump + " ; (28)");

      incrementStatus(type);
    }
    // @formatter:on
  }

  private void beq_fa_jmp_then() {
    for (int i = 0; i < codeList.size() - 2; i++) {
      beq_fa_jmp_then(i, PeepholeType.BEQ_JMP_THEN);
    }
  }

  private void beq_fa_jmp_then(int index, PeepholeType type) {
    // @formatter:off
    if (codeList.get(index).startsWith(" BEQ ?FA") &&
        codeList.get(index + 1).startsWith(" JMP ?THEN")) {
      LOGGER.debug("Peephole Optimization possible at Line: {}", index);

      codeList.set(index, ";opt (26)");
      String oldJump = codeList.get(index+1).replace(" JMP", " BNE");
      codeList.set(index+1, oldJump + " ; (26)");

      incrementStatus(type);
    }
    // @formatter:on
  }

  private void bne_fa_jmp_then() {
    for (int i = 0; i < codeList.size() - 2; i++) {
      bne_fa_jmp_then(i, PeepholeType.BNE_JMP_THEN);
    }
  }

  private void bne_fa_jmp_then(int index, PeepholeType type) {
    // @formatter:off
    if (codeList.get(index).startsWith(" BNE ?FA") &&
        codeList.get(index + 1).startsWith(" JMP ?THEN")) {
      LOGGER.debug("Peephole Optimization possible at Line: {}", index);

      codeList.set(index, ";opt (25)");
      String oldJump = codeList.get(index+1).replace(" JMP", " BEQ");
      codeList.set(index+1, oldJump + " ; (25)");

      incrementStatus(type);
    }
    // @formatter:on
  }

  private void bcc_fa_jmp_then() {
    for (int i = 0; i < codeList.size() - 2; i++) {
      bcc_fa_jmp_then(i, PeepholeType.BCC_JMP_THEN);
    }
  }

  private void bcc_fa_jmp_then(int index, PeepholeType type) {
    // @formatter:off
    if (codeList.get(index).startsWith(" BCC ?FA") &&
        codeList.get(index + 1).startsWith(" JMP ?THEN")) {
      LOGGER.debug("Peephole Optimization possible at Line: {}", index);

      codeList.set(index, ";opt (29)");
      String oldJump = codeList.get(index+1).replace(" JMP", " BCS");
      codeList.set(index+1, oldJump + " ; (29)");

      incrementStatus(type);
    }
    // @formatter:on
  }

  private void bcs_fa_jmp_then() {
    for (int i = 0; i < codeList.size() - 2; i++) {
      bcs_fa_jmp_then(i, PeepholeType.BCS_JMP_THEN);
    }
  }

  private void bcs_fa_jmp_then(int index, PeepholeType type) {
    // @formatter:off
    if (codeList.get(index).startsWith(" BCS ?FA") &&
        codeList.get(index + 1).startsWith(" JMP ?THEN")) {
      LOGGER.debug("Peephole Optimization possible at Line: {}", index);

      codeList.set(index, ";opt (30)");
      String oldJump = codeList.get(index+1).replace(" JMP", " BCC");
      codeList.set(index+1, oldJump + " ; (30)");

      incrementStatus(type);
    }
    // @formatter:on
  }


  /*
 * Expensive Peephole optimize
// @formatter:off
;
; c:=@peek(address + x)
;
2 CLC
3 LDA ADDRESS
3 ADC X
2 TAY
3 LDA ADDRESS+1
2 ADC #0
2 TAX
2 TYA
2 LDY #1
5 STA (@HEAP_PTR),Y
2 TXA
2 INY
5 STA (@HEAP_PTR),Y
35t - 20b

2 CLC
3 LDA ADDRESS
3 ADC X
2 LDY #1
5 STA (@HEAP_PTR),Y
3 LDA ADDRESS+1
2 ADC #0
2 INY
5 STA (@HEAP_PTR),Y
27t - 16b
    // @formatter:on

 */

  public void build() {
    if (codeList != null) {
      source.resetCode(codeList);
    }
  }
}
