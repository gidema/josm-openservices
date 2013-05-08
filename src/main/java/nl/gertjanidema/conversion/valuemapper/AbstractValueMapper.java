package nl.gertjanidema.conversion.valuemapper;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

/**
 * Abstract implementation of the ValueMapper<T> interface
 * Supported properties:
 * - locale
 *    Locale used in conversion. Defaults to JVM default value
 * - nullValues
 *    Comma separated case-sensitive list of strings
 * - length
 *    Maximum length of string values
 * 
 * @author gertjan
 *
 * @param <T>
 */
public abstract class AbstractValueMapper<T> implements ValueMapper<T> {
  private List<String> nullValues;
  private Locale locale;
  protected Integer length = -1;
  private final Properties properties = new Properties();

  @Override
  public void setProperties(Properties properties) throws IllegalArgumentException {
    this.properties.putAll(properties);
    this.init();
  }
  
  /**
   * @throws ConfigException  
   */
  protected void init() throws IllegalArgumentException {
    length = Integer.valueOf(getProperty("length", "-1"));
    String sNullValues = getProperty("null");
    if (sNullValues != null) {
      nullValues = Arrays.asList(sNullValues.split(","));
    }
    String sLocale = getProperty("locale", null);
    if (sLocale != null) {
      locale = new Locale(sLocale);
    } else {
      locale = Locale.getDefault();
    }
  }
  
  public String getProperty(String key) {
    return properties.getProperty(key);
  }

  public String getProperty(String key, String defaultValue) {
    return properties.getProperty(key, defaultValue);
  }

  @Override
  public String format(Object object) throws ValueMapperException {
    return typedFormat(this.getType().cast(object));
  }
  
  protected String typedFormat(T object) throws ValueMapperException {
    String result;
    if (object == null) {
      if (nullValues != null) {
        result = nullValues.get(0);
      }
      else {
        throw new ValueMapperException("Null values not allowed");
      }
    }
    else {
      result = object.toString();
    }
    if (length >0 && result.length() >length) {
      throw new ValueMapperException("String too long");      
    }
    return result;
  }

  public boolean isNull(String source) {
    if (source == null) {
      throw new NullPointerException();
    }
    if (nullValues != null) {
      return nullValues.contains(source);
    }
    return false;
  }

  protected Locale getLocale() {
    return this.locale;
  }

}
