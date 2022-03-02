// cdw by 'The Atari Team' 2021
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.compiler;

import java.util.ArrayList;
import java.util.List;

import lla.privat.atarixl.compiler.expression.Type;

/**
 * VariableDefinition, - was ist eine Variable vom Type, (BYTE, WORD, STRING,
 * BYTE_ARRAY, WORD_ARRAY, WORD_SPLIT_ARRAY) - wieviel Bytes belegt das Array - ArtOfUsage, wurde
 * die Variable beschrieben, nur gelesen oder undefined (überflüssig)
 */
public class VariableDefinition {
  private final String name;
  private Type type;
  private String prefix;
  
  private String address;
  private int sizeOfArray;
  private final List<String> arrayContent;

  private boolean generated;

  // Hier ablegen, aus welcher Datei die Definition kommt, so kann ich Fehler besser verfolgen
  private final String sourcecode_filename;
  
  private enum ArtOfUsageEnum {
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
  };

  private ArtOfUsageEnum artOfUsage;

  public VariableDefinition(String name, Type type, String sourcecode_filename) {
    this(name, type, 0, sourcecode_filename);
  }

  public VariableDefinition(String name, Type type, int sizeOfArray, String sourcecode_filename) {
    this.name = name;
    this.type = type;
    this.sizeOfArray = sizeOfArray;
    this.arrayContent = new ArrayList<>();
    this.artOfUsage = ArtOfUsageEnum.UNDEFINED;
    this.sourcecode_filename = sourcecode_filename;
    generated = false;
    this.prefix = "";
  }

  public String getName() {
    if (prefix.length() > 0) {
      return prefix + "_" + name;
    }
    return name;
  }

  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    VariableDefinition other = (VariableDefinition) obj;
    if (name == null) {
      if (other.name != null)
        return false;
    }
    else if (!name.equals(other.name))
      return false;
    if (type != other.type)
      return false;
    return true;
  }

  public void setRead() {
    artOfUsage = artOfUsage.getAtLeastUsage(ArtOfUsageEnum.READ);
  }

  public void setWrite() {
    if (type.equals(Type.CONST)) {
      throw new IllegalStateException("CONST Variable "+name+" can't be written.");
    }
    artOfUsage = artOfUsage.getAtLeastUsage(ArtOfUsageEnum.WRITE);
  }

  public boolean hasAnyAccess() {
    return artOfUsage.hasAnyAccess();
  }

  public boolean hasAllAccess() {
    return artOfUsage.hasAllAccess();
  }

  public boolean hasReadAccess() {
    return artOfUsage.hasReadAccess();
  }

  public boolean hasWriteAccess() {
    return artOfUsage.hasWriteAccess();
  }

  public int getSizeOfArray() {
    return (arrayContent.size() == 0) ? sizeOfArray : arrayContent.size();
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getAddress() {
    return address;
  }

  public boolean hasAddress() {
    return address != null;
  }
  
  public void setArray(List<String> arrayContent) {
    for (String item : arrayContent) {
      this.arrayContent.add(item);
    }
  }
  public String getArrayElement(int index) {
    return arrayContent.get(index);
  }

  public List<String> getArrayContent(){
    return arrayContent;
  }

  public boolean isGenerated() {
    return generated;
  }

  public void setGenerated(boolean generated) {
    this.generated = generated;
  }

  public String getFilename() {
    return sourcecode_filename;
  }

  public void resetNameWithPrefix(String prefix) {
    this.prefix = prefix;
  }
}
