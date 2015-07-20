package ca.mcgill.cs.comp303.capone.loaders;

import ca.mcgill.cs.comp303.capone.model.Parliament;

/**
 * Services for creating model elements.
 */
public interface ParliamentLoader
{
	/**
	 * Loads an MP record into the Parliament object.
	 * 
	 * @param pRelativeLocation An indicator of where to find the MP's record 
	 * 							relative to the context of the concrete loader.
	 * @param pParliament The object to load the data into.
	 * @return The primary key of the MP just loaded.
	 */
	String loadMP(String pRelativeLocation, Parliament pParliament);

	/**
	 * Loads recent events related to this MP into the Parliament object. 
	 * For M1 these events are all the speeches.
	 * 
	 * @param pMPKey The primary key identifier of the MP
	 * @param pParliament The object to load the data into.
	 */
	void loadRecentEvents(String pMPKey, Parliament pParliament);

}
