package com.villaekinoks.app.utils;

import java.time.format.DateTimeFormatter;
import java.util.Random;

public class RandomizerUtils {
	
	private RandomizerUtils() {
		throw new IllegalStateException("Utility class");
	}
	
	private static Random rnd = new Random();

	public static String getRandomAlphaNumeric(int length) {
		String saltChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		StringBuilder salt = new StringBuilder();
		while (salt.length() < length) {
			int index = (rnd.nextInt(saltChars.length()));
			salt.append(saltChars.charAt(index));
		}
		return salt.toString();
	}

	public static String getRandomAlphaNumericSymbolic(int length) {
		String saltChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890!?_$#:;,+-=";
		StringBuilder salt = new StringBuilder();

		while (salt.length() < length) {
			int index = (rnd.nextInt(saltChars.length()));
			salt.append(saltChars.charAt(index));
		}

		return salt.toString();
	}

	public static String getRandomSymbolic(int length) {
		String saltChars = "!?_$#:;,+-=";
		StringBuilder salt = new StringBuilder();

		while (salt.length() < length) {
			int index = (rnd.nextInt(saltChars.length()));
			salt.append(saltChars.charAt(index));
		}

		return salt.toString();
	}

	public static String getRandomStrongPassword() {
		String passwordFirst = getRandomAlphaNumeric(5);
		String firstCharacter = getRandomSymbolic(1);
		String passwordSecond = getRandomAlphaNumeric(4);
		return passwordFirst + firstCharacter + passwordSecond;
	}

	public static String getRandomNumeric(int length) {
		String saltChars = "1234567890";
		StringBuilder salt = new StringBuilder();

		while (salt.length() < length) {
			int index = (rnd.nextInt(saltChars.length()));
			salt.append(saltChars.charAt(index));
		}

		return salt.toString();
	}

	public static String getRandomInvoiceIssueNumber() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
		return dtf.format(java.time.LocalDate.now()) + getRandomNumeric(4);
	}

	public static String getRandomAppFileName() {
		String saltChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		StringBuilder salt = new StringBuilder();

		while (salt.length() < 10) {
			int index = (rnd.nextInt(saltChars.length()));
			salt.append(saltChars.charAt(index));
		}
		salt.append("_");
		while (salt.length() < 26) {
			int index = (rnd.nextInt(saltChars.length()));
			salt.append(saltChars.charAt(index));
		}
		salt.append("_");
		while (salt.length() < 37) {
			int index = (rnd.nextInt(saltChars.length()));
			salt.append(saltChars.charAt(index));
		}

		return salt.toString();
	}

	public static String getRandomAlphaNumeric() {
		return getRandomAlphaNumeric(15);
	}

	public static int getRandomPositiveInteger(int i) {
		return rnd.nextInt(i);
	}
}
