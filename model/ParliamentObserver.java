package ca.mcgill.cs.comp303.capone.model;

import java.util.ArrayList;

/**
 * Concrete Observer.
 * Watches the parliament and alerts UserProfiles to update when parliament changes.
 * @author MIKE
 */
public class ParliamentObserver
{
/**
 * Tells all user profiles to update.
 * @param pParliament The updated parliament.
 * @param pUsers : All users to be informed of the update.
 */
	public void update(Parliament pParliament, ArrayList<UserProfile> pUsers)
	{
		for(UserProfile aProfile: pUsers)
		{
			aProfile.update(pParliament);
		}
		
	}

}
