package com.zzn.aenote.http.utils;

import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.Date;

public class UtilUniqueKey {
	private static SecureRandom seederStatic = null;
	private static byte addr[] = null;
	private static String midValueStatic = null;
	private SecureRandom seeder = null;

	static {
		try {
			addr = "127.0.0.1".getBytes();
			StringBuffer buffer = new StringBuffer(8);
			buffer.append(toHex(toInt(addr), 8));
			midValueStatic = buffer.toString();
			seederStatic = new SecureRandom();
			seederStatic.nextInt();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public UtilUniqueKey() {
		StringBuffer buffer = new StringBuffer(16);
		buffer.append(midValueStatic);
		buffer.append(toHex(System.identityHashCode(this), 8));
		buffer.toString();
		seeder = new SecureRandom();
		seeder.nextInt();
	}

	public static String getKey(Object obj) {
		StringBuffer uid = new StringBuffer(32);

		// get the system time
		long currentTimeMillis = System.currentTimeMillis();
		uid.append(toHex((int) (currentTimeMillis & -1L), 8));

		// get the internet address
		uid.append(midValueStatic);

		// get the object hash value
		uid.append(toHex(System.identityHashCode(new Date()), 8));

		// get the random number
		uid.append(toHex(getRandom(), 8));

		return uid.toString();
	}

	public static String getKey() {
		StringBuffer uid = new StringBuffer(32);

		// get the system time
		long currentTimeMillis = System.currentTimeMillis();
		uid.append(toHex((int) (currentTimeMillis & -1L), 8));

		// get the internet address
		uid.append(midValueStatic);

		// get the random number
		uid.append(toHex(getRandom(), 8));

		return uid.toString();
	}

	private static String toHex(int value, int length) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'A', 'B', 'C', 'D', 'E', 'F' };
		StringBuffer buffer = new StringBuffer(length);
		int shift = length - 1 << 2;
		for (int i = -1; ++i < length;) {
			buffer.append(hexDigits[value >> shift & 0xf]);
			value <<= 4;
		}

		return buffer.toString();
	}

	private static int toInt(byte[] bytes) {
		int value = 0;
		for (int i = -1; ++i < bytes.length;) {
			value <<= 8;
			value |= 0x00FF & bytes[i];
		}

		return value;
	}

	private static synchronized int getRandom() {
		return seederStatic.nextInt();
	}

	public static void main(String[] args) throws UnknownHostException {
		System.out.println(UtilUniqueKey.getKey(""));
	}
}
