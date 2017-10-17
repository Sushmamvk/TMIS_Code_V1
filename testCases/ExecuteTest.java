package testCases;

import java.util.Properties;

import operation.ReadObject;
import operation.UIOperation;
import excelExportAndFileIO.ReadInputExcelFile;

import java.sql.Connection;
import java.sql.DriverManager;
//import java.sql.SQLException;
import java.sql.SQLException;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class ExecuteTest {
	/**
     * <p>Main Method for TMIS Automation.
     * <p>Steps:
     * <ul><li>1. Initializes the Variables
     * <li>2. Launches Chrome Browser
     * <li>3. Loads Object Repository
     * <li>4. Establish the connection to MS Access DB
     * <li>5. Read Master sheet for processing
     * <li>6. Loops the Master sheet for the scenarios to be automated</ul>
     */
	public void testLogin() throws Exception {		
		Connection conn;
		conn=null;
		int rowCount, curRow, gblRowCount, subRowCount, gblCurRow, subCurRow;
		String URL_StrValue, outputDB_Type;
		System.out.println("TMIS Dashboard Automation verification has started...");
		URL_StrValue = "";
		outputDB_Type = "";
		
		System.out.println("Launching Browser...");
		// Launches Chrome Browser
		ChromeOptions chrmopts = new ChromeOptions();
		chrmopts.addArguments("--start-maximized");
		System.setProperty("webdriver.chrome.driver", (System.getProperty("user.dir")+"\\src\\driver\\chromedriver_win32\\chromedriver.exe"));
		WebDriver webdriver=new ChromeDriver(chrmopts);		
		
		// Object for input excel sheet that drives the flow
		ReadInputExcelFile file = new ReadInputExcelFile();
        System.out.println("Excel loaded...");
        
        // Read and load the objects from the repository
        ReadObject object = new ReadObject();
        Properties allObjects =  object.getObjectRepository();
        System.out.println("Object Repository loaded...");        
        
        //operation.startRecording();
        
        // Read Master sheet for processing
        System.out.println("user directory: "+System.getProperty("user.dir"));
        Sheet masterSheet = file.readExcel(System.getProperty("user.dir")+"\\test-input\\","TMIS-MasterSheet.xlsx" , "ExecuteScenario");
        Sheet globalSheet = file.readExcel(System.getProperty("user.dir")+"\\test-input\\","TMIS-MasterSheet.xlsx" , "Global_Settings");
        
        System.out.println("Fetching values from the Global Sheet");
        // Loop over all the rows to find out the Global Setting Values
        gblRowCount = globalSheet.getLastRowNum()-globalSheet.getFirstRowNum();
		// Create a loop over all the rows of excel file to read it
    	for (gblCurRow = 1; gblCurRow < gblRowCount+1; gblCurRow++)
    	{
    		Row gblRowNum = globalSheet.getRow(gblCurRow);
    		// Output DB Settings
    		if(gblRowNum.getCell(0).toString().contentEquals("Output_DB"))
    		{
    			outputDB_Type = gblRowNum.getCell(1).toString();
    			System.out.println("output DB Type: " +outputDB_Type);
    			
//    			switch(outputDB_Type)
//    			{
//    			case "MS Access":
//    				
//    				// Establish the connection to MS Access DB
//    		        try{
//    		        conn=DriverManager.getConnection("jdbc:ucanaccess:"+"//test-output//DB//POCDB.accdb");
//    		        System.out.println("DB Connection Established");
//    		        }
//    		        catch (Exception e){
//    		        	System.out.println("JDBC exception..."+e.getMessage());
//    		        } 
//    		        break;
//    		        
//    			case "MySQL":
    				
    				    String databaseURL = "jdbc:mysql://localhost:3306/TMIS";
    			        String user = "root";
    			        String password = "root";
    			        conn = null;
    			        try {
    			            Class.forName("com.mysql.jdbc.Driver");
    			            System.out.println("Connecting to Database...MYSQL");
    			            conn = DriverManager.getConnection(databaseURL, user, password);
    			            if (conn != null) {
    			                System.out.println("Connected to the Database...MYSQL");
    			            }
    			        } catch (SQLException ex) {
    			           ex.printStackTrace();
    			        }
//    			        break;
    			
//    			}    			
    		}
    		// URL Settings
    		if(gblRowNum.getCell(0).toString().contentEquals("URL-Env") && gblRowNum.getCell(1).toString().isEmpty()==false)
    		{	
        		URL_StrValue = gblRowNum.getCell(1).toString();
    		}
    	}        
        
        //Calling UIOperation
        UIOperation operation = new UIOperation(webdriver, conn);
        
        // Loops the Master sheet for the scenarios to be automated
    	rowCount = masterSheet.getLastRowNum()-masterSheet.getFirstRowNum();
    	for (curRow = 1; curRow < rowCount+1; curRow++)
    	{
    		// Loop over all the rows
    		Row rowNum = masterSheet.getRow(curRow);
    		// Check if the Execute column contains Y, then need to process the scenario
    		if(rowNum.getCell(1).toString().contentEquals("Y"))
    		{
    			switch(rowNum.getCell(0).toString())
	    		{
	    		case "TMIS_Login":
	    			operation.initSetup(allObjects, URL_StrValue, outputDB_Type);                  
                    break;
                case "Monthly-OverviewPage":
                    System.out.println("Monthly-OverView");
                    Sheet inputSheet1 = file.readExcel(System.getProperty("user.dir")+"\\test-input\\","TMIS-MasterSheet.xlsx" , rowNum.getCell(0).toString());
                    subRowCount = inputSheet1.getLastRowNum()-inputSheet1.getFirstRowNum();
//                    // Loop over all the rows to select the months
        	    	for (subCurRow = 1; subCurRow < subRowCount+1; subCurRow++)
        	    	{
        	    		Row subRowNum = inputSheet1.getRow(subCurRow);
        	    		operation.perform_MonthlyOverview(allObjects, subRowNum.getCell(0).toString(),outputDB_Type);
        	    	}     				
                    break;
                case "Monthly-L2Page":
                	System.out.println("Monthly-L2Page");
                    Sheet inputSheet2 = file.readExcel(System.getProperty("user.dir")+"\\test-input\\","TMIS-MasterSheet.xlsx" , rowNum.getCell(0).toString());
                    subRowCount = inputSheet2.getLastRowNum()-inputSheet2.getFirstRowNum();
                    // Loop over all the rows to select the months
        	    	for (subCurRow = 1; subCurRow < subRowCount+1; subCurRow++)
        	    	{
        	    		Row subRowNum = inputSheet2.getRow(subCurRow);
        	    		operation.perform_MonthlyInnerView(allObjects, subRowNum.getCell(0).toString(), outputDB_Type);
        	    	}
                    break;
                case "Daily-OverviewPage":
                    System.out.println("Daily-OverviewPage");
                    break;
                case "Daily-L2Page":
                    System.out.println("Daily-L2Page");
                    break;
                case "TMIS_Logout":
                	System.out.println("TMIS_Logout");
                	operation.reportWrapUp();
                	webdriver.quit();
                	System.out.println("Automation Execution Completed.");                	
                	//operation.stopRecording();
                	break;
                default:
                    System.out.println("Invalid Selection of Scenario!");
                    break;
	    		}
    		}
    	}
    	}
    	
    	
	
	/**
     * <p>Main Method. Execution starts here.
     */
	public static void main(String args[]) throws Exception
	{
		try {
			ExecuteTest exec = new ExecuteTest();
			exec.testLogin();
		}
		catch(Exception e)
		{
			System.out.println("Exception in Main:"+e.getMessage());
		}
	}
}