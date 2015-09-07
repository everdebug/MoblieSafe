package com.txy.mobliesafe.utils;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {
	public static String MD5Password(String password) {

		try {
			// 得到信息摘要
			MessageDigest digest = MessageDigest.getInstance("md5");
			byte[] bs = digest.digest(password.getBytes());
			StringBuffer buffer = new StringBuffer();
			// 算法加密,每一位都进行与运算
			for (byte b : bs) {
				int number = b & 0xff;
				String str = Integer.toString(number);
				if (str.length() == 1) {
					buffer.append(0);
				}
				buffer.append(str);
			}
			return buffer.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return "";
		}

	}

	/**
	 * 获得文件的MD5值
	 * 
	 * @param path
	 *            文件路径
	 * @return 文件的MD5值
	 */
	public static String getFileMd5(String path)
	{
		try {
			// 获取一个文件的特征信息，签名信息。
			File file = new File(path);
			// md5
			MessageDigest digest = MessageDigest.getInstance("md5");
			FileInputStream fis = new FileInputStream(file);
			byte[] buffer = new byte[1024];
			int len = -1;
			while ((len = fis.read(buffer)) != -1) {
				digest.update(buffer, 0, len);
			}
			byte[] result = digest.digest();
			StringBuffer sb = new StringBuffer();
			for (byte b : result) {
				// 与运算
				int number = b & 0xff;// 加盐
				String str = Integer.toHexString(number);
				// System.out.println(str);
				if (str.length() == 1) {
					sb.append("0");
				}
				sb.append(str);
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	/*
	 * try { File file = new File(path); MessageDigest digest =
	 * MessageDigest.getInstance("md5"); FileInputStream fis = new
	 * FileInputStream(file); byte[] buffer = new byte[1024]; int len = -1;
	 * while ((len = fis.read(buffer)) != -1) { digest.update(buffer, 0, len); }
	 * byte[] result = digest.digest();
	 * 
	 * StringBuffer sb = new StringBuffer(); // 算法加密,每一位都进行与运算 for (byte b :
	 * result) { int number = b & 0xff; String str = Integer.toString(number);
	 * if (str.length() == 1) { sb.append(0); } sb.append(str); } return
	 * sb.toString(); } catch (Exception e) { e.printStackTrace(); return ""; }
	 * }
	 */
}
