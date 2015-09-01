package mainPackage;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import CrimeCase.crimeCase;
import PlotTools.Polygon;
import PlotTools.RpaAggregate;
import PlotTools.areaIdentifier;
import PlotTools.gausianMixture;
import PlotTools.rpaPolygon;
import myPanel.myComponent;

public class StatisticsPanel extends myComponent{
	private static Date today=new Date();
	private JFrame myFrame;
	private TreeMap<Long, crimeCase> map;
	private ChartPanel chartPanel;
	private JButton lastMonthButton;
	private JButton lastYearMonthButton;
	private JButton lastWeekButton;
	private JButton lastYearButton;
	private static final Map<String ,Long> timeMap;
	static{
		timeMap=new HashMap<String, Long>();
		timeMap.put("Hour",(long) 3600000);
		timeMap.put("Day", (long)86400000);
		timeMap.put("Week", (long)86400000*7);
		timeMap.put("Month",(long)86400000*28);
		timeMap.put("Year", (long)86400000*365);
	}
	public StatisticsPanel(TreeMap<Long, crimeCase> map,ArrayList<Polygon> precincts, RpaAggregate rpaAggregate,webView myWebView,JFrame frame){
		super(myWebView,precincts,rpaAggregate);
		this.map=map;
		chartPanel=null;
		myFrame=frame;
		addComponent();
		
	}
	
	private void addComponent(){
		lastMonthButton=new JButton("Compare this month to last month");
		lastMonthButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				getChartAndRpa(today.getTime()-2*timeMap.get("Month"), today.getTime()-timeMap.get("Month"), "Week", "Month");
			}
		});
		
		lastYearMonthButton=new JButton("Compare this month to last year the same month");
		lastYearMonthButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				getChartAndRpa(today.getTime()-timeMap.get("Year")-timeMap.get("Month"), today.getTime()-timeMap.get("Month"), "Week", "Month");
			}
		});
		lastWeekButton=new JButton("Compare this week to last week");
		lastWeekButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				getChartAndRpa(today.getTime()-2*timeMap.get("Week"), today.getTime()-timeMap.get("Week"), "Day", "Week");
			}
		});
		lastWeekButton=new JButton("Compare this week to last week");
		lastWeekButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				getChartAndRpa(today.getTime()-2*timeMap.get("Week"), today.getTime()-timeMap.get("Week"), "Day", "Week");
			}
		});
		lastYearButton=new JButton("Compare this year to last year");
		lastYearButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				getChartAndRpa(today.getTime()-2*timeMap.get("Year"), today.getTime()-timeMap.get("Year"), "Month", "Year");
			}
		});
		this.add(lastWeekButton);
		this.add(lastMonthButton);
		this.add(lastYearButton);
		this.add(lastYearMonthButton,"wrap");
	}
	
	
	/**
	 * Refresh the rpas and create a chart based on the parameters. 
	 * @param time1 starting time of previous time period in long
	 * @param time2 starting time of current time period in long
	 * @param interval String indicating the time interval for comparison
	 * @param totalT total time interval
	 */
	public void getChartAndRpa(long time1,long time2,String interval,String totalT){
		SortedMap<Long, crimeCase> preT=map.subMap(time1, time1+timeMap.get(totalT));
		SortedMap<Long, crimeCase> curT=map.subMap(time2, time2+timeMap.get(totalT));
        removeChartPanel();
		getPanelTime(preT, curT, time1, time2,interval,totalT);
		this.add(chartPanel,"cell 0 1 4 4 ");
		chartPanel.setVisible(true);
		myFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		myFrame.pack();
		refreshRpas(preT, curT);
	}
	
	/**
	 * Compare current time period and previous time period, visualize their differenece on rpas.
	 * @param preT Crime of the previous time period
	 * @param curT Crime of the current time period
	 */
	public void refreshRpas(SortedMap<Long, crimeCase> preT,SortedMap<Long, crimeCase> curT){
		ArrayList<rpaPolygon> rpas=rpaAggregate.rpaPolygons;
		int[] cntArray=new int[rpas.size()];
		for (crimeCase c:curT.values()) {
			for(int j=0;j<rpaAggregate.rpaPolygons.size();j++){
				if(areaIdentifier.isInArea(rpas.get(j),c.getPoint())){
					cntArray[j]++;
					break;
				}
			}
		}
		for (crimeCase c:preT.values()) {
			for(int j=0;j<rpaAggregate.rpaPolygons.size();j++){
				if(areaIdentifier.isInArea(rpas.get(j),c.getPoint())){
					cntArray[j]--;
					break;
				}
			}
		}
		for (int i = 0; i < cntArray.length; i++) {
			myWebView.refreshRpa2(i, cntArray[i]);
		}
	}
	
	/**
	 * Get a chart panel based on the parameters.
	 * @param preT crimes in previous time period
	 * @param curT crimes in current time period
	 * @param time1 starting time of previous time period in long
	 * @param time2 starting time of current time period in long
	 * @param interval String indicating the time interval for comparison
	 * @param totalT total time interval
	 */
	public void getPanelTime(SortedMap<Long, crimeCase> preT,SortedMap<Long, crimeCase> curT,Long time1, Long time2,String interval,String totalT){
		final DefaultCategoryDataset dataset = new DefaultCategoryDataset(); 
		Long intervalLong=timeMap.get(interval);
		for (long i = 1; i <= timeMap.get(totalT)/intervalLong; i++) {
			int lastCrimeCnt=preT.subMap(time1+(i-1)*intervalLong,time1+i*intervalLong).size();
			int curCrimeCnt=curT.subMap(time2+(i-1)*intervalLong,time2+i*intervalLong).size();
			dataset.addValue(lastCrimeCnt, "Previous"+ totalT, interval+i);
			dataset.addValue(curCrimeCnt, "This "+totalT,interval+i);
			
		}
		final JFreeChart chart = ChartFactory.createBarChart(
	            "Dual Axis Chart",        // chart title
	            "Category",               // domain axis label
	            "Value",                  // range axis label
	            dataset,                 // data
	            PlotOrientation.VERTICAL,
	            true,                     // include legend
	            true,                     // tooltips?
	            false                     // URL generator?  Not required...
	        );
	      // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
        chart.setBackgroundPaint(Color.white);
//        chart.getLegend().setAnchor(Legend.SOUTH);

        // get a reference to the plot for further customisation...
        final CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(0xEE, 0xEE, 0xFF));
        plot.setDomainAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);
        // add the chart to a panel...
        chartPanel = new ChartPanel(chart);
	}
	
	public void removeChartPanel(){
		if(chartPanel!=null){
			this.remove(chartPanel);
		}
	}
	
}
