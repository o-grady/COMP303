package ca.mcgill.cs.comp303.capone.ui;

import java.awt.EventQueue;

import javax.swing.AbstractButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.RowFilter;
import javax.swing.RowSorter;

import java.awt.BorderLayout;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MouseInputAdapter;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.html.HTML;

import ca.mcgill.cs.comp303.capone.loaders.op.OpenParliamentFileLoader;
import ca.mcgill.cs.comp303.capone.loaders.op.WebDownloader;
import ca.mcgill.cs.comp303.capone.model.BinaryReadWriteUsers;
import ca.mcgill.cs.comp303.capone.model.Capone;
import ca.mcgill.cs.comp303.capone.model.JSONReadWriteUsers;
import ca.mcgill.cs.comp303.capone.model.MP;
import ca.mcgill.cs.comp303.capone.model.Parliament;
import ca.mcgill.cs.comp303.capone.model.Properties;
import ca.mcgill.cs.comp303.capone.model.Speech;
import ca.mcgill.cs.comp303.capone.model.UserProfile;

import java.awt.Component;
import java.awt.Color;
import javax.swing.JScrollBar;
import java.awt.ComponentOrientation;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import ca.mcgill.cs.comp303.capone.model.UserProfile.recommenderType;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.Box;
import javax.swing.JButton;
import java.awt.Font;
import java.io.File;

public class CaponeUI
{

	private static final String PROVINCE_DEFAULT = Messages.getString("CaponeUI.ViewAllProvinces"); //$NON-NLS-1$
	private static final String PARTY_DEFAULT = Messages.getString("CaponeUI.ViewAllParties"); //$NON-NLS-1$
	private JFrame aFrame;
	private JTable aMPTable;
	private JTable aSpeechTable;
	private JTable aProMPTable;
	private JTextField aKwInput;
	private JTable aKwTable;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args)
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					CaponeUI window = new CaponeUI();
					window.aFrame.setVisible(true);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public CaponeUI()
	{
		Capone.getInstance();
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize()
	{
		Capone.getInstance().loadUsers(new BinaryReadWriteUsers());
		if(Capone.getInstance().getUsers().isEmpty())
		{
			UserProfile aUser = new UserProfile();
			Capone.getInstance().addUserProfile(aUser);
		}
		if(Capone.getInstance().getProperties().isaAutoLoad())
		{
			JOptionPane.showMessageDialog(null, Messages.getString("CaponeUI.PopulatingParliament")); //$NON-NLS-1$
			Capone.getInstance().getParliament().refreshParliament(Properties.getProperties().getaDataLocation());
		}
		aFrame = new JFrame();
		aFrame.setBounds(100, 100, 450, 300);
		aFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		aFrame.setJMenuBar(menuBar);
		
		final JTabbedPane lTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		
		JMenu mnData = new JMenu(Messages.getString("CaponeUI.Data")); //$NON-NLS-1$
		menuBar.add(mnData);
		
		JMenuItem mntmLoadAllFromD = new JMenuItem(Messages.getString("CaponeUI.LoadAllDisk")); //$NON-NLS-1$
		mnData.add(mntmLoadAllFromD);
		
		mntmLoadAllFromD.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent pEvent)
			{
				JFileChooser dataLocChooser;
				if(Capone.getInstance().getProperties().getaDataLocation() != null)
				{
					String dataLoc = Capone.getInstance().getProperties().getaDataLocation();
					dataLocChooser = new JFileChooser(dataLoc);
				}
				else
				{
					dataLocChooser = new JFileChooser();
				}
				dataLocChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				JOptionPane.showMessageDialog(null, Messages.getString("CaponeUI.SelectDir")); //$NON-NLS-1$
				int returnVal = dataLocChooser.showOpenDialog(aFrame);
				if(returnVal == JFileChooser.APPROVE_OPTION)
				{
					File nDataLoc = dataLocChooser.getSelectedFile();
					JOptionPane.showMessageDialog(null, Messages.getString("CaponeUI.PleaseWait")); //$NON-NLS-1$
					Capone.getInstance().getProperties().setaDataLocation(nDataLoc.getAbsolutePath());
					Capone.getInstance().getParliament().refreshParliament(nDataLoc.getAbsolutePath());
					lTabbedPane.removeAll();
					initilizeMPView(lTabbedPane);
					initilizeProfileView(lTabbedPane);
					initilizeRecommenderView(lTabbedPane);
					updateRecTable(Capone.getInstance().getProperties().getaCurrentRecommender());
				}
			}
			
		});
		
		JMenuItem mntmAutoload = new JMenuItem(Messages.getString("CaponeUI.AutoLoad1") + Capone.getInstance().getProperties().isaAutoLoad()); //$NON-NLS-1$
		mnData.add(mntmAutoload);
		
		mntmAutoload.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent pEvent)
			{
				Capone.getInstance().getProperties().setaAutoLoad(!Capone.getInstance().getProperties().isaAutoLoad());
				((JMenuItem) pEvent.getSource()).setText(Messages.getString("CaponeUI.AutoLoad2") + Capone.getInstance().getProperties().isaAutoLoad()); //$NON-NLS-1$
			}
			
		});
		
		JMenuItem mntmLoadAllFromW = new JMenuItem(Messages.getString("CaponeUI.LoadAllWeb")); //$NON-NLS-1$
		mnData.add(mntmLoadAllFromW);
		
		mntmLoadAllFromW.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent pEvent)
			{
				WebDownloader.downloadPoliticians();
				Capone.getInstance().getParliament().refreshParliament(Capone.getInstance().getProperties().getaDataLocation());
				lTabbedPane.removeAll();
				initilizeMPView(lTabbedPane);
				initilizeProfileView(lTabbedPane);
				initilizeRecommenderView(lTabbedPane);
				updateRecTable(Capone.getInstance().getProperties().getaCurrentRecommender());
			}
			
		});
		
		JMenuItem mntmUpdateSpeeches = new JMenuItem(Messages.getString("CaponeUI.UpdateSpeeches")); //$NON-NLS-1$
		mnData.add(mntmUpdateSpeeches);
		
		mntmUpdateSpeeches.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent pEvent)
			{
				WebDownloader.updateDebates();
				for(MP aMP : Capone.getInstance().getParliament().getAllMPs().values())
				{
					OpenParliamentFileLoader loader = new OpenParliamentFileLoader();
					loader.loadRecentEvents(aMP.getPrimaryKey(), Capone.getInstance().getParliament());
				}
				lTabbedPane.remove(2);
				initilizeRecommenderView(lTabbedPane);
				updateRecTable(Capone.getInstance().getProperties().getaCurrentRecommender());
			}
			
		});
		
		JMenu mnProfile = new JMenu(Messages.getString("CaponeUI.Profile")); //$NON-NLS-1$
		menuBar.add(mnProfile);
		
		JMenuItem mntmExportAsJson = new JMenuItem(Messages.getString("CaponeUI.ExportJSON")); //$NON-NLS-1$
		mnProfile.add(mntmExportAsJson);
		
		mntmExportAsJson.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent pEvent)
			{
			    String sb = Messages.getString("CaponeUI.13"); //$NON-NLS-1$
			    JFileChooser chooser = new JFileChooser();
			    if(!Capone.getInstance().getProperties().getaUserJsonLocation().equals(Messages.getString("CaponeUI.14"))) //$NON-NLS-1$
			    {
			    	chooser.setCurrentDirectory(new File(Capone.getInstance().getProperties().getaUserJsonLocation()));
			    }
			    int retrival = chooser.showSaveDialog(null);
			    if (retrival == JFileChooser.APPROVE_OPTION) 
			    {
				        try 
				        {
				            File aFile = new File(chooser.getSelectedFile()+Messages.getString("CaponeUI.15")); //$NON-NLS-1$
				            Capone.getInstance().getProperties().setaUserJsonLocation(aFile.getAbsolutePath());
				            Capone.getInstance().saveUsers(new JSONReadWriteUsers());
				            
				        } 
				        catch (Exception ex) 
				        {
				            ex.printStackTrace();
				        }
				    }
				}
			
		});
		
		JMenuItem mntmForgetReadSpeeches = new JMenuItem(Messages.getString("CaponeUI.ForgetRead")); //$NON-NLS-1$
		mnProfile.add(mntmForgetReadSpeeches);
		
		mntmForgetReadSpeeches.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent pEvent)
			{
				for(UserProfile aUser : Capone.getInstance().getUsers())
				{
					aUser.resetReadSpeeches();
				}
				updateRecTable(Capone.getInstance().getProperties().getaCurrentRecommender());
			}
			
		});
		
		JMenu mnAbout = new JMenu(Messages.getString("CaponeUI.Help")); //$NON-NLS-1$
		menuBar.add(mnAbout);
		
		JMenuItem mntmAbout = new JMenuItem(Messages.getString("CaponeUI.About")); //$NON-NLS-1$
		mnAbout.add(mntmAbout);
		
		
		
		mntmAbout.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent pEvent)
			{
				JOptionPane.showMessageDialog(null, Messages.getString("CaponeUI.CreatedBy")); //$NON-NLS-1$

			}
			
		});
		
		
		aFrame.getContentPane().add(lTabbedPane, BorderLayout.CENTER);
		
		initilizeMPView(lTabbedPane);
		
		initilizeProfileView(lTabbedPane);
		
		initilizeRecommenderView(lTabbedPane);
	}

	private void initilizeMPView(final JTabbedPane pTabbedPane)
	{
		JPanel aMPView = new JPanel();
		pTabbedPane.addTab(Messages.getString("CaponeUI.MP"), null, aMPView, null); //$NON-NLS-1$
		
		TableModel mpData = new MPTableModel();
		aMPView.setLayout(new BorderLayout(0, 0));
		aMPTable = new JTable(mpData);
		aMPTable.setAutoCreateRowSorter(true);
		aMPTable.setBackground(Color.WHITE);
		aMPTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		
		aMPTable.addMouseListener(new MouseInputAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent pEvent) 
			{
	           int aRow = aMPTable.rowAtPoint(pEvent.getPoint());
	           aRow = aMPTable.convertRowIndexToModel(aRow);
	           int aCol = aMPTable.columnAtPoint(pEvent.getPoint());
	           aCol = aMPTable.convertColumnIndexToModel(aCol);
	           if(aCol == 4)
	           {
	        	   MP aMP = (MP)aMPTable.getModel().getValueAt(aRow, 5);
	        	   if((boolean)aMPTable.getModel().getValueAt(aRow, aCol) == true)
	        	   {
	        		   for(UserProfile aUser : Capone.getInstance().getUsers())
	        		   {
	        			   aUser.removeMP(aMP.getPrimaryKey());
	        		   }
	        		   updateMPTable();
	        		   updateProMPTable();
	        		   updateRecTable(Capone.getInstance().getProperties().getaCurrentRecommender());
	        	   }
	        	   else
	        	   {
	        		   for(UserProfile aUser : Capone.getInstance().getUsers())
	        		   {
	        			   aUser.addMP(aMP.getPrimaryKey());   
	        		   }
	        		   updateMPTable();
	        		   updateProMPTable();
	        		   updateRecTable(Capone.getInstance().getProperties().getaCurrentRecommender());
	        	   }
	           }
		}
	});

		aMPView.add(new JScrollPane(aMPTable));
		
		Box filtersBox = Box.createHorizontalBox();
		aMPView.add(filtersBox, BorderLayout.SOUTH);
		
		final JComboBox<String> lPartyFilterBox = new JComboBox<String>();
		filtersBox.add(lPartyFilterBox);
		lPartyFilterBox.addItem(PARTY_DEFAULT);
		
		final JComboBox<String> lProvFilterBox = new JComboBox<String>();
		filtersBox.add(lProvFilterBox);
		lProvFilterBox.addItem(PROVINCE_DEFAULT);
		
		for(int i = 0; i < aMPTable.getModel().getRowCount(); i++)
		{
			String aParty = (String) aMPTable.getModel().getValueAt(i, 1);
			Boolean bParty = true;
			String aProvince = (String) aMPTable.getModel().getValueAt(i, 3);
			Boolean bProvince = true;
			for(int j = 0; j < lPartyFilterBox.getItemCount(); j++)
			{
				if(lPartyFilterBox.getItemAt(j).equals(aParty))
				{
					bParty = false;
				}
			}
			for(int j = 0; j < lProvFilterBox.getItemCount(); j++)
			{
				if(lProvFilterBox.getItemAt(j).equals(aProvince))
				{
					bProvince = false;
				}
			}
			if(bParty)
			{
				lPartyFilterBox.addItem(aParty);
			}
			if(bProvince)
			{
				lProvFilterBox.addItem(aProvince);
			}
		}
		
		List<RowFilter<Object, Object>> filters = new ArrayList<RowFilter<Object, Object>>(2);
		filters.add(RowFilter.regexFilter(Messages.getString("CaponeUI.21"), 1)); //$NON-NLS-1$
		filters.add(RowFilter.regexFilter(Messages.getString("CaponeUI.22"), 3)); //$NON-NLS-1$
		RowFilter<Object, Object> foobarFilter = RowFilter.andFilter(filters);
		
		lPartyFilterBox.addItemListener( new ItemListener()
		{
			public void itemStateChanged(ItemEvent pEvent) 
			{
				if (pEvent.getStateChange() == ItemEvent.SELECTED) 
				{
					String aParty = (String)pEvent.getItem();
					String aProvince = (String)lProvFilterBox.getSelectedItem();
					List<RowFilter<Object, Object>> filters = new ArrayList<RowFilter<Object, Object>>(2);
					if(!aParty.equals(PARTY_DEFAULT))
					{
						filters.add(RowFilter.regexFilter(aParty, 1));
					}
					if(!aProvince.equals(PROVINCE_DEFAULT))
					{						
						filters.add(RowFilter.regexFilter(aProvince, 3));
					}
					if(!filters.isEmpty())
					{
						RowFilter<Object, Object> provPartFilter = RowFilter.andFilter(filters);
						TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(aMPTable.getModel());
				        sorter.setRowFilter(provPartFilter);
				        aMPTable.setRowSorter(sorter);
					}
					else
					{
						TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(aMPTable.getModel());
				        aMPTable.setRowSorter(sorter);
					}
				}
			 }    
		});
		
		lProvFilterBox.addItemListener( new ItemListener()
		{
			public void itemStateChanged(ItemEvent pEvent) 
			{
				if (pEvent.getStateChange() == ItemEvent.SELECTED) 
				{
					String aProvince = (String)pEvent.getItem();
					String aParty = (String)lPartyFilterBox.getSelectedItem();
					List<RowFilter<Object, Object>> filters = new ArrayList<RowFilter<Object, Object>>(2);
					if(!aParty.equals(PARTY_DEFAULT))
					{
						filters.add(RowFilter.regexFilter(aParty, 1));
					}
					if(!aProvince.equals(PROVINCE_DEFAULT))
					{						
						filters.add(RowFilter.regexFilter(aProvince, 3));
					}
					if(!filters.isEmpty())
					{
						RowFilter<Object, Object> provPartFilter = RowFilter.andFilter(filters);
						TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(aMPTable.getModel());
				        sorter.setRowFilter(provPartFilter);
				        aMPTable.setRowSorter(sorter);
					}
					else
					{
						TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(aMPTable.getModel());
				        aMPTable.setRowSorter(sorter);
					}
				}
			 }    
		});
	}

	private void initilizeProfileView(final JTabbedPane pTabbedPane)
	{
		JPanel aProfileView = new JPanel();
		pTabbedPane.addTab(Messages.getString("CaponeUI.Profile"), null, aProfileView, null); //$NON-NLS-1$
		aProfileView.setLayout(new GridLayout(1, 0, 0, 0));
		
		JPanel profilePanel = new JPanel();
		aProfileView.add(profilePanel);
		profilePanel.setLayout(new BorderLayout(0, 0));
		
		JSplitPane profSplitPane = new JSplitPane();
		profilePanel.add(profSplitPane);
		profSplitPane.setResizeWeight(.5);
		
		final JScrollPane lScrollPane = new JScrollPane();
		profSplitPane.setLeftComponent(lScrollPane);
		
		aProMPTable = new JTable(new UserMPTableModel());
		aProMPTable.setFillsViewportHeight(true);
		lScrollPane.setViewportView(aProMPTable);
		
		JPanel rPanel = new JPanel();
		profSplitPane.setRightComponent(rPanel);
		rPanel.setLayout(new BorderLayout(0, 0));
		
		JScrollPane rScrollPane = new JScrollPane();
		rPanel.add(rScrollPane, BorderLayout.CENTER);
		
		final JTable lKwTable = new JTable(new KwTableModel());
		lKwTable.setFillsViewportHeight(true);
		rScrollPane.setViewportView(lKwTable);
		lKwTable.addMouseListener(new MouseInputAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent pEvent) 
			{
	           int aRow = lKwTable.rowAtPoint(pEvent.getPoint());
	           aRow = lKwTable.convertRowIndexToModel(aRow);
	           int aCol = lKwTable.columnAtPoint(pEvent.getPoint());
	           aCol = lKwTable.convertColumnIndexToModel(aCol);
	           if(aCol == 1 && aRow > -1)
	           {
	        	   String keyword = (String) lKwTable.getModel().getValueAt(aRow, 0);
	        	   for(UserProfile aUser : Capone.getInstance().getUsers())
	        	   {
	        		   aUser.removeKeyword(keyword);
	        	   }
	       		   updateMPTable();
	       		   updateProMPTable();
	       		   lKwTable.setModel(new KwTableModel());
	       		   lKwTable.repaint();
	       		   updateRecTable(Capone.getInstance().getProperties().getaCurrentRecommender());
	        	   }
	           }
		});
		
		aKwInput = new JTextField();
		aKwInput.setText(Messages.getString("CaponeUI.KeywordAdd")); //$NON-NLS-1$
		rPanel.add(aKwInput, BorderLayout.NORTH);
		aKwInput.setColumns(10);
		
		aKwInput.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent pEvent) 
            {
            	if(pEvent.getKeyCode() == KeyEvent.VK_ENTER)
            	{
            		String s = aKwInput.getText();
            		if(s == null || s.equals(Messages.getString("CaponeUI.25"))) //$NON-NLS-1$
            		{
            			return;
            		}
            		for(UserProfile aUser : Capone.getInstance().getUsers())
            		{
            			aUser.addKeyword(s);
            		}
            		updateRecTable(Capone.getInstance().getProperties().getaCurrentRecommender());
            		aKwInput.setText(Messages.getString("CaponeUI.26")); //$NON-NLS-1$
            		lKwTable.setModel(new KwTableModel());
 	       		    lKwTable.repaint();
            	}
                
            }
        });
		
		aProMPTable.addMouseListener(new MouseInputAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent pEvent) 
			{
	           int aRow = aProMPTable.rowAtPoint(pEvent.getPoint());
	           aRow = aProMPTable.convertRowIndexToModel(aRow);
	           int aCol = aProMPTable.columnAtPoint(pEvent.getPoint());
	           aCol = aProMPTable.convertColumnIndexToModel(aCol);
	           if(aCol == 4 && aRow > -1)
	           {
	        	   MP aMP = (MP)aProMPTable.getModel().getValueAt(aRow, 5);
	        	   if((boolean)aProMPTable.getModel().getValueAt(aRow, aCol) == true)
	        	   {
	        		   for(UserProfile aUser : Capone.getInstance().getUsers())
	        		   {
	        			   aUser.removeMP(aMP.getPrimaryKey());
	        		   }
	        		   updateMPTable();
	        		   updateProMPTable();
	        		   updateRecTable(Capone.getInstance().getProperties().getaCurrentRecommender());
	        	   }
	           }
		}
	});
	}

	private void initilizeRecommenderView(final JTabbedPane pTabbedPane)
	{
		final JPanel lRecommenderView = new JPanel();
		pTabbedPane.addTab(Messages.getString("CaponeUI.Recommender"), null, lRecommenderView, null); //$NON-NLS-1$
		lRecommenderView.setLayout(new BorderLayout(0, 0));
		
		JSplitPane aRecSplitPane = new JSplitPane();
		aRecSplitPane.setAlignmentX(Component.CENTER_ALIGNMENT);
		aRecSplitPane.setAlignmentY(Component.CENTER_ALIGNMENT);
		aRecSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		lRecommenderView.add(aRecSplitPane, BorderLayout.CENTER);
		aRecSplitPane.setResizeWeight(0.5);
		JScrollPane topScrollPane = new JScrollPane();
		aRecSplitPane.setLeftComponent(topScrollPane);
		
		aSpeechTable = new JTable(new SpeechTableModel());
		aSpeechTable.setShowVerticalLines(false);
		aSpeechTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		topScrollPane.setViewportView(aSpeechTable);
		
		JScrollPane bottomScrollPane = new JScrollPane();
		bottomScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		aRecSplitPane.setRightComponent(bottomScrollPane);
		
		final JTextArea lSpeechArea = new JTextArea();
		lSpeechArea.setLineWrap(true);
		lSpeechArea.setWrapStyleWord(true);
		lSpeechArea.setEditable(false);
		bottomScrollPane.setViewportView(lSpeechArea);
		
		Box buttonsBox = Box.createVerticalBox();
		buttonsBox.setBackground(Color.WHITE);
		bottomScrollPane.setRowHeaderView(buttonsBox);
		
		JButton readButton = new JButton(Messages.getString("CaponeUI.MarkAsRead")); //$NON-NLS-1$
		readButton.setFont(readButton.getFont().deriveFont(readButton.getFont().getStyle() | Font.BOLD));
		buttonsBox.add(readButton);
		
		readButton.addActionListener(new ActionListener() {			 
			@Override
			public void actionPerformed(ActionEvent pEvent)
			{
				if(aSpeechTable.getSelectedRow() == -1)
				{
					return;
				}
				else
				{
					Speech aSpeech = (Speech) aSpeechTable.getModel().getValueAt(aSpeechTable.getSelectedRow(), 5);
					for(UserProfile aUser : Capone.getInstance().getUsers())
					{
						aUser.addReadSpeech(aSpeech);
					}
					updateRecTable(Capone.getInstance().getProperties().getaCurrentRecommender());
				}
			}
        }); 
		
		
		JButton likeButton = new JButton(Messages.getString("CaponeUI.Like")); //$NON-NLS-1$
		likeButton.setFont(likeButton.getFont().deriveFont(likeButton.getFont().getStyle() | Font.BOLD));
		likeButton.setForeground(Color.BLACK);
		likeButton.setBackground(Color.GREEN);
		buttonsBox.add(likeButton);
		
		likeButton.addActionListener(new ActionListener() {			 
			@Override
			public void actionPerformed(ActionEvent pEvent)
			{
				if(aSpeechTable.getSelectedRow() == -1)
				{
					return;
				}
				else
				{
					Speech aSpeech = (Speech) aSpeechTable.getModel().getValueAt(aSpeechTable.getSelectedRow(), 5);
					aSpeech.setThumbs(5);
					if(Capone.getInstance().getProperties().getaCurrentRecommender() == recommenderType.VOTE)
					{
						updateRecTable(Capone.getInstance().getProperties().getaCurrentRecommender());
					}
				}
			}
        });
		
		JButton dislikeButton = new JButton(Messages.getString("CaponeUI.Dislike")); //$NON-NLS-1$
		dislikeButton.setFont(dislikeButton.getFont().deriveFont(dislikeButton.getFont().getStyle() | Font.BOLD));
		dislikeButton.setForeground(Color.BLACK);
		dislikeButton.setBackground(Color.RED);
		buttonsBox.add(dislikeButton);
		
		dislikeButton.addActionListener(new ActionListener() {			 
			@Override
			public void actionPerformed(ActionEvent pEvent)
			{
				if(aSpeechTable.getSelectedRow() == -1)
				{
					return;
				}
				else
				{
					Speech aSpeech = (Speech) aSpeechTable.getModel().getValueAt(aSpeechTable.getSelectedRow(), 5);
					aSpeech.setThumbs(-5);
					if(Capone.getInstance().getProperties().getaCurrentRecommender() == recommenderType.VOTE)
					{
						updateRecTable(Capone.getInstance().getProperties().getaCurrentRecommender());
					}
				}
			}
        });
		
		final JComboBox<recommenderType> lRecTypeCombo = new JComboBox<recommenderType>();
		lRecTypeCombo.setToolTipText(Messages.getString("CaponeUI.31")); //$NON-NLS-1$
		lRecommenderView.add(lRecTypeCombo, BorderLayout.SOUTH);
		lRecTypeCombo.setModel(new DefaultComboBoxModel<recommenderType>(recommenderType.values()));
		lRecTypeCombo.setSelectedIndex(Capone.getInstance().getProperties().getaCurrentRecommender().ordinal());
        lRecTypeCombo.getModel().setSelectedItem(Messages.getString("CaponeUI.RecommenderType1") + (String)lRecTypeCombo.getModel().getSelectedItem().toString()); //$NON-NLS-1$
		lRecTypeCombo.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent pEvent) 
			{
			       if (pEvent.getStateChange() == ItemEvent.SELECTED && pEvent.getItem().getClass() == recommenderType.class) {
			         recommenderType aRec = (recommenderType) pEvent.getItem();
			          //for(UserProfile aUser : Capone.getInstance().getUsers())
			          //{
			        	 // Capone.getInstance().getUsers().get(0).updateRecommender(aRec);
			          //}
			         updateRecTable(aRec);
			         lRecTypeCombo.getModel().setSelectedItem(Messages.getString("CaponeUI.RecommenderType2") + (String)lRecTypeCombo.getModel().getSelectedItem().toString()); //$NON-NLS-1$
			       }
			    }      
			});
		
		aSpeechTable.getSelectionModel().addListSelectionListener( new ListSelectionListener()
		{
	        public void valueChanged(ListSelectionEvent pEvent) 
	        {
	        	if(aSpeechTable.getSelectedRow() != -1 && aSpeechTable.getSelectedColumn() != -1)
	        	{
	        		Speech aSpeech;
	        		aSpeech = (Speech) aSpeechTable.getModel().getValueAt(aSpeechTable.getSelectedRow(), 5);
	 	            lSpeechArea.setText(aSpeech.getContentWithoutHTML());
	        	}
	           return;
	        }
		});
	}
	
	private void updateRecTable(recommenderType pRec)
	{
		if(pRec == recommenderType.SIMILARITY)
		{
			JOptionPane.showMessageDialog(null, Messages.getString("CaponeUI.UpdateingRecs")); //$NON-NLS-1$
		}
		Capone.getInstance().getProperties().setaCurrentRecommender(pRec);
		for(UserProfile aUser : Capone.getInstance().getUsers())
		{
			aUser.updateRecommender(pRec);
		}
		aSpeechTable.setModel(new SpeechTableModel());
		aSpeechTable.repaint();
	}

	private void updateProMPTable()
	{
		aProMPTable.setModel(new UserMPTableModel());
		aProMPTable.repaint();
	}

	private void updateMPTable()
	{
		RowSorter<? extends TableModel> sorter = aMPTable.getRowSorter();
		aMPTable.setModel(new MPTableModel());
		aMPTable.repaint();
		aMPTable.setRowSorter(sorter);
	} 

}
