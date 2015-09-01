package PlotTools;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;

import javax.imageio.ImageIO;

import mainPackage.webView;

import org.jfree.data.Values;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.tc33.jheatchart.HeatChart;

public class heatMap {
	private gausianMixture myModel;
	private int pointSize=250;
	private double[] colorScale={0,0.25,0.5,0.75,1};
	private Color[] colors={new Color(0,0,255),Color.cyan,Color.green,Color.yellow, new Color(153,0,0)};
	private ArrayList<Polygon> precincts;
	private webView myWebView;
	/**
	 * Create a class for creating heat map.
	 * @param model gaussian model
	 * @param preArrayList precincts list for heat map chooser
	 * @param webView webView for showing points.
	 */
	public heatMap(gausianMixture model, ArrayList<Polygon>  preArrayList,webView webView){
		myModel=model;
		precincts=preArrayList;
		myWebView=webView;
	}
	
	/**
	 * Creat a heatmap class based on only model and the image resolution.
	 * @param model
	 * @param pointSize
	 */
	public heatMap(gausianMixture model,int pointSize){
		myModel=model;
		this.pointSize=pointSize;
	}
	
	
	/**
	 * Create a file containing the image of heat map for the area the user is seeing right now.
	 * @param b The area of the google map that the user is seeing right now. 
	 * @param outPutFile Path.
	 * @throws IOException
	 */
	public void predict(bounds b, String outPutFile) throws IOException{
		double[][] values=new double[pointSize][pointSize];
		for(int i=0;i<pointSize;i++){
			double x=b.getN()+((b.getS()-b.getN())/pointSize)*i;
			for(int j=0;j<pointSize;j++){
				Mat mat=new Mat(1,2,CvType.CV_64F);
				double y=b.getW()+((b.getE()-b.getW())/pointSize)*j;
				mat.put(0, 0, x);
				mat.put(0, 1, y);
				Polygon areaPolygon=new WebPolygon(myWebView.webEngine, precincts);
				if(areaIdentifier.isInArea(areaPolygon, new Point2D.Double(x,y))){
					values[i][j]=Math.exp(myModel.getModel().predict(mat)[0]);
				}else{
					values[i][j]=0;
				}
			}
		}
		/*HeatChart chart=new HeatChart(values);
		chart.setHighValueColour(java.awt.Color.RED);
		Color lowValueColor=new Color(255,255,255,0);
		chart.setLowValueColour(java.awt.Color.blue);
		chart.setShowXAxisValues(false);
		chart.setShowYAxisValues(false);
		chart.setColourScale(1);*/
		File pic=new File(outPutFile);
		pic.delete();
		ImageIO.write(draw(values), "png",pic);
		System.out.println("done");
	}
	
	/**
	 * Helper function, rescale all the values to 0 to 1.
	 * @param values
	 * @return The values after rescaling.
	 */
	private double[][] scale(double[][] values){
		double Max=java.lang.Double.NEGATIVE_INFINITY;
		double Min=java.lang.Double.POSITIVE_INFINITY;
		for(int i=0;i<pointSize;i++){
			for(int j=0;j<pointSize;j++){
				if(values[i][j]>Max){
					Max=values[i][j];
				}
				if(values[i][j]<Min){
					Min=values[i][j];
				}
			}
		}
		for(int i=0;i<pointSize;i++){
			for(int j=0;j<pointSize;j++){
				values[i][j]-=Min;
				values[i][j]/=(Max-Min);
				if(values[i][j]>1){
					System.out.println("This is wrong");
				}
			}
		}
		return values;
	}
	
	/**
	 * Draw the image using color interpolation method. 
	 * @param values
	 * @return A image.
	 */
	public BufferedImage draw(double [][] values){
		values=scale(values);
		BufferedImage res=new BufferedImage(pointSize, pointSize,BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D graph=res.createGraphics();
		for(int i=0;i<pointSize;i++){
			for(int j=0;j<pointSize;j++){
				if(values[i][j]<0.05){
					graph.setColor(new Color(255,255,255,0));
					graph.fillRect(i, j, 1, 1);
				}else{
					int k;
					for(k=0;k<5;k++){
						if(colorScale[k]>=values[i][j])break;
					}
					int r=(int)((colors[k].getRed()*(colorScale[k]-values[i][j])+colors[k-1].getRed()*(values[i][j]-colorScale[k-1]))/(colorScale[k]-colorScale[k-1]));
					int g=(int)((colors[k].getGreen()*(colorScale[k]-values[i][j])+colors[k-1].getGreen()*(values[i][j]-colorScale[k-1]))/(colorScale[k]-colorScale[k-1]));
					int b=(int)((colors[k].getBlue()*(colorScale[k]-values[i][j])+colors[k-1].getBlue()*(values[i][j]-colorScale[k-1]))/(colorScale[k]-colorScale[k-1]));
					Color tmpC=new Color(r,g,b);
					graph.setColor(tmpC);
					graph.fillRect(j, i, 1, 1);
				}
			}
		}
		return res;
	}
}
