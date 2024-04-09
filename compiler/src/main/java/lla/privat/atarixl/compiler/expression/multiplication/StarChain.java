//cdw by 'The Atari Team' 2022
//licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler.expression.multiplication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lla.privat.atarixl.compiler.source.Code;
import lla.privat.atarixl.compiler.source.Source;

public class StarChain  extends Code {
  // Program to generate a "star-chain" sequence to replace multiplication by a positive integer constant
  // with a series of add, substract, and shift-left instructions.
  // Assumes two machine "registers". Instructions are formed on a temporary stack, then output. A stack
  // element's magnitude is the shift amount, the sign indicates subsequent add (plus) or substract (minus).

  private static final Logger LOGGER = LoggerFactory.getLogger(StarChain.class);

  // private final Source source;

  int[] stack;

  int mult;

  public StarChain(Source source) {
    super(source);

    // this.source = source;
    stack = new int[10];
  }

  private int trim_trailung(int one_zero) {
    int count = 0;
    while ((mult & 1) == one_zero) {  // ???
      mult >>= 1;              // mult := mult / 2
      count++;
    }
    return count;
  }

  // @return StarChain for fluid programming
  public StarChain domult(int multiplier) {
    mult = multiplier;            // mult contains the multiplier
    int max_stackpointer = 0;
    int stackpointer = 0;

    LOGGER.debug("R1 * " + mult);
    int last_shift = 0;

    if (mult > 0) {
      int last_count = 0;
      int flag = 0;

      last_shift = trim_trailung(0); // cut trailing 0
      while(true) {
        int count = trim_trailung(1);
        if (count > 1) {
          flag = 0;
          if (last_count == 1) {
            // shift k, sub, shift 1, add--
            stack[stackpointer-1] = -(count+1);
          }
          else {
            stack[stackpointer++] = -count;
          }
        }
        else {
          // need another shift-add
          flag = 1;
        }
        if (mult == 0) break; // mult fully decomposed, out

        // count low-order zeros
        last_count = trim_trailung(0) + flag;
        stack[stackpointer++] = last_count;
      }
    }
    max_stackpointer = stackpointer;
    codegen(multiplier, stack, stackpointer, last_shift);

    LOGGER.debug("--------");
    return this;
  }

  public void codegen(int multiplier, int[] stack, int stackpointer, int last_shift) {
    // output code from stack
    if (multiplier > 0) {

      LOGGER.debug("Rw = R1");
//      code(" STY @OP");
      code(" TYA");
      code(" STX @OP+1");               // 6 Takte neu 5
      if (stackpointer > 0) {
        code(" sta @PRODUKT");
        code(" stx @PRODUKT+1");          // 6 Takte
      }
      while (stackpointer > 0) {
        int ts = stack[--stackpointer];
        if (ts < 0) {
          LOGGER.debug("Rw <<= " + -ts);
          rotateLeft(-ts);

          LOGGER.debug("Rw -= R1");
          code(" SEC");
          // code(" LDA @OP");
          code(" SBC @PRODUKT");
//          code(" STA @OP");
          code(" TAY");
          code(" LDA @OP+1");
          code(" SBC @PRODUKT+1");
          code(" STA @OP+1");           // 20 Takte neu 18
          code(" TYA");
        }
        else {
          LOGGER.debug("Rw <<= " + ts);
          rotateLeft(ts);

          LOGGER.debug("Rw += R1");
          code(" CLC");
//          code(" LDA @OP");
          code(" ADC @PRODUKT");
//          code(" STA @OP");
          code(" TAY");
          code(" LDA @OP+1");
          code(" ADC @PRODUKT+1");
          code(" STA @OP+1");            // 20 Takte neu 18
          code(" TYA");
        }
      }
      if (last_shift != 0) {
        LOGGER.debug("Rw <<= " + last_shift);
        rotateLeft(last_shift);
      }
    }
    else {
      LOGGER.debug("Rw = 0"); // spezial case mult with 0
      code(" lda #0");
      code(" sta @op");
      code(" sta @op+1");
    }
  }

  private void rotateLeft(int count) {
    for(int i=0;i<count;i++) {
//      code(" ASL @OP");
      code(" ASL A");
      code(" ROL @OP+1");                 // 10 Takte neu 7
    }
  }

  public void build() {
//    code(" LDY @OP");
    code(" TAY");
    code(" LDX @OP+1");                   // 6 Takte neu 5
  }

  @Override
  public int code(final String sourcecodeline) {
    LOGGER.debug(sourcecodeline);
    return codeGen(sourcecodeline);
  }
}
