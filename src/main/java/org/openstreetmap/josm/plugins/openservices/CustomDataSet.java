package org.openstreetmap.josm.plugins.openservices;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.openstreetmap.josm.data.osm.DataSet;

public abstract class CustomDataSet<T> extends DataSet {
  private final Map<Class<?>, Map<Serializable, T>> data = 
      new HashMap<Class<?>, Map<Serializable, T>>();
  private ObjectToJosmMapper<T> mapper;
  
  protected void setMapper(ObjectToJosmMapper<T> mapper) {
    this.mapper = mapper;
  }

  /**
   * Add an object to the DataSet.
   * The given key and Object class are used to check if the object already exists.
   * If so, do nothing.
   * Otherwise add the object, call the toJosm function to create a collection of
   * OSM objects and add them to the OSM layer; 
   * 
   * @param key
   * @param object
   */
  public final void add(T object) {
    if (addIfNew(object)) {
      toJosm(object);
    }
  }
  
  /**
   * Try to add the object and its id.
   * 
   * @param object
   * @param id
   * @return true if the object didn't exist
   */
  protected boolean addIfNew(T object) {
    Map<Serializable, T> classData = getClassData(object.getClass());
    Serializable id = getId(object);
    if (classData.get(id) != null) {
      return false;
    }
    classData.put(id, object);
    return true;
  }
  
  private Map<Serializable, T> getClassData(Class<?> clazz) {
    Map<Serializable, T> result = data.get(clazz);
    if (result == null) {
      result = new HashMap<Serializable, T>();
      data.put(clazz, result);
    }
    return result;
  }
  
  protected void toJosm(T o) {
    mapper.create(o);
  }
  
  protected abstract Serializable getId(T o);
}
