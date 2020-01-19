package org.openstreetmap.josm.plugins.ods.test.util;
//License: GPL. For details, see LICENSE file.

import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.nio.file.Paths;
import java.text.MessageFormat;

import org.openstreetmap.josm.data.Preferences;
import org.openstreetmap.josm.data.projection.ProjectionRegistry;
import org.openstreetmap.josm.data.projection.Projections;
import org.openstreetmap.josm.io.OsmApi;
import org.openstreetmap.josm.tools.I18n;
import org.openstreetmap.josm.tools.PlatformManager;

/**
 * Fixture to define a proper and safe environment before running tests.
 */
public class JOSMFixture {

    /**
     * Returns a new test fixture initialized to "unit" home.
     * @return A new test fixture for unit tests
     */
    static public JOSMFixture createUnitTestFixture() {
        return new JOSMFixture("test/config/unit-josm.home");
    }

    /**
     * Returns a new test fixture initialized to "functional" home.
     * @return A new test fixture for functional tests
     */
    static public JOSMFixture createFunctionalTestFixture() {
        return new JOSMFixture("test/config/functional-josm.home");
    }

    /**
     * Returns a new test fixture initialized to "performance" home.
     * @return A new test fixture for performance tests
     */
    static public JOSMFixture createPerformanceTestFixture() {
        return new JOSMFixture("test/config/performance-josm.home");
    }

    private final String josmHome;

    /**
     * Constructs a new text fixture initialized to a given josm home.
     * @param josmHome The user home where preferences are to be read/written
     */
    public JOSMFixture(String josmHome) {
        this.josmHome = josmHome;
    }

    /**
     * Initializes the test fixture, without GUI.
     */
    public void init() {
        init(false);
    }

    /**
     * Initializes the test fixture, with or without GUI.
     * @param createGui if {@code true} creates main GUI components
     */
    public void init(boolean createGui) {

        // check josm.home
        //
        if (josmHome == null) {
            fail(MessageFormat.format("property ''{0}'' not set in test environment", "josm.home"));
        } else {
            File f = new File(josmHome);
            if (! f.exists() || ! f.canRead()) {
                fail(MessageFormat.format("property ''{0}'' points to ''{1}'' which is either not existing ({2}) or not readable ({3}). Current directory is ''{4}''.",
                        "josm.home", josmHome, f.exists(), f.canRead(), Paths.get("").toAbsolutePath()));
            }
        }
        System.setProperty("josm.home", josmHome);
        //     Main.main.initialize();
        //     Main.initApplicationPreferences();
        Preferences.main().enableSaveOnPut(false);
        I18n.init();
        // call the really early hook before we anything else
        PlatformManager.getPlatform().preStartupHook();

        Preferences.main().init(false);
        I18n.set(Preferences.main().get("language", "en"));

        // init projection
        ProjectionRegistry.setProjection(Projections.getProjectionByCode("EPSG:3857")); // Mercator

        // make sure we don't upload to or test against production
        //
        String url = OsmApi.getOsmApi().getBaseUrl().toLowerCase().trim();
        if (url.startsWith("http://www.openstreetmap.org") || url.startsWith("http://api.openstreetmap.org")
                || url.startsWith("https://www.openstreetmap.org") || url.startsWith("https://api.openstreetmap.org")) {
            fail(MessageFormat.format("configured server url ''{0}'' seems to be a productive url, aborting.", url));
        }

        if (createGui) {
            //         if (MainApplication.getToolbar() == null) {
            //             MainApplication.toolbar = new ToolbarPreferences();
            //         }
            //            if (Main.main == null) {
            //                MainApplication mainApplication = new MainApplication();
            //            }
            //         if (MainA.map == null) {
            //             Main.main.createMapFrame(null, null);
            //         }
        }
    }
}
