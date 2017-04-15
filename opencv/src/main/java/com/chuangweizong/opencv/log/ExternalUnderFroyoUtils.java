/*
 * Copyright (C) 2013  WhiteCat 白猫 (www.thinkandroid.cn)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.chuangweizong.opencv.log;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.StatFs;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @Title TAExternalUnderFroyoUtils
 * @Package com.ta.util.cache
 * @Description 缓存的工具类,Android 2.2以下版本使用
 * @author 白猫
 * @date 2013-1-20
 * @version V1.0
 */
public class ExternalUnderFroyoUtils
{
	/**
	 * 判断是否存在外部存储设备
	 * 
	 * @return 如果不存在返回false
	 */
	public static boolean hasExternalStorage()
	{
		Boolean externalStorage = Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
		return externalStorage;
	}

	/**
	 * 获取目录使用的空间大�?
	 * 
	 * @param path
	 *            �?查的路径路径
	 * @return 在字节的可用空间
	 */
	@SuppressWarnings("deprecation")
	public static long getUsableSpace(File path)
	{
		final StatFs stats = new StatFs(path.getPath());
		return (long) stats.getBlockSize() * (long) stats.getAvailableBlocks();
	}

	/**
	 * 获得外部应用程序缓存目录
	 * 
	 * @param context
	 *            上下文信�?
	 * @return 外部缓存目录
	 */
	public static File getExternalCacheDir(Context context)
	{
		final String cacheDir = "/Android/data/" + context.getPackageName()
				+ "/cache/";
		return new File(Environment.getExternalStorageDirectory().getPath()
				+ cacheDir);
	}

	/**
	 * �?查如果外部存储器是内置的或是可移动的�?
	 * 
	 * @return 如果外部存储是可移动�?(就像�?个SD�?)返回�? true,否则false�?
	 */
	public static boolean isExternalStorageRemovable()
	{
		return true;
	}

	/**
	 * �?个散列方�?,改变�?个字符串(如URL)到一个散列�?�合使用作为�?个磁盘文件名�?
	 */
	public static String hashKeyForDisk(String key)
	{
		String cacheKey;
		try
		{
			final MessageDigest mDigest = MessageDigest.getInstance("MD5");
			mDigest.update(key.getBytes());
			cacheKey = bytesToHexString(mDigest.digest());
		} catch (NoSuchAlgorithmException e)
		{
			cacheKey = String.valueOf(key.hashCode());
		}
		return cacheKey;
	}

	private static String bytesToHexString(byte[] bytes)
	{
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++)
		{
			String hex = Integer.toHexString(0xFF & bytes[i]);
			if (hex.length() == 1)
			{
				sb.append('0');
			}
			sb.append(hex);
		}
		return sb.toString();
	}

	/**
	 * 得到�?个可用的缓存目录(如果外部可用使用外部,否则内部)�?
	 * 
	 * @param context
	 *            上下文信�?
	 * @param uniqueName
	 *            目录名字
	 * @return 返回目录名字
	 */
	public static File getDiskCacheDir(Context context, String uniqueName)
	{
		// �?查是否安装或存储媒体是内置的,如果是这�?,试着使用
		// 外部缓存 目录
		// 否则使用内部缓存 目录
		final String cachePath = Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState()) ? getExternalCacheDir(context)
				.getPath() : context.getCacheDir().getPath();
		return new File(cachePath + File.separator + uniqueName);
	}

	/**
	 * 得到�?个可用的缓存目录(如果外部可用使用外部,否则内部)�?
	 * 
	 * @param context
	 *            上下文信�?
	 * @return 返回目录名字
	 */
	public static File getSystemDiskCacheDir(Context context)
	{
		// �?查是否安装或存储媒体是内置的,如果是这�?,试着使用
		// 外部缓存 目录
		// 否则使用内部缓存 目录
		final String cachePath = Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState()) ? getExternalCacheDir(context)
				.getPath() : context.getCacheDir().getPath();
		return new File(cachePath);
	}

	/**
	 * 为Bitmap返回�?个合适的缓存大小
	 * 
	 * @param bitmap
	 * @return size in bytes
	 */
	public static int getBitmapSize(Bitmap bitmap)
	{
		return bitmap.getRowBytes() * bitmap.getHeight();
	}

	public static int getMemoryClass(Context context)
	{
		return 1024 * 1024 * 5; // 5MB;
	}

}
