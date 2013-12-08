package org.openstreetmap.josm.plugins.ods;

import org.apache.commons.configuration.ConfigurationException;
import org.opengis.filter.Filter;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;

public abstract class AbstractOdsDataSource implements OdsDataSource {
	protected OdsFeatureSource odsFeatureSource;
	private Filter filter;
	private IdFactory idFactory;
	private boolean initialized;
	private String entityType;

	protected AbstractOdsDataSource(OdsFeatureSource odsFeatureSource) {
		super();
		this.odsFeatureSource = odsFeatureSource;
	}

	@Override
	public final OdsFeatureSource getOdsFeatureSource() {
		return odsFeatureSource;
	}

	public void initialize() throws InitializationException {
		if (!initialized) {
			odsFeatureSource.initialize();
			initialized = true;
		}
	}

	@Override
	public void setEntityType(String entityType) {
	    this.entityType = entityType;
	}
	
	public String getEntityType() {
        return entityType;
    }

    @Override
	public void setFilter(Filter filter) throws ConfigurationException {
		this.filter = filter;
	}

	@Override
	public Filter getFilter() {
		return filter;
	}

	@Override
	public void setIdFactory(DefaultIdFactory idFactory) {
		this.idFactory = idFactory;
	}

	@Override
	public IdFactory getIdFactory() {
		if (idFactory == null) {
			idFactory = new DefaultIdFactory(this);
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
