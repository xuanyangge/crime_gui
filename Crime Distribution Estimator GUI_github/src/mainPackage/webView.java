package mainPackage;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Vector;

import org.python.antlr.PythonParser.else_clause_return;
import org.python.antlr.PythonParser.return_stmt_return;

import strategy.BothStrategy;
import strategy.MarkerStrategy;
import strategy.RpaStrategy;
import strategy.SetFocusStrategy;
import strategy.nullStrategy;
import netscape.javascript.JSObject;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.DoubleExpression;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Popup;
import javafx.stage.Stage;
import PlotTools.*;
import misellanies.*;
import CrimeCase.*;
public class webView extends Region {
	 
    public WebView browser = new WebView();
    public WebEngine webEngine = browser.getEngine();
    public int precinctFocus;
    private long heatMapCount;
    private SetFocusStrategy setFocusStrategy;
    
    //Create a browser that contains the html file from localAdress
    public webView(String localAddress) {
    	Platform.runLater(new Runnable(){
			@Override
            public void run() {
				setFocusStrategy=new MarkerStrategy();
				heatMapCount=0;
		        getStyleClass().add("browser");
		        // load the web page
		        webEngine.load(getClass().getResource(localAddress).toExternalForm());
		        //add the web view to the scene
		        getChildren().add(browser);
		       // addBridge(;
			}
		});
    }

 
    @Override protected void layoutChildren() {
        double w = getWidth();
        double h = getHeight();
        layoutInArea(browser,0,0,w,h,0, HPos.CENTER, VPos.CENTER);
    }
 
    @Override protected double computePrefWidth(double height) {
        return 700;
    }
 
    @Override protected double computePrefHeight(double width) {
        return 400;
    }
 
	//Add crime onto the map.
    public void addPoint(crimeCase c){
    	Platform.runLater(new Runnable(){
			@Override
            public void run() {
		    	String s="addMarker("+c.getLa()+","+c.getlo()+",'"+c.getType() +"','"+c.getTime().toString()+"','"+c.getPrecint()+"')";
		    	webEngine.executeScript(s);
			}
		});
    }
    
	//Add points onto the heat map depending on its location and intensity.
	public void addHeatMapPoints(ArrayList<heatMapPoints> pointsArr){
		Platform.runLater(new Runnable(){
			@Override
            public void run(){
				String executeS="var heatMapData=[";
				for(heatMapPoints h:pointsArr){
					executeS=executeS+"{location: new google.maps.LatLng("+h.getLatitude()+", "
							+h.getLongitude()+"), weight: "+h.getIntensity()+"},";
				}
				//System.out.println(executeS);
				//Delete last comma
				executeS.substring(0, executeS.length()-1);
				executeS=executeS+"];";
				executeS=executeS+"var heatmap = new google.maps.visualization.HeatmapLayer({"+
						"data: heatMapData"+
						"});"+
						"heatmap.setMap(map);";
				webEngine.executeScript(executeS);
			}
		});
	}
	
	//Clear all the markers from the map
	public void clearMarker(){
		Platform.runLater(new Runnable(){
			@Override
            public void run() {
				webEngine.executeScript("clearMarker()");
			}
		});
	}
	
	//Initialize temporal density mode
	public void addRpaListeners(){
		Platform.runLater(new Runnable(){
			@Override
            public void run() {
				webEngine.executeScript("addRpaListeners()");
			}
		});
	}
	
	//Clear the user editable rectangle for circling area
	public void clearRpaListeners(){
		Platform.runLater(new Runnable(){
			@Override
            public void run() {
				webEngine.executeScript("clearRpaListeners()");
			}
		});
	}
	
	//Initialize the precincts. Creating the javascript objects so that they can be visible later.
	public void initializePrecinct(ArrayList<Polygon> polygons){
		Platform.runLater(new Runnable(){
			@Override
            public void run() {
				for(Polygon poly:polygons){
					String executeString="var coords=[";
					for(Point2D.Double po: poly.latlngArrayList){
						executeString=executeString+"new google.maps.LatLng("+po.x+","+po.y+"),";
					}
					executeString=executeString.substring(0,executeString.length()-1);
					executeString=executeString+"];";
					webEngine.executeScript(executeString);
					webEngine.executeScript("addPrecinct(coords,'"+poly.name+"');");
				}
			}
		});
	}
	
	
	//Initialize the rpas. Creating the javascript objects so that they can be visible later.
	public void initializeRpas(ArrayList<rpaPolygon> polygons){
		Platform.runLater(new Runnable(){
			@Override
            public void run() {
				for(rpaPolygon poly:polygons){
					String executeString="var coords=[";
					for(Point2D.Double po: poly.latlngArrayList){
						executeString=executeString+"new google.maps.LatLng("+po.x+","+po.y+"),";
					}
					executeString=executeString.substring(0,executeString.length()-1);
					executeString=executeString+"];";
					webEngine.executeScript(executeString);
					webEngine.executeScript("addRpa(coords,'"+poly.getPrecint()+"','"+poly.name+"');");
				}
			}
		});
	}
	
	//Show the rpa based on the current focus
	//Because the rpa has too much data, if shown at the same time, the java application will be dead,
	//Thus the UI will only show rpas in one precinct at a time,if the user choose "all precinct", the program will show the central precinct
	public void showRpa(int focus){
		Platform.runLater(new Runnable(){
			@Override
            public void run() {
				if(focus<0){
					webEngine.executeScript("showRpa(0)");
				}else{
					webEngine.executeScript("showRpa("+focus+")");
				}
			}
		});
	}
	
	//Show all markers on the map
	public void showMarker(){
		Platform.runLater(new Runnable(){
			@Override
            public void run() {
				webEngine.executeScript("showMarker()");
			}
		});
	}
	
	//Hide all the rpas
	public void hideRpa(){
		Platform.runLater(new Runnable(){
			@Override
            public void run() {
				webEngine.executeScript("hideRpa()");
			}
		});
	}
	
	//Add the heatspot based on  the polygons that is passed in.
	public void addHeatSpot(ArrayList<Polygon> polygons){
		Platform.runLater(new Runnable(){
			@Override
            public void run() {
				for(Polygon poly:polygons){
					String executeString="var coords=[";
					for(Point2D.Double po: poly.latlngArrayList){
						executeString=executeString+"new google.maps.LatLng("+po.x+","+po.y+"),";
					}
					executeString=executeString.substring(0,executeString.length()-1);
					executeString=executeString+"];";
					webEngine.executeScript(executeString);
					//System.out.println(executeString);
					webEngine.executeScript("addHotSpot(coords)");
				}
			}
		});
	}
	
	//Make the precincts visible
	public void showPrecinct(){
		Platform.runLater(new Runnable(){
			@Override
            public void run() {
				webEngine.executeScript("showPrecinct()");
			}
		});
	}
	
	//Hide the precincts
	public void hidePrecinct(){
		Platform.runLater(new Runnable(){
			@Override
            public void run() {
				webEngine.executeScript("hidePrecinct()");
			}
		});
	}
	
	//Get bounds of the rectangle in temporal density mode,must run in FX thread
	public bounds getBounds(){
        bounds res;
        webEngine.executeScript("heatMapBoundsCache=map.getBounds()");
        double n=(double)webEngine.executeScript("map.getBounds().getNorthEast().lat();");
		double e=(double)webEngine.executeScript("map.getBounds().getNorthEast().lng();");
		double s=(double)webEngine.executeScript("map.getBounds().getSouthWest().lat();");
		double w=(double)webEngine.executeScript("map.getBounds().getSouthWest().lng();");
		res=new bounds(n, e, s, w);
		return res;
	}
	
	//Get user draw polygon, must run in FX thread
	/*public Polygon getPoly(){
		return new WebPolygon(webEngine);
	}*/
	
	public void showHeatMap(String fileName){
		Platform.runLater(new Runnable(){
			@Override
            public void run() {
				webEngine.executeScript("showHeatMap('"+fileName+"')");
			}
		});
	}
	
	public void clearShape(){
		Platform.runLater(new Runnable(){
			@Override
            public void run() {
				webEngine.executeScript("clearShape()");
			}
		});
	}
	
	public void clearHeatMap(){
		Platform.runLater(new Runnable(){
			@Override
            public void run() {
				webEngine.executeScript("clearHeatMap()");
			}
		});
	}
	public void clearHotSpot(){
		Platform.runLater(new Runnable(){
			@Override
            public void run() {
				webEngine.executeScript("clearHotSpot()");
			}
		});
	}

	public void printBrow(Scene bScene) {
		Printer printer = Printer.getDefaultPrinter();
		PageLayout pageLayout = printer.createPageLayout(Paper.NA_LETTER, PageOrientation.LANDSCAPE, Printer.MarginType.DEFAULT);
		pageLayout.getPageOrientation();
		PrinterJob job = PrinterJob.createPrinterJob(printer);
		if (job != null && pageLayout.getPageOrientation() == PageOrientation.LANDSCAPE) {
			Popup popup = new Popup();
			job.showPageSetupDialog(popup);
			webEngine.print(job);
			job.endJob();
		}else{
			System.out.println("failure met");
		}
	}

    /*public class Bridge{
    	public void changePrecinct(int focus){
    		precinctFocus=focus;
    		System.out.println(focus);
    	}
    }
    
    public int getPrecinctFocus(){
    	return this.precinctFocus;
    }
    
    
    private void addBridge(){
    	JSObject jsobj = (JSObject) webEngine.executeScript("window");
    	jsobj.setMember("java", new Bridge());
    }*/
	
	/**
	 * A very useful method that occurs in a lot of places in this program. 
	 * This will show the UI components like rpas or markers in a certain precinct based on the things the user selects to see.
	 * The integer focus controls which precinct to be shown and the strategy controls which UI components to show.
	 * @param focus
	 */
	public void setFocus(int focus){
		Platform.runLater(new Runnable(){
			@Override
            public void run() {
				setFocusStrategy.setFocus(webEngine, focus);
			}
		});
	}
	
	//Hide all the markers
	public void hideMarker(){
		Platform.runLater(new Runnable(){
			@Override
            public void run() {
				webEngine.executeScript("hideMarker()");
			}
		});
	}
	
	/**
	 * Set the rpas to be a certain color. Used in past crime and simulated crime. 
	 * The logic for determining color based on the number is in the javacript.
	 * @param ind the index of the rpa.
	 * @param col the number of crimes in the rpa.
	 */
	public void refreshRpa(int ind,int col){
		Platform.runLater(new Runnable(){
			@Override
            public void run() {
				webEngine.executeScript("refreshRpaColor("+ind+","+col+");");
			}
		});
	}
	
	/**
	 * This method is almost the same as refreshRpa but the number can be negative which is used for comparison
	 * between two time period.
	 * @param ind
	 * @param col
	 */
	public void refreshRpa2(int ind,int col){
		Platform.runLater(new Runnable(){
			@Override
            public void run() {
				webEngine.executeScript("refreshRpaColor2("+ind+","+col+");");
			}
		});
	}
	
	//Show the color schema1.
	public void showOverMap1(){
		Platform.runLater(new Runnable(){
			@Override
            public void run() {
				webEngine.executeScript("$(\"#over_map\").show()");
			}
		});
	}
	
	//Show the color schema2
	public void showOverMap2(){
		Platform.runLater(new Runnable(){
			@Override
            public void run() {
				webEngine.executeScript("$(\"#over_map2\").show()");
			}
		});
	}
	
	//Hide both of the color schemas
	public void hideOverMap(){
		Platform.runLater(new Runnable(){
			@Override
            public void run() {
				webEngine.executeScript("$(\"#over_map\").hide()");
				webEngine.executeScript("$(\"#over_map2\").hide()");
			}
		});
	}
	
	/**
	 * Change the set focus strategy based on whether the user selects to see markers and/or rpas.
	 * @param enableMarker
	 * @param enableRpa
	 */
	public void changeStrategy(boolean enableMarker,boolean enableRpa){
		if(enableMarker&&enableRpa){
			setFocusStrategy=new BothStrategy();
		}else if(enableMarker){
			setFocusStrategy=new MarkerStrategy();
		}else if(enableRpa){
			setFocusStrategy=new RpaStrategy();
		}else{
			setFocusStrategy=new nullStrategy();
		}
	}

}