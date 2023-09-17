package org.openstreetmap.josm.plugins.ods.util;

import java.util.Collection;

public interface OneOrMany<T> {

    public void add(T value);
    public boolean hasMany();
    public Collection<? extends T> getAll();
    public T get();
 }
