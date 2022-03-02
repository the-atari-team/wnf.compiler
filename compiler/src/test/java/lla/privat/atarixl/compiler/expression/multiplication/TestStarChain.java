package lla.privat.atarixl.compiler.expression.multiplication;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import lla.privat.atarixl.compiler.source.Source;

public class TestStarChain {

  private StarChain starchainSUT;
  
  @Before
  public void setUp() {
    Source source = new Source(" ");
    starchainSUT = new StarChain(source);
  }
 
  @Test
  public void testMultAnd1() {
    for (int i=0;i<15;i++) {
      if ((i & 1) == 1) {
        System.out.println(i);
      }
    }
  }
  
  @Test
  public void test7() {
    starchainSUT.domult(7);
  }
  
  @Ignore
  @Test
  public void all16BitIntegerValues() {
    for(int i=0;i<65535;i++) {
      starchainSUT.domult(i).build();
    }
  }

  
}
