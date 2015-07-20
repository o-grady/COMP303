package ca.mcgill.cs.comp303.capone.model;

import java.util.ArrayList;

/** Interface for writing userprofiles to a file.
 * @author Grady
 *
 */
public interface IReadWriteUsers
{
	/** Write an ArrayList of UserProfiles to a file.
	 * @param pUsers the ArrayList to be written.
	 */
	void writeUsersToFile(ArrayList<UserProfile> pUsers);
	
	/** Reads stored data on UserProfiles and returns an ArrayList.
	 * @return An ArrayList of UserProfiles read from the file.
	 */
	ArrayList<UserProfile> readUsersFromFile();
}
