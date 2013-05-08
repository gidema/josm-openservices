package nl.gertjanidema.conversion.valuemapper;

import java.util.Properties;

/**
 * The ValueMapper interface is an interface for mapping String values
 * to and from Java Objects.
 * 
 * Each implementation of this class can map values for a specific data type.
 * Each instance of an implementation can be further configured by setting
 * properties that specify the mapper's operation mode.
 * This allows configuration for things like :
 * - format strings
 * - null value handling
 * - size limitations
 * etc
 * 
 * @author gertjan
 *
 * @param <T> The parameter type this mapper can map
 *            currently only 'primitive' Object types are supported
 */
public interface ValueMapper<T> {
  
  /**
   * Set the properties for this value mapper
   * 
   * @param properties
   * @throws ConfigException
   */
  public void setProperties(Properties properties) throws IllegalArgumentException;
  
  /**
   * Parse a source string and convert it to a type <T> object
   * 
   * @param source
   *   The string value to be parsed
   * @return
   *   The <T> type Object value parsed from the source string
   * @throws ValueMapperException
   *   The exception is thrown when the source string could not be
   *   converted to an object of type <T>
   */
  public T parse(String source) throws ValueMapperException;
  
  /**
   * Format a type <T> Object into a string, respecting the properties
   * 
   * @param object
   * @return
   * @throws ValueMapperException
   */
  public String format(Object object) throws ValueMapperException;
  
  /**
   * Return the type <T> of this mapper
   * 
   * @return
   *   The Class of the object type this mapper can map
   */
  public Class<T> getType();
}
