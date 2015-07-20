package ca.mcgill.cs.comp303.capone.ui;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import ca.mcgill.cs.comp303.capone.model.Capone;
import ca.mcgill.cs.comp303.capone.model.MP;
import ca.mcgill.cs.comp303.capone.model.Parliament;
import ca.mcgill.cs.comp303.capone.model.UserProfile;



class UserMPTableModel extends AbstractTableModel 
{
    private ArrayList<Object[]> aTableData ;
    private String[] aColumnNames = {Messages.getString("UserMPTableModel.Name"), Messages.getString("UserMPTableModel.Party"), Messages.getString("UserMPTableModel.Riding"), Messages.getString("UserMPTableModel.Province"), Messages.getString("UserMPTableModel.INProfile")}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$

    public UserMPTableModel() {
		Parliament aParliament = Capone.getInstance().getParliament();
		ArrayList<Object[]> aDataList = new ArrayList<Object[]>();
		for(UserProfile aUser : Capone.getInstance().getUsers())
		{
			for(String s : aUser.getMPs())
			{
				Object[] aMPData = new Object[6];
				MP aMP = aParliament.getMP(s);
				if(aMP == null)
				{
					continue;
				}
				aMPData[0] = aMP.getName();
				aMPData[1] = aMP.getCurrentMembership().getParty().getName();
				aMPData[2] = aMP.getCurrentMembership().getRiding().getName();
				aMPData[3] = aMP.getCurrentMembership().getRiding().getProvince();
				aMPData[4] = (boolean)true;
				aMPData[5] = aMP;
				aDataList.add(aMPData);
			}
		}
		this.aTableData = aDataList;
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
    
    public void removeRow(int pRow)
    {
    	aTableData.remove(pRow);
    }
}
