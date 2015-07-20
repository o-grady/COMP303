package ca.mcgill.cs.comp303.capone.ui;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.NavigableMap;

import javax.swing.table.AbstractTableModel;

import ca.mcgill.cs.comp303.capone.model.Capone;
import ca.mcgill.cs.comp303.capone.model.MP;
import ca.mcgill.cs.comp303.capone.model.Parliament;
import ca.mcgill.cs.comp303.capone.model.Speech;
import ca.mcgill.cs.comp303.capone.model.Triple;
import ca.mcgill.cs.comp303.capone.model.UserProfile;



class SpeechTableModel extends AbstractTableModel 
{
    private Object[][] aTableData ;
    private String[] aColumnNames = {Messages.getString("SpeechTableModel.Rank"), Messages.getString("SpeechTableModel.MP"), Messages.getString("SpeechTableModel.Date"), Messages.getString("SpeechTableModel.KeyWords"), Messages.getString("SpeechTableModel.Title")}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
    ArrayList<Object[]> aDataList = new ArrayList<Object[]>();
    public SpeechTableModel() 
    {
    	
		for(UserProfile aUser : Capone.getInstance().getUsers())
		{
			ArrayList<Triple<Speech, MP>> aSpeechList = aUser.getaCurrentRecommendedList();
			int i = 0;
			while(i < aSpeechList.size())
			{
				NavigableMap<Integer, String>  keywords = aSpeechList.get(i).getFirst().getKeywords();
				Object[] aSpeechData = new Object[6];
				Speech aSpeech = aSpeechList.get(i).getFirst();
				aSpeechData[0] = i+1 + Messages.getString("SpeechTableModel.period"); //$NON-NLS-1$
				aSpeechData[1] = aSpeechList.get(i).getSecond().getName() + Messages.getString("SpeechTableModel.openParen") + aSpeechList.get(i).getSecond().getCurrentMembership().getParty().getShortName() + Messages.getString("SpeechTableModel.CloseParen") + aSpeechList.get(i).getSecond().getCurrentMembership().getRiding().getName() + Messages.getString("SpeechTableModel.Comma") + aSpeechList.get(i).getSecond().getCurrentMembership().getRiding().getProvince(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				aSpeechData[2] = aSpeechList.get(i).getFirst().getTime().toString();
				aSpeechData[3] = Messages.getString("SpeechTableModel.9"); //$NON-NLS-1$
				for (int j = 0; j < 3; j++)
				{
					Entry<Integer, String> aEntry = keywords.lastEntry();
					if(aEntry != null && !aEntry.getValue().equals(Messages.getString("SpeechTableModel.10"))) //$NON-NLS-1$
					{
						aSpeechData[3] += aEntry.getValue();
						if (j != 2)
						{
							aSpeechData[3] += Messages.getString("SpeechTableModel.Comma2"); //$NON-NLS-1$
						}
					}
					keywords.pollLastEntry();
				}
				aSpeechData[4] = aSpeechList.get(i).getFirst().getHeader2();
				aSpeechData[5] = aSpeech;
				aDataList.add(aSpeechData);
				i++;
			}
		}
		Object[][] aDataArray = new Object[aDataList.size()][6];
		aDataArray = aDataList.toArray(aDataArray);
		this.aTableData = aDataArray;
    }
  
	@Override
    public Object getValueAt(int pRowIndex, int pColumnIndex) 
	{
        return aTableData[pRowIndex][pColumnIndex];
    }
	@Override
    public void setValueAt(Object pObj, int pRowIndex, int pColumnIndex)
	{
        aTableData[pRowIndex][pColumnIndex] = pObj;
    }
    @Override
    public int getRowCount()
    {
        return this.aTableData.length;
    }
    @Override
    public int getColumnCount() 
    {
        return aColumnNames.length;
    }
    @Override
    public String getColumnName(int pColumn) 
    {
        return aColumnNames[pColumn];
    }
    @Override
    public Class<?> getColumnClass(int pColumnIndex) 
    {
       
        return String.class;
    }
    @Override
    public boolean isCellEditable(int pRow, int pCol) 
    {
        return false; 
    }
}
