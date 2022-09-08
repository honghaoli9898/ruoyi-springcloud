package com.sdps.common.crypto.algorithm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdps.common.crypto.annotation.EncodingCrypto;
import com.sdps.common.crypto.bean.ApiCryptoBody;
import com.sdps.common.crypto.config.ApiCryptoConfig;
import com.sdps.common.crypto.constants.EncodingType;
import com.sdps.common.crypto.exception.ApiCryptoExceptionType;
import com.sdps.common.crypto.exception.ApiDecodeException;
import com.sdps.common.crypto.ov.IApiRequestBody;
import com.sdps.common.crypto.ov.IApiResponseBody;
import com.sdps.common.crypto.util.EncodingUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.StringUtils;

import java.lang.reflect.Type;
import java.util.Objects;

/**
 * 编码 实现
 *
 * @author hermes-di
 * @since 1.0.0.RELEASE
 */
public class EncodingApiCrypto implements ApiCryptoAlgorithm {
    private static final Log logger = LogFactory.getLog(EncodingApiCrypto.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ApiCryptoConfig apiCryptoConfig;

    private IApiRequestBody iApiRequestBody;

    private IApiResponseBody iApiResponseBody;


    public EncodingApiCrypto() {
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void setiApiRequestBody(IApiRequestBody iApiRequestBody) {
        this.iApiRequestBody = iApiRequestBody;
    }

    public void setiApiResponseBody(IApiResponseBody iApiResponseBody) {
        this.iApiResponseBody = iApiResponseBody;
    }

    @Override
    public boolean isCanRealize(MethodParameter methodParameter, boolean requestOrResponse) {
        EncodingCrypto annotation = this.getAnnotation(methodParameter, EncodingCrypto.class);
        return !Objects.isNull(annotation);
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        EncodingCrypto annotation = this.getAnnotation(methodParameter, EncodingCrypto.class);

        ApiCryptoBody apiCryptoBody = this.requestBody(annotation, httpInputMessage, iApiRequestBody, objectMapper, logger);

        if (!StringUtils.hasText(apiCryptoBody.getData())) {
            ApiCryptoExceptionType exceptionType = ApiCryptoExceptionType.PARAM_DATA_MISSING;
            logger.error(exceptionType.getMessage());
            throw new ApiDecodeException(exceptionType);
        }

        EncodingType encodingType = apiCryptoConfig.getEncodingType();
        if (!annotation.encodingType().equals(EncodingType.DEFAULT)) {
            encodingType = annotation.encodingType();
        }

        byte[] decode = EncodingUtil.decode(encodingType, apiCryptoBody.getData().getBytes(apiCryptoConfig.getCharset()));

        return this.stringToInputStream(decode, httpInputMessage.getHeaders(), logger);
    }


    @Override
    public Object responseBefore(Object body, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        EncodingCrypto annotation = this.getAnnotation(methodParameter, EncodingCrypto.class);

        String json = responseBody(body, objectMapper, logger);

        EncodingType encodingType = apiCryptoConfig.getEncodingType();
        if (!annotation.encodingType().equals(EncodingType.DEFAULT)) {
            encodingType = annotation.encodingType();
        }

        String encode = EncodingUtil.encode(encodingType, json.getBytes(apiCryptoConfig.getCharset()));

        ApiCryptoBody apiCryptoBody = new ApiCryptoBody().setData(encode);

        // 使用自定义响应体
        if (iApiResponseBody != null) {
            return iApiResponseBody.responseBody(annotation, apiCryptoBody);
        }

        if (body instanceof String) {
            return responseBody(apiCryptoBody, objectMapper, logger);
        } else {
            return apiCryptoBody;
        }
    }
}
