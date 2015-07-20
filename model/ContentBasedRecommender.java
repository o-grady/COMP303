package ca.mcgill.cs.comp303.capone.model;

import java.util.ArrayList;
import java.util.Collections;

/** 
 * Ranks speeches based off a UserProfile's keywords and MP's.
 * @author Grady
 *
 */
public class ContentBasedRecommender implements ISpeechRecommender
{

	private static final int HEADER_MATCH_RANK_INCREASE = 2;
	private static final int CONTENT_MATCH_RANK_INCREASE = 1;
	private static final int MP_MATCH_RANK_INCREASE = 5;

	@Override
	public ArrayList<Speech> rank(UserProfile pUser, Parliament pParliament)
	{
		ArrayList<Triple<Speech , MP>> allSpeeches = rankedSpeechesWithMP(pUser , pParliament);
		ArrayList<Speech> ret = new ArrayList<Speech>();
		for(Triple<Speech , MP> x: allSpeeches)
		{
			ret.add(x.getFirst());
		}
		return ret;
	}
	
	/** Returns a ranked list of Triples of Speech MP and rank. 
	 * This is used for subclasses of this ranking system because only returning a list of speeches loses 
	 * important information in the rank and MP.
	 * @param pUser The UserProfile that the ranking is made for.
	 * @param pParliament the parliament where all MPs and speeches come from.
	 * @return An arraylist of triples sorted by rank.
	 */
	public ArrayList<Triple<Speech , MP>> rankedSpeechesWithMP(UserProfile pUser, Parliament pParliament)
	{
		ArrayList<Triple<Speech , MP>> allSpeeches = new ArrayList<Triple<Speech , MP>>();
		//Create and populate and arraylist of the subclass.
		for(MP aMP : pParliament.getAllMPs().values())
		{
			for(Speech aSpeech : aMP.getSpeeches())
			{
				if(!pUser.getReadSpeeches().contains(aSpeech))
				{
					allSpeeches.add(new Triple<Speech , MP>(aSpeech , aMP , 0));
				}
				else
				{
					pUser.getReadSpeeches().add(aSpeech);
				}
			}
		}
		for(Triple<Speech , MP> aTriple : allSpeeches)
		{
			//Increases rank if the MP giving the speech
			// is interesting to the user.
			for(String x : pUser.getMPs())
			{
				if(pParliament.getMP(x).equals(aTriple.getSecond()))
				{
					aTriple.setInt(aTriple.getInt() + MP_MATCH_RANK_INCREASE);
				}
			}
			//Increases rank if the users keywords show up in the speech
			for(String s : pUser.getKeywords())
			{
				if(aTriple.getFirst().getContent().contains(s))
				{
					aTriple.setInt(aTriple.getInt() + CONTENT_MATCH_RANK_INCREASE);
				}
				if(aTriple.getFirst().getHeader1().contains(s) || aTriple.getFirst().getHeader2().contains(s))
				{
					aTriple.setInt(aTriple.getInt() + HEADER_MATCH_RANK_INCREASE);
				}
			}
		}
		Collections.sort(allSpeeches);
		return allSpeeches;
	}
}
