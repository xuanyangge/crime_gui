package strategy;

import javafx.scene.web.WebEngine;

public class RpaStrategy implements SetFocusStrategy{
	@Override
	public void setFocus(WebEngine executeEngine,int focus){
		if(focus<0){
			executeEngine.executeScript("setFocusRpa("+0+");");
		}else{
			//executeEngine.executeScript("setFocusPrecinct("+focus+");");	
			executeEngine.executeScript("setFocusRpa("+focus+");");
		}
	}
}
