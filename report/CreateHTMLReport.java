package report;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CreateHTMLReport {
	StringBuilder htmlStringBuilder;
	BufferedReader br = null;
	FileReader fr = null;
	String currline;
	String timeStamp; 
	
	public CreateHTMLReport()
	{
		timeStamp = new SimpleDateFormat("ddMMMyyyy_HHmmss").format(Calendar.getInstance().getTime());
	}
	
    public void designHeader(String filename)
    {
        try {
            //define a HTML String Builder
            htmlStringBuilder=new StringBuilder();
            
            fr = new FileReader(filename);
            br = new BufferedReader(fr);
            
            while((currline = br.readLine()) != null)
            {
            	htmlStringBuilder.append(currline);
            }
            
            //write html string content to a file
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void appendBody(String mnth, String trnd, String rgn, String mode, String cusfailfields, String finfailfields, String oprfailfields, String strfailfields)
    {
    	htmlStringBuilder.append("<tr align=\"center\">");
    	htmlStringBuilder.append("<td>"+mnth+"</td>");
    	htmlStringBuilder.append("<td>"+trnd+"</td>");
    	htmlStringBuilder.append("<td>"+rgn+"</td>");
    	htmlStringBuilder.append("<td>"+mode+"</td>");
    	htmlStringBuilder.append("<td>"+cusfailfields+"</td>");
    	htmlStringBuilder.append("<td>"+finfailfields+"</td>");
    	htmlStringBuilder.append("<td>"+oprfailfields+"</td>");
    	htmlStringBuilder.append("<td>"+strfailfields+"</td>");
    	htmlStringBuilder.append("</tr>");
    }
    
    //public void appendFooter(String datetime, int totalfailcnt)
    public void appendFooter()
    {
    	htmlStringBuilder.append("</table>");
    	htmlStringBuilder.append("<BR><BR>");
    	htmlStringBuilder.append("<Table>");
    	htmlStringBuilder.append("<tr align=\"Left\"><td width='100%' colspan='2' bgcolor='#4169E1'><b><font face='Tahoma' size='3' color='#FFFFFF'>Summary</font></b></td></tr>");
    	htmlStringBuilder.append("<tr align=\"center\"><td width='45%' bgcolor='#FCF3CF'><b><font face='Tahoma' size='2'>Automated Date and Time</b></td><td width='55%' bgcolor='#FCF3CF'><font face='Tahoma' size='2'>"+timeStamp+"</font></td></tr>");
//    	htmlStringBuilder.append("<tr align=\"center\"><td width='45%' bgcolor='#F6DDCC'><b><font face='Tahoma' size='2'>Total Number of Data Loading issues in Financial Fields</b></td><td width='55%' bgcolor='#F6DDCC'><font face='Tahoma' size='2'>"+totalfailcnt+"</font></td></tr>");
    	htmlStringBuilder.append("</Table>");
    	htmlStringBuilder.append("</body>");
    	htmlStringBuilder.append("</html>");
    }
    public void WriteToFile() throws IOException {
    	String content = htmlStringBuilder.toString();
    	
    	String projectPath = System.getProperty("user.dir")+"\\test-output\\HTML";
        String tempFile = projectPath+File.separator+"Report_"+timeStamp+".html";
        
        File file = new File(tempFile);
        
        // if file does exists, then delete and create a new file
        if (file.exists()) {
            try {
                File newFileName = new File(projectPath + File.separator+ "backup_"+tempFile);
                file.renameTo(newFileName);
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //write to file with OutputStreamWriter
        OutputStream outputStream = new FileOutputStream(file.getAbsoluteFile());
        Writer writer=new OutputStreamWriter(outputStream);
        writer.write(content);
        writer.close();
    }
}