package PlotTools;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

import de.micromata.opengis.kml.v_2_2_0.Coordinate;
import javafx.application.Platform;
import javafx.scene.web.WebEngine;
import netscape.javascript.JSObject;


public class Polygon {
	public String name;
	public ArrayList<Point2D.Double> latlngArrayList;
	public Polygon(){}
	
	/**
	 * Create a polygon with a name and a latitude, longitude list
	 * @param name
	 * @param latlngArrayList
	 */
	public Polygon(String name, ArrayList<Point2D.Double> latlngArrayList){
		this.name=name;
		this.latlngArrayList=latlngArrayList;
	}
	
	@Deprecated
	/**
	 * Import a file containing polygon data. Format : name: latlnglist
	 * @param fileName
	 * @return
	 * @throws FileNotFoundException 
	 */
	public Polygon(String fileName) throws FileNotFoundException{
		Scanner in= new Scanner(new File(fileName));
		this.name=in.next();
		ArrayList<Point2D.Double> listPoint=new ArrayList<Point2D.Double>();
		while(in.hasNext()){	
			String[] cur=in.next().split(",");
			double lng=Double.parseDouble(cur[0])
					,lat=Double.parseDouble(cur[1]);
			listPoint.add(new Point2D.Double(lat,lng));
		}
		this.latlngArrayList=listPoint;
		in.close();
	}
}
