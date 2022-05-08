package org.openstreetmap.josm.plugins.ods.wfs;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

import javax.xml.namespace.QName;

import org.openstreetmap.josm.plugins.ods.InitializationException;
import org.openstreetmap.josm.plugins.ods.ParameterType;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;
import org.openstreetmap.josm.plugins.ods.saxparser.gml.AxisOrder;

public class WfsHostBuilder {
    private final String name;
    private final String url;
    private final String xmlNs;
    private final String xmlPrefix;
    private String geometryProperty;
    private final Long srid;
    private String wfsVersion = "2.0.0";
    private boolean fesFilterCapable = true;
    private Integer pageSize = 0;
    private String httpMethod = "POST";
    private AxisOrder axisOrder = AxisOrder.XY;
    private Integer timeOut;

    public WfsHostBuilder(String name, String url, String xmlNs, String xmlPrefix,
            String geometryProperty, Long srid, Integer timeOut) {
        super();
        this.name = name;
        this.url = url;
        this.xmlNs = xmlNs;
        this.xmlPrefix = xmlPrefix;
        this.geometryProperty = geometryProperty;
        this.srid = srid;
        this.timeOut = timeOut;
    }

    public void setWfsVersion(String wfsVersion) {
        this.wfsVersion = wfsVersion;
    }

    public void setFesFilterCapable(boolean fesFilterCapable) {
        this.fesFilterCapable = fesFilterCapable;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public void setAxisOrder(AxisOrder axisOrder) {
        this.axisOrder = axisOrder;
    }
    
    public WfsHost build() {
        Objects.requireNonNull(name);
        Objects.requireNonNull(url);
        Objects.requireNonNull(xmlNs);
        Objects.requireNonNull(xmlPrefix);
        Objects.requireNonNull(geometryProperty);
        Objects.requireNonNull(srid);
        Objects.requireNonNull(axisOrder);
        QName gp = new QName(xmlNs, geometryProperty, xmlPrefix);
        URL _url;
        try {
            _url = new URL(url);
        return new WfsHostImpl(name, _url, xmlNs, xmlPrefix,
                wfsVersion, fesFilterCapable, 
                pageSize, httpMethod, gp,
                srid, axisOrder);
        } catch (MalformedURLException e) {
            throw new RuntimeException(String.format("The url is not valid: '%s'", url));
        }
    }

    public static class WfsHostImpl implements WfsHost {
        public final static ParameterType<String> WFS_VERSION = ParameterType
                .STRING();
        public final static ParameterType<Boolean> FES_FILTER_CAPABLE = ParameterType
                .BOOLEAN();

        private final String _name;
        private final URL _url;
        private final String _wfsVersion;
        private final boolean _fesFilterCapable;
        private final String _xmlNs;
        private final String _xmlPrefix;
        private final Integer _pageSize;
        private final String _httpMethod;
        private final QName _geometryProperty;
        private final Long _srid;
        private final AxisOrder _axisOrder;

        private Boolean initialized = false;

        public WfsHostImpl(String name, URL url, String xmlNs, String xmlPrefix,
                String wfsVersion, boolean fesFilterCapable, 
                Integer pageSize, String httpMethod, QName geometryProperty,
                Long srid, AxisOrder axisOrder) {
            super();
            this._name = name;
            this._url = url;
            this._wfsVersion = wfsVersion;
            this._fesFilterCapable = fesFilterCapable;
            this._xmlNs = xmlNs;
            this._xmlPrefix = xmlPrefix;
            this._pageSize = pageSize;
            this._httpMethod = httpMethod;
            this._geometryProperty = geometryProperty;
            this._srid = srid;
            this._axisOrder = axisOrder;
        }

        @Override
        public String getName() {
            return _name;
        }

        @Override
        public URL getUrl() {
            return _url;
        }

        @Override
        public boolean isFesFilterCapable() {
            return _fesFilterCapable;
        }

        @Override
        public MetaData getMetaData() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public boolean isInitialized() {
            return initialized;
        }

        @Override
        public synchronized void initialize() throws InitializationException {
            // try {
            // if (!isInitialized()) {
            // if (wfsVersion == null) {
            // // Check the WFS version that is negotiated with the server
            // // TODO
            // WFSDataStore dataStore = createDataStore(initTimeout);
            // wfsVersion = new Version(dataStore.getVersion());
            // }
            // this.initialized = true;
            // }
            // } catch (IOException e) {
            // throw new InitializationException(e);
            // }
            // return;
        }

        @Override
        public String getXmlNs() {
            return _xmlNs;
        }

        @Override
        public String getXmlPrepix() {
            return _xmlPrefix;
        }

        @Override
        public Integer getPageSize() {
            return _pageSize;
        }

        @Override
        public String getHttpMethod() {
            return _httpMethod;
        }

        @Override
        public QName getGeometryProperty() {
            return _geometryProperty;
        }

        @Override
        public Long getSrid() {
            return _srid;
        }

        @Override
        public AxisOrder getAxisOrder() {
            return _axisOrder;
        }
    }
}
