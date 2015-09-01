 package PlotTools;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import org.apache.commons.math3.analysis.function.Gaussian;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.TermCriteria;
import org.opencv.ml.EM;
import org.python.antlr.PythonParser.return_stmt_return;

import CrimeCase.crimeCase;

import com.ziclix.python.sql.handler.UpdateCountDataHandler;


public class gausianMixture {
	private EM myModel;
	private int nClusters;
	private String mType=null;
	//ax^2+bxy+cy^2
	private double a[],b[],c[];
	//Major axis length and minor axis length
	private double major[],minor[];
	//Rotating of the ellipse
	private double rotatingAngle[];
	/**
	 * Get a model
	 * @param sample is a n*2 matrix, M_n_0 is lat, M_n_1 is lng, maxcount defines the max number of iteration, type defines type of crime
	 */
	public gausianMixture(Mat sample,int nClusters,int maxcount, String type){
		this.nClusters=nClusters;
		TermCriteria criteria=new TermCriteria();
		criteria.maxCount=maxcount;
		myModel=new EM(nClusters,EM.COV_MAT_GENERIC,criteria);
		myModel.train(sample);
		getParameters();
		this.mType=type;
	}
	
	/**
	 * Get a model
	 * @param jsonFile jsonFile that contains the parameters and information about the model.
	 */
	public gausianMixture(String jsonFile){
		TermCriteria criteria=new TermCriteria();
		myModel=new EM(nClusters,EM.COV_MAT_GENERIC,criteria);
		
	}
	
	
	/**
	 * Some math calculations to get the eclipse.
	 */
	private void getParameters(){
		a=new double[nClusters];b=new double[nClusters];c=new double[nClusters];
		major=new double[nClusters];rotatingAngle=new double[nClusters];minor=new double[nClusters];
		for(int i=0;i<nClusters;i++){
			Mat cov=myModel.getMatVector("covs").get(i).inv();
			double[] covArr=new double[4];
			cov.get(0, 0, covArr);
			a[i]=covArr[0];
			b[i]=covArr[1];
			c[i]=covArr[3];
			if(a[i]<c[i]){
				rotatingAngle[i]=0.5*(Math.PI/2-Math.atan((a[i]-c[i])/2/b[i]));
			}else{
				rotatingAngle[i]=0.5*(Math.PI/2-Math.atan((a[i]-c[i])/2/b[i]))+Math.PI/2;
			}
			double majorS=-2.0/(Math.sqrt((a[i]-c[i])*(a[i]-c[i])+4*b[i]*b[i])-(a[i]+c[i]));
			double minorS=-2.0/(-1*Math.sqrt((a[i]-c[i])*(a[i]-c[i])+4*b[i]*b[i])-(a[i]+c[i]));
			major[i]=Math.sqrt(majorS);
			minor[i]=Math.sqrt(minorS);
		}
	}
	
	/*
	 * Generate a random point based on the probability distribution.
	 */
	public Point2D.Double generatePoint(){
		Random r=new Random(System.nanoTime());
		Mat weight=myModel.getMat("weights");
		int choice=-1;
		double left=0,right=0;
		double rp=r.nextDouble();
		double[] weightArr=new double[nClusters];
		weight.get(0, 0, weightArr);
		for(int i=0;i<nClusters;i++){
			left=right;
			right+=weightArr[i];
			if(rp>left&&rp<right){
				choice=i;
				break;
			}
		}
		if(choice==-1){
			System.out.println("Something wrong with the weight");
			return null;
		}else{
			double x1=r.nextGaussian();
			double x2=r.nextGaussian();
			//Apply a linear transformation based on covariance matrix from two 
			//independent gaussian variable x1,x2 to covariate gaussian (x,y)
			x1=x1*major[choice];
			x2=x2*minor[choice];
			double cos=Math.cos(rotatingAngle[choice]);
			double sin=Math.sin(rotatingAngle[choice]);
			double x=x1*cos+x2*sin;
			double y=-1*x1*sin+x2*cos;
			Mat mean=myModel.getMat("means");
			double[] meanArr=new double[2];
			mean.get(choice, 0, meanArr);
			x+=meanArr[0];
			y+=meanArr[1];
			Point2D.Double res=new Point2D.Double(x,y);
			return res;
		}
	}
	
	/**
	 * Generate a list of crimes with "random" position.
	 * @param number of crimes to be generated
	 * @return
	 */
	public ArrayList<crimeCase> generateCrimes(int number){
		ArrayList<crimeCase> res=new ArrayList<crimeCase>();
		for(int i=0;i<number;i++){
			//Generate a point
		 	Point2D.Double simuP=generatePoint();
		 	//Create a crime case
		 	crimeCase simu=new crimeCase(new Date(),simuP.x , simuP.y);
		 	res.add(simu);
		 	simu.addType("simulated");
		}
	 	return res;
	}
	
	
	public EM getModel(){
		return myModel;
	}
	
	/**
	 * Number of gaussian centers.
	 * @return
	 */
	public int getNClusters(){
		return nClusters;
	}
	
	/**
	 * Get the ellipse with index i.
	 * @param i
	 * @return
	 */
	public ArrayList<Point2D.Double> getEllipse(int i){
		ArrayList<Point2D.Double> res=new ArrayList<Point2D.Double>();
		double x1=major[i];
		double x2=minor[i];
		for(int j=1;j<=360;j++){
			double tmpX=x1*Math.cos(j*Math.PI/180);
			double tmpY=x2*Math.sin(j*Math.PI/180);
			double cos=Math.cos(rotatingAngle[i]);
			double sin=Math.sin(rotatingAngle[i]);
			double x=tmpX*cos+tmpY*sin;
			double y=-1*tmpX*sin+tmpY*cos;
			Mat mean=myModel.getMat("means");
			double[] meanArr=new double[2];
			mean.get(i, 0, meanArr);
			x+=meanArr[0];
			y+=meanArr[1];
			res.add(new Point2D.Double(x,y));
		}
		return res;	
	}
	
	public String getType(){
		return mType;
	}
}
