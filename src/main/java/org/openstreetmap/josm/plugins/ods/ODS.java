package org.openstreetmap.josm.plugins.ods;

public class ODS {
    
    public static class KEY {
        public static String BASE = "|ODS";
        public static String IDMATCH = BASE + ":idMatch";
        public static String STATUS_MATCH = BASE + ":status";
        public static String GEOMETRY_MATCH = BASE + ":geometryMatch";
        public static String ATTRIBUTE_MATCH = BASE + ":attributeMatch";
        
    }
    
    public static String[] KEYS = {KEY.BASE, KEY.IDMATCH, KEY.STATUS_MATCH, 
            KEY.GEOMETRY_MATCH, KEY.ATTRIBUTE_MATCH};

}
