package org.openstreetmap.josm.plugins.ods.entities.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**
 * Set implementation for very small (2 - 3 items) Set
 *
 * @author Gertjan Idema
 *
 * @param <T>
 */
public class TinySet<T> extends ArrayList<T> implements Set<T> {
    /**
     *
     */
    private static final long serialVersionUID = -6519134310315458069L;

    public TinySet() {
        this(2);
    }

    public TinySet(int size) {
        super(size);
    }

    /**
     * Create a Tiny set with initial size 2 and add 2 elements
     *
     * @param element1
     * @param element2
     */
    public TinySet(T element1, T element2) {
        this(2, element1, element2);
    }

    /**
     * Create a Tiny set with initial <code>size</code> and add 2 elements
     *
     * @param size The initial size
     * @param element1 The first element to add
     * @param element2 The second element to add
     */
    public TinySet(int size, T element1, T element2) {
        this(size);
        this.add(element1);
        this.add(element2);
    }

    @Override
    public boolean add(T o) {
        if (!contains(o)) {
            super.add(o);
            return true;
        }
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends T> elements) {
        boolean changed = false;
        for (T element : elements) {
            changed = changed || add(element);
        }
        return changed;
    }
}
