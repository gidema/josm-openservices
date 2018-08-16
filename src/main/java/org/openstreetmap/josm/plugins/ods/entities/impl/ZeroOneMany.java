package org.openstreetmap.josm.plugins.ods.entities.impl;

import static org.openstreetmap.josm.plugins.ods.entities.impl.ZeroOneMany.Cardinality.Many;
import static org.openstreetmap.josm.plugins.ods.entities.impl.ZeroOneMany.Cardinality.One;
import static org.openstreetmap.josm.plugins.ods.entities.impl.ZeroOneMany.Cardinality.Zero;

import java.util.Collection;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public class ZeroOneMany<T> {
    public static <T> ZeroOneMany<T> forCollection(Collection<T> collection) {
        if (collection == null || collection.isEmpty())
            return new ZeroOneMany<>();
        return new ZeroOneMany<>(collection);
    }

    /**
     * If non-null, the single value; if null, indicates o or many values are
     * present
     */
    private T one;
    private Set<T> many;

    public enum Cardinality {
        Zero, One, Many
    }

    /**
     * Constructs an empty instance.
     */
    public ZeroOneMany() {
    }

    /**
     * Constructs an instance from an optional
     */
    public ZeroOneMany(Optional<T> optional) {
        if (optional.isPresent()) {
            one = optional.get();
        }
    }

    public ZeroOneMany(Collection<T> many) {
        if (many == null || many.size() == 0) {
            throw new UnsupportedOperationException();
        }
        if (many.size() == 1) {
            this.one = many.iterator().next();
        }
        else {
            this.many = new HashSet<>(many);
        }
    }

    /**
     * Constructs an instance from a (possibly null) value.
     *
     */
    public ZeroOneMany(T value) {
        this.one = value;
    }

    public Cardinality getCardinality() {
        return many != null ? Many : one != null ? One : Zero;
    }

    /**
     * If a single value is present in this {@code ZeroOneMany}, returns the
     * value, otherwise throws {@code NoSuchElementException}.
     *
     * @return the non-null value held by this {@code ZeroOneMany}
     * @throws NoSuchElementException
     *             if there is no value present
     */
    public T getOne() {
        if (one == null) {
            throw new NoSuchElementException(many != null
                    ? "More than one value present" : "No value present");
        }
        return one;
    }

    /**
     * If multiple values are present in this {@code ZeroOneMany}, returns the
     * values, otherwise throws {@code NoSuchElementException}.
     *
     * @return the values held by this {@code ZeroOneMany}
     * @throws NoSuchElementException
     *             if there are 0 or 1 values
     */
    public Set<T> getMany() {
        if (many == null) {
            throw new NoSuchElementException(many != null
                    ? "More than one value present" : "No value present");
        }
        return many;
    }

    /**
     * Return {@code true} if there are no values present, otherwise
     * {@code false}.
     *
     * @return {@code true} if there are no values present, otherwise
     *         {@code false}
     */
    public boolean isEmpty() {
        return one == null && many == null;
    }

    /**
     * Return {@code true} if there is exactly one value present, otherwise
     * {@code false}.
     *
     * @return {@code true} if there is exactly one value present, otherwise
     *         {@code false}
     */
    public boolean isOne() {
        return one != null;
    }

    /**
     * Return {@code true} if there are more than one values present, otherwise
     * {@code false}.
     *
     * @return {@code true} if there are more than one values value present,
     *         otherwise {@code false}
     */
    public boolean isMany() {
        return many != null;
    }

    /**
     * If exactly 1 value is present, invoke the specified consumer with the
     * value, otherwise do nothing.
     *
     * @param consumer
     *            block to be executed if a single value is present
     * @throws NullPointerException
     *             if value is present and {@code consumer} is null
     */
    public void ifOne(Consumer<? super T> consumer) {
        if (one != null)
            consumer.accept(one);
    }

    /**
     * Execute the consumer for each element
     *
     */
    public void forEach(Consumer<T> consumer) {
        switch (getCardinality()) {
        case One:
            consumer.accept(one);
            break;
        case Many:
            many.forEach(consumer);
            break;
        default:
            // No action required
        }
    }

    public boolean add(T element) {
        switch (getCardinality()) {
        case Zero:
            one = element;
            return true;
        case One:
            if (element.equals(one)) {
                return false;
            }
            many = new TinySet<>(one, element);
            one = null;
            return true;
        case Many:
            if (many.contains(element)) {
                return false;
            }
            return true;
        default:
            return false;
        }
    }

    public void remove(T element) {
        if (one.equals(element)) {
            one = null;
        } else if (many.remove(element)) {
            if (many.size() == 1) {
                one = many.iterator().next();
                many = null;
            }
        }
    }

    /**
     * Indicates whether some other object is "equal to" this ZeroOrMany. The
     * other object is considered equal if:
     * <ul>
     * <li>it is also an {@code ZeroOrMany} and;
     * <li>both instances have equal values present
     * </ul>
     *
     * @param obj
     *            an object to be tested for equality
     * @return {code true} if the other object is "equal to" this object
     *         otherwise {@code false}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof ZeroOneMany)) {
            return false;
        }

        ZeroOneMany<?> other = (ZeroOneMany<?>) obj;
        return Objects.equals(one, other.one)
                && Objects.equals(many, other.many);
    }

    /**
     * Returns the hash code value of the present value, if any, or 0 (zero) if
     * no value is present.
     *
     * @return hash code value of the present value or 0 if no value is present
     */
    @Override
    public int hashCode() {
        return Objects.hash(one, many);
    }

    /**
     * Returns a non-empty string representation of this ZeroOneMany suitable
     * for debugging. The exact presentation format is unspecified and may vary
     * between implementations and versions.
     *
     * @implSpec If a value is present the result must include its string
     *           representation in the result. Empty and other ZeroOneManys must
     *           be unambiguously differentiable.
     *
     * @return the string representation of this instance
     */
    @Override
    public String toString() {
        switch (getCardinality()) {
        case Zero:
            return "ZeroOneMany.empty";
        case One:
            return String.format("ZeroOneMany[%s]", one);
        case Many:
            return String.format("ZeroOneMany[%s]", many);
        }
        return null;
    }
}
