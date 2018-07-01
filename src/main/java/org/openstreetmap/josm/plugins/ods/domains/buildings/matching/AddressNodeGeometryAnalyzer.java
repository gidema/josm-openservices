package org.openstreetmap.josm.plugins.ods.domains.buildings.matching;

import java.util.Objects;

import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.plugins.ods.ODS;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OdAddressNode;
import org.openstreetmap.josm.plugins.ods.domains.buildings.OsmAddressNode;
import org.openstreetmap.josm.plugins.ods.entities.Deviation;
import org.openstreetmap.josm.plugins.ods.entities.impl.ZeroOneMany;
import org.openstreetmap.josm.plugins.ods.matching.AddressNodeMatch;

public class AddressNodeGeometryAnalyzer {
    final static String DIFF_KEY = ODS.KEY.DIFF_KEY + "geometry";

    void analyze(OsmAddressNode osmNode) {
        AddressNodeMatch match = osmNode.getMatch();
        if (match == null) {
            osmNode.removeDeviation(GeometryDeviation.class);
            return;
        }
        ZeroOneMany<OdAddressNode> odAddressNodes = match.getOpenDataEntities();
        if (odAddressNodes.isOne()) {
            OdAddressNode odNode = odAddressNodes.getOne();
            if (!Objects.equals(osmNode.getBuilding().getBuildingId(),
                    odNode.getBuilding().getBuildingId())) {
                Deviation deviation = new GeometryDeviation();
                osmNode.addDeviation(deviation);
                osmNode.getPrimitive().put(DIFF_KEY, "yes");
            }
        }
    }

    public class GeometryDeviation implements Deviation {

        public GeometryDeviation() {
            super();
        }

        @Override
        public boolean isFixable() {
            return false;
        }

        @Override
        public Command getFix() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clearOdsTags() {
            // TODO Auto-generated method stub
        }
    }
}
