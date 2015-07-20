package ca.mcgill.cs.comp303.capone.model;

import java.util.ArrayList;
import java.util.Collections;

/** Recommender that extended ContentBasedRecommender and adds thumbs up / down into the recommendation. Rank is
 * increased if there is a thumbs up or removed from recommendations if it has a thumbs down.
 * @author Grady
 *
 */
public class VoteBasedRecommender extends ContentBasedRecommender implements ISpeechRecommender
{
	private static final int THUMBVALUE = 5; //Rank value for a vote
	@Override
	public ArrayList<Speech> rank(UserProfile pUser , Parliament pParliament)
	{
		ArrayList<Triple<Speech , MP>> allSpeeches = this.rankedSpeechesWithMP(pUser , pParliament);
		ArrayList<Speech> ret = new ArrayList<Speech>();
		for(Triple<Speech , MP> x : allSpeeches)
		{
			ret.add(x.getFirst());
		}
		return ret;
	}
	@Override
	public ArrayList<Triple<Speech, MP>> rankedSpeechesWithMP(UserProfile pUser, Parliament pParliament)
	{
		ArrayList<Triple<Speech, MP>> contentRecommended = super.rankedSpeechesWithMP(pUser, pParliament);
		for(int i = 0; i < contentRecommended.size() ; i++)
		{
			Triple<Speech, MP> aTriple = contentRecommended.get(i);
			if(aTriple.getFirst().getThumbs() > 0)
			{
				aTriple.setInt(aTriple.getInt() + THUMBVALUE);
			}
			else if(aTriple.getFirst().getThumbs() < 0)
			{
				contentRecommended.remove(aTriple); //Do not recommend any speeches that are thumbs down.
			}
		}
		Collections.sort(contentRecommended);
		return contentRecommended;
	}
	
}
