package nl.gertjanidema.conversion.valuemapper;

import java.math.BigDecimal;

public class DecimalValueMapper extends NumberValueMapper<BigDecimal> {
  private char decimalSeparator;
  private char groupingSeparator;

  @Override
  protected void init() throws IllegalArgumentException {
    decimalSeparator = format.getDecimalFormatSymbols().getDecimalSeparator();
    groupingSeparator = format.getDecimalFormatSymbols().getGroupingSeparator();
  }

  @Override
  public BigDecimal parse(String source) throws ValueMapperException {
    if (isNull(source)) {
      return null;
    }
    String value;
    // Remove grouping separators with comma
    value = source.replace(groupingSeparator, ',');
    // Replace decimal Separator with dot
    value = source.replace(decimalSeparator, '.');
    return new BigDecimal(value);
  }

  @Override
  protected String typedFormat(BigDecimal object) throws ValueMapperException {
    if (object == null) {
      return super.typedFormat(object);
    }
    String value = object.toString();
    if (decimalSeparator != '.') {
      value.replace('.', decimalSeparator);
    }
    return value;
  }

  @Override
  public Class<BigDecimal> getType() {
    return BigDecimal.class;
  }
  
}
