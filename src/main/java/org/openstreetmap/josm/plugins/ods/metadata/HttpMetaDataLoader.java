package org.openstreetmap.josm.plugins.ods.metadata;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import nl.gertjanidema.conversion.valuemapper.ValueMapperException;

import org.apache.commons.configuration.ConfigurationException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * Implementation of MetaDataLoader that
 * reads configuration data from a HTML or XML page using JSoup.
 * 
 * This class uses combinations of attribute names and JSoup cssQueries to
 * select arbitrary values from a HTML or XML page.
 * 
 * @author Gertjan Idema
 *
 */
public class HttpMetaDataLoader implements MetaDataLoader {
  private final String url;
  private String method = "GET";
  private Map<String, String> requestData = null;
  private final List<MetaDataAttribute> attributes = 
    new LinkedList<MetaDataAttribute>();
  
  /**
   * Construct an instance from a URL
   * 
   * @param url The url pointing to the desired HTML page
   * @param attributes The attributeName to cssQuery mapping
   * @throws ConfigurationException
   */
  public HttpMetaDataLoader(String url) {
    this(url, "GET", null);
  }
  
  /**
   * Construct a HtmlConfiguration object from a URL, a attributeName to
   * cssQuery mapping, a HTTP method and a Map with query parameters.
   *
   * @param url The url pointing to the desired HTML page
   * @param method The Http method (GET and POST) are supported
   * @param data The data for the POST request, or null for GET requests.
   * @throws ConfigurationException
   */
  public HttpMetaDataLoader(String url, String method, Map<String, String> requestData) {
    this.url = url;
    this.method = method;
    this.requestData = requestData;
  }
  
  public void addAttribute(MetaDataAttribute attribute) {
    attributes.add(attribute);
  }
  
  @Override
  public void populateMetaData(MetaData metaData) throws MetaDataException {
    Document doc;
    try {
  	  Connection conn = Jsoup.connect(url);
  	  conn.timeout(30000);
      if (method.equals("GET")) {
          doc = conn.get();
      }
      else if (method.equals("POST")) {
        doc = conn.data(requestData).post();
      }
      else {
        throw new MetaDataException("Unsupported HTML access method: " + method);
      }
      for (MetaDataAttribute attribute: attributes) {
        Elements elements = doc.select(attribute.getQuery());
        if (elements.size() > 0) {
          String sValue = elements.get(0).text();
          Object oValue = attribute.getValueMapper().parse(sValue);
          metaData.put(attribute.getName(), oValue);
        }
      }
    } catch (UnknownHostException e) {
        URL theUrl;
        try {
            theUrl = new URL(url);
            throw new MetaDataException("The connection to the webserver at " + theUrl.getHost() + " failed. Please try again later.", e);
        } catch (MalformedURLException e1) {
            throw new MetaDataException("The URL " + url + " is malformed. This is probably a configuration error." +
                "Please check your configuration files;", e1);
        }
    } catch (IOException e) {
        throw new MetaDataException(e.getMessage(), e);
    } catch (ValueMapperException e) {
      throw new MetaDataException(e.getMessage(), e);
    }
  }
}
