package ca.mcgill.cs.comp303.capone.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;


/**Ranks speeches based off a UserProfile's keywords and MP's as well as offering speeches from MP's similar to those in the UserProfile.
 * The similarity of the MP's is determined by their party affiliation as well as common words within their speeches (ignoring common words).
 * @author Grady
 *
 */
public class SimilarityBasedRecommender  extends ContentBasedRecommender implements ISpeechRecommender
{
	private static final int SPEECHES_COMPARED_PER_MP = 5;
	private static final double MATCHED_WORDS_MULTIPLIER = 0.01;
	private static final int SAME_PARTY_RANK_CHANGE = 2;
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
		for(Triple<Speech , MP> aTriple : contentRecommended)
		{
			for(MP aMP : pParliament.getAllMPs().values())
			{
				aTriple.setInt(aTriple.getInt() + compareMPs(aTriple.getSecond() , aMP));
			}
		}
		Collections.sort(contentRecommended);
		return contentRecommended;
	}
	
	private int compareMPs(MP pMP1 , MP pMP2)
	{
		int ret = 0;
		try
		{
			if(pMP1.getCurrentMembership().getParty().equals(pMP2.getCurrentMembership().getParty()))
			{
				ret += SAME_PARTY_RANK_CHANGE;
			}
		}
		catch(NullPointerException e)
		{
			Capone.LOGGER.log(Level.WARNING, "Null Pointer Exception when comparing memberships");
		}
		//compare all speeches
		if(pMP1.getSpeeches().size() > SPEECHES_COMPARED_PER_MP)
		{
			//Choose 5 random speeches.
			Speech[] randomSpeechesMP1 = new Speech[SPEECHES_COMPARED_PER_MP];
			for(int i = 0 ; i < SPEECHES_COMPARED_PER_MP ; i++)
			{
				randomSpeechesMP1[i] = pMP1.getSpeeches().get((int) (Math.random() * pMP1.getSpeeches().size()));
			}
			if(pMP2.getSpeeches().size() > SPEECHES_COMPARED_PER_MP)
			{
				//Choose 5 random speeches.
				Speech[] randomSpeechesMP2 = new Speech[SPEECHES_COMPARED_PER_MP];
				for(int i = 0 ; i < SPEECHES_COMPARED_PER_MP ; i++)
				{
					randomSpeechesMP2[i] = pMP2.getSpeeches().get((int) (Math.random() * pMP2.getSpeeches().size()));
				}
				for(Speech speech1 : randomSpeechesMP1)
				{
					for(Speech speech2 : randomSpeechesMP2)
					{
						ret += compareSpeeches(speech1 , speech2);
					}
				}
			}
			else
			{
				for(Speech speech1 : randomSpeechesMP1)
				{
					for(Speech speech2 : pMP2.getSpeeches())
					{
						ret+= compareSpeeches(speech1 , speech2);
					}
				}
			}
		}
		else
		{
			if(pMP2.getSpeeches().size() > SPEECHES_COMPARED_PER_MP)
			{
				//Choose 5 random speeches.
				Speech[] randomSpeechesMP2 = new Speech[SPEECHES_COMPARED_PER_MP];
				for(int i = 0 ; i < SPEECHES_COMPARED_PER_MP ; i++)
				{
					randomSpeechesMP2[i] = pMP2.getSpeeches().get((int) (Math.random() * pMP2.getSpeeches().size()));
				}
				for(Speech speech1 : pMP1.getSpeeches())
				{
					for(Speech speech2 : randomSpeechesMP2)
					{
						ret += compareSpeeches(speech1 , speech2);
					}
				}
			}
			else
			{
				for(Speech speech1 : pMP1.getSpeeches())
				{
					for(Speech speech2 : pMP2.getSpeeches())
					{
						ret += compareSpeeches(speech1 , speech2);
					}
				}
			}
		}
		return ret;
	}
	private int compareSpeeches(Speech pSpeech1 , Speech pSpeech2)
	{
		//Split the content of the speeches by spaces
		String[] speech1split = pSpeech1.getHeader2().split(" ");
		String[] speech2split = pSpeech1.getHeader2().split(" ");
		String[] commonwords = "'tis 'twas a able about across after ain't all almost also am among an and any are aren't as at be because been but by can can't cannot could could've couldn't dear did didn't do does doesn't don't either else ever every for from get got had has hasn't have he he'd he'll he's her hers him his how how'd how'll how's however i i'd i'll i'm i've if in into is isn't it it's its just least let like likely may me might might've mightn't most must must've mustn't my neither no nor not of off often on only or other our own rather said say says shan't she she'd she'll she's should should've shouldn't since so some than that that'll that's the their them then there there's these they they'd they'll they're they've this tis to too twas us wants was wasn't we we'd we'll we're were weren't what what'd what's when when when'd when'll when's where where'd where'll where's which while who who'd who'll who's whom why why'd why'll why's will with won't would would've wouldn't yet you you'd you'll you're you've your".split(" ");
		Set<String> compare = new HashSet<String>();
		Set<String> matches = new HashSet<String>();
		for(String aString : speech1split)
		{
			//remove punctuation from the words and set them to uppercase
			aString = aString.replaceAll("," , "");
			aString = aString.replaceAll("." , "");
			aString = aString.replaceAll("!" , "");
			aString = aString.replaceAll("\\?" , "");
			aString = aString.replaceAll(";" , "");
			aString = aString.replaceAll(":" , "");
			aString = aString.replaceAll("\"" , "");
			aString = aString.replaceAll("\\(" , "");
			aString = aString.replaceAll("\\)" , "");
			aString = aString.replaceAll("\\[" , "");
			aString = aString.replaceAll("\\]" , "");
			aString = aString.replaceAll("\\{" , "");
			aString = aString.replaceAll("\\}" , "");
			aString.toUpperCase();
			compare.add(aString);
			for (String common : commonwords)
			{
				if(aString.equalsIgnoreCase(common))
				{
					compare.remove(aString);
				}
			}
		}
		for(String aString : speech2split)
		{
			//format the second speech the same way.
			aString = aString.replaceAll("," , "");
			aString = aString.replaceAll("." , "");
			aString = aString.replaceAll("!" , "");
			aString = aString.replaceAll("\\?" , "");
			aString = aString.replaceAll(";" , "");
			aString = aString.replaceAll(":" , "");
			aString = aString.replaceAll("\"" , "");
			aString = aString.replaceAll("\\(" , "");
			aString = aString.replaceAll("\\)" , "");
			aString = aString.replaceAll("\\[" , "");
			aString = aString.replaceAll("\\]" , "");
			aString = aString.replaceAll("\\{" , "");
			aString = aString.replaceAll("\\}" , "");
			aString.toUpperCase();
			for(String otherString : compare)
			{
				//remove any word that does not appear in both speeches.
				if(aString.equals(otherString))
				{
					matches.add(aString);
				}
			}
		}
		return (int)(matches.size() * MATCHED_WORDS_MULTIPLIER);
	}
}
