package com.sdps.common.crypto.bean;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

/**
 * 响应体、请求体
 *
 * @author hermes-di
 * @since 1.0.0.RELEASE
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiCryptoBody implements Serializable {
	private static final long serialVersionUID = 1818807978473433905L;

	/**
	 * 数据体
	 */
	private String data;

	/**
	 * 偏移量
	 */
	private String iv;

	/**
	 * 随机字符串
	 */
	private String nonce;

	/**
	 * 时间戳
	 */
	private Long timestamp;

	/**
	 * 签名
	 */
	private String signStr;

	public ApiCryptoBody() {
	}

	public String getData() {
		return data;
	}

	public ApiCryptoBody setData(String data) {
		this.data = data;
		return this;
	}

	public String getIv() {
		return iv;
	}

	public ApiCryptoBody setIv(String iv) {
		this.iv = iv;
		return this;
	}

	public String getSignStr() {
		return signStr;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public String getNonce() {
		return nonce;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public void setNonce(String nonce) {
		this.nonce = nonce;
	}

	public void setSignStr(String signStr) {
		this.signStr = signStr;
	}
}
