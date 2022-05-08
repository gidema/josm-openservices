package org.openstreetmap.josm.plugins.ods.wfs;

import java.util.Arrays;
import java.util.List;

import javax.xml.namespace.QName;

import org.openstreetmap.josm.plugins.ods.saxparser.gml.AxisOrder;

public class WfsFeatureSourceBuilder {
    private final WfsHost host;
    private final String featureName;
    private Boolean required = true;
    private List<String> properties = null;
    private List<String> sortBy = null;
    private QName geometryProperty;
    private Long srid;
    private AxisOrder axisOrder;
    private String httpMethod;
    private int pageSize = 0;
    
    public WfsFeatureSourceBuilder(WfsHost host, String featureName) {
        super();
        this.host = host;
        this.featureName = featureName;
        this.httpMethod = host.getHttpMethod();
        this.pageSize = host.getPageSize();
        this.axisOrder = host.getAxisOrder();
        this.srid = host.getSrid();
    }
    
    public void setGeometryProperty(String propertyName) {
        this.geometryProperty = new QName(host.getXmlNs(), propertyName, host.getXmlPrepix());
    }


    public void setProperties(String ... properties) {
        this.properties = Arrays.asList(properties);
    }
    
    public void setRequired(Boolean required) {
        this.required = required;
    }

    public void setSortBy(String ... sortBy ) {
        this.sortBy = Arrays.asList(sortBy);
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }
    
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
    
    public WfsFeatureSource build() {
        String xmlNs = host.getXmlNs();
        String xmlPrefix = host.getXmlPrepix();
        return new WfsFeatureSourceImpl(host, new QName(xmlNs, featureName, xmlPrefix),
                geometryProperty != null ? geometryProperty : host.getGeometryProperty(),
                properties, sortBy,  required, 
                pageSize > 0 ? pageSize : host.getPageSize(),
                httpMethod != null ? httpMethod : host.getHttpMethod(),
                srid != null ? srid : host.getSrid(),
                axisOrder != null ? axisOrder : host.getAxisOrder());
    }
    
    public class WfsFeatureSourceImpl implements WfsFeatureSource {
        private final WfsHost _host;
        private final QName _featureType;
        private final QName _geometryProperty;
        private final List<String> _properties;
        private final List<String> _sortBy;
        private final Boolean _required;
        private final Integer _pageSize;
        private final String _httpMethod;
        private final Long _srid;
        private final AxisOrder _axisOrder;

        public WfsFeatureSourceImpl(WfsHost host, QName featureType,
                QName geometryProperty, List<String> properties,
                List<String> sortBy, Boolean required,
                Integer pageSize, String httpMethod,
                Long srid, AxisOrder axisOrder) {
            super();
            this._host = host;
            this._featureType = featureType;
            this._geometryProperty = geometryProperty;
            this._properties = properties;
            this._sortBy = sortBy;
            this._required = required;
            this._pageSize = pageSize;
            _httpMethod = httpMethod;
            this._srid = srid;
            this._axisOrder = axisOrder;
        }

        @Override
        public WfsHost getHost() {
            return _host;
        }

        @Override
        public QName getFeatureType() {
            return _featureType;
        }

        @Override
        public QName getGeometryProperty() {
            return _geometryProperty;
        }

        @Override
        public List<String> getProperties() {
            return _properties;
        }

        @Override
        public List<String> getSortBy() {
            return _sortBy;
        }

        @Override
        public Boolean isRequired() {
            return _required;
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
        public Long getSrid() {
            return _srid;
        }

        @Override
        public AxisOrder getAxisOrder() {
            return _axisOrder;
        }
    }
}
