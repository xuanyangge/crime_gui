package PlotTools;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import mainPackage.crimeDist_init;
import javafx.application.Platform;
import javafx.scene.web.WebEngine;


public class WebPolygon extends Polygon {
	private boolean definedRegion;
	
	/**
	 * This class is now only getting the focus and choose precinct.
	 * The commented previous codes allow the user to choose a custom area using drawing tool in google map.
	 * @param myEngine
	 * @param precincts
	 */
	public WebPolygon(WebEngine myEngine,ArrayList<Polygon> precincts){
		name="UserDefinedShape";
		latlngArrayList=new ArrayList<Point2D.Double>();
		Runnable runnable=new Runnable(){
			@Override
            public void run() {
				/*definedRegion=(boolean)myEngine.executeScript("isEnableArea()");
				if(definedRegion){
					int length=(int)myEngine.executeScript("shape.getPath().getLength()");
					for(int i=0;i<length;i++){
						double lat=(double)myEngine.executeScript("shape.getPath().getAt("+i+").lat()");
						double lng=(double)myEngine.executeScript("shape.getPath().getAt("+i+").lng()");
						latlngArrayList.add(new Point2D.Double(lat,lng));
					}
				}else{*/
					int focus=crimeDist_init.getFocus();
					if(focus>=0){
						latlngArrayList=precincts.get(focus).latlngArrayList;
					}
				//}
			}
		};
		Platform.runLater(runnable);
	}
	
	public boolean getDefinedRegion(){
		return definedRegion;
	}
	
	public Polygon getArea(){
		if(latlngArrayList.size()==0){
			return null;
		}else{
			return this;
		}
	}
}
