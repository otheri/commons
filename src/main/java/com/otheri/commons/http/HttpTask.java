package com.otheri.commons.http;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Http任务的抽象基类，包含HttpURLConnection初始化的一些公用方法。
 * 
 * 本类参考了AsyncTask的实现方式，但是修改了线程池部分。最多同时允许8个线程去访问网络（经过一些测试，在移动网络下表现还不错）
 * 
 * 建议在联网操作，访问硬件设备，读写文件等操作时，不要使用AsyncTask。这些情况的并发都可能造成系统异常中断，并且不抛出任何异常。
 * 
 * 同时还增加了cancel和retry等实用功能，直接调用即可。
 * 
 * @author Administrator
 * 
 */
public abstract class HttpTask implements Runnable {

	protected static final int MESSAGE_ON_FAILURE = 0x1;
	protected static final int MESSAGE_ON_CANCEL = 0x2;
	protected static final int MESSAGE_ON_SUCCESS = 0x3;

	protected String url;
	protected HttpListener httpListener;

	protected HttpURLConnection conn;

	protected HashMap<String, String> propertys;

	protected boolean isCancel;
	protected boolean isRunning;

	private static ExecutorService httpThreadPool;

	static {
		System.setProperty("http.keepAlive", "false");
		System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
		System.setProperty("sun.net.client.defaultReadTimeout", "10000");

		try {
			httpThreadPool = Executors.newFixedThreadPool(8);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected HttpTask(String url, HttpListener httpListener) {
		this.url = url;
		this.httpListener = httpListener;
		this.propertys = new HashMap<String, String>();
	}

	protected abstract void init();

	public void run() {
		isRunning = true;
		init();

		Iterator<Entry<String, String>> it = propertys.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, String> entry = it.next();
			conn.addRequestProperty(entry.getKey(), entry.getValue());
		}

		runTask();
		isRunning = false;
	}

	/**
	 * 执行http任务
	 */
	public void execute() {
		if (!isRunning) {
			httpThreadPool.execute(this);
		} else {
			throw new IllegalStateException(
					"Cannot execute http task,it`s already running.");
		}
	}

	public HttpResult syncExecute() {
		isRunning = true;
		init();

		Iterator<Entry<String, String>> it = propertys.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, String> entry = it.next();
			conn.addRequestProperty(entry.getKey(), entry.getValue());
		}

		isRunning = false;
		return syncRunTask();
	}

	protected abstract void runTask();

	protected abstract HttpResult syncRunTask();

	public String getUrl() {
		return url;
	}

	public void addRequestProperty(String key, String value) {
		propertys.put(key, value);
	}

	/**
	 * 取消http任务
	 */
	public void cancel() {
		this.isCancel = true;
		// Message message = InternalHandler.obtainMessage(MESSAGE_ON_CANCEL,
		// new HttpTaskResult(this, httpListener, null));
		// message.sendToTarget();
		httpListener.onCancel(this);
	}

	/**
	 * 重试http任务
	 */
	public void retry() {
		this.execute();
	}

	// protected static Handler InternalHandler = new Handler() {
	// final public void handleMessage(Message msg) {
	// HttpTaskResult httpTaskResult = (HttpTaskResult) msg.obj;
	// HttpTask httpTask = httpTaskResult.httpTask;
	// HttpListener httpListener = httpTaskResult.httpListener;
	// Object obj = httpTaskResult.obj;
	//
	// switch (msg.what) {
	// case MESSAGE_ON_FAILURE:
	// httpListener.onFailure(httpTask, obj.toString());
	// break;
	// case MESSAGE_ON_CANCEL:
	// httpListener.onCancel(httpTask);
	// break;
	// case MESSAGE_ON_SUCCESS:
	// httpListener.onSuccess(httpTask, obj);
	// break;
	// }
	// }
	// };

	// protected static class HttpTaskResult {
	// final public HttpTask httpTask;
	// final public HttpListener httpListener;
	// final public Object obj;
	//
	// HttpTaskResult(HttpTask httpTask, HttpListener httpListener, Object obj)
	// {
	// this.httpTask = httpTask;
	// this.httpListener = httpListener;
	// this.obj = obj;
	// }
	// }

}
