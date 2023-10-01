// cdw by 'The Atari Team' 2022
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

  private String arrayVariable;
  
  public Assignment(Source source) {
    super(source);

    this.source = source;
  }

  public int code(final String sourcecodeline) {
    LOGGER.debug(sourcecodeline);
    return codeGen(sourcecodeline);
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

//        int width = source.getVariableSize(name);
        // The wishedType here is the type between the [ ] squared brackets
        // fat_byte is more than 256 bytes long byte array
        // word array is more then 512 bytes long word array
        // fat_word_array is equal to word_array
        // word_split_array is max 512 bytes word array with 2 256 byte long arrays
        Type wishedType = source.getVariableType(name);
        
        if (wishedType.equals(Type.UNKNOWN) && name.equals("@MEM")) {
          // There exist a new way to set values in Memory with the @MEM[n] fat_byte_array, faster than poke!
          source.addVariable("@MEM", Type.FAT_BYTE_ARRAY);
          wishedType = source.getVariableType(name);
        }
        
        
        if (source.getVariableType(name) == Type.FAT_BYTE_ARRAY ||
            source.getVariableType(name) == Type.WORD_ARRAY ||
            source.getVariableType(name) == Type.FAT_WORD_ARRAY) {
          wishedType = Type.WORD;
        }
        if (source.getVariableType(name) == Type.WORD_SPLIT_ARRAY ||
            source.getVariableType(name) == Type.BYTE_ARRAY ||
            source.getVariableType(name) == Type.STRING) {
          wishedType = Type.BYTE;
        }
        // TODO we should not support signed here!
        // This must convert to unsigned at first
        Symbol arrayAccess = source.nextElement();
        Symbol squaredBracketClose = new Expression(source).setType(wishedType).expression(arrayAccess).build();
        Type typeOfLastExpression = source.getTypeOfLastExpression();
        source.match(squaredBracketClose, "]");
        isArray = true;
        if (!name.equals("@MEM")) {
          if (source.isBoundsCheck()) {
            // TODO: Check if not already checked.
            code(";");
            code("; WRITE BOUNDS CHECK");
            code(";");
            code("; (y+256*x) must be less than " + source.getVariableArraySize(name));
            code(";");
            if (typeOfLastExpression.getBytes() == 1) {
              code(" LDX #0");
            }
            addBoundsCheckCode(name);
          }
        }        
        if (source.getTypeOfLastExpression() != Type.WORD &&
            source.getTypeOfLastExpression() != Type.BYTE &&
            source.getTypeOfLastExpression() != Type.UINT16) {
          source.error(arrayAccess, "The array pointer must be WORD or BYTE only. Please convert before.");
        }
        if (source.getVariableType(name) == Type.BYTE_ARRAY ||
            source.getVariableType(name) == Type.STRING) {
          code(" sty @putarray");
          arrayVariable = "@PUTARRAY";
        }
        else if (source.getVariableType(name) == Type.FAT_BYTE_ARRAY) {
          if (source.getTypeOfLastExpression().getBytes() == 1) {
            code(" ldx #0"); // fat_byte_array
          }
          if (name.equals("@MEM")) {
            code(" sty @PUTARRAY");
            code(" stx @PUTARRAY+1");
            arrayVariable = "@PUTARRAY";
          }
          else {
            code(" tya");
            code(" clc");              // old putarrayb MACRO
            code(" adc #<"+name);
            code(" sta @PUTARRAY0");
            code(" txa");
            code(" adc #>"+name);
            code(" sta @PUTARRAY0+1");
            arrayVariable = "@PUTARRAY0";
          }
        }
        else if (source.getVariableType(name) == Type.WORD_SPLIT_ARRAY) {
          code(" sty @putarray");
          arrayVariable = "@PUTARRAY";
        }
        else if (source.getVariableType(name) == Type.WORD_ARRAY ||
            source.getVariableType(name) == Type.FAT_WORD_ARRAY ) {
          if (source.getTypeOfLastExpression().getBytes() == 1) {
            code(" ldx #0"); // word_array
          }
          code(" tya");
//          code(" putarrayw " + name);
//          if (false) {
//          code(" ASL A"); // ; 2 Mult (x,y)*2
//          code(" TAY"); //          ; 2
//          code(" TXA"); //          ; 2
//          code(" ROL A"); //        ; 2
//          code(" TAX"); //          ; 2
//          code(" CLC"); //          ; 2 add %1 to the nth value
//          code(" TYA"); //          ; 2
//          code(" ADC #<" + name); //    ; 2
//          code(" STA @PUTARRAY"); //  ; 3
//          code(" TXA"); //          ; 2
//          code(" ADC #>" + name); //    ; 2
//          code(" STA @PUTARRAY+1"); //  ; 3
//          }

          /*
           * tya           ; 2
           * stx @reg+1    ; 3
           * asl a         ; 2
           * rol @reg+1    ; 5 sollte immer carry loeschen, sonst kommen wir in Teufels Kueche
           * adc #<Y       ; 2
           * sta @putarray ; 3
           * lda @reg+1    ; 3
           * adc #>Y       ; 2
           * sta @putarray+1 ; 3 25 Zyklen 16 Bytes
           * */

          code(" stx @putarray+1"); // ; 3
          code(" asl a"); //           ; 2
          code(" rol @putarray+1"); // ; 5
          code(" adc #<"+name); //     ; 2
          code(" sta @putarray"); //   ; 3
          code(" lda @putarray+1"); // ; 3
          code(" adc #>"+name); //     ; 2
          code(" sta @putarray+1"); // ; 3
          arrayVariable = "@PUTARRAY";
        }
        else {
          source.error(arrayAccess, String.format("Given variable '{%s}' is not of type array.", name));
        }
      }

      if (source.peekSymbol().get().equals(":=")) {
        Symbol assignment = source.nextElement();
        source.match(assignment, ":=");

        if (isArray == false && source.isArrayType(source.getVariableType(name))) {
          source.error(assignment, "Variable " + name + " is an array, wrong assignment.");          
        }
        
        if (source.getVariable(name).isReadOnly()) {
          source.error(assignment, "Variable " + name + " is marked readonly.");
        }
        source.getVariable(name).setWrite();

        Symbol rightHand = source.nextElement();
        if (rightHand.get().equals("[")) {
          if (name.equals("@MEM")) {
            int count = 0;
            Type wishedType = Type.BYTE;
            boolean isMoreParameter = true;
            nextSymbol = source.nextElement();
            while (isMoreParameter) {
              nextSymbol = new Expression(source).setType(wishedType).expression(nextSymbol).build();

              code(" tya");
              code(" ldy #" + count);
              code(" sta (" + arrayVariable + "),y");
              count ++;
              if (count > 255 ) {
                source.error(rightHand, "max count of ListElements [...] is 255");                
              }
              String mnemonic = nextSymbol.get();
              if (mnemonic.equals(",")) {
                nextSymbol = source.nextElement();
              }
              else {
                source.match(nextSymbol, "]");
                isMoreParameter = false;
              }
            }
            nextSymbol = source.nextElement();            
          }
          else {
            source.error(rightHand, "Assignment of ListElements [...] only to @MEM[] allowed");
          }
        }
        else {
          nextSymbol = handleSingleValue(name, isArray, rightHand);
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

//  public String convertSourceFilenameToVariable() {
//    String filename = source.getFilename();
//    filename = filename.replaceAll("\\.","_");
//    filename = filename.replaceAll("-","_");
//    return "__"+filename.toUpperCase();
//  }
  
  private void addBoundsCheckCode(String name) {
    source.addVariable(source.getFilename(), Type.STRING_ANONYM, 0, ReadOnly.YES);
    
    int variableArraySize = source.getVariableArraySize(name);
    code(" cpy #<"+variableArraySize);
    code(" txa");
    code(" sbc #>"+variableArraySize);
    code(" bcc __bounds_ok" + source.getBoundsCheckCount()); // var1 greater equal var2
    code(" jsr @panic_bounds_check"); // PANIC will not return! MUST JSR for the values after!
    code(" .word " + variableArraySize);
    code(" .word " + source.getLine());
    code(" .word " + "?STRING" + source.getVariablePosition(source.getFilename()));
    
    code("__bounds_ok" + source.getBoundsCheckCount());
    source.incrementBoundsCheckCount();
  }
  
  private Symbol handleSingleValue(String name, boolean isArray, Symbol rightHand) {
    Type wishedType = source.getVariableOverType(name);
    Symbol nextSymbol = new Expression(source).setType(wishedType).expression(rightHand).build();

    LOGGER.debug("(y,x) zuweisen an {}", name);
    if (!isArray) {
      code(" sty " + name);
      source.incrementWrite(name);
      if (source.getVariableSize(name) == 2) {
        if (source.getTypeOfLastExpression().getBytes() == 1) {
          if (source.getTypeOfLastExpression() == Type.BYTE || source.getTypeOfLastExpression() == Type.UINT8 ) {
            code(" ldx #0");
          }
          if (source.getTypeOfLastExpression() == Type.INT8) {
            code(" cpy #$80");
            code(" ldx #0");
            code(" bcc *+4");
            code(" ldx #$FF");
          }
        }
        code(" stx " + name + "+1");
        source.incrementWrite(name);
      }
    }
    else if (source.getVariableType(name) == Type.BYTE_ARRAY ||
             source.getVariableType(name) == Type.STRING) {
      code(" tya");
      code(" ldx " + arrayVariable);
      code(" sta " + name + ",x");
      source.incrementWrite(name);
    }
    else if (source.getVariableType(name) == Type.FAT_BYTE_ARRAY) {
      code(" tya");
      code(" ldy #0");
      code(" sta ("+arrayVariable+"),y");
    }
    else if (source.getVariableType(name) == Type.WORD_SPLIT_ARRAY) {
      if (source.getTypeOfLastExpression().getBytes() == 1) {
        if (source.getTypeOfLastExpression() == Type.BYTE || source.getTypeOfLastExpression() == Type.UINT8 ) {
          code(" ldx #0");
        }
        if (source.getTypeOfLastExpression() == Type.INT8) {
          code(" cpy #$80");
          code(" ldx #0");
          code(" bcc *+4");
          code(" ldx #$FF");
        }
      }
      // y/x contains value should copied to name,x and name,x
//          code(" stx @putarray+1"); // zwischenspeichern
//          code(" tya");
//          code(" ldx @putarray");
//          code(" sta " + name + "_low,x");
//          code(" lda @putarray+1"); // aus dem zwischenspeicher holen
//          code(" sta " + name + "_high,x");

      code(" txa");
      code(" ldx "+arrayVariable);
      code(" sta " + name + "_high,x");
      code(" tya");
      code(" sta " + name + "_low,x");
    }
    else if (source.getVariableType(name) == Type.WORD_ARRAY ||
        source.getVariableType(name) == Type.FAT_WORD_ARRAY) {
//          if (source.getErgebnis() == Type.BYTE) {
// TODO herausfinden, ob ich hier hin komme!
//            code(" ldx #0");
//          }
      code(" tya");
      code(" ldy #0");
      code(" sta ("+arrayVariable+"),y");
      if (source.getVariableSize(name) == 2) {
        code(" iny");
        if (source.getTypeOfLastExpression() == Type.WORD) {
          code(" txa");
        }
        else {
          code(" lda #0");
        }
        code(" sta ("+arrayVariable+"),y");
      }
    }
    return nextSymbol;
  }

  public Symbol build() {
    return nextSymbol;
  }

}
