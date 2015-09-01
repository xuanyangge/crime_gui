package misellanies;
import java.awt.AWTException;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File; //
import java.io.IOException; //
import java.util.Optional; //

import javax.imageio.ImageIO; //
import javax.swing.JFrame; //
import javax.swing.JMenuBar; //
import javax.swing.JOptionPane; //
import javax.swing.filechooser.FileFilter; //
import javax.swing.filechooser.FileNameExtensionFilter; //

import org.apache.poi.openxml4j.exceptions.InvalidFormatException; //

import javafx.scene.Scene;

public class saveMap {
	public BufferedImage capture=null;


	//Class cdeIClass = crimeDist_init.newInstance();
	
	public saveMap() throws InvalidFormatException, IOException /* throws exceptions to account for those thrown in 'crimeDist_init' */{
		// TODO Auto-generated constructor stub
	}
/*	public BufferedImage save() {
		try {
			Robot robot = new Robot();
			   
		
			int JFX = crimeDist_init.getJFX();
			int JFY = crimeDist_init.getJFY();
			int MBH = crimeDist_init.getMBHeight();
			int BW = crimeDist_init.getBW();
			int BH = crimeDist_init.getBH();
			
			// JOptionPane.showMessageDialog(cdeInit.crimeDistEstimJFrame.getContentPane().toolBar.tabbedPane, crimeDist_init.tpSize);
			// first parameter is 'JFX+1' to account for the side bar
			// add-int: If JFrame is not fullscreen then add 1 pixel to JFX.
			
			
			
			Rectangle screenRect = new Rectangle(JFX+1, JFY+MBH, BW, BH);
			Rectangle d=new Rectangle();
			capture = robot.createScreenCapture(screenRect);
			}catch (AWTException e){
				e.printStackTrace();
		}
		return capture;
	}
*/	
	public void saveBrow(File fileName, FileFilter ffilter, JFrame frame, JMenuBar menuBar, Scene scene) {
		final String lastExt;
		if (ffilter.getDescription() == "PNG Image") {
			lastExt = "png";
		}else if (ffilter.getDescription() == "BMP Image") {
			lastExt = "bmp";
		}else /* ffilter.getDescription must == "JPG Image" */ {
			lastExt = "jpg";
		}
		System.out.println(ffilter.getDescription());
		System.out.println(lastExt);
       try {          
    	  // Point jfPoint = new Point(0,0);
    	   Point mbPoint = new Point(0,0);
   			//jfPoint = frame.getLocationOnScreen();
   			mbPoint = menuBar.getLocationOnScreen();
   			//System.out.println(jfPoint.toString());
   			//System.out.println(mbPoint.toString());
   			//int JFX = jfPoint.x;
   			//int JFY = jfPoint.y;
   			int MBX = mbPoint.x;
   			int MBY = mbPoint.y;
   			//int frameBarDiff = MBX - JFX;
   			//int SX = (int) scene.getX();
   			//int SY = (int) scene.getY();
   			//System.out.println(SX + "," + SY);
   			//System.out.println(JFX);
   			//System.out.println(JFY);
   			int MBH = menuBar.getHeight();
   			//int MBW = menuBar.getWidth();
   			int BH = (int) scene.getHeight();
   			int BW = (int) scene.getWidth();
   			String inMessage = "Are you sure you want to save your browser?";
   			int delay = 1;
    	   //int JFX = method.getJFX();
			//int JFY = cdeI.getJFY();
			//int BW = cdeI.getBW();
			//int BH = cdeI.getBH();
		
			//JOptionPane.showMessageDialog(cdeInit.crimeDistEstimJFrame.getContentPane().toolBar.tabbedPane, crimeDist_init.tpSize);
			// first parameter is 'JFX+1' to account for the side bar
			// add-int: If JFrame is not fullscreen then add 1 pixel to JFX.
			
			
			
			//Rectangle screenRect = new Rectangle(JFX+1, JFY+MBH, BW, BH);
			//Rectangle d=new Rectangle();
			//capture = robot.createScreenCapture(screenRect);
    	   
			long time = Long.valueOf(delay) * 1000L;
   			
			//Alert alert = new Alert(AlertType.CONFIRMATION, "Are you sure you want to save your browser?", ButtonType.YES, ButtonType.NO);
			int option = JOptionPane.showConfirmDialog(frame, inMessage, "Confirm save?", JOptionPane.YES_NO_OPTION);
			//Optional<ButtonType> result = alert.showAndWait();
			if (option == JOptionPane.YES_OPTION) {
        	    //System.out.println("Creating a delay of " + delay + " second(s)...");
				Thread.sleep(time);
				Rectangle screenRect = new Rectangle(MBX, MBY+MBH, BW+10, BH+10);
				capture = new Robot().createScreenCapture(screenRect);
				RenderedImage im = (RenderedImage) capture;
				//System.out.println(fileName);
				ImageIO.write(im, "bmp", fileName);
			}       
			
       } catch (InterruptedException | HeadlessException | IOException | AWTException ex) {
           System.out.println("Exception occurred: " + ex.getMessage());
           	
       }
	 
	   }
}
