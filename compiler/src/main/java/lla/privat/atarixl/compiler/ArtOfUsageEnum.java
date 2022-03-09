//cdw by 'The Atari Team' 2021
//licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler;

public enum ArtOfUsageEnum {
  UNDEFINED(0), READ(1), WRITE(2), ALL(3);

  private int internValue;
 
  private ArtOfUsageEnum(int value) {
    internValue = value;
  }

  private int getValue() {
    return internValue;
  }
  
  public ArtOfUsageEnum getAtLeastUsage(ArtOfUsageEnum usage) {
    if ((usage.getValue() & getValue()) != 0) {
      return this;
    }
    int newValue = usage.getValue() | getValue();
    switch (newValue) {
    case 0:
      return UNDEFINED;
    case 1:
      return READ;
    case 2:
      return WRITE;
    }
    return ALL;
  }

  public boolean hasReadAccess() {
    return (getValue() & 0x1) == 0x1;
  }

  public boolean hasWriteAccess() {
    return (getValue() & 0x2) == 0x2;
  }

  public boolean hasAnyAccess() {
    return (getValue() & 0x3) != 0;
  }

  public boolean hasAllAccess() {
    return getValue() == 0x3;
  }

}
