package com.villaekinoks.app.generic.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
public class GenericApiResponse<T> {

	private Integer code;

	private String message;
	
	private String responsecode;
	
	private T object;
	
	private String resulthashcode;

	public GenericApiResponse() {
		super();
		this.code = -1;
		this.message = "N/A";
		this.responsecode = "N/A";
		this.object = null;
		this.resulthashcode = null;
	}

	public GenericApiResponse(Integer code , String message) {
		super();
		this.code = code;
		this.message = message;
		this.responsecode = "N/A";
		this.object = null;
		this.resulthashcode = null;
	}

	public GenericApiResponse(Integer code, String message, String responsecode) {
		super();
		this.code = code;
		this.message = message;
		this.responsecode = responsecode;
		this.object = null;
		this.resulthashcode = null;
	}
	
	public GenericApiResponse(Integer code, String message, String responsecode, T object) {
		super();
		this.code = code;
		this.message = message;
		this.responsecode = responsecode;
		this.object = object;
		this.resulthashcode = null;
	}
	
	public GenericApiResponse(Integer code, String message, String responsecode, T object, String resulthashcode) {
		super();
		this.code = code;
		this.message = message;
		this.responsecode = responsecode;
		this.object = object;
		this.resulthashcode = resulthashcode;
	}
	
}

