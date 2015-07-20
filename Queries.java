package ca.mcgill.cs.comp303.capone;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ca.mcgill.cs.comp303.capone.model.Capone;
import ca.mcgill.cs.comp303.capone.model.MP;
import ca.mcgill.cs.comp303.capone.model.Membership;
import ca.mcgill.cs.comp303.capone.model.Speech;

/**
 * Compute the results of two queries on the data.
 */
public final class Queries
{
	private Queries(){}
	
	/**
	 * @return The email address of the MP who has served the longest 
	 * in parliament, in terms of total number of days in any membership.
	 * If there are ties, return all ties in any order.
	 */
	public static Set<String> q1LongestServingMPs()
	{
		Set<String> aLongest = new HashSet<String>();
		Map<String, MP> aMPs = Capone.getInstance().getParliament().getAllMPs();
		Date startDate = null;
		for (String aMP : aMPs.keySet())
		{
			if(aLongest.isEmpty())
			{
				startDate = aMPs.get(aMP).startDate();
				aLongest.add(aMP);
			}
			else if(aMPs.get(aMP).startDate().before(startDate))
			{
				startDate = aMPs.get(aMP).startDate();
				aLongest.clear();
				aLongest.add(aMP);
			}
			else if(aMPs.get(aMP).startDate().equals(startDate))
			{
				aLongest.add(aMP);
			}
		}
		return aLongest;
	}
	
	/**
	 * @return The email address of the MP who has served in the largest
	 * number of different political parties. If there are ties, return 
	 * all ties in any order.
	 * @author Kevin Dick
	 */
	public static Set<String> q2LargestNumberOfParties()
	{
		Set<String> aLargest = new HashSet<String>();
		Map<String, MP> aMPs = Capone.getInstance().getParliament().getAllMPs();
		
		// Store value for largest party number; current MP number of parties
		int largestPartyNumber = 0;
		int currentMPPartyNumber = 0;
		
		// Iterate through all MPs and identify those serving longest
		for (String aMP : aMPs.keySet())
		{
			currentMPPartyNumber = getNumberOfParties(aMP, aMPs);
			
			if (aLargest.isEmpty())
			{
				largestPartyNumber = currentMPPartyNumber;
				aLargest.add(aMP);
			} 
			else if (currentMPPartyNumber > largestPartyNumber)
			{
				largestPartyNumber = currentMPPartyNumber;
				aLargest.clear();
				aLargest.add(aMP);
			}
			else if (currentMPPartyNumber == largestPartyNumber)
			{
				aLargest.add(aMP);
			}
		}
		return aLargest;
	}
	
	/**
	 * Will iterate through pMPs memberships and return the number of unique
	 * parties that the MP has served in. This is to avoid MPs who jump
	 * back and forth between given parties.
	 * @param pMP, the MP to query for number of unique parties.
	 * @param pMPs, the Map to pull specific MPs from.
	 * @return An integer value of the number of unique parties an MP has served in.
	 * @author Kevin Dick
	 */
	private static int getNumberOfParties(String pMP, Map<String, MP> pMPs)
	{
		// First iterate through aMPs and get their list of memberships.
		// Create a new arraylist for each ensuring there is no copy of
		// parties, and then the size of the arraylist is number of unique parties.
		ArrayList<Membership> uniqueParties = new ArrayList<Membership>();
		ArrayList<Membership> thisMPMemberships = (ArrayList<Membership>) pMPs.get(pMP).getMemberships();
		
		// Now iterate through memberships and take out repeated parties.
		for (Membership membership : thisMPMemberships)
		{
			if (uniqueParties.isEmpty())
			{
				uniqueParties.add(membership);
			}
			else if (!uniqueParties.contains(membership.getParty()))
			{
				uniqueParties.add(membership);				
				}
		}
		// Each party in uniqueParties is unique, thus represents number of parties
		// this MP has served in.
		return uniqueParties.size();
	}
	
	/**
	 * @return The number of speeches in which Thomas Mulcair
	 * uses the word "Conservative" (not the plural variant, 
	 * but case-insensitive).
	 */
	public static int q3NumberOfConservativeWordUsage()
	{
		MP aMP = Capone.getInstance().getParliament().getMP("thomas.mulcair@parl.gc.ca");
		int wordCount = 0;
		for(Speech e : aMP.getEvents())
		{
			if(e.getContent().toLowerCase().contains("conservative"))
			{
				wordCount++;
			}
		}
		return wordCount;
	}
}
