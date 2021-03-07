// cdw by 'The Atari Team' 2020
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler;

public class Symbol {

  SymbolEnum id;
  String value;

  public Symbol(CharSequence value, SymbolEnum id) {
    this.value = value.toString();
    this.id = id;
  }

  public static Symbol noSymbol() {
    return new Symbol("", SymbolEnum.noSymbol);
  }

  public String get() {
    return this.value;
  }

  public SymbolEnum getId() {
    return id;
  }

  public void changeId(SymbolEnum newId) {
    id = newId;
  }

  public void changeValue(String value) {
    this.value = value;
  }
}
