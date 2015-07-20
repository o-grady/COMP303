package ca.mcgill.cs.comp303.capone.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.logging.Level;

/** Writes and reads UserProfiles to/from a binary .ser file.
 * @author Grady
 *
 */
public class BinaryReadWriteUsers implements IReadWriteUsers
{
	@Override
	public void writeUsersToFile(ArrayList<UserProfile> pUsers)
	{
		/*if(Capone.getInstance().getProperties().getaUserProfileSaveLocation().equals(""))
		{
			Capone.getInstance().getProperties().setaUserProfileSaveLocation(System.getProperty("user.home") + File.separator + "users.ser");
			File aUsers = new File(Capone.getInstance().getProperties().getaUserProfileSaveLocation());
			try
			{
				aUsers.createNewFile();
			}
			catch (IOException e)
			{
				Capone.LOGGER.log(Level.SEVERE, "Error creating users.ser");
			}
		}*/
		
		File users = new File(Properties.getProperties().getaUserProfileSaveLocation());
		users.setReadable(true);
		users.setWritable(true);
		try
		(
			FileOutputStream fout = new FileOutputStream(users);
			ObjectOutputStream oos = new ObjectOutputStream(fout);
		)
		{
			oos.writeObject(pUsers);
		}
		catch(IOException e)
		{
			System.out.println(e.getMessage());
			Capone.LOGGER.log(Level.WARNING, "Error saving users.");
		}
	}

	@Override
	public ArrayList<UserProfile> readUsersFromFile()
	{
		
		/*if(Properties.getProperties().getaUserProfileSaveLocation().equals(""))
		{
			Capone.getInstance().getProperties().setaUserProfileSaveLocation(System.getProperty("user.home") + File.separator + "users.ser");
			File aUsers = new File(Capone.getInstance().getProperties().getaUserProfileSaveLocation());
			try
			{
				aUsers.createNewFile();
			}
			catch (IOException e)
			{
				Capone.LOGGER.log(Level.SEVERE, "Error creating users.ser");
			}
		}*/
		File users = new File(Properties.getProperties().getaUserProfileSaveLocation());
		users.setReadable(true);
		users.setWritable(true);
		ArrayList<UserProfile> ret = new ArrayList<UserProfile>();
		try
		(
			FileInputStream fin = new FileInputStream(users);
			ObjectInputStream ois = new ObjectInputStream(fin);	
		)
		{
			
			ret = (ArrayList<UserProfile>)ois.readObject();
		}
		catch(IOException | ClassNotFoundException e)
		{
			Capone.LOGGER.log(Level.WARNING, "Error loading users.");
		}
		return ret;
	}
}

