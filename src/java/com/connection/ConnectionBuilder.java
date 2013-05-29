package com.connection;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.spec.PKCS8EncodedKeySpec;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.LogFactoryImpl;

public class ConnectionBuilder {
	
	private static Log logger = LogFactoryImpl.getLog(ConnectionBuilder.class);

	private static KeyStore importKeys(String keyStoreFileName, String certificateChainFileName, String privateKeyFileName, String entryAlias)
	{
		try {

		// Load the keystore
		KeyStore keyStore = KeyStore.getInstance("jks");
		FileInputStream keyStoreInputStream =
		new FileInputStream(keyStoreFileName);
		keyStore.load(keyStoreInputStream, "changeme".toCharArray());
		keyStoreInputStream.close();

		// Load the certificate chain (in X.509 DER encoding).
		FileInputStream certificateStream =
		new FileInputStream(certificateChainFileName);
		CertificateFactory certificateFactory =
		CertificateFactory.getInstance("X.509");
		// Required because Java is STUPID.  You can't just cast the result
		// of toArray to Certificate[].
		java.security.cert.Certificate[] chain = {};
		chain = certificateFactory.generateCertificates(certificateStream).toArray(chain);
		certificateStream.close();

		// Load the private key (in PKCS#8 DER encoding).
		File keyFile = new File(privateKeyFileName);
		byte[] encodedKey = new byte[(int)keyFile.length()];
		FileInputStream keyInputStream = new FileInputStream(keyFile);
		keyInputStream.read(encodedKey);
		keyInputStream.close();
		KeyFactory rSAKeyFactory = KeyFactory.getInstance("RSA");
		PrivateKey privateKey = rSAKeyFactory.generatePrivate(
		new PKCS8EncodedKeySpec(encodedKey));

		keyStore.setEntry(entryAlias,
		new KeyStore.PrivateKeyEntry(privateKey, chain),
		new KeyStore.PasswordProtection("changeme".toCharArray())
		);

		// Write out the keystore
//		FileOutputStream keyStoreOutputStream =
//		new FileOutputStream(keyStoreFileName);
//		keyStore.store(keyStoreOutputStream, keyStorePassword.toCharArray());
//		keyStoreOutputStream.close();
		
			return keyStore;
		}

		catch (Exception e) {
		e.printStackTrace();
		System.exit(1);
		}
		return null;
	}
	
	
	
}
