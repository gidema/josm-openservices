package org.openstreetmap.josm.plugins.ods.domains.buildings.impl;

import java.util.List;
import java.util.Set;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.ods.domains.buildings.BuildingType;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddressNode;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdBuilding;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdBuildingUnit;
import org.openstreetmap.josm.plugins.ods.entities.EntityStatus;
import org.openstreetmap.josm.plugins.ods.entities.OdEntity;
import org.openstreetmap.josm.plugins.ods.entities.OsmEntity;
import org.openstreetmap.josm.plugins.ods.entities.impl.ZeroOneMany;
import org.openstreetmap.josm.plugins.ods.io.DownloadResponse;
import org.openstreetmap.josm.plugins.ods.matching.Match;

import com.vividsolutions.jts.geom.Geometry;

public class DefaultOdBuildingUnit implements OdBuildingUnit {

    @Override
    public DownloadResponse getDownloadResponse() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setDownloadResponse(DownloadResponse response) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setPrimaryId(Object primaryId) {
        // TODO Auto-generated method stub

    }

    @Override
    public Object getPrimaryId() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setSource(String source) {
        // TODO Auto-generated method stub

    }

    @Override
    public String getSource() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setSourceDate(String sourceDate) {
        // TODO Auto-generated method stub

    }

    @Override
    public String getSourceDate() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setCompleteness(Completeness completeness) {
        // TODO Auto-generated method stub

    }

    @Override
    public Completeness getCompleteness() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setStatus(EntityStatus status) {
        // TODO Auto-generated method stub

    }

    @Override
    public EntityStatus getStatus() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Long getPrimitiveId() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Geometry getGeometry() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setGeometry(Geometry geometry) {
        // TODO Auto-generated method stub

    }

    @Override
    public Match<? extends OsmEntity, ? extends OdEntity> getMatch() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public OsmPrimitive getPrimitive() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setPrimitive(OsmPrimitive primitive) {
        // TODO Auto-generated method stub

    }

    @Override
    public Long getBuildingUnitId() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setBuildingUnitId(Long id) {
        // TODO Auto-generated method stub

    }

    @Override
    public Set<Long> getBuildingIds() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addBuilding(OdBuilding building) {
        // TODO Auto-generated method stub

    }

    @Override
    public ZeroOneMany<OdBuilding> getBuildings() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<OdAddressNode> getAddressNodes() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public double getArea() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public BuildingType getBuildingType() {
        // TODO Auto-generated method stub
        return null;
    }

}
