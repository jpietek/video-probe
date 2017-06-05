package com.video.probe.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProbeResult {

	private boolean isSuccess;
	private String message;
	private Probe result;

	public ProbeResult(boolean isSuccess, String message) {
		super();
		this.isSuccess = isSuccess;
		this.message = message;
	}
	
	public ProbeResult(boolean isSuccess, String message, Probe result) {
		super();
		this.isSuccess = isSuccess;
		this.message = message;
		this.result = result;
	}
	
}
