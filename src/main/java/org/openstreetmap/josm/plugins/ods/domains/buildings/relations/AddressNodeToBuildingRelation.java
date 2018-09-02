package org.openstreetmap.josm.plugins.ods.domains.buildings.relations;

import java.util.HashSet;
import java.util.Objects;

@SuppressWarnings("serial")
public class AddressNodeToBuildingRelation extends HashSet<AddressNodeToBuildingRelation.Tuple> {
    public static class Tuple {
        private final Long addressNodeId;
        private final Long buildingId;

        public Tuple(Long addressNodeId, Long buildingId) {
            super();
            this.addressNodeId = addressNodeId;
            this.buildingId = buildingId;
        }

        public Long getAddressNodeId() {
            return addressNodeId;
        }

        public Long getBuildingId() {
            return buildingId;
        }

        @Override
        public int hashCode() {
            return Objects.hash(addressNodeId, buildingId);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || !(obj instanceof Tuple)) {
                return false;
            }
            Tuple other = (Tuple) obj;
            return Objects.equals(other.buildingId, this.buildingId) &&
                    Objects.equals(other.addressNodeId, this.addressNodeId);
        }
    }
}
