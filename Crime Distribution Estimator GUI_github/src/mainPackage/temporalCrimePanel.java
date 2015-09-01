package mainPackage;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import javafx.application.Platform;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.jdatepicker.impl.JDatePickerImpl;

import myPanel.datePanel;
import myPanel.myComponent;
import CrimeCase.crimeCase;
import PlotTools.Polygon;
import PlotTools.RpaAggregate;
import PlotTools.WebPolygon;
import PlotTools.gausianMixture;
import PlotTools.plotTemporal;

public class temporalCrimePanel extends myComponent {
	private gausianMixture myModel;
	private JFrame myFrame;
	private TreeMap<Long, crimeCase> myMap;
	private JPanel chartPanel;
	private temporalCrimePanel that=this;
	
	public temporalCrimePanel(webView myWebView,ArrayList<Polygon> precincts, RpaAggregate rpaAggregate, gausianMixture myModel,JFrame Frame,TreeMap<Long, crimeCase> map){
		super(myWebView,precincts,rpaAggregate);
		chartPanel=null;
		this.myModel=myModel;
		this.myFrame=Frame;
		this.myMap=map;
		datePanel temporalPanel=new datePanel();
		temporalPanel.addListener(temporalCrimeListener(temporalPanel.lDate(), temporalPanel.rDate()));
		this.add(temporalPanel.getPanel());
	}
	
	/**
	 * Generates a chart based on the time the user choose
	 * @param dPick Date picker UI component
	 * @param dPick2 Date picker UI component
	 * @return
	 */
	private ActionListener temporalCrimeListener(JDatePickerImpl dPick, JDatePickerImpl dPick2){
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
					WebPolygon webPolygon=new WebPolygon(myWebView.webEngine,precincts);
					Platform.runLater(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							plotTemporal plot=new plotTemporal(webPolygon,myModel,precincts,myMap);
							if(chartPanel!=null)that.remove(chartPanel);
							chartPanel=plot.plot(dateL, dateR,true,crimeDist_init.getFocus());
							that.add(chartPanel,"cell 0 1 4 4 ");
							chartPanel.setVisible(true);
							myFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
							myFrame.pack();
						}
					});
				}
				
			}
		};
		return resActionListener;
	}
	
	public JPanel getChartPanel(){
		return chartPanel;
	}
	
	public void removeChartPanel(){
		if(chartPanel!=null){
			this.remove(chartPanel);
		}
	}
}
