// cdw by 'The Atari Team' 2020
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler.source;

public abstract class ListGenerator {

  private final Source source;
  
  public ListGenerator(Source source) {
    this.source = source;
  }

  abstract public String getElement(int index);

  abstract public String getType();

  public void generateCode(int sizeOfArray) {
    StringBuilder line = new StringBuilder();
    boolean setType = true;
    int valuesPerLine = 8;
    int valueCount = 0;
    
    for (int i = 0; i < sizeOfArray; i++) {
      if (setType) {
        line.append(' ').append(getType()).append(' ');
        setType = false;
      }
      final String element = getElement(i);
      String quotedElement = StringHelper.maybeString(element);
      line.append(quotedElement);
      if (StringHelper.isSingleQuotedString(element)) {
        valueCount = valuesPerLine - 1;
      }
      else {
        ++valueCount;
      }
      if (valueCount == valuesPerLine) {
        source.code(line.toString());
        valueCount = 0;
        setType = true;
        line = new StringBuilder();
      }
      else {
        if (i < sizeOfArray - 1) {
          line.append(",");
        }
      }
    }
    if (line.length() > 0) {
      source.code(line.toString());
    }
  }
}
