package PlotTools;

public class bounds {
	private double n,e,s,w;
	//Create a bound with latitude and longitude as n,e,s,w
	public bounds(double n,double e,double s,double w){
		this.n=n;
		this.e=e;
		this.s=s;
		this.w=w;
	}
	
	public double getN(){
		return n;
	}
	public double getE(){
		return e;
	}
	public double getW(){
		return w;
	}
	public double getS(){
		return s;
	}
	
	public void print(){
		System.out.println(n+" "+e+" "+w+" "+s);
	}
}
