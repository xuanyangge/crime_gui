package misellanies;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Map;

import de.micromata.opengis.kml.v_2_2_0.Boundary;
import de.micromata.opengis.kml.v_2_2_0.Coordinate;
import de.micromata.opengis.kml.v_2_2_0.Document;
import de.micromata.opengis.kml.v_2_2_0.ExtendedData;
import de.micromata.opengis.kml.v_2_2_0.Feature;
import de.micromata.opengis.kml.v_2_2_0.Folder;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.MultiGeometry;
import de.micromata.opengis.kml.v_2_2_0.Placemark;
import de.micromata.opengis.kml.v_2_2_0.SchemaData;
import de.micromata.opengis.kml.v_2_2_0.SimpleData;
import PlotTools.Polygon;
import PlotTools.rpaPolygon;

public class kmlParser {
	private static kmlParser instance=null;
	private static final String precinctFile="resources/police/threshold10.kml";
	private static final String RPAFILE_STRING="resources/rpa/threshold25.kml";
	
	
	private kmlParser(){	
	}
	
	public static kmlParser getInstance() throws FileNotFoundException{
		if(instance==null){
			instance=new kmlParser();
		}
		return instance;
	}
	/**
	 * Parse precincts from xml file.
	 * @return An arraylist of polygons.
	 */
	public static ArrayList<Polygon> getPrecinct(){
		Kml mykml=Kml.unmarshal(new File(precinctFile));
		Document myFeatures=(Document)mykml.getFeature();
		Folder folder=(Folder) myFeatures.getFeature().get(0);
		ArrayList<Feature> features=(ArrayList<Feature>) folder.getFeature();
		ArrayList<Polygon> polygonList=new ArrayList<Polygon>();
		for(int i=0;i<features.size();i++){
			Placemark rpa=(Placemark) features.get(i);
			MultiGeometry geometry=(MultiGeometry)rpa.getGeometry();
			@SuppressWarnings({ "rawtypes", "unchecked" })
			ArrayList<de.micromata.opengis.kml.v_2_2_0.Polygon> polygons=(ArrayList)geometry.getGeometry();
			Boundary b=polygons.get(0).getOuterBoundaryIs();
			ArrayList<Coordinate> coords=(ArrayList<Coordinate>) b.getLinearRing().getCoordinates();
			ArrayList<Point2D.Double> latLngList=new ArrayList<Point2D.Double>();
			for (int j = 0; j <coords.size(); j++) {
				latLngList.add(new Point2D.Double(coords.get(j).getLatitude(),coords.get(j).getLongitude()));
			}
			ExtendedData extendedData=rpa.getExtendedData();
			ArrayList<SchemaData> schemaDatas=(ArrayList<SchemaData>) extendedData.getSchemaData();
			ArrayList<SimpleData> simpleData=(ArrayList<SimpleData>) schemaDatas.get(0).getSimpleData();
			String PrecinctName=simpleData.get(0).getValue();
			PrecinctName=PrecinctName.toUpperCase().replaceAll("[^a-zA-Z]", "");
			Polygon polygon=new Polygon(PrecinctName,latLngList);
			polygonList.add(polygon);
		}
		return polygonList;
	}
	/**
	 * Parse rpas from xml file.
	 * @return An arraylist of polygons.
	 */
	public static ArrayList<rpaPolygon> getRPAs(){
		Kml mykml=Kml.unmarshal(new File(RPAFILE_STRING));
		Document myFeatures=(Document)mykml.getFeature();
		Folder folder=(Folder) myFeatures.getFeature().get(0);
		ArrayList<Feature> features=(ArrayList<Feature>) folder.getFeature();
		ArrayList<rpaPolygon> polygonList=new ArrayList<rpaPolygon>();
		for(int i=0;i<features.size();i++){
			Placemark rpa=(Placemark) features.get(i);
			MultiGeometry geometry=(MultiGeometry)rpa.getGeometry();
			@SuppressWarnings({ "rawtypes", "unchecked" })
			ArrayList<de.micromata.opengis.kml.v_2_2_0.Polygon> polygons=(ArrayList)geometry.getGeometry();
			Boundary b=polygons.get(0).getOuterBoundaryIs();
			ArrayList<Coordinate> coords=(ArrayList<Coordinate>) b.getLinearRing().getCoordinates();
			ArrayList<Point2D.Double> latLngList=new ArrayList<Point2D.Double>();
			for (int j = 0; j <coords.size(); j++) {
				latLngList.add(new Point2D.Double(coords.get(j).getLatitude(),coords.get(j).getLongitude()));
			}
			ExtendedData extendedData=rpa.getExtendedData();
			ArrayList<SchemaData> schemaDatas=(ArrayList<SchemaData>) extendedData.getSchemaData();
			ArrayList<SimpleData> simpleData=(ArrayList<SimpleData>) schemaDatas.get(0).getSimpleData();
			String PrecinctName=simpleData.get(14).getValue();
			PrecinctName=PrecinctName.toUpperCase().replaceAll("[^a-zA-Z]", "");
			String idString=simpleData.get(0).getValue();
			String trafficString=simpleData.get(4).getValue();
			if(trafficString.equals("TRAFFIC RPA"))continue;
			rpaPolygon polygon=new rpaPolygon(idString,latLngList,PrecinctName);
			polygonList.add(polygon);
		}
		return polygonList;
	}
	
}
