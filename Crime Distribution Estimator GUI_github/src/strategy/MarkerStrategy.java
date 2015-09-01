package strategy;

import javafx.scene.web.WebEngine;

public class MarkerStrategy implements SetFocusStrategy{
	@Override
	public void setFocus(WebEngine executeEngine,int focus){
		if(focus<0){
			executeEngine.executeScript("showMarker();");
		}else{
			//executeEngine.executeScript("setFocusPrecinct("+focus+");");
			executeEngine.executeScript("setFocusMarker("+focus+");");
		}
	}
}
