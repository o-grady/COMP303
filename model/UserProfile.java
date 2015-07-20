package ca.mcgill.cs.comp303.capone.model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;

import com.google.gson.Gson;


/**User Profile class stores a list of MPs (as keys).
 * Stores a list of keywords (strings).
 * Should me notified to any change in parliament by Observer class.
 * @author MIKE
 *
 */
public class UserProfile implements Serializable
{
	private static final long serialVersionUID = 1L;
	private ArrayList <String> aMPs;
	private ArrayList <String> aKeywords;
	private ArrayList<Triple<Speech, MP>> aCurrentRecommendedList;
	private ArrayList<Speech> aReadSpeeches;

	private String aDataLocation = null; // Stores the relative location for loading data
	
	/** Constructor for the UserProfile Class; assigns MPs and Keywords.
	 * @param pMPs An arraylist of MP keys that are of interest to the user.
	 * @param pKeyWords An arraylsit of keywords that are of interest to the user.
	 */
	public UserProfile(ArrayList<String> pMPs, ArrayList<String> pKeyWords)
	{
		aMPs = pMPs;
		aKeywords = pKeyWords;
		updateRecommender(Capone.getInstance().getProperties().getaCurrentRecommender());
	}
	/** Default constuctor for UserProfile; sets aMPs and aKeywords to empty arraylists.
	 * 
	 */
	public UserProfile()
	{
		aMPs = new ArrayList<String>();
		aKeywords = new ArrayList<String>();
		aReadSpeeches = new ArrayList<Speech>();
		updateRecommender(Capone.getInstance().getProperties().getaCurrentRecommender());
	}
	/**
	 * Adds a MP to the User's list of associated MPs.
	 * MPs are loaded by their key.
	 * @param pMP key of the MP to be loaded
	 */
	public void addMP(String pMP)
	{
		if(pMP == null)
		{
			return;
		}
		aMPs.add(pMP);
		Capone.getInstance().saveUsers(Capone.getInstance().getaFileReadWriter());
	}
	/**
	 * Removes a MP to the User's list of associated MPs.
	 * MPs are removed by their key.
	 * @param pMP key of the MP to be removed
	 */
	public void removeMP(String pMP)
	{
		if(aMPs.contains(pMP))
		{
			aMPs.remove(pMP);
			Capone.getInstance().saveUsers(Capone.getInstance().getaFileReadWriter());	
		}
		return;
	}
	/**
	 * Returns a copy of the list of MPs associated with the user.
	 * MPs are returned as keys.
	 * @return a copy of the list of MPs
	 */
	public ArrayList <String> getMPs()
	{
		ArrayList <String> aList = new ArrayList<String>(aMPs);
		Collections.copy(aList, aMPs);
		return aList;
	}
	/**
	 * Adds a keyword to the users list of associated keywords.
	 * @param pKeyword the keyword to be added
	 */
	public void addKeyword(String pKeyword)
	{
		if(pKeyword == null)
		{
			return;
		}
		aKeywords.add(pKeyword);
		Capone.getInstance().saveUsers(Capone.getInstance().getaFileReadWriter());
	}
	
	/** Removes a keyword from the UserProfile.
	 * @param pKeyword the keyword to be removed.
	 */
	public void removeKeyword(String pKeyword)
	{
		if(pKeyword == null || !this.aKeywords.contains(pKeyword))
		{
			return;
		}
		aKeywords.remove(pKeyword);
		Capone.getInstance().saveUsers(Capone.getInstance().getaFileReadWriter());
	}
	/** Returns a copy of the list of keywords associated with the user.
	 * 
	 * @return a copy of the list of keywords associated with the user
	 */
	public ArrayList <String> getKeywords()
	{
		ArrayList <String> aList = new ArrayList<String>(aKeywords);
		Collections.copy(aList, aKeywords);
		return aList;
	}
	/**
	 * Uses Strategy pattern to recommend speeches.
	 * @param pStrategy The recommender to be used
	 * @param pParliament The parliament speeches will be pulled from
	 * @return An ordered list of speeches
	 */
	public ArrayList<Speech> recommendSpeeches(ISpeechRecommender pStrategy, Parliament pParliament)
	{
		return pStrategy.rank(this, pParliament);
	}
	
	/**
	 * Returns a triple of the ranked speeches, their ranks, and the MP that said them.
	 * @param pStrategy The recommender to be user
	 * @param pParliament The parliament speeches will be pulled from
	 * @return An ordered triple of speeches
	 */
	public ArrayList<Triple<Speech , MP>> recommendSpeechesWithMP(ISpeechRecommender pStrategy, Parliament pParliament)
	{
		return pStrategy.rankedSpeechesWithMP(this, pParliament);
	}
	/**
	 * Checks each string in aMPs to see if it is still in the parliament.
	 * As MPs are stored by key, only their presence in the parliament needs to be updated.
	 * @param pParliament the Parliament to update from
	 */
	public void update(Parliament pParliament)
	{
		for(String aMP: aMPs)
		{
			if(pParliament.getMP(aMP) == null)
			{
				aMPs.remove(aMP);
				Capone.getInstance().saveUsers(Capone.getInstance().getaFileReadWriter());
			}
		}
	}
	/** Updates the current recommended list of speeches for the UserProfile. Call using
	 * updateRecommender(aProfile.getaRecType()) to update with current recommender. To change recommenders,
	 * call using the recommenderType enum, either CONTENT, SIMILARITY or VOTE.
	 * @param pRecType the type of recommender being used when updated.
	 */
	public void updateRecommender(recommenderType pRecType)
	{
		Capone.getInstance().getProperties().setaCurrentRecommender(pRecType);
		if(pRecType.equals(recommenderType.CONTENT))
		{
			aCurrentRecommendedList = this.recommendSpeechesWithMP(new ContentBasedRecommender(), Capone.getInstance().getParliament());
	
		}
		else if(pRecType.equals(recommenderType.SIMILARITY))
		{
			aCurrentRecommendedList = this.recommendSpeechesWithMP(new SimilarityBasedRecommender(), Capone.getInstance().getParliament());
		}
		else if(pRecType.equals(recommenderType.VOTE))
		{
			aCurrentRecommendedList = this.recommendSpeechesWithMP(new VoteBasedRecommender(), Capone.getInstance().getParliament());
		}
	}
	/** Getter for the current list of recommended speeches.
	 * @return list of currently recommended speeches.
	 */
	public ArrayList<Triple<Speech , MP>> getaCurrentRecommendedList()
	{
		return aCurrentRecommendedList;
	}
	/** Returns an ArrayList of speeches that have been read.
	 * @return ArrayList of read speeches.
	 */
	public ArrayList<Speech> getReadSpeeches()
	{
		return aReadSpeeches;
	}
	
	/** Adds a speech to the list of read speeches.
	 * @param pSpeech the speech that has been read.
	 */
	public void addReadSpeech(Speech pSpeech)
	{
		if(pSpeech != null)
		{
			aReadSpeeches.add(pSpeech);
			pSpeech.setIsRead(true);
		}
	}
	
	/** Resets all read speeches for the userProfile.
	 * 
	 */
	public void resetReadSpeeches()
	{
		for(Speech aSpeech : aReadSpeeches)
		{
			aSpeech.setIsRead(false);
		}
		this.aReadSpeeches.clear();
	}

	/** Enum class used in updateRecommender, contains CONTENT, SIMILARITY and VOTE, which are passed in
	 * updateRecommender.
	 * @author Grady
	 *
	 */
	public enum recommenderType implements Serializable
	{
		CONTENT,
		SIMILARITY,
		VOTE
	}
	
	/**
	 * Setter to define the data location in the filesystem.
	 * Data is loaded from this location.
	 * @param pRelativePath: Relative path to locate the data in the filesystem
	 * @author kdick
	 */
	void setDataLocation(String pRelativePath)
	{
		aDataLocation = pRelativePath;
	}	
	
	/**
	 * Getter for the relative path where data is stored.
	 * @return aDataLocation
	 * @author kdick
	 */
	String getDataLocation()
	{
		return aDataLocation;
	}
	
	/** Exports the user profile as JSON at the passes path.
	 * @param pPath the path of the location that the JSON is being saved at.
	 */
	public void exportAsJSON(String pPath)
	{
		Gson gson = new Gson();
		 
		// convert java object to JSON format,
		// and returned as JSON formatted string
		String json = gson.toJson(this);
	 
		try 
		{
			//write converted json data to a file named "out.json"
			FileWriter writer = new FileWriter(pPath + File.separator + "userProfile.json");
			writer.write(json);
			writer.close();
	 
		} 
		catch (IOException e) 
		{
			Capone.LOGGER.log(Level.SEVERE, "Error exporting User Profile as JSON");
		}
	}
}
