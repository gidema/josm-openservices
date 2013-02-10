package org.openstreetmap.josm.plugins.openservices;

public class PropertyMapper {
  private String key;
  private String property;
  private String format;

  public final String getKey() {
    return key;
  }

  public final void setKey(String key) {
    this.key = key;
  }

  public final String getProperty() {
    return property;
  }

  public final void setProperty(String property) {
    this.property = property;
  }

  public final String getFormat() {
    return format;
  }

  public final void setFormat(String format) {
    this.format = format;
  }

  public String map(Object value) {
    if (format == null) {
      return value.toString();
    }
    return String.format(format, value);
  }
}
