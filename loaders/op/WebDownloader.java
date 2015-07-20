package ca.mcgill.cs.comp303.capone.loaders.op;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;

import ca.mcgill.cs.comp303.capone.model.Capone;
import ca.mcgill.cs.comp303.capone.model.MP;

import org.apache.commons.io.FileUtils;


/**
 * Downloader for the web data to store .json files in the Properties specified directory.
 * This class assumes that the specified directory has been loaded up prior to use.
 * @author kdick
 *
 */
public class WebDownloader 
{
	private static final String CONTEXT_POLITICIANS = "politicians" + File.separator;
	
	/**
	 * Acquires the most recent Politician .json files from openparliament.ca
	 * @author kdick
	 */
	public static void downloadPoliticians ()
	{
		// First get a manageable list of all the Politicians
		String url = "http://openparliament.ca/politicians/";
		URL politicianURL = null;
		try {
			politicianURL = new URL(url);
		} catch (MalformedURLException e) {
			Capone.LOGGER.log(Level.SEVERE, "Error: Could not form URL for politician.");
		}
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(politicianURL.openStream()));
		} catch (IOException e) {
			Capone.LOGGER.log(Level.SEVERE, "Error: Could not initiate Buffer for downloadPoliticians()");
		}

//		ArrayList<URL> poliURLs = new ArrayList<URL>();
		ArrayList<String> poliNames = new ArrayList<String>();
		String inLine = null;
		try {
			while ((inLine = in.readLine()) != null){
				// Parse out the politician names and their URLs
				String[] temp = new String[3]; 
				
				if (inLine.contains("<li class=\"three oneline\"><a href=\"")){
					// Parse out the /politicians/name/
					String pulledURL = inLine.substring(48);
					String [] holdValue = pulledURL.split(" class=\"tip\" title");
					holdValue[0] = holdValue[0].substring(0, holdValue[0].length()-2); // Remove last two characters
					pulledURL = url + holdValue[0];
					
					// Add Elements to the array list
					poliNames.add(holdValue[0]);
//					URL toAdd = new URL(pulledURL);
//					poliURLs.add(toAdd);
					System.out.println(holdValue[0]);
				}
			}
			in.close();
		} catch (IOException e) {
			Capone.LOGGER.log(Level.SEVERE, "Error while acquiring politician data from web.");
		}

		// Now acquire the content for each politician
		for (String politician : poliNames){
			try {
				// Format the URL required for this politician
				URL thisPoliticianURL = new URL("http://api.openparliament.ca/politicians/" + politician +"/?format=json");
			
				File tmpFile = new File (Capone.getInstance().getProperties().getaDataLocation() + File.separator +
					politician + ".json");
//				File tmpFile = new File("/home/2012/kdick/Desktop/TEST/" + File.separator + CONTEXT_POLITICIANS+ politician+".json");
			
				System.out.println(tmpFile.toString());
				System.out.println("POLI: " + politician);
				FileUtils.copyURLToFile(thisPoliticianURL, tmpFile);
				Capone.LOGGER.log(Level.INFO, "Downloaded "+politician+" to disk at "+System.currentTimeMillis());
			} catch (IOException e) {
				Capone.LOGGER.log(Level.SEVERE, "Error: Could not acquire politician data for " + politician);
			}
		}
	}
	
	
	/**
	 * Updates the speeches based on the newly updated politicians.
	 * Assumption: Always called AFTER downloadPoliticians to maintain updatedness.
	 * @author kdick
	 */
	public static void updateDebates()
	{
		File aPolit = new File(Capone.getInstance().getProperties().getaDataLocation());
		String saveloc = aPolit.getParentFile().getAbsolutePath() + File.separator + "rss" + File.separator;
		for(MP aMP : Capone.getInstance().getParliament().getAllMPs().values())
		{
			String rss = aMP.getRSSFeedURL();
			rss = rss.substring(0, rss.length()-4);
			if(rss.equals(""))
			{
				continue;
			}
			rss = "http://openparliament.ca/politicians/"+rss+"/rss/statements/";
			System.out.println(rss);
			URL website = null;
			try
			{
				website = new URL(rss);
			}
			catch(MalformedURLException e)
			{
				Capone.LOGGER.log(Level.SEVERE, "Malformed RSS URL");
			}
			
			ReadableByteChannel rbc = null;
			try
			{
				rbc = Channels.newChannel(website.openStream());
			}
			catch (IOException e)
			{
				Capone.LOGGER.log(Level.SEVERE, "Error loading new speeches");
			}
			FileOutputStream fos = null;
			try
			{
				fos = new FileOutputStream(saveloc+aMP.getRSSFeedURL());
				System.out.println(saveloc + aMP.getRSSFeedURL());
			}
			catch (FileNotFoundException e)
			{
				Capone.LOGGER.log(Level.SEVERE, "Error Writing Speech to File");
			}
			try
			{
				fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			}
			catch (IOException e)
			{
				Capone.LOGGER.log(Level.SEVERE, "Error Writing Speech to File");
			}
			try
			{
				fos.close();
			}
			catch (IOException e)
			{
				Capone.LOGGER.log(Level.SEVERE, "Error Writing Speech to File");
			}
		}
	}
	
	// Testing Purposes	
//	public static void main (String [] args){
//		System.out.println("Start");
//				
//		downloadPoliticians ();
//		System.out.println("PoliDone");
//		updateDebates();
//		System.out.println("End");
//	}
}
