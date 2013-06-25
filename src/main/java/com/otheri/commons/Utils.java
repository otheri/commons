package com.otheri.commons;

import java.io.File;

public class Utils {

	/**
	 * 删除文件或目录
	 * 
	 * @param file
	 */
	public static void deleteFile(File file) {
		if (file.exists()) {
			if (file.isDirectory()) {
				File[] files = file.listFiles();
				if (files != null) {
					for (File f : files) {
						deleteFile(f);
					}
				}
			} else {
				file.delete();
			}
		}
	}
}
