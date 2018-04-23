package de.fh_zwickau.simon.jaxRsJwtDemo.util;

public class UserDatabase {
	
	public static String getUserID(String login, String password)
	{
		if (login.equals("root") && password.equals("password"))
		{
			return "0000";
		}
		else if (login.equals("user") && password.equals("password"))
		{
			return "0001";
		}
		else return "";
	}
	
	public static boolean isAdmin(String userID)
	{
		if (userID.equals("0000"))
		{
			return true;
		}
		else return false;
	}
	
}
