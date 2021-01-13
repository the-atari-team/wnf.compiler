// cdw by 'The Atari Team' 2020
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lla.privat.atarixl.compiler.expression.Expression;
import lla.privat.atarixl.compiler.expression.Type;
import lla.privat.atarixl.compiler.source.Code;
import lla.privat.atarixl.compiler.source.Source;

/**
 * Assignment means a variable get a new value
 *
 * <li>a:=1
 * <li>a[i]:=j
 *
 * @author develop
 *
 */
public class Assignment extends Code {
  private static final Logger LOGGER = LoggerFactory.getLogger(Assignment.class);

  private final Source source;

  private Symbol nextSymbol;

  public Assignment(Source source) {
    super(source);

    this.source = source;
  }

  public void code(final String sourcecodeline) {
    LOGGER.debug(sourcecodeline);
    codeGen(sourcecodeline);
  }

  /**
   * Assignment variable := Expression
   *
   * @param symbol
   * @return
   */

  public Assignment assign(final Symbol symbol) {
    if (symbol.getId() == SymbolEnum.variable_name) {
      String name = symbol.get();

      boolean isArray = false;
      if (source.peekSymbol().get().equals("[")) {
        Symbol squaredBracketOpen = source.nextElement();
        source.match(squaredBracketOpen, "[");

        int width = source.getVariableSize(name);

        if (source.getVariableType(name) == Type.FAT_BYTE_ARRAY) {
          width = 2;
        }
        Symbol arrayAccess = source.nextElement();
        Symbol squaredBracketClose = new Expression(source).setWidth(width).expression(arrayAccess).build();
        source.match(squaredBracketClose, "]");
        isArray = true;

        if (source.getVariableType(name) == Type.BYTE_ARRAY) {
          code(" sty @putarray");
        }
        else if (source.getVariableType(name) == Type.FAT_BYTE_ARRAY) {
          if (source.getErgebnis().getBytes() == 1) {
            code(" ldx #0");
          }
          code(" tya");
          code(" putarrayb " + name);
        }
        else if (source.getVariableType(name) == Type.WORD_ARRAY) {
          if (source.getErgebnis().getBytes() == 1) {
            code(" ldx #0");
          }
          code(" tya");
          code(" putarrayw " + name);
        }
        else {
          source.error(arrayAccess, String.format("Given variable '{%s}' is not of type array.", name));
        }
      }
      if (source.peekSymbol().get().equals(":=")) {
        Symbol assignment = source.nextElement();
        source.match(assignment, ":=");

        source.getVariable(name).setWrite();

        Symbol rightHand = source.nextElement();
        int width = source.getVariableSize(name);
        nextSymbol = new Expression(source).setWidth(width).expression(rightHand).build();

        LOGGER.debug("(y,x) zuweisen an {}", name);
        if (!isArray) {
          code(" sty " + name);
          if (source.getVariableSize(name) == 2) {
            if (source.getErgebnis().getBytes() == 1) {
              code(" ldx #0");
            }
            code(" stx " + name + "+1");
          }
        }
        else if (source.getVariableType(name) == Type.BYTE_ARRAY) {
          code(" tya");
          code(" ldx @putarray");
          code(" sta " + name + ",x");
        }
        else if (source.getVariableType(name) == Type.FAT_BYTE_ARRAY) {
          code(" tya");
          code(" ldy #0");
          code(" sta (@putarray),y");
        }
        else if (source.getVariableType(name) == Type.WORD_ARRAY) {
          code(" tya");
          code(" ldy #0");
          code(" sta (@putarray),y");
          if (source.getVariableSize(name) == 2) {
            code(" iny");
            if (source.getErgebnis().getBytes() == 2) {
              code(" txa");
            }
            else {
              code(" lda #0");
            }
            code(" sta (@putarray),y");
          }
        }
      }
      else {
        source.error(source.peekSymbol(), ":= expected");
      }
    }
    else {
      source.error(symbol, "variable name expected");
    }
    return this;
  }

  public Symbol build() {
    return nextSymbol;
  }

}
