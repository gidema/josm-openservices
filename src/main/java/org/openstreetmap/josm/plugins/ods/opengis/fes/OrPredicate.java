package org.openstreetmap.josm.plugins.ods.opengis.fes;

import java.util.Arrays;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class OrPredicate implements FilterPredicate {
    private final List<FilterPredicate> operands;

    private OrPredicate(List<FilterPredicate> operands) {
        super();
        this.operands = operands;
    }

    public static FilterPredicate of(FilterPredicate ...predicates) {
        return new OrPredicate(Arrays.asList(predicates));
    }
    
    @Override
    public Element buildXmlElement(Document doc) {
        Element andClause = doc.createElement("fes:Or");
        operands.forEach(op -> andClause.appendChild(op.buildXmlElement(doc)));
        return andClause;
    }
}
