package misellanies;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.TreeMap;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import mainPackage.webView;

import org.apache.poi.hssf.record.EmbeddedObjectRefSubRecord;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.json.JSONArray;
import org.json.simple.parser.ParseException;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.TermCriteria;
import org.opencv.ml.EM;
import org.python.antlr.PythonParser.or_test_return;
import org.python.antlr.PythonParser.power_return;
import org.python.antlr.base.mod;
import org.tc33.jheatchart.HeatChart;
import org.xml.sax.InputSource;

import CrimeCase.*;
import PlotTools.*;


public class test{
	
	public static void testPrint(){
		crimeCase testCrime=new crimeCase("20140101 00:00", 36.1767523037931,-86.6037992308459);
		testCrime.print();
	}
	
	public static void convertToText() throws InvalidFormatException, IOException{
		PrintStream output=new PrintStream(new File("burglary.txt"));
		pastCrime p = new pastCrime("data/Incident2014_Sample_Burglary.xlsx", 1, 0, 'N'-'A', 'O'-'A', 'G'-'A', 2,2);
		Map<Long,crimeCase> map=p.getList();
		map=pastCrime.burglaryFilter(map);
		for(crimeCase c:map.values()){
			output.println(c.getIncidentNumber()+" "+c.getLa()+" "+c.getlo()+" "+ c.getTime().getTime()+" "+c.getType());
		}
		output.close();
	}
	
	public static void testScanner(){
		String s="((35.0,15.2),(36.5,40.5))";
		String res="";
		for (int i = 0; i < s.length(); i++) {
			char c=s.charAt(i);
			if(c!='('&&c!=')'&&c!=','){
				res+=c;
			}else{
				res+=' ';
			}
		}
		Scanner sc=new Scanner(res);
		while(sc.hasNextDouble()){
			System.out.println(sc.nextDouble());
		}
	}
	
	public static ArrayList<Polygon> testPolygons() throws FileNotFoundException{
		ArrayList<Polygon> res= new ArrayList<Polygon>();
		Polygon central=new Polygon("txtFile/central.txt");
		Polygon east=new Polygon("txtFile/east.txt");
		Polygon hermitage=new Polygon("txtFile/hermitage.txt");
		Polygon madison=new Polygon("txtFile/madison.txt");
		Polygon mid_town_hills=new Polygon("txtFile/mid-town-hills.txt");
		Polygon north=new Polygon("txtFile/North.txt");
		Polygon south=new Polygon("txtFile/South.txt");
		Polygon south_lake=new Polygon("txtFile/South-lake.txt");
		Polygon west=new Polygon("txtFile/west.txt");
		res.add(west);
		res.add(south_lake);
		res.add(south);
		res.add(north);
		res.add(mid_town_hills);
		res.add(madison);
		res.add(hermitage);
		res.add(central);
		res.add(east);
		return res;
	}
	
	
	public static void printOpenCv() throws FileNotFoundException{
		PrintStream output=new PrintStream(new File("openCVTest.txt"));
		Random r=new Random();
		for(int i=0;i<5000;i++){
			double x=r.nextDouble()*1000;
			double y=r.nextDouble()*1000;
			output.println(x+" "+y);
		}
		output.close();
	}
	
	public static void testOpenCv() throws FileNotFoundException{
		Scanner scanner=new Scanner(new File("openCVTest.txt"));
		//PrintStream output=new PrintStream(new File("openCVTest1.txt"));
		TermCriteria t=new TermCriteria();
		t.maxCount=1000;
		t.epsilon=Math.pow(10, -6);
		EM train=new EM(5,EM.COV_MAT_GENERIC,t);
		Mat sample=new Mat(5000,2,CvType.CV_64F);
		int count=0;
		Random r=new Random();
		double sum=0;
		while(scanner.hasNext()){
			double x=scanner.nextDouble();
			double y=scanner.nextDouble();
			sum+=x;
			//output.println(x);
			sample.put(count, 0, x);
			sample.put(count++, 1, y);
		}
		System.out.println(sample.dump());
		Mat prob=new Mat(5,1,CvType.CV_64F);
		for(int i=0;i<5;i++){
			prob.put(0, i, 0.2);
		}
		System.out.println(train.train(sample));
		System.out.println(train.getMat("means").dump()+" "+sum/5000);
		System.out.println(train.getMat("weights").dump());
		System.out.println(train.getMatVector("covs").get(0).dump());
		Mat means=train.getMat("means");
		double[] a=new double[4];
		System.out.println(means.get(0, 0,a));
		System.out.println(a[0]+" "+a[1]+" "+a[2]+" "+a[3]);
	}
	
	
	
	private static void convertToTextWIthPrecinct() throws FileNotFoundException{
		Map<Long, crimeCase> map=pastCrime.burglaryCases("burglary.txt");
		ArrayList<Polygon> polygons=kmlParser.getPrecinct();
		for(crimeCase c:map.values()){
			c.setPrecinct(polygons);
		}
		PrintStream output=new PrintStream(new File("burglarywithprecinct.txt"));
		int unknowncount=0;
		for(crimeCase c:map.values()){
			if(c.getPrecint().equals("unknown"))unknowncount++;
			output.println(c.getIncidentNumber()+" "+c.getLa()+" "
					+c.getlo()+" "+ c.getTime().getTime()+" "+c.getPrecint() +" "+c.getType());
		}
		System.out.println(unknowncount);
	}
	
	
	
	private static void testHeatMap() throws IOException{
		Map<Long, crimeCase> map = pastCrime.burglaryCases("burglarywithprecinct.txt");
		Mat sample=new Mat(map.size(),2,CvType.CV_64F);
		int count=0;
		for(crimeCase c:map.values()){
			sample.put(count, 0, c.getLa());
			sample.put(count++, 1, c.getlo());
		}
		gausianMixture myModel=new gausianMixture(sample, 5,1000,"");
		EM model=myModel.getModel();
		final int pointSize=100;
		double[][] coords=new double[pointSize][pointSize];
		double l=36.014666149259334;
		double r=36.35881675881081;
		double bottom=-87.02499921875;
		double top=-86.56288679199218;
		for(int i=0;i<pointSize;i++){
			double x=l+(r-l)/pointSize*i;
			for(int j=0;j<pointSize;j++){
				Mat mat=new Mat(1,2,CvType.CV_64F);
				double y=bottom+(top-bottom)/pointSize*j;
				mat.put(0, 0, x);
				mat.put(0, 1, y);
				coords[i][j]=model.predict(mat)[0];
			}
		}
		HeatChart chart=new HeatChart(coords);
		chart.setShowXAxisValues(false);
		chart.setShowYAxisValues(false);
		chart.setHighValueColour(java.awt.Color.RED);
		chart.setLowValueColour(java.awt.Color.GREEN);
		File pic=new File("file.jpg");
		chart.saveToFile(pic);
	}
	
	
	public static void getNcluseter() throws FileNotFoundException{
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		TreeMap<Long, crimeCase> map = pastCrime.burglaryCases("burglarywithprecinct.txt");
		Mat sample=new Mat(map.size(),2,CvType.CV_64F);
		int count=0;
		
		//Put crimes into the Matrix for trainning
		for(crimeCase c:map.values()){
			sample.put(count, 0, c.getLa());
			sample.put(count++, 1, c.getlo());
		}
		double Min=Double.MAX_VALUE;
		double choice=-1;
		for(int i=1;i<=20;i++){
			gausianMixture myModel=new gausianMixture(sample, i,1000,"");
			//Free variables in means+ free variables in cov + free variables in weight
			double k=i*2+i*4+i-1;
			double L=0;
			for(crimeCase c:map.values()){
				Mat mat=new Mat(1,2,CvType.CV_64F);
				mat.put(0, 0, c.getLa());
				mat.put(0, 1, c.getlo());
				L-=2*myModel.getModel().predict(mat)[0];
			}
			double BIC=L+k*Math.log(map.size());
			System.out.println(L+" "+BIC);
			if(BIC<Min){
				Min=BIC;
				choice=i;
			}
		}
		cookies myCookies=cookies.getInstance();
		myCookies.put("nClusters", choice);
		myCookies.saveCookies();
	}
	
	
	public static void testJson() throws FileNotFoundException, ParseException{
		JSONArray mArray=new JSONArray();
		TreeMap<Long, crimeCase> map= pastCrime.burglaryCases("burglarywithprecinct.txt");
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat sample=new Mat(map.size(),2,CvType.CV_64F);
		int count=0;
		
		//Put crimes into the Matrix for trainning
		for(crimeCase c:map.values()){
			sample.put(count, 0, c.getLa());
			sample.put(count++, 1, c.getlo());
		}
	    gausianMixture myModel=new gausianMixture(sample,17,1000,"");
		JsonBuilder builder=new JsonBuilder();
		builder.buildGaussianModelJson(myModel);
	}
	
	
	
	public static void main(String[] args) throws InvalidFormatException, IOException, ParseException {
		// TODO Auto-generated method stub
		//testPrint();
		//testScanner();
		//convertToText();
		//testPolygon();
		//testPolygons();
		//System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
		//testHeatMap();
		//getNcluseter();
		//testOpenCv();
		//convertToText();
		//convertToTextWIthPrecinct();
		//String string="midtown-hills";
		//System.out.println(string.toUpperCase().replaceFirst("[^a-zA-Z]", ""));
		testJson();
	}

}
