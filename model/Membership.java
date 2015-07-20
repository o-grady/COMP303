package ca.mcgill.cs.comp303.capone.model;

import java.io.Serializable;
import java.util.Date;

/**
 * This class maps to a single politician membership resource from the openparliament.ca API. 
 * See an example of the data format from this example: api.openparliament.ca/politicians/memberships/1534/
 * 
 * Memberships should be naturally sortable in decreasing chronological order.
 * 
 * Not all data must be captured by this class. The minimum set is represented by the methods.
 * 
 * The class should be immutable.
 * 
 * @see http://api.openparliament.ca/politicians/memberships/
 */
public class Membership implements Comparable<Membership>, Serializable
{
	
	private static final long serialVersionUID = 1L;
	private Party aParty;
	private Riding aRiding;
	private Date aStartDate;
	private Date aEndDate;
	/**
	 * Constructor for Membership Class.
	 * @param pParty Party of the membership.
	 * @param pRiding Riding of the membership.
	 * @param pStartDate Start date of the membership.
	 * @param pEndDate End date of the membership.
	 */
	public Membership(Party pParty, Riding pRiding, Date pStartDate, Date pEndDate)
	{
		this.aParty = pParty;
		this.aRiding = pRiding;
		this.aStartDate = pStartDate;
		this.aEndDate = pEndDate;
	}
	/**
	 * Getter for the party field of membership.
	 * @return the party of the membership.
	 */
	public Party getParty()
	{
		return this.aParty;
	}

	/**
	 * Getter for the riding field of the membership.
	 * @return the riding of the membership.
	 */
	public Riding getRiding()
	{
		return this.aRiding;
	}

	/**
	 * Getter for the start date of the membership.
	 * @return the start date of the membership.
	 */
	public Date getStartDate()
	{
		return (Date)this.aStartDate.clone();
	}

	/**
	 * Getter for the end date of the membership (if any).
	 * @return If this is a past membership, it returns the end date of that membership.
	 * If this is a current membership, return null;
	 */
	public Date getEndDate()
	{
		if(this.aEndDate == null)
		{
			return null;
		}
		return (Date)this.aEndDate.clone();
	}
	
	/**
	 * Conpares 2 memberships.
	 * @param pMembership the membership to be compared to
	 * @return 1 if they are eqivilent, 0 if they are not
	 */
	@Override
	public int compareTo(Membership pMembership)
	{
		if(pMembership.getParty() == this.aParty && pMembership.getRiding() == this.aRiding)
		{
			return 1;
		}
		return 0;
	}
}
