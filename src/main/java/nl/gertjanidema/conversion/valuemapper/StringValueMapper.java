package nl.gertjanidema.conversion.valuemapper;

public class StringValueMapper extends AbstractValueMapper<String> {
  private boolean trim = false;

  @Override
  protected void init() throws IllegalArgumentException {
    trim = new Boolean(getProperty("trim", "false"));
  }

  @Override
  public String parse(String source) throws ValueMapperException{
    if (isNull(source)) {
      return null;
    }
    if ((length <= 0 ) || (source.length() <= length)) {
      return source;
    }
    if (trim) {
      return source.substring(0, length-1);
    }
    throw new ValueMapperException("String too long");
  }

  @Override
  protected String typedFormat(String s) throws ValueMapperException {
    if (s == null) {
      return super.typedFormat(s);
    }
    return new String(s);
  }

  @Override
  public Class<String> getType() {
    return String.class;
  }
}
