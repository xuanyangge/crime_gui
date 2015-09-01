package misellanies;

import java.awt.List;
import java.io.File;
import java.util.ArrayList;

import javax.xml.bind.Unmarshaller;

import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;

import de.micromata.opengis.kml.v_2_2_0.Boundary;
import de.micromata.opengis.kml.v_2_2_0.Coordinate;
import de.micromata.opengis.kml.v_2_2_0.Document;
import de.micromata.opengis.kml.v_2_2_0.ExtendedData;
import de.micromata.opengis.kml.v_2_2_0.Feature;
import de.micromata.opengis.kml.v_2_2_0.Folder;
import de.micromata.opengis.kml.v_2_2_0.Geometry;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.MultiGeometry;
import de.micromata.opengis.kml.v_2_2_0.Placemark;
import de.micromata.opengis.kml.v_2_2_0.Polygon;
import de.micromata.opengis.kml.v_2_2_0.SchemaData;
import de.micromata.opengis.kml.v_2_2_0.SimpleData;

public class testParsing {

	public static void testParseRpa(){
		Kml mykml=Kml.unmarshal(new File("resources/rpa/threshold25.kml"));
		Document myFeatures=(Document)mykml.getFeature();
		Folder folder=(Folder) myFeatures.getFeature().get(0);
		ArrayList<Feature> features=(ArrayList<Feature>) folder.getFeature();
		for(int i=0;i<features.size();i++){
			Placemark rpa=(Placemark) features.get(i);
			MultiGeometry geometry=(MultiGeometry)rpa.getGeometry();
			ArrayList<de.micromata.opengis.kml.v_2_2_0.Polygon> polygons=(ArrayList)geometry.getGeometry();
			Boundary b=polygons.get(0).getOuterBoundaryIs();
			ArrayList<Coordinate> coords=(ArrayList<Coordinate>) b.getLinearRing().getCoordinates();
			
			ExtendedData extendedData=rpa.getExtendedData();
			ArrayList<SchemaData> schemaDatas=(ArrayList<SchemaData>) extendedData.getSchemaData();
			ArrayList<SimpleData> simpleData=(ArrayList<SimpleData>) schemaDatas.get(0).getSimpleData();
			int PrecinctNumber=Integer.parseInt(simpleData.get(12).getValue())-1;
			String PrecinctName=simpleData.get(14).getValue();
			System.out.println(PrecinctName);
		}
	}
	
	public static void testParsingPrecinct(){
		Kml mykml=Kml.unmarshal(new File("resources/police/threhold10.kml"));
		Feature myFeature=mykml.getFeature();
		myFeature.getAbstractView();
		//Precinct String at simpleElement 0;
	}
	
	
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		testParseRpa();
	}

}
