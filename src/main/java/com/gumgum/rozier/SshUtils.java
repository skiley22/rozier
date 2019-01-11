package com.gumgum.rozier;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.OpenSshConfig;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.util.FS;

import java.nio.charset.Charset;

/**
 * Class for creating a SshSessionFactory which will be used to authenticate with Bitbucket. Note that I have provided methods for
 * creating an SSH identity via public/private key pair, but you can write your own implementation of JschConfigSessionFactory if you'd
 * like to create an SSH identity through other means.
 *
 * @author skiley on 2018-11-15
 */
@SuppressWarnings("unused")
public final class SshUtils {

    private SshUtils() { }

    /**
     * @param identityName An alias for this ssh identity
     * @return An SSH Identity in the form of a SshSessionFactory object
     */
    public static SshSessionFactory getSshSessionFactory(String publicKey, String privateKey, String identityName) {
        return getSshSessionFactory(publicKey, privateKey, identityName, null);
    }

    /**
     * @param identityName An alias for this ssh identity
     * @return An SSH Identity in the form of a SshSessionFactory object
     */
    public static SshSessionFactory getSshSessionFactory(String publicKey, String privateKey, String identityName, String passPhrase) {

        return new JschConfigSessionFactory() {
            @Override
            protected void configure(OpenSshConfig.Host hc, Session session) {
                session.setConfig("StrictHostKeyChecking", "no");
            }

            @Override
            protected JSch createDefaultJSch(FS fs) throws JSchException {
                JSch defaultJSch = super.createDefaultJSch(fs);

                byte[] passPhraseBytes = null;

                if (StringUtils.isNotBlank(passPhrase)) {
                    passPhraseBytes = passPhrase.getBytes(Charset.defaultCharset());
                }

                defaultJSch.addIdentity(identityName,
                        privateKey.getBytes(Charset.defaultCharset()),
                        publicKey.getBytes(Charset.defaultCharset()),
                        passPhraseBytes);
                return defaultJSch;
            }
        };
    }
}
