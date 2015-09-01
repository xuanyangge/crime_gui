package PlotTools;

import java.awt.geom.Point2D;


public class areaIdentifier {
	private final static double PRECISION=Math.pow(10, -9);
	private static class pd{
		public double x,y;
		public pd(double la, double lo){
			this.x=la;
			this.y=lo;
		}
	}
	
	//helper method,calculating cross product
	private static double cross(pd a,pd b, pd c){
		return (b.x - a.x)*(c.y - a.y) - (b.y - a.y)*(c.x - a.x);
	}
	
	
	private static int dblcmp(double d){
		if (Math.abs(d) < PRECISION)return 0;
		return d > 0 ? 1 : -1;
	}
	
	//helper method calculating intersection,1 is two lines intersect, 0 is two lines does not
	private static int intersect(pd a, pd b, pd c, pd d){
		int d1,d2,d3,d4;
		d1=dblcmp(cross(a, b, c));
		d2=dblcmp(cross(a,b,d));
		d3=dblcmp(cross(c, d, a));
		d4=dblcmp(cross(c, d, b));
		if((d1^d2)==-2&&(d3^d4)==-2){
			return 1;
		}
		return 0;
	}
	
	/**
	 * Determine if a point is in a certain polygon. If the polygon is not defined or its latlnglist is not defined,
	 * the method return true.
	 * @param p polygon
	 * @param point
	 * @return
	 */
	public static boolean isInArea(Polygon p, Point2D.Double point){
		if(p.latlngArrayList.size()==0||p==null||p.latlngArrayList==null){
			return true;
		}
		int count=0;
		pd a=new pd(point.x,point.y);
		pd b=new pd(point.x,point.y+50);
		for(int i=0;i<p.latlngArrayList.size()-1;i++){
			Point2D.Double pointC=p.latlngArrayList.get(i),pointD=p.latlngArrayList.get(i+1);
			pd c=new pd(pointC.x,pointC.y);
			pd d=new pd(pointD.x, pointD.y);
			if(intersect(a, b, c, d)==1)count++;
		}
		if(count%2==1){
			return true;
		}
		return false;
	}
	
	/**
	 * Determine if a point is in a certain polygon. If the polygon is not defined or its latlnglist is not defined,
	 * the method return true.
	 * Rpa polygon must define its leftmost,rightmost,topmost and bottommost coordination.
	 * @param p rpaPolygon
	 * @param point
	 * @return
	 */
	public static boolean isInArea(rpaPolygon p, Point2D.Double point){
		if(p.latlngArrayList.size()==0||p==null||p.latlngArrayList==null){
			return true;
		}
		if(p.maxX>=point.x&&p.minX<=point.x&&p.minY<=point.y&&p.maxY>=point.y){
			int count=0;		
			pd a=new pd(point.x,point.y);
			pd b=new pd(point.x,point.y+50);
			for(int i=0;i<p.latlngArrayList.size()-1;i++){
				Point2D.Double pointC=p.latlngArrayList.get(i),pointD=p.latlngArrayList.get(i+1);
				pd c=new pd(pointC.x,pointC.y);
				pd d=new pd(pointD.x, pointD.y);
				if(intersect(a, b, c, d)==1)count++;
			}
			if(count%2==1){
				return true;
			}
		}
		return false;
	}
	
}
