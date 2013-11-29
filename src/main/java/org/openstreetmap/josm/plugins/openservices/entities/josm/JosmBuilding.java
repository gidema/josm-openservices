package org.openstreetmap.josm.plugins.openservices.entities.josm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.plugins.openservices.crs.GeoUtil;
import org.openstreetmap.josm.plugins.openservices.crs.InvalidGeometryException;
import org.openstreetmap.josm.plugins.openservices.crs.InvalidMultiPolygonException;
import org.openstreetmap.josm.plugins.openservices.entities.BuildException;
import org.openstreetmap.josm.plugins.openservices.entities.builtenvironment.Address;
import org.openstreetmap.josm.plugins.openservices.entities.builtenvironment.Block;
import org.openstreetmap.josm.plugins.openservices.entities.builtenvironment.Building;
import org.openstreetmap.josm.plugins.openservices.entities.builtenvironment.Place;
import org.openstreetmap.josm.plugins.openservices.issue.Issue;
import org.openstreetmap.josm.plugins.openservices.issue.JosmIssue;

import com.vividsolutions.jts.geom.MultiPolygon;

public class JosmBuilding extends JosmEntity implements Building {
    private MultiPolygon multiPolygon;
    private String source = "unknown";
    private String sourceDate;
    private String startDate;
    private String buildingType = "yes";
    private String bagId;
    private boolean underConstruction = false;
    private Set<Address> addresses = new HashSet<Address>();
    private Map<String, String> addressKeys = new HashMap<String, String>();
    private boolean hasAddress = false; // True if this building has address tags

    public JosmBuilding(OsmPrimitive primitive) {
        super(primitive);
    }

    public void build() throws BuildException {
        OsmPrimitive primitive = getPrimitive();
        try {
            buildGeometry();
        } catch (InvalidGeometryException e) {
            Issue issue = new JosmIssue(primitive, e);
            throw new BuildException(issue);
        }
        Iterator<Entry<String, String>> it = primitive.getKeys()
                .entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, String> entry = it.next();
            String key = entry.getKey();
            String value = entry.getValue();
            if ("building".equals(key)) {
                if ("construction".equals(value)) {
                    underConstruction = true;
                    if (primitive.hasKey("construction")) {
                        buildingType = primitive.get("construction");
                    }
                }
                else {
                    buildingType = value;
                }
            }
            else if ("3dshapes:ggmodelk".equals(key) ||
                ("source".equals(key) && "3dShapes".equals(value))) {
                source="3dshapes";
            } 
            else if ("source".equals(key)
                    && value.toUpperCase().startsWith("BAG")) {
                source = "BAG";
                if (value.length() == 11 && value.charAt(6) == '-') {
                    try {
                        String month = value.substring(4, 2);
                        String year = value.substring(7, 4);
                        int m = Integer.parseInt(month);
                        int y = Integer.parseInt(year);
                        sourceDate = String.format("%1$4s-%2$02s", y, m);
                    }
                    finally {};
                }
            }
            else if ("source:date".equals(key)) {
                sourceDate = value;
            }
            else if ("bag:bouwjaar".equals(key) || 
                "start_date".equals(key)) {
                startDate = value;
                if (!"start_date".equals(key)) {
                    primitive.put("start_date",  value);
                    primitive.put(key,  null);
                }
            }
            else if ("ref:bagid".equals(key) || "bag:id".equals(key) ||
                    "ref:bag".equals(key) || "bag:pand_id".equals(key)) {
                bagId = value;
            }
            else if ("bag:extract".equals(key)) {
                sourceDate = parseBagExtract(value);
            }
            else if ("addr:housenumber".equals(key)) {
                JosmAddress address = new JosmAddress(primitive);
                address.build();
                getAddresses().add(address);
                hasAddress = true;
            }
            else if ("address:street".equals(key) ||
                     "address:housename".equals(key) ||
                     "address:city".equals(key) ||
                     "address:postcode".equals(key)) {
                // Save other address keys in case address:housenumber is missing
                addressKeys.put(key, value);
            }
            else {
                getOtherKeys().put(key, value);
            }
        }
    }

    @Override
    public MultiPolygon getGeometry() {
        return multiPolygon;
    }

    @Override
    public Place getPlace() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<Address> getAddresses() {
        return addresses;
    }

    @Override
    public Block getBlock() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getBuildingType() {
        return buildingType;
    }

    @Override
    public boolean isComplete() {
        // TODO check this. The whole area must be downloaded
        return !getPrimitive().isIncomplete();
    }

    @Override
    public String getSource() {
        return source;
    }

    public boolean isUnderConstruction() {
        return underConstruction;
    }

    @Override
    public String getStartDate() {
        return startDate;
    }
    
    public boolean hasAddress() {
        return hasAddress;
    }
    
    private void buildGeometry() throws InvalidGeometryException {
        if (getPrimitive().getDisplayType() == OsmPrimitiveType.CLOSEDWAY) {
            buildGeometry((Way)getPrimitive());
        }
        else if (getPrimitive().getDisplayType() == OsmPrimitiveType.RELATION) {
            buildGeometry((Relation)getPrimitive());
        }
    }

    private void buildGeometry(Way way) throws InvalidMultiPolygonException {
        GeoUtil geoUtil = GeoUtil.getInstance();
        multiPolygon = geoUtil.toMultiPolygon(way);
    }
    
    private void buildGeometry(Relation relation) throws InvalidMultiPolygonException {
        GeoUtil geoUtil = GeoUtil.getInstance();
        multiPolygon = geoUtil.toMultiPolygon(relation);
    }
    
    private String pad(String s, int length, char c) {
        if (s.length() >= length) return s;
        StringBuilder sb = new StringBuilder(length);
        for (int i=s.length(); i<length; i++) {
            sb.append(c);
        }
        sb.append(s);
        return sb.toString();
    }
    
    private String parseBagExtract(String s) {
        if (s.startsWith("9999PND") || s.startsWith("9999LIG") || s.startsWith("9999STA")) {
            StringBuilder sb = new StringBuilder(10);
            sb.append(s.substring(11,15)).append("-").append(s.substring(9,11)).append("-").append(s.substring(7, 9));
            return sb.toString();
        }
        return s;
    }
}
