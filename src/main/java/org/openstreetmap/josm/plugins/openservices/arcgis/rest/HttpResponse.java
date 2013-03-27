package org.openstreetmap.josm.plugins.openservices.arcgis.rest;

import java.io.IOException;
import java.io.InputStream;

public class HttpResponse {
  private final HttpRequest request;
  private final InputStream inputStream;
  
  public HttpResponse(HttpRequest request) throws IOException {
    this.request = request;
    this.inputStream = request.getInputStream();
  }
  
  public InputStream getInputStream() {
    return inputStream;
  }
  
  public void close() {
    if (inputStream != null) {
      try {
        inputStream.close();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    request.close();
  }
}
