package nl.gertjanidema.conversion.valuemapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ValueMapperFactory {
  private final Map<String, Class<? extends ValueMapper<?>>> registry;
  
  public ValueMapperFactory() {
    registry = new HashMap<String, Class<? extends ValueMapper<?>>>();
    registry.put("boolean", BooleanValueMapper.class);
    registry.put("byte", ByteValueMapper.class);
    registry.put("date", DateValueMapper.class);
    registry.put("decimal", DecimalValueMapper.class);
    registry.put("double", DoubleValueMapper.class);
    registry.put("enum", EnumValueMapper.class);
    registry.put("float", FloatValueMapper.class);
    registry.put("integer", IntegerValueMapper.class);
    registry.put("long", LongValueMapper.class);
    registry.put("short", ShortValueMapper.class);
    registry.put("string", StringValueMapper.class);
  }
  
  public ValueMapper<?> createValueMapper(String type, Properties properties) throws ValueMapperException {
    Class<? extends ValueMapper<?>> clazz =
      registry.get(type.toLowerCase());
    if (clazz != null) {
      ValueMapper<?> mapper;
      try {
        mapper = clazz.newInstance();
        mapper.setProperties(properties);
        return mapper;
      } catch (InstantiationException e) {
        throw new ValueMapperException("Couldn't instantiate ValueMapper", e);
      } catch (IllegalAccessException e) {
        throw new ValueMapperException("Couldn't access ValueMapper", e);
      }
    }
    else {
      throw new ValueMapperException(String.format(
          "Unknown valueMapper type: %s", type));
    }
  }
}
