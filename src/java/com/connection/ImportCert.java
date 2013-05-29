package com.connection;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Collection;
import java.util.Iterator;



public class ImportCert {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try
		{
		 FileInputStream fis = new FileInputStream("/root/git/AndroidPuppet/src/java/resources/pe-internal-dashboard.pem");
		 CertificateFactory cf = CertificateFactory.getInstance("X.509");
		 Collection c = cf.generateCertificates(fis);
		 Iterator i = c.iterator();
		 while (i.hasNext()) {
		    Certificate cert = (Certificate)i.next();
		    System.out.println(cert);
		    
		    KeyStore trustStore  = KeyStore.getInstance(KeyStore.getDefaultType());
//		    trustStore.load(null,null)
		 }
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
