// cdw by 'The Atari Team' 2020
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public class SourceReader {

  private final String filename;

  public SourceReader(String filename) {
    this.filename = filename;
  }

  public String readFile() throws IOException {
    StringBuilder buffer = new StringBuilder();

    Reader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename), StandardCharsets.UTF_8));

    int theCharNum = reader.read();
    while (theCharNum != -1) {
      char theChar = (char) theCharNum;
      buffer.append(theChar);

      theCharNum = reader.read();
    }
    buffer.append(' ');

    reader.close();
    return buffer.toString();
  }

}
