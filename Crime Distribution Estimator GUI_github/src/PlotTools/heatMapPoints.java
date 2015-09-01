package PlotTools;

public class heatMapPoints {
	private double la;
	private double lo;
	private double intensity ;
	public heatMapPoints(double la,double lo, double intensity){
		this.la=la;
		this.lo=lo;
		this.intensity=intensity;
	}
	public double getLatitude(){
		return la;
	}
	public double getLongitude(){
		return lo;
	}
	public double getIntensity(){
		return intensity;
	}
}
