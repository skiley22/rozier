package com.gumgum.rozier;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.eclipse.jgit.transport.SshSessionFactory;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Configuration for DatabaseSchemaManager. Specify a Bitbucket username, SSH Session object, schema file path, and URL to the repo.
 *
 * @author skiley on 2018-11-15
 */
public final class DatabaseSchemaManagerConfig {

    /**
     * I recommend setting up a "service account" for the sake of authenticating with Bitbucket programmatically.
     */
    private String bitbucketUser;
    /**
     * Use SshUtils to create a SshSessionFactory object.
     */
    private SshSessionFactory sshSessionFactory;
    /**
     * Path to the schema file in the specified git repo.
     */
    private String schemaFilePath;
    /**
     * URI which points to the git repository which hosts your database schema file.
     */
    private String gitUri;

    private DatabaseSchemaManagerConfig() { }

    public String getBitbucketUser() {
        return bitbucketUser;
    }

    private void setBitbucketUser(String bitbucketUser) {
        this.bitbucketUser = bitbucketUser;
    }

    public String getSchemaFilePath() {
        return schemaFilePath;
    }

    private void setSchemaFilePath(String schemaFilePath) {
        this.schemaFilePath = schemaFilePath;
    }

    public String getGitUri() {
        return gitUri;
    }

    private void setGitUri(String gitUri) {
        this.gitUri = gitUri;
    }

    public SshSessionFactory getSshSessionFactory() {
        return sshSessionFactory;
    }

    private void setSshSessionFactory(SshSessionFactory sshSessionFactory) {
        this.sshSessionFactory = sshSessionFactory;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }

    public static Builder create() {
        return new Builder();
    }

    /**
     *
     * To help ensure that all required fields are populated in the config, this builder class must be used to create a
     * DatabaseSchemaManagerConfig.
     *
     * @author skiley on 2018-11-15
     */
    public static final class Builder {

        private DatabaseSchemaManagerConfig result;

        private Builder() {
            result = new DatabaseSchemaManagerConfig();
        }

        public Builder withBitbucketUser(String bitbucketUser) {
            result.setBitbucketUser(bitbucketUser);
            return this;
        }

        public Builder withSchemaFilePath(String schemaFilePath) {
            result.setSchemaFilePath(schemaFilePath);
            return this;
        }

        public Builder withGitUri(String gitUri) {
            result.setGitUri(gitUri);
            return this;
        }

        public Builder withSshSessionFactory(SshSessionFactory sshSessionFactory) {
            result.setSshSessionFactory(sshSessionFactory);
            return this;
        }

        public DatabaseSchemaManagerConfig build() {
            if (isBlank(result.getBitbucketUser())
                    || isBlank(result.getGitUri())
                    || isBlank(result.getSchemaFilePath())
                    || result.sshSessionFactory == null) {
                throw new IllegalArgumentException(
                        "Attempted to build a com.gumgum.rozier.DatabaseSchemaManagerConfig with an invalid state.\n" + result.toString());
            }

            return result;
        }
    }
}
