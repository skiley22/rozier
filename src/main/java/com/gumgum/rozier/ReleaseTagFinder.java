package com.gumgum.rozier;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;

import java.util.Comparator;
import java.util.List;

/**
 * Use this class to find the latest release tag in Git.
 *
 * @author skiley on 2018-11-15
 */
@SuppressWarnings("unused")
public final class ReleaseTagFinder {

    private ReleaseTagFinder() { }

    /**
     * Method for finding the latest version tag which follows the format "v0.1".
     */
    public static String getNewestTagByVersion(final Git git) throws GitAPIException {
        final List<Ref> tags = git.tagList().call();

        return tags.stream()
                .max(getVersionNumberComparator())
                .orElseThrow(() -> new RuntimeException("No tags found"))
                .getName();
    }

    /**
     * Comparator for finding the latest version which follows the format "v0.1".
     */
    private static Comparator<Ref> getVersionNumberComparator() {
        return  (aTag, bTag) -> {

            String aTagName = aTag.getName();
            String bTagName = bTag.getName();

            int indexOfMajorVersionNumberA = aTagName.indexOf('v') + 1;
            int indexOfMajorVersionNumberB = bTagName.indexOf('v') + 1;

            int indexOfMinorVersionNumberA = aTagName.indexOf('.');
            int indexOfMinorVersionNumberB = bTagName.indexOf('.');

            String majorVersionStringA = aTagName.substring(indexOfMajorVersionNumberA, indexOfMinorVersionNumberA);
            String majorVersionStringB = bTagName.substring(indexOfMajorVersionNumberB, indexOfMinorVersionNumberB);

            Double majorVersionNumberA = Double.valueOf(majorVersionStringA);
            Double majorVersionNumberB = Double.valueOf(majorVersionStringB);

            if (majorVersionNumberA > majorVersionNumberB) {
                return 1;
            } else if (majorVersionNumberA < majorVersionNumberB) {
                return -1;
            }

            String minorVersionStringA = aTagName.substring(indexOfMinorVersionNumberA + 1);
            String minorVersionStringB = bTagName.substring(indexOfMinorVersionNumberB + 1);

            Double versionNumberA = Double.valueOf(minorVersionStringA);
            Double versionNumberB = Double.valueOf(minorVersionStringB);

            if (versionNumberA > versionNumberB) {
                return 1;
            } else if (versionNumberA < versionNumberB) {
                return -1;
            } else {
                return 0;
            }
        };
    }
}
