package org.openstreetmap.josm.plugins.ods.entities.builtenvironment;

import static org.openstreetmap.josm.gui.help.HelpUtil.ht;
import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.event.ActionEvent;
import java.util.Collection;

import javax.swing.JOptionPane;

import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.gui.Notification;
import org.openstreetmap.josm.plugins.ods.crs.UnclosedWayException;
import org.openstreetmap.josm.plugins.ods.jts.GeoUtil;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;

/**
 * Creates a building passage for a highway crossing a building
 * 
 * @author Gertjan Idema <mail@gertjanidema.nl>
 * 
 */
public class BuildingPassageAction extends JosmAction {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private static final GeoUtil geoUtil = GeoUtil.getInstance();


    /**
     * Constructs a new {@code CombineWayAction}.
     */
    public BuildingPassageAction() {
        super(
                tr("Building passage"),
                null,
                tr("Create a tunnel=building_building passage for a highway crossing a building."),
                null, true);
        putValue("help", ht("/Action/BuildingPassage"));
    }

    // protected static void warnCombiningImpossible() {
    // String msg = tr("Could not combine ways<br>"
    // + "(They could not be merged into a single string of nodes)");
    // new Notification(msg)
    // .setIcon(JOptionPane.INFORMATION_MESSAGE)
    // .show();
    // return;
    // }

    // protected static Way getTargetWay(Collection<Way> combinedWays) {
    // // init with an arbitrary way
    // Way targetWay = combinedWays.iterator().next();
    //
    // // look for the first way already existing on
    // // the server
    // for (Way w : combinedWays) {
    // targetWay = w;
    // if (!w.isNew()) {
    // break;
    // }
    // }
    // return targetWay;
    // }

//    /**
//     * @param ways
//     * @return null if ways cannot be combined. Otherwise returns the combined
//     *         ways and the commands to combine
//     * @throws UserCancelException
//     */
//    public static Pair<Way, Command> combineWaysWorker(Collection<Way> ways)
//            throws UserCancelException {
//
//        // prepare and clean the list of ways to combine
//        //
//        if (ways == null || ways.isEmpty())
//            return null;
//        ways.remove(null); // just in case - remove all null ways from the
//                           // collection
//
//        // remove duplicates, preserving order
//        ways = new LinkedHashSet<Way>(ways);
//
//        // try to build a new way which includes all the combined
//        // ways
//        //
//        NodeGraph graph = NodeGraph.createUndirectedGraphFromNodeWays(ways);
//        List<Node> path = graph.buildSpanningPath();
//        if (path == null) {
//            warnCombiningImpossible();
//            return null;
//        }
//        // check whether any ways have been reversed in the process
//        // and build the collection of tags used by the ways to combine
//        //
//        TagCollection wayTags = TagCollection.unionOfAllPrimitives(ways);
//
//        List<Way> reversedWays = new LinkedList<Way>();
//        List<Way> unreversedWays = new LinkedList<Way>();
//        for (Way w : ways) {
//            // Treat zero or one-node ways as unreversed as Combine action
//            // action is a good way to fix them (see #8971)
//            if (w.getNodesCount() < 2
//                    || (path.indexOf(w.getNode(0)) + 1) == path.lastIndexOf(w
//                            .getNode(1))) {
//                unreversedWays.add(w);
//            } else {
//                reversedWays.add(w);
//            }
//        }
//        // reverse path if all ways have been reversed
//        if (unreversedWays.isEmpty()) {
//            Collections.reverse(path);
//            unreversedWays = reversedWays;
//            reversedWays = null;
//        }
//        if ((reversedWays != null) && !reversedWays.isEmpty()) {
//            if (!confirmChangeDirectionOfWays())
//                return null;
//            // filter out ways that have no direction-dependent tags
//            unreversedWays = ReverseWayTagCorrector
//                    .irreversibleWays(unreversedWays);
//            reversedWays = ReverseWayTagCorrector
//                    .irreversibleWays(reversedWays);
//            // reverse path if there are more reversed than unreversed ways with
//            // direction-dependent tags
//            if (reversedWays.size() > unreversedWays.size()) {
//                Collections.reverse(path);
//                List<Way> tempWays = unreversedWays;
//                unreversedWays = reversedWays;
//                reversedWays = tempWays;
//            }
//            // if there are still reversed ways with direction-dependent tags,
//            // reverse their tags
//            if (!reversedWays.isEmpty() && PROP_REVERSE_WAY.get()) {
//                List<Way> unreversedTagWays = new ArrayList<Way>(ways);
//                unreversedTagWays.removeAll(reversedWays);
//                ReverseWayTagCorrector reverseWayTagCorrector = new ReverseWayTagCorrector();
//                List<Way> reversedTagWays = new ArrayList<Way>(
//                        reversedWays.size());
//                Collection<Command> changePropertyCommands = null;
//                for (Way w : reversedWays) {
//                    Way wnew = new Way(w);
//                    reversedTagWays.add(wnew);
//                    changePropertyCommands = reverseWayTagCorrector.execute(w,
//                            wnew);
//                }
//                if ((changePropertyCommands != null)
//                        && !changePropertyCommands.isEmpty()) {
//                    for (Command c : changePropertyCommands) {
//                        c.executeCommand();
//                    }
//                }
//                wayTags = TagCollection.unionOfAllPrimitives(reversedTagWays);
//                wayTags.add(TagCollection
//                        .unionOfAllPrimitives(unreversedTagWays));
//            }
//        }
//
//        // create the new way and apply the new node list
//        //
//        Way targetWay = getTargetWay(ways);
//        Way modifiedTargetWay = new Way(targetWay);
//        modifiedTargetWay.setNodes(path);
//
//        List<Command> resolution = CombinePrimitiveResolverDialog
//                .launchIfNecessary(wayTags, ways,
//                        Collections.singleton(targetWay));
//
//        LinkedList<Command> cmds = new LinkedList<Command>();
//        LinkedList<Way> deletedWays = new LinkedList<Way>(ways);
//        deletedWays.remove(targetWay);
//
//        cmds.add(new ChangeCommand(targetWay, modifiedTargetWay));
//        cmds.addAll(resolution);
//        cmds.add(new DeleteCommand(deletedWays));
//        final SequenceCommand sequenceCommand = new SequenceCommand(/*
//                                                                     * for
//                                                                     * correct
//                                                                     * i18n of
//                                                                     * plural
//                                                                     * forms -
//                                                                     * see #9110
//                                                                     */
//        trn("Combine {0} way", "Combine {0} ways", ways.size(), ways.size()),
//                cmds);
//
//        return new Pair<Way, Command>(targetWay, sequenceCommand);
//    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (getCurrentDataSet() == null)
            return;
        Collection<OsmPrimitive> selection = getCurrentDataSet().getSelected();
        BuildingHighwayPair pair = getPair(selection);
        if (pair == null) {
            new Notification(tr("Please select exactly 1 building and 1 highway."))
                    .setIcon(JOptionPane.INFORMATION_MESSAGE)
                    .setDuration(Notification.TIME_SHORT).show();
            return;
        }
        LinearRing building;
        LineString highway = geoUtil.toLineString(pair.getHighway());
        try {
            building = geoUtil.toLinearRing(pair.getBuilding());
        } catch (UnclosedWayException e) {
            new Notification(tr("The building ring is not closed."))
                .setIcon(JOptionPane.INFORMATION_MESSAGE)
                .setDuration(Notification.TIME_SHORT).show();
            return;
        }
        
        Geometry intersection = building.intersection(highway);
        if (!intersection.getGeometryType().equals("MultiPoint") || intersection.getNumGeometries() != 2) {
            new Notification(tr("The building and the highway should intersect at exactly 2 point."))
            .setIcon(JOptionPane.INFORMATION_MESSAGE)
            .setDuration(Notification.TIME_SHORT).show();
            return;
        }
        intersection.toString();
    }

    @Override
    protected void updateEnabledState() {
        if (getCurrentDataSet() == null) {
            setEnabled(false);
            return;
        }
        Collection<OsmPrimitive> selection = getCurrentDataSet().getSelected();
        updateEnabledState(selection);
    }

    @Override
    protected void updateEnabledState(
            Collection<? extends OsmPrimitive> selection) {
        BuildingHighwayPair pair = getPair(selection);
        setEnabled(pair != null);
    }

    /**
     * Get the selected building and highway.
     * 
     * @param selection
     * @return
     * A BuildingHighwayPair if exactly 1 building and 1 higway are selected.
     * null otherwise.
     */
    protected BuildingHighwayPair getPair(
            Collection<? extends OsmPrimitive> selection) {
        if (selection.size() != 2) {
            return null;
        }
        Way building = null;
        Way highway = null;
        for (OsmPrimitive osm : selection) {
            if (osm instanceof Way) {
                if (osm.hasKey("building")) {
                    building = (Way) osm;
                }
                if (osm.hasKey("highway")) {
                    highway = (Way) osm;
                }
            }
        }
        if (building == null || highway == null) {
            return null;
        }
        return new BuildingHighwayPair(building, highway);
    }
    
    /**
     * The building and highway to create a building passage for.
     * 
     * @author Gertjan Idema <mail@gertjanidema.nl>
     *
     */
    private class BuildingHighwayPair {
        private Way building;
        private Way highway;

        public BuildingHighwayPair(Way building, Way highway) {
            super();
            this.building = building;
            this.highway = highway;
        }

        public Way getBuilding() {
            return building;
        }

        public Way getHighway() {
            return highway;
        }
    }
}
