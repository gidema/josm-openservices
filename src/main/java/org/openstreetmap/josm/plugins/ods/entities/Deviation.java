package org.openstreetmap.josm.plugins.ods.entities;

import org.openstreetmap.josm.command.Command;

public interface Deviation {
    /**
     * Return true if this deviation can be fixed
     * @return
     */
    public boolean isFixable();

    /**
     * Get the command that fixes the deviation
     *
     * @return
     */
    public Command getFix();

    /**
     * Remove the tag(s) that indicate this deviation
     */
    public void clearOdsTags();
}
