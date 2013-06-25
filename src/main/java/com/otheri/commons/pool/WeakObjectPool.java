package com.otheri.commons.pool;

/**
 * 对象池，使用数组实现，利用弱引用自动释放的特性，避免因操作不规范，不合法返还对象的问题。
 * 
 * @author cloud
 * 
 */
public class WeakObjectPool<T> {

	/**
	 * 对象池最大数量
	 */
	protected int maxSize;

	/**
	 * 当前可用的数量
	 */
	protected int count;

	/**
	 * 数组，存放对象或者对象的弱引用
	 */
	protected T[] objs;

	protected ObjectFactory<T> objectFactory;

	public WeakObjectPool(int maxSize, ObjectFactory<T> objectFactory) {
		if (maxSize <= 0) {
			this.maxSize = 32;
		} else {
			this.maxSize = maxSize;
		}
		this.objectFactory = objectFactory;
		this.count = 0;
		this.objs = (T[]) new Object[maxSize];
	}

	public synchronized T borrowObject() {
		if (count > 0) {
			// 有可用对象，借出并释放引用
			// System.out.println("yes & borrow. count=" + count);
			count--;
			T object = objs[count];
			objs[count] = null;
			return object;
		} else {
			// System.out.println("no & new. count=" + count);
			return objectFactory.newObject();
		}

	}

	public synchronized void returnObject(T object) {
		if (count < maxSize) {
			// System.out.println("not full & return. count=" + count);
			objs[count] = object;
			count++;
		} else {
			// 如果池满则丢弃不要
			// System.out.println("full & throw. count=" + count);
		}
	}

}
