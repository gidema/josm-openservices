package nl.gertjanidema.conversion.valuemapper;

import java.util.Arrays;
import java.util.List;

public class BooleanValueMapper extends AbstractValueMapper<Boolean> {
  private List<String> trueValues;
  private List<String> falseValues;
  
  @Override
  protected void init()
      throws IllegalArgumentException {
    super.init();
    String sTrueValues = getProperty("true", "true");
    String sFalseValues = getProperty("false", "false");
    trueValues = Arrays.asList(sTrueValues.split(","));    
    falseValues = Arrays.asList(sFalseValues.split(","));
  }

  @Override
  public Boolean parse(String source) throws ValueMapperException {
    if (isNull(source)) {
      return null;
    }
    if (trueValues.contains(source)) {
      return true;
    }
    if (falseValues.contains(source)) {
      return false;
    }
    throw new ValueMapperException("Illegal boolean value");
  }

  @Override
  protected String typedFormat(Boolean object) throws ValueMapperException {
    if (object == null) {
      return super.typedFormat(object);
    }
    return (object)?trueValues.get(0):falseValues.get(0);
  }

  @Override
  public Class<Boolean> getType() {
    return Boolean.class;
  }

}
