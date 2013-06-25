package com.otheri.commons.http;

import com.otheri.commons.io.Input;

/**
 * Http任务监听器，用于异步接受Http的调用结果。
 * 
 * 值得提出的是需要注意，onCancel，onSuccess和onFailure方法，都是执行在最初创建HttpTask的线程中。
 * 这样做可以减少很多代码编写方面的麻烦， 例如onSuccess后更新界面，如果不这样做，就需要再启动线程去刷新界面。
 * 
 * @author Administrator
 * 
 */
public interface HttpListener {

	/**
	 * 连接成功，可以读取数据，运行在独立线程中
	 */
	public Object onConnect(HttpTask httpTask, Input in) throws Exception;

	/**
	 * 运行在Handler创建线程中
	 */
	public void onCancel(HttpTask httpTask);

	/**
	 * 运行在Handler创建线程中
	 */
	public void onSuccess(HttpTask httpTask, Object obj);

	/**
	 * 运行在Handler创建线程中
	 */
	public void onFailure(HttpTask httpTask, String error);
}
