package com.connection;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.LogFactoryImpl;

/**
 * This Trust Manager is "naive" because it trusts everyone. 
 **/
public class NaiveTrustManager implements X509TrustManager
{
	private static Log logger = LogFactoryImpl.getLog(NaiveTrustManager.class);
	
	public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
		logger.debug("In Naive checkClientTrusted String: " + string + " cert: " + xcs);
	}
	 
	public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
		logger.debug("In Naive checkServerTrusted String: " + string + " cert: " + xcs);
	}
	 
	public X509Certificate[] getAcceptedIssuers() {
		return null;
	}
	
  
}
