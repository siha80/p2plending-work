package com.skp.fintech.p2plending.backoffice.common.util;

import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.HTTPSProperties;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.config.ClientConfig;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.MediaType;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class HttpClientManager {
    public ClientResponse postRawData(String uri, String data) throws KeyManagementException, NoSuchAlgorithmException {
        Client c = Client.create(sslConfigure());
        c.setReadTimeout(10000);
        c.setConnectTimeout(10000);
        return c.resource(uri).type(MediaType.APPLICATION_JSON).
                accept(MediaType.APPLICATION_JSON).
                post(ClientResponse.class, data);
    }

    /**
     * HTTPS 설정을 반환한다.
     *
     * @return {@link ClientConfig}
     * @since 2015.02.13
     */
    public ClientConfig sslConfigure() throws NoSuchAlgorithmException, KeyManagementException {
        System.setProperty("jsse.enableSNIExtension", "false");
        TrustManager[] certs = new TrustManager[]{new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }
        }};
        SSLContext ctx = SSLContext.getInstance("SSL");
        ctx.init(null, certs, new SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(ctx.getSocketFactory());
        ClientConfig config = new DefaultClientConfig();
        config.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, new HTTPSProperties((hostname, session) -> true, ctx));
        return config;
    }

    /**
     * @Method Name : getRequest
     * @Method 설명 :
     * @return HttpServletRequest
     */
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest hsr = sra.getRequest();
        return hsr;

    }


    public static HttpSession getHeader() {
        return getRequest().getSession();
    }
}
