package ca.mcgill.cs.comp303.capone.model;

import java.util.ArrayList;
 /**
  * Interface for the recommenders.
  * 
  * @author MIKE
  *
  */
public interface ISpeechRecommender
{
	/**
	 * Returns an ordered list of speeches by rank.
	 * @param pUser The user who the speeches are being recommended to.
	 * @param pParliament The parliament to pull speeches from
	 * @return an ordered list of speeches by rank.
	 */
	ArrayList<Speech> rank(UserProfile pUser, Parliament pParliament);

	/**
	 * Returns an ordered triple of Speech and MP by rank.
	 * @param pUserProfile The user profile to recommend speeches for
	 * @param pParliament The parliament to pull speeches from
	 * @return an ordered triple of Speech and MP by rank
	 */
	ArrayList<Triple<Speech, MP>> rankedSpeechesWithMP(UserProfile pUserProfile, Parliament pParliament);

}
