package lla.privat.atarixl.compiler.linker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generates a list of includes
 * @author develop
 *
 */
public class IncludeGenerator {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(IncludeGenerator.class);
  
  private List<String> includes;
  private List<String> includeFilenames;
  private List<String> functionsToSearchFor;

  private Map<String, String> functionInFilename;
  
  public IncludeGenerator(List<String> includes, List<String> functions) {
    this.includes = includes;
    this.functionsToSearchFor = functions;
    functionInFilename = new HashMap<>();
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
  
  public IncludeGenerator collectIncludeFiles() {

    includeFilenames = new ArrayList<>();
    
    for (String pathName : includes) {
      File directory = new File(pathName);
      File[] files = directory.listFiles(includeFilefilter);
      for (File file : files) {
        includeFilenames.add(file.getAbsolutePath());
      }
    }
    
    return this;
  }

  public List<String> build() {
    List<String> includeList = new ArrayList<>();
    if (includes == null) {
      return includeList;
    }

    collectIncludeFiles();
    collectAllFunctionsOutOfFiles();
    
    Set<String> includes = new HashSet<>();
    
    for (String function: functionsToSearchFor) {
      if (functionInFilename.containsKey(function)) {
        includes.add(functionInFilename.get(function));
      }
    }
    return new ArrayList<String>(includes);
  }

  private void collectAllFunctionsOutOfFiles() {
    for (String includeFilename: includeFilenames) {
      File file = new File(includeFilename);
      try {
        List<String> lines = readLines(file);
      
        for (String line : lines) {
          String filename = includeFilename;
          if (line.startsWith("@")) {
            int remark = line.indexOf(";");
            if (remark != -1) {
              line = line.substring(0, remark);  
            }
            int equates = line.indexOf("=");
            if (equates != -1) {
              continue;
            }
            String functionName = line.trim();
            if (functionInFilename.containsKey(functionName)) {
              String alreadyExistInFile = functionInFilename.get(functionName);
              LOGGER.error("Functionname {} in file {} already exists in {}", functionName, filename, alreadyExistInFile);
              functionName = functionName + " // AMBIGUOUS";
              filename = filename + " // AMBIGUOUS";
            }
            functionInFilename.put(line, filename);
          }
        }
      } catch (IOException e) {
        LOGGER.error("Caught IOException", e);
      }
    }
  }
  
//  private boolean fileContainsFunction(File file, List<String> functions) {
//    try {
//      List<String> lines = readLines(file);
//      for (String function : functions) {
//        for (String line : lines) {
//          String value = line;
//          if (value.startsWith(function) && function.startsWith(value)) {
//            return true;
//          }
//        }
//      }
//    }
//    catch (IOException e) {
//      // LOGGER.error("can't read file: {}", file.getAbsoluteFile());
//    }
//    return false;
//  }

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

}
