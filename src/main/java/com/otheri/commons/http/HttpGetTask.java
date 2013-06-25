package com.otheri.commons.http;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import com.otheri.commons.io.Input;

/**
 * Http的Get请求任务，自动处理重定向。没有处理cookie，session等内容，只支持最基本的http调用。
 * 如有必要，可以使用HttpClient重新扩展一套API。
 * 
 * @author Administrator
 * 
 */
public class HttpGetTask extends HttpTask {

	public HttpGetTask(String url, HttpListener httpListener) {
		super(url, httpListener);

	}

	protected void init() {
		try {
			this.conn = (HttpURLConnection) new URL(url).openConnection();
			this.conn.setInstanceFollowRedirects(true);
			this.conn.setConnectTimeout(10000);
			this.conn.setReadTimeout(10000);

			conn.setDoInput(true);

			this.isCancel = false;
			this.isRunning = false;
		} catch (Exception e) {
			// Message message =
			// InternalHandler.obtainMessage(MESSAGE_ON_FAILURE,
			// new HttpTaskResult(this, httpListener, e));
			// message.sendToTarget();
			httpListener.onFailure(this, e.toString());
		}
	}

	protected HttpResult syncRunTask() {
		Input in = null;
		HttpResult result = new HttpResult();
		try {
			if (isCancel) {
				result.setResult(false);
				result.setError("http was canceled.");
				return result;
			}
			conn.connect();
			if (isCancel) {
				result.setResult(false);
				result.setError("http was canceled.");
				return result;
			}
			int responseCode = conn.getResponseCode();
			if (isCancel) {
				result.setResult(false);
				result.setError("http was canceled.");
				return result;
			}
			if (responseCode >= 200 && responseCode <= 299) {
				in = new Input(conn.getInputStream());
				// Object obj = httpListener.onConnect(HttpGetTask.this, in);
				// if (obj == null) {
				// throw new IOException("error in reading content");
				// }
				// Message message = InternalHandler.obtainMessage(
				// MESSAGE_ON_SUCCESS, new HttpTaskResult(this,
				// httpListener, obj));
				// message.sendToTarget();
				// httpListener.onSuccess(this, obj);
				result.setResult(true);
				result.setIn(in);
				return result;
			} else {
				throw new IOException("ResponseCode: " + responseCode);
			}
		} catch (Exception e) {
			// Message message =
			// InternalHandler.obtainMessage(MESSAGE_ON_FAILURE,
			// new HttpTaskResult(this, httpListener, e));
			// message.sendToTarget();
			// httpListener.onFailure(this, e.toString());
			result.setResult(false);
			result.setError(e.toString());
			return result;
		}
	}

	protected void runTask() {
		Input in = null;
		try {
			if (isCancel) {
				return;
			}
			conn.connect();
			if (isCancel) {
				return;
			}
			int responseCode = conn.getResponseCode();
			if (isCancel) {
				return;
			}
			if (responseCode >= 200 && responseCode <= 299) {
				in = new Input(conn.getInputStream());
				Object obj = httpListener.onConnect(HttpGetTask.this, in);
				if (obj == null) {
					throw new IOException("error in reading content");
				}
				// Message message = InternalHandler.obtainMessage(
				// MESSAGE_ON_SUCCESS, new HttpTaskResult(this,
				// httpListener, obj));
				// message.sendToTarget();
				httpListener.onSuccess(this, obj);
			} else {
				throw new IOException("ResponseCode: " + responseCode);
			}
		} catch (Exception e) {
			// Message message =
			// InternalHandler.obtainMessage(MESSAGE_ON_FAILURE,
			// new HttpTaskResult(this, httpListener, e));
			// message.sendToTarget();
			httpListener.onFailure(this, e.toString());
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
				}
			}
		}
	}
}
