package ca.mcgill.cs.comp303.capone.loaders.op;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import ca.mcgill.cs.comp303.capone.loaders.MPEvent;
import ca.mcgill.cs.comp303.capone.loaders.ParliamentLoader;
import ca.mcgill.cs.comp303.capone.loaders.op.stubs.JMP;
import ca.mcgill.cs.comp303.capone.loaders.op.stubs.JMembership;
import ca.mcgill.cs.comp303.capone.loaders.op.stubs.JSpeech;
import ca.mcgill.cs.comp303.capone.model.Capone;
import ca.mcgill.cs.comp303.capone.model.MP;
import ca.mcgill.cs.comp303.capone.model.Membership;
import ca.mcgill.cs.comp303.capone.model.Parliament;
import ca.mcgill.cs.comp303.capone.model.Party;
import ca.mcgill.cs.comp303.capone.model.Riding;
import ca.mcgill.cs.comp303.capone.model.Speech;

import com.google.gson.Gson;

/**
 * A builder that can build the model from serialized 
 * JSON objects stored at specific locations on disk. Objects of this class should 
 * store the root of the data tree internally (e.g., C:\workspace...\data). The 
 * last branches of the path map directly to the OpenParliament API.
 * 
 * We packaged the data under Capone-M1/data. You can (should) use this as the root path.
 */

public class OpenParliamentFileLoader implements ParliamentLoader
{
	// Some formatters you might find useful.
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	// You should keep this root to make sure we can run your project from our environment.
	private static final String DATA_FILE_SUBDIR = "." + File.separator + "data" + File.separator;
	private static final String QUERY_CONTEXT = new File(DATA_FILE_SUBDIR).getAbsolutePath() + File.separator;

	// Relative paths to JSON files	
	private static final String CONTEXT_POLITICIANS = "politicians" + File.separator;
	private static final String CONTEXT_SPEECHES = "debates" + File.separator;
	private static final String CONTEXT_RSS = "rss" + File.separator;

	private static final String JSON_SUFFIX = ".json";
	
	/* 
	 * The relative location indicates the subpath leading to a
	 * specific politician. For the complete list, see:
	 * http://api.openparliament.ca/politicians/
	 * an example of input for pRelativeLocation is: gord-brown
	 * @see ca.mcgill.cs.comp303.capone.loaders.ParliamentLoader#loadMP(java.lang.String, ca.mcgill.cs.comp303.capone.model.Parliament)
	 */
	@Override
	public String loadMP(String pRelativeLocation, Parliament pParliament)
	{
		// This method produces a complete path pointing to the
		// JSON file for this MP.
		String jsonUri = getMPJsonUri(pRelativeLocation);
		
		if(pRelativeLocation.endsWith(JSON_SUFFIX))
		{
			jsonUri = pRelativeLocation;
		}

		// JSON file can get loaded easily into stub objects
		// using the Google GSON library. See the ...op.stubs 
		// package. Do not change any stub.
		JMP jmp = getGson(jsonUri, JMP.class);
		
		// Load all the information available about the MP from the MP's
		// page on OpenParliament.ca.
		MP aMP = new MP();
		List<Membership> memberships = new ArrayList<Membership>();
		List<JMembership> jMemberships = jmp.getMemberships();
		for(JMembership jMem : jMemberships)
		{
			Membership tempMembership = loadMembership(jMem);
			memberships.add(tempMembership);
		}
		aMP.setEmail(jmp.getEmail());
		aMP.setFamilyName(jmp.getFamily_name());
		aMP.setGivenName(jmp.getGiven_name());          //setting all values for MP
		aMP.setMemberships(memberships); 
		aMP.setName(jmp.getName());
		aMP.setPhoneNumber(jmp.getVoice());
		aMP.setPrimaryKey(jmp.getEmail());
		String image = jmp.getImage();
		Pattern rssPattern = Pattern.compile("[0-9]{2}[0-9]?[0-9]?"); //Creating parser for img url
		Matcher m = rssPattern.matcher(image);
		String rss = "";
		while (m.find())
		{
			rss = m.group();
		}
		rss = rss + ".xml";   //img url converted to RSS URL
		aMP.setRSSFeedURL(rss);
		
		pParliament.addMP(aMP, aMP.getPrimaryKey()); //Loading our created MP into pParliament

		return aMP.getPrimaryKey(); // The primary key of the MP just loaded.
	}
	
	/**
	 * Returns a Membership with data from pjMem added to it.
	 * @param pJMem The JMembership that data is being loaded from.
	 * @return The Membership with data from the pJMem param.
	 */
	public Membership loadMembership(JMembership pJMem)
	{
		Date startdate = null;
		Date enddate = null;
		try
		{
			startdate = DATE_FORMAT.parse(pJMem.getStart_date());
		}
		catch(ParseException E)
		{}
		if (!(pJMem.getEnd_date()==null))
		{	
			try
			{
				enddate = DATE_FORMAT.parse(pJMem.getEnd_date());
			}
			catch(ParseException E)
			{
				
			}
		}
		Membership rMem = new Membership(Party.newParty(pJMem.getParty().getName(), pJMem.getParty().getShort_name()),  
				Riding.newRiding(pJMem.getRiding().getId(), pJMem.getRiding().getName(), pJMem.getRiding().getProvince()),  
				startdate, enddate);
		return rMem;
	}


	/* 
     * For milestone 1, only load the speeches into the MP object.
     * However, load the entire speech data for each speech entry 
     * in the RSS feed. This will require you to fetch complementary
     * data from the JSON files under /debates.
     * Only a subset of the speeches are available in the local folder
     * (we did not include speeches in Committees, from example). 
     * If you don't find a certain speech mentioned in the RSS feed,
     * just ignore it.
	 * @see ca.mcgill.cs.comp303.capone.loaders.ParliamentLoader#loadRecentEvents(java.lang.String, ca.mcgill.cs.comp303.capone.model.Parliament)
	 */
	@Override
	public void loadRecentEvents(String pMPKey, Parliament pParliament)
	{
		MP pMP = pParliament.getMP(pMPKey);

		RssSaxParser parse = new RssSaxParser();
		String location = pMP.getRSSFeedURL();
		if(location == null || location.equals(".xml"))
		{
			return;
		}
		File aPoli = new File(Capone.getInstance().getProperties().getaDataLocation());
		InputStream aRSS = getInputStream(aPoli.getParentFile().getPath()+File.separator+"rss"+File.separator +location); 
		//Converting xml location to inputstream for parser
		if(aRSS == null)
		{
			return;
		}
		List<MPEvent> events = parse.parse(aRSS);
		for (MPEvent e : events)
		{
			System.out.println(e.getType().toString() + ": " + e.getTitle());
			if(e.getTitle().contains("House"))
			{ //finding only speeches
				Pattern speechPattern = Pattern.compile("/debates/[0-9]{4}/[0-9]{1,2}/[0-9]{1,2}/([A-z]+-)*[0-9]{1,2}"); //Creating parser for img url
				Matcher m = speechPattern.matcher(e.getLink()); //Finding where in the data folder each speech is
				String speechLoc = "";
				while (m.find())
				{
					speechLoc = m.group();
				}
				if(speechLoc.equals(""))
				{
					continue;
				}
				JSpeech jSp = getGson("./data" + speechLoc + ".json", JSpeech.class); //loading speech into json
				if(jSp == null)
				{
					continue;
				}
				Date date = null;
				try
				{
					date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(jSp.getTime()); //Date strings must be parsed
				}
				catch (ParseException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Speech s = new Speech(jSp.getH1().getEn(), jSp.getH2().getEn(), jSp.getContent().getEn(), date); 
				//loading jSpeech into a speech object
				pMP.addSpeech(s);
			}
		}
		
		
		// Remember: use the getRSSFeedURI method in an MP to get access to their feed.
		// new RssSaxParser().parse(xmlStream); // API Call to use the SAX Parser
	}
	
	// Below are some potentially useful utility methods. You may need to 
	// experiment with them a bit to make sure you understand how they work.
	
	/*
	 * Takes as input the speech link in the RSS item and returns 
	 * the partial path leading to the corresponding speech file in
	 * the debate directory tree.
	 */

	private String getSpeechContext(String pRssSpokeInTheHouseLink) 
	{
		String httpLink = pRssSpokeInTheHouseLink;
		String speechContextHttp = CONTEXT_SPEECHES.replace(File.separator, "/");
		int start = httpLink.indexOf(speechContextHttp)+speechContextHttp.length();
		String context = httpLink.substring(start, httpLink.length()-1).replace("/", File.separator);
		return context;
	}	
	
	/**
	 * Uses the google-gson library to convert JSON to Java objects (prefixed 
	 * those objects with J, in package ...loaders.op.stubs directory).
	 * 
	 * @param pJsonUri Filename of the JSON data
	 * @param ptype A Class object from the package ...loaders.op.stubs
	 * @return A GSON stub object, specified by the parameter "type" 
	 * CSOFF:
	 */
	public <T> T getGson(String pJsonUri, Class<T> ptype)
	{
		try{
			InputStreamReader json = new InputStreamReader(getInputStream(pJsonUri));
			T gson = new Gson().fromJson(json, ptype);
			return gson;
		}
		catch(NullPointerException e)
		{
			Capone.LOGGER.log(Level.WARNING, "Error getting Gson from " +pJsonUri);
			return null;
		}
		
	}
	// CSON:
	
	/**
	 * @param pMPRelativeLocation Relative location that identifies the MP, returned by getMPRelativeLocations
	 * @return Filename of the JSON data  on the given MP
	 */
	public String getMPJsonUri(String pMPRelativeLocation)
	{
		return QUERY_CONTEXT + CONTEXT_POLITICIANS + pMPRelativeLocation + JSON_SUFFIX;
	}

	/**
	 * @param pSpeechContext The speech context obtained from the link from the 
	 * 		   RSS feed "Spoke in the house" event, e.g., debates/2013/6/17/thomas-mulcair-4/
	 * @return  Filename of the JSON data on transcript of the given MP's speeches
	 */
	public String getSpeechJsonUri(String pSpeechContext)
	{
		return QUERY_CONTEXT + CONTEXT_SPEECHES + pSpeechContext + JSON_SUFFIX;
	}

	/**
	 *  @param pMPRelativeLocation Relative location that identifies the MP, returned by getMPRelativeLocations
	 * @return Filename of the RSS feed for the specified MP.
	 */
	public String getRssUri(String pMPRelativeLocation)
	{
		return QUERY_CONTEXT + CONTEXT_RSS + pMPRelativeLocation + ".xml";
	}

	/*
	 * Return a FileInputStream for the given path
	 */
	private InputStream getInputStream(String pFilePath)
	{
		try
		{
			File file = new File(pFilePath);
			return new FileInputStream(file);
		}
		catch (FileNotFoundException e)
		{
			Capone.LOGGER.log(Level.WARNING, "Error loading file " + pFilePath);
			return null;
		}
	}
	
	/**
	 * @return the relative locations of all the MPs.
	 */
	public Iterator<String> getMPRelativeLocations() 
	{
		List<String> result = new ArrayList<String>();
		String dir = QUERY_CONTEXT + CONTEXT_POLITICIANS; 
		Collection<File> files = FileUtils.listFiles(new File(dir), new WildcardFileFilter("*.json"), null);
		
		for ( File file : files )
		{
			String f = file.getName();
			result.add(f.substring(0, f.length()-".json".length()));
		}
		return result.iterator();
	}
}



