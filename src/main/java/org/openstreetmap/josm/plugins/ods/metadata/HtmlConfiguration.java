package org.openstreetmap.josm.plugins.ods.metadata;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * Implementation of org.apache.commons.configuration.Configuration that
 * reads configuration data from a HTML page using JSoup.
 * 
 * This class uses combinations of attribute names and JSoup cssQueries to
 * select arbitrary values from a HTML page.
 * 
 * @author Gertjan Idema
 *
 */
public class HtmlConfiguration extends BaseConfiguration {  
  
  /**
   * Construct a HtmlConfiguration object from a URL and a attributeName to
   * cssQuery mapping.
   * 
   * If a value is not found, it is ignored.
   * If a query results in multiple values. Only the first one is returned. 
   * @param url The url pointing to the desired HTML page
   * @param attributes The attributeName to cssQuery mapping
   * @throws ConfigurationException
   */
  public HtmlConfiguration(String url, Map<String, String> attributes) throws ConfigurationException {
    this(url, attributes, "GET", null);
  }
  
  /**
   * Construct a HtmlConfiguration object from a URL, a attributeName to
   * cssQuery mapping, a HTTP method and a Map with query parameters.
   *
   * If a value is not found, it is ignored.
   * If a query results in multiple values. Only the first one is returned. 
   * @param url The url pointing to the desired HTML page
   * @param attributes The attributeName to cssQuery mapping
   * @param method The Http method (GET and POST) are supported
   * @param data The data for the POST request, or null for GET requests.
   * @throws ConfigurationException
   */
  public HtmlConfiguration(String url, Map<String, String> attributes, String method, Map<String, String> data) throws ConfigurationException {
    Document doc;
    try {
      if (method.equals("GET")) {
        doc = Jsoup.connect(url).get();
      }
      else if (method.equals("POST")) {
        doc = Jsoup.connect(url).data(data).post();
      }
      else {
        throw new ConfigurationException("Unsupported HTML access method: " + method);
      }
      for (Entry<String, String> attribute: attributes.entrySet()) {
        Elements elements = doc.select(attribute.getValue());
        if (elements.size() > 0) {
          String value = elements.get(0).text();
          this.addProperty(attribute.getKey(), value);
        }
      }
    } catch (IOException e) {
      throw new ConfigurationException(e);
    }
  }

}
