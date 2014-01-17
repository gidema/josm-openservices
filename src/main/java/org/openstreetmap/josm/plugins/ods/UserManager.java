package org.openstreetmap.josm.plugins.ods;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.net.Authenticator.RequestorType;
import java.net.PasswordAuthentication;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.io.OsmApi;
import org.openstreetmap.josm.io.auth.CredentialsAgent;
import org.openstreetmap.josm.io.auth.CredentialsAgentException;
import org.openstreetmap.josm.io.auth.CredentialsManager;

/**
 * The user manager class can switch between plain and import user
 * accounts.
 * 
 * @author gertjan
 *
 */
public class UserManager {
    String suffix;
    String user;
    char[] password;

    public UserManager(String postfix) {
        this.suffix = "_" +postfix;
        initFromPreferences();
    }

    boolean isPlain() {
        return !user.endsWith(suffix);
    }
    
    public void switchUser() {
        if (isPlain()) {
            switchImportUser();
        }
        else {
            switchPlainUser();
        }
    }
    
    public void switchImportUser() {
        if (isPlain()) {
            user = user + suffix;
            saveToPreferences();
        }
    }
    
    public void switchPlainUser() {
        if (!isPlain()) {
            user = user.substring(0, user.length() - suffix.length());
            saveToPreferences();            
        }
    }
    
    public void initFromPreferences() {
        CredentialsAgent cm = CredentialsManager.getInstance();
        try {
            PasswordAuthentication pa = cm.lookup(RequestorType.SERVER, OsmApi.getOsmApi().getHost());
            user = pa.getUserName();
            password = pa.getPassword();
        } catch(CredentialsAgentException e) {
            e.printStackTrace();
            Main.warn(tr("Failed to retrieve OSM credentials from credential manager."));
            Main.warn(tr("Current credential manager is of type ''{0}''", cm.getClass().getName()));
        }
    }

    public void saveToPreferences() {
        CredentialsAgent cm = CredentialsManager.getInstance();
        try {
            PasswordAuthentication pa = new PasswordAuthentication(user, password);
            cm.store(RequestorType.SERVER, OsmApi.getOsmApi().getHost(), pa);
        } catch (CredentialsAgentException e) {
            e.printStackTrace();
            Main.warn(tr("Failed to save OSM credentials to credential manager."));
            Main.warn(tr("Current credential manager is of type ''{0}''", cm.getClass().getName()));
        }
    }
}
