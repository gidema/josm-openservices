package org.openstreetmap.josm.plugins.ods;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.openstreetmap.josm.plugins.ods.metadata.MetaData;
import org.openstreetmap.josm.plugins.ods.metadata.MetaDataException;
import org.openstreetmap.josm.plugins.ods.metadata.MetaDataLoader;
import org.openstreetmap.josm.tools.Logging;

public abstract class Host {
    private String name;
    private String type;
    private final String uncheckedUrl;
    private URL url;
    private Integer maxFeatures;
    private MetaData metaData;
    private final List<MetaDataLoader> metaDataLoaders = new LinkedList<>();
    private Boolean initialized = false;

    public Host(String name, String uncheckedUrl, Integer maxFeatures) {
        super();
        this.name = name;
        this.uncheckedUrl = uncheckedUrl;
        this.maxFeatures = maxFeatures;
    }

    public final String getName() {
        return name;
    }

    public final void setName(String name) {
        this.name = name;
    }

    public final URL getUrl() {
        if (this.url == null) {
            try {
                this.url = new URL(uncheckedUrl);
            } catch (MalformedURLException e) {
                Logging.error("Invalid url: {0}.", uncheckedUrl);
                throw new RuntimeException("Invalid url'");
            }
        }
        return this.url;
    }

    //    public final void setUrl(String url) {
    //        this.url = url;
    //    }

    public final String getType() {
        return type;
    }

    public final void setType(String type) {
        this.type = type;
    }

    public Integer getMaxFeatures() {
        return maxFeatures;
    }

    public void setMaxFeatures(Integer maxFeatures) {
        this.maxFeatures = maxFeatures;
    }

    public void addMetaDataLoader(MetaDataLoader metaDataLoader) {
        metaDataLoaders.add(metaDataLoader);
    }

    public MetaData getMetaData() {
        if (metaData == null) {
            metaData = new MetaData();
        }
        return metaData;
    }

    public final void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public final boolean isInitialized() {
        return initialized;
    }

    public boolean equals(Host other) {
        return other.getName().equals(name)
                && other.getType().equals(type)
                && other.getUrl().equals(url);
    }

    public synchronized void initialize() throws InitializationException {
        if (initialized) return;
        try {
            this.url = new URL(uncheckedUrl);
        } catch (MalformedURLException e) {
            throw new InitializationException(e);
        }
        metaData = new MetaData();
        List<Exception> exceptions = new LinkedList<>();
        for (MetaDataLoader loader : metaDataLoaders) {
            try {
                loader.populateMetaData(metaData);
            } catch (MetaDataException e) {
                exceptions.add(e);
            }
        }
        if (!exceptions.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append(
                    "One or more error occured while initializing the dowload job(s):");
            for (Exception e : exceptions) {
                sb.append("\n").append(e.getMessage());
            }
            throw new InitializationException(sb.toString());
        }
        initialized = true;
    }

    //    public abstract boolean hasFeatureType(String feature)
    //            throws ServiceException;

    public abstract OdsFeatureSource getOdsFeatureSource(String feature)
            throws ServiceException;

}
