package ca.mcgill.cs.comp303.capone.ui;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import ca.mcgill.cs.comp303.capone.model.Capone;

import ca.mcgill.cs.comp303.capone.model.UserProfile;



class KwTableModel extends AbstractTableModel 
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<Object[]> aTableData ;
    private String[] aColumnNames = {Messages.getString("KwTableModel.Keyword"), Messages.getString("KwTableModel.Remove")}; //$NON-NLS-1$ //$NON-NLS-2$

    public KwTableModel() 
    {
		ArrayList<Object[]> aDataList = new ArrayList<Object[]>();
		for(UserProfile aUser : Capone.getInstance().getUsers())
		{
			for(String s : aUser.getKeywords())
			{
				Object[] keyword = new Object[2];
				keyword[0] = s;
				keyword[1] = true;
				aDataList.add(keyword);
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
        if(pColumnIndex == 1)
        {
        	return Boolean.class;
        }
    	return String.class;
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
