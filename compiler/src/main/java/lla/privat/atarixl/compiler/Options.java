package lla.privat.atarixl.compiler;

public class Options {
  private int optimisationLevel = 0;
  private int verboseLevel = 0;
  private boolean selfModifiedCode = false;
  private boolean starChainMult = true;
  private boolean shiftMultDiv = true;
  private boolean smallAddSubHeapPtr = false;
  private boolean importHeader = true;
  private boolean showVariableUsage = false;
  private boolean showVariableUnused = false;
  private boolean showPeepholeOptimize = false;
  private boolean testIncludes = false;
  private char precalculate;
  private boolean boundsCheck = false;
  private boolean saveLocalToStack = true;
  
  public Options() {
    verboseLevel = 0;
    optimisationLevel = 2;
    selfModifiedCode = false;
    starChainMult = true;
    shiftMultDiv = true;
    smallAddSubHeapPtr = false;
    importHeader = true;
    showVariableUsage = false;
    showVariableUnused = false;
    showPeepholeOptimize = false;
    testIncludes = false;
    precalculate = 'n';
    boundsCheck = false;
  }

  public Options(int optimisationLevel,
      int verboseLevel,
      boolean selfModifiedCode, boolean starChainMult, boolean shiftMultDiv,
      boolean smallAddSubHeapPtr, boolean importHeader, boolean showVariableUsage,
      boolean showVariableUnused, boolean showPeepholeOptimize,
      char precalculate, boolean boundsCheck) {
    this.optimisationLevel = optimisationLevel;
    this.verboseLevel = verboseLevel;
    this.selfModifiedCode = selfModifiedCode;
    this.starChainMult = starChainMult;
    this.shiftMultDiv = shiftMultDiv;
    this.smallAddSubHeapPtr = smallAddSubHeapPtr;
    this.importHeader = importHeader;
    this.showVariableUsage = showVariableUsage;
    this.showVariableUnused = showVariableUnused;
    this.showPeepholeOptimize = showPeepholeOptimize;
    this.precalculate = precalculate;
    this.boundsCheck = boundsCheck;
  }

  public int getOptimisationLevel() {
    return optimisationLevel;
  }

  public void setOptimisationLevel(int optimisationLevel) {
    this.optimisationLevel = optimisationLevel;
  }

  public int getVerboseLevel() {
    return verboseLevel;
  }

  public void setVerboseLevel(int verboseLevel) {
    this.verboseLevel = verboseLevel;
  }

  public boolean isSelfModifiedCode() {
    return selfModifiedCode;
  }

  public void setSelfModifiedCode(boolean selfModifiedCode) {
    this.selfModifiedCode = selfModifiedCode;
  }

  public boolean isStarChainMult() {
    return starChainMult;
  }

  public void setStarChainMult(boolean starChainMult) {
    this.starChainMult = starChainMult;
  }

  public boolean isShiftMultDiv() {
    return shiftMultDiv;
  }

  public void setShiftMultDiv(boolean shiftMultDiv) {
    this.shiftMultDiv = shiftMultDiv;
  }

  public boolean isSmallAddSubHeapPtr() {
    return smallAddSubHeapPtr;
  }

  public void setSmallAddSubHeapPtr(boolean smallAddSubHeapPtr) {
    this.smallAddSubHeapPtr = smallAddSubHeapPtr;
  }

  public boolean isImportHeader() {
    return importHeader;
  }

  public void setImportHeader(boolean importHeader) {
    this.importHeader = importHeader;
  }

  public boolean isShowVariableUsage() {
    return showVariableUsage;
  }

  public void setShowVariableUsage(boolean showVariableUsage) {
    this.showVariableUsage = showVariableUsage;
  }

  public boolean isShowPeepholeOptimize() {
    return showPeepholeOptimize;
  }

  public void setShowPeepholeOptimize(boolean showPeepholeOptimize) {
    this.showPeepholeOptimize = showPeepholeOptimize;
  }

  public boolean isShowVariableUnused() {
    return showVariableUnused;
  }

  public void setShowVariableUnused(boolean showVariableUnused) {
    this.showVariableUnused = showVariableUnused;
  }

  public boolean isErrorPrecalculate() {
    return precalculate=='e';
  }
  public boolean isWarningPrecalculate() {
    return precalculate=='w';
  }
  public char getPrecalculate() {
    return precalculate;
  }

  public void setPrecalculate(char precalculate) {
    this.precalculate = precalculate;
  }

  public boolean isTestIncludes() {
    return testIncludes;
  }

  public void setTestIncludes(boolean testIncludes) {
    this.testIncludes = testIncludes;
  }

  public boolean isBoundsCheck() {
    return boundsCheck;
  }

  public void setBoundsCheck(boolean boundsCheck) {
    this.boundsCheck = boundsCheck;
  }

  public boolean isSaveLocalToStack() {
    return saveLocalToStack;
  }

  public void setSaveLocalToStack(boolean saveLocalToStack) {
    this.saveLocalToStack = saveLocalToStack;
  }
}
