package org.openstreetmap.josm.plugins.ods.saxparser.opengis.wfs;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.openstreetmap.josm.plugins.ods.saxparser.api.SaxRootHandler;
import org.openstreetmap.josm.plugins.ods.saxparser.gml.AxisOrder;
import org.openstreetmap.josm.plugins.ods.saxparser.impl.SaxRootHandlerImpl;
import org.openstreetmap.josm.plugins.ods.wfs.WfsFeatureCollection;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class TestFeatureCollectionHandler {
    @Test
    public void runXml() {
//        InputStream is = this.getClass().getResourceAsStream("BagPanden.xml");
        InputStream is = this.getClass().getResourceAsStream("BagVerblijfsobjecten.xml");
        SaxRootHandler rootHandler = new SaxRootHandlerImpl();
        rootHandler.setContextItem(GeometryFactory.class, 
                new GeometryFactory(new PrecisionModel(), 28992));
        
        QName geometryProperty = new QName("http://bag.geonovum.nl", "geom");
        FeatureCollectionHandler featureCollectionHandler = new FeatureCollectionHandler(rootHandler, geometryProperty, AxisOrder.LatLon);
                
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        try {
            InputSource inputSource = new InputSource(is);
            SAXParser saxParser = spf.newSAXParser();
            XMLReader xmlReader = saxParser.getXMLReader();
            xmlReader.setContentHandler(rootHandler);
            xmlReader.parse(inputSource);
         }
        catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException(e);
        }
        
        WfsFeatureCollection featureCollection = featureCollectionHandler.getFeatureCollection();
    }

}
