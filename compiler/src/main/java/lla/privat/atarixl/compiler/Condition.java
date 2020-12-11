// cdw by 'The Atari Team' 2020
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lla.privat.atarixl.compiler.expression.Expression;
import lla.privat.atarixl.compiler.source.Code;
import lla.privat.atarixl.compiler.source.Source;

public class Condition extends Code {
  private static final Logger LOGGER = LoggerFactory.getLogger(Condition.class);

  private final Source source;

  private Symbol nextSymbol;
  private final String conditionStr;

  public Condition(Source source, final String conditionStr) {
    super(source);
    this.source = source;
    this.conditionStr = conditionStr;
  }

  public void code(final String sourcecodeline) {
    LOGGER.debug(sourcecodeline);
    codeGen(sourcecodeline);
  }




  public Condition condition(Symbol symbol) {

    boolean hasMoreCondition = true;
    int ct = source.getConditionCount();
    int ctf = ct;

    nextSymbol = symbol;

    while (hasMoreCondition) {
      Symbol condition = new Expression(source).expression(nextSymbol).build();

      code(" STY @ERG");
      int ergebnisBytes1 = source.getErgebnis().getBytes();
      if (ergebnisBytes1 == 2) {
        code(" STX @ERG+1");
      }

      if (condition.getId() == SymbolEnum.condition_symbol) {
        String currentCondition = condition.get();

        nextSymbol = source.nextElement();
        nextSymbol = new Expression(source).expression(nextSymbol).build();

        int ergebnisBytes2 = source.getErgebnis().getBytes();

        code("; Bedingung (a" + currentCondition + "b)");
        if (ergebnisBytes1 == 1 && ergebnisBytes2 != 1) {
          code(" lda #0");
          code(" sta @erg+1");
        }
        else if (ergebnisBytes2 == 1 && ergebnisBytes1 != 1) {
          code(" ldx #0");
        }
        if (ergebnisBytes1 == 1 && ergebnisBytes2 == 1) {
          code(" cpy @erg");
          switch (currentCondition) {
          case "<>":
          case "!=":
            code(" beq ?fa" + ctf);
            break;

          case "=":
          case "==":
            code(" bne ?fa" + ctf);
            break;

          case "<":
            code(" beq ?fa" + ctf);
            code(" bcc ?fa" + ctf);
            break;
          case ">=":
            code(" beq ?tr" + ct);
            code(" bcs ?fa" + ctf);
            code("?tr" + ct);
            break;
          case ">":
            code(" bcs ?fa" + ctf);
            break;
          case "<=":
            code(" bcc ?fa" + ctf);
            break;
          }
        }
        else {
          switch (currentCondition) {
          case "<>":
          case "!=":
            code(" cpy @erg");
            code(" bne ?tr" + ct);
            code(" cpx @erg+1");
            code(" beq ?fa" + ctf);
            code("?tr" + ct);
            break;
          case "=":
          case "==":
            code(" cpy @erg");
            code(" bne ?fa" + ctf);
            code(" cpx @erg+1");
            code(" bne ?fa" + ctf);
            break;
          case "<=":
            code(" cpy @erg");
            code(" txa");
            code(" sbc @erg+1");
            code(" bvc ?vc" + ct);
            code(" eor #$80");
            code("?vc" + ct);
            code(" bmi ?fa" + ctf);
            break;
          case ">":
            code(" cpy @erg");
            code(" txa");
            code(" sbc @erg+1");
            code(" bvc ?vc" + ct);
            code(" eor #$80");
            code("?vc" + ct);
            code(" bpl ?fa" + ctf);
            break;
          case "<":
            code(" cpy @erg");
            code(" bne ?ne"+ct);
            code(" cpx @erg+1");
            code(" beq ?fa" + ctf);
            code("?ne"+ct);
            code(" cpy @erg");
            code(" txa");
            code(" sbc @erg+1");
            code(" bvc ?vc" + ct);
            code(" eor #$80");
            code("?vc" + ct);
            code(" bmi ?fa" + ctf);
            break;
          case ">=":
            code(" cpy @erg");
            code(" bne ?ne"+ct);
            code(" cpx @erg+1");
            code(" beq ?tr" + ct);
            code("?ne"+ct);
            code(" cpy @erg");
            code(" txa");
            code(" sbc @erg+1");
            code(" bvc ?vc" + ct);
            code(" eor #$80");
            code("?vc" + ct);
            code(" bpl ?fa" + ctf);
            code("?tr" + ct);
            break;
          }

        }
      }
      else {
        source.error(condition, "condition expected");
      }

      String mnemonic = nextSymbol.get();
      if (mnemonic.equals("OR")) {
        code(" jmp "+conditionStr); // fuer OR
        code("?fa"+ctf);
        source.incrementConditionCount();
        ++ct;
        ++ctf;

        nextSymbol = source.nextElement();
      }
      else if (mnemonic.equals("AND")) {
        source.incrementConditionCount();
        ++ct;
        nextSymbol = source.nextElement();
      }
      else {
        hasMoreCondition = false;
      }
    }
    code(" jmp "+conditionStr);
    code("?fa"+ctf);

    source.incrementConditionCount();
    return this;
  }

  public Symbol build() {
    return nextSymbol;
  }

}
