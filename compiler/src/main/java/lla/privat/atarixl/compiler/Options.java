package lla.privat.atarixl.compiler;

public class Options {
  private int optimisationLevel = 0;
  private int verboseLevel = 0;
  private boolean selfModifiedCode = false;
  private boolean starChainMult = true;
  private boolean shiftMultDiv = true;
  private boolean smallAddSubHeapPtr = false;
  private boolean importHeader = true;
  
  public Options() {
    verboseLevel = 0;
    optimisationLevel = 2;
    selfModifiedCode = false;
    starChainMult = true;
    shiftMultDiv = true;
    smallAddSubHeapPtr = false;
    importHeader = true;
  }
  
  public Options(int optimisationLevel,
      int verboseLevel,
      boolean selfModifiedCode, boolean starChainMult, boolean shiftMultDiv,
      boolean smallAddSubHeapPtr, boolean importHeader) {
    this.optimisationLevel = optimisationLevel;
    this.verboseLevel = verboseLevel;
    this.selfModifiedCode = selfModifiedCode;
    this.starChainMult = starChainMult;
    this.shiftMultDiv = shiftMultDiv;
    this.smallAddSubHeapPtr = smallAddSubHeapPtr;
    this.importHeader = importHeader;
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
  
  
}
