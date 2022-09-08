package com.sdps.common.model.user;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class LoginUserVo implements Serializable {
	private static final long serialVersionUID = 1L;
	private String ip;
	private String date;
	private String token;
}
