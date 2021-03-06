package com.learning.securedapp;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

public class ApplicationTests extends AbstractApplicationTests
{

    @Autowired
    private TestRestTemplate testRestTemplate;

    @BeforeClass
    public static void sslSetUp()
    {
        try
        {
            // setup ssl context to ignore certificate errors
            SSLContext ctx = SSLContext.getInstance("TLS");
            X509TrustManager tm = new X509TrustManager()
            {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType)
                        throws CertificateException
                {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType)
                        throws CertificateException
                {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers()
                {
                    return null;
                }
            };
            ctx.init(null, new TrustManager[] { tm }, null);
            SSLContext.setDefault(ctx);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @TestConfiguration
    static class Config
    {
        final MySimpleClientHttpRequestFactory factory = new ApplicationTests().new MySimpleClientHttpRequestFactory(
                new HostnameVerifier()
                {
                    @Override
                    public boolean verify(String hostname, SSLSession session)
                    {
                        return true;
                    }
                });

        @Bean
        public RestTemplateBuilder restTemplateBuilder()
        {
            return new RestTemplateBuilder().requestFactory(factory);
        }
    }

    @Test
    public void testHome() throws Exception
    {
        ResponseEntity<String> entity = testRestTemplate.getForEntity("/login.html",
                String.class);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    class MySimpleClientHttpRequestFactory extends SimpleClientHttpRequestFactory
    {

        private HostnameVerifier hostnameVerifier;

        public MySimpleClientHttpRequestFactory(HostnameVerifier hostnameVerifier)
        {
            this.hostnameVerifier = hostnameVerifier;
        }

        @Override
        protected void prepareConnection(final HttpURLConnection connection,
                final String httpMethod) throws IOException
        {
            if (connection instanceof HttpsURLConnection)
            {
                ((HttpsURLConnection) connection)
                        .setHostnameVerifier(this.hostnameVerifier);
            }
            super.prepareConnection(connection, httpMethod);
        }
    }

}
