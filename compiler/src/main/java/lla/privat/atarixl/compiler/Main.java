// cdw by 'The Atari Team' 2022
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

import lla.privat.atarixl.compiler.optimization.LongJumpOptimizer;
import lla.privat.atarixl.compiler.optimization.PeepholeOptimizer;
import lla.privat.atarixl.compiler.optimization.RegisterOptimizer;
import lla.privat.atarixl.compiler.source.Source;

/**
 * Atari 8bit WNF-Compiler 1990 - 2022
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

  private String filename;

  private String filepath;

  private Source source;

  private Source headerSource;

  private int countOfUsedOptimisations;

  private String outputPath;

  private List<String> includePaths;

  private final Options options;

  protected Main() {
    options = new Options();
    outputPath = null;
  }

  public Main(Source source) {
    this.source = source;
    options = new Options();
    outputPath = null;
  }

  public Main(String filename) {
    this(filename, 0, 0);
  }

  public Main(String filename, int optimize, int verboseLevel) {
//    this(filename, optimize, verboseLevel, null, false, true, true, false, true, false, false, false, false);

    options = new Options();
    options.setOptimisationLevel(optimize);
    options.setVerboseLevel(verboseLevel);
    this.outputPath = null;
    this.filename = filename;
  }

  public Main(String filename, String outputPath, final Options options) {
    this.filename = filename;
    this.outputPath = outputPath;
    this.options = options;
  }

//  public Main(String filename, int optimize, int verboseLevel, String outputPath,
//      boolean selfModifiedCode, boolean starChainMult, boolean shiftMultDiv,
//      boolean smallAddSubHeapPtr, boolean importHeader, boolean showVariableUsage,
//      boolean showVariableUnused, boolean showPeepholeOptimize,
//      ) {
//    this.filename = filename;
//    this.options = new Options(optimize, verboseLevel, selfModifiedCode, starChainMult, shiftMultDiv, smallAddSubHeapPtr, importHeader, showVariableUsage, showVariableUnused, showPeepholeOptimize, errorIfPrecalculatable);
//    this.outputPath = outputPath;
//  }

  public Main setOptions(final Options options) {
    this.options.setOptimisationLevel(options.getOptimisationLevel());
    this.options.setVerboseLevel(options.getVerboseLevel());
    this.options.setPrecalculate(options.getPrecalculate());
    // TODO: fill with all options
    source.setOptions(this.options);
    return this;
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
    LOGGER.info(" -O level | --optimize level - 0, 1,2 or 3 possible (not ready yet!)");
    LOGGER.info(" -v level | --verbose level  - be more verbose");
    LOGGER.info(" -I path               - include given path to the list for search to includes,");
    LOGGER.info("                         can given more than once.");
    LOGGER.info(" -o path               - set output path, where the ASM file is stored.");
    LOGGER.info(" -smc                  - if given, allow self modified code (smc).");
    LOGGER.info("                         THIS IS VERY EXPERIMENTAL! DO NOT USE!");
    LOGGER.info(" -noscm                - if given, hard coded value multiplications in");
    LOGGER.info("                         expressions will not convert to shift/(add|sub)");
    LOGGER.info("                         but call the external @IMULT function.");
    LOGGER.info(" -noshift              - if given, mult/div with 2-complement will not use.");
    LOGGER.info("                         Expressions call the @IMULT/@IDIV function instead.");
    LOGGER.info(" -smallHeapPtr         - if given, Heap Ptr is only 256 bytes big, be careful!");
    LOGGER.info(" -noHeader             - if given, header.wnf will not import if file exists!");
    LOGGER.info(" -showVariableUsage    - if given, show how often a variable is used.");
    LOGGER.info(" -showVariableUnused   - if given, show a hint if a variable is unused.");
    LOGGER.info(" -showPeepholeOptimize - if given, show which peephole optimize will applied.");
    LOGGER.info(" -testincludes         - if given, test given includes");
    LOGGER.info(" -boundscheck or -bc   - if given, bounds check will be added in write to array");
    LOGGER.info(" -noSafeLocalToStack   - if given, local variables will store on heap");
    LOGGER.info("                         6502 Stack holds only 256 bytes and need 2 bytes per function call.");
    LOGGER.info("                         Now it stores also local variables on stack.");
    LOGGER.info("                         If stack underrun occur, use this parameter.");
    LOGGER.info("                         Most the time the stack should be big enough.");    
    LOGGER.info("");
    LOGGER.info(" -h | --help           - display this help and exit.");
  }


  public static void main(final String[] args) throws IOException {
    if (args.length < 1) {
      LOGGER.error("No parameter given");
      usage();
      System.exit(1);
    }

    int optimisationLevel = 0;
    int verboseLevel = 0;
    boolean selfModifiedCode = false;
    boolean starChainMult = true;
    boolean shiftMultDiv = true;
    boolean smallAddSubHeapPtr = false;
    boolean importHeader = true;
    boolean showVariableUsage = false;
    boolean showVariableUnused = false;
    boolean showPeepholeOptimize = false;
    boolean testInclude = false;
    boolean boundsCheck = false;
    boolean saveLocalToStack = true;
    

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
      else if (parameter.equalsIgnoreCase("-smc")) {
        LOGGER.warn("SMC Parameter is given, use self modified code. Expect the unexpected!");
        selfModifiedCode = true;
      }
      else if (parameter.equalsIgnoreCase("-noscm")) {
        LOGGER.warn("'no Star Chain Mult' Parameter is given, will not use shift/add for multiply!");
        starChainMult = false;
      }
      else if (parameter.equalsIgnoreCase("-noshift")) {
        LOGGER.warn("'no Shift' Parameter is given, will not use shift for mult/div 2-complement!");
        shiftMultDiv = false;
      }
      else if (parameter.equalsIgnoreCase("-smallHeapPtr")) {
        LOGGER.warn("Parameter for small Heap Pointer is given, we use only byte wide size heap!");
        LOGGER.warn("Make sure your Heap_Ptr starts at equal word address! See RUNTIME.INC");
        smallAddSubHeapPtr = true;
      }
      else if (parameter.equalsIgnoreCase("-noheader")) {
        LOGGER.warn("Parameter to ignore header file is given, do not import header.wnf if exists!");
        importHeader = false;
      }
      else if (parameter.equalsIgnoreCase("-showVariableUsage")) {
        LOGGER.info("Parameter for variable usage is given, see how often each variable is used.");
        showVariableUsage = true;
      }
      else if (parameter.equalsIgnoreCase("-showVariableUnused")) {
        LOGGER.info("Parameter for variable unused given, see if variable is not used in code.");
        showVariableUnused = true;
      }
      else if (parameter.equalsIgnoreCase("-showPeepholeOptimize")) {
        LOGGER.info("Parameter for peephole optimize is given, show which optimization is applied.");
        showPeepholeOptimize = true;
      }
      else if (parameter.equalsIgnoreCase("-testincludes")) {
        LOGGER.info("Parameter for test includes is given, we check all includes.");
        testInclude = true;
      }
      else if (parameter.equalsIgnoreCase("-boundscheck") || parameter.equalsIgnoreCase("-bc")) {
        LOGGER.info("Parameter for bounds check is given, we add bounds check.");
        boundsCheck = true;
      }
      else if (parameter.equalsIgnoreCase("-noSafeLocalToStack") || parameter.equalsIgnoreCase("-nl")) {
        LOGGER.info("Parameter for noSaveLocalToStack is given, local variables will store on heap.");
        saveLocalToStack = true;
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
//      final Main main = new Main(filename, optimisationLevel, verboseLevel, outputpath,
//          selfModifiedCode, starChainMult, shiftMultDiv, smallAddSubHeapPtr, importHeader,
//          showVariableUsage, showVariableUnused, showPeepholeOptimize);

      Options options = new Options();
      options.setOptimisationLevel(optimisationLevel);
      options.setVerboseLevel(verboseLevel);
      options.setSelfModifiedCode(selfModifiedCode);
      options.setStarChainMult(starChainMult);
      options.setShiftMultDiv(shiftMultDiv);
      options.setSmallAddSubHeapPtr(smallAddSubHeapPtr);
      options.setImportHeader(importHeader);
      options.setShowVariableUnused(showVariableUnused);
      options.setShowVariableUsage(showVariableUsage);
      options.setShowPeepholeOptimize(showPeepholeOptimize);
      options.setTestIncludes(testInclude);
      options.setBoundsCheck(boundsCheck);
      options.setSaveLocalToStack(saveLocalToStack);
      
      final Main main = new Main(filename, outputpath, options);

      main.setIncludePath(includePaths);
      if (!basename.isEmpty()) {
        main.addIncludePath(basename);
      }
      main.start();
    } catch (final IllegalArgumentException e) {
      LOGGER.error("ERROR: {}", e.getMessage());
      throw new IllegalArgumentException(e) {
        @Override
        public synchronized Throwable fillInStackTrace() {
          return null;
        }
      };
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
      throw new FileNotFoundException("ERROR: Given file '"+filename+"' does not exist.");
    }

    String headerfilename;
    if (file.getParent() != null) {
      headerfilename = file.getParent() + "/header.wnf";
    }
    else {
      headerfilename = "header.wnf";
    }

    LOGGER.info("Check for header file: '{}'", headerfilename);
    String wnfHeaderCode = null;
    File headerfile = new File(headerfilename);
    if (headerfile.exists()) {
      if (!options.isImportHeader()) {
        LOGGER.info("Found header file, but will ingnored due to parameter");
      }
      else {
        LOGGER.info("Found a 'header.wnf' file, read it first.");
        wnfHeaderCode = new SourceReader(headerfilename).readFile();
        this.headerSource = new Source(wnfHeaderCode);
        this.headerSource.setFilename("header.wnf");
      }
    }

    String wnfProgramCode = new SourceReader(this.filename).readFile();
    this.source = new Source(wnfProgramCode);
    this.source.setFilename(file.getName());
    this.source.setOptions(options);
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

    optimize(options.getOptimisationLevel());

    write();

    System.out.println("");
  }

  public Main optimize(int optimisationLevel) {
    PeepholeOptimizer optimizer = new PeepholeOptimizer(source, optimisationLevel);
    optimizer.optimize().build();
    optimizer.showStatus();

    setUsedOptimisations(optimizer.getUsedOptimisations());

    RegisterOptimizer regOptimizer = new RegisterOptimizer(source, optimisationLevel);
    regOptimizer.optimize().build();
    regOptimizer.showStatus();

    LongJumpOptimizer longJumpOptimizer = new LongJumpOptimizer(source, optimisationLevel);
    longJumpOptimizer.optimize().build();
    longJumpOptimizer.showStatus();

    return this;
  }

  public Main compile() {
    if (source == null) {
      throw new IllegalStateException("Source not initialised");
    }

    LOGGER.info("compile");

    Header header = null;
    if (headerSource != null) {
      header = new Header(headerSource).load();
    }

    // Block

    new Block(source).start(header).build();

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
    throw new IllegalStateException(errorMessage) {
      @Override
      public synchronized Throwable fillInStackTrace() {
        return null;
      }
    };
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

  public Source getSource() {
    return source;
  }
}
