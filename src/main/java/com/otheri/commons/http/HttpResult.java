package com.otheri.commons.http;

import com.otheri.commons.io.Input;

public class HttpResult {
	private boolean result;
	private Input in;
	private String error;

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public Input getIn() {
		return in;
	}

	public void setIn(Input in) {
		this.in = in;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

}
