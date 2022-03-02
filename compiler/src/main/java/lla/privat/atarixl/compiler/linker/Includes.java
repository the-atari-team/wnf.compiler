package lla.privat.atarixl.compiler.linker;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lla.privat.atarixl.compiler.Block;
import lla.privat.atarixl.compiler.Symbol;
import lla.privat.atarixl.compiler.SymbolEnum;
import lla.privat.atarixl.compiler.VariableDefinition;
import lla.privat.atarixl.compiler.expression.Type;
import lla.privat.atarixl.compiler.source.Code;
import lla.privat.atarixl.compiler.source.Source;
import lla.privat.atarixl.compiler.source.StringHelper;


public class Includes extends Code {

  private static final Logger LOGGER = LoggerFactory.getLogger(Block.class);

  private final Source source;
  private final FileHelper fileHelper;

  private Symbol nextSymbol;
  
  public Includes(Source source, FileHelper fileHelper) {
    super(source);
    this.source = source;
    this.fileHelper = fileHelper;
  }
  
  // TODO: könnte man automatisieren, dann werden unnoetige includes
  // weggelassen/automatisch hinzugefügt (Linker)
  public Includes readAllIncludes(Symbol symbol) {
    nextSymbol = symbol;
    boolean isIncludes = true;
    int count = 0;
    while (isIncludes) {
      String mnemonic = nextSymbol.get();
      if (mnemonic.equals("INCLUDE")) {
        Symbol filenameSymbol = source.nextElement(); // filename
        if (filenameSymbol.getId() == SymbolEnum.string) {
          final String filenameWithoutQuotes = StringHelper.removeQuotes(filenameSymbol.get());
          final String absFilename = fileHelper.findInPaths(filenameWithoutQuotes);
          code(" .INCLUDE " + StringHelper.makeDoubleQuotedString(absFilename));
          ++count;
          nextSymbol = source.nextElement();
        }
        else {
          source.error(filenameSymbol, "We support only strings after 'INCLUDE' keyword.");
        }
      }
      else {
        isIncludes = false;
      }
    }
    if (count == 0) {
      List<String> includes = new IncludeGenerator(source.getIncludePaths(), getFunctions()).build();

      for (String include : includes) {
         code(" .INCLUDE " + StringHelper.makeDoubleQuotedString(include));
       }
    }
    return this;
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

  @Override
  protected int code(final String sourcecodeline) {
    LOGGER.debug(sourcecodeline);
    return codeGen(sourcecodeline);
  }

  public Symbol build() {
    // runtime hier anhaengen!
    code(" .INCLUDE " + StringHelper.makeDoubleQuotedString(fileHelper.findInPaths("RUNTIME.INC")));
    return nextSymbol;
  }
}
