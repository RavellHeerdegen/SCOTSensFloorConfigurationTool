package de.ableitner.barrierfreeSmarthome.common;

/**
 * This class provides static methods to check parameter values.
 * 
 * This class is thread safe.
 * 
 * @author Tobias Ableitner
 *
 */
public class Checker {
	

	// *********************************************************************************************************************************************
	// *********************************************************************************************************************************************
	// attributes
	// *********************************************************************************************************************************************
	// *********************************************************************************************************************************************




	// *********************************************************************************************************************************************
	// *********************************************************************************************************************************************
	// constructors
	// *********************************************************************************************************************************************
	// *********************************************************************************************************************************************




	// *********************************************************************************************************************************************
	// *********************************************************************************************************************************************
	// getters and setters
	// *********************************************************************************************************************************************
	// *********************************************************************************************************************************************

	


	// *********************************************************************************************************************************************
	// *********************************************************************************************************************************************
	// abstract methods
	// *********************************************************************************************************************************************
	// *********************************************************************************************************************************************

	
	

	// *********************************************************************************************************************************************
	// *********************************************************************************************************************************************
	// override methods
	// *********************************************************************************************************************************************
	// *********************************************************************************************************************************************




	// *********************************************************************************************************************************************
	// *********************************************************************************************************************************************
	// public methods
	// *********************************************************************************************************************************************
	// *********************************************************************************************************************************************

	/**
	 * Checks a string against null and emptiness.
	 * @param stringToCheck the string which should be checked
	 * @param parameterName the name of the parameter which value is checked
	 * @throws IllegalArgumentException if stringToCheck is null or empty
	 */
	public static void checkNullAndEmptiness(String stringToCheck, String parameterName){
		Checker.checkNull(stringToCheck, parameterName);
		Checker.checkEmptiness(stringToCheck, parameterName);
	}
	
	/**
	 * Checks an object against null.
	 * @param objectToCheck the object which should be checked
	 * @param parameterName the name of the parameter which value is checked
	 * @throws IllegalArgumentException if objectToCheck is null
	 */
	public static void checkNull(Object objectToCheck, String parameterName){
		if(objectToCheck == null){
			throw new IllegalArgumentException("The parameter " + parameterName + " must not be null!");
		}
	}
	
	/**
	 * Checks a string against emptiness.
	 * @param stringToCheck the string which should be checked
	 * @param parameterName the name of the parameter which value is checked
	 * @throws IllegalArgumentException if stringToCheck is not null but empty
	 */
	public static void checkEmptiness(String stringToCheck, String parameterName){
		if(stringToCheck != null && stringToCheck.isEmpty()){
			throw new IllegalArgumentException("The parameter " + parameterName + " must not be empty!");
		}
	}
	
	/**
	 * Checks whether an integer is greater equals 0 or not.
	 * @param intToCheck the integer which should be checked
	 * @param parameterName the name of the parameter which value is checked
	 * @throws IllegalArgumentException if intToCheck is smaller 0
	 */
	public static void checkPositiveInteger(int intToCheck, String parameterName){
		if(intToCheck < 0){
			throw new IllegalArgumentException("The parameter " + parameterName + " must be greater euqals 0!");
		}
	}
	
	/**
	 * Checks whether an integer is greater than a minimum or not.
	 * @param intToCheck the integer which should be checked.
	 * @param parameterName the name of the parameter which value is checked
	 * @param minimum the minimum
	 * @throws IllegalArgumentException if intToCheck is smaller equals minimum
	 */
	public static void checkIntegerGreater(int intToCheck, String parameterName, int minimum){
		if(intToCheck <= minimum){
			throw new IllegalArgumentException("The parameter " + parameterName + " must be greater " + minimum + "!");
		}
	}
	
	/**
	 * Checks whether an integer is greater equals than a minimum or not.
	 * @param intToCheck the integer which should be checked.
	 * @param parameterName the name of the parameter which value is checked
	 * @param minimum the minimum
	 * @throws IllegalArgumentException if intToCheck is smaller minimum
	 */
	public static void checkIntegerGreaterEquals(int intToCheck, String parameterName, int minimum){
		if(intToCheck < minimum){
			throw new IllegalArgumentException("The parameter " + parameterName + " must be greater euqals " + minimum + "!");
		}
	}
	
	/**
	 * Checks whether an integer is lower equals than a maximum or not.
	 * @param intToCheck the integer which should be checked.
	 * @param parameterName the name of the parameter which value is checked
	 * @param maximum the maximum
	 * @throws IllegalArgumentException if intToCheck is greater or equals maximum
	 */
	public static void checkIntegerLower(int intToCheck, String parameterName, int maximum) {
		if(intToCheck >= maximum) {
			throw new IllegalArgumentException("The parameter " + parameterName + " must be lower " + maximum + "!");
		}
	}
	
	/**
	 * Checks whether an integer is lower equals than a maximum or not.
	 * @param intToCheck the integer which should be checked.
	 * @param parameterName the name of the parameter which value is checked
	 * @param maximum the maximum
	 * @throws IllegalArgumentException if intToCheck is greater maximum
	 */
	public static void checkIntegerLowerEquals(int intToCheck, String parameterName, int maximum) {
		if(intToCheck > maximum) {
			throw new IllegalArgumentException("The parameter " + parameterName + " must be lower equals " + maximum + "!");
		}
	}
	
	/**
	 * Checks whether an integer is in a value range lower equals maximum and greater equals minimum.
	 * @param intToCheck the integer which should be checked.
	 * @param parameterName the name of the parameter which value is checked
	 * @param minimum the mininum / lower bound bound of the range. It must be lower equals than the maximum. Otherwise an {@link IllegalArgumentException} will be thrown!
	 * @param maximum the maximum / upper boudn of the range. It must be greater equals than the minimum. Otherwise an {@link IllegalArgumentException} will be thrown!
	 * @throws IllegalArgumentException if intToCheck is not in the defined range or the parameters minimum and maximum are not defining a valid range.
	 */
	public static void checkIntegerInValueRange(int intToCheck, String parameterName, int minimum, int maximum) {
		if(minimum > maximum) {
			throw new IllegalArgumentException("The parameters minimum and maximum are not defining a valid range! The parameter minimum has to be lower equal to the maximum parameter.");
		}
		if(intToCheck > maximum || intToCheck < minimum) {
			throw new IllegalArgumentException("The parameter " + parameterName + " must be lower equals " + maximum + " and greater equals " + minimum + "!");
		}
	}
	
	/**
	 * Checks whether a long is in a value range lower equals maximum and greater equals minimum.
	 * @param floatToCheck the long which should be checked.
	 * @param parameterName the name of the parameter which value is checked
	 * @param minimum the mininum / lower bound bound of the range. It must be lower equals than the maximum. Otherwise an {@link IllegalArgumentException} will be thrown!
	 * @param maximum the maximum / upper boudn of the range. It must be greater equals than the minimum. Otherwise an {@link IllegalArgumentException} will be thrown!
	 * @throws IllegalArgumentException if longToCheck is not in the defined range or the parameters minimum and maximum are not defining a valid range.
	 */
	public static void checkLongInValueRange(long floatToCheck, String parameterName, long minimum, long maximum) {
		if(minimum > maximum) {
			throw new IllegalArgumentException("The parameters minimum and maximum are not defining a valid range! The parameter minimum has to be lower equal to the maximum parameter.");
		}
		if(floatToCheck > maximum || floatToCheck < minimum) {
			throw new IllegalArgumentException("The parameter " + parameterName + " must be lower equals " + maximum + " and greater equals " + minimum + "!");
		}
	}
	
	/**
	 * Checks whether a float is in a value range lower equals maximum and greater equals minimum.
	 * @param floatToCheck the float which should be checked.
	 * @param parameterName the name of the parameter which value is checked
	 * @param minimum the mininum / lower bound bound of the range. It must be lower equals than the maximum. Otherwise an {@link IllegalArgumentException} will be thrown!
	 * @param maximum the maximum / upper boudn of the range. It must be greater equals than the minimum. Otherwise an {@link IllegalArgumentException} will be thrown!
	 * @throws IllegalArgumentException if floatToCheck is not in the defined range or the parameters minimum and maximum are not defining a valid range.
	 */
	public static void checkFloatInValueRange(float floatToCheck, String parameterName, float minimum, float maximum) {
		if(minimum > maximum) {
			throw new IllegalArgumentException("The parameters minimum and maximum are not defining a valid range! The parameter minimum has to be lower equal to the maximum parameter.");
		}
		if(floatToCheck > maximum || floatToCheck < minimum) {
			throw new IllegalArgumentException("The parameter " + parameterName + " must be lower equals " + maximum + " and greater equals " + minimum + "!");
		}
	}
	
	/**
	 * Checks whether a long is greater equals 0 or not.
	 * @param longToCheck the long which should be checked
	 * @param parameterName the name of the parameter which value is checked
	 * @throws IllegalArgumentException if longToCheck is smaller 0
	 */
	public static void checkPositiveLong(long longToCheck, String parameterName){
		if(longToCheck < 0){
			throw new IllegalArgumentException("The parameter " + parameterName + " must be greater euqals 0!");
		}
	}

	/**
	 * Checks whether a long is greater than a minimum or not.
	 * @param longToCheck the long which should be checked.
	 * @param parameterName the name of the parameter which value is checked
	 * @param minimum the minimum
	 * @throws IllegalArgumentException if longToCheck is smaller equals minimum
	 */
	public static void checkLongGreater(long longToCheck, String parameterName, long minimum){
		if(longToCheck <= minimum){
			throw new IllegalArgumentException("The parameter " + parameterName + " must be greater " + minimum + "!");
		}
	}

	/**
	 * Checks whether a long is greater equals than a minimum or not.
	 * @param longToCheck the long which should be checked.
	 * @param parameterName the name of the parameter which value is checked
	 * @param minimum the minimum
	 * @throws IllegalArgumentException if longToCheck is smaller equals minimum
	 */
	public static void checkLongGreaterEquals(long longToCheck, String parameterName, long minimum){
		if(longToCheck < minimum){
			throw new IllegalArgumentException("The parameter " + parameterName + " must be greater euqals " + minimum + "!");
		}
	}
	
	/**
	 * Checks whether a string is a valid integer.
	 * @param integerAsString the string which should be checked
	 * @return true if the string is a valid integer and false if not
	 */
	public static boolean isStringInteger(String integerAsString){
		return integerAsString.matches("[0]|[1-9][0-9]*");
	}




	// *********************************************************************************************************************************************
	// *********************************************************************************************************************************************
	// protected methods
	// *********************************************************************************************************************************************
	// *********************************************************************************************************************************************




	// *********************************************************************************************************************************************
	// *********************************************************************************************************************************************
	// private methods
	// *********************************************************************************************************************************************
	// *********************************************************************************************************************************************




	// *********************************************************************************************************************************************
	// *********************************************************************************************************************************************
	// inner classes
	// *********************************************************************************************************************************************
	// *********************************************************************************************************************************************
	
}