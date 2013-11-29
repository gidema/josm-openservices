package org.openstreetmap.josm.plugins.ods.tags;

import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.opengis.feature.Feature;
import org.opengis.filter.expression.Expression;

public class ExpressionTagBuilder implements TagBuilder {
  private final String key;
  private final Expression expression;
  
  public ExpressionTagBuilder(String key, String expression) throws ConfigurationException {
    super();
    this.key = key;
    try {
      this.expression = CQL.toExpression(expression);
    } catch (CQLException e) {
      throw new ConfigurationException(e);
    }
  }

  @Override
  public void createTag(Map<String, String> tags, Feature feature) {
    Object o = expression.evaluate(feature);
    tags.put(key, o.toString());
  }

}
