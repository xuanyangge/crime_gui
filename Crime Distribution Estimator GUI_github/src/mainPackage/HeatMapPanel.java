package mainPackage;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;

import javafx.application.Platform;

import javax.swing.JButton;

import PlotTools.Polygon;
import PlotTools.RpaAggregate;
import PlotTools.gausianMixture;
import PlotTools.heatMap;
import myPanel.myComponent;

public class HeatMapPanel extends myComponent {
	private JButton heatMapButton;
	private gausianMixture myModel;
	private JButton hotSpotButton;
	
	public HeatMapPanel(webView myWebView, ArrayList<Polygon> precincts, RpaAggregate rpaAggregate,gausianMixture myModel){
		super(myWebView,precincts,rpaAggregate);
		this.myModel=myModel;
		addHeatmMapButton();
		addHotSpotButton();
		this.add(heatMapButton);
		this.add(hotSpotButton,"cell 2 0");
	}
	public void addHeatmMapButton(){
		heatMapButton=new JButton("View heat Map");
		heatMapButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				Platform.runLater(new Runnable(){
					@Override
		            public void run() {
						heatMap myHeatMap=new heatMap(myModel,precincts,myWebView);
						try {
				        	myWebView.clearHeatMap();
				        	//Store jpg file for heat map to use
				        	myHeatMap.predict(myWebView.getBounds(), "target\\classes\\resources\\file.png");
							myWebView.showHeatMap("file.png");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
			}
		});
	}
	private void addHotSpotButton(){
		hotSpotButton=new JButton("View hot spot");
		hotSpotButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				ArrayList<Polygon> polygons=new ArrayList<>();
				for(int i=0;i<myModel.getNClusters();i++){
					ArrayList<Point2D.Double> pointList=myModel.getEllipse(i);
					Polygon p=new Polygon("ellipse",pointList);
					polygons.add(p);
				}
				myWebView.addHeatSpot(polygons);
			}
		});
		//hotSpotButton.setVisible(false);
		//crimeDistEstimJFrame.getContentPane().add(hotSpotButton);
	}
}
