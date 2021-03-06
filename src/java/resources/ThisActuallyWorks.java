package resources;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class ThisActuallyWorks {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		
        try {
        	
//        	KeyStore trustStore  = KeyStore.getInstance(KeyStore.getDefaultType());
//            FileInputStream instream = new FileInputStream(new File("/root/git/AndroidPuppet/src/java/resources/ubuntuvm2.pem.key"));
//            try {
//                trustStore.load(instream, "nopassword".toCharArray());
////            	trustStore.load(null, null);
//            } finally {
//                try { instream.close(); } catch (Exception ignore) {}
//            }
////
        	
        	String[] args2 = {"/root/git/AndroidPuppet/src/java/resources/ca.jks","/root/git/AndroidPuppet/src/java/resources/ubuntuvm2.der","/root/git/AndroidPuppet/src/java/resources/ubuntuvm2.key.der","ubuntuvm2"};
        	KeyStore trustStore = importKeys(args2);
        	
//            SSLSocketFactory socketFactory = new SSLSocketFactory(trustStore);
//            Scheme sch = new Scheme("https", 443, socketFactory);
//            httpclient.getConnectionManager().getSchemeRegistry().register(sch);
        	KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        	kmf.init(trustStore, "changeme".toCharArray());
        	
            HttpClient client = wrapClient(httpclient,kmf.getKeyManagers());    
        	
            
            
//            httpclient.getCredentialsProvider().setCredentials(
//                    new AuthScope("192.168.100.103", 443),
//                    new UsernamePasswordCredentials("ahenderson@momentumsi.com", "opennow1"));

            HttpGet httpget = new HttpGet("https://192.168.100.103:8081/v2/nodes");
//            HttpGet httpget = new HttpGet("https://192.168.100.101");

            System.out.println("executing request" + httpget.getRequestLine());
//            HttpResponse response = httpclient.execute(httpget);
            HttpResponse response = client.execute(httpget);
            HttpEntity entity = response.getEntity();

            System.out.println("----------------------------------------");
            System.out.println(response.getStatusLine());
            if (entity != null) {
                System.out.println("Response content length: " + entity.getContentLength());
                System.out.println(inputStreamToString(response.getEntity().getContent()).toString());
            }
            EntityUtils.consume(entity);
        }
        catch (Exception e)
        {
        	e.printStackTrace();
        	
        }
         finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            httpclient.getConnectionManager().shutdown();
        }

        
	}
	
	
	private static KeyStore importKeys(String[] args)
	{
		try {
		// Meaningful variable names for the arguments
		String keyStoreFileName = args[0];
		String certificateChainFileName = args[1];
		String privateKeyFileName = args[2];
		String entryAlias = args[3];

		// Get the password for the keystore.
		System.out.println("Keystore password:  ");

//		String keyStorePassword = (new BufferedReader(
//		new InputStreamReader(System.in))).readLine();
//		String keyStorePassword = "changeme";

		// Load the keystore
		KeyStore keyStore = KeyStore.getInstance("jks");
		FileInputStream keyStoreInputStream =
		new FileInputStream(keyStoreFileName);
		keyStore.load(keyStoreInputStream, "changeme".toCharArray());
		keyStoreInputStream.close();
//		keyStore.load(null,null);	

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

		// Add the new entry
		System.out.println("Private key entry password:  ");

//		String privateKeyEntryPassword = (new BufferedReader(
//		new InputStreamReader(System.in))).readLine();
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
	
	public static HttpClient wrapClient(HttpClient base,KeyManager[] kms) {
		try {
		SSLContext ctx = SSLContext.getInstance("TLS");
		X509TrustManager tm = new X509TrustManager() {
		 
		public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
			System.out.println("inhere1");
		}
		 
		public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
			System.out.println("inhere2");
		}
		 
		public X509Certificate[] getAcceptedIssuers() {
		return null;
		}
		};
		ctx.init(kms, new TrustManager[]{tm}, null);
		SSLSocketFactory ssf = new SSLSocketFactory(ctx);
		ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		ClientConnectionManager ccm = base.getConnectionManager();
		SchemeRegistry sr = ccm.getSchemeRegistry();
		sr.register(new Scheme("https",443, ssf));
		return new DefaultHttpClient(ccm, base.getParams());
		} catch (Exception ex) {
		ex.printStackTrace();
		return null;
		}
		}
	
	private static StringBuilder inputStreamToString(InputStream is) {
	    String line = "";
	    StringBuilder total = new StringBuilder();
	    
	    // Wrap a BufferedReader around the InputStream
	    BufferedReader rd = new BufferedReader(new InputStreamReader(is));

	    try
	    {
	    // Read response until the end
	    while ((line = rd.readLine()) != null) { 
	        total.append(line); 
	    }
	    }
	    catch (Exception e)
	    {
	    	e.printStackTrace();
	    }
	    
	    // Return full string
	    return total;
	}


}
