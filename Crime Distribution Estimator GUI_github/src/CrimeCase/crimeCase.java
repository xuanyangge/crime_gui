package CrimeCase;
import PlotTools.*;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;

import org.python.antlr.PythonParser.lambdef_return;
import org.python.antlr.PythonParser.return_stmt_return;


public class crimeCase {
	private static HashMap<String, Long> precinctNumberMap;
	private Date time;
	private boolean past=true;
	private double la;
	private long incidentNumber;
	private double lo;
	private String precinct="undefined";
	
	private ArrayList<String> type=new ArrayList<String>();
	@Deprecated
	/*
	 * Input incident number,the time of the occurance, latitude,longitude, and type of the crime case
	 * Time format:YYYYMMDD HH:MM
	 */
	public crimeCase(String Time,double La ,double Lo){
		time=parseTime(Time);
		la=La;
		lo=Lo;
	}
	
	/**
	 * Input a date object, latitude,longitude;
	 * @param date date object
	 * @param La
	 * @param Lo
	 */
	public crimeCase(Date date,double La,double Lo){
		time=date;
		la=La;
		lo=Lo;
	}
	
	@Deprecated
	/**
	 * Constructor
	 * @param incidentNumber
	 * @param Time format:YYYYMMDD HH:MM
	 * @param La
	 * @param Lo
	 */
	public crimeCase(long incidentNumber,String Time,double La ,double Lo){
		time=parseTime(Time);
		this.incidentNumber=incidentNumber;
		la=La;
		lo=Lo;
	}
	/**
	 * Constructor
	 * @param incidentNumber
	 * @param Time format:long.
	 * @param La
	 * @param Lo
	 */
	public crimeCase(long incidentNumber,long Time,double La ,double Lo){
		time=new Date(Time);
		this.incidentNumber=incidentNumber;
		la=La;
		lo=Lo;
	}
	/*
	 * Input incident number,the time of the occurance, latitude,longitude, 
	 * and type of the crime case, and whether the case is simulated or past, true for the past crime.
	 * Time format:YYYYMMDD HH:MM
	 */
	public crimeCase(long incidentNumber,String time,double La ,double Lo,boolean past){
		this.time=parseTime(time);
		this.incidentNumber=incidentNumber;
		la=La;
		lo=Lo;
		this.past=past;
	}
	/*
	 * get occur time of the crime
	 */
	public Date getTime(){
		return time;
	}
	/*
	 * get latitude of the crime
	 */
	public double getLa(){
		return la;
	}
	/*
	 * get longitude of the crime
	 */
	public double getlo(){
		return lo;
	}
	/*
	 * get Type of the crime
	 */
	public String getType(){
		if(type.size()>2){
			return "morethan2";
		}
		return type.get(0);
	}
	
	/*
	 * get Type of the crime
	 */
	public ArrayList<String> getTypeArr(){
		return type;
	}
	
	/*
	 * get Type of the crime as a string like "Type1 Type2 Type 3..."
	 */
	public String getTypeS(){
		String res="";
		for(String s:type){
			res+=s+" ";
		}
		return res;
	}
	/*
	 * get incident number of the crime
	 */
	public long getIncidentNumber(){
		return incidentNumber;
	}
	/*
	 * Parse string of occur time into long int
	 * Time format:YYYYMMDD HH:MM
	 */
	public static Date parseTime(String Time){
		String res="";
		for(int i=0;i<Time.length();i++){
			if(Time.charAt(i)!=' '&&Time.charAt(i)!=':'){
				res=res+Time.charAt(i);
			}
		}
		int year=Integer.parseInt(res.substring(0, 4))-1900;
		int month=Integer.parseInt(res.substring(4,6));
		int day=Integer.parseInt(res.substring(6, 8));
		int hours=Integer.parseInt(res.substring(8,10));
		int minutes=Integer.parseInt(res.substring(10, 12));
		@SuppressWarnings("deprecation")
		Date resDate=new Date(year,month,day,hours,minutes);
		return resDate;
	}
	
	//Set the crime to be the type ty. If a crime has more than one type, add ty to the array.
	public void addType(String ty){
		type.add(ty);
	}
	
	
	//Get whether the crime is past or simulated
	public boolean getPast(){
		return past;
	}
	
	public void print(){
		System.out.println("Crime: time: "+time+" latitude: "+la+" longitude: "+lo);
	}	
	
	/**
	 * Pass a group of precincts. If the crime locates in one of them. Set the precinct to be that.
	 * Otherwise set the precinct to be "unknown".
	 * @param polygons
	 */
	public void setPrecinct(ArrayList<Polygon> polygons){
		for(Polygon p:polygons){
			if(areaIdentifier.isInArea(p, new Point2D.Double(la,lo))){
				precinct=p.name;
				break;
			}
		}
		if(this.precinct==null){
			this.precinct="unknown";
		}
	}
	
	/**
	 * Manually set the precinct with a string.
	 * @param s
	 */
	public void setPrecinct(String s){
		precinct=s;
	}
	
	/**
	 * Helper function, determines if the crime is in the precinct
	 * @param p
	 * @return
	 */
	public boolean isInArea(Polygon p){
		return areaIdentifier.isInArea(p, new Point2D.Double(la,lo));
	}
	
	/**
	 * Another helper function,determines if the crime is in the precinct, need to pass a point parameter
	 * @param p
	 * @param point
	 * @return
	 */
	public static boolean isInArea(Polygon p, Point2D.Double point){
		return areaIdentifier.isInArea(p,point);
	}
	
	
	public static void putInMap(String precinct,long num){
		precinctNumberMap.put(precinct, num);
	}
	
	public String getPrecint(){
		return precinct;
	}
	
	public Point2D.Double getPoint(){
		return new Point2D.Double(la, lo);
	}
}
