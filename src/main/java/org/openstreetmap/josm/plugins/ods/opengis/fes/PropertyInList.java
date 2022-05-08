package org.openstreetmap.josm.plugins.ods.opengis.fes;

import java.util.Arrays;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class PropertyInList implements FilterPredicate {
    private String propertyName;
    private List<String> values;
    
    public PropertyInList(String propertyName, List<String> values) {
        super();
        this.propertyName = propertyName;
        this.values = values;
    }
    
    public static FilterPredicate of(String propertyName, List<String> values) {
        return new PropertyInList(propertyName, values);
    }
    
    public static FilterPredicate of(String propertyName, String ... values) {
        return new PropertyInList(propertyName, Arrays.asList(values));
    }
    
    @Override
    public Element buildXmlElement(Document doc) {
        if (values.size() == 1) {
            return buildPropertySelector(doc, values.get(0));
        }
        Element or = doc.createElement("fes:Or");
        values.forEach(value -> or.appendChild(buildPropertySelector(doc, value)));
        return or;
    }

    private Element buildPropertySelector(Document doc, String value) {
        Element selector = doc.createElement("fes:PropertyIsEqualTo");
        Element reference = doc.createElement("fes:ValueReference");
        reference.appendChild(doc.createTextNode(propertyName));
        Element literal =  doc.createElement("fes:Literal");
        literal.appendChild(doc.createTextNode(value));
        selector.appendChild(reference);
        selector.appendChild(literal);
        return selector;
    }
}
