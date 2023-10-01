package lla.privat.atarixl.compiler.optimization;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lla.privat.atarixl.compiler.source.Source;

public class RegisterOptimizer extends Optimizer {

  private static final Logger LOGGER = LoggerFactory.getLogger(RegisterOptimizer.class);
      
  // Source source;
  private List<String> codeList;

//  private int line;

  private ArrayList<String> accu = new ArrayList<>();
  private ArrayList<String> yRegisterList = new ArrayList<>();
  private ArrayList<String> xRegisterList = new ArrayList<>();

  private int countOfOptimizations;
  
  public RegisterOptimizer(Source source, int optimizationLevel) {
    super(source, optimizationLevel);
    
    // this.source = source;
  }

  @Override
  public RegisterOptimizer optimize() {
    // create a copy of current assembler-source
    this.codeList = new ArrayList<>();
    for (int i = 0; i < source.getCode().size(); i++) {
      codeList.add(source.getCode().get(i));
    }

//    if (optimisationLevel > 0) {
      registerOptimizeAll();
//    }
    
    LOGGER.info("Register Optimizer has {} optimizations applied.", countOfOptimizations);

    return this;
  }

  @Override
  public void build() {
    if (codeList != null) {
      source.resetCode(codeList);
    }
  }
  
  public void showStatus() {
      if (source.showPeepholeOptimize()) {
        LOGGER.info("Register Optimizer Status Usage:");
        LOGGER.info("{} number of register optimizations", countOfOptimizations);
      }
  }

  private void registerOptimizeAll() {
    for (int line = 0; line < codeList.size() - 1; line++) {
      final String codeLine = codeList.get(line);

      if (isSprungMarke(codeLine)) {
        // Sämtliche Register löschen.
        this.accu.clear();
        this.yRegisterList.clear();
        this.xRegisterList.clear();
      }
      else if (codeLine.startsWith(" LDA ")) {
        String accu = getVariable(codeLine.replace(" LDA ", ""));
        if (!accu.contains(",") && this.accu.contains(accu)) {
          // optimize possible
          codeList.set(line, "; " + codeLine + " ; (100)");

          incrementStatus();
        }
        else {
          this.accu.clear();
          if (accu.length() > 0) {
            this.accu.add(accu);
          }
        }
      }
      else if (codeLine.startsWith(" STA ")) {
        String accu = getVariable(codeLine.replace(" STA ", ""));
        if (!accu.contains(",")) {
          if (this.yRegisterList.contains(accu)) {
            yRegisterList.clear();
          }
          if (this.xRegisterList.contains(accu)) {
            xRegisterList.clear();
          }
          if (accu.length() > 0) {
            this.accu.add(accu);
          }
        }
      }
      else if (codeLine.startsWith(" LDY ")) {
        String yRegister = getVariable(codeLine.replace(" LDY ", ""));
        if (!yRegister.contains(",") && yRegisterList.contains(yRegister)) {
          // optimize possible
          codeList.set(line, "; " + codeLine + " ; (100)");

          incrementStatus();
        }
        else {
          this.yRegisterList.clear();
          this.yRegisterList.add(yRegister);
        }
      }
      else if (codeLine.startsWith(" STY ")) {
        String yRegister = getVariable(codeLine.replace(" STY ", ""));
        if (!yRegister.contains(",") && yRegister.length() > 0) {
          if (this.xRegisterList.contains(yRegister)) {
            xRegisterList.clear();
          }
          if (this.accu.contains(yRegister)) {
            this.accu.clear();
          }
          // this.yRegisterList.clear();
          this.yRegisterList.add(yRegister);
        }
      }
      else if (codeLine.startsWith(" LDX ")) {
        String xRegister = getVariable(codeLine.replace(" LDX ", ""));
        if (!xRegister.contains(",") && this.xRegisterList.contains(xRegister)) {
          // optimize possible
          codeList.set(line, "; " + codeLine + " ; (100)");

          incrementStatus();
        }
        else {
          this.xRegisterList.clear();
          this.xRegisterList.add(xRegister);
        }
      }
      else if (codeLine.startsWith(" STX ")) {
        String xRegister = getVariable(codeLine.replace(" STX ", ""));
        if (!xRegister.contains(",")) {
          if (this.yRegisterList.contains(xRegister)) {
            this.yRegisterList.clear();
          }
          if (this.accu.contains(xRegister)) {
            this.accu.clear();
          }
          this.xRegisterList.add(xRegister);
        }
      }
      // @formatter:off
      // all of next Memonics will destroy/change the accu
      else if (
          codeLine.startsWith(" ADC ") ||
          codeLine.startsWith(" SBC ") ||
          codeLine.startsWith(" AND ") ||
          codeLine.startsWith(" EOR ") ||
          codeLine.startsWith(" ORA ") ||
          codeLine.startsWith(" TXA") ||
          codeLine.startsWith(" TYA") ||
          codeLine.startsWith(" PLA")) {
        // @formatter:on

        // accu wurde manipulliert, also einfach löschen
        this.accu.clear();
      }

//      codeLine.startsWith(" LSR") ||
//      codeLine.startsWith(" ROL") ||
//      codeLine.startsWith(" ROR")

      // @formatter:off
      else if (codeLine.startsWith(" ASL")) { // TODO! Only ASL A, LSR A, ROR A, ROL A will destroy accu
      // @formatter:on
      // accu wurde manipulliert, also einfach löschen
        String memory = getVariable(codeLine.replace(" ASL ", ""));
        if (memory.contains(",")) {
          yRegisterList.clear();
          xRegisterList.clear();
          accu.clear();
        }
        else {
          if (this.yRegisterList.contains(memory)) {
            this.yRegisterList.clear();
          }
          if (this.xRegisterList.contains(memory)) {
            this.xRegisterList.clear();
          }
          if (this.accu.contains(memory) || memory.equals("A")) {
            this.accu.clear();
          }
        }
      }
      else if (codeLine.startsWith(" LSR")) { // TODO! Only ASL A, LSR A, ROR A, ROL A will destroy accu
      // @formatter:on
      // accu wurde manipulliert, also einfach löschen
        String memory = getVariable(codeLine.replace(" LSR ", ""));
        if (memory.contains(",")) {
          yRegisterList.clear();
          xRegisterList.clear();
          accu.clear();
        }
        else {
          if (this.yRegisterList.contains(memory)) {
            this.yRegisterList.clear();
          }
          if (this.xRegisterList.contains(memory)) {
            this.xRegisterList.clear();
          }
          if (this.accu.contains(memory) || memory.equals("A")) {
            this.accu.clear();
          }
        }
      }
      else if (codeLine.startsWith(" ROR")) { // TODO! Only ASL A, LSR A, ROR A, ROL A will destroy accu
      // @formatter:on
      // accu wurde manipulliert, also einfach löschen
        String memory = getVariable(codeLine.replace(" ROR ", ""));
        if (memory.contains(",")) {
          yRegisterList.clear();
          xRegisterList.clear();
          accu.clear();
        }
        else {
          if (this.yRegisterList.contains(memory)) {
            this.yRegisterList.clear();
          }
          if (this.xRegisterList.contains(memory)) {
            this.xRegisterList.clear();
          }
          if (this.accu.contains(memory) || memory.equals("A")) {
            this.accu.clear();
          }
        }
      }
      else if (codeLine.startsWith(" ROL")) { // TODO! Only ASL A, LSR A, ROR A, ROL A will destroy accu
      // @formatter:on
      // accu wurde manipulliert, also einfach löschen
        String memory = getVariable(codeLine.replace(" ROL ", ""));
        if (memory.contains(",")) {
          yRegisterList.clear();
          xRegisterList.clear();
          accu.clear();
        }
        else {
          if (this.yRegisterList.contains(memory)) {
            this.yRegisterList.clear();
          }
          if (this.xRegisterList.contains(memory)) {
            this.xRegisterList.clear();
          }
          if (this.accu.contains(memory) || memory.equals("A")) {
            this.accu.clear();
          }
        }
      }

      // @formatter:off
      else if (
          codeLine.startsWith(" INY") ||
          codeLine.startsWith(" DEY") ||
          codeLine.startsWith(" TAY")) {
        // yRegister wurde manipuliert, also einfach löschen
        // @formatter:on
        this.yRegisterList.clear();
      }
      // @formatter:off
      else if (
          codeLine.startsWith(" INX") ||
          codeLine.startsWith(" DEX") ||
          codeLine.startsWith(" TAX") ||
          codeLine.startsWith(" TSX")) {
        // xRegister wurde manipuliert, also einfach löschen
        // @formatter:on
        this.xRegisterList.clear();
      }
      else if (codeLine.startsWith(" INC ")) {
        String memory = getVariable(codeLine.replace(" INC ", ""));
        if (memory.contains(",")) {
          memory = memory.substring(0, memory.indexOf(','));
        }
        if (this.xRegisterList.contains(memory)) {
          xRegisterList.clear();
        }
        if (this.yRegisterList.contains(memory)) {
          yRegisterList.clear();
        }
        if (this.accu.contains(memory)) {
          this.accu.clear();
        }
      }
      else if (codeLine.startsWith(" DEC ")) {
        String memory = getVariable(codeLine.replace(" DEC ", ""));
        if (memory.contains(",")) {
          memory = memory.substring(0, memory.indexOf(','));
        }
        if (this.xRegisterList.contains(memory)) {
          xRegisterList.clear();
        }
        if (this.yRegisterList.contains(memory)) {
          yRegisterList.clear();
        }
        if (this.accu.contains(memory)) {
          this.accu.clear();
        }
      }
      
    }
  }

  private void incrementStatus() {
    incrementCountOfOptimize();
    ++countOfOptimizations;
  }

//  public int getUsedOptimisations() {
//    return countOfOptimizations;
//  }
  
  private boolean isSprungMarke(String codeLine) {
    if (codeLine.charAt(0) == '?' && codeLine.charAt(1) == 'F' && codeLine.charAt(2) == 'A' &&
        (codeLine.charAt(3) >= '0' && codeLine.charAt(3) <= '9')) {
      // TODO: Test of Sonderlocke für 'if a=1 or a=2 or a=3...' 
      return false;
    }
      
    if (codeLine.charAt(0) == '@' || codeLine.charAt(0) == '?' || codeLine.charAt(0) == '_'
        || (codeLine.charAt(0) >= 'A' && codeLine.charAt(0) <= 'Z')) {
      return true;
    }
    // koennen wir nicht verfolgen!
    if (codeLine.startsWith(" JSR ") || codeLine.startsWith(" BRK ") || codeLine.startsWith(" RTS ")
        || codeLine.startsWith(" RTI ") || codeLine.startsWith(" JMP ")) {
      return true;
    }

    if (codeLine.startsWith(" BNE ?THEN") ||
        codeLine.startsWith(" BEQ ?THEN"))
    {
      // TODO: Test of Sonderlocke für 'if a=1 or a=2 or a=3...' 
      return false;
    }

    if (codeLine.startsWith(" BCC ") ||
        codeLine.startsWith(" BCS ") ||
        codeLine.startsWith(" BEQ ") ||
        codeLine.startsWith(" BMI ") ||
        codeLine.startsWith(" BNE ") ||
        codeLine.startsWith(" BPL ") ||
        codeLine.startsWith(" BVC ") ||
        codeLine.startsWith(" BVS ")) {
      return true;
    }

    return false;
  }
  
  private String getVariable(final String variableAndComment) {
    String variable = variableAndComment;
    int space = variable.indexOf(" ;");
    if (space != -1) {
      variable = variable.substring(0, space);
    }
    if (variable.startsWith("(")) {
      return "";
    }
//    if (variable.startsWith("@")) {
//      return "";
//    }
    if (variable.equals("#<0") || variable.equals("#>0")) {
      variable = "#0";
    }
    return variable;
  }
}
