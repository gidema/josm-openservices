package org.openstreetmap.josm.plugins.ods;

import java.net.URL;

import org.openstreetmap.josm.plugins.ods.metadata.MetaData;

public interface Host {
    public static ParameterType<String> HOST_TYPE = new ParameterType<>(String.class);
    public static ParameterType<String> HOST_NAME = new ParameterType<>(String.class);
    public static ParameterType<String> BASE_URL = new ParameterType<>(String.class);
    // MAX_FEATURES is the maximal number of feature that a WFS service will return in 1
    // request.
    public static ParameterType<Integer> MAX_FEATURES = new ParameterType<>(Integer.class);
    // PAGE_SIZE can be used to trigger the collection of features in batches.
    public static ParameterType<Integer> PAGE_SIZE = new ParameterType<>(Integer.class);

    //    private String name;
    //    private String type;
    //    private final String uncheckedUrl;
    //    private URL url;
    //    private Integer maxFeatures;
    //    private MetaData metaData;
    //    private final List<MetaDataLoader> metaDataLoaders = new LinkedList<>();
    //    private Boolean initialized = false;
    //
    //    public Host(String name, String uncheckedUrl, Integer maxFeatures) {
    //        super();
    //        this.name = name;
    //        this.uncheckedUrl = uncheckedUrl;
    //        this.maxFeatures = maxFeatures;
    //    }

    public String getName();

    public URL getUrl();

    //    public final void setUrl(String url) {
    //        this.url = url;
    //    }

    public String getType();

    public Integer getMaxFeatures();

    //    public void addMetaDataLoader(MetaDataLoader metaDataLoader) {
    //        metaDataLoaders.add(metaDataLoader);
    //    }

    public MetaData getMetaData();

    //    public final void setInitialized(boolean initialized) {
    //        this.initialized = initialized;
    //    }

    public boolean isInitialized();

    //    public boolean equals(Host other) {
    //        return other.getName().equals(name)
    //                && other.getType().equals(type)
    //                && other.getUrl().equals(url);
    //    }

    public void initialize() throws InitializationException;
    //    {
    //        if (initialized) return;
    //        try {
    //            this.url = new URL(uncheckedUrl);
    //        } catch (MalformedURLException e) {
    //            throw new InitializationException(e);
    //        }
    //        metaData = new MetaData();
    //        List<Exception> exceptions = new LinkedList<>();
    //        for (MetaDataLoader loader : metaDataLoaders) {
    //            try {
    //                loader.populateMetaData(metaData);
    //            } catch (MetaDataException e) {
    //                exceptions.add(e);
    //            }
    //        }
    //        if (!exceptions.isEmpty()) {
    //            StringBuilder sb = new StringBuilder();
    //            sb.append(
    //                    "One or more error occured while initializing the dowload job(s):");
    //            for (Exception e : exceptions) {
    //                sb.append("\n").append(e.getMessage());
    //            }
    //            throw new InitializationException(sb.toString());
    //        }
    //        initialized = true;
    //    }

    //    public abstract boolean hasFeatureType(String feature)
    //            throws ServiceException;

    public OdsFeatureSource getOdsFeatureSource(String feature)
            throws ServiceException;

}
