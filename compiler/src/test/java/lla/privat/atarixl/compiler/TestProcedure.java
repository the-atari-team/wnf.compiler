// cdw by 'The Atari Team' 2022
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import lla.privat.atarixl.compiler.expression.Type;
import lla.privat.atarixl.compiler.source.Source;

public class TestProcedure {

  @Test
  public void testProcedure() {
    Source source = new Source("procedure name() begin end").setVerboseLevel(1);
    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Procedure(source).procedure(symbol, Type.PROCEDURE).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    List<String> code = source.getCode();

    int n = -1;
    Assert.assertEquals(";", code.get(++n));
    Assert.assertEquals("; [1]  procedure name() begin end", code.get(++n));
    Assert.assertEquals(";", code.get(++n));

    Assert.assertEquals("NAME", code.get(++n));
    Assert.assertEquals("; NAME", code.get(++n));
    Assert.assertEquals("; procedure body", code.get(++n));
    Assert.assertEquals("; procedure end", code.get(++n));
    Assert.assertEquals("?RETURN1", code.get(++n));
    Assert.assertEquals(" RTS", code.get(++n));
  }

  @Test
  public void testProcedureOneParameter() {
    Options options = new Options();
// options.setSmallAddSubHeapPtr(false);
    options.setSaveLocalToStack(false);
    options.setVerboseLevel(1);

    Source source = new Source("procedure name(one) begin end").setVerboseLevel(1);
    source.setOptions(options);
    source.addVariable("ONE", Type.BYTE);

    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Procedure(source).procedure(symbol, Type.PROCEDURE).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    List<String> code = source.getCode();

    int n = -1;
    Assert.assertEquals(";", code.get(++n));
    Assert.assertEquals("; [1]  procedure name(one) begin end", code.get(++n));
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

    Assert.assertEquals("; procedure body", code.get(++n));
    Assert.assertEquals("; procedure end", code.get(++n));

    Assert.assertEquals("?RETURN1", code.get(++n));
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

    Assert.assertEquals(" RTS", code.get(++n));
  }

  @Test
  public void testProcedureOneTwoParameters() {
    Options options = new Options();
// options.setSmallAddSubHeapPtr(false);
    options.setSaveLocalToStack(false);
    options.setVerboseLevel(1);

    Source source = new Source("procedure name(one, two) begin end").setVerboseLevel(1);
    source.setOptions(options);

    source.addVariable("ONE", Type.BYTE);
    source.addVariable("TWO", Type.WORD);

    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Procedure(source).procedure(symbol, Type.PROCEDURE).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    List<String> code = source.getCode();

    int n = -1;
    Assert.assertEquals(";", code.get(++n));
    Assert.assertEquals("; [1]  procedure name(one, two) begin end", code.get(++n));
    Assert.assertEquals(";", code.get(++n));

    Assert.assertEquals("NAME", code.get(++n));
    // zwei Parameter, somit gibt es auch die Funktion NAME_II (2 I)
    Assert.assertEquals("NAME_II", code.get(++n));

    // Wir holen den Parameter vom Heap, gleichzeitig sichern die Variable ONE auf dem Heap
    // hier ein Byte
    
    Assert.assertEquals(" LDX ONE", code.get(++n));
    Assert.assertEquals(" LDY #1", code.get(++n));
    Assert.assertEquals(" LDA (@HEAP_PTR),Y", code.get(++n));
    Assert.assertEquals(" STA ONE", code.get(++n));
    Assert.assertEquals(" TXA", code.get(++n));
    Assert.assertEquals(" STA (@HEAP_PTR),Y", code.get(++n));

    // Wir holen den Parameter vom Heap, gleichzeitig sichern die Variable ONE auf dem Heap
    // hier zwo Bytes

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

    Assert.assertEquals("; procedure body", code.get(++n));
    Assert.assertEquals("; procedure end", code.get(++n));

    Assert.assertEquals("?RETURN1", code.get(++n));
//    Assert.assertEquals(" SUB_FROM_HEAP_PTR 5", code.get(++n));
    Assert.assertEquals(" SEC", code.get(++n));
    Assert.assertEquals(" LDA @HEAP_PTR", code.get(++n));
    Assert.assertEquals(" SBC #5", code.get(++n));
    Assert.assertEquals(" STA @HEAP_PTR", code.get(++n));
    Assert.assertEquals(" BCS *+4", code.get(++n));
    Assert.assertEquals(" DEC @HEAP_PTR+1", code.get(++n));

    // Wir stellen den 2. Parameter wieder her
    
    Assert.assertEquals(" LDY #3", code.get(++n));
    Assert.assertEquals(" LDA (@HEAP_PTR),Y", code.get(++n));
    Assert.assertEquals(" STA TWO", code.get(++n));
    Assert.assertEquals(" INY", code.get(++n));
    Assert.assertEquals(" LDA (@HEAP_PTR),Y", code.get(++n));
    Assert.assertEquals(" STA TWO+1", code.get(++n));

    // Wir stellen den 1. Parameter wieder her
    
    Assert.assertEquals(" LDY #1", code.get(++n));
    Assert.assertEquals(" LDA (@HEAP_PTR),Y", code.get(++n));
    Assert.assertEquals(" STA ONE", code.get(++n));

    Assert.assertEquals(" RTS", code.get(++n));
  }

  @Test
  public void testProcedureOneAndLocalParameter() {
    Options options = new Options();
// options.setSmallAddSubHeapPtr(false);
    options.setSaveLocalToStack(false);
    options.setVerboseLevel(1);


    Source source = new Source("procedure name(one) local b begin end");
    source.setOptions(options);
    source.addVariable("ONE", Type.BYTE);
    source.addVariable("B", Type.WORD);

    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Procedure(source).procedure(symbol, Type.PROCEDURE).build();

    Assert.assertEquals("", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.noSymbol, nextSymbol.getId());

    List<String> code = source.getCode();

    int n = -1;
    Assert.assertEquals(";", code.get(++n));
    Assert.assertEquals("; [1]  procedure name(one) local b begin end", code.get(++n));
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

    Assert.assertEquals("; procedure body", code.get(++n));
    Assert.assertEquals("; procedure end", code.get(++n));

    Assert.assertEquals("?RETURN1", code.get(++n));
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

    Assert.assertEquals(" RTS", code.get(++n));
  }

  @Test
  public void testProcedureAndProcedure() {
    Source source = new Source("procedure first()\nbegin\n  @second()\nend\nprocedure @second()\nbegin\nend").setVerboseLevel(1);
    Symbol symbol = source.nextElement();

    Symbol nextSymbol = new Procedure(source).procedure(symbol, Type.PROCEDURE).build();

    Assert.assertEquals("PROCEDURE", nextSymbol.get());
    Assert.assertEquals(SymbolEnum.reserved_word, nextSymbol.getId());

    nextSymbol = new Procedure(source).procedure(nextSymbol, Type.PROCEDURE).build();

    List<String> code = source.getCode();

    int n = -1;
    Assert.assertEquals(";", code.get(++n));
    Assert.assertEquals("; [1]  procedure first()", code.get(++n));
    Assert.assertEquals(";", code.get(++n));

    Assert.assertEquals("FIRST", code.get(++n));
    Assert.assertEquals("; FIRST", code.get(++n));
    Assert.assertEquals("; procedure body", code.get(++n));
    Assert.assertEquals(";", code.get(++n));
    Assert.assertEquals("; [3]  @second()", code.get(++n));
    Assert.assertEquals(";", code.get(++n));
    Assert.assertEquals(" JSR @SECOND", code.get(++n));
    
    Assert.assertEquals("; procedure end", code.get(++n));
    Assert.assertEquals("?RETURN1", code.get(++n));
    Assert.assertEquals(" RTS", code.get(++n));

    Assert.assertEquals(";", code.get(++n));
    Assert.assertEquals("; [5]  procedure @second()", code.get(++n));
    Assert.assertEquals(";", code.get(++n));

    Assert.assertEquals("@SECOND", code.get(++n));
    Assert.assertEquals("; @SECOND", code.get(++n));
    Assert.assertEquals("; procedure body", code.get(++n));
    Assert.assertEquals("; procedure end", code.get(++n));
    Assert.assertEquals("?RETURN2", code.get(++n));
    Assert.assertEquals(" RTS", code.get(++n));

  }

}
