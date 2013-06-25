package com.otheri.commons.cache;

import java.util.Iterator;
import java.util.WeakHashMap;

/**
 * 对象缓存，使用WeakHashMap实现，当内存不够或者缓存对象数量超过预设限额时，会自动释放部分内容（释放1/3，隔两个释放一个，如有必要，
 * 将来可以改进释放算法）。
 * 
 * 小心WeakHashMap不支持线程同步，HashMap都有这个问题，因此使用了synchronized。将来可以改进同步性能。
 * 
 * @param <K>
 *            key
 * @param <V>
 *            value
 */
public class WeakObjectCache<K, V> {

	private static final int MAX_SIZE = 256;

	private WeakHashMap<K, V> cache;
	private int maxSize;

	public WeakObjectCache() {
		this(MAX_SIZE);
	}

	/**
	 * 
	 * @param maxSize
	 *            所缓存对象的最大数量
	 */

	public WeakObjectCache(int maxSize) {
		this.cache = new WeakHashMap<K, V>();
		this.maxSize = maxSize;
	}

	/**
	 * 清空对象缓存
	 */
	public synchronized void clear() {
		cache.clear();
	}

	/**
	 * 是否包含关键字为key的对象
	 * 
	 * @param key
	 * @return
	 */
	public synchronized boolean containsKey(K key) {
		return cache.containsKey(key);
	}

	/**
	 * 将一个对象放入缓存
	 * 
	 * @param key
	 *            关键字
	 * @param value
	 *            值
	 */
	public synchronized void put(K key, V value) {
		// Log.e(getClass().getSimpleName(), "MEMORY------------put>" + uri);
		int size = cache.size();
		if (size >= maxSize) {
			// 越界，释放掉三分之一
			Iterator<K> iterator = cache.keySet().iterator();
			int count = 0;
			while (iterator.hasNext()) {
				iterator.next();
				if (count % 3 == 0) {
					iterator.remove();
				}
				count++;
			}
		}
		cache.put(key, value);
	}

	/**
	 * 根据所给的key，从缓存中读取对象
	 * 
	 * @param key
	 *            关键字
	 * @return 返回key所对应的对象，如果没有则返回null
	 */
	public synchronized V get(K key) {
		// Log.e(getClass().getSimpleName(), "MEMORY------------get>" + uri);
		return cache.get(key);
	}

}
