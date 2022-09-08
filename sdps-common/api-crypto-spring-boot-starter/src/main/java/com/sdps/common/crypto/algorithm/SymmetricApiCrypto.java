package com.sdps.common.crypto.algorithm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdps.common.crypto.annotation.SymmetricCrypto;
import com.sdps.common.crypto.bean.ApiCryptoBody;
import com.sdps.common.crypto.config.ApiCryptoConfig;
import com.sdps.common.crypto.constants.EncodingType;
import com.sdps.common.crypto.exception.ApiCryptoExceptionType;
import com.sdps.common.crypto.exception.ApiDecodeException;
import com.sdps.common.crypto.exception.ApiEncryptException;
import com.sdps.common.crypto.ov.IApiRequestBody;
import com.sdps.common.crypto.ov.IApiResponseBody;
import com.sdps.common.crypto.util.CryptoUtil;
import com.sdps.common.crypto.util.RandomStrUtil;

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

import javax.crypto.Cipher;

import java.lang.reflect.Type;
import java.util.Objects;

/**
 * 对称性加密、解密 实现
 *
 * @author hermes-di
 * @since 1.0.0.RELEASE
 */
public class SymmetricApiCrypto implements ApiCryptoAlgorithm {

    private static final Log logger = LogFactory.getLog(SymmetricApiCrypto.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ApiCryptoConfig apiCryptoConfig;

    private IApiRequestBody iApiRequestBody;

    private IApiResponseBody iApiResponseBody;

    public SymmetricApiCrypto() {
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
        SymmetricCrypto annotation = this.getAnnotation(methodParameter, SymmetricCrypto.class);
        return !Objects.isNull(annotation);
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        SymmetricCrypto annotation = this.getAnnotation(methodParameter, SymmetricCrypto.class);

        ApiCryptoBody apiCryptoBody = this.requestBody(annotation, httpInputMessage, iApiRequestBody, objectMapper, logger);

        if (!StringUtils.hasText(apiCryptoBody.getData())) {
            ApiCryptoExceptionType exceptionType = ApiCryptoExceptionType.PARAM_DATA_MISSING;
            logger.error(exceptionType.getMessage());
            throw new ApiDecodeException(exceptionType);
        }

        if (annotation.type().isProduceIv()) {
            if (!StringUtils.hasText(apiCryptoBody.getIv())) {
                ApiCryptoExceptionType exceptionType = ApiCryptoExceptionType.PARAM_VI_MISSING;
                logger.error(exceptionType.getMessage());
                throw new ApiDecodeException(exceptionType);
            }
        } else {
            apiCryptoBody.setIv(null);
        }

        String secretKey = secretKey(annotation);


        EncodingType encodingType = apiCryptoConfig.getEncodingType();
        if (!annotation.encodingType().equals(EncodingType.DEFAULT)) {
            encodingType = annotation.encodingType();
        }

        String encryptData;

        try {

            encryptData = CryptoUtil.symmetric(
                    annotation.type().getType(),
                    annotation.type().getMethod(),
                    Cipher.DECRYPT_MODE,
                    secretKey,
                    apiCryptoBody.getData(),
                    encodingType,
                    apiCryptoBody.getIv() != null ? apiCryptoBody.getIv() : null,
                    apiCryptoConfig.getCharset()
            );

        } catch (Exception e) {
            ApiCryptoExceptionType exceptionType = ApiCryptoExceptionType.DECRYPTION_FAILED;
            logger.error(exceptionType.getMessage() + " ERROR：" + e.getMessage());
            throw new ApiDecodeException(exceptionType);
        }

        if (!StringUtils.hasText(encryptData)) {
            ApiCryptoExceptionType exceptionType = ApiCryptoExceptionType.DATA_EMPTY;
            logger.error(exceptionType.getMessage());
            throw new ApiDecodeException(exceptionType);
        }

        return this.stringToInputStream(encryptData.getBytes(apiCryptoConfig.getCharset()), httpInputMessage.getHeaders(), logger);
    }

    @Override
    public Object responseBefore(Object body, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        SymmetricCrypto annotation = this.getAnnotation(methodParameter, SymmetricCrypto.class);

        String json = responseBody(body, objectMapper, logger);

        String secretKey = secretKey(annotation);

        String iv = null;

        if (annotation.type().isProduceIv()) {
            iv = RandomStrUtil.getRandomNumber(annotation.type().getIvLength());
        }

        if (StringUtils.hasText(annotation.SecretKey())) {
            secretKey = annotation.SecretKey();
        }

        EncodingType encodingType = apiCryptoConfig.getEncodingType();
        if (!annotation.encodingType().equals(EncodingType.DEFAULT)) {
            encodingType = annotation.encodingType();
        }

        String encryptData;

        try {
            encryptData = CryptoUtil.symmetric(
                    annotation.type().getType(),
                    annotation.type().getMethod(),
                    Cipher.ENCRYPT_MODE,
                    secretKey,
                    json,
                    encodingType,
                    iv,
                    apiCryptoConfig.getCharset()
            );

        } catch (Exception e) {
            ApiCryptoExceptionType exceptionType = ApiCryptoExceptionType.ENCRYPTION_FAILED;
            logger.error(exceptionType.getMessage() + " ERROR：" + e.getMessage());
            throw new ApiEncryptException(exceptionType);
        }

        // 使用默认响应体
        ApiCryptoBody apiCryptoBody = new ApiCryptoBody().setData(encryptData);
        if (annotation.type().isProduceIv()) {
            apiCryptoBody.setIv(iv);
        }

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

    /**
     * 获取 秘钥
     *
     * @param annotation: 执行注解
     * @return java.lang.String
     * @author hermes-di
     **/
    private String secretKey(SymmetricCrypto annotation) {
        String secretKey = apiCryptoConfig.getSymmetric().get(annotation.type().getType());

        if (StringUtils.hasText(annotation.SecretKey())) {
            secretKey = annotation.SecretKey();
        }

        if (!StringUtils.hasText(secretKey)) {
            ApiCryptoExceptionType exceptionType = ApiCryptoExceptionType.NO_SECRET_KEY;
            logger.error(exceptionType.getMessage() + " ERROR：(无效的秘钥,请在配置文件 symmetric 或注解中配置秘钥 secretKey )");
            throw new ApiEncryptException(exceptionType);
        }
        return secretKey;
    }

}
