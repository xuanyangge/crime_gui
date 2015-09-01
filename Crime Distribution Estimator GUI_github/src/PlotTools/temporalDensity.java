package PlotTools;
import java.util.ArrayList;
import java.util.Random;

import org.python.antlr.PythonParser.return_stmt_return;
import org.apache.commons.math3.special.Gamma;

public class temporalDensity {
	private double[] b;
	private double a;
	private int numOfB;
	private double lamda;
	private long timeInterval;
	
	public temporalDensity(double a,double[] b,int numberOfCoefficient,long timeInterval){
		this.a=a;
		this.b=b;
		numOfB=numberOfCoefficient;
		lamda=0;
		this.timeInterval=timeInterval;
	}
	
	public double updateLamda(int[] x,double interception){
		lamda=0;
		lamda+=interception;
		for(int i=0;i<numOfB;i++){
			lamda+=b[i]*x[i];
		}
		lamda=Math.exp(lamda);
		return lamda;
	}
	
	public int getNumOfCoefficient(){
		return numOfB;
	}
	
	
	//Output from weibull function has the unit minute
	public ArrayList<Long> generateRandom(){
		Random r=new Random(System.nanoTime());
		long time=0;
		ArrayList<Long> res=new  ArrayList<Long>();
		long t=(int)(1000*60*lamda*Math.pow(-1*Math.log(r.nextDouble()),1/a));
		time+=t;
		while(time<=4*3600*1000){
			res.add(t);
			t=(int)(1000*60*lamda*Math.pow(-1*Math.log(r.nextDouble()),1/a));
			time+=t;
		}
		return res;
	}
	
	
	
	/*
	 * Time is in minutes, get expected amount of crime in four hours 
	 */
	public double getAvg(int time){
		double mean=lamda*Gamma.gamma(1+1/a);
		return (double)time/mean;
	}
	
	
	//Time in minutes
	public double getProb(double time){
		double p=(a/lamda)*Math.pow(time/lamda, a-1)*Math.exp(Math.pow(-1*time/lamda,a));
		return p;
	}
}
