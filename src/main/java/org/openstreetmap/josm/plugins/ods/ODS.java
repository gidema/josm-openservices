package org.openstreetmap.josm.plugins.ods;

public class ODS {
    public static ParameterType<String> OPERATION_MODE = new ParameterType<>(String.class);
    
    public static class KEY {
        public static String BASE = "|ODS";
        public static String IDMATCH = BASE + ":idMatch";
        public static String STATUS = BASE + ":status";
        public static String STATUS_MATCH = BASE + ":statusMatch";
        public static String GEOMETRY_MATCH = BASE + ":geometryMatch";
        public static String TAG_MATCH = BASE + ":tagMatch";
        
    }
    
//    public static String[] KEYS = {KEY.BASE, KEY.IDMATCH, KEY.STATUS, KEY.STATUS_MATCH, 
//            KEY.GEOMETRY_MATCH, KEY.TAG_MATCH};

}
