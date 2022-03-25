// cdw by 'The Atari Team' 2022
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lla.privat.atarixl.compiler.expression.Type;
import lla.privat.atarixl.compiler.linker.FileHelper;
import lla.privat.atarixl.compiler.linker.Includes;
import lla.privat.atarixl.compiler.source.Code;
import lla.privat.atarixl.compiler.source.Source;
import lla.privat.atarixl.compiler.source.StringHelper;
import lla.privat.atarixl.compiler.statement.Statement;

public class Block extends Code {

  private static final Logger LOGGER = LoggerFactory.getLogger(Block.class);

  private final Source source;

  private Symbol nextSymbol;

  private final FileHelper fileHelper;

  public Block(Source source) {
    super(source);
    this.source = source;
    fileHelper = new FileHelper(this.source.getIncludePaths());
  }

  public Block start(Header header) {
    Symbol currentSymbol = source.nextElement();

    nextSymbol = new Program(source).program(currentSymbol).build();

    code(";Compiled with WiNiFe Compiler");
    code(";cdw by 'The Atari Team' 1990-2021");
    code(";LLA: make it work again");

    if (!source.isProgram()) {
      code(" .local");
    }
  
//    code(" .OPT LIST");
//    code(" .TITLE " + StringHelper.makeDoubleQuotedString(source.getProgramOrIncludeName()));
    if (source.isProgram()) {
      code(" .INCLUDE " + StringHelper.makeDoubleQuotedString(fileHelper.findInPaths("VARIABLE.INC")));
      code(" .INCLUDE " + StringHelper.makeDoubleQuotedString(fileHelper.findInPaths("HARDWARE.INC")));
      code(" .INCLUDE " + StringHelper.makeDoubleQuotedString(fileHelper.findInPaths("MACROS.INC")));

      if (source.isRunAd()) {
        code(" .bank");
        code(" .set 6,0");
      }
      code(" *=" + source.getLomem());

      code(" JMP @MAIN");
      code(" JMP @BASIC_MAIN");
    }

    if (header != null) {
      code(";Header Variables");

      for (String name :header.getVariableList()) {
        VariableDefinition definition = header.getVariable(name);
        source.addVariableResetPrefix(definition);
      }
      source.generateAllNotAlreadyGeneratedEquatesVariables();
    }

    nextSymbol = procedures(nextSymbol);

    if (source.isProgram()) {
      LOGGER.debug("Hauptroutine");
      code("; ----<<< Hauptroutine >>>----");
      code("@MAIN");
      code(" LDY #0"); // Wichtig, wird auf den Heap-Pointer addiert (vom Basic fuer Parameter
                       // verwendet)
      code("@MAIN_FROM_BASIC");
      code(" TSX"); // Speichere den aktuellen Stackpointer, hinter evtl. Basic!
      code(" JSR @INIT_RUNTIME");

      nextSymbol = new Statement(source).statement(nextSymbol).build();

      code(" JMP @EXIT");
      source.generateVariables();

      nextSymbol = new Includes(source, fileHelper).readAllIncludes(nextSymbol).build();
      
      if (source.isRunAd()) {
        code(" .bank");
        code(" .set 6,0");
        code(" *=736");
        code(" .word " + source.getLomem());
      }
    }
    else {
      source.generateVariables();
    }
    return this;
  }

  private Symbol procedures(final Symbol symbol) {
    boolean couldBeProcedureOrVariableInit = true;
    Symbol nextSymbol = symbol;
    while (couldBeProcedureOrVariableInit) {
      String mnemonic = nextSymbol.get();
      if (mnemonic.equals("BYTE") ||
          mnemonic.equals("CONST") ||
          mnemonic.equals("UINT8") ||
          mnemonic.equals("INT8") ||
          mnemonic.equals("WORD") ||
          mnemonic.equals("UINT16") ||
          mnemonic.equals("INT16") ||
          mnemonic.equals("STRING")) {
        nextSymbol = new Variable(source).variable(nextSymbol).build();
      }
      else if (mnemonic.equals("PROCEDURE")) {
        nextSymbol = new Procedure(source).procedure(nextSymbol, Type.PROCEDURE).build();
      }
      else if (mnemonic.equals("FUNCTION")) {
        nextSymbol = new Procedure(source).procedure(nextSymbol, Type.FUNCTION).build();
      }
      else {
        couldBeProcedureOrVariableInit = false;
      }
    }
    return nextSymbol;
  }

  public int code(final String sourcecodeline) {
    LOGGER.debug(sourcecodeline);
    return codeGen(sourcecodeline);
  }

  public Symbol build() {
    if (source.getFilename().startsWith("test") && source.getCountOfAsserts() == 0) {
      // source.error(Symbol.noSymbol(), "Tests should contain at least an assert()");
      source.warn("Tests should contain at least one assert() function call!");
    }
    return nextSymbol;
  }

}
