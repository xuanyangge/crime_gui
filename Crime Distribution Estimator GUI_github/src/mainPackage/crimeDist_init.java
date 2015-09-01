package mainPackage;

import java.awt.EventQueue;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JScrollPane;
import javax.swing.JInternalFrame;
import javax.swing.JToolBar;
import javax.swing.JComponent;
import javax.swing.SpinnerNumberModel;
import javax.swing.UnsupportedLookAndFeelException;

import java.awt.Panel;

import javax.swing.JSlider;
import javax.swing.JEditorPane;
import javax.swing.JTextPane;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;
import java.awt.Color;

import javax.swing.UIManager;

import java.awt.CardLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.SystemColor;
import java.awt.Component;
import java.awt.Window;

import javax.swing.Box;

import java.awt.Dimension;

import javax.swing.SwingConstants;

import net.miginfocom.swing.MigLayout;

import javax.swing.JTree;
import javax.swing.JScrollBar;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//added since proj shared w/ Xuanyang 
import org.jdatepicker.JDatePicker;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.ml.EM;
import org.python.antlr.PythonParser.else_clause_return;
import org.python.antlr.ast.boolopType;

import com.kenai.jaffl.annotations.Clear;

import java.awt.print.*;

import javax.print.*;

import myPanel.*;
import misellanies.*;
import PlotTools.*;
import CrimeCase.*;



/**
 * @author gexuanyang, dumenye
 *
 */
public class crimeDist_init {
	private final static int BWidth=(int)Math.round(java.awt.Toolkit.getDefaultToolkit().getScreenSize().width*2.0/3);
	private final static int BHeight=(int)Math.round(java.awt.Toolkit.getDefaultToolkit().getScreenSize().height*2.0/3);
	public final static int maxCaseDisplay=800;
	private final static int maxPrecinct=20;
	public final static int dayInMili=8640000;
	
	//WebView object for controlling and executing javascript using java
	protected static webView myWebView;
	
	//UI Components
	private JFrame crimeDistEstimJFrame;
	private JFXPanel myPanel;
	private JTabbedPane tabbedPane;
	private JButton toggleRpaButton;
	private JButton toggleMarkerB;
	private JButton togglePrecinctButton;
	private JComboBox<String> precinctChooser=new JComboBox<String>();
	private pastCrimePanel panel1;
	private simulateCrimePanel panel2;
	private HeatMapPanel	panel3;
	private temporalCrimePanel panel4;
	private StatisticsPanel panel5;
    static Scene browserScene;  
    protected static ArrayList<Polygon> precincts;
    
	
	// Common dialog messages/titles
	String saveMessage = "Select file location for browser save.";
	String inputErrorMessage = "The input you gave is invalid. Try again?";
	
	private static int nClusters;
	//cookies
	private cookies myCookies=cookies.getInstance();
	
	//Map of past crime cases
	protected static TreeMap<Long, crimeCase> map;
	//Rpa data
	protected static RpaAggregate rpaAggregate;
	//Gaussian Mixture model for predicting data.
	protected static gausianMixture myModel;
	
	private saveMap mySaveMap = new saveMap();
	private static int focus=-1;
	//State
	private boolean enableRpa=false; 
	private boolean enableMarker=true;
	
	
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					crimeDist_init window = new crimeDist_init();
					window.crimeDistEstimJFrame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	
	public JFrame getFrame(){
		return crimeDistEstimJFrame;
	}
	
	
	public void addComponents(){
		crimeDistEstimJFrame.getContentPane().add(myPanel,"cell 0 0 4 3");
		crimeDistEstimJFrame.getContentPane().add(tabbedPane,"span 3,growy 1000, wrap");
		crimeDistEstimJFrame.getContentPane().add(toggleMarkerB, "cell 0 4");
		crimeDistEstimJFrame.getContentPane().add(togglePrecinctButton,"cell 0 5");
		precinctChooser.setVisible(true);
		crimeDistEstimJFrame.getContentPane().add(precinctChooser,"cell 1 5");
		crimeDistEstimJFrame.getContentPane().add(toggleRpaButton,"cell 0 4");
	}
	
	public webView getWebView(){
		return myWebView;
	}
	
	
	//Get dimension of the screen
	public static Dimension getDimen(){
		return new Dimension(BWidth,BHeight);
	}
	
	
	public static int getFocus(){
		return focus;
	}

	/**
	 * Create the application.
	 * @throws IOException 
	 * @throws InvalidFormatException 
	 */
	private crimeDist_init() throws InvalidFormatException, IOException /* the constructor */ {
		initialize();
	}
	
	//Initialize frame
	private void addFrame(){
		crimeDistEstimJFrame = new JFrame();
		crimeDistEstimJFrame.getContentPane().setBackground(SystemColor.control);
		crimeDistEstimJFrame.setBackground(UIManager.getColor("Button.disabledForeground"));
		crimeDistEstimJFrame.setTitle("Crime Distribution Estimator");
		crimeDistEstimJFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		crimeDistEstimJFrame.setMinimumSize(new Dimension(800,600));
		crimeDistEstimJFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		crimeDistEstimJFrame.getContentPane().setLayout(new MigLayout("", "[][][][][]", "[477.00][-16.00][-30.00][86.00][][28.00][]"));
	}
	
	
	//Add JFXPanel
	private void addPanel(){
		//Add a browser to the application
		myPanel=new JFXPanel();
		Platform.runLater(new Runnable(){
			@Override
            public void run() {
				myWebView=new webView("/resources/map.html");
				Scene browserScene=new Scene(myWebView);
				myPanel.setScene(browserScene);
            }
		});
	}
	
	//Add a button to toggle rpa visibility. This will affects the behavior of some buttons.
	//For example past crimes will change the color of rpas if rpas are turned on. 
	private void addToggleRpa(){
		toggleRpaButton=new JButton("Toggle rpa");
		toggleRpaButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(enableRpa){
					enableRpa=false;
					myWebView.hideRpa();
					myWebView.hideOverMap();
				}else{
					enableRpa=true;
					if(tabbedPane.getSelectedIndex()==4){
						myWebView.showOverMap2();
					}else{
						myWebView.showOverMap1();
					}
				}
				myWebView.changeStrategy(enableMarker,enableRpa);
				myWebView.setFocus(focus);
			}
		});
	}
	
	

	//Add precinct selector
	private void addComboBox(){
		precinctChooser.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String choice=(String) precinctChooser.getSelectedItem();
				int i;
				for(i=0;i<precincts.size();i++){
					if(choice.equals(precincts.get(i).name)){
						focus=i;
						break;
					}
				}
				if(i==precincts.size()){
					focus=-1;
				}
				myWebView.setFocus(focus);
			}
		});
	}
	
	//Add all the tabbed panes.
	//I seperate each pane into a different class to make the code cleaner.
	//This however causes a lot of parameters to be passed. 
	private void addTabPane() throws InvalidFormatException, IOException{
		tabbedPane = new JTabbedPane();
		tabbedPane.setMinimumSize(new Dimension(0,500));
		panel1=new pastCrimePanel(myWebView, map, precincts, rpaAggregate, crimeDistEstimJFrame);
		tabbedPane.addTab("Past Crime", panel1);
		panel2=new simulateCrimePanel(myModel, precincts, rpaAggregate, myWebView);
		tabbedPane.addTab("Simulated Crime", panel2);
		panel3=new HeatMapPanel(myWebView, precincts, rpaAggregate, myModel);
		tabbedPane.addTab("Spacial Crime Density", panel3);
		panel4=new temporalCrimePanel(myWebView, precincts, rpaAggregate, myModel, crimeDistEstimJFrame, map);
		tabbedPane.addTab("Temporal Crime Density", panel4);
		panel5=new StatisticsPanel(map, precincts, rpaAggregate, myWebView, crimeDistEstimJFrame);
		tabbedPane.addTab("Stastics", panel5);
		tabbedPane.addChangeListener(new ChangeListener() {
			int prevState=-1;
			
			@Override
			public void stateChanged(ChangeEvent e) {
				// TODO Auto-generated method stub
				clear();
				if(enableRpa){
					if(tabbedPane.getSelectedIndex()==4){
						myWebView.showOverMap2();
					}else{
						myWebView.showOverMap1();
					}
				}
				if(tabbedPane.getSelectedIndex()==3||tabbedPane.getSelectedIndex()==2){
					if(prevState!=3&&prevState!=2){
						myWebView.addRpaListeners();
					}
				}else if(prevState==3||prevState==2){
					myWebView.clearRpaListeners();
				}
				prevState=tabbedPane.getSelectedIndex();
			}
		});
	}
	
	//Add Menu bar made by dumenye
	private void addMenu(){
		// Adding the menu bar and options
		JMenuBar menuBar = new JMenuBar();
		crimeDistEstimJFrame.setJMenuBar(menuBar);
		
		JMenu mnNewMenu = new JMenu("File");
		menuBar.add(mnNewMenu);
		
		// Sub-menus for "File"
		JMenuItem mntmNew = new JMenuItem("New");
		mnNewMenu.add(mntmNew);
		JMenuItem mntmEdit = new JMenuItem("Edit");
		mnNewMenu.add(mntmEdit);
		JMenuItem mntmSave = new JMenuItem("Save");
		mnNewMenu.add(mntmSave);
		mntmSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Object delayMessage = "How long should the program delay before capturing the browser? Please enter only an integer value.";
				String name = new String();
				int qResult;
						JFileChooser fcSAV = new JFileChooser();
						fcSAV.setDialogTitle(saveMessage);
						FileFilter pngFil = new FileNameExtensionFilter("PNG Image","png");
						FileFilter bmpFil = new FileNameExtensionFilter("BMP Image","bmp");
						FileFilter jpgFil = new FileNameExtensionFilter("JPG Image","jpg");
						FileFilter selectedFil = new FileNameExtensionFilter("PNG Image","png"); /* sets default file type to PNG */
						String pngExt = ".png";
						String bmpExt = ".bmp";
						String jpgExt = ".jpg";
						fcSAV.setFileFilter(pngFil);
						fcSAV.addChoosableFileFilter(bmpFil);
						fcSAV.addChoosableFileFilter(jpgFil);
						fcSAV.showSaveDialog(crimeDistEstimJFrame);
						//File file = new File(saveMessage);
						File file = fcSAV.getSelectedFile();
						File browSave = new File(name);
						name = file.getPath();
						int pos = name.lastIndexOf(".");
						if (pos > 0) {
							name = name.substring(0, pos);
						}
						if(fcSAV.getFileFilter() == pngFil) {
							browSave = new File(name + pngExt);
							selectedFil = pngFil;
						}else if(fcSAV.getFileFilter() == bmpFil){
							browSave = new File(name + bmpExt);
							selectedFil = bmpFil;
						}else /* fcSAV.getFileFilter() then MUST = jpgFil */{
							browSave = new File(name + jpgExt);
							selectedFil = jpgFil;
						}
						System.out.println(browSave.toString());
						//qResult = Integer.parseInt(JOptionPane.showInputDialog(crimeDistEstimJFrame, delayMessage));
						//if (qResult) {
						//	
						//}
						mySaveMap.saveBrow(browSave, selectedFil, crimeDistEstimJFrame, menuBar, browserScene);
						
/*				}catch (NumberFormatException e){
					while(e != null) {
						int ieResult = JOptionPane.showOptionDialog(crimeDistEstimJFrame, inputErrorMessage, "Input Error",JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, null, null);
						if (ieResult == JOptionPane.YES_OPTION) {
							qResult = Integer.parseInt(JOptionPane.showInputDialog(delayMessage));
							mySaveMap.saveBrow(qResult, name);
						}
					}	
				}
*/				
			}	
		}); 
		
		
		JMenuItem mntmSaveAs = new JMenuItem("Save As");
		mnNewMenu.add(mntmSaveAs);
		JSeparator separator = new JSeparator();
		mnNewMenu.add(separator);
		JMenuItem mntmRefresh = new JMenuItem("Refresh");
		mnNewMenu.add(mntmRefresh);	
		JSeparator separator_1 = new JSeparator();
		mnNewMenu.add(separator_1);
		/* Modified Print Function */
		/* Modified Print Function */
		JMenuItem mntmPrint = new JMenuItem("Print");
		mnNewMenu.add(mntmPrint);
		mntmPrint.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
			//	/* must take out later */ JOptionPane.showMessageDialog(crimeDistEstimJFrame, tpSize);
				//printSetup.prelim();
				//printSetup prinTing = new printSetup();
			/*	try {
					prinTing.print(tpSize, null, fullIndex);
				} catch (PrinterException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			*/	
			Platform.runLater(new Runnable(){
					@Override
		            public void run() {
						myWebView.printBrow(browserScene);
		            }
				});
			}
		}); 
		
		JSeparator separator_2 = new JSeparator();
		mnNewMenu.add(separator_2);
		JMenuItem mntmImport = new JMenuItem("Import");
		mntmImport.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				String importMessage = "Select crime data to import.";
				final JFileChooser fcIMP = new JFileChooser();
				fcIMP.setDialogTitle(importMessage);
			}
		});
		mnNewMenu.add(mntmImport);	
		JMenuItem mntmExport = new JMenuItem("Export");
		mntmExport.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String exportMessage = "Select file location for crime data.";
				final JFileChooser fcEXP = new JFileChooser();
				fcEXP.setDialogTitle(exportMessage);
			}
		});
		mnNewMenu.add(mntmExport);
		
		// Sub-menus for "Edit"
		JMenu mnEdit = new JMenu("Edit");
		menuBar.add(mnEdit);
		
		// Sub-menus for "Statistics"
		JMenu mnStatistics = new JMenu("Statistics");
		menuBar.add(mnStatistics);
		
		// Sub-menus for "Spatial"
		JMenu mnSpatialCrime = new JMenu("Spatial");
		menuBar.add(mnSpatialCrime);
		
		// Sub-menus for "Temporal"
		JMenu mnTemporalCrime = new JMenu("Temporal");
		menuBar.add(mnTemporalCrime);
		
		// Sub-menus for "Search"
		JMenu mnSearch = new JMenu("Search");
		menuBar.add(mnSearch);
		
		// Sub-menus for "Help"
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		

		JMenuItem setting=new JMenuItem("Setting");
		mnNewMenu.add(setting);
		setting.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				//settDialog.setOptions(settDialog.YES_NO_OPTION, new JTextBox line1(""),new JLabel);
				 String number = JOptionPane.showInputDialog(crimeDistEstimJFrame,"How many crime cases do you want to simulate at the same time?", null);
				 if(number != null) {
					 myCookies.put("predictNumber", number);
				 }
			}
		});
		setting.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				 String number = JOptionPane.showInputDialog(crimeDistEstimJFrame,"How many crime cases do you want at the same time?", null);
			}
		});
	}
	
	//Toggle marker visibility
	private void addToggleMarker(){
		toggleMarkerB=new JButton("Toggle marker");
		toggleMarkerB.addActionListener(new ActionListener(){
			 public void actionPerformed(ActionEvent e)
	            {
					if(enableMarker){
						enableMarker=false;
						myWebView.hideMarker();
					}else{
						enableMarker=true;
					}
					myWebView.changeStrategy(enableMarker,enableRpa);
					myWebView.setFocus(focus);
	            }
		});
	}

	//Used in transition from one tab to anothr
	public void clear(){
		myWebView.clearMarker();
	 	myWebView.clearHeatMap();
	 	myWebView.clearHotSpot();
	 	myWebView.hideOverMap();
	 	rpaAggregate.clearCrime(myWebView);
	 	panel1.removeAllText();
	 	panel2.removeAllText();
	 	panel3.removeAllText();
	 	panel4.removeAllText();
	 	panel4.removeChartPanel();
	 	panel5.removeChartPanel();
	 	panel2.clear();
	}
	
	private void addPrecinct() throws FileNotFoundException{
		precinctChooser.addItem("All Precincts");
		for(int i=0;i<precincts.size();i++){
			precinctChooser.addItem(precincts.get(i).name);
		}
		togglePrecinctButton=new JButton("Toggle Precinct visibility");
		togglePrecinctButton.addActionListener(new ActionListener(){
			private boolean visible=false; 
			public void actionPerformed(ActionEvent e)
	            {	
					if(!visible){
						visible=true;
						myWebView.showPrecinct();
						myWebView.setFocus(focus);
					}else{
						visible=false;
						myWebView.hidePrecinct();
						focus=-1;
					}
	            }
		});
	}
	
	//Initialize all the data
	private void initializeData() throws FileNotFoundException{
		nClusters=Integer.parseInt(cookies.getInstance().getString("nClusters"));
		map= pastCrime.burglaryCases("burglarywithprecinct.txt");
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat sample=new Mat(map.size(),2,CvType.CV_64F);
		int count=0;
		
		//Put crimes into the Matrix for trainning
		for(crimeCase c:map.values()){
			sample.put(count, 0, c.getLa());
			sample.put(count++, 1, c.getlo());
		}
		myModel=new gausianMixture(sample,nClusters,1000,"burgulary");
		precincts=kmlParser.getPrecinct();
		rpaAggregate=new RpaAggregate(kmlParser.getRPAs());
	}
	
	
	private void addLookAndFeel(){

		try {
            // Set System L&F
        UIManager.setLookAndFeel(
            UIManager.getSystemLookAndFeelClassName());
    } 
    catch (UnsupportedLookAndFeelException e) {
       // handle exception
    	e.printStackTrace();
    }
    catch (ClassNotFoundException e) {
       // handle exception
    	e.printStackTrace();
    }
    catch (InstantiationException e) {
       // handle exception
    	e.printStackTrace();
    }
    catch (IllegalAccessException e) {
       // handle exception
    	e.printStackTrace();
    }
	}
	
	/**
	 * Initialize the contents of the frame.
	 * @throws IOException 
	 * @throws InvalidFormatException 
	 * 
	 */
	private void initialize() throws InvalidFormatException, IOException {
		addPanel();
		initializeData();
		addLookAndFeel();
		addFrame();
		addMenu();
		addToggleMarker();
		addToggleRpa();
		addPrecinct();
		addComboBox();
		addTabPane();
		myWebView.initializePrecinct(precincts);
		myWebView.initializeRpas(rpaAggregate.rpaPolygons);
		addComponents();
		crimeDistEstimJFrame.pack();
	}
}
