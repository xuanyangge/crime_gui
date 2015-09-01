import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.awt.*;
import java.awt.print.*;
import java.awt.Robot;
import java.awt.image.*;


public class printSetup implements Printable {

	public static void prelim() {
		// TODO Auto-generated constructor stub
		saveMap s2map = new saveMap();
		s2map.save();
	//	capture; /* FIX !!! */
		PrinterJob prtJob = PrinterJob.getPrinterJob();
		prtJob.setPrintable(new printSetup());
		boolean doPrint = prtJob.printDialog();
				if (doPrint) {
					try {
						prtJob.print();
					}catch (PrinterException e){
						// The job did not successfully complete
					}
				}
	}

	@Override
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
			throws PrinterException {
		// TODO Auto-generated method stub
		// We have only one page, and 'page'
	    // is zero-based
	    if (pageIndex > 0) {
	         return NO_SUCH_PAGE;
	    }
	 // User (0,0) is typically outside the
	    // imageable area, so we must translate
	    // by the X and Y values in the PageFormat
	    // to avoid clipping.
	    Graphics2D g2d = (Graphics2D) graphics;
	    g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

	    // Now we perform our rendering
	    graphics.drawString("Hello world!", 100, 100);

	    // tell the caller that this page is part
	    // of the printed document
	    return PAGE_EXISTS;
	}

}
