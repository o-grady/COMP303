package ca.mcgill.cs.comp303.capone.ui;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import ca.mcgill.cs.comp303.capone.model.Capone;
import ca.mcgill.cs.comp303.capone.model.MP;
import ca.mcgill.cs.comp303.capone.model.Parliament;
import ca.mcgill.cs.comp303.capone.model.UserProfile;



class MPTableModel extends AbstractTableModel 
{
    private ArrayList<Object[]> aTableData ;
    private String[] aColumnNames = {Messages.getString("MPTableModel.Name"), Messages.getString("MPTableModel.Party"), Messages.getString("MPTableModel.Riding"), Messages.getString("MPTableModel.Province"), Messages.getString("MPTableModel.InProfile")}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$

    public MPTableModel()
    {
		Parliament aParliament = Capone.getInstance().getParliament();
		ArrayList<Object[]> aDataList = new ArrayList<Object[]>();
		for(MP aMP : aParliament.getAllMPs().values())
		{
			Object[] aMPData = new Object[6];
			aMPData[0] = aMP.getName();
			aMPData[1] = aMP.getCurrentMembership().getParty().getName();
			aMPData[2] = aMP.getCurrentMembership().getRiding().getName();
			aMPData[3] = aMP.getCurrentMembership().getRiding().getProvince();
			aMPData[4] = (boolean)false;
			for(UserProfile aUser: Capone.getInstance().getUsers())
			{
				if(aUser.getMPs().contains(aMP.getPrimaryKey()))
				{
					aMPData[4] = true;
				}
			}
			aMPData[5] = aMP;
			aDataList.add(aMPData);
		}
		aTableData = aDataList;
    }
  
	@Override
    public Object getValueAt(int pRowIndex, int pColumnIndex)
	{
        return aTableData.get(pRowIndex)[pColumnIndex];
    }
	@Override
    public void setValueAt(Object pObj, int pRowIndex, int pColumnIndex) 
	{
        aTableData.get(pRowIndex)[pColumnIndex] = pObj;
    }
    @Override
    public int getRowCount() 
    {
        return this.aTableData.size();
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
        if (pColumnIndex == 4)
        {
            return Boolean.class;
        }
        else
        {
        	return String.class;
        }
    }
    @Override
    public boolean isCellEditable(int pRow, int pCol) 
    {
        return false; 
    }
    
}
