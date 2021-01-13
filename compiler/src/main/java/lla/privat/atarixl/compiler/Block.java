// cdw by 'The Atari Team' 2020
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lla.privat.atarixl.compiler.expression.Type;
import lla.privat.atarixl.compiler.linker.FileHelper;
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

  public Block start() {
    Symbol currentSymbol = source.nextElement();

    Symbol nextSymbol = new Program(source).program(currentSymbol).build();

    code(";Compiled with WiNiFe Compiler");
    code(";cdw by 'The Atari Team' 1990-2020");
    code(";LLA: make it work again");

    if (!source.isProgram()) {
      code(" .local");
    }

    code(" .OPT LIST");
    code(" .TITLE " + StringHelper.makeDoubleQuotedString(source.getProgramOrIncludeName()));

    if (source.isProgram()) {
      code(" .INCLUDE " + StringHelper.makeDoubleQuotedString(fileHelper.findInPaths("VARIABLE.INC")));
      code(" .INCLUDE " + StringHelper.makeDoubleQuotedString(fileHelper.findInPaths("HARDWARE.INC")));
      code(" .INCLUDE " + StringHelper.makeDoubleQuotedString(fileHelper.findInPaths("MACROS.INC")));

      code(" *=" + source.getLomem());

      code(" JMP @MAIN");
      code(" JMP @BASIC_MAIN");
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
      code(" JSR @HEAP_INIT");

      nextSymbol = new Statement(source).statement(nextSymbol).build();

      code(" JMP @EXIT");
      source.generateVariables();

      includes(nextSymbol);
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
      if (mnemonic.equals("BYTE") || mnemonic.equals("WORD") || mnemonic.equals("STRING")) {
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

  // TODO: könnte man automatisieren, dann werden unnoetige includes
  // weggelassen/automatisch hinzugefügt (Linker)
  private void includes(Symbol symbol) {
    boolean isIncludes = true;
    int count = 0;
    while (isIncludes) {
      String mnemonic = symbol.get();
      if (mnemonic.equals("INCLUDE")) {
        Symbol filename = source.nextElement(); // filename
        final String filenameWithoutQuotes = StringHelper.removeQuotes(filename.get());
        final String absFilename = fileHelper.findInPaths(filenameWithoutQuotes);
        code(" .INCLUDE " + StringHelper.makeDoubleQuotedString(absFilename));
        ++count;
        symbol = source.nextElement();
      }
      else {
        isIncludes = false;
      }
    }
    if (count == 0) {
       List<String> includes = createIncludeList(source.getIncludePaths(), getFunctions());
       for (String include : includes) {
         code(" .INCLUDE " + StringHelper.makeDoubleQuotedString(include));
       }
    }
    // runtime hier anhaengen!
    code(" .INCLUDE " + StringHelper.makeDoubleQuotedString(fileHelper.findInPaths("RUNTIME.INC")));
  }

  /**
   * getFunctions return a list of all function start with '@'
   * @return
   */
  private List<String> getFunctions() {
    List<String> allFunctions = new ArrayList<>();
    for (String name :source.getAllVariables()) {
      if (name.startsWith("@")) {
        VariableDefinition definition = source.getVariable(name);
        if (definition.getType().equals(Type.FUNCTION)) {
          allFunctions.add(name);
        }
      }
    }
    return allFunctions;
  }

  private FileFilter includeFilefilter = new FileFilter() {
    // Override accept method
    public boolean accept(File file) {

      // if the file extension is .log return true, else false
      if (file.getName().endsWith(".INC")) {
        return true;
      }
      return false;
    }
  };

  public List<String> createIncludeList(List<String> includes, List<String> functions) {
    List<String> includeList = new ArrayList<>();
    if (includes == null) {
      return includeList;
    }

    for (String pathName : includes) {
      File directory = new File(pathName);
      File[] files = directory.listFiles(includeFilefilter);
      for (File file : files) {
        if (fileContainsFunction(file, functions)) {
          includeList.add(file.getAbsolutePath());
        }
      }
    }
    return includeList;
  }

  private boolean fileContainsFunction(File file, List<String> functions) {
    try {
      List<String> lines = readLines(file);
      for (String function : functions) {
        for (String line : lines) {
          String value = line;
          if (value.startsWith(function) && function.startsWith(value)) {
            return true;
          }
        }
      }
    }
    catch (IOException e) {
      // LOGGER.error("can't read file: {}", file.getAbsoluteFile());
    }
    return false;
  }

  private List<String> readLines(File file) throws IOException {
    List<String> lines = new ArrayList<>();
    FileInputStream fstream = new FileInputStream(file);
    BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

    String strLine;

    while ((strLine = br.readLine()) != null) {
      lines.add(strLine);
    }

    fstream.close();
    return lines;
  }

  public void code(final String sourcecodeline) {
    LOGGER.debug(sourcecodeline);
    codeGen(sourcecodeline);
  }

  public Symbol build() {
    return nextSymbol;
  }

}
