package org.openstreetmap.josm.plugins.ods.wfs;

import java.io.IOException;
import java.io.Reader;
import java.net.SocketTimeoutException;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.openstreetmap.josm.plugins.ods.context.OdsContext;
import org.openstreetmap.josm.plugins.ods.crs.CRSUtil;
import org.openstreetmap.josm.plugins.ods.http.OdsHttpClient;
import org.openstreetmap.josm.plugins.ods.http.OkHttp3Client;
import org.openstreetmap.josm.plugins.ods.saxparser.api.SaxRootHandler;
import org.openstreetmap.josm.plugins.ods.saxparser.impl.SaxRootHandlerImpl;
import org.openstreetmap.josm.plugins.ods.saxparser.opengis.wfs.FeatureCollectionHandler;
import org.openstreetmap.josm.plugins.ods.wfs.query.WfsRequest;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;


/**
 * Default implementation of the WfsPageReader class.
 * 
 * @See GtPageReader
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class DefaultWFSPageReader implements WfsPageReader {
    private static OdsHttpClient httpClient = new OkHttp3Client();
    private final WfsFeatureSource featureSource;
    private final OdsContext context;
    private final String method;
    
    public DefaultWFSPageReader(WfsFeatureSource featureSource, OdsContext context) {
        super();
        this.featureSource = featureSource;
        this.context = context;
        this.method = featureSource.getHttpMethod();
    }

    @Override
    public WfsFeatureCollection read(WfsRequest request) throws IOException {
        WfsFeatureCollection features;
        if (method.equals("POST")) {
            features = readWithPost(request);
        }
        else {
            features = readWithGet(request);
        }
        if (features == null) return features;
        CRSUtil crsUtil = context.getComponent(CRSUtil.class);
        return features.transform(crsUtil, CRSUtil.OSM_SRID);
    }

    private WfsFeatureCollection readWithPost(WfsRequest request) {
        String postData = request.getPostData();
        
        SaxRootHandler rootHandler = new SaxRootHandlerImpl();
        rootHandler.setContextItem(GeometryFactory.class, 
                new GeometryFactory(new PrecisionModel()));
        
        FeatureCollectionHandler featureCollectionHandler = new FeatureCollectionHandler(rootHandler, featureSource.getGeometryProperty(), featureSource.getAxisOrder());
                
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        try (
            Reader responseReader = httpClient.doXmlPostRequest(featureSource.getHost().getUrl().toString(), postData);
        ) {
            InputSource inputSource = new InputSource(responseReader);
            SAXParser saxParser = spf.newSAXParser();
            XMLReader xmlReader = saxParser.getXMLReader();
            xmlReader.setContentHandler(rootHandler);
            xmlReader.parse(inputSource);
         }
        catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException(e);
        }
        
        return featureCollectionHandler.getFeatureCollection();
    }
    
    private WfsFeatureCollection readWithGet(WfsRequest request) throws IOException {
        Map<String, String> queryParameters = request.getQueryParameters();
        SaxRootHandler rootHandler = new SaxRootHandlerImpl();
        rootHandler.setContextItem(GeometryFactory.class, 
                new GeometryFactory(new PrecisionModel()));
        
        FeatureCollectionHandler featureCollectionHandler = new FeatureCollectionHandler(rootHandler, featureSource.getGeometryProperty(), featureSource.getAxisOrder());
                
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        try (
            Reader responseReader = httpClient.doGetRequest(featureSource.getHost().getUrl().toString(), queryParameters);
        ) {
            InputSource inputSource = new InputSource(responseReader);
            SAXParser saxParser = spf.newSAXParser();
            XMLReader xmlReader = saxParser.getXMLReader();
            xmlReader.setContentHandler(rootHandler);
            xmlReader.parse(inputSource);
         }
        catch (SocketTimeoutException e) {
            throw new IOException(String.format("A timeout occured in the connection to this server: '%s'.", featureSource.getHost().getUrl()));
        }
        catch (ParserConfigurationException | SAXException | IOException e) {
            throw new IOException(e);
        }
        
        return featureCollectionHandler.getFeatureCollection();
    }
}
