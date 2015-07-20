package ca.mcgill.cs.comp303.capone.model;

import java.io.File;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import ca.mcgill.cs.comp303.capone.ui.CaponeUI;



/**
 * Driver for Milestone2. 
 * Performs the following processes:
 * - Loads the Parliament with data-before-june-2013
 * - Creates a user profile with interest in Thomas Mulclair and Stephen Harper and topics "fishing" and "recreational fisheries"
 * - Recommends using the ContentBasedRecommender. Top 3 recommended speeches output to console in the format:
 *   MP Name	Date	h2 Title	First Sentence
 * - Recomments using the SimilarityBasedRecommender. Top 3 recommended speeches output as above.
 * - Load the Parliament with data-since-june-2013 (MPs and Events)
 * - Recommendations printed as above.
 * - Persist both user profiles in both formats in out.dat and out.json using relative paths.
 * 
 * @author kdick
 *
 */
public class Milestone2Driver 
{

	static final String OUTPUT_DIR = "." + File.separator + "output" + File.separator;
	static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	static final String CAPONE_M2_COMPLETE_DIR = "Capone-M2-Complete" + File.separator; // TODO :: Verify this
	/**
	 * The driver for milestone 2.
	 * @param args default import
	 */
	public static void main(String [] args)
	{
		// TODO :: Create out.dat and out.json files to write to.
		Parliament aParliament = Capone.getInstance().getParliament();
		// Loads the Parliament with data-before-june-2013
		String politicianDir = CAPONE_M2_COMPLETE_DIR + "data-snapshot-before-june-2013" + File.separator + "politicians" + File.separator;
		new ParliamentRefresh(aParliament, politicianDir);
		// TODO :: Create a new user profile
		UserProfile aUser = new UserProfile();
		// TODO :: Add interest to Thomas Mulclair and Stephen Harper
		for (MP aMP : aParliament.getAllMPs().values())
		{
			if(aMP.getFamilyName().equals("Mulcair"))
			{
				aUser.addMP(aMP.getPrimaryKey());
			}
			else if (aMP.getFamilyName().equals("Harper"))
			{
				aUser.addMP(aMP.getPrimaryKey());
			}
		}
		
		aUser.addKeyword("fishing");
		aUser.addKeyword("recreational fisheries");
		aUser.updateRecommender(UserProfile.recommenderType.CONTENT);
		Capone.getInstance().addUserProfile(aUser);
		
		CaponeUI aUI = new CaponeUI();
		aUI.main(args);
		/*ArrayList<Triple<Speech, MP>> cSpeeches = aUser.recommendSpeechesWithMP(new ContentBasedRecommender(), aParliament);
		for (int i = 0; i < 3; i++)
		{
			Triple<Speech, MP> s = cSpeeches.get(i);
			MP aMP = s.getSecond();
			Speech aSpeech =  s.getFirst();
			System.out.println(aMP.getName() + ":" + aSpeech.getTime() + ":" + aSpeech.getHeader2() + ":" + aSpeech.getContent().substring(0, aSpeech.getContent().indexOf(".")));
	
		}
		// TODO :: Print top 3 to console then write to file
		ArrayList<Triple<Speech, MP>> sSpeeches = aUser.recommendSpeechesWithMP(new SimilarityBasedRecommender(), aParliament);
		for (int i = 0; i < 3; i++)
		{
			Triple<Speech, MP> s = sSpeeches.get(i);
			MP aMP = s.getSecond();
			Speech aSpeech = s.getFirst();
			System.out.println(aMP.getName() + ":" + aSpeech.getTime() + ":" + aSpeech.getHeader2() + ":" + aSpeech.getContent().substring(0, aSpeech.getContent().indexOf(".")));
		}
		// TODO :: Apply the SimilarityBasedRecommender
		System.out.println("------------------------------------------------");
		// TODO :: Print top 3 to console then write to file
		
		// Load the Parliament with data-after-june-2013
		politicianDir = CAPONE_M2_COMPLETE_DIR + "data-snapshot-since-june-2013" + File.separator + "politicians" + File.separator;
		new ParliamentRefresh(aParliament, politicianDir);
		
		cSpeeches = aUser.recommendSpeechesWithMP(new ContentBasedRecommender(), aParliament);
		for (int i = 0; i < 3; i++)
		{
			Triple<Speech, MP> s = cSpeeches.get(i);
			MP aMP = s.getSecond();
			Speech aSpeech =  s.getFirst();
			System.out.println(aMP.getName() + ":" + aSpeech.getTime() + ":" + aSpeech.getHeader2() + ":" + aSpeech.getContent().substring(0, aSpeech.getContent().indexOf(".")));
	
		}
		// TODO :: Print top 3 to console then write to file
		sSpeeches = aUser.recommendSpeechesWithMP(new SimilarityBasedRecommender(), aParliament);
		for (int i = 0; i < 3; i++)
		{
			Triple<Speech, MP> s = sSpeeches.get(i);
			MP aMP = s.getSecond();
			Speech aSpeech = s.getFirst();
			System.out.println(aMP.getName() + ":" + aSpeech.getTime() + ":" + aSpeech.getHeader2() + ":" + aSpeech.getContent().substring(0, aSpeech.getContent().indexOf(".")));
		}
		
		Capone.getInstance().setReadWriteType(new JSONReadWriteUsers());
		Capone.getInstance().saveUsers(new JSONReadWriteUsers());
		Capone.getInstance().setReadWriteType(new BinaryReadWriteUsers());
		Capone.getInstance().saveUsers(new BinaryReadWriteUsers());*/

	}
}