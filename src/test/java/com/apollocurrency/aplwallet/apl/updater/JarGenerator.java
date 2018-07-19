/*
 * Copyright © 2017-2018 Apollo Foundation
 *
 * See the LICENSE.txt file at the top-level directory of this distribution
 * for licensing information.
 *
 * Unless otherwise agreed in a custom licensing agreement with Apollo Foundation,
 * no part of the Apl software, including this file, may be copied, modified,
 * propagated, or distributed except according to the terms contained in the
 * LICENSE.txt file.
 *
 * Removal or modification of this copyright notice is prohibited.
 *
 */

package com.apollocurrency.aplwallet.apl.updater;

import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.Random;

public class JarGenerator {
    private SimpleJar jar;
    private static final String KEY_ALIAS = "test";
    private static final String KEY_PASSWORD = "test";


    public JarGenerator(OutputStream outputStream, Certificate certificate, PrivateKey privateKey) throws GeneralSecurityException, IOException {
        KeyStore keyStore = initKeyStore(certificate, privateKey);
        this.jar = new SimpleSignedJar(outputStream, keyStore, KEY_ALIAS,  KEY_PASSWORD);
    }

    public JarGenerator(OutputStream outputStream) {
        this.jar = new SimpleJar(outputStream);
    }


    private KeyStore initKeyStore(Certificate certificate, PrivateKey privateKey) throws GeneralSecurityException, IOException {
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(null, null);
        keyStore.setEntry(KEY_ALIAS, new KeyStore.PrivateKeyEntry(privateKey, new Certificate[] {certificate}), new KeyStore.PasswordProtection(KEY_PASSWORD.toCharArray()));
        return keyStore;
    }

    public void generate() throws IOException {
        jar.addManifestAttribute("Main-Class", "com.test.MainClass");
        jar.addManifestAttribute("Application-Name", "Test-app");
        jar.addManifestAttribute("Permissions", "all-permissions");
        jar.addFileContents("com/test/MainClass.class", randomBytes(4096));
        jar.addFileContents("com/test/AnotherClass.class", randomBytes(2123));
        jar.addFileContents("com/test/AnotherClass2.class", randomBytes(7654));
    }

    public void close() throws IOException {
        jar.close();
    }

    private byte[] randomBytes(int size) {
        byte[] bytes = new byte[size];
        Random random = new Random();
        random.nextBytes(bytes);
        return bytes;
    }
}
