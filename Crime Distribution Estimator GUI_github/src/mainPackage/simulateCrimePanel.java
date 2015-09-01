package mainPackage;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JSpinner;
import javax.swing.JTextPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.kenai.jaffl.annotations.Clear;

import misellanies.cookies;
import myPanel.myComponent;


import CrimeCase.crimeCase;
import PlotTools.Polygon;
import PlotTools.RpaAggregate;
import PlotTools.gausianMixture;

public class simulateCrimePanel extends myComponent{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JButton simulateB;
	private gausianMixture myModel;
	private int[] precinctCount;
	private cookies myCookies;
	private ArrayList<crimeCase> crimeCases;
	
	public simulateCrimePanel(gausianMixture model,ArrayList<Polygon> precincts, RpaAggregate rpaAggregate,webView myWebView) throws FileNotFoundException{
		super(myWebView,precincts,rpaAggregate);
		myCookies=cookies.getInstance();
		this.precinctCount=new int[precincts.size()];
		this.myModel=model;
		crimeCases=new ArrayList<crimeCase>();
		addUI();
	}
	
	
	//Helper function to add UI component into the current panel
	private void addUI(){
		simulateB=new JButton("simulate");
		simulateB.addActionListener(simuActionListener(this));
		this.add(simulateB,"wrap");
		JTextPane textPane=new JTextPane();
		textPane.setFont(new Font("Serif", Font.BOLD, 18));
		textPane.setText("Input the number of crimes to be simulated at the same time");
		SpinnerNumberModel spinnerModel=new SpinnerNumberModel(10, 1, 100, 1);
		JSpinner spinner=new JSpinner(spinnerModel);
		spinner.setFont(new Font("Serif", Font.BOLD, 18));
		spinner.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				// TODO Auto-generated method stub
				myCookies.put(cookies.predictNumber, spinner.getValue());
			}
		});
		
		this.add(textPane);
		this.add(spinner,"wrap");
	}
	
	/**
	 * Simulate action listener
	 * @param panel for information about the simulate crimes to show on.
	 * @return ActionListener
	 */
	private ActionListener simuActionListener(myComponent panel){
		ActionListener resActionListener=new ActionListener(){
			 @Override
			 public void actionPerformed(ActionEvent e)
	            {
				 	int numberSize=Integer.parseInt(myCookies.getString("predictNumber"));
				 	ArrayList<crimeCase> caseList=myModel.generateCrimes(numberSize);
				 	rpaAggregate.addCrimeList(caseList, myWebView);
				 	for(int i=0;i<numberSize;i++){
				 		crimeCase simu=caseList.get(i);
					 	simu.setPrecinct(precincts);
				 		for(int j=0;j<precincts.size();j++){
				 			if(precincts.get(j).name.equals(simu.getPrecint())){
				 				precinctCount[j]++;
				 			}
				 		}
					 	if(!simu.getPrecint().equals("unknown")){
							crimeCases.add(simu);
					 		myWebView.addPoint(simu);
					 	}
				 	}
				 	panel.removeAllText();
				 	panel.addText("Total simulated crime: "+crimeCases.size());
				 	myWebView.setFocus(crimeDist_init.getFocus());
				 	for(int i=0;i<precincts.size();i++){
						panel.addText("Total crime on precinct "+precincts.get(i).name+": "+precinctCount[i]);	
					}
	            }
		};
		return resActionListener;
	}
	
	//Refresh the state of the simulate crime panel so that current crimecases are all deleted and crime count start again.
	public void clear(){
		precinctCount=new int[precincts.size()];
		crimeCases.clear();
	}
	
}
