package PlotTools;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Map;

import mainPackage.webView;
import CrimeCase.crimeCase;

public class RpaAggregate {
	public ArrayList<rpaPolygon> rpaPolygons;
	
	public RpaAggregate(ArrayList<rpaPolygon> rpaPolygons){
		this.rpaPolygons=rpaPolygons;
	}
	
	/**
	 * Add the crime to one of the rpa depending on the crime's geo location.
	 * @param crimeCase c
	 * @return
	 */
	public void addCrime(crimeCase c){
		for(rpaPolygon rpa:rpaPolygons){
			if(areaIdentifier.isInArea(rpa, new Point2D.Double(c.getLa(),c.getlo()))){
				rpa.addCrime(c);
				break;
			}
		}
	}

	//Assign colors to the polygons on map based on the crimes inside that area.
	public void refresh(webView myWebView){
		for(int i=0;i<rpaPolygons.size();i++){
			rpaPolygon rpa= rpaPolygons.get(i);
			int cnt=rpa.getCount();
			myWebView.refreshRpa(i, cnt);
		}
	}
	
	//Assign a list of crimes into the rpas they belongs to, then refresh their colors based on the number of crimes.
	public void addCrimeList(ArrayList<crimeCase> crimeList,webView myWebView){
		for(int i=0;i<crimeList.size();i++){
			addCrime(crimeList.get(i));
		}
		refresh(myWebView);
	}
	
	//Assign a map of crimes into the rpas they belongs to, then refresh their colors based on the number of crimes.
	public void addCrimeList(Map<Long,crimeCase> crimeList,webView myWebView){
		for(crimeCase c:crimeList.values()){
			addCrime(c);
		}
		refresh(myWebView);
	}
	
	
	//Clear all the crimes inside the rpaPolygons.
	public void clearCrime(webView myWebView){
		for(rpaPolygon rpa:rpaPolygons){
			rpa.clearCrime();
		}
		refresh(myWebView);
	}
	
}
