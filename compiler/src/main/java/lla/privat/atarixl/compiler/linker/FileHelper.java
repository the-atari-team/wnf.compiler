// cdw by 'The Atari Team' 2021
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler.linker;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileHelper {
  private static final Logger LOGGER = LoggerFactory.getLogger(FileHelper.class);
  private final List<String> includePaths;

  public FileHelper(final List<String> includePaths) {
    this.includePaths = includePaths;
  }

  /**
   * findInPaths runs through all known include paths and try to find the given name
   * return absolutePath as String
   * @param filename
   * @return absolutePath as String or empty String
   */
  public String findInPaths(String filename) {
    for(String path: includePaths) {
      File searchFile = new File(path, filename);
      if (searchFile.exists()) {
        return searchFile.getAbsolutePath();
      }
    }
    LOGGER.warn("File {} not found in given paths.", filename);
    return filename;
  }
}
