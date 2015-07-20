package ca.mcgill.cs.comp303.capone.model;

import java.io.Serializable;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;




/**
 * This class represents a Member of Parliament, the representative of the 
 * voters to the Parliament of Canada, the federal legislative branch of Canada.  
 * [Reference: Wikipedia (http://en.wikipedia.org/wiki/Member_of_Parliament)]
 * 
 * This class directly maps to a single politician resource from the openparliament.ca API.
 * For an example, check out the data format of an MP: api.openparliament.ca/politicians/tom-lukiwski/
 * 
 * The minimum set of data to capture in this class is indicated through the getter methods.
 * 
 * This class should not be immutable. It will be a dynamic entity in the final application.
 */
public class MP implements Serializable
{

	private static final long serialVersionUID = 1L;
	/**
	 * @return A primary key (unique identifier) for this object. We will use an MP's email
	 * as primary key.
	 */
	private String aPrimaryKey;
	private String aFamilyName;
	private String aGivenName;
	private String aName;
	private String aEmail;
	private String aPhoneNumber;
	private String aRSSFeedURL;
	private List<Membership> aMemberships;
	private int aHashCode;
	private List<Speech> aSpeeches = new ArrayList<Speech>();

	/** Sets the primary key to the passed string.
	 * Note: this also changes the hashCode because it is tied to the primary key.
	 * @param pPrimaryKey New value for the primary key.
	 */
	public void setPrimaryKey(String pPrimaryKey)
	{
		aPrimaryKey = pPrimaryKey;
		aHashCode = pPrimaryKey.hashCode();    //Hash code is tied to primary key
	}

	/** Sets the family name of the MP.
	 * @param pFamilyName the new family name.
	 */
	public void setFamilyName(String pFamilyName)
	{
		aFamilyName = pFamilyName;
	}

	/** Sets the given name of the MP.
	 * @param pGivenName the new given name.
	 */
	public void setGivenName(String pGivenName)
	{
		aGivenName = pGivenName;
	}

	/** Sets the name of the MP.
	 * @param pName The new name.
	 */
	public void setName(String pName)
	{
		aName = pName;
	}

	/** Sets the email address of the MP.
	 * @param pEmail the new Email address.
	 */
	public void setEmail(String pEmail)
	{
		aEmail = pEmail;
	}

	/** Sets the phone number of the MP.
	 * @param pPhoneNumber The new phone number.
	 */
	public void setPhoneNumber(String pPhoneNumber)
	{
		aPhoneNumber = pPhoneNumber;
	}

	/** Sets the RSSFeelURL of the MP.
	 * @param pRSSFeedURL the new RSSFeedURL.
	 */
	public void setRSSFeedURL(String pRSSFeedURL)
	{
		aRSSFeedURL = pRSSFeedURL;
	}

	/** Sets the list of memberships for the MP.
	 * @param pList the new list of memberships.
	 */
	public void setMemberships(List<Membership> pList)
	{
		aMemberships = pList;
	}
	
	/** Getter for the primary key of the MP.
	 * @return the primary key.
	 */
	public String getPrimaryKey()
	{
		return aPrimaryKey; 
	}
	
	/** Adds a speech to the MP object.
	 * @param pSpeech the Speech to be added.
	 */
	public void addSpeech(Speech pSpeech)
	{
		aSpeeches.add(pSpeech);
	}
	
	/** Returns the list of speeches that the MP has given.
	 * @return list of given speeches.
	 */
	public List<Speech> getEvents()
	{
		return aSpeeches;
	}
	/**
	 * @return The family name(s) of the MP
	 */
	public String getFamilyName()
	{
		return aFamilyName; // TODO
	}
	
	/**
	 * @return The given name(s) of the MP
	 */
	public String getGivenName()
	{
		return aGivenName; // TODO
	}
	
	/**
	 * @return The given and family name(s), separated by a white space.
	 */
	public String getName()
	{
		return aName; // TODO
	}
	
	/**
	 * @return The email address of the MP. This is used as the primary key.
	 */
	public String getEmail()
	{
		return aEmail; // TODO
	}
	
	/**
	 * @return The phone number of the MP
	 */
	public String getPhoneNumber()
	{
		return aPhoneNumber; // TODO
	}
	
	/**
	 * @return This MP's official RSS feed URL.
	 * Note that this is not found directly in the MP's JSON data. You
	 * have to find it somewhere else. Hint: look at the image field 
	 * in the JSON data, and the corresponding RSS URL from the website. 
	 */
	public String getRSSFeedURL()
	{
		return aRSSFeedURL; // TODO
	}
	
	// PROVIDE: Functionality to add memberships AND make membership information 
	// available to client objects.
	
	/**
	 * @return The total number of memberships for this MP, including the current one.
	 */
	public int getNumberOfMemberships()
	{
		return aMemberships.size(); // TODO Probably not the correct value.
	}
	
	/**
	 * @return The current MP membership  
	 */
	public Membership getCurrentMembership() 
	{
		for (Membership aMem : aMemberships)
		{
			if (aMem.getEndDate() == null)
			{
				return aMem;
			}
		}
		return null;
	}
	

	/**
	 * @return The list of memberships of this MP
	 * @author Kevin Dick
	 */
	public List<Membership> getMemberships()
	{
		 return aMemberships;
	}
	
	/**
	 * 
	 * @return The date the MP joined parliament
	 */
	public Date startDate()
	{
		Date startDate = null;
		for (Membership aMem : aMemberships)
		{
			if(startDate==null)
			{
				startDate = aMem.getStartDate();
			}
			else if (aMem.getStartDate().before(startDate))
			{
				startDate = aMem.getStartDate();
			}
		}
		return startDate;
	}
	
	/**
	 * Getter for the Speeches.
	 * @return aSpeeches
	 * @author Kevin Dick
	 */
	public List<Speech> getSpeeches()
	{
		return aSpeeches;
	}
	
	// PROVIDE: Functionality to add speeches AND make speeches information 
	// available to client objects.
	
	

	/* 
	 * Two MPs objects are equals if they represent the same
	 * physical MP.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object pMP)
	{
		if (aHashCode == pMP.hashCode())
		{ //Using hashcode to check eqivilence
			return true;
		}
		return false; // TODO
	}

	@Override
	public int hashCode()
	{
		return aHashCode; // TODO 
	}

}
