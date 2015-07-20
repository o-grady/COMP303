package ca.mcgill.cs.comp303.capone.model;

import java.util.Arrays;
import java.util.Collections;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class maps to a single speech (that is part of a house aDebate)
 * resource from the openparliament.ca API.
 * 
 * Here is an example of the data format: http://api.openparliament.ca/debates/2013/6/18/tom-lukiwski-1/
 * 
 */
public class Speech implements Serializable
{
	final String[] commonwords = "'tis 'twas a able about across after ain't all almost also am among an and any are aren't as at be because been but by can can't cannot could could've couldn't dear did didn't do does doesn't don't either else ever every for from get got had has hasn't have he he'd he'll he's her hers him his how how'd how'll how's however i i'd i'll i'm i've if in into is isn't it it's its just least let like likely may me might might've mightn't most must must've mustn't my neither no nor not of off often on only or other our own rather said say says shan't she she'd she'll she's should should've shouldn't since so some than that that'll that's the their them then there there's these they they'd they'll they're they've this tis to too twas us wants was wasn't we we'd we'll we're were weren't what what'd what's when when when'd when'll when's where where'd where'll where's which while who who'd who'll who's whom why why'd why'll why's will with won't would would've wouldn't yet you you'd you'll you're you've your".split(" ");

	private static final long serialVersionUID = 1L;
	private String aHeader1;
	private String aHeader2;
	private String aContent;
	private Date aTime;
	private boolean aIsRead;
	private int aThumbUpDown; //1 if up , -1 if down, 0 otherwise
	
	/**
	 * Constructor for the Speech Object.
	 * @param pHeader1 The first header of the speech.
	 * @param pHeader2 The second header of the speech.
	 * @param pContent The content of the speech.
	 * @param pTime The time the speech was spoken.
	 */
	public Speech(String pHeader1, String pHeader2, String pContent, Date pTime)
	{
		aHeader1 = pHeader1;
		aHeader2 = pHeader2;
		aContent = pContent;
		aTime = pTime;
		aIsRead = false;
		aThumbUpDown = 0;
	}
	/**
	 * @return The main label for this speech. e.g., "Routine Proceedings"
	 */
	public String getHeader1()
	{
		return aHeader1; // TODO
	}

	/**
	 * @return The secondary label for this speech. e.g., "Government Response to Petitions"
	 */
	public String getHeader2()
	{
		return aHeader2; // TODO
	}

	/**
	 * @return The content of the speech.
	 */
	public String getContent()
	{
		return aContent; 
	}

	/**
	 * @return The time at which the speech was given.
	 */
	public Date getTime()
	{
		return (Date)aTime.clone(); 
	}
	
	/** Returns weather or not the speech has been read.
	 * @return weather the speech has been read.
	 */
	public boolean getIsRead()
	{
		return aIsRead;
	}
	
	/** Changes the read flag on a speech.
	 * @param pIsRead the new flag for the read status of the speecch.
	 */
	public void setIsRead(boolean pIsRead)
	{
		aIsRead = pIsRead;
	}
	
	/** Returns the thumb up / down on a speech.
	 * @return the thumb up / down on a speech.
	 */
	public int getThumbs()
	{
		return aThumbUpDown;
	}
	/** Sets the thumbs up / down on a speech.
	 * @param pVote int representing vote; Positive if up, negative if down, 0 if resetting.
	 */
	public void setThumbs(int pVote)
	{
		aThumbUpDown = pVote;
	}
	
	/** Returns a map of the keywords in a speech.
	 * @return map of keywords.
	 */
	public NavigableMap<Integer, String> getKeywords()
	{	 
		List<String> list = Arrays.asList(this.getContentWithoutHTML().replaceAll("[^a-zA-Z ]", "").split(" "));
		for(String s : list)
		{
			s.toLowerCase();
		}
		Set<String> uniqueWords = new HashSet<String>(list);
		TreeMap<Integer, String> wordsWithFreq = new TreeMap<Integer, String>();
		for(String s : uniqueWords)
		{
			if(!Arrays.asList(commonwords).contains(s))
			{
				wordsWithFreq.put(Collections.frequency(list, s), s);
			}

		}
		return wordsWithFreq;
		
	}

	/** Removes HTML information from a speech and returns the content.
	 * @return The content of the speech, wichout HTML tags.
	 */
	public String getContentWithoutHTML() 
	{
		final Pattern REMOVE_TAGS = Pattern.compile("<.+?>");
	    if (aContent == null || aContent.length() == 0) 
	    {
	        return aContent;
	    }
	    String noHtml = "";
	    noHtml += aContent;
	    Matcher m = REMOVE_TAGS.matcher(noHtml);
	    return m.replaceAll("");
	}

}
