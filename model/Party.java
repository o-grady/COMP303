
package ca.mcgill.cs.comp303.capone.model;

import java.io.Serializable;
import java.util.ArrayList;



/**
 * This represents a political party in Canada. Immutable.
 * There should only even one instance of each unique Party object
 * within a given JVM.
 */
public class Party implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Used to store existing ridings to check before creating duplicates.
	 */
	private static ArrayList<Party> existingParties = new ArrayList<Party>();
	private String aName;
	private String aShortName;
	/**
	 * For example, "Conservative Party of Canada".
	 */
	private Party(String pName, String pShortName)
	{
		aName = pName;
		aShortName = pShortName;
	}
	/**
	 * A psudoconstructor for party. Checks if there is a party with a matching name
	 * and returns that if there is a match, and otherwise creates a new 
	 * party and returns that.
	 * @param pName the name of the party
	 * @param pShortName the short name for the party
	 * @return either a new party or an existing party that matches the passes jParty.
	 */
	public static Party newParty(String pName, String pShortName)
	{
		for(Party p : Party.getExistingParties())
		{
			if(pName.equals(p.getName()) && pShortName.equals(p.getShortName()))
			{
				return p;
			}	
		}
		Party retParty = new Party(pName, pShortName);
		Party.getExistingParties().add(retParty);
		return retParty;
	}
	/**
	 * Getter for the name of the party.
	 * @return the name of the party.
	 */
	public String getName()
	{
		return aName; 
	}

	/**
	 * Gets the short name of the party.
	 * @return the short name of the party.
	 */
	public String getShortName()
	{
		return aShortName; 
	}
	
	@Override
	public String toString() 
	{
		return aName; 
	}
	/**
	 * Returns the static arraylist containing all the party objects that already exist.
	 * @return the static arraylist containing all the party objects that already exist.
	 */
	public static ArrayList<Party> getExistingParties()
	{
		return existingParties;
	}
}
