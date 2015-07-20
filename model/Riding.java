package ca.mcgill.cs.comp303.capone.model;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * This class represents an electoral district in Canada, a geographical constituency 
 * upon which a Member of Parliament to the Canadian House of Commons represents. 
 * [Reference: Wikipedia (http://en.wikipedia.org/wiki/Electoral_district_%28Canada%29)]
 * 
 * Immutable.
 * There should only even one instance of each unique Riding object
 * within a given JVM.
 */
public final class Riding implements Serializable
{

	private static final long serialVersionUID = 1L;
	/**
	 * Used to store existing ridings to check before creating duplicates.
	 */
	private static ArrayList<Riding> existingRidings = new ArrayList<Riding>();
	private int aID;
	private String aName;
	private String aProvince;
	
	private Riding(int pID, String pName, String pProvince)
	{
		aID = pID;
		aName = pName;
		aProvince = pProvince;
	}
	/**
	 * A psudoconstructor for riding. Checks if the jRiding being passes has been
	 * matches an existing riding and returns that if it does. If there is no match,
	 * a new riding is created and returned.
	 * @param pID the unique ID for this riding
	 * @param pName the name of this riding
	 * @param pProvince the province that the riding is in
	 * @return either a new riding or an existing riding that matches the passes jRiding.
	 */
	public static Riding newRiding(int pID, String pName, String pProvince)
	{
		for(Riding r : Riding.getExistingRidings())
		{
			if(pID == r.getId() && pName.equals(r.getName()) && pProvince.equals(r.getProvince()))
			{
				return r;
			}	
		}
		Riding retRiding = new Riding(pID, pName, pProvince);
		Riding.getExistingRidings().add(retRiding);
		return retRiding;
	}

	/**
	 * The unique ID for this riding, as obtained from OpenParliament. E.g., 4700.
	 * @return The unique ID for this riding, as obtained from OpenParliament. E.g., 4700.
	 */
	public int getId()
	{
		return aID;
	}

	/**
	 * The official name of the riding.
	 * @return the official name of the riding.
	 */
	public String getName()
	{
		return aName;
	}

	/**
	 * The province code, e.g, SK.
	 * @return the province code.
	 */
	public String getProvince()
	{
		return aProvince;
	}
	/**
	 * The ArrayList containing all existing ridings.
	 * @return the ArrayList containing all existing ridings.
	 */
	public static ArrayList<Riding> getExistingRidings()
	{
		return existingRidings;
	}
}
