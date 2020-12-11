// cdw by 'The Atari Team' 2020
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lla.privat.atarixl.compiler.optimization.PeepholeOptimizer;
import lla.privat.atarixl.compiler.source.Source;

/**
 * Atari 8bit WNF-Compiler 1990 - 2020
 *
 * @author lars <dot> langhans <at> gmx <dot> de
 *
 *         Yes! The first ideas are over 30 years old!
 *
 *         de_DE: WNF für "WiNiFe"-Projekt oder "Wird nie fertig" (ak. Baumann und Clausen Comedy)
 *         en_EN: WNF stays for "WiNeFi" project or "Will never finished"
 *
 */
public class Main {

  private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

  private final int verboseLevel;

  private String filename;

  private String filepath;

  private Source source;

  private final int optimisationLevel;
  private int countOfUsedOptimisations;

  private String outputPath;

  private List<String> includePaths;

  protected Main() {
    optimisationLevel = 0;
    verboseLevel = 0;
    outputPath = null;
  }

  public Main(Source source) {
    this.source = source;
    optimisationLevel = 0;
    verboseLevel = 0;
    outputPath = null;
  }

  public Main(String filename) throws IOException {
    this(filename, 0, 0);
  }

  public Main(String filename, int optimize, int verboseLevel) throws IOException {
    this(filename, optimize, verboseLevel, null);
  }

  public Main(String filename, int optimize, int verboseLevel, String outputPath) throws IOException {
    this.filename = filename;
    this.optimisationLevel = optimize;
    this.verboseLevel = verboseLevel;
    this.outputPath = outputPath;
  }

  public Main setOutputPath(String outputPath) {
    this.outputPath = outputPath;
    return this;
  }

  public static void usage() {
    LOGGER.info("Usage:");
    LOGGER.info(" java -jar wnf-compiler.jar [OPTIONS] [FILE]");
    LOGGER.info("Compiler for a programming language similar to Algol.");
    LOGGER.info("Generates mostly mac/65 kompatible 6502-assembler file.");
    LOGGER.info("");
    LOGGER.info(" -O level | --optimize level - 0, 1 or 2 possible (not ready yet!)");
    LOGGER.info(" -v level | --verbose level  - be more verbose");
    LOGGER.info(" -I path                     - include path to search for includes, can given more than once.");
    LOGGER.info(" -o path                     - set output path, where the ASM file is stored.");
    LOGGER.info(" -h | --help                 - display this help and exit.");
  }

  public static void main(final String[] args) throws IOException {
    if (args.length < 1) {
      LOGGER.error("No parameter given");
      usage();
      System.exit(1);
    }

    int optimisationLevel = 0;
    int verboseLevel = 0;
    String outputpath = "";
    List<String> includePaths = new ArrayList<>();

    final String currentWorkingDirectory = System.getProperty("user.dir");
    includePaths.add(currentWorkingDirectory);
    String filename = "";

    int index = 0;

    while (args.length > index) {
      String parameter = args[index];

      if (parameter.equals("-O") || parameter.startsWith("--optim")) {
        optimisationLevel = Integer.valueOf(args[index + 1]);
        ++index;
      }
      else if (parameter.equals("-o")) {
        outputpath = args[index + 1];
        ++index;
      }
      else if (parameter.equals("-I")) {
        String additionalIncludePath = args[index + 1];

        File file = new File(additionalIncludePath);
        String absolutePath = file.getCanonicalPath();
        if (file.isDirectory()) {
          includePaths.add(absolutePath);
        }
        else {
          LOGGER.error("Given path {} does not exist.", args[index + 1]);
        }
        ++index;
      }
      else if (parameter.equals("-v") || parameter.startsWith("--verbose")) {
        verboseLevel = Integer.valueOf(args[index + 1]);
        ++index;
      }
      else if (parameter.equals("-V") || parameter.startsWith("--version")) {
        LOGGER.info("This is a WNF-Project (Will never finished), but version is 1.0.0");
        System.exit(1);
      }
      else if (parameter.equals("-h") || parameter.startsWith("--help")) {
        usage();
        System.exit(0);
      }
      else {
        filename = parameter;
      }
      ++index;
    }

    try {
      String basename = "";
      if (outputpath.length() == 0) {
        File file = new File(filename);
        basename = new File(file.getAbsolutePath()).getParent();
        outputpath = basename;
      }
      final Main main = new Main(filename, optimisationLevel, verboseLevel, outputpath);
      main.setIncludePath(includePaths);
      if (!basename.isEmpty()) {
        main.addIncludePath(basename);
      }
      main.start();
    } catch (final IllegalArgumentException e) {
      LOGGER.error("ERROR: {}", e.getMessage());
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * readFile read the whole file into memory
   * Due to the fact, atari files are small, this is no problem at all.
   * @return
   * @throws IOException
   */
  Main readFile() throws IOException {

    LOGGER.info("Read file: '{}'", filename);

    File file = new File(this.filename);
    if (!file.exists()) {
      throw new FileNotFoundException("ERROR: Given file does not exist.");
    }

    String wnfProgram = new SourceReader(this.filename).readFile();
    this.source = new Source(wnfProgram);
    this.source.setVerboseLevel(verboseLevel);
    if (includePaths != null) {
      this.source.setIncludePaths(includePaths);
    }
    if (outputPath == null) {
      this.outputPath = this.filename;
    }
    return this;
  }

  /**
   *  small helper function,
   *  which do the whole things in the right direction
   *  readFile()
   *  compile()
   *  optimize()
   *  write()
   *
   * @throws IOException
   */

  private void start() throws IOException {
    readFile();

    compile();

    optimize(optimisationLevel);

    write();
  }

  public Main optimize(int optimisationLevel) {
    PeepholeOptimizer optimizer = new PeepholeOptimizer(source, optimisationLevel);
    optimizer.optimize().build();
    optimizer.showStatus();

    setUsedOptimisations(optimizer.getUsedOptimisations());
    return this;
  }

  public Main compile() {
    if (source == null) {
      throw new IllegalStateException("Source not initialised");
    }

    LOGGER.info("compile");

    // Block

    new Block(source).start().build();

    if (source.hasMoreElements()) {
      error(source, "Expect no more elements.");
    }
    return this;
  }

  public Main show() {
    LOGGER.info("---------- CODE ----------");
    source.show();

    return this;
  }

  public Main write() throws IOException {
    File file = new File(outputPath);

    if (outputPath != null) {
      file.mkdir();
      if (!file.isDirectory()) {
        throw new FileNotFoundException("ERROR: Given output path must be a directory.");
      }
      this.filepath = outputPath;
    }
    else {
      this.filepath = file.getParent();
    }

    String filenameAsm = source.getProgramOrIncludeName() + source.getExtension();
    Path path = Paths.get(filepath, filenameAsm);

    final String message = "write to " + filepath + "/" + filenameAsm;
    LOGGER.info(message);

    Files.write(path, source.getCode(), StandardCharsets.UTF_8);
    return this;
  }

  private void error(Source source, String errorMessage) {
    LOGGER.error("Error at line: {}, Message:{}", source.getLine(), errorMessage);
    throw new IllegalStateException(errorMessage);
  }

  public int getUsedOptimisations() {
    return countOfUsedOptimisations;
  }

  public void setUsedOptimisations(int usedOptimisations) {
    this.countOfUsedOptimisations = usedOptimisations;
  }

  public Main setIncludePath(List<String> includePaths) {
    this.includePaths = includePaths;
    return this;
  }
  public Main addIncludePath(String includePath) {
    this.includePaths.add(includePath);
    return this;
  }
}
