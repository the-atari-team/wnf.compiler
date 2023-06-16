package lla.privat.atarixl.compiler.optimization;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import lla.privat.atarixl.compiler.source.Source;

public class TestLongJumpOptimizer {

  LongJumpOptimizer longJumpOptimizerSUT;
  
  Source source;
  
  @Before
  public void setUp() {
    source = new Source("");
    longJumpOptimizerSUT = new LongJumpOptimizer(source, 0);
  }
  
// jccfixer was a fix for failure in JCC opcode in atasm v1.22
// JCC ist fixed in atasm v1.23
  @Ignore
  @Test
  public void test() {
    List<String> code = new ArrayList<>();
    code.add(" JCC jumpmark ; comment");
    
    source.resetCode(code);
    
    longJumpOptimizerSUT.setLevel(0).optimize().build();

    int n=-1;
    Assert.assertEquals(" .BYTE $B0,$03,$4C,<jumpmark,>jumpmark ; jccfixer", source.getCode().get(++n));
  }

}
