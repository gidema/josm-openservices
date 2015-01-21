package org.openstreetmap.josm.plugins.ods;

import org.opengis.filter.Filter;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;

public class DefaultOdsDataSource implements OdsDataSource {
	protected OdsFeatureSource odsFeatureSource;
	private Filter filter;
	private IdFactory idFactory;
	private boolean initialized;
	private boolean required;

	public DefaultOdsDataSource(OdsFeatureSource odsFeatureSource, Filter filter) {
		super();
		this.odsFeatureSource = odsFeatureSource;
		this.filter = filter;
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
    public void setRequired(boolean required) {
        this.required = required;
    }

    @Override
    public boolean isRequired() {
        return required;
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
