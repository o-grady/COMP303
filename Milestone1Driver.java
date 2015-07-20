package ca.mcgill.cs.comp303.capone;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;


import ca.mcgill.cs.comp303.capone.loaders.ParliamentLoader;
import ca.mcgill.cs.comp303.capone.loaders.op.OpenParliamentFileLoader;
import ca.mcgill.cs.comp303.capone.model.Capone;
import ca.mcgill.cs.comp303.capone.model.MP;


/**
 * Code to demonstrate the milestone 1 functionality.
 */
public final class Milestone1Driver
{
	static final String MULCAIR = "thomas-mulcair";
	static final String OUTPUT_DIR = "." + File.separator + "output" + File.separator;
	static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


	private Milestone1Driver() {}
	
	/**
	 * Demonstrates how to use the Parliament API.
	 * 
	 * @param pArgs Not used.
	 * @throws Exception Everything
	 */
	public static void main(String[] pArgs) throws Exception
	{
		ParliamentLoader loader = new OpenParliamentFileLoader();
		String key = loader.loadMP(MULCAIR, Capone.getInstance().getParliament());
		loader.loadRecentEvents(key, Capone.getInstance().getParliament());
		MP mulcair = Capone.getInstance().getParliament().getMP(key); // Requires additional implementation.
		System.out.println("WOAH");
		System.out.println(Queries.q1LongestServingMPs());
		PrintWriter lOut = null;
		try
		{
			lOut = new PrintWriter(new File(OUTPUT_DIR + MULCAIR + ".html"));
			lOut.println("<link rel=\"stylesheet\" href=\"main.css\" type=\"text/css\" media=\"screen, projection\" />");
		
			// Printout some more information about the MP into a webpage.
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (lOut != null)
			{
				lOut.close();
			}
		}
	}
}
