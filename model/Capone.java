package ca.mcgill.cs.comp303.capone.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.swing.JOptionPane;

/**
 * The Singleton Capone Class.
 * Serves as the "head" of the application.
 * Contains the applications Logger, an instance of Parliament, and aggregate users via UserProfiles.
 * @author kdick
 */
public final class Capone 
{
	public static final Logger LOGGER = Logger.getLogger(Capone.class.getName());
	private static final Capone INSTANCE = new Capone();
	private static final Parliament PARLIAMENT = new Parliament();
	private static ArrayList<UserProfile> users = new ArrayList<UserProfile>();
	private static FileHandler fh = null;
	private static Properties aProperties = Properties.getProperties();
	private IReadWriteUsers aFileReadWriter = new BinaryReadWriteUsers();
	
	private Capone() 
	{
		initLogger();
		loadUsers(aFileReadWriter);
	}
	/** Initalizes the logging system for CAPONE.
	 * 
	 */
	public static void initLogger()
	{
		 try 
		 {
			 fh  = new FileHandler(System.getProperty("user.home")+File.separator+"capone-log.log", false);
		 } 
		 catch (SecurityException | IOException e) 
		 {
			 e.printStackTrace();
		 }
		 Logger l = Logger.getLogger("");
		 fh.setFormatter(new SimpleFormatter());
		 l.addHandler(fh);
		 l.setLevel(Level.CONFIG);
	}
	
	/**
	 * Getter for the Application Instance.
	 * Supports the Singleton Design Pattern.
	 * @return Instance of the Capone project.
	 */
	public static Capone getInstance()
	{
		return INSTANCE;		
	}

	/**
	 * Getter for the parliament object in capone.
	 * @return the static parliament object in the capone instance.
	 */
	public Parliament getParliament()
	{
		return PARLIAMENT;
	}
	
	/** 
	 * Getter for the list of UserProfiles contained in capone.
	 * @return The list of UserProfiles.
	 */
	public ArrayList<UserProfile> getUsers()
	{
		return (ArrayList<UserProfile>)users.clone();
	}
	
	/** Adds a userprofile to the list of users.
	 * @param pUser the userprofile to add.
	 */
	public void addUserProfile(UserProfile pUser)
	{
		if(pUser == null)
		{
			return;
		}
		users.add(pUser);
		saveUsers(aFileReadWriter);
	}
	/**
	 * Loads the list of Users from a file.
	 * @param pFileManager the File read/writer being used to load users.
	 */
	public void loadUsers(IReadWriteUsers pFileManager)
	{
		users = pFileManager.readUsersFromFile();
	}
	
	/**
	 * Saves the list of Users to a file.
	 * @param pFileManager the File read/writer being used to save users.
	 */
	public void saveUsers(IReadWriteUsers pFileManager)
	{
		pFileManager.writeUsersToFile(users);
	}
	
	/** Changes the way that files are being written and read.
	 * @param pFileManager The new FileManager being used.
	 */
	public void setReadWriteType(IReadWriteUsers pFileManager)
	{
		saveUsers(aFileReadWriter);
		aFileReadWriter = pFileManager;
	}
	
	/** Getter for the current preferred method for saving userprofiles.
	 * @return The current IReadWriteUsers being used.
	 */
	public IReadWriteUsers getaFileReadWriter()
	{
		return aFileReadWriter;
	}
	/** Returns the saved properties of the application.
	 * @return the Properties object that contains property information.
	 */
	public Properties getProperties()
	{
		return aProperties;
	}
}
