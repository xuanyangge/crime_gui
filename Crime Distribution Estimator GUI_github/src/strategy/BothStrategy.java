package strategy;

import javafx.scene.web.WebEngine;

public class BothStrategy implements SetFocusStrategy{
	@Override
	public void setFocus(WebEngine executeEngine,int focus){
		if(focus<0){
			executeEngine.executeScript("showMarker();");
			executeEngine.executeScript("setFocusRpa("+0+");");
		}else{
			//executeEngine.executeScript("setFocusPrecinct("+focus+");");
			executeEngine.executeScript("setFocusRpa("+focus+");");
			executeEngine.executeScript("setFocusMarker("+focus+");");
		}
	}
}
 