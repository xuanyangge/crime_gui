package PlotTools;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import org.python.antlr.PythonParser.return_stmt_return;
import org.python.modules.math;

import CrimeCase.crimeCase;

public class rpaPolygon extends Polygon{
	private String precinct;
	private ArrayList<crimeCase> crimeCases;
	public double maxX,minX,maxY,minY;
	public static final int colorSchema=5;
	
	public rpaPolygon(String name, ArrayList<Point2D.Double> latlngArrayList,String precinct){
		super(name, latlngArrayList);
		this.precinct=precinct;
		crimeCases=new ArrayList<crimeCase>();
		maxX=-700;minX=700;maxY=-700;minY=700;
		for(int i=0;i<latlngArrayList.size();i++){
			maxX=Math.max(maxX, latlngArrayList.get(i).x);
			maxY=Math.max(maxY, latlngArrayList.get(i).y);
			minX=Math.min(minX, latlngArrayList.get(i).x);
			minY=Math.min(minY, latlngArrayList.get(i).y);
		}
	}
	
	//Get count of crimeCase on the rpa.
	public int getCount(){
		return crimeCases.size();
	}
	
	//Get the precinct the rpa belongs to
	public String getPrecint(){
		return precinct;
	}
	
	//Add the crime which is on the rpa to the rpa.
	public void addCrime(crimeCase c){
		crimeCases.add(c);
	}
	public void clearCrime(){
		crimeCases.clear();
	}
}
