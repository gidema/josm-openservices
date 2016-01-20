package org.openstreetmap.josm.plugins.ods;

import org.geotools.data.Query;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;

import exceptions.OdsException;

/**
 * @author Gertjan Idema <mail@gertjanidema.nl>
 *
 */
public class DefaultOdsDataSource implements OdsDataSource {
    protected OdsFeatureSource odsFeatureSource;
    private Query query;
    private IdFactory idFactory;
    private boolean initialized;
    private boolean required;

    public DefaultOdsDataSource(OdsFeatureSource odsFeatureSource, Query query) {
        super();
        this.odsFeatureSource = odsFeatureSource;
        this.query = query;
    }

    @Override
    public final OdsFeatureSource getOdsFeatureSource() {
        return odsFeatureSource;
    }
    
    @Override
    public void initialize() throws OdsException {
        if (!initialized) {
            odsFeatureSource.initialize();
            initialized = true;
        }
    }

    @Override
    public void setRequired(boolean required) {
        this.required = required;
    }

    @Override
    public boolean isRequired() {
        return required;
    }

    @Override
    public Query getQuery() {
        return query;
    }

    @Override
    public void setIdFactory(DefaultIdFactory idFactory) {
        this.idFactory = idFactory;
    }

    /**
     * @see org.openstreetmap.josm.plugins.ods.OdsDataSource#getIdFactory()
     */
    @Override
    public IdFactory getIdFactory() {
        if (idFactory == null) {
            idFactory = new DefaultIdFactory(
                    getOdsFeatureSource().getIdAttribute());
        }
        return idFactory;
    }

    @Override
    public String getFeatureType() {
        return odsFeatureSource.getFeatureName();
    }

    @Override
    public MetaData getMetaData() {
        return getOdsFeatureSource().getMetaData();
    }
}
