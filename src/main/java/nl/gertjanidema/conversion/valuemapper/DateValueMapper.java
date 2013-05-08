package nl.gertjanidema.conversion.valuemapper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateValueMapper extends AbstractValueMapper<Date> {
  private DateFormat format = new SimpleDateFormat();

  
  @Override
  protected void init()
      throws IllegalArgumentException {
    super.init();
    String pattern = getProperty("pattern", null);
    if (pattern != null) {
      this.format = new SimpleDateFormat(pattern, getLocale());
    } 
    else {
      this.format = DateFormat.getDateInstance(DateFormat.SHORT, getLocale()); 
    }
  }

  @Override
  public Date parse(String source) throws ValueMapperException {
    if (isNull(source)) {
      return null;
    }
    try {
      return format.parse(source);
    } catch (ParseException e) {
      throw new ValueMapperException("Invalid date value", e);
    }
  }

  @Override
  protected String typedFormat(Date date) throws ValueMapperException {
    if (date == null) {
      return super.typedFormat(date);
    }
    try {
      return format.format(date);
    } catch (IllegalArgumentException e) {
      throw new ValueMapperException("Illegal argument type");
    }
  }

  @Override
  public Class<Date> getType() {
    return Date.class;
  }

  
}
