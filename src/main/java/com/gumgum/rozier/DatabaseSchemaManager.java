package com.gumgum.rozier;

import com.google.common.io.Files;
import org.apache.commons.io.IOUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * Use this class to pull the latest DB schmea from a Bitbucket repo.
 *
 * @author skiley
 */
@SuppressWarnings("unused")
public final class DatabaseSchemaManager {

    private static final String REF_BRANCH_PREFIX = "refs/remotes/origin/";
    private static final String REF_TAG_PREFIX = "refs/tags/";

    private DatabaseSchemaManagerConfig config;

    /**
     * Use this enum to specify what type of ref you're grabbing the schema from.
     */
    public enum RefType {
        TAG,
        BRANCH
    }

    /**
     * Pass in a config object to instantiate a Database Schema Manager.
     *
     * @param config  Configuration object which contains a Bitbucket username, SSH Session object, schema file path, and URL to the repo.
     */
    public DatabaseSchemaManager(DatabaseSchemaManagerConfig config) {
        this.config = config;
    }

    /**
     *
     * This method will pull the DB schema from the specified branch or tag.
     *
     * @param refName Branch/Tag to clone
     * @return Returns the contents of the filename specified as part of DatabaseSchemaManagerConfig
     * @throws IOException Can be thrown due to a variety of reasons, including if the Git client is unable to find
     * the "HEAD" ref and if its unable to deserialize the schema file from a git object to a String
     * @throws GitAPIException Will be called if the method is unable to clone the repo
     */
    public String getSchemaFromGit(RefType refType, String refName) throws IOException, GitAPIException {

        File temp = Files.createTempDir();
        temp.deleteOnExit();

        Git git = Git.cloneRepository()
                .setURI(config.getGitUri())
                .setTransportConfigCallback(transport -> {
                    SshTransport sshTransport = (SshTransport) transport;
                    sshTransport.setSshSessionFactory(config.getSshSessionFactory());
                })
                .setDirectory(temp)
                .call();

        Ref ref;

        switch (refType) {
            case BRANCH:
                ref = git.getRepository().exactRef(REF_BRANCH_PREFIX + refName);
                break;
            case TAG:
                ref = git.getRepository().exactRef(REF_TAG_PREFIX + refName);
                break;
            default:
                ref = git.getRepository().exactRef(refName);
        }

        if (ref == null) {
            throw new NullPointerException(String.format("Provided %s \"%s\" does not exist", refType.name().toLowerCase(), refName));
        }

        RevWalk walk = new RevWalk(git.getRepository());
        RevCommit commit = walk.parseCommit(ref.getObjectId());
        RevTree tree = commit.getTree();

        TreeWalk treeWalk = new TreeWalk(git.getRepository());
        treeWalk.addTree(tree);
        treeWalk.setRecursive(true);
        treeWalk.setFilter(PathFilter.create(config.getSchemaFilePath()));

        if (!treeWalk.next()) {
            throw new IllegalStateException("Did not find expected file " + config.getSchemaFilePath());
        }

        ObjectId objectId = treeWalk.getObjectId(0);
        ObjectLoader loader = git.getRepository().open(objectId);
        InputStream is = loader.openStream();

        return IOUtils.toString(is, Charset.defaultCharset());
    }
}
