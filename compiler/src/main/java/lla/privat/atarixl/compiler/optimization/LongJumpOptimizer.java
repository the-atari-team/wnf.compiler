package lla.privat.atarixl.compiler.optimization;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lla.privat.atarixl.compiler.source.Source;

public class LongJumpOptimizer extends Optimizer {

  private static final Logger LOGGER = LoggerFactory.getLogger(LongJumpOptimizer.class);

  private List<String> codeList;

  public LongJumpOptimizer(Source source, int optimizationLevel) {
    super(source, optimizationLevel);
  }

  @Override
  public LongJumpOptimizer optimize() {
    // create a copy of current assembler-source
    this.codeList = new ArrayList<>();
    for (int i = 0; i < source.getCode().size(); i++) {
      codeList.add(source.getCode().get(i));
    }

    if (optimisationLevel > 2) {
      longJumpOptimizeAll();
    }
    
    LOGGER.info("Long Jump Optimizer has {} optimizations applied.", getUsedOptimisations());

    // Due to a bug in atasm we need to replace
    // JCC jumpmark ; to
    // $B0 $03      ; bcs *+3
    // JMP jumpmark
    
//    if (optimisationLevel > 2) {
// TODO: only need until atasm v1.22
//      jccFixer();
//    }
    
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
      LOGGER.info("Long Jump Optimizer Status Usage:");
      LOGGER.info("{} number of register optimizations", getUsedOptimisations());
    }
  }

  private void longJumpOptimizeAll() {
    for (int i = 0; i < codeList.size() - 4; i++) {
      longjump_optimize(i);
    }
  }

  private void longjump_optimize(int index) {
    // @formatter:off
      if (codeList.get(index).startsWith(" JNE ") ||
          codeList.get(index).startsWith(" JEQ ") ||
          codeList.get(index).startsWith(" JCC ") ||
          codeList.get(index).startsWith(" JCS ") ||
          codeList.get(index).startsWith(" JPL ") ||
          codeList.get(index).startsWith(" JMI ")        
          ) {

        LOGGER.debug("Peephole Optimization possible at Line: {}", index);

        String jumpMark = codeList.get(index).substring(5);
//        System.out.println("LINE:(" + index + "): " + codeList.get(index));
        
        // We must search from beginning to index-1 and search for the falseNumber and replace by jump to elseNumber
        int positiv = 0;
        // boolean found = false;
        int size = codeList.size()-4;
        
        for (int i = index + 1; i < index + 128; i++) {
          if (i >= size) {break;}
          if (positiv > 127) {break;}
          
          String mnemonic = codeList.get(i);
//          System.out.println("forward - " + mnemonic);

          if (jumpMark.startsWith(mnemonic)) {
            if (positiv < 127) {
              mnemonic = codeList.get(index).replace('J', 'B');
              codeList.set(index, mnemonic + " ; (99)");
            }
            // found = true;
            break;
          }
          else {
            positiv += getGrobLengthOfMnemonic(mnemonic);
          }
        }
//        System.out.println("------------------------------");
        // TODO: look backward?
        
        incrementStatus();
      }
      // @formatter:on
  }

  private void incrementStatus() {
    incrementCountOfOptimize();
  }
  
  private int getGrobLengthOfMnemonic(String mnemonic) {
    if (mnemonic.length() < 1) {
      return 0;
    }

    if (mnemonic.charAt(0) == ';') {
      return 0;
    } // comment
    if (mnemonic.charAt(0) == '?') {
      return 0;
    } // mark
    if (mnemonic.charAt(0) >= '@' && mnemonic.charAt(0) >= 'Z') {
      return 0;
    } // variable?

    if (mnemonic.startsWith(" CLC") || mnemonic.startsWith(" SEC") || mnemonic.startsWith(" TAX")
        || mnemonic.startsWith(" TAY") || mnemonic.startsWith(" TXA") || mnemonic.startsWith(" TYA")
        || mnemonic.startsWith(" RTS") || mnemonic.startsWith(" INX") || mnemonic.startsWith(" INY")
        || mnemonic.startsWith(" DEX") || mnemonic.startsWith(" DEY") || mnemonic.startsWith(" PLA")
        || mnemonic.startsWith(" PHA") || mnemonic.startsWith(" RTI")) {
      return 1;
    }

    if (mnemonic.charAt(5) == '#') {
      return 2;
    } // imediate .lda.#, cpy #, adc #
    if (mnemonic.charAt(5) == '(') {
      return 2;
    } // indirect

    return 3;
  }


  //  private void jccFixer() {
//    int fixed = 0;
//    
//    for (int i = 0; i < codeList.size(); i++) {
//      if (codeList.get(i).startsWith(" JCC ")) {
//        String jumpmark = codeList.get(i).replace(" JCC ", "");
//        if (jumpmark.contains(";")) {
//          int semikolon = jumpmark.indexOf(";");
//          if (semikolon>0) {
//            jumpmark = jumpmark.substring(0, semikolon-1);
//          }
//        }
//        // That we give it in a byte code, because we can't simple update the list
//        // also it is just a fix
//        codeList.set(i, " .BYTE $B0,$03,$4C,<" + jumpmark + ",>" + jumpmark + " ; jccfixer");
//        ++fixed;
//      }
//    }
//    LOGGER.info("Long Jump Optimizer use jccfixer:{} times", fixed);
//  }

}
