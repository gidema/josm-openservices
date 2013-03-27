package org.openstreetmap.josm.plugins.openservices.arcgis.rest;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

public class HttpRequest {
  private String method = "GET";
  private String url;
  private final Map<String, String> parameters = new HashMap<String, String>();
  private final boolean asynchronous = false;
  private boolean acceptCompression = true;
  private int connectTimeout = 15;
  private final String user = null;
  private final String password = null;
  private boolean cancel;
  private HttpURLConnection connection;
  private HttpResponse response;

  public HttpRequest() {
    super();
  }
  
  public void setAcceptCompression(boolean acceptCompression) {
    this.acceptCompression = acceptCompression;
  }

  public void setConnectTimeout(int connectTimeout) {
    this.connectTimeout = connectTimeout;
  }

  public void addParameter(String key, String value) {
    parameters.put(key, value);
  }
  
  public void open(String method, String url) throws IOException {
    this.method = method;
    this.url = url;
  }
  
  public void abort() {
    if (connection != null) {
      connection.disconnect();
    }
  }
  
  public HttpResponse send() throws IOException {
    return send(null);
  }
  
  public HttpResponse send(String postData) throws IOException {
    InputStream is = null;
    try {
      URL requestURL = buildUrl();
      // TODO add logger
      System.out.println("Get " + requestURL.toString());
      connection = (HttpURLConnection) requestURL.openConnection();
      connection.setRequestMethod(method);
      // see
      // http://www.tikalk.com/java/forums/httpurlconnection-disable-keep-alive
      connection.setRequestProperty("Connection", "close");
      if (acceptCompression) {
        connection.setRequestProperty("Accept-Encoding", "gzip, deflate");
      }
      connection.setConnectTimeout(connectTimeout * 1000);
      connection.setDoOutput(true);
      if (method.equals("POST")) {
        connection.setDoInput(true);
        connection.setRequestProperty("Content-Type", 
          "application/x-www-form-urlencoded");
        connection.setRequestProperty("Content-Length", "" + 
          Integer.toString(postData.getBytes().length));
        DataOutputStream wr = new DataOutputStream (
            connection.getOutputStream ());
        wr.writeBytes (postData);
        wr.flush ();
        wr.close ();
      }
      return new HttpResponse(this);
    }
    catch (Exception e) {
      if (connection != null) {
        connection.disconnect();
      }
      e.printStackTrace();
      return null;
    }
  }
  
  public void close() {
    if (connection != null) {
      connection.disconnect();
    }
  }
  
  private URL buildUrl() throws IOException {
    StringBuilder sb = new StringBuilder(1000);
    sb.append(url.toString()).append('?');
    boolean first = true;
    for (Entry<String, String> parameter : parameters.entrySet()) {
      if (first) {
        first = false;
      }
      else {
        sb.append('&');
      }
      sb.append(parameter.getKey());
      sb.append('=');
      sb.append(encode(parameter.getValue()));
    }
    try {
      return new URL(sb.toString());
    } catch (MalformedURLException e) {
      throw new IOException(e.getMessage());
    }
  }
  
  public InputStream getInputStream() throws IOException {
    connection.connect();
    if (connection.getResponseCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
      throw new IOException("Connection refused");
    }
    if (connection.getResponseCode() == HttpURLConnection.HTTP_PROXY_AUTH) {
      throw new IOException("Connection cancelled");
    }
    String encoding = connection.getContentEncoding();
    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
      String errorHeader = connection.getHeaderField("Error");
      String errorMessage = getErrorMessage(encoding);
      throw new IOException(connection.getResponseCode() +
              errorHeader + errorMessage);
    }
    return FixEncoding(connection.getInputStream(), encoding);
  } 
  
  private String getErrorMessage(String encoding) {
    StringBuilder errorBody = new StringBuilder();
    try
    {
      InputStream i = FixEncoding(connection.getErrorStream(), encoding);
      if (i != null) {
        BufferedReader in = new BufferedReader(new InputStreamReader(i));
        String s;
        while ((s = in.readLine()) != null) {
          errorBody.append(s);
          errorBody.append("\n");
        }
      }
    } catch (Exception e) {
      errorBody.append("Reading error text failed.");
    }
    return errorBody.toString();
  }

//  protected abstract void processResult(InputStream is) throws IOException;
  
  private String encode(Object o) {
    if (o == null) return "";
    return o.toString();
  }
  
  private String encode(String s) {
    if (s == null) return "";
    try {
      return URLEncoder.encode(s, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return "";
    }
  }

  private static InputStream FixEncoding(InputStream is, String encoding)
      throws IOException
  {
    InputStream stream = is;
    if (encoding != null && encoding.equalsIgnoreCase("gzip")) {
      stream = new GZIPInputStream(is);
    }
    else if (encoding != null && encoding.equalsIgnoreCase("deflate")) {
      stream = new InflaterInputStream(is, new Inflater(true));
    }
    return stream;
  }
}
