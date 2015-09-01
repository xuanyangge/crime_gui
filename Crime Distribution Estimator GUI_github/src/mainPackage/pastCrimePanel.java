package mainPackage;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jdatepicker.impl.JDatePickerImpl;

import myPanel.datePanel;
import myPanel.myComponent;
import CrimeCase.crimeCase;
import PlotTools.Polygon;
import PlotTools.RpaAggregate;
import PlotTools.rpaPolygon;

public class pastCrimePanel extends myComponent{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TreeMap<Long, crimeCase> map;
	private SortedMap<Long,crimeCase> mySubMap=null;
	private JFrame myFrame;
	private int count=0;
	
	//Indirect dependency on crimeDist_init data member. 
	public pastCrimePanel(webView myWebView, TreeMap<Long, crimeCase> map, ArrayList<Polygon> precincts, RpaAggregate rpaAggregate,JFrame crimeDist_initFrame) throws InvalidFormatException, IOException {
		// TODO Auto-generated constructor stub
		super(myWebView,precincts,rpaAggregate);
		this.map=map;
		myFrame=crimeDist_initFrame;
		addUI();
	}
	
	//Helper function to add UI component into the current panel
	private void addUI(){
		datePanel pastCrimePanel=new datePanel();
		pastCrimePanel.addListener(pastCrimeListener(pastCrimePanel.lDate(), pastCrimePanel.rDate()));
		this.add(pastCrimePanel.getPanel(),"wrap");
		JButton monthButton=new JButton("Get last month.");
		monthButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				Date lastMonthDate=new Date(new Date().getTime()-crimeDist_init.dayInMili*30);
				addPastCrime(lastMonthDate,new Date());
			}
		});
		JButton weekButton=new JButton("Get last week.");
		weekButton.addActionListener(new ActionListener() {
		
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				Date lastWeekDate=new Date(new Date().getTime()-crimeDist_init.dayInMili*7);
				addPastCrime(lastWeekDate,new Date());
			}
		});
		this.add(monthButton);
		this.add(weekButton,"cell 1 1, wrap");
	}
	
	
	/**
	 * 
	 * @param dPick date picker for the start date
	 * @param dPick2 date picker for the end date
	 * @return An actionListener.
	 */
	private ActionListener pastCrimeListener(JDatePickerImpl dPick, JDatePickerImpl dPick2){
		ActionListener resActionListener=new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				Date dateL = (Date) dPick.getModel().getValue();
				Date dateR=(Date)dPick2.getModel().getValue();
				if(dateL==null||dateR==null){
					JOptionPane.showMessageDialog(myFrame, "Please enter both dates for range.");
				}else if(dateL.after(dateR)){
					JOptionPane.showMessageDialog(myFrame, "Date on the left must be smaller than the date on the right.");
				}
				else{
					addPastCrime(dateL, dateR);
				}
				
			}
		};
		return resActionListener;
	}
	
	/**
	 * Pick submap of the crimes, then add them to google map UI based on the precinct that is chosen right now.
	 * This method will assign the crime to one of the precinct and rpa for future use.
	 * @param dateL Start date
	 * @param dateR end date
	 */
	private void addPastCrime(Date dateL,Date dateR){
		myWebView.clearMarker();
		removeAllText();
		mySubMap=map.subMap(dateL.getTime(), dateR.getTime());
		int[] precinctCount=new int[precincts.size()];
		count=0;
		for(crimeCase c:mySubMap.values()){
			//c.print();
			if(count<=crimeDist_init.maxCaseDisplay){
				myWebView.addPoint(c);
				count++;
			}
			for(int i=0;i<precincts.size();i++){
				if(c.getPrecint().equals(precincts.get(i).name)){
					precinctCount[i]++;
				}
			}
		}
		myWebView.setFocus(crimeDist_init.getFocus());
		rpaAggregate.addCrimeList(mySubMap, myWebView);
		this.addText("Total crime:"+mySubMap.size());
		if(count==801){
			this.addText("Total crime on the map: "+count+". (Equal to max display of crimes.)");	
		}else{
			this.addText("Total crime on the map: "+count+".");
		}
		for(int i=0;i<precincts.size();i++){
			this.addText("Total crime on precinct "+precincts.get(i).name+": "+precinctCount[i]);	
		}
	}
	
}
