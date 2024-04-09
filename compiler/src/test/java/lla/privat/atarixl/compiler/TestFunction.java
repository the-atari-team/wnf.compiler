// cdw by 'The Atari Team' 2022
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import lla.privat.atarixl.compiler.expression.Type;
import lla.privat.atarixl.compiler.source.Source;

public class TestFunction {

  @Test
  public void testFunction() {
    Source source = new Source("function name() begin end").setVerboseLevel(1);
    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Procedure(source).procedure(symbol, Type.FUNCTION).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    List<String> code = source.getCode();

    int n = -1;
    Assert.assertEquals(";", code.get(++n));
    Assert.assertEquals("; [1]  function name() begin end", code.get(++n));
    Assert.assertEquals(";", code.get(++n));

    Assert.assertEquals("NAME", code.get(++n));
    Assert.assertEquals("; NAME", code.get(++n));
    Assert.assertEquals("; function body", code.get(++n));
    Assert.assertEquals("; function end", code.get(++n));
    Assert.assertEquals("?RETURN1", code.get(++n));
    Assert.assertEquals(" RTS", code.get(++n));
  }

  @Test
  public void testFunctionOneParameter() {
    Options options = new Options();
// options.setSmallAddSubHeapPtr(false);
    options.setSaveLocalToStack(false);
    options.setVerboseLevel(1);

    Source source = new Source("function name(one) begin end").setVerboseLevel(1);
    source.setOptions(options);
    
    source.addVariable("ONE", Type.BYTE);

    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Procedure(source).procedure(symbol, Type.FUNCTION).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    List<String> code = source.getCode();

    int n = -1;
    Assert.assertEquals(";", code.get(++n));
    Assert.assertEquals("; [1]  function name(one) begin end", code.get(++n));
    Assert.assertEquals(";", code.get(++n));

    Assert.assertEquals("NAME", code.get(++n));
    Assert.assertEquals("NAME_I", code.get(++n));
    Assert.assertEquals(" LDX ONE", code.get(++n));
    Assert.assertEquals(" LDY #1", code.get(++n));
    Assert.assertEquals(" LDA (@HEAP_PTR),Y", code.get(++n));
    Assert.assertEquals(" STA ONE", code.get(++n));
    Assert.assertEquals(" TXA", code.get(++n));
    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));

//    Assert.assertEquals(" ADD_TO_HEAP_PTR 3", code.get(++n));
    Assert.assertEquals(" CLC", code.get(++n));
    Assert.assertEquals(" LDA @HEAP_PTR", code.get(++n));
    Assert.assertEquals(" ADC #3", code.get(++n));
    Assert.assertEquals(" STA @HEAP_PTR", code.get(++n));
    Assert.assertEquals(" BCC *+4", code.get(++n));
    Assert.assertEquals(" INC @HEAP_PTR+1", code.get(++n));

    Assert.assertEquals("; function body", code.get(++n));
    Assert.assertEquals("; function end", code.get(++n));

    Assert.assertEquals("?RETURN1", code.get(++n));
    Assert.assertEquals(" STY @REG+2", code.get(++n));
//    Assert.assertEquals(" SUB_FROM_HEAP_PTR 3", code.get(++n));
    Assert.assertEquals(" SEC", code.get(++n));
    Assert.assertEquals(" LDA @HEAP_PTR", code.get(++n));
    Assert.assertEquals(" SBC #3", code.get(++n));
    Assert.assertEquals(" STA @HEAP_PTR", code.get(++n));
    Assert.assertEquals(" BCS *+4", code.get(++n));
    Assert.assertEquals(" DEC @HEAP_PTR+1", code.get(++n));

    Assert.assertEquals(" LDY #1", code.get(++n));
    Assert.assertEquals(" LDA (@HEAP_PTR),Y", code.get(++n));
    Assert.assertEquals(" STA ONE", code.get(++n));

    Assert.assertEquals(" LDY @REG+2", code.get(++n));
    Assert.assertEquals(" RTS", code.get(++n));
  }

  @Test
  public void testFunctionOneTwoParameters() {
    Options options = new Options();
// options.setSmallAddSubHeapPtr(false);
    options.setSaveLocalToStack(false);
    options.setVerboseLevel(1);

    Source source = new Source("function name(one, two) begin end").setVerboseLevel(1);
    source.setOptions(options);
    source.addVariable("ONE", Type.BYTE);
    source.addVariable("TWO", Type.WORD);

    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Procedure(source).procedure(symbol, Type.FUNCTION).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    List<String> code = source.getCode();

    int n = -1;
    Assert.assertEquals(";", code.get(++n));
    Assert.assertEquals("; [1]  function name(one, two) begin end", code.get(++n));
    Assert.assertEquals(";", code.get(++n));

    Assert.assertEquals("NAME", code.get(++n));
    Assert.assertEquals("NAME_II", code.get(++n));
    Assert.assertEquals(" LDX ONE", code.get(++n));
    Assert.assertEquals(" LDY #1", code.get(++n));
    Assert.assertEquals(" LDA (@HEAP_PTR),Y", code.get(++n));
    Assert.assertEquals(" STA ONE", code.get(++n));
    Assert.assertEquals(" TXA", code.get(++n));
    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));

    Assert.assertEquals(" LDX TWO", code.get(++n));
    Assert.assertEquals(" LDY #3", code.get(++n));
    Assert.assertEquals(" LDA (@HEAP_PTR),Y", code.get(++n));
    Assert.assertEquals(" STA TWO", code.get(++n));
    Assert.assertEquals(" TXA", code.get(++n));
    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));
    Assert.assertEquals(" INY", code.get(++n));
    Assert.assertEquals(" LDX TWO+1", code.get(++n));
    Assert.assertEquals(" LDA (@HEAP_PTR),Y", code.get(++n));
    Assert.assertEquals(" STA TWO+1", code.get(++n));
    Assert.assertEquals(" TXA", code.get(++n));
    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));

//    Assert.assertEquals(" ADD_TO_HEAP_PTR 5", code.get(++n));
    Assert.assertEquals(" CLC", code.get(++n));
    Assert.assertEquals(" LDA @HEAP_PTR", code.get(++n));
    Assert.assertEquals(" ADC #5", code.get(++n));
    Assert.assertEquals(" STA @HEAP_PTR", code.get(++n));
    Assert.assertEquals(" BCC *+4", code.get(++n));
    Assert.assertEquals(" INC @HEAP_PTR+1", code.get(++n));

    Assert.assertEquals("; function body", code.get(++n));
    Assert.assertEquals("; function end", code.get(++n));

    Assert.assertEquals("?RETURN1", code.get(++n));
    Assert.assertEquals(" STY @REG+2", code.get(++n));
//    Assert.assertEquals(" SUB_FROM_HEAP_PTR 5", code.get(++n));
    Assert.assertEquals(" SEC", code.get(++n));
    Assert.assertEquals(" LDA @HEAP_PTR", code.get(++n));
    Assert.assertEquals(" SBC #5", code.get(++n));
    Assert.assertEquals(" STA @HEAP_PTR", code.get(++n));
    Assert.assertEquals(" BCS *+4", code.get(++n));
    Assert.assertEquals(" DEC @HEAP_PTR+1", code.get(++n));

    Assert.assertEquals(" LDY #3", code.get(++n));
    Assert.assertEquals(" LDA (@HEAP_PTR),Y", code.get(++n));
    Assert.assertEquals(" STA TWO", code.get(++n));
    Assert.assertEquals(" INY", code.get(++n));
    Assert.assertEquals(" LDA (@HEAP_PTR),Y", code.get(++n));
    Assert.assertEquals(" STA TWO+1", code.get(++n));

    Assert.assertEquals(" LDY #1", code.get(++n));
    Assert.assertEquals(" LDA (@HEAP_PTR),Y", code.get(++n));
    Assert.assertEquals(" STA ONE", code.get(++n));
    Assert.assertEquals(" LDY @REG+2", code.get(++n));

    Assert.assertEquals(" RTS", code.get(++n));
  }

  @Test
  public void testFunctionOneAndLocalParameter() {
    Options options = new Options();
// options.setSmallAddSubHeapPtr(false);
    options.setSaveLocalToStack(false);
    options.setVerboseLevel(1);

    Source source = new Source("function name(one) local b begin end");
    source.setOptions(options);
    source.addVariable("ONE", Type.BYTE);
    source.addVariable("B", Type.WORD);

    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Procedure(source).procedure(symbol, Type.FUNCTION).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    List<String> code = source.getCode();

    int n = -1;
    Assert.assertEquals(";", code.get(++n));
    Assert.assertEquals("; [1]  function name(one) local b begin end", code.get(++n));
    Assert.assertEquals(";", code.get(++n));

    Assert.assertEquals("NAME", code.get(++n));
    Assert.assertEquals("NAME_I", code.get(++n));
    Assert.assertEquals(" LDX ONE", code.get(++n));
    Assert.assertEquals(" LDY #1", code.get(++n));
    Assert.assertEquals(" LDA (@HEAP_PTR),Y", code.get(++n));
    Assert.assertEquals(" STA ONE", code.get(++n));
    Assert.assertEquals(" TXA", code.get(++n));
    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));

//    Assert.assertEquals(" ADD_TO_HEAP_PTR 3", code.get(++n));
    Assert.assertEquals(" CLC", code.get(++n));
    Assert.assertEquals(" LDA @HEAP_PTR", code.get(++n));
    Assert.assertEquals(" ADC #3", code.get(++n));
    Assert.assertEquals(" STA @HEAP_PTR", code.get(++n));
    Assert.assertEquals(" BCC *+4", code.get(++n));
    Assert.assertEquals(" INC @HEAP_PTR+1", code.get(++n));

    Assert.assertEquals(" LDA B", code.get(++n));
    Assert.assertEquals(" LDY #1", code.get(++n));
    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));
    Assert.assertEquals(" INY", code.get(++n));
    Assert.assertEquals(" LDA B+1", code.get(++n));
    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));

//    Assert.assertEquals(" ADD_TO_HEAP_PTR 3", code.get(++n));
    Assert.assertEquals(" CLC", code.get(++n));
    Assert.assertEquals(" LDA @HEAP_PTR", code.get(++n));
    Assert.assertEquals(" ADC #3", code.get(++n));
    Assert.assertEquals(" STA @HEAP_PTR", code.get(++n));
    Assert.assertEquals(" BCC *+4", code.get(++n));
    Assert.assertEquals(" INC @HEAP_PTR+1", code.get(++n));

    Assert.assertEquals("; function body", code.get(++n));
    Assert.assertEquals("; function end", code.get(++n));

    Assert.assertEquals("?RETURN1", code.get(++n));
    Assert.assertEquals(" STY @REG+2", code.get(++n));

//    Assert.assertEquals(" SUB_FROM_HEAP_PTR 3", code.get(++n));
    Assert.assertEquals(" SEC", code.get(++n));
    Assert.assertEquals(" LDA @HEAP_PTR", code.get(++n));
    Assert.assertEquals(" SBC #3", code.get(++n));
    Assert.assertEquals(" STA @HEAP_PTR", code.get(++n));
    Assert.assertEquals(" BCS *+4", code.get(++n));
    Assert.assertEquals(" DEC @HEAP_PTR+1", code.get(++n));

    Assert.assertEquals(" LDY #1", code.get(++n));
    Assert.assertEquals(" LDA (@HEAP_PTR),Y", code.get(++n));
    Assert.assertEquals(" STA B", code.get(++n));
    Assert.assertEquals(" INY", code.get(++n));
    Assert.assertEquals(" LDA (@HEAP_PTR),Y", code.get(++n));
    Assert.assertEquals(" STA B+1", code.get(++n));

//    Assert.assertEquals(" SUB_FROM_HEAP_PTR 3", code.get(++n));
    Assert.assertEquals(" SEC", code.get(++n));
    Assert.assertEquals(" LDA @HEAP_PTR", code.get(++n));
    Assert.assertEquals(" SBC #3", code.get(++n));
    Assert.assertEquals(" STA @HEAP_PTR", code.get(++n));
    Assert.assertEquals(" BCS *+4", code.get(++n));
    Assert.assertEquals(" DEC @HEAP_PTR+1", code.get(++n));

    Assert.assertEquals(" LDY #1", code.get(++n));
    Assert.assertEquals(" LDA (@HEAP_PTR),Y", code.get(++n));
    Assert.assertEquals(" STA ONE", code.get(++n));
    Assert.assertEquals(" LDY @REG+2", code.get(++n));

    Assert.assertEquals(" RTS", code.get(++n));
  }

  @Test
  public void testFunctionOneAndLocalParameterSmallHeapAndSaveLocalOnStack() {
    Options options = new Options();
    options.setSmallAddSubHeapPtr(true);
    options.setSaveLocalToStack(true);
    options.setVerboseLevel(1);
    
    Source source = new Source("function name(one) local b begin end").setVerboseLevel(1);
    source.setOptions(options);
    source.addVariable("ONE", Type.WORD);
    source.addVariable("B", Type.WORD);

    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Procedure(source).procedure(symbol, Type.FUNCTION).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    List<String> code = source.getCode();

    int n = -1;
    Assert.assertEquals(";", code.get(++n));
    Assert.assertEquals("; [1]  function name(one) local b begin end", code.get(++n));
    Assert.assertEquals(";", code.get(++n));

    Assert.assertEquals("NAME", code.get(++n));
    Assert.assertEquals("NAME_I", code.get(++n));
    Assert.assertEquals(" LDA ONE", code.get(++n));
    Assert.assertEquals(" PHA", code.get(++n));
    Assert.assertEquals(" LDY #1", code.get(++n));
    Assert.assertEquals(" LDA (@HEAP_PTR),Y", code.get(++n));
    Assert.assertEquals(" STA ONE", code.get(++n));
    Assert.assertEquals(" LDA ONE+1", code.get(++n));
    Assert.assertEquals(" PHA", code.get(++n));
    Assert.assertEquals(" INY", code.get(++n));
    Assert.assertEquals(" LDA (@HEAP_PTR),Y", code.get(++n));
    Assert.assertEquals(" STA ONE+1", code.get(++n));
//    Assert.assertEquals(" TXA", code.get(++n));
//    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));

//    Assert.assertEquals(" ADD_TO_HEAP_PTR 3", code.get(++n));
    Assert.assertEquals(" CLC", code.get(++n));
    Assert.assertEquals(" LDA @HEAP_PTR", code.get(++n));
    Assert.assertEquals(" ADC #3", code.get(++n));
    Assert.assertEquals(" STA @HEAP_PTR", code.get(++n));
//    Assert.assertEquals(" BCC *+4", code.get(++n));
//    Assert.assertEquals(" INC @HEAP_PTR+1", code.get(++n));

//    Assert.assertEquals(" LDY #1", code.get(++n));
//    Assert.assertEquals(" LDA B", code.get(++n));
//    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));
//    Assert.assertEquals(" INY", code.get(++n));
//    Assert.assertEquals(" LDA B+1", code.get(++n));
//    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));
    Assert.assertEquals(" LDA B", code.get(++n));
    Assert.assertEquals(" PHA", code.get(++n));
    Assert.assertEquals(" LDA B+1", code.get(++n));
    Assert.assertEquals(" PHA", code.get(++n));

//    Assert.assertEquals(" ADD_TO_HEAP_PTR 3", code.get(++n));
//    Assert.assertEquals(" CLC", code.get(++n));
//    Assert.assertEquals(" LDA @HEAP_PTR", code.get(++n));
//    Assert.assertEquals(" ADC #3", code.get(++n));
//    Assert.assertEquals(" STA @HEAP_PTR", code.get(++n));
//    Assert.assertEquals(" BCC *+4", code.get(++n));
//    Assert.assertEquals(" INC @HEAP_PTR+1", code.get(++n));

    Assert.assertEquals("; function body", code.get(++n));
    Assert.assertEquals("; function end", code.get(++n));

    Assert.assertEquals("?RETURN1", code.get(++n));
//    Assert.assertEquals(" STY @REG+2", code.get(++n));

//    Assert.assertEquals(" SUB_FROM_HEAP_PTR 3", code.get(++n));
//    Assert.assertEquals(" SEC", code.get(++n));
//    Assert.assertEquals(" LDA @HEAP_PTR", code.get(++n));
//    Assert.assertEquals(" SBC #3", code.get(++n));
//    Assert.assertEquals(" STA @HEAP_PTR", code.get(++n));
//    Assert.assertEquals(" BCS *+4", code.get(++n));
//    Assert.assertEquals(" DEC @HEAP_PTR+1", code.get(++n));

//    Assert.assertEquals(" LDY #1", code.get(++n));
//    Assert.assertEquals(" LDA (@HEAP_PTR),Y", code.get(++n));
//    Assert.assertEquals(" STA B", code.get(++n));
//    Assert.assertEquals(" INY", code.get(++n));
//    Assert.assertEquals(" LDA (@HEAP_PTR),Y", code.get(++n));
//    Assert.assertEquals(" STA B+1", code.get(++n));
    Assert.assertEquals(" PLA", code.get(++n));
    Assert.assertEquals(" STA B+1", code.get(++n));
    Assert.assertEquals(" PLA", code.get(++n));
    Assert.assertEquals(" STA B", code.get(++n));

//    Assert.assertEquals(" SUB_FROM_HEAP_PTR 3", code.get(++n));
//    Assert.assertEquals(" STY @REG+2", code.get(++n));
    Assert.assertEquals(" SEC", code.get(++n));
    Assert.assertEquals(" LDA @HEAP_PTR", code.get(++n));
    Assert.assertEquals(" SBC #3", code.get(++n));
    Assert.assertEquals(" STA @HEAP_PTR", code.get(++n));
//    Assert.assertEquals(" BCS *+4", code.get(++n));
//    Assert.assertEquals(" DEC @HEAP_PTR+1", code.get(++n));

//    Assert.assertEquals(" LDY #1", code.get(++n));
//    Assert.assertEquals(" LDA (@HEAP_PTR),Y", code.get(++n));
    Assert.assertEquals(" PLA", code.get(++n));
    Assert.assertEquals(" STA ONE+1", code.get(++n));
    Assert.assertEquals(" PLA", code.get(++n));
    Assert.assertEquals(" STA ONE", code.get(++n));
//    Assert.assertEquals(" LDY @REG+2", code.get(++n));

    Assert.assertEquals(" RTS", code.get(++n));
  }


}
