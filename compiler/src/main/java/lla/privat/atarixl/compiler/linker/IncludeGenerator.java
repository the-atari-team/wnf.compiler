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
  private List<String> currentIncludes; // already given by source
  private List<String> functionsToSearchFor;

  private Map<String, String> functionInFilename;
  
  public IncludeGenerator(List<String> includes, List<String> currentIncludes, List<String> functions) {
    this.includes = includes;
    this.functionsToSearchFor = functions;
    this.currentIncludes = currentIncludes;
    functionInFilename = new HashMap<>();
  }
  

  private FileFilter includeFilefilter = new FileFilter() {
    // Override accept method
    public boolean accept(File file) {

      // if the file extension is .log return true, else false
      if (file.getName().toUpperCase().endsWith(".INC")) {
        return true;
      }
      return false;
    }
  };

  /**
   * run through all given paths and collect the *.INC files found there
   */
  public List<String> collectIncludeFiles() {

    List<String> includeFilenames = new ArrayList<>();
    
    for (String pathName : includes) {
      File directory = new File(pathName);
      File[] files = directory.listFiles(includeFilefilter);
      for (File file : files) {
        includeFilenames.add(file.getAbsolutePath());
      }
    }
    return includeFilenames;
  }

  public List<String> getIncludeFilenames() {
    return collectIncludeFiles();
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

  public Map<String, String> collectAllFunctionsFromOneInclude(String includeFilename) {
    Map<String, String> functionsInOneInclude = new HashMap<>();

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
          String functionName = line.trim().toUpperCase();
//          // TODO: hier etwas generisches bauen
//          if (functionName.endsWith("_I") ||
//              functionName.endsWith("_II") ||
//              functionName.endsWith("_III") ||
//              functionName.endsWith("_IIII") ||
//              functionName.endsWith("_IIIII") ||
//              functionName.endsWith("_IIIIII") ||
//              functionName.endsWith("_IIIIIII") ||
//              functionName.endsWith("_IIIIIIII") ||
//              functionName.endsWith("_IIIIIIIII") ||
//              functionName.endsWith("_IIIIIIIIII") ||
//              functionName.endsWith("_IIIIIIIIIII") ||
//              functionName.endsWith("_IIIIIIIIIIII")
//              )
//          {
//            continue;
//          }
          functionsInOneInclude.put(functionName, filename);
        }
      }
    } catch (IOException e) {
      LOGGER.error("Caught IOException", e);
    }
    return functionsInOneInclude;
  }
  
  private Map<String, String> collectAllFunctionsOutOfFiles(List<String> includeFilenames) {
    Map<String, String> functionsInAllIncludes = new HashMap<>();

    for (String includeFilename: includeFilenames) {
      Map<String, String> functionsInOneInclude = collectAllFunctionsFromOneInclude(includeFilename);

      for (Map.Entry<String, String> entry: functionsInOneInclude.entrySet()) {
        String functionName = entry.getKey();
        String value = entry.getValue();
        
        if (functionsInAllIncludes.containsKey(functionName)) {
          String alreadyExistInFile = functionsInAllIncludes.get(functionName);
          functionsInAllIncludes.put(functionName, alreadyExistInFile + "," + value);
        }
        else {
          functionsInAllIncludes.put(functionName, value);
        }
      }
    }
    return functionsInAllIncludes;
  }

  public Map<String, String> collectAllFunctionsOutOfFiles() {
    List<String> includeFilenames = collectIncludeFiles();
    return collectAllFunctionsOutOfFiles(includeFilenames);
  }
  
  public Map<String, String> collectAllFunctionsLeaveOutAlreadyIncluded() {
    Map<String, String> functionsInAllIncludes = collectAllFunctionsOutOfFiles();
    Map<String, String> functionsAlreadyIncluded = collectAllFunctionsOutOfFiles(currentIncludes);
   
    for (Map.Entry<String, String> entry: functionsAlreadyIncluded.entrySet()) {
      String functionName = entry.getKey();
      if (functionsInAllIncludes.containsKey(functionName)) {
        functionsInAllIncludes.remove(functionName);
//        LOGGER.info("Function {} removed", functionName);
      }
      else {
//        LOGGER.info("Function {}", functionName);        
      }
    }
    return functionsInAllIncludes;
  }

  public Map<String, String> getAllUnknownFunctionsButIncludeExistsForIt() {
    Map<String, String> allFunctionsWithoutAlreadyIncluded = collectAllFunctionsLeaveOutAlreadyIncluded();
    Map<String, String> allFunctionsWeSearchFor = new HashMap<>();
    
    for (String functionToSearchFor : functionsToSearchFor) {
      String upperCaseFunctionToSearchFor = functionToSearchFor.toUpperCase();
      if (allFunctionsWithoutAlreadyIncluded.containsKey(upperCaseFunctionToSearchFor)) {
        String path = allFunctionsWithoutAlreadyIncluded.get(upperCaseFunctionToSearchFor);
        allFunctionsWeSearchFor.put(upperCaseFunctionToSearchFor, path);
      }
    }
    return allFunctionsWeSearchFor;
  }

  public void showAllUnknownFunctionsButIncludeExistsForIt() {
    Map<String, String> allFunctionsWithoutAlreadyIncluded = getAllUnknownFunctionsButIncludeExistsForIt();
    List<String> alreadyShows = new ArrayList<>();
    
    for (String functionToSearchFor : functionsToSearchFor) {
      if (allFunctionsWithoutAlreadyIncluded.containsKey(functionToSearchFor)) {
        String path = allFunctionsWithoutAlreadyIncluded.get(functionToSearchFor);
        if (alreadyShows.contains(path)) {
          
        }
        else {
          if (path.contains(";")) {
            LOGGER.warn("found function {} in different files {}", functionToSearchFor, path);
          }
          else {
            File shortPath = new File(path);
            LOGGER.warn("add include '{}' // for {}", shortPath.getName(), functionToSearchFor);
            alreadyShows.add(path);
          }
        }
      }
    }
  }
  
  public List<String> getAllUnknownFunctions() {
//    Map<String, String> allFunctionsWithoutAlreadyIncluded = getAllUnknownFunctionsButIncludeExistsForIt();
    Map<String, String> allFunctionsOnlyFromInclude = collectAllFunctionsOutOfFiles(currentIncludes);
    List<String> unknownFunctions = new ArrayList<>();
    
    for (String functionToSearchFor : functionsToSearchFor) {
      if (allFunctionsOnlyFromInclude.containsKey(functionToSearchFor)) {
      }
      else
      {
        unknownFunctions.add(functionToSearchFor);
      }
    }
    return unknownFunctions;
  }

  public void showAllUnknownFunctions() {
    List<String> allUnknownFunctions = getAllUnknownFunctions();

    for (String unknown: allUnknownFunctions) {
      LOGGER.warn("function {}() not found in any known include", unknown);      
    }
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

}
