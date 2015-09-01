package myPanel;

import java.awt.Font;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextPane;

import PlotTools.Polygon;
import PlotTools.RpaAggregate;
import mainPackage.webView;
import net.miginfocom.swing.MigLayout;

public class myComponent extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<JTextPane> myList;
	protected webView myWebView;
	protected ArrayList<Polygon> precincts;
	protected RpaAggregate rpaAggregate;
	public myComponent(){
		this.setLayout(new MigLayout());
		myList=new ArrayList<JTextPane>();
	}
	
	public myComponent(webView myWebView,ArrayList<Polygon> precincts, RpaAggregate rpaAggregate){
		this.setLayout(new MigLayout());
		myList=new ArrayList<JTextPane>();
		this.myWebView=myWebView;
		this.precincts=precincts;
		this.rpaAggregate=rpaAggregate;
	}
	
	public void addText(String text){
		if(myList.size()>15){
			System.out.println("Too many Text added");
		}else{
			JTextPane jTextPane=new JTextPane();
			this.add(jTextPane,"wrap");
			Font myFont = new Font("Serif", Font.BOLD, 18);
			jTextPane.setFont(myFont);
			jTextPane.setText(text);
			myList.add(jTextPane);
		}
	}
	
	public void removeAllText(){
		for(int i=0;i<myList.size();i++){
			myList.get(i).setVisible(false);
			this.remove(myList.get(i));
		}
		myList.clear();
	}
	
}
