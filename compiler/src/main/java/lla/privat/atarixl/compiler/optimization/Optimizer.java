package lla.privat.atarixl.compiler.optimization;

import lla.privat.atarixl.compiler.source.Source;

public abstract class Optimizer {

  final Source source;
  protected int optimisationLevel;

  private int countOfOptimize;
  
  public Optimizer(Source source, int optimisationLevel) {
    this.source = source;
    this.optimisationLevel = optimisationLevel;
    countOfOptimize =0;
  }
  
  public int getUsedOptimisations() {
    return countOfOptimize;
  }
  
  public void incrementCountOfOptimize() {
    ++countOfOptimize;
  }
  
  abstract public Optimizer optimize();
  
  abstract public void build();

  public Optimizer setLevel(int level) {
    this.optimisationLevel = level;
    return this;
  }

}
