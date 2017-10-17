package operation;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Properties;
import report.CreateHTMLReport;

import java.util.Arrays;
import java.util.List;
import org.openqa.selenium.WebElement;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.*;
import org.monte.media.Format;
import org.monte.media.math.Rational;
//import org.monte.screenrecorder.ScreenRecorder;
import static org.monte.media.AudioFormatKeys.*;
import static org.monte.media.VideoFormatKeys.*;

public class UIOperation {

	WebDriver driver;
	int month_index, trend_index, region_index, slider_index, res, ov_res, iv_res, failcnt,totcnt, iv_fin_basic_res, iv_fin_basic_res2, iv_fin_basic_res3;
	String month_val, trend_val, region_val, slider_val, query, ov_query, iv_query, cus_status, fin_status, opr_status, str_status, failfields,iv_fin_basic_query,iv_fin_basic_query2, iv_fin_basic_query3;
	
	// Drop down Array
	String [] trend_list = {"Vs Forecast","Vs Budget","Month Over Month","Year Over Year"};
	String [] region_list = {"Global","PP NA","PP EMEA","PP APAC","PP LATAM","CREDIT","BRAINTREE","VENMO","XOOM","CORP"};
	
	// Field Name Array
	String [] cus_list = {"NNA","Activations","Re-activations","Churn Rate","Actives (12M)","Txns/Active","Consumer CV/Activation","Merchant CV/Activation","Merchant XO EC Conversion *","MAU"};
	String [] fin_list = {"Txns","TPV","Take Rate","Revenue","Txn Expense","Txn Loss","Txn Margin","Opex","OI/CM","Credit-Net Credit Loss %"};
	String [] opr_list = {"FCI Availability *","P0/P1/PX *","Good User Declines *","Contact Rate","cNPS *","Total Employee Population (AWF & FTE)","Total Employee Cost (FTE Only)","Top 50 Merchants TPV","Authorization Rate"};
	String [] str_list = {"CBT % of Txn","PwV TPV","Choise Incremental TE Impact *","Full Stack Processing","Merchant Product Usage *","Customer Product Usage","Credit - Ending Loan Balance","Delinquencies","Mobile % total txns"};

	// Field Value XPath Array
	String [] cus_xpath = {"PP_lnk_CUS_NNA","PP_lnk_CUS_Activations","PP_lnk_CUS_Reactivations","PP_lnk_CUS_ChurnRate","PP_lnk_CUS_Actives","PP_lnk_CUS_TxnsActive","PP_lnk_CUS_ConsumerCVActivation","PP_lnk_CUS_MerchantCVActivation","PP_lnk_CUS_MerchantXOECConversion","PP_lnk_CUS_MAU"};
	String [] fin_xpath = {"PP_lnk_FIN_Txns","PP_lnk_FIN_TPV","PP_lnk_FIN_TakeRate","PP_lnk_FIN_Revenue","PP_lnk_FIN_TxnExpense","PP_lnk_FIN_TxnLoss","PP_lnk_FIN_TxnMargin","PP_lnk_FIN_Opex","PP_lnk_FIN_OI_CM","PP_lnk_FIN_CrNetCrLoss"};
	String [] opr_xpath = {"PP_lnk_OPR_FCIAvailability","PP_lnk_OPR_P0P1PX","PP_lnk_OPR_GoodUserDeclines","PP_lnk_OPR_ContactRate","PP_lnk_OPR_cNPS","PP_lnk_OPR_TotalEmployeePopulation","PP_lnk_OPR_TotalEmployeeCost","PP_lnk_OPR_Top50MerchantsTPV","PP_lnk_OPR_AuthorizationRate"};
	String [] str_xpath = {"PP_lnk_STR_CBTPercentofTxn","PP_lnk_STR_PwVTPV","PP_lnk_STR_ChoiseIncTEImpact","PP_lnk_STR_FullStackProcessing","PP_lnk_STR_MerchantProductUsage","PP_lnk_STR_CustomerProductUsage","PP_lnk_STR_CreditEndingLoanBalance","PP_lnk_STR_Delinquencies","PP_lnk_STR_MobilePercentTotalTxns"};
	
	// Mode XPath Array for Overview Page
	String [] modeOV_list = {"PP_btn_Abs","PP_btn_Per"};
	
	// Mode XPath Array for Level2 Page
	String [] modeL2_list = {"PP_IV_btn_Abs","PP_IV_btn_Per"};
	
	// Field Value Array
	String[] cus_val = new String[10];
	String[] fin_val = new String[10];
	String[] opr_val = new String[10];
	String[] str_val = new String[10];
	
	WebElement Temp;
	String[] arrofStr;
	
	CreateHTMLReport testreport; 
	StringBuilder htmlstr;
	Connection conn;
	Statement stmt;
	TestRecorder screenRecorder;
	WebDriverWait wait, wait1;
	Select time_dropdown, region_dropdown;
	
	
	public UIOperation(WebDriver driver, Connection conn){
		this.driver = driver;
		this.conn = conn;
		wait = new WebDriverWait(driver, 180);
		wait1 = new WebDriverWait(driver, 120);
		testreport = new CreateHTMLReport();
	}
	/**
     * <p>Navigates to the specific URL and lands in the Home Page of TMIS Application
     * <p>Steps:
     * <ul><li>1. Navigates to the specific URL
     * <li>2. Waits for the user to enter the Credentials
     * <li>3. Deletes the old records in the table
     * <li>4. Clicks on the Acknowledge Button
     * <li>5. Creates HTML header
     * <li>6. Wait till the home page gets loaded
     * @param   p      The reference to the Object Repository
     * @param   url_link      The URL-Environment to which the browser has to be navigated
     * @param   db_type		  The Database to which the output should be written
     * @return   None
     */
	public void initSetup(Properties p,String url_link, String db_type) throws Exception
	{
		// Navigate to the URL
		try
		{
			System.out.println("Navigating to the URL: "+url_link);
			System.out.println("URL link from Obj prop: "+p.getProperty(url_link));
			driver.get(p.getProperty(url_link));
			System.out.println("After URL navigation");
		}
		catch(Exception e)
		{
			System.out.println("Exception in Navigating URL: "+e.getMessage());
		}
		
		Thread.sleep(3000);
		
		if(db_type=="MS Access")
		{
			System.out.println("Starting DB Conn Create Stmt");
			stmt=conn.createStatement();
			// Delete Overview Table
			ov_query="delete * from TMIS_Dashboard";
			ov_res=stmt.executeUpdate(ov_query);
			
			// Delete Level2 Table
			iv_query="delete * from TMIS_Customer_InnerView";
			iv_res=stmt.executeUpdate(iv_query);
			
			
		}
		
		else
		{
			System.out.println("Starting DB Conn Create Stmt");
			stmt=conn.createStatement();
			
			// Delete Overview Table
			ov_query="TRUNCATE tmis.tmis_dashboard";
			ov_res=stmt.executeUpdate(ov_query);
			
			// Delete Level2 Customer Table
			iv_query="TRUNCATE TMIS.TMIS_Customer_InnerView";
			iv_res=stmt.executeUpdate(iv_query);
			
			//Delete Level2 Financial Table-1
			iv_fin_basic_query="TRUNCATE TMIS.tmis_fin_basic";
			iv_fin_basic_res=stmt.executeUpdate(iv_fin_basic_query);
			
			//Delete Level2 Financial Table-2
			iv_fin_basic_query2="TRUNCATE TMIS.tmis_fin_adv";
			iv_fin_basic_res2=stmt.executeUpdate(iv_fin_basic_query2);
			
			//Delete Level2 Operational Table-2
			iv_fin_basic_query3="TRUNCATE TABLE TMIS.tmis_operational_iv";
			iv_fin_basic_res3=stmt.executeUpdate(iv_fin_basic_query3);
			
			
			System.out.println("Database Cleared!!");
		}		
		String currentURL = driver.getCurrentUrl();
		
		// Click on the Acknowledge button
		if (currentURL != null)
		{
			try
			{
				// Wait for the Acknowledge button to appear
				wait.until(ExpectedConditions.elementToBeClickable(this.getObject(p, "PP_btn_ack", "XPATH")));
				driver.findElement(this.getObject(p, "PP_btn_ack", "XPATH")).click();
			}
			catch(Exception e)
			{
				System.out.println("Error in clicking ACK / ACK button not available: "+e.getMessage());
				System.exit(-1);
			}
		}
		else
		{
			System.out.println("URL is not loaded properly");
			System.exit(-1);
		}
		
		String hdrfile = System.getProperty("user.dir")+"\\Reference\\HTML_Header.txt";
		testreport.designHeader(hdrfile);
		
		// Wait till the home page gets loaded
		wait1.until(ExpectedConditions.visibilityOfElementLocated(this.getObject(p, "PP_txt_UserName", "XPATH")));
		Thread.sleep(6000);
	}	
	
	/**
     * <p>Loops over the month, region and trend and with the mode selected,
     * it fetches all the values of each attributes in the overview page
     * and store them in a database.
     * <p>Steps:
     * <ul><li>1. Do the Month selection -- Outer Loop
     * <li>2. Do the Region selection -- Middle Loop
     * <li>3. Do the Trend selection -- Inner Loop
     * <li>4. Select the Mode as either 'Absolute' or 'Percent' in the slider
     * <li>5. Get visible text of all 10 items using XPath
     * <li>6. Call the DB insert method to write the above read values</ul>
     * @param   p      The reference for Object Repositories
     * @param   objMonth      The month for which the values to be fetched
     * @return   None
     */
	public void perform_MonthlyOverview(Properties p, String objMonth, String db_type) throws Exception
	{
		Select trend_dropdown=null;
		
		// Set the Slider as Month
		driver.findElement(this.getObject(p, "PP_btn_Monthly", "XPATH")).click();
		Thread.sleep(6000);
		System.out.println("Monthly Overview Page, Data Extraction Process has started for:\""+objMonth);
		// this.takescreenshot();
		
		// Wait for the Time Drop down to appear, and set the value got from the input sheet
		wait1.until(ExpectedConditions.elementToBeClickable(this.getObject(p, "PP_ddn_Time", "XPATH")));
		time_dropdown = new Select(driver.findElement(this.getObject(p, "PP_ddn_Time", "XPATH")));
		time_dropdown.selectByVisibleText(objMonth);
		month_val = objMonth;
		
		// Wait for 3 seconds, to all the page to load the data
		Thread.sleep(3000);

		stmt=conn.createStatement();		
		Thread.sleep(1000);
		
		// Click on the trend drop down one by one (one at a time)
		for (trend_index=0; trend_index < 4; trend_index++)
		{
			try
			{
				wait1.until(ExpectedConditions.elementToBeClickable(this.getObject(p, "PP_ddn_Trend", "XPATH")));
				trend_dropdown = new Select(driver.findElement(this.getObject(p, "PP_ddn_Trend", "XPATH")));
				trend_dropdown.selectByIndex(trend_index);
			}
			catch(Exception e){
				System.out.println("Exception on Trend dropdown Selection: "+e.getMessage());
				//trend_dropdown = new Select(driver.findElement(this.getObject(p, "PP_ddn_Trend", "XPATH")));
				//trend_dropdown.selectByIndex(trend_index);
			}

			trend_val = trend_list [trend_index];
			Thread.sleep(4000);
			
			// Click on the region drop down one by one (one at a time)
			for (region_index=0; region_index < 10; region_index++)
			{
				try{
					wait1.until(ExpectedConditions.elementToBeClickable(this.getObject(p, "PP_ddn_Percent", "XPATH")));
				    region_dropdown = new Select(driver.findElement(this.getObject(p, "PP_ddn_Percent", "XPATH")));
					region_dropdown.selectByIndex(region_index);
				}
				catch(Exception e){
					System.out.println("Exception on Trend dropdown Selection: "+e.getMessage());
				    //region_dropdown = new Select(driver.findElement(By.id("region-selector")));
					//region_dropdown.selectByIndex(region_index);	
				}
				region_val = region_list [region_index];
				Thread.sleep(4000);
				
				for (String sliderEmnt : modeOV_list)
				{
					// Set the Slider Value
					if(sliderEmnt == "PP_btn_Abs")
					{
						slider_val = "Absolute";						
					}
					else if(sliderEmnt == "PP_btn_Per")
					{
						slider_val = "Percent";
					}	
					// Set the Slider position to 'Absolute' or 'Percent'
					wait1.until(ExpectedConditions.elementToBeClickable(this.getObject(p, sliderEmnt, "XPATH")));
					driver.findElement(this.getObject(p, sliderEmnt, "XPATH")).click();
					Thread.sleep(5000);
					
					// Loop and get Customer and Financial Column Values
					for (int attr=0;attr<10;attr++)
					{
						cus_val [attr] = driver.findElement(this.getObject(p, cus_xpath[attr], "XPATH")).getText();
						fin_val [attr] = driver.findElement(this.getObject(p, fin_xpath[attr], "XPATH")).getText();
					}
					
					// Loop and get Operational and Strategic Column Values
					for (int attr=0;attr<9;attr++)
					{
						opr_val [attr] = driver.findElement(this.getObject(p, opr_xpath[attr], "XPATH")).getText();
						str_val [attr] = driver.findElement(this.getObject(p, str_xpath[attr], "XPATH")).getText();
					}
					
					cus_status = validateEmptyData("Customer");
					fin_status = validateEmptyData("Financial");
					opr_status = validateEmptyData("Operational");
					str_status = validateEmptyData("Strategic");
					if (failcnt > 0)
					{
						testreport.appendBody(month_val, trend_val, region_val, slider_val, cus_status, fin_status, opr_status, str_status);
					}
					
					query="insert into TMIS_Dashboard values('"+month_val+"','"+trend_val+"','"+region_val+"','"+slider_val+"','"+cus_val [0]+"','"+cus_val [1]+"','"+cus_val[2]+"','"+cus_val[3]+"','"+cus_val[4]+"','"+cus_val[5]+"','"+cus_val[6]+"','"+cus_val[7]+"','"+cus_val[8]+"','"+cus_val[9]+"','"+fin_val [0]+"','"+fin_val [1]+"','"+fin_val[2]+"','"+fin_val[3]+"','"+fin_val[4]+"','"+fin_val[5]+"','"+fin_val[6]+"','"+fin_val[7]+"','"+fin_val[8]+"','"+fin_val[9]+"','"+opr_val [0]+"','"+opr_val [1]+"','"+opr_val[2]+"','"+opr_val[3]+"','"+opr_val[4]+"','"+opr_val[5]+"','"+opr_val[6]+"','"+opr_val[7]+"','"+opr_val[8]+"','"+str_val [0]+"','"+str_val [1]+"','"+str_val[2]+"','"+str_val[3]+"','"+str_val[4]+"','"+str_val[5]+"','"+str_val[6]+"','"+str_val[7]+"','"+str_val[8]+"')";
					res=stmt.executeUpdate(query);
				}
			}	
		}		
		//this.takescreenshot();
	}
	
	/**
     * <p>Loops over the month and it clicks on the each attribute.
     * Then it fetches all the values of Level 2 page and store them in a database.
     * <p>Steps:
     * <ul><li>1. Do the Month selection
     * <li>2. Click on the each Attribute of the Customer Column
     * <li>3. Do the Trend selection
     * <li>4. Select the Mode as either 'Absolute' or 'Percent' in the slider
     * <li>5. Get the Table Values in the L2 Page
     * <li>6. Call the DB insert method to write the above read values</ul>
     * @param   p      The reference for Object Repositories
     * @param   objMonth      The month for which the values to be fetched
     * @return   None
     */
	public void perform_MonthlyInnerView(Properties p, String objMonth, String db_type) throws Exception
	{
		// Call  Monthly Level 2 page's method
		
		//Customer
			this.perform_MonthlyCusInnerView(p, objMonth, db_type);
		
		//Financial 
			this.perform_MonthlyFinInnerView_Basic(p, objMonth, db_type);
			this.perform_MonthlyFinInnerView_Advanced(p, objMonth, db_type);
			this.perform_MonthlyFinInnerView_Med(p, objMonth, db_type);
		
		//Operational
			this.perform_MonthlyOprInnerView_basic(p, objMonth, db_type);
			this.perform_MonthlyOprInnerView_Med(p, objMonth, db_type);
			this.perform_MonthlyOprInnerView_Adv(p, objMonth, db_type);
			this.perform_MonthlyOprInnerView_EmpPop(p, objMonth, db_type);
	}
	/**
     * <p>Loops over the month and it clicks on the each attribute of Customer Column.
     * Then it fetches all the values of Level 2 page and store them in the respective customer tables.
     * <p>Steps:
     * <ul><li>1. Do the Month selection
     * <li>2. Click on the each Attribute of the Customer Column
     * <li>3. Do the Trend selection
     * <li>4. Select the Mode as either 'Absolute' or 'Percent' in the slider
     * <li>5. Get the Table Values in the L2 Page
     * <li>6. Call the DB insert method to write the above read values</ul>
     * @param   p      The reference for Object Repositories
     * @param   objMonth      The month for which the values to be fetched
     * @return   None
     */
	public void perform_MonthlyCusInnerView(Properties p, String objMonth, String db_type) throws Exception

	{
		Select rate_sel_ddn;
		Select trend_dropdown=null;
		int rowcnt=0;
		String attrName;
		String At_nm;
		At_nm=null;
		
		// Set the Slider as Month
		driver.findElement(this.getObject(p, "PP_btn_Monthly", "XPATH")).click();
		Thread.sleep(6000);
		System.out.println("Monthly Level 2 Page, Data Extraction Process has started for: "+objMonth);
		//this.takescreenshot();
		
		// Wait for the Time Drop down to appear, and set the value got from the input sheet
		wait1.until(ExpectedConditions.elementToBeClickable(this.getObject(p, "PP_ddn_Time", "XPATH")));
	    time_dropdown = new Select(driver.findElement(this.getObject(p, "PP_ddn_Time", "XPATH")));
		time_dropdown.selectByVisibleText(objMonth);
		
		month_val = objMonth;
		
		// Wait for 3 seconds, to all the page to load the data
		Thread.sleep(3000);
		
		stmt=conn.createStatement();		
		Thread.sleep(1000);
		
		// Click on the each Attribute of the Customer Column
		for (int cus_attr=0;cus_attr<9;cus_attr++)
		{
			driver.findElement(this.getObject(p, cus_xpath[cus_attr], "XPATH")).click();
			Thread.sleep(10000);
			wait1.until(ExpectedConditions.visibilityOfElementLocated(this.getObject(p, "PP_IV_txt_attrName", "XPATH")));
			 attrName = driver.findElement(this.getObject(p, "PP_IV_txt_attrName", "XPATH")).getText();			
			
			
			Select trend4ddn = new Select(driver.findElement(this.getObject(p, "PP_IV_ddn_Trend", "XPATH")));
			
			int Trend_Count=trend4ddn.getOptions().size();
			System.out.println(Trend_Count);
			
			
			// Click on the trend drop down one by one (one at a time)
			
			Select trend_dropdown2 = new Select(driver.findElement(this.getObject(p, "PP_IV_ddn_Trend", "XPATH")));
			List <WebElement> trendvalues=trend_dropdown2.getOptions();
			System.out.println("Trend Values options"+trendvalues.size());
			
			for (int i=0; i<trendvalues.size();i++)
			{
				System.out.println("Tren values::"+trendvalues.get(i).getText());
				Thread.sleep(4000);
				
			}
			
			
			for (trend_index=0; trend_index < Trend_Count; trend_index++)
			{	
				try
				{
					wait1.until(ExpectedConditions.elementToBeClickable(this.getObject(p, "PP_IV_ddn_Trend", "XPATH")));
				    trend_dropdown = new Select(driver.findElement(this.getObject(p, "PP_IV_ddn_Trend", "XPATH")));
					trend_dropdown.selectByIndex(trend_index);
					trend_val = trend_dropdown.getFirstSelectedOption().getText();
					System.out.println("Trend value:"+trend_val);
				}
				catch(Exception e){
					System.out.println("Exception on Trend dropdown Selection: "+e.getMessage());
					
				}

				
				Thread.sleep(4000);
				
				System.out.println("Checking Rate Selector Size");
				
				int rate_sel_count = 0;
				
				if(driver.findElements(this.getObject(p, "PP_IV_ddn_RateSelector", "XPATH")).size() != 0)
				{
				rate_sel_ddn = new Select(driver.findElement(this.getObject(p, "PP_IV_ddn_RateSelector", "XPATH")));
				rate_sel_count=rate_sel_ddn.getOptions().size();
				rate_sel_ddn.selectByIndex(0);
				
				}
				int rate_loop_index = 0;
				
				System.out.println("Rate Selector Size"+rate_sel_count);
				
				do
				{
				
				if (rate_loop_index > 0)
				{
					try {
						
						System.out.println("about to select the rate ddn"+rate_loop_index);
						rate_sel_ddn = new Select(driver.findElement(this.getObject(p, "PP_IV_ddn_RateSelector", "XPATH")));
						rate_sel_ddn.selectByIndex(rate_loop_index);
						Thread.sleep(5000);
						System.out.println("rate_loop_index"+rate_loop_index);
//						
					
					}
					catch(Exception e)
					{
						System.out.println("Exception on attribute Selection: "+e.getMessage());
						System.out.println("about to select the rate ddn"+rate_loop_index);
						Thread.sleep(5000);
						
					}
					
				}
				System.out.println("TrendIndex:"+trend_index);
				System.out.println("Rate Loop Index"+rate_loop_index);
				System.out.println("Trend Element value"+trend_dropdown.toString());
				
				Select trend_dropdown3 = new Select(driver.findElement(this.getObject(p, "PP_IV_ddn_Trend", "XPATH")));
				trend_dropdown3.selectByIndex(trend_index);
			
				for (String sliderEmnt : modeL2_list)
				{
					
					System.out.println("choosing abs/per"+sliderEmnt);
					// Set the Slider Value
					if(sliderEmnt == "PP_IV_btn_Abs")
					{
						slider_val = "Absolute";						
					}
					else if(sliderEmnt == "PP_IV_btn_Per")
					{
						slider_val = "Percent";
					}	
					
					Thread.sleep(6000);
					// Set the Slider position to 'Absolute' or 'Percent'
					wait1.until(ExpectedConditions.elementToBeClickable(this.getObject(p, sliderEmnt, "XPATH")));
					driver.findElement(this.getObject(p, sliderEmnt, "XPATH")).click();
					Thread.sleep(8000);				
					
					// Clicking all the (+) Buttons to expand
					System.out.println("Size of the button before the list"+driver.findElements(By.xpath("//button[contains(@class, 'toggleSubProduct')]")).size());;
					List<WebElement> plusBtnList=driver.findElements(By.xpath("//button[contains(@class, 'toggleSubProduct')]"));
					System.out.println("Size of buttons:"+ plusBtnList.size());
					System.out.println("Button Elements:"+plusBtnList);
					
					WebElement Attribute_Name= driver.findElement(By.xpath("//div[@id='fav-desc']"));
					 At_nm=Attribute_Name.getText();
					 System.out.println("Attribute name on the second page"+ Attribute_Name);
					 
					 
			        for(WebElement plusBtn:plusBtnList)
			        {
			        	Thread.sleep(6000);
			        	System.out.println("Button:"+plusBtn.getText());
			        	plusBtn.click();
			        	System.out.println("Button Clicked");
			       
			        } 	
			        Thread.sleep(5000);
			        
					// Get the Table Values in the L2 Page
					List<WebElement> ele=driver.findElements(By.xpath("//div[contains(@class, 'product-region-data')]"));
					String dat = "";
					String otpt = "";
					int iter=0;
					System.out.println("Element size"+ele.size());
					for (WebElement e:ele)
					{
						
						dat = e.getText().trim();
						dat = dat.trim();
						if (dat.contains(""+'\n'))
						{
							dat = dat.replaceAll(Pattern.quote(""+'\n'), " ");
						}
						iter++;
						otpt=otpt+'\n'+dat;

						
					}
					Thread.sleep(4000);
					
					// Build Query to insert the record
					String[] chk = new String[150];
					chk = otpt.split("\\n");
					for(int zag=1;zag<chk.length;zag+= 6)
					{
						rowcnt++;
						System.out.println(rowcnt+"   Metric:"+At_nm+"  Trend:"+trend_val+"  Slider:"+slider_val+"  attr1: "+chk[zag]);
						query="insert into TMIS_Customer_InnerView values"
								+ "('"+At_nm+"','"+month_val+"','"+trend_val+"','"+slider_val+"',"
										+ "'"+chk[zag+0]+"','"+chk[zag+1]+"','"+chk[zag+2]+"',"
												+ "'"+chk[zag+3]+"','"+chk[zag+4]+"','"+chk[zag+5]+"')";
						res=stmt.executeUpdate(query);
					}				
					Thread.sleep(4000);					
				}
				rate_loop_index++;
				}
				while((rate_loop_index < rate_sel_count) && (rate_sel_count != 0));
				System.out.println(" I am out of the loop the Rate loop Index is: "+rate_loop_index);
				
				if((rate_loop_index == rate_sel_count) && (rate_sel_count != 0))
				{
					rate_sel_ddn = new Select(driver.findElement(this.getObject(p, "PP_IV_ddn_RateSelector", "XPATH")));
					rate_sel_ddn.selectByIndex(0);
					System.out.println("Resetting the rate sel ddn back to first option");
				}
				
				
				Thread.sleep(4000);		

			}	
			// Back to OverView Page
			driver.findElement(this.getObject(p, "PP_IV_txt_toggleArrow", "XPATH")).click();
			Thread.sleep(5000);
			wait1.until(ExpectedConditions.visibilityOfElementLocated(this.getObject(p, cus_xpath[cus_attr], "XPATH")));
		}
	}
	/**
     * <p>Loops over the month and it clicks on the each attribute of Financial Column.
     * Then it fetches all the values of Level 2 page and store them in the respective Financial tables.
     * This is confined for the attributes-TXN, TPV, Take Rate.
     * <p>Steps:
     * <ul><li>1. Do the Month selection
     * <li>2. Click on the each Attribute of the financial Column
     * <li>3. Do the Trend selection
     * <li>4. Select the Mode as either 'Absolute' or 'Percent' in the slider
     * <li>5. Get the Table Values in the L2 Page
     * <li>6. Call the DB insert method to write the above read values</ul>
     * @param   p      The reference for Object Repositories
     * @param   objMonth      The month for which the values to be fetched
     * @return   None
     */
	public void perform_MonthlyFinInnerView_Basic(Properties p, String objMonth, String db_type) throws Exception

	{
		Select rate_sel_ddn;
		Select trend_dropdown=null;
		int rowcnt=0;
		String attrName;
		String At_nm;
		At_nm=null;
		
		// Set the Slider as Month
		driver.findElement(this.getObject(p, "PP_btn_Monthly", "XPATH")).click();
		Thread.sleep(6000);
		System.out.println("Monthly Level 2 Page, Data Extraction Process has started for: "+objMonth);
		//this.takescreenshot();
		
		// Wait for the Time Drop down to appear, and set the value got from the input sheet
		wait1.until(ExpectedConditions.elementToBeClickable(this.getObject(p, "PP_ddn_Time", "XPATH")));
	    time_dropdown = new Select(driver.findElement(this.getObject(p, "PP_ddn_Time", "XPATH")));
		time_dropdown.selectByVisibleText(objMonth);
		
		month_val = objMonth;
		
		// Wait for 3 seconds, to all the page to load the data
		Thread.sleep(3000);
		
		stmt=conn.createStatement();		
		Thread.sleep(1000);
		
		// Click on the each Attribute of the Financial Column
		for (int fin_attr=0;fin_attr<3;fin_attr++)
		{
			driver.findElement(this.getObject(p, fin_xpath[fin_attr], "XPATH")).click();
			Thread.sleep(10000);
			wait1.until(ExpectedConditions.visibilityOfElementLocated(this.getObject(p, "PP_IV_txt_attrName", "XPATH")));
			 attrName = driver.findElement(this.getObject(p, "PP_IV_txt_attrName", "XPATH")).getText();			
			
			
			Select trend4ddn = new Select(driver.findElement(this.getObject(p, "PP_IV_ddn_Trend", "XPATH")));
			
			int Trend_Count=trend4ddn.getOptions().size();
			System.out.println(Trend_Count);
			
			
			// Click on the trend drop down one by one (one at a time)
			
			Select trend_dropdown2 = new Select(driver.findElement(this.getObject(p, "PP_IV_ddn_Trend", "XPATH")));
			List <WebElement> trendvalues=trend_dropdown2.getOptions();
			System.out.println("Trend Values options"+trendvalues.size());
			
			for (int i=0; i<trendvalues.size();i++)
			{
				System.out.println("Tren values::"+trendvalues.get(i).getText());
				Thread.sleep(4000);
				
			}
			
			
			for (trend_index=0; trend_index < Trend_Count; trend_index++)
			{	
				try
				{
					wait1.until(ExpectedConditions.elementToBeClickable(this.getObject(p, "PP_IV_ddn_Trend", "XPATH")));
				    trend_dropdown = new Select(driver.findElement(this.getObject(p, "PP_IV_ddn_Trend", "XPATH")));
					trend_dropdown.selectByIndex(trend_index);
					trend_val = trend_dropdown.getFirstSelectedOption().getText();
					System.out.println("Trend value:"+trend_val);
				}
				catch(Exception e){
					System.out.println("Exception on Trend dropdown Selection: "+e.getMessage());
					
				}

				
				Thread.sleep(4000);
				
				System.out.println("Checking Rate Selector Size");
				
				int rate_sel_count = 0;
				
				if(driver.findElements(this.getObject(p, "PP_IV_ddn_RateSelector", "XPATH")).size() != 0)
				{
				rate_sel_ddn = new Select(driver.findElement(this.getObject(p, "PP_IV_ddn_RateSelector", "XPATH")));
				rate_sel_count=rate_sel_ddn.getOptions().size();
				rate_sel_ddn.selectByIndex(0);
				
				}
				int rate_loop_index = 0;
				
				System.out.println("Rate Selector Size"+rate_sel_count);
				
				do
				{
				
				if (rate_loop_index > 0)
				{
					try {
						
						System.out.println("about to select the rate ddn"+rate_loop_index);
						rate_sel_ddn = new Select(driver.findElement(this.getObject(p, "PP_IV_ddn_RateSelector", "XPATH")));
						rate_sel_ddn.selectByIndex(rate_loop_index);
						Thread.sleep(5000);
						System.out.println("rate_loop_index"+rate_loop_index);
//						
					
					}
					catch(Exception e)
					{
						System.out.println("Exception on attribute Selection: "+e.getMessage());
						System.out.println("about to select the rate ddn"+rate_loop_index);
						Thread.sleep(5000);
						
					}
					
				}
				System.out.println("TrendIndex:"+trend_index);
				System.out.println("Rate Loop Index"+rate_loop_index);
				System.out.println("Trend Element value"+trend_dropdown.toString());
				
				Select trend_dropdown3 = new Select(driver.findElement(this.getObject(p, "PP_IV_ddn_Trend", "XPATH")));
				trend_dropdown3.selectByIndex(trend_index);
			
				for (String sliderEmnt : modeL2_list)
				{
					
					System.out.println("choosing abs/per"+sliderEmnt);
					// Set the Slider Value
					if(sliderEmnt == "PP_IV_btn_Abs")
					{
						slider_val = "Absolute";						
					}
					else if(sliderEmnt == "PP_IV_btn_Per")
					{
						slider_val = "Percent";
					}	
					
					Thread.sleep(6000);
					// Set the Slider position to 'Absolute' or 'Percent'
					wait1.until(ExpectedConditions.elementToBeClickable(this.getObject(p, sliderEmnt, "XPATH")));
					Thread.sleep(8000);
					driver.findElement(this.getObject(p, sliderEmnt, "XPATH")).click();
					Thread.sleep(8000);				
					
					// Clicking all the (+) Buttons to expand
					System.out.println("Size of the button before the list"+driver.findElements(By.xpath("//button[contains(@class, 'toggleSubProduct')]")).size());;
					List<WebElement> plusBtnList=driver.findElements(By.xpath("//button[contains(@class, 'toggleSubProduct')]"));
					System.out.println("Size of buttons:"+ plusBtnList.size());
					System.out.println("Button Elements:"+plusBtnList);
					
					WebElement Attribute_Name= driver.findElement(By.xpath("//div[@id='fav-desc']"));
					 At_nm=Attribute_Name.getText();
					 System.out.println("Attribute name on the second page"+ Attribute_Name);
					 
					 
			        for(WebElement plusBtn:plusBtnList)
			        {
			        	Thread.sleep(6000);
			        	System.out.println("Button:"+plusBtn.getText());
			        	plusBtn.click();
			        	System.out.println("Button Clicked");
			       
			        } 	
			        Thread.sleep(5000);
			        
					// Get the Table Values in the L2 Page
					List<WebElement> ele=driver.findElements(By.xpath("//div[contains(@class, 'product-region-data')]"));
					String dat = "";
					String otpt = "";
					int iter=0;
					System.out.println("Element size"+ele.size());
					for (WebElement e:ele)
					{
						
						dat = e.getText().trim();
						dat = dat.trim();
						if (dat.contains(""+'\n'))
						{
							dat = dat.replaceAll(Pattern.quote(""+'\n'), " ");
						}
						iter++;
						otpt=otpt+'\n'+dat;

						
					}
					Thread.sleep(4000);
					
					// Build Query to insert the record
					String[] chk = new String[150];
					chk = otpt.split("\\n");
					for(int zag=1;zag<chk.length;zag+= 6)
					{
						rowcnt++;
						System.out.println(rowcnt+"   Metric:"+At_nm+"  Trend:"+trend_val+"  Slider:"+slider_val+"  attr1: "+chk[zag]);
						query="insert into TMIS_fin_basic values"
								+ "('"+At_nm+"','"+month_val+"','"+trend_val+"','"+slider_val+"',"
										+ "'"+chk[zag+0]+"','"+chk[zag+1]+"','"+chk[zag+2]+"',"
												+ "'"+chk[zag+3]+"','"+chk[zag+4]+"','"+chk[zag+5]+"')";
						res=stmt.executeUpdate(query);
					}				
					Thread.sleep(4000);					
				}
				rate_loop_index++;
				Thread.sleep(4000);	
				}
				while((rate_loop_index < rate_sel_count) && (rate_sel_count != 0));
				System.out.println(" I am out of the loop the Rate loop Index is: "+rate_loop_index);
				Thread.sleep(6000);				
				if((rate_loop_index == rate_sel_count) && (rate_sel_count != 0))
				{
					rate_sel_ddn = new Select(driver.findElement(this.getObject(p, "PP_IV_ddn_RateSelector", "XPATH")));
					rate_sel_ddn.selectByIndex(0);
					System.out.println("Resetting the rate sel ddn back to first option");
				}
				
				
				Thread.sleep(4000);		

			}	
			// Back to OverView Page
			driver.findElement(this.getObject(p, "PP_IV_txt_toggleArrow", "XPATH")).click();
			Thread.sleep(5000);
			wait1.until(ExpectedConditions.visibilityOfElementLocated(this.getObject(p, fin_xpath[fin_attr], "XPATH")));
		}
	}
	
	/**
     * <p>Loops over the month and it clicks on the each attribute of Financial Column.
     * Then it fetches all the values of Level 2 page and store them in the respective Financial tables.
     * This is confined for the attributes-Revenue
     * <p>Steps:
     * <ul><li>1. Do the Month selection
     * <li>2. Click on the each Attribute of the financial Column
     * <li>3. Do the Trend selection
     * <li>4. Select the Mode as either 'Absolute' or 'Percent' in the slider
     * <li>5. Get the Table Values in the L2 Page
     * <li>6. Call the DB insert method to write the above read values</ul>
     * @param   p      The reference for Object Repositories
     * @param   objMonth      The month for which the values to be fetched
     * @return   None
     */
	public void perform_MonthlyFinInnerView_Advanced(Properties p, String objMonth, String db_type) throws Exception

	{
		Select rate_sel_ddn;
		Select trend_dropdown=null;
		int rowcnt=0;
		String attrName;
		String At_nm;
		At_nm=null;
		
		// Set the Slider as Month
		driver.findElement(this.getObject(p, "PP_btn_Monthly", "XPATH")).click();
		Thread.sleep(6000);
		System.out.println("Monthly Level 2 Page, Data Extraction Process has started for: "+objMonth);
		//this.takescreenshot();
		
		// Wait for the Time Drop down to appear, and set the value got from the input sheet
		wait1.until(ExpectedConditions.elementToBeClickable(this.getObject(p, "PP_ddn_Time", "XPATH")));
	    time_dropdown = new Select(driver.findElement(this.getObject(p, "PP_ddn_Time", "XPATH")));
		time_dropdown.selectByVisibleText(objMonth);
		
		month_val = objMonth;
		
		// Wait for 3 seconds, to all the page to load the data
		Thread.sleep(3000);
		
		stmt=conn.createStatement();		
		Thread.sleep(1000);
		
		// Click on the each Attribute of the Financial Column
		for (int fin_attr=3;fin_attr<4;fin_attr++)
		{
			driver.findElement(this.getObject(p, fin_xpath[fin_attr], "XPATH")).click();
			Thread.sleep(10000);
			wait1.until(ExpectedConditions.visibilityOfElementLocated(this.getObject(p, "PP_IV_txt_attrName", "XPATH")));
			 attrName = driver.findElement(this.getObject(p, "PP_IV_txt_attrName", "XPATH")).getText();			
			
			
			Select trend4ddn = new Select(driver.findElement(this.getObject(p, "PP_IV_ddn_Trend", "XPATH")));
			
			int Trend_Count=trend4ddn.getOptions().size();
			System.out.println(Trend_Count);
			
			
			// Click on the trend drop down one by one (one at a time)
			
			Select trend_dropdown2 = new Select(driver.findElement(this.getObject(p, "PP_IV_ddn_Trend", "XPATH")));
			List <WebElement> trendvalues=trend_dropdown2.getOptions();
			System.out.println("Trend Values options"+trendvalues.size());
			
			for (int i=0; i<trendvalues.size();i++)
			{
				System.out.println("Tren values::"+trendvalues.get(i).getText());
				Thread.sleep(4000);
				
			}
			
			
			for (trend_index=0; trend_index < Trend_Count; trend_index++)
			{	
				try
				{
					wait1.until(ExpectedConditions.elementToBeClickable(this.getObject(p, "PP_IV_ddn_Trend", "XPATH")));
				    trend_dropdown = new Select(driver.findElement(this.getObject(p, "PP_IV_ddn_Trend", "XPATH")));
					trend_dropdown.selectByIndex(trend_index);
					trend_val = trend_dropdown.getFirstSelectedOption().getText();
					System.out.println("Trend value:"+trend_val);
				}
				catch(Exception e){
					System.out.println("Exception on Trend dropdown Selection: "+e.getMessage());
					
				}

				
				Thread.sleep(4000);
				
				System.out.println("Checking Rate Selector Size");
				
				int rate_sel_count = 0;
				
				if(driver.findElements(this.getObject(p, "PP_IV_ddn_RateSelector", "XPATH")).size() != 0)
				{
				rate_sel_ddn = new Select(driver.findElement(this.getObject(p, "PP_IV_ddn_RateSelector", "XPATH")));
				rate_sel_count=rate_sel_ddn.getOptions().size();
				rate_sel_ddn.selectByIndex(0);
				
				}
				int rate_loop_index = 0;
				
				System.out.println("Rate Selector Size"+rate_sel_count);
				
				do
				{
				
				if (rate_loop_index > 0)
				{
					try {
						
						System.out.println("about to select the rate ddn"+rate_loop_index);
						rate_sel_ddn = new Select(driver.findElement(this.getObject(p, "PP_IV_ddn_RateSelector", "XPATH")));
						rate_sel_ddn.selectByIndex(rate_loop_index);
						Thread.sleep(5000);
						System.out.println("rate_loop_index"+rate_loop_index);
//						
					
					}
					catch(Exception e)
					{
						System.out.println("Exception on attribute Selection: "+e.getMessage());
						System.out.println("about to select the rate ddn"+rate_loop_index);
						Thread.sleep(5000);
						
					}
					
				}
				System.out.println("TrendIndex:"+trend_index);
				System.out.println("Rate Loop Index"+rate_loop_index);
				System.out.println("Trend Element value"+trend_dropdown.toString());
				
				Select trend_dropdown3 = new Select(driver.findElement(this.getObject(p, "PP_IV_ddn_Trend", "XPATH")));
				trend_dropdown3.selectByIndex(trend_index);
			
				for (String sliderEmnt : modeL2_list)
				{
					
					System.out.println("choosing abs/per"+sliderEmnt);
					// Set the Slider Value
					if(sliderEmnt == "PP_IV_btn_Abs")
					{
						slider_val = "Absolute";						
					}
					else if(sliderEmnt == "PP_IV_btn_Per")
					{
						slider_val = "Percent";
					}	
					
					Thread.sleep(6000);
					// Set the Slider position to 'Absolute' or 'Percent'
					wait1.until(ExpectedConditions.elementToBeClickable(this.getObject(p, sliderEmnt, "XPATH")));
					driver.findElement(this.getObject(p, sliderEmnt, "XPATH")).click();
					Thread.sleep(8000);				
					
					// Clicking all the (+) Buttons to expand
					System.out.println("Size of the button before the list"+driver.findElements(By.xpath("//button[contains(@class, 'toggleSubProduct')]")).size());;
					List<WebElement> plusBtnList=driver.findElements(By.xpath("//button[contains(@class, 'toggleSubProduct')]"));
					System.out.println("Size of buttons:"+ plusBtnList.size());
					System.out.println("Button Elements:"+plusBtnList);
					
					WebElement Attribute_Name= driver.findElement(By.xpath("//div[@id='fav-desc']"));
					 At_nm=Attribute_Name.getText();
					 System.out.println("Attribute name on the second page"+ Attribute_Name);
					 
					 
			        for(WebElement plusBtn:plusBtnList)
			        {
			        	Thread.sleep(6000);
			        	System.out.println("Button:"+plusBtn.getText());
			        	plusBtn.click();
			        	System.out.println("Button Clicked");
			       
			        } 	
			        Thread.sleep(5000);
			        
					// Get the Table Values in the L2 Page
					List<WebElement> ele=driver.findElements(By.xpath("//div[contains(@class, 'product-region-data')]"));
					String dat = "";
					String otpt = "";
					int iter=0;
					System.out.println("Element size"+ele.size());
					for (WebElement e:ele)
					{
						
						dat = e.getText().trim();
						dat = dat.trim();
						if (dat.contains(""+'\n'))
						{
							dat = dat.replaceAll(Pattern.quote(""+'\n'), " ");
						}
						iter++;
						otpt=otpt+'\n'+dat;

						
					}
					Thread.sleep(4000);
					
					// Build Query to insert the record
					String[] chk = new String[150];
					chk = otpt.split("\\n");
					int n=Arrays.asList(chk).lastIndexOf("ALL");
					for(int zag=1;zag<n;zag+= 7)
					{
						query="insert into tmis_fin_adv values"
								+ "('"+At_nm+"','"+month_val+"','"+trend_val+"','"+slider_val+"',"
										+ "'"+chk[zag+0]+"','"+chk[zag+1]+"','"+chk[zag+2]+"',"
												+ "'"+chk[zag+3]+"','"+chk[zag+4]+"','"+chk[zag+5]+"','"+chk[zag+6]+"')";
						res=stmt.executeUpdate(query);
					}				
					Thread.sleep(2000);
					
					for(int zag=n;zag<chk.length;zag+= 6)
					{
						query="insert into tmis_fin_basic values"
								+ "('"+At_nm+"','"+month_val+"','"+trend_val+"','"+slider_val+"',"
										+ "'"+chk[zag+0]+"','"+chk[zag+1]+"','"+chk[zag+2]+"',"
												+ "'"+chk[zag+3]+"','"+chk[zag+4]+"','"+chk[zag+5]+"')";
						res=stmt.executeUpdate(query);
					}
					
					Thread.sleep(4000);						
				}
				rate_loop_index++;
				}
				while((rate_loop_index < rate_sel_count) && (rate_sel_count != 0));
				System.out.println(" I am out of the loop the Rate loop Index is: "+rate_loop_index);
				
				if((rate_loop_index == rate_sel_count) && (rate_sel_count != 0))
				{
					rate_sel_ddn = new Select(driver.findElement(this.getObject(p, "PP_IV_ddn_RateSelector", "XPATH")));
					rate_sel_ddn.selectByIndex(0);
					System.out.println("Resetting the rate sel ddn back to first option");
				}
				
				
				Thread.sleep(4000);		

			}	
			// Back to OverView Page
			driver.findElement(this.getObject(p, "PP_IV_txt_toggleArrow", "XPATH")).click();
			Thread.sleep(5000);
			wait1.until(ExpectedConditions.visibilityOfElementLocated(this.getObject(p, fin_xpath[fin_attr], "XPATH")));
		}
	}
	
	/**
     * <p>Loops over the month and it clicks on the each attribute of Financial Column.
     * Then it fetches all the values of Level 2 page and store them in the respective customer tables.
     * This is confined for the attributes-TXN EXPENSE, TXN LOSS, TXN MARGIN, OPEX, OI
     * <p>Steps:
     * <ul><li>1. Do the Month selection
     * <li>2. Click on the each Attribute of the financial Column
     * <li>3. Do the Trend selection
     * <li>4. Select the Mode as either 'Absolute' or 'Percent' in the slider
     * <li>5. Get the Table Values in the L2 Page
     * <li>6. Call the DB insert method to write the above read values</ul>
     * @param   p      The reference for Object Repositories
     * @param   objMonth      The month for which the values to be fetched
     * @return   None
	*/
	public void perform_MonthlyFinInnerView_Med(Properties p, String objMonth, String db_type) throws Exception

	{
		Select rate_sel_ddn;
		Select trend_dropdown=null;
		int rowcnt=0;
		String attrName;
		String At_nm;
		At_nm=null;
		
		// Set the Slider as Month
		driver.findElement(this.getObject(p, "PP_btn_Monthly", "XPATH")).click();
		Thread.sleep(6000);
		System.out.println("Monthly Level 2 Page, Data Extraction Process has started for: "+objMonth);
		//this.takescreenshot();
		
		// Wait for the Time Drop down to appear, and set the value got from the input sheet
		wait1.until(ExpectedConditions.elementToBeClickable(this.getObject(p, "PP_ddn_Time", "XPATH")));
	    time_dropdown = new Select(driver.findElement(this.getObject(p, "PP_ddn_Time", "XPATH")));
		time_dropdown.selectByVisibleText(objMonth);
		
		month_val = objMonth;
		
		// Wait for 3 seconds, to all the page to load the data
		Thread.sleep(3000);
		
		stmt=conn.createStatement();		
		Thread.sleep(1000);
		
		// Click on the each Attribute of the Financial Column
		for (int fin_attr=6;fin_attr<9;fin_attr++)
		{
			driver.findElement(this.getObject(p, fin_xpath[fin_attr], "XPATH")).click();
			Thread.sleep(10000);
			wait1.until(ExpectedConditions.visibilityOfElementLocated(this.getObject(p, "PP_IV_txt_attrName", "XPATH")));
			 attrName = driver.findElement(this.getObject(p, "PP_IV_txt_attrName", "XPATH")).getText();			
			
			
			Select trend4ddn = new Select(driver.findElement(this.getObject(p, "PP_IV_ddn_Trend", "XPATH")));
			
			int Trend_Count=trend4ddn.getOptions().size();
			System.out.println(Trend_Count);
			
			
			// Click on the trend drop down one by one (one at a time)
			
			Select trend_dropdown2 = new Select(driver.findElement(this.getObject(p, "PP_IV_ddn_Trend", "XPATH")));
			List <WebElement> trendvalues=trend_dropdown2.getOptions();
			System.out.println("Trend Values options"+trendvalues.size());
			
			for (int i=0; i<trendvalues.size();i++)
			{
				System.out.println("Tren values::"+trendvalues.get(i).getText());
				Thread.sleep(4000);
				
			}
			
			
			for (trend_index=0; trend_index < Trend_Count; trend_index++)
			{	
				try
				{
					wait1.until(ExpectedConditions.elementToBeClickable(this.getObject(p, "PP_IV_ddn_Trend", "XPATH")));
				    trend_dropdown = new Select(driver.findElement(this.getObject(p, "PP_IV_ddn_Trend", "XPATH")));
					trend_dropdown.selectByIndex(trend_index);
					trend_val = trend_dropdown.getFirstSelectedOption().getText();
					System.out.println("Trend value:"+trend_val);
				}
				catch(Exception e){
					System.out.println("Exception on Trend dropdown Selection: "+e.getMessage());
					
				}

				
				Thread.sleep(4000);
				
				System.out.println("Checking Rate Selector Size");
				
				int rate_sel_count = 0;
				
				if(driver.findElements(this.getObject(p, "PP_IV_ddn_RateSelector", "XPATH")).size() != 0)
				{
				rate_sel_ddn = new Select(driver.findElement(this.getObject(p, "PP_IV_ddn_RateSelector", "XPATH")));
				rate_sel_count=rate_sel_ddn.getOptions().size();
				rate_sel_ddn.selectByIndex(0);
				
				}
				int rate_loop_index = 0;
				
				System.out.println("Rate Selector Size"+rate_sel_count);
				
				do
				{
				
				if (rate_loop_index > 0)
				{
					try {
						
						System.out.println("about to select the rate ddn"+rate_loop_index);
						rate_sel_ddn = new Select(driver.findElement(this.getObject(p, "PP_IV_ddn_RateSelector", "XPATH")));
						rate_sel_ddn.selectByIndex(rate_loop_index);
						Thread.sleep(5000);
						System.out.println("rate_loop_index"+rate_loop_index);
//						
					
					}
					catch(Exception e)
					{
						System.out.println("Exception on attribute Selection: "+e.getMessage());
						System.out.println("about to select the rate ddn"+rate_loop_index);
						Thread.sleep(5000);
						
					}
					
				}
				System.out.println("TrendIndex:"+trend_index);
				System.out.println("Rate Loop Index"+rate_loop_index);
				System.out.println("Trend Element value"+trend_dropdown.toString());
				
				Select trend_dropdown3 = new Select(driver.findElement(this.getObject(p, "PP_IV_ddn_Trend", "XPATH")));
				trend_dropdown3.selectByIndex(trend_index);
			
				for (String sliderEmnt : modeL2_list)
				{
					
					System.out.println("choosing abs/per"+sliderEmnt);
					// Set the Slider Value
					if(sliderEmnt == "PP_IV_btn_Abs")
					{
						slider_val = "Absolute";						
					}
					else if(sliderEmnt == "PP_IV_btn_Per")
					{
						slider_val = "Percent";
					}	
					
					Thread.sleep(6000);
					// Set the Slider position to 'Absolute' or 'Percent'
					wait1.until(ExpectedConditions.elementToBeClickable(this.getObject(p, sliderEmnt, "XPATH")));
					driver.findElement(this.getObject(p, sliderEmnt, "XPATH")).click();
					Thread.sleep(8000);				
					
					// Clicking all the (+) Buttons to expand
					System.out.println("Size of the button before the list"+driver.findElements(By.xpath("//button[contains(@class, 'toggleSubProduct')]")).size());;
					List<WebElement> plusBtnList=driver.findElements(By.xpath("//button[contains(@class, 'toggleSubProduct')]"));
					System.out.println("Size of buttons:"+ plusBtnList.size());
					System.out.println("Button Elements:"+plusBtnList);
					
					WebElement Attribute_Name= driver.findElement(By.xpath("//div[@id='fav-desc']"));
					 At_nm=Attribute_Name.getText();
					 System.out.println("Attribute name on the second page"+ Attribute_Name);
					 
					 
			        for(WebElement plusBtn:plusBtnList)
			        {
			        	Thread.sleep(6000);
			        	System.out.println("Button:"+plusBtn.getText());
			        	plusBtn.click();
			        	System.out.println("Button Clicked");
			       
			        } 	
			        Thread.sleep(5000);
			        
					// Get the Table Values in the L2 Page
					List<WebElement> ele=driver.findElements(By.xpath("//div[contains(@class, 'product-region-data')]"));
					String dat = "";
					String otpt = "";
					int iter=0;
					System.out.println("Element size"+ele.size());
					for (WebElement e:ele)
					{
						
						dat = e.getText().trim();
						dat = dat.trim();
						if (dat.contains(""+'\n'))
						{
							dat = dat.replaceAll(Pattern.quote(""+'\n'), " ");
						}
						iter++;
						otpt=otpt+'\n'+dat;

						
					}
					Thread.sleep(4000);
					
					// Build Query to insert the record
					String[] chk = new String[150];
					chk = otpt.split("\\n");
					for(int zag=1;zag<chk.length;zag+= 7)
					{
						query="insert into tmis_fin_adv values"
								+ "('"+At_nm+"','"+month_val+"','"+trend_val+"','"+slider_val+"',"
										+ "'"+chk[zag+0]+"','"+chk[zag+1]+"','"+chk[zag+2]+"',"
												+ "'"+chk[zag+3]+"','"+chk[zag+4]+"','"+chk[zag+5]+"','"+chk[zag+6]+"')";
						res=stmt.executeUpdate(query);
					}					
					Thread.sleep(2000);
										
				}
				rate_loop_index++;
				}
				while((rate_loop_index < rate_sel_count) && (rate_sel_count != 0));
				System.out.println(" I am out of the loop the Rate loop Index is: "+rate_loop_index);
				
				if((rate_loop_index == rate_sel_count) && (rate_sel_count != 0))
				{
					rate_sel_ddn = new Select(driver.findElement(this.getObject(p, "PP_IV_ddn_RateSelector", "XPATH")));
					rate_sel_ddn.selectByIndex(0);
					System.out.println("Resetting the rate sel ddn back to first option");
				}
				
				
				Thread.sleep(4000);		

			}	
			// Back to OverView Page
			driver.findElement(this.getObject(p, "PP_IV_txt_toggleArrow", "XPATH")).click();
			Thread.sleep(5000);
			wait1.until(ExpectedConditions.visibilityOfElementLocated(this.getObject(p, fin_xpath[fin_attr], "XPATH")));
		}
	}
	/**
     * <p>Loops over the month and it clicks on the each attribute of Operational Column.
     * Then it fetches all the values of Level 2 page and store them in the respective Operational tables.
     * This is confined for the attributes-FCI Availability, P0/P1/PX
     * <p>Steps:
     * <ul><li>1. Do the Month selection
     * <li>2. Click on the each Attribute of the Operational Column
     * <li>3. Do the Trend selection
     * <li>4. Select the Mode as either 'Absolute' or 'Percent' in the slider
     * <li>5. Get the Table Values in the L2 Page
     * <li>6. Call the DB insert method to write the above read values</ul>
     * @param   p      The reference for Object Repositories
     * @param   objMonth      The month for which the values to be fetched
     * @return   None
     * */
	public void perform_MonthlyOprInnerView_basic(Properties p, String objMonth, String db_type) throws Exception

	{
		Select rate_sel_ddn;
		Select trend_dropdown=null;
		int rowcnt=0;
		String attrName;
		String At_nm;
		At_nm=null;
		
		// Set the Slider as Month
		driver.findElement(this.getObject(p, "PP_btn_Monthly", "XPATH")).click();
		Thread.sleep(6000);
		System.out.println("Monthly Level 2 Page, Data Extraction Process has started for: "+objMonth);
		//this.takescreenshot();
		
		// Wait for the Time Drop down to appear, and set the value got from the input sheet
		wait1.until(ExpectedConditions.elementToBeClickable(this.getObject(p, "PP_ddn_Time", "XPATH")));
	    time_dropdown = new Select(driver.findElement(this.getObject(p, "PP_ddn_Time", "XPATH")));
		time_dropdown.selectByVisibleText(objMonth);
		
		month_val = objMonth;
		
		// Wait for 3 seconds, to all the page to load the data
		Thread.sleep(3000);
		
		stmt=conn.createStatement();		
		Thread.sleep(1000);
		
		// Click on the each Attribute of the operational Column
		for (int opr_attr=0;opr_attr<2;opr_attr++)
		{
			driver.findElement(this.getObject(p, opr_xpath[opr_attr], "XPATH")).click();
			Thread.sleep(10000);
			wait1.until(ExpectedConditions.visibilityOfElementLocated(this.getObject(p, "PP_IV_txt_attrName", "XPATH")));
			 attrName = driver.findElement(this.getObject(p, "PP_IV_txt_attrName", "XPATH")).getText();			
			
			
			Select trend4ddn = new Select(driver.findElement(this.getObject(p, "PP_IV_ddn_Trend", "XPATH")));
			
			int Trend_Count=trend4ddn.getOptions().size();
			System.out.println(Trend_Count);
			
			
			// Click on the trend drop down one by one (one at a time)
			
			Select trend_dropdown2 = new Select(driver.findElement(this.getObject(p, "PP_IV_ddn_Trend", "XPATH")));
			List <WebElement> trendvalues=trend_dropdown2.getOptions();
			System.out.println("Trend Values options"+trendvalues.size());
			
			for (int i=0; i<trendvalues.size();i++)
			{
				System.out.println("Tren values::"+trendvalues.get(i).getText());
				Thread.sleep(4000);
				
			}
			
			
			for (trend_index=0; trend_index < Trend_Count; trend_index++)
			{	
				try
				{
					wait1.until(ExpectedConditions.elementToBeClickable(this.getObject(p, "PP_IV_ddn_Trend", "XPATH")));
				    trend_dropdown = new Select(driver.findElement(this.getObject(p, "PP_IV_ddn_Trend", "XPATH")));
					trend_dropdown.selectByIndex(trend_index);
					trend_val = trend_dropdown.getFirstSelectedOption().getText();
					System.out.println("Trend value:"+trend_val);
				}
				catch(Exception e){
					System.out.println("Exception on Trend dropdown Selection: "+e.getMessage());
					
				}

				
				Thread.sleep(4000);
				
				System.out.println("Checking Rate Selector Size");
				
				int rate_sel_count = 0;
				
				if(driver.findElements(this.getObject(p, "PP_IV_ddn_RateSelector", "XPATH")).size() != 0)
				{
				rate_sel_ddn = new Select(driver.findElement(this.getObject(p, "PP_IV_ddn_RateSelector", "XPATH")));
				rate_sel_count=rate_sel_ddn.getOptions().size();
				rate_sel_ddn.selectByIndex(0);
				
				}
				int rate_loop_index = 0;
				
				System.out.println("Rate Selector Size"+rate_sel_count);
				
				do
				{
				
				if (rate_loop_index > 0)
				{
					try {
						
						System.out.println("about to select the rate ddn"+rate_loop_index);
						rate_sel_ddn = new Select(driver.findElement(this.getObject(p, "PP_IV_ddn_RateSelector", "XPATH")));
						rate_sel_ddn.selectByIndex(rate_loop_index);
						Thread.sleep(5000);
						System.out.println("rate_loop_index"+rate_loop_index);
//						
					
					}
					catch(Exception e)
					{
						System.out.println("Exception on attribute Selection: "+e.getMessage());
						System.out.println("about to select the rate ddn"+rate_loop_index);
						Thread.sleep(5000);
						
					}
					
				}
				System.out.println("TrendIndex:"+trend_index);
				System.out.println("Rate Loop Index"+rate_loop_index);
				System.out.println("Trend Element value"+trend_dropdown.toString());
				
				Select trend_dropdown3 = new Select(driver.findElement(this.getObject(p, "PP_IV_ddn_Trend", "XPATH")));
				trend_dropdown3.selectByIndex(trend_index);
			
				for (String sliderEmnt : modeL2_list)
				{
					
					System.out.println("choosing abs/per"+sliderEmnt);
					// Set the Slider Value
					if(sliderEmnt == "PP_IV_btn_Abs")
					{
						slider_val = "Absolute";						
					}
					else if(sliderEmnt == "PP_IV_btn_Per")
					{
						slider_val = "Percent";
					}	
					
					Thread.sleep(6000);
					// Set the Slider position to 'Absolute' or 'Percent'
					wait1.until(ExpectedConditions.elementToBeClickable(this.getObject(p, sliderEmnt, "XPATH")));
					driver.findElement(this.getObject(p, sliderEmnt, "XPATH")).click();
					Thread.sleep(8000);				
					
					// Clicking all the (+) Buttons to expand
					System.out.println("Size of the button before the list"+driver.findElements(By.xpath("//button[contains(@class, 'toggleSubProduct')]")).size());;
					List<WebElement> plusBtnList=driver.findElements(By.xpath("//button[contains(@class, 'toggleSubProduct')]"));
					System.out.println("Size of buttons:"+ plusBtnList.size());
					System.out.println("Button Elements:"+plusBtnList);
					
					WebElement Attribute_Name= driver.findElement(By.xpath("//div[@id='fav-desc']"));
					 At_nm=Attribute_Name.getText();
					 System.out.println("Attribute name on the second page"+ Attribute_Name);
					 
					 
			        for(WebElement plusBtn:plusBtnList)
			        {
			        	Thread.sleep(6000);
			        	System.out.println("Button:"+plusBtn.getText());
			        	plusBtn.click();
			        	System.out.println("Button Clicked");
			       
			        } 	
			        Thread.sleep(5000);
			        
					// Get the Table Values in the L2 Page
					List<WebElement> ele=driver.findElements(By.xpath("//div[contains(@class, 'product-region-data')]"));
					String dat = "";
					String otpt = "";
					int iter=0;
					System.out.println("Element size"+ele.size());
					for (WebElement e:ele)
					{
						
						dat = e.getText().trim();
						dat = dat.trim();
						if (dat.contains(""+'\n'))
						{
							dat = dat.replaceAll(Pattern.quote(""+'\n'), " ");
						}
						iter++;
						otpt=otpt+'\n'+dat;

						
					}
					Thread.sleep(4000);
					
					// Build Query to insert the record
					String[] chk = new String[150];
					String var_NA="[-]";
					for(int zag=1;zag<chk.length;zag+=6)
					{
						query="insert into tmis.tmis_operational_iv values"
								+ "('"+At_nm+"','"+month_val+"','"+trend_val+"','"+slider_val+"',"
										+ "'"+chk[zag+0]+"','"+var_NA+"','"+var_NA+"',"
												+ "'"+var_NA+"','"+var_NA+"','"+chk[zag+5]+"')";
						
						res=stmt.executeUpdate(query);
					}						
					Thread.sleep(2000);
										
				}
				rate_loop_index++;
				}
				while((rate_loop_index < rate_sel_count) && (rate_sel_count != 0));
				System.out.println(" I am out of the loop the Rate loop Index is: "+rate_loop_index);
				
				if((rate_loop_index == rate_sel_count) && (rate_sel_count != 0))
				{
					rate_sel_ddn = new Select(driver.findElement(this.getObject(p, "PP_IV_ddn_RateSelector", "XPATH")));
					rate_sel_ddn.selectByIndex(0);
					System.out.println("Resetting the rate sel ddn back to first option");
				}
				
				
				Thread.sleep(4000);		

			}	
			// Back to OverView Page
			driver.findElement(this.getObject(p, "PP_IV_txt_toggleArrow", "XPATH")).click();
			Thread.sleep(5000);
			wait1.until(ExpectedConditions.visibilityOfElementLocated(this.getObject(p, opr_xpath[opr_attr], "XPATH")));
		}
	}
	
	
	/**
     * <p>Loops over the month and it clicks on the each attribute of Operational Column.
     * Then it fetches all the values of Level 2 page and store them in the respective Operational tables.
     * This is confined for the attributes-Good User Declines,Contact Rate, cNPS*
     * <p>Steps:
     * <ul><li>1. Do the Month selection
     * <li>2. Click on the each Attribute of the Operational Column
     * <li>3. Do the Trend selection
     * <li>4. Select the Mode as either 'Absolute' or 'Percent' in the slider
     * <li>5. Get the Table Values in the L2 Page
     * <li>6. Call the DB insert method to write the above read values</ul>
     * @param   p      The reference for Object Repositories
     * @param   objMonth      The month for which the values to be fetched
     * @return   None
     * */
	public void perform_MonthlyOprInnerView_Med(Properties p, String objMonth, String db_type) throws Exception

	{
		Select rate_sel_ddn;
		Select trend_dropdown=null;
		int rowcnt=0;
		String attrName;
		String At_nm;
		At_nm=null;
		
		// Set the Slider as Month
		driver.findElement(this.getObject(p, "PP_btn_Monthly", "XPATH")).click();
		Thread.sleep(6000);
		System.out.println("Monthly Level 2 Page, Data Extraction Process has started for: "+objMonth);
		//this.takescreenshot();
		
		// Wait for the Time Drop down to appear, and set the value got from the input sheet
		wait1.until(ExpectedConditions.elementToBeClickable(this.getObject(p, "PP_ddn_Time", "XPATH")));
	        time_dropdown = new Select(driver.findElement(this.getObject(p, "PP_ddn_Time", "XPATH")));
		time_dropdown.selectByVisibleText(objMonth);
		
		month_val = objMonth;
		
		// Wait for 3 seconds, to all the page to load the data
		Thread.sleep(3000);
		
		stmt=conn.createStatement();		
		Thread.sleep(1000);
		
		// Click on the each Attribute of the operational Column
		for (int opr_attr=2;opr_attr<5;opr_attr++)
		{
			driver.findElement(this.getObject(p, opr_xpath[opr_attr], "XPATH")).click();
			Thread.sleep(10000);
			wait1.until(ExpectedConditions.visibilityOfElementLocated(this.getObject(p, "PP_IV_txt_attrName", "XPATH")));
			 attrName = driver.findElement(this.getObject(p, "PP_IV_txt_attrName", "XPATH")).getText();			
			
			
			Select trend4ddn = new Select(driver.findElement(this.getObject(p, "PP_IV_ddn_Trend", "XPATH")));
			
			int Trend_Count=trend4ddn.getOptions().size();
			System.out.println(Trend_Count);
			
			
			// Click on the trend drop down one by one (one at a time)
			
			Select trend_dropdown2 = new Select(driver.findElement(this.getObject(p, "PP_IV_ddn_Trend", "XPATH")));
			List <WebElement> trendvalues=trend_dropdown2.getOptions();
			System.out.println("Trend Values options"+trendvalues.size());
			
			for (int i=0; i<trendvalues.size();i++)
			{
				System.out.println("Tren values::"+trendvalues.get(i).getText());
				Thread.sleep(4000);
				
			}
			
			
			for (trend_index=0; trend_index < Trend_Count; trend_index++)
			{	
				try
				{
					wait1.until(ExpectedConditions.elementToBeClickable(this.getObject(p, "PP_IV_ddn_Trend", "XPATH")));
				    trend_dropdown = new Select(driver.findElement(this.getObject(p, "PP_IV_ddn_Trend", "XPATH")));
					trend_dropdown.selectByIndex(trend_index);
					trend_val = trend_dropdown.getFirstSelectedOption().getText();
					System.out.println("Trend value:"+trend_val);
				}
				catch(Exception e){
					System.out.println("Exception on Trend dropdown Selection: "+e.getMessage());
					
				}

				
				Thread.sleep(4000);
				
				System.out.println("Checking Rate Selector Size");
				
				int rate_sel_count = 0;
				
				if(driver.findElements(this.getObject(p, "PP_IV_ddn_RateSelector", "XPATH")).size() != 0)
				{
				rate_sel_ddn = new Select(driver.findElement(this.getObject(p, "PP_IV_ddn_RateSelector", "XPATH")));
				rate_sel_count=rate_sel_ddn.getOptions().size();
				rate_sel_ddn.selectByIndex(0);
				
				}
				int rate_loop_index = 0;
				
				System.out.println("Rate Selector Size"+rate_sel_count);
				
				do
				{
				
				if (rate_loop_index > 0)
				{
					try {
						
						System.out.println("about to select the rate ddn"+rate_loop_index);
						rate_sel_ddn = new Select(driver.findElement(this.getObject(p, "PP_IV_ddn_RateSelector", "XPATH")));
						rate_sel_ddn.selectByIndex(rate_loop_index);
						Thread.sleep(5000);
						System.out.println("rate_loop_index"+rate_loop_index);
//						
					
					}
					catch(Exception e)
					{
						System.out.println("Exception on attribute Selection: "+e.getMessage());
						System.out.println("about to select the rate ddn"+rate_loop_index);
						Thread.sleep(5000);
						
					}
					
				}
				System.out.println("TrendIndex:"+trend_index);
				System.out.println("Rate Loop Index"+rate_loop_index);
				System.out.println("Trend Element value"+trend_dropdown.toString());
				
				Select trend_dropdown3 = new Select(driver.findElement(this.getObject(p, "PP_IV_ddn_Trend", "XPATH")));
				trend_dropdown3.selectByIndex(trend_index);
			
				for (String sliderEmnt : modeL2_list)
				{
					
					System.out.println("choosing abs/per"+sliderEmnt);
					// Set the Slider Value
					if(sliderEmnt == "PP_IV_btn_Abs")
					{
						slider_val = "Absolute";						
					}
					else if(sliderEmnt == "PP_IV_btn_Per")
					{
						slider_val = "Percent";
					}	
					
					Thread.sleep(6000);
					// Set the Slider position to 'Absolute' or 'Percent'
					wait1.until(ExpectedConditions.elementToBeClickable(this.getObject(p, sliderEmnt, "XPATH")));
					driver.findElement(this.getObject(p, sliderEmnt, "XPATH")).click();
					Thread.sleep(8000);				
					
					// Clicking all the (+) Buttons to expand
					System.out.println("Size of the button before the list"+driver.findElements(By.xpath("//button[contains(@class, 'toggleSubProduct')]")).size());;
					List<WebElement> plusBtnList=driver.findElements(By.xpath("//button[contains(@class, 'toggleSubProduct')]"));
					System.out.println("Size of buttons:"+ plusBtnList.size());
					System.out.println("Button Elements:"+plusBtnList);
					
					WebElement Attribute_Name= driver.findElement(By.xpath("//div[@id='fav-desc']"));
					 At_nm=Attribute_Name.getText();
					 System.out.println("Attribute name on the second page"+ Attribute_Name);
					 
					 
			        for(WebElement plusBtn:plusBtnList)
			        {
			        	Thread.sleep(6000);
			        	System.out.println("Button:"+plusBtn.getText());
			        	plusBtn.click();
			        	System.out.println("Button Clicked");
			       
			        } 	
			        Thread.sleep(5000);
			        
					// Get the Table Values in the L2 Page
					List<WebElement> ele=driver.findElements(By.xpath("//div[contains(@class, 'product-region-data')]"));
					String dat = "";
					String otpt = "";
					int iter=0;
					System.out.println("Element size"+ele.size());
					for (WebElement e:ele)
					{
						
						dat = e.getText().trim();
						dat = dat.trim();
						if (dat.contains(""+'\n'))
						{
							dat = dat.replaceAll(Pattern.quote(""+'\n'), " ");
						}
						iter++;
						otpt=otpt+'\n'+dat;

						
					}
					Thread.sleep(4000);
					
					// Build Query to insert the record
					String[] chk = new String[150];
					String var_NA="[-]";
					
					for(int zag=1;zag<chk.length;zag+= 6)
					{
						query="insert into tmis_operational_iv values"
								+ "('"+At_nm+"','"+month_val+"','"+trend_val+"','"+slider_val+"',"
										+ "'"+chk[zag+0]+"','"+chk[zag+1]+"','"+chk[zag+2]+"',"
												+ "'"+chk[zag+3]+"','"+chk[zag+4]+"','"+chk[zag+5]+"')";
						res=stmt.executeUpdate(query);
					}							
					Thread.sleep(2000);
										
				}
				rate_loop_index++;
				}
				while((rate_loop_index < rate_sel_count) && (rate_sel_count != 0));
				System.out.println(" I am out of the loop the Rate loop Index is: "+rate_loop_index);
				
				if((rate_loop_index == rate_sel_count) && (rate_sel_count != 0))
				{
					rate_sel_ddn = new Select(driver.findElement(this.getObject(p, "PP_IV_ddn_RateSelector", "XPATH")));
					rate_sel_ddn.selectByIndex(0);
					System.out.println("Resetting the rate sel ddn back to first option");
				}
				
				
				Thread.sleep(4000);		

			}	
			// Back to OverView Page
			driver.findElement(this.getObject(p, "PP_IV_txt_toggleArrow", "XPATH")).click();
			Thread.sleep(5000);
			wait1.until(ExpectedConditions.visibilityOfElementLocated(this.getObject(p, opr_xpath[opr_attr], "XPATH")));
		}
	}
	/**
     * <p>Loops over the month and it clicks on the each attribute of Operational Column.
     * Then it fetches all the values of Level 2 page and store them in the respective Operational tables.
     * This is confined for the attributes-Authorization Rate
     * <p>Steps:
     * <ul><li>1. Do the Month selection
     * <li>2. Click on the each Attribute of the Operational Column
     * <li>3. Do the Trend selection
     * <li>4. Select the Mode as either 'Absolute' or 'Percent' in the slider
     * <li>5. Get the Table Values in the L2 Page
     * <li>6. Call the DB insert method to write the above read values</ul>
     * @param   p      The reference for Object Repositories
     * @param   objMonth      The month for which the values to be fetched
     * @return   None
     * */	
	
	public void perform_MonthlyOprInnerView_Adv(Properties p, String objMonth, String db_type) throws Exception

	{
		Select rate_sel_ddn;
		Select trend_dropdown=null;
		int rowcnt=0;
		String attrName;
		String At_nm;
		At_nm=null;
		
		// Set the Slider as Month
		driver.findElement(this.getObject(p, "PP_btn_Monthly", "XPATH")).click();
		Thread.sleep(6000);
		System.out.println("Monthly Level 2 Page, Data Extraction Process has started for: "+objMonth);
		//this.takescreenshot();
		
		// Wait for the Time Drop down to appear, and set the value got from the input sheet
		wait1.until(ExpectedConditions.elementToBeClickable(this.getObject(p, "PP_ddn_Time", "XPATH")));
	        time_dropdown = new Select(driver.findElement(this.getObject(p, "PP_ddn_Time", "XPATH")));
		time_dropdown.selectByVisibleText(objMonth);
		
		month_val = objMonth;
		
		// Wait for 3 seconds, to all the page to load the data
		Thread.sleep(3000);
		
		stmt=conn.createStatement();		
		Thread.sleep(1000);
		
		// Click on the each Attribute of the operational Column
		for (int opr_attr=8;opr_attr<9;opr_attr++)
		{
			driver.findElement(this.getObject(p, opr_xpath[opr_attr], "XPATH")).click();
			Thread.sleep(10000);
			wait1.until(ExpectedConditions.visibilityOfElementLocated(this.getObject(p, "PP_IV_txt_attrName", "XPATH")));
			 attrName = driver.findElement(this.getObject(p, "PP_IV_txt_attrName", "XPATH")).getText();			
			
			
			Select trend4ddn = new Select(driver.findElement(this.getObject(p, "PP_IV_ddn_Trend", "XPATH")));
			
			int Trend_Count=trend4ddn.getOptions().size();
			System.out.println(Trend_Count);
			
			
			// Click on the trend drop down one by one (one at a time)
			
			Select trend_dropdown2 = new Select(driver.findElement(this.getObject(p, "PP_IV_ddn_Trend", "XPATH")));
			List <WebElement> trendvalues=trend_dropdown2.getOptions();
			System.out.println("Trend Values options"+trendvalues.size());
			
			for (int i=0; i<trendvalues.size();i++)
			{
				System.out.println("Tren values::"+trendvalues.get(i).getText());
				Thread.sleep(4000);
				
			}
			
			
			for (trend_index=0; trend_index < Trend_Count; trend_index++)
			{	
				try
				{
					wait1.until(ExpectedConditions.elementToBeClickable(this.getObject(p, "PP_IV_ddn_Trend", "XPATH")));
				    trend_dropdown = new Select(driver.findElement(this.getObject(p, "PP_IV_ddn_Trend", "XPATH")));
					trend_dropdown.selectByIndex(trend_index);
					trend_val = trend_dropdown.getFirstSelectedOption().getText();
					System.out.println("Trend value:"+trend_val);
				}
				catch(Exception e){
					System.out.println("Exception on Trend dropdown Selection: "+e.getMessage());
					
				}

				
				Thread.sleep(4000);
				
				System.out.println("Checking Rate Selector Size");
				
				int rate_sel_count = 0;
				
				if(driver.findElements(this.getObject(p, "PP_IV_ddn_RateSelector", "XPATH")).size() != 0)
				{
				rate_sel_ddn = new Select(driver.findElement(this.getObject(p, "PP_IV_ddn_RateSelector", "XPATH")));
				rate_sel_count=rate_sel_ddn.getOptions().size();
				rate_sel_ddn.selectByIndex(0);
				
				}
				int rate_loop_index = 0;
				
				System.out.println("Rate Selector Size"+rate_sel_count);
				
				do
				{
				
				if (rate_loop_index > 0)
				{
					try {
						
						System.out.println("about to select the rate ddn"+rate_loop_index);
						rate_sel_ddn = new Select(driver.findElement(this.getObject(p, "PP_IV_ddn_RateSelector", "XPATH")));
						rate_sel_ddn.selectByIndex(rate_loop_index);
						Thread.sleep(5000);
						System.out.println("rate_loop_index"+rate_loop_index);
//						
					
					}
					catch(Exception e)
					{
						System.out.println("Exception on attribute Selection: "+e.getMessage());
						System.out.println("about to select the rate ddn"+rate_loop_index);
						Thread.sleep(5000);
						
					}
					
				}
				System.out.println("TrendIndex:"+trend_index);
				System.out.println("Rate Loop Index"+rate_loop_index);
				System.out.println("Trend Element value"+trend_dropdown.toString());
				
				Select trend_dropdown3 = new Select(driver.findElement(this.getObject(p, "PP_IV_ddn_Trend", "XPATH")));
				trend_dropdown3.selectByIndex(trend_index);
			
				for (String sliderEmnt : modeL2_list)
				{
					
					System.out.println("choosing abs/per"+sliderEmnt);
					// Set the Slider Value
					if(sliderEmnt == "PP_IV_btn_Abs")
					{
						slider_val = "Absolute";						
					}
					else if(sliderEmnt == "PP_IV_btn_Per")
					{
						slider_val = "Percent";
					}	
					
					Thread.sleep(6000);
					// Set the Slider position to 'Absolute' or 'Percent'
					wait1.until(ExpectedConditions.elementToBeClickable(this.getObject(p, sliderEmnt, "XPATH")));
					driver.findElement(this.getObject(p, sliderEmnt, "XPATH")).click();
					Thread.sleep(8000);				
					
					// Clicking all the (+) Buttons to expand
					System.out.println("Size of the button before the list"+driver.findElements(By.xpath("//button[contains(@class, 'toggleSubProduct')]")).size());;
					List<WebElement> plusBtnList=driver.findElements(By.xpath("//button[contains(@class, 'toggleSubProduct')]"));
					System.out.println("Size of buttons:"+ plusBtnList.size());
					System.out.println("Button Elements:"+plusBtnList);
					
					WebElement Attribute_Name= driver.findElement(By.xpath("//div[@id='fav-desc']"));
					 At_nm=Attribute_Name.getText();
					 System.out.println("Attribute name on the second page"+ Attribute_Name);
					 
					 
			        for(WebElement plusBtn:plusBtnList)
			        {
			        	Thread.sleep(6000);
			        	System.out.println("Button:"+plusBtn.getText());
			        	plusBtn.click();
			        	System.out.println("Button Clicked");
			       
			        } 	
			        Thread.sleep(5000);
			        
					// Get the Table Values in the L2 Page
					List<WebElement> ele=driver.findElements(By.xpath("//div[contains(@class, 'product-region-data')]"));
					String dat = "";
					String otpt = "";
					int iter=0;
					System.out.println("Element size"+ele.size());
					for (WebElement e:ele)
					{
						
						dat = e.getText().trim();
						dat = dat.trim();
						if (dat.contains(""+'\n'))
						{
							dat = dat.replaceAll(Pattern.quote(""+'\n'), " ");
						}
						iter++;
						otpt=otpt+'\n'+dat;

						
					}
					Thread.sleep(4000);
					
					// Build Query to insert the record
					String[] chk = new String[150];
					String var_NA="[-]";
					
					for(int zag=1;zag<chk.length;zag+= 6)
								{
									query="insert into tmis_operational_iv values"
											+ "('"+At_nm+"','"+month_val+"','"+trend_val+"','"+slider_val+"',"
													+ "'"+chk[zag+0]+"','"+chk[zag+1]+"','"+chk[zag+2]+"',"
															+ "'"+chk[zag+3]+"','"+chk[zag+4]+"','"+chk[zag+5]+"')";
									res=stmt.executeUpdate(query);
								}							
					Thread.sleep(2000);
										
				}
				rate_loop_index++;
				}
				while((rate_loop_index < rate_sel_count) && (rate_sel_count != 0));
				System.out.println(" I am out of the loop the Rate loop Index is: "+rate_loop_index);
				
				if((rate_loop_index == rate_sel_count) && (rate_sel_count != 0))
				{
					rate_sel_ddn = new Select(driver.findElement(this.getObject(p, "PP_IV_ddn_RateSelector", "XPATH")));
					rate_sel_ddn.selectByIndex(0);
					System.out.println("Resetting the rate sel ddn back to first option");
				}
				
				
				Thread.sleep(4000);		

			}	
			// Back to OverView Page
			driver.findElement(this.getObject(p, "PP_IV_txt_toggleArrow", "XPATH")).click();
			Thread.sleep(5000);
			wait1.until(ExpectedConditions.visibilityOfElementLocated(this.getObject(p, opr_xpath[opr_attr], "XPATH")));
		}
	}
	
	/**
     * <p>Loops over the month and it clicks on the each attribute of Operational Column.
     * Then it fetches all the values of Level 2 page and store them in the respective Operational tables.
     * This is confined for the attributes-Total Employee Population and Total Employee Cost
     * <p>Steps:
     * <ul><li>1. Do the Month selection
     * <li>2. Click on the each Attribute of the Operational Column
     * <li>3. Do the Trend selection
     * <li>4. Select the Mode as either 'Absolute' or 'Percent' in the slider
     * <li>5. Get the Table Values in the L2 Page
     * <li>6. Call the DB insert method to write the above read values</ul>
     * @param   p      The reference for Object Repositories
     * @param   objMonth      The month for which the values to be fetched
     * @return   None
     * */	
	
	public void perform_MonthlyOprInnerView_EmpPop(Properties p, String objMonth, String db_type) throws Exception

	{
		Select rate_sel_ddn;
		Select trend_dropdown=null;
		int rowcnt=0;
		String attrName;
		String At_nm;
		At_nm=null;
		
		// Set the Slider as Month
		driver.findElement(this.getObject(p, "PP_btn_Monthly", "XPATH")).click();
		Thread.sleep(6000);
		System.out.println("Monthly Level 2 Page, Data Extraction Process has started for: "+objMonth);
		//this.takescreenshot();
		
		// Wait for the Time Drop down to appear, and set the value got from the input sheet
		wait1.until(ExpectedConditions.elementToBeClickable(this.getObject(p, "PP_ddn_Time", "XPATH")));
	        time_dropdown = new Select(driver.findElement(this.getObject(p, "PP_ddn_Time", "XPATH")));
		time_dropdown.selectByVisibleText(objMonth);
		
		month_val = objMonth;
		
		// Wait for 3 seconds, to all the page to load the data
		Thread.sleep(3000);
		
		stmt=conn.createStatement();		
		Thread.sleep(1000);
		
		// Click on the each Attribute of the operational Column
		for (int opr_attr=5;opr_attr<7;opr_attr++)
		{
			driver.findElement(this.getObject(p, opr_xpath[opr_attr], "XPATH")).click();
			Thread.sleep(10000);
			wait1.until(ExpectedConditions.visibilityOfElementLocated(this.getObject(p, "PP_IV_txt_attrName", "XPATH")));
			 attrName = driver.findElement(this.getObject(p, "PP_IV_txt_attrName", "XPATH")).getText();			
			
			
			Select trend4ddn = new Select(driver.findElement(this.getObject(p, "PP_IV_ddn_Trend", "XPATH")));
			
			int Trend_Count=trend4ddn.getOptions().size();
			System.out.println(Trend_Count);
			
			
			// Click on the trend drop down one by one (one at a time)
			
			Select trend_dropdown2 = new Select(driver.findElement(this.getObject(p, "PP_IV_ddn_Trend", "XPATH")));
			List <WebElement> trendvalues=trend_dropdown2.getOptions();
			System.out.println("Trend Values options"+trendvalues.size());
			
			for (int i=0; i<trendvalues.size();i++)
			{
				System.out.println("Tren values::"+trendvalues.get(i).getText());
				Thread.sleep(4000);
				
			}
			
			
			for (trend_index=0; trend_index < Trend_Count; trend_index++)
			{	
				try
				{
					wait1.until(ExpectedConditions.elementToBeClickable(this.getObject(p, "PP_IV_ddn_Trend", "XPATH")));
				    trend_dropdown = new Select(driver.findElement(this.getObject(p, "PP_IV_ddn_Trend", "XPATH")));
					trend_dropdown.selectByIndex(trend_index);
					trend_val = trend_dropdown.getFirstSelectedOption().getText();
					System.out.println("Trend value:"+trend_val);
				}
				catch(Exception e){
					System.out.println("Exception on Trend dropdown Selection: "+e.getMessage());
					
				}

				
				Thread.sleep(4000);
				
				System.out.println("Checking Rate Selector Size");
				
				int rate_sel_count = 0;
				
				if(driver.findElements(this.getObject(p, "PP_IV_ddn_RateSelector", "XPATH")).size() != 0)
				{
				rate_sel_ddn = new Select(driver.findElement(this.getObject(p, "PP_IV_ddn_RateSelector", "XPATH")));
				rate_sel_count=rate_sel_ddn.getOptions().size();
				rate_sel_ddn.selectByIndex(0);
				
				}
				int rate_loop_index = 0;
				
				System.out.println("Rate Selector Size"+rate_sel_count);
				
				do
				{
				
				if (rate_loop_index > 0)
				{
					try {
						
						System.out.println("about to select the rate ddn"+rate_loop_index);
						rate_sel_ddn = new Select(driver.findElement(this.getObject(p, "PP_IV_ddn_RateSelector", "XPATH")));
						rate_sel_ddn.selectByIndex(rate_loop_index);
						Thread.sleep(5000);
						System.out.println("rate_loop_index"+rate_loop_index);
//						
					
					}
					catch(Exception e)
					{
						System.out.println("Exception on attribute Selection: "+e.getMessage());
						System.out.println("about to select the rate ddn"+rate_loop_index);
						Thread.sleep(5000);
						
					}
					
				}
				System.out.println("TrendIndex:"+trend_index);
				System.out.println("Rate Loop Index"+rate_loop_index);
				System.out.println("Trend Element value"+trend_dropdown.toString());
				
				Select trend_dropdown3 = new Select(driver.findElement(this.getObject(p, "PP_IV_ddn_Trend", "XPATH")));
				trend_dropdown3.selectByIndex(trend_index);
			
				for (String sliderEmnt : modeL2_list)
				{
					
					System.out.println("choosing abs/per"+sliderEmnt);
					// Set the Slider Value
					if(sliderEmnt == "PP_IV_btn_Abs")
					{
						slider_val = "Absolute";						
					}
					else if(sliderEmnt == "PP_IV_btn_Per")
					{
						slider_val = "Percent";
					}	
					
					Thread.sleep(6000);
					// Set the Slider position to 'Absolute' or 'Percent'
					wait1.until(ExpectedConditions.elementToBeClickable(this.getObject(p, sliderEmnt, "XPATH")));
					driver.findElement(this.getObject(p, sliderEmnt, "XPATH")).click();
					Thread.sleep(8000);				
					
					// Clicking all the (+) Buttons to expand
					System.out.println("Size of the button before the list"+driver.findElements(By.xpath("//button[contains(@class, 'toggleSubProduct')]")).size());;
					List<WebElement> plusBtnList=driver.findElements(By.xpath("//button[contains(@class, 'toggleSubProduct')]"));
					System.out.println("Size of buttons:"+ plusBtnList.size());
					System.out.println("Button Elements:"+plusBtnList);
					
					WebElement Attribute_Name= driver.findElement(By.xpath("//div[@id='fav-desc']"));
					 At_nm=Attribute_Name.getText();
					 System.out.println("Attribute name on the second page"+ Attribute_Name);
					 
					 
			        for(WebElement plusBtn:plusBtnList)
			        {
			        	Thread.sleep(6000);
			        	System.out.println("Button:"+plusBtn.getText());
			        	plusBtn.click();
			        	System.out.println("Button Clicked");
			       
			        } 	
			        Thread.sleep(5000);
			        
					// Get the Table Values in the L2 Page
					List<WebElement> ele=driver.findElements(By.xpath("//div[contains(@class, 'product-region-data')]"));
					String dat = "";
					String otpt = "";
					int iter=0;
					System.out.println("Element size"+ele.size());
					for (WebElement e:ele)
					{
						
						dat = e.getText().trim();
						dat = dat.trim();
						if (dat.contains(""+'\n'))
						{
							dat = dat.replaceAll(Pattern.quote(""+'\n'), " ");
						}
						iter++;
						otpt=otpt+'\n'+dat;

						
					}
					Thread.sleep(4000);
					
					// Build Query to insert the record
					String[] chk = new String[150];
					
					for(int zag=1;zag<chk.length;zag+= 4)
								{
									query="insert into tmis_operational_emppop values"
											+ "('"+At_nm+"','"+month_val+"','"+trend_val+"','"+slider_val+"',"
													+ "'"+chk[zag+0]+"','"+chk[zag+1]+"','"+chk[zag+2]+"',"
															+ "'"+chk[zag+3]+"')";
									res=stmt.executeUpdate(query);
								}							
					Thread.sleep(2000);
										
				}
				rate_loop_index++;
				}
				while((rate_loop_index < rate_sel_count) && (rate_sel_count != 0));
				System.out.println(" I am out of the loop the Rate loop Index is: "+rate_loop_index);
				
				if((rate_loop_index == rate_sel_count) && (rate_sel_count != 0))
				{
					rate_sel_ddn = new Select(driver.findElement(this.getObject(p, "PP_IV_ddn_RateSelector", "XPATH")));
					rate_sel_ddn.selectByIndex(0);
					System.out.println("Resetting the rate sel ddn back to first option");
				}
				
				
				Thread.sleep(4000);		

			}	
			// Back to OverView Page
			driver.findElement(this.getObject(p, "PP_IV_txt_toggleArrow", "XPATH")).click();
			Thread.sleep(5000);
			wait1.until(ExpectedConditions.visibilityOfElementLocated(this.getObject(p, opr_xpath[opr_attr], "XPATH")));
		}
	}
	
	/**
     * <p>To append footer and to save the HTML File
     */
	public void reportWrapUp()
	{
		testreport.appendFooter();
		try{
			testreport.WriteToFile();
		}
		catch(Exception e)
		{
			System.out.println("Error while saving the report file: "+e.getMessage());
		}
	}
	/**
     * <p>Loops over the attributes of Customer, Financial, Strategic and Operational.
     * To find out if any empty data is present.
     * @param   status_Field      The column in which empty validation should be checked
     * @return   {@code status} as either <B>Pass</B> or <B>Fail</B>, to indicate the presence of empty attribute.
     */
	public String validateEmptyData(String status_Field) throws Exception{
		String status;
		failcnt = 0;
		status = "Pass";
		failfields="";
		
		switch (status_Field) {
        case "Customer":
			for (int attr=0;attr<10;attr++)
			{
				String testStr = cus_val[attr].trim();
				System.out.println("cus_val[attr]: " +cus_val[attr]);			
				if(testStr.equalsIgnoreCase("-"))
				{
					status = "Fail";
					if (failcnt > 0)
						failfields = failfields+", "+cus_val[attr];
					else
						failfields = failfields+cus_val[attr];
					failcnt = failcnt + 1;
				}
			}
		
        case "Financial":
			for (int attr=0;attr<10;attr++)
			{
				String testStr = fin_val[attr].trim();
							
				if(testStr.equalsIgnoreCase("-"))
				{
					status = "Fail";
					if (failcnt > 0)
						failfields = failfields+", "+fin_val[attr];
					else
						failfields = failfields+fin_val[attr];
					failcnt = failcnt + 1;
				}
			}
        case "Operational":
			for (int attr=0;attr<9;attr++)
			{
				String testStr = opr_val[attr].trim();
							
				if(testStr.equalsIgnoreCase("-"))
				{
					status = "Fail";
					if (failcnt > 0)
						failfields = failfields+", "+opr_val[attr];
					else
						failfields = failfields+opr_val[attr];
					failcnt = failcnt + 1;
				}
			}
        case "Strategic":
			for (int attr=0;attr<9;attr++)
			{
				String testStr = str_val[attr].trim();
							
				if(testStr.equalsIgnoreCase("-"))
				{
					status = "Fail";
					if (failcnt > 0)
						failfields = failfields+", "+str_val[attr];
					else
						failfields = failfields+str_val[attr];
					failcnt = failcnt + 1;
				}
			}
		}
		System.out.println("failfields: "+failfields);
		return status;
	}
	/**
     * <p>To find an object by any of the locators in Selenium.
     * <ul>
     * <li>XPATH
     * <li>CLASSNAME
     * <li>NAME
     * <li>ID
     * <li>CSS
     * <li>LINK
     * <li>PARTIALLINK
     * </ul>
     * @param   p      The reference to the Object Repository
     * @param   objectName      The name of the Object
     * @param   objectType      The type to the Object
     */
	private By getObject(Properties p,String objectName,String objectType) throws Exception{
		// Find by XPath
		if(objectType.equalsIgnoreCase("XPATH")){
			return By.xpath(p.getProperty(objectName));
		}
		// Find by Class
		else if(objectType.equalsIgnoreCase("CLASSNAME")){
			
			return By.className(p.getProperty(objectName));
			
		}
		// Find by Name
		else if(objectType.equalsIgnoreCase("NAME")){
			
			return By.name(p.getProperty(objectName));
			
		}
		// Find by ID
		else if(objectType.equalsIgnoreCase("ID")){
					
			return By.id(p.getProperty(objectName));
					
		}
		// Find by CSS
		else if(objectType.equalsIgnoreCase("CSS")){
			
			return By.cssSelector(p.getProperty(objectName));
			
		}
		// Find by Link
		else if(objectType.equalsIgnoreCase("LINK")){
			
			return By.linkText(p.getProperty(objectName));
			
		}
		// Find by Partial Link
		else if(objectType.equalsIgnoreCase("PARTIALLINK")){
			
			return By.partialLinkText(p.getProperty(objectName));
			
		}else
		{
			throw new Exception("Wrong object type");
		}
	}
	/**
     * <p>To capture screenshot and save it in the Output folder
     */
	public void takescreenshot(){
		// Maximize the window to get full screen
		driver.manage().window().maximize();
		
		// Take screenshot and store as a PNG file
		File scrnshot=((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
		
		try{
			// Save the screenshot in the output folder
			FileUtils.copyFile(scrnshot,new File(System.getProperty("user.dir")+"\\test-output\\screenshots\\"+System.currentTimeMillis()+".png"));
		}
		catch(IOException e){
			System.out.println("Exception on Screenshot: "+e.getMessage());
		}
	}
	/**
     * <p>To start video recording and save it in the Output folder
     */
    public void startRecording() throws Exception
    {    
        File file = new File(System.getProperty("user.dir")+"\\test-output\\videos\\");
                        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = screenSize.width;
        int height = screenSize.height;
                         
        Rectangle captureSize = new Rectangle(0,0, width, height);
                         
        GraphicsConfiguration gc = GraphicsEnvironment
            .getLocalGraphicsEnvironment()
            .getDefaultScreenDevice()
            .getDefaultConfiguration();

        this.screenRecorder = new TestRecorder(gc, captureSize,
            new Format(MediaTypeKey, MediaType.FILE, MimeTypeKey, MIME_AVI),
            new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                 CompressorNameKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                 DepthKey, 24, FrameRateKey, Rational.valueOf(15),
                 QualityKey, 1.0f,
                 KeyFrameIntervalKey, 15 * 60),
            new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, "black",
                 FrameRateKey, Rational.valueOf(30)),
            null, file, "Test");
        	this.screenRecorder.start();
    }
    /**
     * <p>To stop video recording
     */
    public void stopRecording() throws Exception
    {
    	this.screenRecorder.stop();
    }
}
