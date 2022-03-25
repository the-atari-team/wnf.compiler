// cdw by 'The Atari Team' 2022
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler.source;

public abstract class WordListGenerator extends ListGenerator {

  public WordListGenerator(Source source) {
    super(source);
  }

  @Override
  public String getType() {
    return ".WORD";
  }

}
