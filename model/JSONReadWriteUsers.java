package ca.mcgill.cs.comp303.capone.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.logging.Level;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


/** Writes and reads UserProfiles to/from a JSON file.
 * @author Grady
 *
 */
public class JSONReadWriteUsers implements IReadWriteUsers
{

	@Override
	public void writeUsersToFile(ArrayList<UserProfile> pUsers)
	{
		Gson gson = new Gson();
	 
		// convert java object to JSON format,
		// and returned as JSON formatted string
		String json = gson.toJson(pUsers);
	 
		try 
		{
			//write converted json data to a file named "out.json"
			FileWriter writer = new FileWriter(Capone.getInstance().getProperties().getaUserJsonLocation());
			writer.write(json);
			writer.close();
	 
		} 
		catch (IOException e) 
		{
			Capone.LOGGER.log(Level.SEVERE, "Error writing UserProfiles to JSON");
		}
	}

	/* (non-Javadoc)
	 * @see ca.mcgill.cs.comp303.capone.model.IReadWriteUsers#readUsersFromFile()
	 */
	@Override
	public ArrayList<UserProfile> readUsersFromFile()
	{
		Gson gson = new Gson();
		ArrayList<UserProfile> list = new ArrayList<UserProfile>();
		try 
		{
			BufferedReader br = new BufferedReader(
				new FileReader("out.json"));
	 
			//convert the json string back to object
			Type listOfUserProfile = new TypeToken<ArrayList<UserProfile>>(){}.getType();
			list = gson.fromJson(br, listOfUserProfile);
	 
		} 
		catch (IOException e)
		{
			Capone.LOGGER.log(Level.SEVERE, "Error loading users from JSON");
		}
		return list;
	}


}
