package nl.gertjanidema.conversion.valuemapper;

import java.text.ParseException;

public class DoubleValueMapper extends NumberValueMapper<Double> {

  //private NumberFormat numberFormat = NumberFormat.getInstance();
  
  public Double parse(String source) throws ValueMapperException {
    if (isNull(source)) {
      return null;
    }
    try {
      return format.parse(source).doubleValue();
    } catch (ParseException e) {
      throw new ValueMapperException("Invalid number format", e);
    }
  }

  @Override
  protected String typedFormat(Double object) throws ValueMapperException {
    if (object == null) {
      return super.typedFormat(object);
    }
    return format.format(object);
  }

  public Class<Double> getType() {
    return Double.class;
  }
  
}
