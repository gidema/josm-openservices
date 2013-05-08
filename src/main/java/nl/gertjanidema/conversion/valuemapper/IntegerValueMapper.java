package nl.gertjanidema.conversion.valuemapper;

import java.text.ParseException;

// TODO implement octal, hex, radix
public class IntegerValueMapper extends NumberValueMapper<Integer> {

  public Integer parse(String source) throws ValueMapperException {
    if (isNull(source)) {
      return null;
    }
    try {
      return format.parse(source).intValue();
    } catch (ParseException e) {
      throw new ValueMapperException("Invalid number format", e);
    }
  }

  @Override
  protected String typedFormat(Integer object) throws ValueMapperException {
    if (object == null) {
      return super.typedFormat(object);
    }
    return format.format(object);
  }

  public Class<Integer> getType() {
    return Integer.class;
  }
  
}
