package CrimeCase;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

import javafx.application.Platform;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class pastCrime {
	private Map<Long,crimeCase> pastCrimeList;
	/**
	 * Parse from the xmls file the past crimes
	 * @param fileName
	 * @param sheetNumber
	 * @param incidentNumberColumn
	 * @param laColumnNumber
	 * @param loColumnNumber
	 * @param timeColumnNumber
	 * @param typeColumnNumber
	 * @param typeSheetNumber
	 * @throws InvalidFormatException
	 * @throws IOException
	 */
	public pastCrime(String fileName, int sheetNumber, int incidentNumberColumn,int laColumnNumber, 
			int loColumnNumber, int timeColumnNumber,int typeColumnNumber,int typeSheetNumber) throws InvalidFormatException, IOException{
		File myFile=new File(fileName);
		FileInputStream fis = new FileInputStream(myFile);
		// Finds the workbook instance for XLSX file
        XSSFWorkbook myWorkBook = new XSSFWorkbook (fis);
       
        // Return first sheet from the XLSX workbook
        XSSFSheet mySheet = myWorkBook.getSheetAt(sheetNumber);
        XSSFSheet typeSheet=myWorkBook.getSheetAt(typeSheetNumber);
        // Get iterator to all the rows in current sheet
        Iterator<Row> it=mySheet.iterator(),itS=typeSheet.iterator();
        //Consume the first row
        it.next();
        itS.next();
        //A map of <incident number, crimCase>
        Map<Long,crimeCase> res = new TreeMap<Long,crimeCase>();
        //Read crime information into the map
        while(it.hasNext()){
        	Row cur=it.next();
      		long inci=(long) cur.getCell(incidentNumberColumn).getNumericCellValue();
        	if(cur.getCell(laColumnNumber)==null||
        			cur.getCell(loColumnNumber)==null||cur.getCell(timeColumnNumber)==null){
        		if(cur.getCell(incidentNumberColumn)!=null){
            		//System.out.print("Incident " +cur.getCell(incidentNumberColumn).getNumericCellValue()+"'s information is not complete ");	
        		}
        	}else{
        		double la=cur.getCell(laColumnNumber).getNumericCellValue();
        		double lo=cur.getCell(loColumnNumber).getNumericCellValue();
        		String time=cur.getCell(timeColumnNumber).getStringCellValue();
        		crimeCase crime=new crimeCase(inci,time,la,lo);
        		res.put(inci, crime);
        	}
        }
        //Read crime type information, if the crime type's incident number matches the crime's incident number
        //add the type to the crime and add the crime to the map.
        //This is because the file is inconsistent.
    	Row curS=itS.next();
		while(itS.hasNext()){
			if(curS.getCell(typeColumnNumber)!=null){
				long inci=(long)curS.getCell(incidentNumberColumn).getNumericCellValue();
    			String s=curS.getCell(typeColumnNumber).getStringCellValue();
    			if(res.containsKey(inci)){
    				crimeCase crime=res.get(inci);
    				crime.addType(s);
    			}
			}
			curS=itS.next();
		}
        pastCrimeList=res;
        myWorkBook.close();
	}
	
	/**
	 * Pass in a map of crimeList
	 * @param crimeList
	 */
	public pastCrime(Map<Long,crimeCase> crimeList){
		this.pastCrimeList=crimeList;
	}
	
	public Map<Long,crimeCase> getList(){
		return pastCrimeList;
	}
	/**
	 * Filter the past crime list by the bound of date range. 
	 * @param leftB
	 * @param rightB
	 * @param input
	 * @return map of crimes, <incident number, crime>
	 */
	public static Map<Long,crimeCase> timeFilter(Date leftB,Date rightB,TreeMap<Long,crimeCase> input){
		return input.subMap(leftB.getTime(), rightB.getTime());
	}
	
	/**
	 * Filter the past crime list by the bound of latitudes,longitudes. 
	 * @param leftB
	 * @param rightB
	 * @param input
	 * @return map of crimes, <incident number, crime>
	 */
	public static Map<Long,crimeCase> spaceFilter(double lLa,double rLa,double uLo,double dLo,Map<Long,crimeCase> input){
		Map<Long,crimeCase> res=new TreeMap<Long,crimeCase>();
		for(crimeCase c: input.values()){
			boolean check=c.getLa()>=lLa&&c.getLa()<=rLa&&c.getlo()>dLo&&c.getlo()<uLo;
			if(check){
				res.put(c.getTime().getTime(), c);
			}
		}
		return res;
	}
	
	/**
	 * @param Map of crimes, <incident number, crime>
	 * @return Map of crimes of all the burglary type, <incident number, crime>
	 */
	public static TreeMap<Long, crimeCase> burglaryFilter(Map<Long,crimeCase> input){
		TreeMap<Long,crimeCase> res=new TreeMap<Long,crimeCase>();
		for(crimeCase c: input.values()){
			boolean check=false;
			ArrayList<String> type=c.getTypeArr();
			for(String s:type){
				if(s.equals("Burglary/Breaking & Entering")){
					check=true;
				}
			}
			if(check){
				System.out.println(c.getTime().getTime());
				res.put(c.getTime().getTime(), c);
			}
		}
		return res;
	}
	
	
	/**
	 * 
	 * @param file
	 * @return A tree map of <Time,crimecases>.
	 * @throws FileNotFoundException
	 */
	public static TreeMap<Long, crimeCase> burglaryCases(String file) throws FileNotFoundException{
		Scanner in =new Scanner(new File(file));
		TreeMap<Long, crimeCase> res=new TreeMap<Long, crimeCase>();
		while(in.hasNextLine()){
			Scanner line=new Scanner(in.nextLine());
			long inci=line.nextLong();
			double la=line.nextDouble(),lo=line.nextDouble();
			long time=line.nextLong();
			String precinct=line.next();
			crimeCase c=new crimeCase(inci, time, la, lo);
			c.setPrecinct(precinct);
			c.addType("burglary");
			res.put(c.getTime().getTime(), c);
			line.close();
		}
		in.close();
		return res;
	}
}
