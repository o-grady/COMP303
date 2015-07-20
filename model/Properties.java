package ca.mcgill.cs.comp303.capone.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.logging.Level;

import ca.mcgill.cs.comp303.capone.model.UserProfile.recommenderType;


/** Used to save application information that needs to persist across sessions. Properties is serializable and is
 * saved to disk whenever it is updated.
 * @author Grady
 *
 */
public final class Properties implements Serializable
{
	private static final long serialVersionUID = 1L;
	private String aDataLocation = null;
	private boolean aAutoLoad = false;
	private String aUserProfileSaveLocation = System.getProperty("user.home") + File.separator + "users.ser";
	private recommenderType aCurrentRecommender = recommenderType.CONTENT;
	private String aUserJsonLocation = "";
	
	Properties(){
		
	};
	/** Returns the properties saved to disk or a new properties if the saved file cannot be loaded.
	 * @return The saved properties if loaded properly.
	 */
	public static Properties getProperties()
	{
		Properties ret = readFromFile();
		if(ret == null)
		{
			ret = new Properties();
			//Creates a new Properties if there was an error loading the file.
		}
		return ret;
	}
	/** Getter for current data save location.
	 * @return current data save location.
	 */
	public String getaDataLocation()
	{
		return aDataLocation;
	}
	/** Setter for current date save location.
	 * @param pDataLocation new save location
	 */
	public void setaDataLocation(String pDataLocation)
	{
		this.aDataLocation = pDataLocation;
		writeToFile();
	}
	/** Getter for current user profile save location.
	 * @return current user profile save location.
	 */
	public String getaUserProfileSaveLocation()
	{
		if(aUserProfileSaveLocation == null)
		{
			return "";
		}
		return aUserProfileSaveLocation;
	}
	/** Setter for user profile save location.
	 * @param pUserProfileSaveLocation new user profile save location.
	 */
	public void setaUserProfileSaveLocation(String pUserProfileSaveLocation)
	{
		this.aUserProfileSaveLocation = pUserProfileSaveLocation;
		writeToFile();
	}

	/** Setter for user profile JSON save location.
	 * @param pJsonLocation new user profile JSON save location.
	 */
	public void setaUserJsonLocation(String pJsonLocation)
	{
		this.aUserJsonLocation = pJsonLocation;
		writeToFile();
	}
	
	/** Getter for user profile JSON save location.
	 *  @return aUserJsonLocation
	 */
	public String getaUserJsonLocation()
	{
		return this.aUserJsonLocation;
	}
	
	/** Getter for autoload boolean.
	 * @return autoload boolean.
	 */
	public boolean isaAutoLoad()
	{
		return aAutoLoad;
	}
	/** Setter for the autoload boolean.
	 * @param pAutoLoad new value for the autoload boolean.
	 */
	public void setaAutoLoad(boolean pAutoLoad)
	{
		this.aAutoLoad = pAutoLoad;
		writeToFile();
	}
	/** Getter for current recommender.
	 * @return current recommender
	 */
	public recommenderType getaCurrentRecommender()
	{
		return aCurrentRecommender;
	}
	/** Setter for the current recommender.
	 * @param pCurrentRecommender New recommender to be used.
	 */
	public void setaCurrentRecommender(recommenderType pCurrentRecommender)
	{
		this.aCurrentRecommender = pCurrentRecommender;
		writeToFile();
	}
	
	/** Writes the Properties object to file.
	 * 
	 */
	public void writeToFile()
	{
		try
		(
		FileOutputStream fout = new FileOutputStream("." + File.separator +"properties.ser");
		ObjectOutputStream oos = new ObjectOutputStream(fout);
		)
		{
			oos.writeObject(this);
		}
		catch(IOException e)
		{
			Capone.LOGGER.log(Level.WARNING, "Error saving properties.");
		}
	}

	
	/** Reads from the properties file stored on disk.
	 * @return The loaded Properties.
	 */
	public static Properties readFromFile()
	{
		Properties ret = new Properties();
		try
		(
		FileInputStream fin = new FileInputStream("." + File.separator +"properties.ser");
		ObjectInputStream ois = new ObjectInputStream(fin);
		)
		{
			ret = (Properties)ois.readObject();
		}
		catch(IOException | ClassNotFoundException e)
		{
			Capone.LOGGER.log(Level.WARNING, "Error loading properies.");
		}
		return ret;
	}

}
