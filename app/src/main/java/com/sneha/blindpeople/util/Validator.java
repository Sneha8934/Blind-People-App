package com.sneha.blindpeople.util;

import android.graphics.Color;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {
	
	private static Pattern pattern;
	private static Matcher matcher;
	public static final int VALID_TEXT_COLOR = Color.BLACK;
	public static final int INVALID_TEXT_COLOR = Color.RED;
	
	public static String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
											+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	
	public static String NUMBER_PATTERN = "^[0-9]+$";
	
	public static String ALPHABET_PATTERN = "^[a-zA-Z]+$";
	
	public static String ALPHANUMERIC_PATTERN = "^[a-zA-Z0-9]+$";
	
	
	public static boolean IsEmail(String hex)
	{
		pattern = Pattern.compile(EMAIL_PATTERN);
		matcher = pattern.matcher(hex);
		
		return matcher.matches();
	}
	
	public static boolean IsNumber(String hex)
	{
		pattern = Pattern.compile(NUMBER_PATTERN);
		matcher = pattern.matcher(hex);
		
		return matcher.matches();
	}

	public static boolean IsAlphabet(String hex)
	{
		pattern = Pattern.compile(ALPHABET_PATTERN);
		matcher = pattern.matcher(hex);

		return matcher.matches();
	}

	public static boolean IsAlphaNumeric(String hex)
	{
		pattern = Pattern.compile(ALPHANUMERIC_PATTERN);
		matcher = pattern.matcher(hex);

		return matcher.matches();
	}

	public static boolean hasText(EditText et)
	{
		boolean validated = true;
		
		String text = et.getText().toString().trim();
		
		if(text.length() == 0){
			validated = false;
		}
		
		return validated;
	}
}
