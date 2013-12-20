package org.openstreetmap.josm.plugins.ods.entities.internal;

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
import org.openstreetmap.josm.plugins.ods.crs.GeoUtil;
import org.openstreetmap.josm.plugins.ods.crs.InvalidGeometryException;
import org.openstreetmap.josm.plugins.ods.crs.InvalidMultiPolygonException;
import org.openstreetmap.josm.plugins.ods.entities.BuildException;
import org.openstreetmap.josm.plugins.ods.entities.Entity;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.AddressNode;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.Block;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.Building;
import org.openstreetmap.josm.plugins.ods.entities.builtenvironment.City;
import org.openstreetmap.josm.plugins.ods.issue.Issue;
import org.openstreetmap.josm.plugins.ods.issue.JosmIssue;

import com.vividsolutions.jts.geom.Geometry;

public class InternalBuilding extends InternalEntity implements Building {
    private Geometry geometry;
    private String source = "unknown";
    private String sourceDate;
    private String startDate;
    private String buildingType = "yes";
    private String bagId;
    private boolean underConstruction = false;
    private Set<AddressNode> addresses = new HashSet<AddressNode>();
    private Map<String, String> addressKeys = new HashMap<>();
    private boolean hasAddress = false; // True if this building has address tags

    public InternalBuilding(OsmPrimitive primitive) {
        super(primitive);
    }

    @Override
    public void setIncomplete(boolean complete) {
        // TODO Auto-generated method stub
    }

    @Override
    public Class<? extends Entity> getType() {
        return Building.class;
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
                        String month = value.substring(4, 6);
                        String year = value.substring(7, 11);
                        int m = Integer.parseInt(month);
                        int y = Integer.parseInt(year);
                        sourceDate = String.format("%1$4d-%2$02d", y, m);
                    }
                    catch (Exception e) {
                        // Something went wrong. Ignore the source date and print the stack trace
                        e.printStackTrace();
                    };
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
                InternalAddressNode address = new InternalAddressNode(primitive);
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

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    @Override
    public Geometry getGeometry() {
        return geometry;
    }

    @Override
    public City getCity() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<AddressNode> getAddresses() {
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
    public boolean isIncomplete() {
        // TODO check this. The whole area must be downloaded
        return getPrimitive().isIncomplete();
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

    private void buildGeometry(Way way) throws IllegalArgumentException {
        GeoUtil geoUtil = GeoUtil.getInstance();
        geometry = geoUtil.toPolygon(way);
    }
    
    private void buildGeometry(Relation relation) throws InvalidMultiPolygonException {
        GeoUtil geoUtil = GeoUtil.getInstance();
        geometry = geoUtil.toMultiPolygon(relation);
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
