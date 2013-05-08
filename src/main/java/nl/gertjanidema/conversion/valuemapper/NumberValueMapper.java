package nl.gertjanidema.conversion.valuemapper;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public abstract class NumberValueMapper<T extends Number> extends AbstractValueMapper<T> {
  protected DecimalFormat format = new DecimalFormat();
  
  @Override
  protected void init() throws IllegalArgumentException {
    super.init();
    format = (DecimalFormat) NumberFormat.getInstance(getLocale());
    String pattern = getProperty("pattern");
    if (pattern != null) {
      format.applyLocalizedPattern(pattern);
    }
    format = (DecimalFormat) NumberFormat.getInstance();
  }

}
