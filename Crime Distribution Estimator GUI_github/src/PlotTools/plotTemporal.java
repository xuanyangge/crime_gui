package PlotTools;
import java.awt.BorderLayout;

import CrimeCase.*;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.JPanel;

import javafx.geometry.Point2D;
import javafx.scene.chart.NumberAxis;
import javafx.scene.web.WebView;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Hour;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.util.Log;
import org.python.antlr.PythonParser.return_stmt_return;
import org.python.modules.itertools;


public class plotTemporal {
	private final long dayMili=3600*24*1000;
	private final long hourMili=3600*1000;
	//Parameters for weibull distribution from the python code.
	private final double myIntercept=5.165322;
	private final double b[]={5.165322,  0.2254251 ,0.05051744,  0.09785005 , 0.1232631, -0.01222590 , -0.1805454, 0.04519435, -0.01158541, -0.007802575, -0.002253878, -0.006131052 ,-0.00004056611, -0.002697404};
	private final double a=1/0.9785091;
	private temporalDensity tDensity;	
	private long timeInterval;
	private WebPolygon myArea;
	private gausianMixture myGausianMixture;
	private TreeMap<Long,crimeCase> crimeMap;
	private ArrayList<Polygon> precincts;
	
	/**
	 * Create a class for ploting temporal crime distribution.
	 * @param list of crimes
	 * @param area the user choose. 
	 * @param gausianMixture the model.
	 */
	public plotTemporal(TreeMap<Long, crimeCase> list, WebPolygon area,gausianMixture gausianMixture){
		//miliseconds in 4 hours 
		timeInterval=4*3600*1000;
		tDensity=new temporalDensity(a, b, b.length,timeInterval);
		myArea=area;
		myGausianMixture=gausianMixture;
	}
	
	public plotTemporal(WebPolygon area,gausianMixture gausianMixture,ArrayList<Polygon> precincts, TreeMap<Long, crimeCase> crimeMap){
		//miliseconds in 4 hours 
		timeInterval=4*3600*1000;
		tDensity=new temporalDensity(a, b, b.length,timeInterval);
		myArea=area;
		myGausianMixture=gausianMixture;
		this.precincts=precincts;
		this.crimeMap=crimeMap;
	}

	
	/**
	 * Generate a random number sampling from weibull distribution. 
	 * If the time period is longer than 10 days the program will choose day as unit, otherwise as 4 hrs.
	 * @param rangeL Starting date
	 * @param rangeR End date
	 * @param focus The current focus precinct.
	 * @param offSet The point on x-axis that the random values should start to be plotted. Since the plot will be created by both the random generator and the past crime generator, part of x-axis is already plotted. 
	 * @return
	 */
	private XYSeries randomGeneratorMode(Date rangeL,Date rangeR,int focus,long offSet){
		final XYSeries s1 = new XYSeries("Predicted crime");
		long cnt=offSet;
		int dayCnt=0;
		for(long i=rangeL.getTime();i<=rangeR.getTime();i+=timeInterval){
			Date date=new Date(i);
			int x[]=getX(date);
			tDensity.updateLamda(x, myIntercept);
			ArrayList<Long> timeList=tDensity.generateRandom();
			int caseCnt=timeList.size();
			caseCnt=0;
			for(int j=0;j<timeList.size();j++){
				Date dateC =new Date(timeList.get(j));
				Double myPoint=myGausianMixture.generatePoint();
				crimeCase c=new crimeCase(dateC, myPoint.x, myPoint.y);
				c.addType("Simulated");
				if(c.isInArea(myArea)){
					caseCnt++;
				};
			}
			if(rangeR.getTime()-rangeL.getTime()<=10*dayMili){
				//Show hours
				s1.add(4*cnt,caseCnt);
			}else{
				//Show days
				if(cnt%6==0){
					s1.add(cnt/6.0,dayCnt);
					dayCnt=0;
				}else{
					dayCnt+=caseCnt;
				}
			}
			cnt++;
		}
		s1.setDescription("Predicted Crime");
		return s1;
	}
	
	
	
	/**
	 * Plot the average number of weibull distribution. 
	 * If the time period is longer than 10 days the program will choose day as unit, otherwise as 4 hrs.
	 * @param rangeL Starting date
	 * @param rangeR End date
	 * @param focus The current focus precinct.
	 * @param offSet The point on x-axis that the random values should start to be plotted. Since the plot will be created by both the random generator and the past crime generator, part of x-axis is already plotted. 
	 * @return
	 */
	private XYSeries probabilityMode(Date rangeL,Date rangeR, int focus,long offSet){
		final XYSeries s1 = new XYSeries("Predicted crime");
		long cnt=offSet;
		int dayCnt=0;
		for(long i=rangeL.getTime();i<=rangeR.getTime();i+=timeInterval){
			Date date=new Date(i);
			int x[]=getX(date);
			tDensity.updateLamda(x, myIntercept);
			double caseCnt=tDensity.getAvg(60*4);
			/*System.out.println(caseCnt);
			for(int j=0;j<=(int)caseCnt;j++){
				Date dateC =new Date(i);
				Double myPoint=myGausianMixture.generatePoint();
				crimeCase c=new crimeCase(dateC, myPoint.x, myPoint.y);
				c.addType("Simulated");
				if(c.isInArea(myArea)){
					caseCnt++;
				};
			}*/
			if(rangeR.getTime()-rangeL.getTime()<=10*dayMili){
				//Show hours
				s1.add(4*cnt,caseCnt);
			}else{
				//Show days
				if(cnt%6==0){
					s1.add(cnt/6.0,dayCnt);
					dayCnt=0;
				}else{
					dayCnt+=caseCnt;
				}
			}
			cnt++;
		}
		s1.setDescription("Predicted Crime");
		return s1;
	}
	
	
	public XYSeries pastSeries(Date rangeL, Date rangeR,int focus){
		final XYSeries s1 = new XYSeries("Past crime");
		int cnt=0;
		for(long i=rangeL.getTime();i<=rangeR.getTime();i+=timeInterval){
			Date date=new Date(i);
			Date dateR=new Date(i+dayMili/6);
			if(rangeR.getTime()-rangeL.getTime()<=10*dayMili){
				//Show hours
				int crimeCnt=0;
				SortedMap<Long, crimeCase> tmp=crimeMap.subMap(date.getTime(), dateR.getTime());
				for(crimeCase c:tmp.values()){
					if(c.isInArea(myArea)){
						crimeCnt++;
					}
				}
				s1.add(cnt*4,crimeCnt);
			}else{
				//Show days
				if(cnt%6==0){
					dateR=new Date(i+dayMili);
					int crimeCnt=0;
					SortedMap<Long, crimeCase> tmp=crimeMap.subMap(date.getTime(), dateR.getTime());
					for(crimeCase c:tmp.values()){
						if(c.isInArea(myArea)){
							crimeCnt++;
						}
					}
					s1.add(cnt/6,crimeCnt);
				}
			}
			cnt++;
		}
		s1.setDescription("Past Crime");
		return s1;
	}
	

	
	public JPanel plot(Date rangeL,Date rangeR,boolean probabiltyMode,int focus){
		String dayOrHours="hours";
		if(rangeR.getTime()-rangeL.getTime()<=10*dayMili){
			//Show hours
			dayOrHours="Hours";
		}else{
			//Show days
			dayOrHours="days";
		}
		XYSeriesCollection dataSet=new XYSeriesCollection();
		if(rangeL.before(new Date())&&rangeR.before(new Date())){
			dataSet.addSeries(pastSeries(rangeL, rangeR,focus));
		}else{
			long offSet=0;
			Date now=new Date();
			if(rangeL.before(now)){
				offSet=(now.getTime()-rangeL.getTime())/hourMili/4;
				dataSet.addSeries(pastSeries(rangeL, now,focus));
				rangeL=now;
			}
			if(probabiltyMode){
				dataSet.addSeries(probabilityMode(rangeL, rangeR, focus, offSet));
			}else{
				dataSet.addSeries(randomGeneratorMode(rangeL, rangeR,focus,offSet));
			}
		}
		
		final JFreeChart chart = ChartFactory.createXYLineChart(
	            "Temporal crime density",          // chart title
	            dayOrHours,               // domain axis label
	            "Number of crimes",                  // range axis label
	            dataSet,                  // data
	            PlotOrientation.VERTICAL,
	            true,                     // include legend
	            true,
	            false
	        );
		final XYPlot plot = chart.getXYPlot();
        chart.setBackgroundPaint(Color.white);
        plot.setOutlinePaint(Color.black);
        final ChartPanel chartPanel = new ChartPanel(chart);
		return chartPanel;
	}
	
	/**
	 * Helper function.
	 * Get the parameters used in model based on the date. 
	 * @param date
	 * @return An array
	 */
	private int[] getX(Date date){
		int numOfCoefficient=tDensity.getNumOfCoefficient();
		int x[]=new int[numOfCoefficient];
		for(int j=0;j<numOfCoefficient;j++){
			x[j]=0;
		}
		int timeZone=date.getHours()/4;
		if(timeZone<5){
			x[timeZone]=1;
		}
		if(date.getDay()<5){
			x[5]=1;
		};
		int season=plotTemporal.getSeason(date);
		if(season>=0){
			x[6+season]=1;
		}
		Map<Long,crimeCase> lastWeek=crimeMap.subMap(date.getTime()-dayMili*7, date.getTime());
		x[9]=lastWeek.size();
		for(int j=1;j<=7;j++){
			Date leftB=new Date(date.getTime()-date.getTime()%(4*3600*1000)-dayMili*j);
			Date rightB=new Date(leftB.getTime()+4*3600*1000);
			x[10]+=crimeMap.subMap(leftB.getTime(), rightB.getTime()).size();
		}
		Map<Long,crimeCase> lastMonth=pastCrime.timeFilter(new Date(date.getTime()-dayMili*30), date, crimeMap);
		x[11]=lastMonth.size();
		for(int j=1;j<=30;j++){
			Date leftB=new Date(date.getTime()-date.getTime()%(4*3600*1000)-dayMili*j);
			Date rightB=new Date(leftB.getTime()+4*3600*1000);
			x[12]+=crimeMap.subMap(leftB.getTime(), rightB.getTime()).size();
		}
		return x;
	}
	
	/**
	 * Helper function for getting the season.
	 * @param Date d
	 * @return the season as a  int we defined.
	 */
	public static int getSeason(Date d){
		@SuppressWarnings("deprecation")
		int mon=d.getMonth();
		if(mon==0||mon==1||mon==11){
			return 0;
		}
		if(mon==2||mon==3){
			return 1;
		}
		if(mon>=4&&mon<=8){
			return 2;
		}
		return -1;
	}
	
}
