package lla.privat.atarixl.compiler.optimization;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lla.privat.atarixl.compiler.source.Source;

public class RegisterOptimizer {

  private static final Logger LOGGER = LoggerFactory.getLogger(RegisterOptimizer.class);
      
  Source source;
  private List<String> codeList;

//  private int line;

  private String accu = "";
  private String yRegister = "";
  private String xRegister = "";

  private int countOfOptimizations;
  
  public RegisterOptimizer(Source source) {
    this.source = source;
  }

  public RegisterOptimizer optimize() {
    // create a copy of current assembler-source
    this.codeList = new ArrayList<>();
    for (int i = 0; i < source.getCode().size(); i++) {
      codeList.add(source.getCode().get(i));
    }

    registerOptimizeAll();

    LOGGER.info("Register Optimizer has {} optimizations applied.", countOfOptimizations);

    return this;
  }

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
        this.accu = "";
        this.yRegister = "";
        this.xRegister = "";
      }
      else if (codeLine.startsWith(" LDA ")) {
        String accu = getVariable(codeLine.replace(" LDA ", ""));
        if (this.accu.equals(accu) && this.accu.length() > 0) {
          // optimize possible
          codeList.set(line, "; " + codeLine + " ; (100)");

          incrementStatus();
        }
        this.accu = accu;        
      }
      else if (codeLine.startsWith(" STA ")) {
        String accu = getVariable(codeLine.replace(" STA ", ""));
        if (!accu.contains(",")) {
          if (this.xRegister.equals(accu)) {
            xRegister = "";
          }
          if (this.yRegister.equals(accu)) {
            yRegister = "";
          }
          this.accu = accu;
        }
      }
      else if (codeLine.startsWith(" LDY ")) {
        String yRegister = getVariable(codeLine.replace(" LDY ", ""));
        if (this.yRegister.equals(yRegister) && this.yRegister.length() > 0) {
          // optimize possible
          codeList.set(line, "; " + codeLine + " ; (100)");

          incrementStatus();
        }
        this.yRegister = yRegister;
      }
      else if (codeLine.startsWith(" STY ")) {
        String yRegister = getVariable(codeLine.replace(" STY ", ""));
        if (!yRegister.contains(",")) {
          if (this.xRegister.equals(yRegister)) {
            xRegister = "";
          }
          if (this.accu.equals(yRegister)) {
            this.accu = "";
          }
          this.yRegister = yRegister;
        }
      }
      else if (codeLine.startsWith(" LDX ")) {
        String xRegister = getVariable(codeLine.replace(" LDX ", ""));
        if (this.xRegister.equals(xRegister) && this.xRegister.length() > 0) {
          // optimize possible
          codeList.set(line, "; " + codeLine + " ; (100)");

          incrementStatus();
        }
        this.xRegister = xRegister;
      }
      else if (codeLine.startsWith(" STX ")) {
        String xRegister = getVariable(codeLine.replace(" STX ", ""));
        if (!xRegister.contains(",")) {
          if (this.yRegister.equals(xRegister)) {
            this.yRegister = "";
          }
          if (this.accu.equals(xRegister)) {
            this.accu = "";
          }
          this.xRegister = xRegister;
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
          codeLine.startsWith(" ASL") || // TODO! Only ASL A, LSR A, ROR A, ROL A will destroy accu
          codeLine.startsWith(" LSR") ||
          codeLine.startsWith(" ROL") ||
          codeLine.startsWith(" ROR") ||
          codeLine.startsWith(" TXA") ||
          codeLine.startsWith(" TYA") ||
          codeLine.startsWith(" PLA")) {
        // @formatter:on

        // accu wurde manipulliert, also einfach löschen
        this.accu = "";
      }

      // @formatter:off
      else if (
          codeLine.startsWith(" INY") ||
          codeLine.startsWith(" DEY") ||
          codeLine.startsWith(" TAY")) {
        // yRegister wurde manipuliert, also einfach löschen
        // @formatter:on
        this.yRegister = "";
      }
      // @formatter:off
      else if (
          codeLine.startsWith(" INX") ||
          codeLine.startsWith(" DEX") ||
          codeLine.startsWith(" TAX") ||
          codeLine.startsWith(" TSX")) {
        // xRegister wurde manipuliert, also einfach löschen
        // @formatter:on
        this.xRegister = "";
      }
    }
  }

  private void incrementStatus() {
    ++countOfOptimizations;
  }

  public int getUsedOptimisations() {
    return countOfOptimizations;
  }
  
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
    if (variable.startsWith("@")) {
      return "";
    }
    return variable;
  }
}
