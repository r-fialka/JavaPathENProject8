package com.openclassrooms.tourguide.helper;

public class InternalTestHelper {

	// Default number of internal test users (100,000).
	private static volatile int internalUserNumber = 100_000;

	public static void setInternalUserNumber(int internalUserNumber) {
		InternalTestHelper.internalUserNumber = internalUserNumber;
	}

	public static int getInternalUserNumber() {
		return internalUserNumber;
	}
}