package com.skp.payment.p2plending.backoffice.common;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

@Service
public class HttpClientService {

    /**
     * json parameter 를 받아서 POST 전송을 한다.
     */
    public String doPostRawAsString(String uri, String parameter, String authKey) throws Exception{

        HttpUriRequest httpUriRequest = getHttpUriRequest(uri, parameter, authKey);
        ResponseHandler<String> responseHandler = getResponseHander(String.class, null);

        HttpClient httpClient = HttpClientBuilder.create().build();

        return httpClient.execute(httpUriRequest, responseHandler);
    }

    /**
     * MAP 에 담겨진 파라미터를 받아서 GET 으로 조회 요청을 한다.
     */
    public String doGetAsString(String uri, Map<String, String> paramMap, String authKey) throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();

        try {
            HttpUriRequest uriRequest = createHttpGetRequestUri(uri, paramMap, authKey);

            ResponseHandler<String> responseHandler = getResponseHander(String.class, null);

            return httpclient.execute(uriRequest, responseHandler);
        } finally {
            HttpClientUtils.closeQuietly(httpclient);
        }
    }


    private HttpUriRequest getHttpUriRequest(String uri, String parameter, String authKey) throws Exception{

        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(5000).setConnectionRequestTimeout(5000).build();

        StringEntity stringEntity  = new StringEntity(parameter, "UTF-8");
        stringEntity.setContentType("application/json");

        HttpUriRequest httpUriRequest = RequestBuilder.post()
                .setUri(uri)
                .setConfig(requestConfig)
                .setEntity(stringEntity)
                .setHeader("Content-Type", "application/json;")
                .setHeader("Authorization", "Basic " + authKey)
                .build();

        return httpUriRequest;
    }


    private <T> ResponseHandler<T> getResponseHander(final Class<T> classofT, final String charset) {
        ResponseHandler<T> responseHandler = new ResponseHandler<T>() {

            @SuppressWarnings("unchecked")
            @Override
            public T handleResponse(HttpResponse response) throws IOException {
                HttpEntity httpEntity = response.getEntity();
                if (String.class == classofT) {
                    return (T) EntityUtils.toString(httpEntity, charset);
                }
                return null;
            }
        };

        return responseHandler;
    }

    private HttpUriRequest createHttpGetRequestUri(String uri, Map<String, String> paramMap, String authKey) throws UnsupportedEncodingException {
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(5000).setConnectionRequestTimeout(5000).build();

        RequestBuilder rb = RequestBuilder.get().setUri(uri).setConfig(requestConfig);

        rb.addHeader("Content-Type", "application/json;");
        if(!"".equals(authKey) && authKey != null){
            rb.addHeader("Authorization", "Basic " + authKey);
        }

        for (Map.Entry<String, String> param : paramMap.entrySet()) {
            String key = param.getKey();
            String value = param.getValue();

            rb.addParameter(key, value);
        }

        return rb.build();
    }

    public static HttpServletRequest getRequest() {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest hsr = sra.getRequest();
        return hsr;
    }
}
