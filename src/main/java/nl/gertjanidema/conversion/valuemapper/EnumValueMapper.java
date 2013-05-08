package nl.gertjanidema.conversion.valuemapper;

@SuppressWarnings("rawtypes")
public class EnumValueMapper extends AbstractValueMapper<Enum> {

  private Class<? extends Enum> type;
  
  public EnumValueMapper(Class<? extends Enum> type) {
    super();
    this.type = type;
  }

  public EnumValueMapper() {
    // No argument constructor
  }

  @SuppressWarnings("unchecked")
  @Override
  protected void init() throws IllegalArgumentException {
    super.init();
    String className = getProperty("class");
    if (className != null) {
      try {
        Class<?> clazz = Class.forName(className);
        if (clazz.isEnum()) {
          type = (Class<? extends Enum>) clazz;
        }
        else {
          throw new IllegalArgumentException(String.format(
              "Invalid class type. '%s' is not an Enum class", className));
        }
      } catch (ClassNotFoundException e) {
        throw new IllegalArgumentException(e);
      }
    }
    if (type == null) {
      throw new IllegalArgumentException("The 'class' parameter must be set for enum converters");
    }
  }
  
  @Override
  @SuppressWarnings("unchecked")
  public Enum parse(String source) throws ValueMapperException {
    if (isNull(source)) {
      return null;
    }
    try {
      return Enum.valueOf(type, source);
    } catch (Exception e) {
      throw new ValueMapperException("Invalid enum value: '%s'", source);
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public Class getType() {
    return type;
  }
  
}
