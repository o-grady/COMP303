package ca.mcgill.cs.comp303.capone.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.apache.commons.io.FileUtils;

import ca.mcgill.cs.comp303.capone.loaders.ParliamentLoader;
import ca.mcgill.cs.comp303.capone.loaders.op.OpenParliamentFileLoader;

/**
 * An object representing a graph of data about the Canadian parliament, including MPs, speeches, etc.
 * 
 * There should only ever be one instance of this class in any given JVM.
 * It is permissible to access this instance through a global variable.
 * 
 */
public class Parliament
{
	private ArrayList<ParliamentObserver> aObservers = new ArrayList<ParliamentObserver>();
	
	private Map<String, MP> aMPTable = new HashMap<String, MP>();  //Hashmap for MPs
	
	/**
	 * Constructor for the Parliament object.
	 * 
	 */
	public Parliament() {}


	/**
	 * Adds an MP to the parliament object.
	 * If an MP or Key is added that is already in the table, old MP and key are overridden
	 * @param pMP the MP object to be added.
	 * @param pKey the primary key of the MP being added.
	 */
	public void addMP(MP pMP, String pKey)
	{
		if(pMP == null || pKey == null)
		{
			return;
		}
		else if(aMPTable.containsValue(pMP))
		{
			aMPTable.values().remove(pMP);
			aMPTable.put(pKey, pMP);
			return;
		}
		else if(aMPTable.containsKey(pKey))
		{
			aMPTable.remove(pKey);
			aMPTable.put(pKey, pMP);
			return;
		}
		aMPTable.put(pKey, pMP);
	}
	
	/**
	 * Removes an MP from the aMPTable.
	 * Created to support the refresh functionality class.
	 * @author kdick
	 * @param pMP : MP to be deleted.
	 * @param pKey : Key of the MP to be deleted.
	 */
	public void removeMP(MP pMP, String pKey)
	{
		if (pMP == null || pKey == null)
		{
			return;
		}
		else if (aMPTable.containsValue(pMP) && aMPTable.containsKey(pKey))
		{
			aMPTable.values().remove(pMP);
			aMPTable.remove(pKey);
			return;
		}
	}
	
	/**
	 * Returns the MP with this key, or null if no information is available.
	 * @param pMP the key of the MP to get.
	 * @return the MP object corresponding the the key.
	 */
	public MP getMP(String pMP)
	{
		if(aMPTable.containsKey(pMP))
		{
			return aMPTable.get(pMP);
		}
		return null;
	}
	
	/**
	 * Returns MPTable.
	 * @return a Map of MPs and Keys
	 */
	public Map<String, MP> getAllMPs()
	{
		return aMPTable; 

	}
	 /**
	  * Adds a new observer to the Parliament.
	  * @param pObs : The observer to be added
	  */
	public void addObserver(ParliamentObserver pObs)
	{
		if(pObs == null)
		{
			return;
		}
		aObservers.add(pObs);
	}
	 /**
	  * Alerts all Observers, telling them to alert all user profiles.
	  * @param pUsers : The users to be updated.
	  */
	public void notifyObsevers(ArrayList<UserProfile> pUsers)
	{
		for(ParliamentObserver o: aObservers)
		{
			o.update(this, pUsers);
		}
	}
	
	/** 
	 * Returns an arraylist of all speeches made by MP's in parliament.
	 * @return arraylist of speeches
	 */
	public ArrayList<Speech> getAllSpeeches()
	{
		ArrayList<Speech> ret = new ArrayList<Speech>();
		for(MP aMP : this.getAllMPs().values())
		{
			for(Speech s : aMP.getSpeeches())
			{
				ret.add(s);
			}
		}
		return ret;
	}
	
	/** Refreshes parliament with new data.
	 * @param pLoc Path to the new MP information.
	 */
	public void refreshParliament(String pLoc)
	{
		new ParliamentRefresh(this, pLoc);
	}
}

/**
 * A Functionality class that will update the current version of the Parliament with information of a new directory.
 * Refreshes Parliament by updating the current instance of parliament with new information stored in another Parliament.
 * This refresher class is called using the new operator, passing in the relative path to the directory required.
 * Update for M3: Will now delete MPs that exist in memory, but are not located on disc.
 * @author kdick
 *
 */
class ParliamentRefresh 
{
	private Parliament aCurrentParliament;
	private Parliament aNewParliament;
	
	/**
	 * Constructor for the two parliaments, that will then update the information in the current Parliament
	 * by calling each field refresher method.
	 * @param pNewMPDirectoryPath : The relative path name to the directory with the new MP information.
	 * 								Allows pointing to various directory names.
	 * @author kdick, MIKE
	 */
	ParliamentRefresh(Parliament pParliament, String pNewMPDirectoryPath)
	{
		aCurrentParliament = pParliament;
		aNewParliament = new Parliament();
		// Load up the new Parliament with the MP information from the directory pNewMPDirectoryPath.
		if(pNewMPDirectoryPath == null)
		{
			return;
		}
		File folder = new File(pNewMPDirectoryPath);
		File[] aJSON = folder.listFiles();
		
		/*Copying over rss and debates because loadRecentEvents only works on the data folder*/
		
		File aNewRSS = new File(folder.getParentFile().getAbsolutePath()+ File.separator + "rss");
		File aOldRSS = new File("." + File.separator + "data" + File.separator + "rss");
		File aNewDebates = new File(folder.getParentFile().getAbsolutePath()+ File.separator + "debates");  
		File aOldDebates = new File("." + File.separator + "data" + File.separator + "debates");
		try
		{
			FileUtils.copyDirectory(aNewRSS, aOldRSS);
		}
		catch (IOException e1)
		{
			Capone.LOGGER.log(Level.SEVERE, "Error updating RSS directory");
		}
		try
		{
			FileUtils.copyDirectory(aNewDebates, aOldDebates);
		}
		catch (IOException e1)
		{
			Capone.LOGGER.log(Level.SEVERE, "Error updating Debates directory");
		}
		ParliamentLoader loader = new OpenParliamentFileLoader();
		for (File f : aJSON)
		{
			try
			{
				loader.loadMP(f.getAbsolutePath(), aNewParliament);
			}
			catch(NullPointerException e)
			{
				Capone.LOGGER.log(Level.SEVERE, "Null Pointer Exception when loading file " + f.getName()+", probably a problem with directory path");
			}
		}
		
		for (MP aMP : aNewParliament.getAllMPs().values())
		{
			if (!(aCurrentParliament.getAllMPs().values().contains(aMP)))
			{
				aCurrentParliament.addMP(aMP, aMP.getPrimaryKey());
			}
		}
		// Deletes all the MPs that exist in Memory but do not exist on Disk.
		for (MP aMP : aCurrentParliament.getAllMPs().values())
		{
			if(!(aNewParliament.getAllMPs().values().contains(aMP)))
			{
				aCurrentParliament.removeMP(aMP, aMP.getPrimaryKey());
			}
		}
		
		// Now that all the information is loaded into the new parliament, each MP field is iterated over to update.
		for (MP mp : aCurrentParliament.getAllMPs().values())
		{
			MP aMP = aNewParliament.getMP(mp.getPrimaryKey());
			if (aMP == null)
			{
				// TODO :: Deal with cases when an MP is kicked out?
				Capone.LOGGER.log(Level.WARNING, "MP could not be found in NEW_PARLIAMENT.");
				continue;
			}
			// Call each method to individually deal with the update.
			refreshFamilyName(mp, aMP);
			refreshGivenName(mp, aMP);
			refreshName(mp, aMP);
			refreshEmail(mp, aMP);
			refreshPhoneNumber(mp, aMP);
			refreshMemberships(mp, aMP);
			refreshRSSFeedURL(mp, aMP);
			
			// Load the Events for each as well. This is done after the entire MP has been refreshed.
			try
			{
				loader.loadRecentEvents(aMP.getPrimaryKey(), aCurrentParliament);
			} 
			catch (NullPointerException e)
			{
				Capone.LOGGER.log(Level.WARNING, "NullPointerException when trying to refresh Events.");
			}
		}
		aCurrentParliament.notifyObsevers(Capone.getInstance().getUsers());
	}
	
	/**
	 * Additional constructor designed for testing.
	 * Takes 2 parliaments as inputs, making testing easier.
	 * @param pParliamentOld : Your Current Parliament
	 * @param pParliamentNew : The parliament to pull new data from
	 */
	ParliamentRefresh(Parliament pParliamentOld, Parliament pParliamentNew)
	{
		aCurrentParliament = pParliamentOld;
		aNewParliament = pParliamentNew;
		// Load up the new Parliament with the MP information from the directory pNewMPDirectoryPath.
		for (MP aMP : aNewParliament.getAllMPs().values())
		{
			if (!(aCurrentParliament.getAllMPs().values().contains(aMP)))
			{
				aCurrentParliament.addMP(aMP, aMP.getPrimaryKey());
			}
		}
		ParliamentLoader loader = new OpenParliamentFileLoader();
		// Now that all the information is loaded into the new parliament, each MP field is iterated over to update.
		for (MP mp : aCurrentParliament.getAllMPs().values())
		{
			MP aMP = aNewParliament.getMP(mp.getPrimaryKey());
			if (aMP == null)
			{
				// TODO :: Deal with cases when an MP is kicked out?
				Capone.LOGGER.log(Level.WARNING, "MP could not be found in NEW_PARLIAMENT.");
			}
			// Call each method to individually deal with the update.
			refreshFamilyName(mp, aMP);
			refreshGivenName(mp, aMP);
			refreshName(mp, aMP);
			refreshEmail(mp, aMP);
			refreshPhoneNumber(mp, aMP);
			refreshMemberships(mp, aMP);
			refreshRSSFeedURL(mp, aMP);	
		}
	}
	
	
	/**
	 * Refreshes the FamilyName field of each of the MPs if they are different.
	 * @param pOldMP : The original MP.
	 * @param pNewMP : The new MP from the specified directory.
	 * @author kdick
	 */
	void refreshFamilyName(MP pOldMP, MP pNewMP)
	{
		if (pOldMP.getFamilyName() != pNewMP.getFamilyName() && pNewMP.getFamilyName()!= null)
		{
			aCurrentParliament.getMP(pOldMP.getPrimaryKey()).setFamilyName(pNewMP.getFamilyName());
		} 
	}
	
	/**
	 * Refreshes the GivenName field of each of the MPs if they are different.
	 * @param pOldMP : The original MP.
	 * @param pNewMP : The new MP from the specified directory.
	 * @author kdick
	 */
	void refreshGivenName(MP pOldMP, MP pNewMP)
	{
		if (pOldMP.getGivenName() != pNewMP.getGivenName() && pNewMP.getGivenName()!= null )
		{
			aCurrentParliament.getMP(pOldMP.getPrimaryKey()).setGivenName(pNewMP.getGivenName());
		}
	}
	
	/**
	 * Refreshes the Name field of each of the MPs if they are different.
	 * @param pOldMP : The original MP.
	 * @param pNewMP : The new MP from the specified directory.
	 * @author kdick
	 */
	void refreshName(MP pOldMP, MP pNewMP)
	{
		if (pOldMP.getName() != pNewMP.getName() && pNewMP.getName()!= null)
		{
			aCurrentParliament.getMP(pOldMP.getPrimaryKey()).setName(pNewMP.getName());
		}	
	}
	
	/**
	 * Refreshes the Email field of each of the MPs if they are different.
	 * @param pOldMP : The original MP.
	 * @param pNewMP : The new MP from the specified directory.
	 * @author kdick
	 */
	void refreshEmail(MP pOldMP, MP pNewMP)
	{
		if (pOldMP.getEmail() != pNewMP.getEmail() && pNewMP.getEmail()!= null)
		{
			aCurrentParliament.getMP(pOldMP.getPrimaryKey()).setEmail(pNewMP.getEmail());
		}
	}
	
	/**
	 * Refreshes the PhoneNumber field of each of the MPs if they are different.
	 * @param pOldMP : The original MP.
	 * @param pNewMP : The new MP from the specified directory.
	 * @author kdick
	 */
	void refreshPhoneNumber(MP pOldMP, MP pNewMP)
	{
		if (pOldMP.getPhoneNumber() != pNewMP.getPhoneNumber() && pNewMP.getPhoneNumber()!= null)
		{
			aCurrentParliament.getMP(pOldMP.getPrimaryKey()).setPhoneNumber(pNewMP.getPhoneNumber());
		}
	}
	
	/**
	 * Overwrites entire memberships of the current MP with the new MP.
	 * The past memberships should not be able to be modified, thus overwriting
	 * will simply update the new memberships.
	 * @param pOldMP : The original MP.
	 * @param pNewMP : The new MP from the specified directory.
	 * @author kdick
	 */
	void refreshMemberships(MP pOldMP, MP pNewMP)
	{
		if(pNewMP.getMemberships()!= null)
		{
			aCurrentParliament.getMP(pOldMP.getPrimaryKey()).setMemberships(pNewMP.getMemberships());
		}
	}
		
	/**
	 * Refreshes the RSSFeedURL field of each of the MPs if they are different.
	 * @param pOldMP : The original MP.
	 * @param pNewMP : The new MP from the specified directory.
	 * @author kdick
	 */
	void refreshRSSFeedURL(MP pOldMP, MP pNewMP)
	{
		if (pOldMP.getRSSFeedURL() != pNewMP.getRSSFeedURL() && pNewMP.getRSSFeedURL()!= null)
		{
			aCurrentParliament.getMP(pOldMP.getPrimaryKey()).setRSSFeedURL(pNewMP.getRSSFeedURL());
		}
	}
}

