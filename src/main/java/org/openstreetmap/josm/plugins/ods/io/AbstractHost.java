package org.openstreetmap.josm.plugins.ods.io;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.openstreetmap.josm.plugins.ods.OdsConfigurationException;
import org.openstreetmap.josm.plugins.ods.OdsFeatureSource;
import org.openstreetmap.josm.plugins.ods.ServiceException;
import org.openstreetmap.josm.plugins.ods.metadata.MetaData;
import org.openstreetmap.josm.plugins.ods.metadata.MetaDataException;
import org.openstreetmap.josm.plugins.ods.metadata.MetaDataLoader;

import exceptions.OdsException;

public abstract class AbstractHost implements Host {
    private boolean initialized = false;
    private boolean available = false;
    private String name;
    private String type;
    private String uncheckedUrl;
    private URL url;
    private Integer maxFeatures;
    private MetaData metaData;
    private final List<MetaDataLoader> metaDataLoaders = new LinkedList<MetaDataLoader>();

    public AbstractHost(String name, String url, Integer maxFeatures) {
        super();
        this.name = name;
        this.uncheckedUrl = url;
        this.maxFeatures = maxFeatures;
    }

    public boolean isInitialized() {
        return initialized;
    }
    
    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }
    
    /**
     * @see org.openstreetmap.josm.plugins.ods.io.Host#isAvailable()
     */
    @Override
    public boolean isAvailable() {
        return available;
    }

    public final void setAvailable(boolean available) {
        this.available = available;
    }

    /**
     * @throws OdsConfigurationException 
     * @see org.openstreetmap.josm.plugins.ods.io.Host#initialize()
     */
    @Override
    public synchronized void initialize() throws OdsException {
        if (isInitialized()) return;
        setInitialized(true);
        try {
            url = new URL(uncheckedUrl);
        } catch (@SuppressWarnings("unused") MalformedURLException e) {
            setAvailable(false);
            String msg = String.format("Invalid url: %s", uncheckedUrl);
            throw new OdsException(msg);
        }
        metaData = new MetaData();
        for (MetaDataLoader loader : metaDataLoaders) {
            try {
                loader.populateMetaData(metaData);
            } catch (MetaDataException e) {
                setAvailable(false);
                throw new OdsException("Invalid meta data", e);
            }
        }
        return;
    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.io.Host#getName()
     */
    @Override
    public final String getName() {
        return name;
    }

    public final void setName(String name) {
        this.name = name;
    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.io.Host#getUrl()
     */
    @Override
    public final URL getUrl() {
        return url;
    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.io.Host#getType()
     */
    @Override
    public final String getType() {
        return type;
    }

    public final void setType(String type) {
        this.type = type;
    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.io.Host#getMaxFeatures()
     */
    @Override
    public Integer getMaxFeatures() {
        return maxFeatures;
    }

    public void setMaxFeatures(Integer maxFeatures) {
        this.maxFeatures = maxFeatures;
    }

    public void addMetaDataLoader(MetaDataLoader metaDataLoader) {
        metaDataLoaders.add(metaDataLoader);
    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.io.Host#getMetaData()
     */
    @Override
    public MetaData getMetaData() {
        if (metaData == null) {
            metaData = new MetaData();
        }
        return metaData;
    }

    public boolean equals(Host other) {
        return other.getName().equals(name) && other.getType().equals(type)
                && other.getUrl().equals(url);
    }

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.io.Host#hasFeatureType(java.lang.String)
     */
    @Override
    public abstract boolean hasFeatureType(String feature)
            throws ServiceException;

    /* (non-Javadoc)
     * @see org.openstreetmap.josm.plugins.ods.io.Host#getOdsFeatureSource(java.lang.String)
     */
    @Override
    public abstract OdsFeatureSource getOdsFeatureSource(String feature)
            throws ServiceException;

}
