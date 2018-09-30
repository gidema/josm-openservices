package org.openstreetmap.josm.plugins.ods.util;

import org.openstreetmap.josm.tools.I18n;

public final class MessageBuilder {
    private final StringBuilder sb = new StringBuilder();

    public MessageBuilder append(String str) {
        sb.append(str);
        return this;
    }

    public MessageBuilder appendI18n(String str, Object... objects) {
        sb.append(I18n.tr(str, objects));
        return this;
    }

    public MessageBuilder appendJoin(CharSequence delimiter, CharSequence... elements) {
        sb.append(String.join(delimiter, elements));
        return this;
    }

    public MessageBuilder appendJoin(CharSequence delimiter, Iterable<? extends CharSequence> elements) {
        sb.append(String.join(delimiter, elements));
        return this;
    }

    @Override
    public String toString() {
        return sb.toString();
    }

    public int length() {
        return sb.length();
    }
}
