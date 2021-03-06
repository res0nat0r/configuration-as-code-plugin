package io.jenkins.plugins.casc.util;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import hudson.security.Permission;
import hudson.security.PermissionGroup;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;


/**
 * Implements lookup for {@link Permission}s.
 * Created by mads on 2/9/18.
 */
@Restricted(NoExternalUse.class)
public class PermissionFinder {

    /** For Matrix Auth - Title/Permission **/
    private static final Pattern PERMISSION_PATTERN = Pattern.compile("^([^\\/]+)\\/(.+)$");

    /**
     * Attempt to match a given permission to what is defined in the UI.
     * @param id String of the form "Title/Permission" (Look in the UI) for a particular permission
     * @return a matched permission
     */
    @CheckForNull
    public static Permission findPermission(String id) {
        final String resolvedId = findPermissionId(id);
        return resolvedId != null ? Permission.fromId(resolvedId) : null;
    }

    /**
     * Attempt to match a given permission to what is defined in the UI.
     * @param id String of the form "Title/Permission" (Look in the UI) for a particular permission
     * @return a matched permission ID
     */
    @CheckForNull
    public static String findPermissionId(String id) {
        List<PermissionGroup> pgs = PermissionGroup.getAll();
        Matcher m = PERMISSION_PATTERN.matcher(id);
        if(m.matches()) {
            String owner = m.group(1);
            String name = m.group(2);
            for(PermissionGroup pg : pgs) {
                if(pg.owner.equals(Permission.class)) {
                    continue;
                }

                //TODO: Support PermissionGroupIDs (JENKINS-51598)
                //How do we do this properly, we want to mimic the UI as best as possible. So the logic conclusion is
                //That when you want admin to be Overall/Administer you put that in. Overall being the group title...
                //Name being the Permission you want to set in the matrix.
                if(pg.title.toString(Locale.US).equals(owner)) {
                    return pg.owner.getName() + "." + name;
                }
            }
        }
        return null;
    }
}
