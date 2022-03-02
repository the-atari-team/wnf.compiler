package lla.privat.atarixl.compiler;

import java.util.List;

import lla.privat.atarixl.compiler.source.Source;

/**
 * Header gibt uns die Möglichkeit, eine header.wnf mit Konstanten/Globalen
 * Variablen zu definieren die dann in jeder Source-Datei automatisch mit
 * eingebunden wird. Es wird immer nach header.wnf gesucht, hier koennen alle
 * gewünschten Variablen untergebracht werden, die sind dann überall sichtbar.
 *
 * Testen mit Variablen
 */
public class Header {
  private final Source source;

  public Header(Source source) {
    this.source = source;
  }

  public Header load() {
    // Simple read the whole header file and store variables in source tables 

    if (source.hasMoreElements()) {
      boolean couldBeVariableInit = true;
      Symbol nextSymbol = source.nextElement();
      
      while (couldBeVariableInit) {
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
        else {
          source.error(nextSymbol, "We are in the header file, only variable definitions are allowed here.");
        }
        if (!source.hasMoreElements()) {
          couldBeVariableInit = false;
        }
      }
    }
    return this;
  }

  public List<String> getVariableList() {
    return source.getAllVariables();
  }

  public VariableDefinition getVariable(String name) {
    return source.getVariable(name);
  }

}
