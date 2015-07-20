package ca.mcgill.cs.comp303.capone.model;

import java.io.Serializable;

/** Helper class used for storing information in the Recommender classes.
 * The project instructions have recommenders return a list of Speeches, but for
 * subclasses to add on to the rankings from superclasses they need the integer rank
 * and not just the total ordering. These triples hold that data and allow it to be 
 * given to subclasses.
 * @author Grady
 *
 * @param <X> An object to be contained in a triple, in recommenders this is Speech.
 * @param <Y> An object to be contained in a triple, in recommenders this is MP.
 */
public class Triple<X , Y> implements Comparable<Triple<X , Y>>, Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final X aX;
	private final Y aY;
	private Integer aInt;
	
	/** Basic constructor for triples.
	 * @param pX The first object being added to the triple.
	 * @param pY The second object being added to the triple.
	 * @param pInt The third object being added to the triple, this has to be an Integer.
	 */
	public Triple(X pX , Y pY , Integer pInt)
	{
		aX = pX;
		aY = pY;
		aInt = pInt;
	}
	
	/** Returns the first element of the triple.
	 * @return The first element of the triple.
	 */
	public X getFirst()
	{
		return aX;
	}
	
	/** Returns the second element of the triple.
	 * @return The second element of the triple.
	 */
	public Y getSecond()
	{
		return aY;
	}
	
	/** Returns the third (Integer) element of the triple.
	 * @return The thrid elecment of the triple.
	 */
	
	public Integer getInt()
	{
		return aInt;
	}
	/** Sets the Integer element of the triple.
	 * @param pX The value the Integer element of the triple is being set to.
	 */
	public void setInt(Integer pX)
	{
		aInt = pX;
	}
	
	@Override
	public int compareTo(Triple<X, Y> pOther)
	{
		return -1 * this.getInt().compareTo(pOther.getInt());
	}
}
