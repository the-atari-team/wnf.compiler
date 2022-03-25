// cdw by 'The Atari Team' 2022
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler.source;

public abstract class Code {

  private final Source source;

  public Code(final Source source) {
    this.source = source;
  }

  protected abstract int code(final String codeline);

  /**
   * insert Code into Source-List
   * @param codeline
   * @return position of inserted code
   */
  protected int codeGen(String codeline) {
    String mayBeUpperCode = codeline;

    if (!(isCodeLineARemark(codeline) || containsCodeLineAnInclude(codeline) || containsCodeLineData(codeline))) {
      // make everything uppercase which is not a codeline or
      // contains a '.INCLUDE'
      mayBeUpperCode = mayBeUpperCode.toUpperCase();
    }

    // verbose == 0 do not show/insert comment lines (first 3 lines are always)
    if (mayBeUpperCode.startsWith(";#")) {
      final int level = source.getVerboseLevel();

      if (mayBeUpperCode.startsWith(";#3")) {
        if (level < 3) {
          return 0;
        }
        else {
          source.code(";" + mayBeUpperCode.substring(3));
          return 0;
        }
      }
      else if (mayBeUpperCode.startsWith(";#2")) {
        if (level < 2) {
          return 0;
        }
        else {
          source.code(";" + mayBeUpperCode.substring(3));
          return 0;
        }
      }
      else if (mayBeUpperCode.startsWith(";#1")) {
        if (level < 1 ) {
          return 0;
        }
        else {
          source.code(";" + mayBeUpperCode.substring(3));
          return 0;
        }
      }

      if (level == 0 && source.getCode().size() > 3) {
        return 0;
      }
    }

    return source.code(mayBeUpperCode);
  }

  private boolean containsCodeLineData(String codeline) {
    return codeline.startsWith(" .BYTE") || codeline.startsWith(" .WORD");
  }

  private boolean containsCodeLineAnInclude(String codeline) {
    return codeline.contains(".INCLUDE ");
  }

  private boolean isCodeLineARemark(String codeline) {
    return codeline.startsWith(";");
  }
}
