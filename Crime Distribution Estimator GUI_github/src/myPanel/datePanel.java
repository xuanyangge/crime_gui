package myPanel;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import org.python.antlr.PythonParser.return_stmt_return;
import misellanies.*;

public class datePanel {
	private JPanel myPanel;
	private JButton btnSetDatetimeRange;
	private JDatePickerImpl dPick;
	private JDatePickerImpl dPick2;
	//Create a date panel that is used to choose a date range.
	public datePanel(){
		myPanel=new JPanel(new MigLayout("", "[][][][]", "[][]"));
		myPanel.setBorder(new EmptyBorder(10, 10, 10, 10) );
		btnSetDatetimeRange = new JButton("Set date/time range");
		myPanel.add(btnSetDatetimeRange, "cell 0 0"); 
		UtilDateModel model = new UtilDateModel();
		UtilDateModel model2 = new UtilDateModel();
		Date today=new Date();
		long miliSecInDay=86400000;	
		Date tenDaysAgo=new Date(today.getTime()-miliSecInDay*10);
		model.setValue(tenDaysAgo);
		model.setSelected(true);
		model2.setSelected(true);
		JLabel dateLabel=new JLabel("Date: ");
		myPanel.add(dateLabel, "cell 1 0");
		Properties p = new Properties();
		p.put("text.today", "Today");
		p.put("text.month", "Month");
		p.put("text.year", "Year");
		JDatePanelImpl dPan = new JDatePanelImpl(model, p);
		dPick = new JDatePickerImpl(dPan, new DateLabelFormatter());
		myPanel.add(dPick, "cell 1 0");
		JDatePanelImpl dPan2 = new JDatePanelImpl(model2, p);
		dPick2 = new JDatePickerImpl(dPan2,  new DateLabelFormatter());
		myPanel.add(dPick2,"cell 1 0");
		//crimeDistEstimJFrame.getContentPane().add(datePanel,"wrap");
		if (dPick.isVisible()) {
		//	dLabel = /* Convert data from date picker into string & set equal to 'dLabel' */
		}
	}
	
	//Get the panel UI component
	public JPanel getPanel(){
		return myPanel;
	}
	
	//Add the listener to the set time button.
	public void addListener(ActionListener listener){
		btnSetDatetimeRange.addActionListener(listener);
	}
	
	//Start Date.
	public JDatePickerImpl lDate(){
		return dPick;
	}
	//End date.
	public JDatePickerImpl rDate(){
		return dPick2;
	}
	
	
}
