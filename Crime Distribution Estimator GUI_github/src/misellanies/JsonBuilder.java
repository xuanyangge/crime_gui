package misellanies;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import mainPackage.webView;

import org.apache.commons.math3.stat.correlation.Covariance;
import org.json.JSONWriter;
import org.json.simple.JSONObject;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.opencv.core.Mat;
import org.opencv.ml.EM;

import PlotTools.gausianMixture;

public class JsonBuilder {
	public JsonBuilder(){
	}
	
	@SuppressWarnings("unchecked")
	public void buildGaussianModelJson(gausianMixture mGausianMixture) throws ParseException{
		EM model=mGausianMixture.getModel();
		Mat mean=model.getMat("means");
		int clusters=mGausianMixture.getNClusters();
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("clusters", clusters);
		LinkedList<Double> xMean=new LinkedList<Double>(),yMean= new LinkedList<Double>(),weightLinkedList=new LinkedList<Double>();
		LinkedList<LinkedList<Double>> mat= new LinkedList<LinkedList<Double>>();
		double[] meanArr=new double[2];
		double[] covArr=new double[4];
		double[] weightArr=new double[clusters];
		Mat weight=model.getMat("weights");
		weight.get(0, 0, weightArr);
		for (int i = 0; i < clusters; i++) {
			mean.get(i, 0, meanArr);
			xMean.add(meanArr[0]);
			yMean.add(meanArr[1]);
			weightLinkedList.add(weightArr[i]);
			model.getMatVector("covs").get(i).get(0,0,covArr);
			LinkedList<Double> tmp=new LinkedList<Double>();
			for (int j = 0; j < 4; j++) {
				tmp.add(covArr[j]);
			}
			mat.add(tmp);
		}
		if(mGausianMixture.getType()!=null){
			jsonObject.put("Type", mGausianMixture.getType());
		};
		jsonObject.put("xMean", xMean);
		jsonObject.put("yMean", yMean);
		jsonObject.put("covs", mat);
		jsonObject.put("weight", weightLinkedList);
		System.out.print(jsonObject.toJSONString());
	}
}
