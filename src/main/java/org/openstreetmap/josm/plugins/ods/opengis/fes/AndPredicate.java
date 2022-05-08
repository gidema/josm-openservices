package org.openstreetmap.josm.plugins.ods.opengis.fes;

import java.util.Arrays;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class AndPredicate implements FilterPredicate {
    private final List<FilterPredicate> operands;

    private AndPredicate(List<FilterPredicate> operands) {
        super();
        this.operands = operands;
    }

    public static FilterPredicate of(FilterPredicate ...predicates) {
        return new AndPredicate(Arrays.asList(predicates));
    }
    
    @Override
    public Element buildXmlElement(Document doc) {
        Element andClause = doc.createElement("fes:And");
        operands.forEach(op -> andClause.appendChild(op.buildXmlElement(doc)));
        return andClause;
    }
}
