package org.openstreetmap.josm.plugins.ods.http;

import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;

import org.openstreetmap.josm.tools.Logging;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class OkHttp3Client implements OdsHttpClient {
    private static OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();

    @Override
    public Reader doXmlPostRequest(String url, String postRequest) throws IOException{
        OkHttpClient client = clientBuilder.build();

        RequestBody formBody = RequestBody.create(postRequest, null);
        Request request = new Request.Builder()
            .url(url)
            .post(formBody)
            .build();
        
        Call call = client.newCall(request);
        try {
            @SuppressWarnings("resource")
            Response response = call.execute();
            Integer responseCode = response.code();
            String contentType = response.header("content-type").split(";")[0];
            if (responseCode != 200) {
                System.err.println("The server returned an unexpexted response witch code " + responseCode.toString());
                System.err.println(getResponseString(response));
                response.close();
                throw new IOException("Http error:" + Integer.toString(responseCode));
            }
            switch (contentType) {
            case "text/xml":
            case "application/gml+xml":
                return new ResponseReader(response);
            default:
                System.err.println("The server returned unexpexted content:\n");
                System.err.println(response.body().string());
                response.close();
                throw new IOException("Unexpected content type: " + contentType);
            }
        }
        catch (IOException e) {
            throw e;
        }
    }
    
    @Override
    public Reader doGetRequest(String url, Map<String, String> queryParameters) throws IOException{
        OkHttpClient client = clientBuilder.build();

        String httpGet = buildHttpGetQuery(url, queryParameters);
        Logging.info("GET " + httpGet);
        Request request = new Request.Builder()
            .url(httpGet)
            .build();
        
        Call call = client.newCall(request);
        try {
            @SuppressWarnings("resource")
            Response response = call.execute();
            Integer responseCode = response.code();
            String contentType = response.header("content-type").split(";")[0];
            if (responseCode != 200) {
                System.err.println("The server returned an unexpexted response witch code " + responseCode.toString());
                System.err.println(getResponseString(response));
                response.close();
                throw new IOException(String.format("Http error: %d for url '%s'.", responseCode,
                        httpGet));
            }
            switch (contentType) {
            case "text/xml":
            case "application/gml+xml":
                return new ResponseReader(response);
            default:
                System.err.println("The server returned unexpexted content:\n");
                System.err.println(response.body().string());
                response.close();
                throw new IOException("Unexpected content type: " + contentType);
            }
        }
        catch (IOException e) {
            throw e;
        }
    }
    
    private static String buildHttpGetQuery(String url, Map<String, String> queryParameters) {
        StringBuilder sb = new StringBuilder();// TODO Auto-generated method stub
        sb.append(url).append('?');
        boolean first = true;
        for (Entry<String, String> entry : queryParameters.entrySet()) {
            try {
                if (first) first = false; else sb.append('&');
                sb.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                sb.append('=');
                sb.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        return sb.toString();
    }
    
    private static String getResponseString(Response response) {
        try (ResponseBody body = response.body();
        ){
            return body.toString();
        }
    }

    public class ResponseReader extends Reader {
        private Response response;
        private Reader reader;
        
        @SuppressWarnings("resource")
        public ResponseReader(Response response) {
            super();
            this.response = response;
            this.reader = response.body().charStream();
        }

        @Override
        public int read(char[] cbuf, int off, int len) throws IOException {
            return reader.read(cbuf, off, len);
        }

        @Override
        public void close() throws IOException {
            if (reader != null) reader.close();
            if (response != null) response.close();
        }
    }
}
