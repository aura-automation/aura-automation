/**	   Copyright [2009] [www.apartech.com]


**/
package com.apartech.auraconfig.resources.parser;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Comment;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import com.apartech.auraconfig.resources.DiffAttribute;
import com.apartech.auraconfig.resources.InvalidAttribute;
import com.apartech.auraconfig.resources.Resource;
import com.apartech.auraconfig.resources.ResourceConstants;
import com.apartech.auraconfig.resources.ResourceHelper;
import com.apartech.auraconfig.resources.ResourceStatsHelper;
import com.apartech.common.Constants.DeployValues;
import com.apartech.common.deploy.DeployInfo;
import com.apartech.common.exception.DeployException;
import com.apartech.common.properties.helper.PropertyHelper;

public class ResourceXMLWriter {
	int cnt = 0;
	StringBuffer idAllArray = new StringBuffer();
	StringBuffer idDiffArray = new StringBuffer();

	boolean syncOperation = false;
	String displayStatus = "none";
	private static final Log logger  = LogFactory.getLog(ResourceXMLWriter.class);
	public static String newline = System.getProperty("line.separator");
	
	public void createResourceXMLFile(Resource resources,DeployInfo deployInfo)
		throws DeployException{
		FileWriter reportFileOutputStream = null;
		String syncResourceXML = deployInfo.getSyncResourceXML();
		Element root = new Element(resources.getName());

		// set this so that in report we dont show any incoming resources
		if (deployInfo.getOperationMode().equalsIgnoreCase(DeployValues.OPERATION_MODE_SYNC )) 
			syncOperation = true;
		else{
			displayStatus = "show";
		}
		try {
			
			//DocType type = new DocType("xsi:noNamespaceSchemaLocation", "resources.xsd");
			File reportFile = new File(deployInfo.getSyncReportLocation());
			reportFile.getParentFile().mkdir();
			reportFileOutputStream =  new FileWriter (reportFile);
				reportFileOutputStream.write("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("<html lang=\"en\" dir=\"ltr\">");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("<head>");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("<meta content=\"text/html; charset=UTF-8\" http-equiv=\"Content-Type\"/>");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("<title>  Comparison Report </title>");
				reportFileOutputStream.write(newline);
				//reportFileOutputStream.write("<link rel=\"stylesheet\" type=\"text/css\" href=\"interface_styles.css\"/>");
				
				
				
				
				
				
				

				reportFileOutputStream.write("<style>");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("#wrap {");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("background:#FFFFFF none repeat scroll 0 0;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("border-left:1px solid #BEC9D1;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("border-right:1px solid #BEC9D1;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("margin:0 auto;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("padding-left:20px;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("padding-right:20px;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("text-align:left;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("width:77%;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("}");
				reportFileOutputStream.write(newline);

				reportFileOutputStream.write("body {");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("-x-system-font:none;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("background:#2E3A41 url(/content/style/images/rg/gradients/gradient_new.gif) repeat-x scroll left top;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("color:#444444;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("font-family:verdana,geneva,lucida,'lucida grande',arial,helvetica,sans-serif;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("font-size:small;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("font-size-adjust:none;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("font-stretch:normal;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("font-style:normal;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("font-variant:normal;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("font-weight:normal;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("line-height:1.7em;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("margin:0;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("padding:0;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("}");
				reportFileOutputStream.write(newline);

				reportFileOutputStream.write("td, th, p, li {");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("-x-system-font:none;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("font-family:verdana,geneva,lucida,'lucida grande',arial,helvetica,sans-serif;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("font-size:8pt;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("font-size-adjust:none;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("font-stretch:normal;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("font-style:normal;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("font-variant:normal;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("font-weight:normal;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("line-height:normal;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("}");
				reportFileOutputStream.write(newline);



				reportFileOutputStream.write(".normal {");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("font-weight:normal;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("}");
				reportFileOutputStream.write(newline);

				reportFileOutputStream.write(".page {");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("color: #000000;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("}");
				reportFileOutputStream.write(newline);


				reportFileOutputStream.write(".tdiff {");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("background: #FFFF66;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("border:1px solid #BEC9D1;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("}");
				reportFileOutputStream.write(newline);

				reportFileOutputStream.write(".tinvalid {");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("background: #CC0000;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("border:1px solid #BEC9D1;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("}");
				reportFileOutputStream.write(newline);

				reportFileOutputStream.write(".tsame {");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("background: #FFFFFF;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("border:1px solid #BEC9D1;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("}");
				reportFileOutputStream.write(newline);

				reportFileOutputStream.write(".tnew {");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("background: #CC99CC;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("border:1px solid #BEC9D1;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("}");
				reportFileOutputStream.write(newline);

				reportFileOutputStream.write(".textra{");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("background: #FF9966;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("border:1px solid #BEC9D1;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("}");
				reportFileOutputStream.write(newline);

				reportFileOutputStream.write(".heading{");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("color:#333333;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("font-family:verdana,geneva,lucida,'lucida grande',arial,helvetica,sans-serif;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("font-size:30px;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("}");
				reportFileOutputStream.write(newline);

				reportFileOutputStream.write(".tcat {");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("-x-system-font:none;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("background:#999999  repeat-x scroll left top;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("color:#444444;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("font-family:verdana,geneva,lucida,'lucida grande',arial,helvetica,sans-serif;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("font-size:12px;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("font-size-adjust:none;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("font-stretch:normal;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("font-style:normal;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("font-variant:normal;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("font-weight:bold;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("line-height:normal;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("}");
				reportFileOutputStream.write(newline);

				reportFileOutputStream.write(".tcat a:link {");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("color:#FFFFFF;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("text-decoration:none;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("}");
				reportFileOutputStream.write(newline);

				reportFileOutputStream.write(".tcat a:visited {");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("color:#FFFFFF;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("text-decoration:none;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("}");
				reportFileOutputStream.write(newline);

				reportFileOutputStream.write(".tcat a:hover, .tcat a:active {");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("color:#FFFFFF;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("text-decoration:none;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("}");
				reportFileOutputStream.write(newline);

				reportFileOutputStream.write(".thead a:link {");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("color:#0049AA;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("text-decoration:underline;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("}");
				reportFileOutputStream.write(newline);

				reportFileOutputStream.write(".thead a:visited {");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("color:#23488C;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("text-decoration:underline;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("}");
				reportFileOutputStream.write(newline);


				reportFileOutputStream.write(".thead a:hover, .thead a:active {");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("color:#0067F0;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("text-decoration:none;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("}");
				reportFileOutputStream.write(newline);

				reportFileOutputStream.write(".tborder {");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("	border-collapse: collapse;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("	background:#FFFFFF none repeat scroll 0 0;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("	border:1px solid #BEC9D1;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("	color:#000000;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("}");
				reportFileOutputStream.write(newline);

				reportFileOutputStream.write(".toptable {");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("width:92%;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("}");
				reportFileOutputStream.write(newline);

				reportFileOutputStream.write(".alt1, .alt1Active {");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("background:#F1F1F1 none repeat scroll 0 0;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("color:#000000;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("}");
				reportFileOutputStream.write(newline);

				reportFileOutputStream.write(".alt2, .alt2Active {");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("background:#EEF0F2 none repeat scroll 0 0;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("color:#000000;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("}");
				reportFileOutputStream.write(newline);

				reportFileOutputStream.write("a:link {");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("color:#0049AA;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("text-decoration:underline;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("}");
				reportFileOutputStream.write(newline);

				reportFileOutputStream.write("a:visited {");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("color:#0049AA;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("text-decoration:underline;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("}");
				reportFileOutputStream.write(newline);

				reportFileOutputStream.write("a:hover, a:active {");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("color:#0067F0;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("text-decoration:none;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("}");
				reportFileOutputStream.write(newline);




				reportFileOutputStream.write(".crumbrow {");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("-x-system-font:none;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("color:#999999;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("word-wrap:break-word;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("font-family:verdana,geneva,lucida,'lucida grande',arial,helvetica,sans-serif;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("font-size:7pt;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("font-size-adjust:none;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("font-stretch:normal;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("font-style:normal;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("font-variant:normal;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("font-weight:normal;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("line-height:normal;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("background:#F1F1F1 none repeat scroll 0 0;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("}");
				reportFileOutputStream.write(newline);


					
				reportFileOutputStream.write(".crumbrow a:link {");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("color:#999999;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("text-decoration:underline;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("}");
				reportFileOutputStream.write(newline);

				reportFileOutputStream.write(".crumbrow a:visited {");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("color:#999999;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("text-decoration:underline;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("}");
				reportFileOutputStream.write(newline);

				reportFileOutputStream.write(".crumbrow a:hover, a:active {");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("color:#000000;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("text-decoration:none;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("}");
				reportFileOutputStream.write(newline);

				reportFileOutputStream.write("</style>");
				reportFileOutputStream.write(newline);

				
				
				
				
				
				
				
				
				
				
				
				
				
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("<script type=\"text/javascript\">");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("function toggleBoxArray(idArray){");
				reportFileOutputStream.write("if (document.getElementById(\"mainTable\").style.display == \"\") {");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("	show = \"none\";");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("	} else {");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("	show = \"\";");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("	}");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("	document.getElementById(\"mainTable\").style.display = show;");
				reportFileOutputStream.write(newline);

				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("	for (i=0; i < idArray.length;i++){");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("		id = idArray[i];");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("	document.getElementById(id).style.display = show;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("		}");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("	}");
				reportFileOutputStream.write(newline);

				reportFileOutputStream.write(newline);
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("function toggleBox(id){");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("if (document.getElementById(id).style.display == \"\") {");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("show = \"none\";");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("} else {");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("show = \"\";");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("}");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("document.getElementById(id).style.display = show;");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("}");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("</script>");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("</head>");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("<body>");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("<div id=\"wrap\">");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("<table id=\"mainTable\" style=\"display: none;\" >");
				reportFileOutputStream.write("</table>");
				
				reportFileOutputStream.write("<table width=\"100%\" class=\"tborder\">");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("		<tr>");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("			<td width=\"30%\" align=\"left\">");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("			<table class=\"tborder\">");
				reportFileOutputStream.write(newline);
				
				reportFileOutputStream.write("				<tr>");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("					<td class=\"tcat\">Server</td>");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("					<td><strong>" + deployInfo.getHost() + "</strong></td>");
				reportFileOutputStream.write(newline);
		
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("				</tr>");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("				<tr>");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("					<td class=\"tcat\">Time</td>");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("					<td><strong>" + Calendar.getInstance().getTime()+"</strong></td>");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("				<tr class=\"alt2Active\">");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("					<td class=\"tcat\">Operation</td>");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("					<td><strong>" + deployInfo.getOperationMode() +"</strong></td>");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("				</tr>");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("				<tr>");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("					<td class=\"tcat\">Source</td>");
				reportFileOutputStream.write(newline);
				if (deployInfo.getSourceDeployInfo()!=null){
					reportFileOutputStream.write("					<td>"+ deployInfo.getSourceDeployInfo().getHost() +"</td>");
					reportFileOutputStream.write(newline);
				}else{
					reportFileOutputStream.write("					<td>"+ deployInfo.getResourceXML() +"</td>");
					reportFileOutputStream.write(newline);
				}
		
				reportFileOutputStream.write("				</tr>");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("				<tr class=\"alt2Active\">");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("					<td class=\"tcat\">Target</td>");
				reportFileOutputStream.write(newline);
				if (deployInfo.getSourceDeployInfo()!=null){
					reportFileOutputStream.write("					<td>"+ deployInfo.getHost() +"</td>");
					reportFileOutputStream.write(newline);
				}else{
					reportFileOutputStream.write("					<td>"+ deployInfo.getSyncResourceXML()+"</td>");
					reportFileOutputStream.write(newline);
				}
	
				
				reportFileOutputStream.write("				</tr>");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("			</table>");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("			</td>");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("			<td align=\"center\">");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("				<div class=\"heading\">");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("				Aura Config Lite");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("				</div>");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("			</td>");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("			<td width=\"30%\" valign=\"bottom\" align=\"right\">");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("			<table class=\"tborder\">");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("				<tr>");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("					<td class=\"tdiff\">Yellow</td>");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("					<td><strong>Different</strong></td>");
				reportFileOutputStream.write(newline);
		
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("				</tr>");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("				<tr>");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("					<td class=\"tsame\">White</td>");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("					<td><strong>Same</strong></td>");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("				</tr>");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("				<tr>");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("		<td class=\"tnew\">Purple</td>");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("		<td><strong>Missing in Target</strong></td>");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("				</tr>");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("				<tr>");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("					<td class=\"textra\">Orange</td>");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("					<td><strong>Missing in Source </strong></td>");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("				<tr>");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("		<td class=\"tinvalid\">Red</td>");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("		<td><strong>Invalid </strong></td>");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("				</tr>");

				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("				</tr>");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("				<tr>");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("					<td>&nbsp;</td>");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("					<td></td>");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("				</tr>");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("			</table>");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("			</td>");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("		</tr>");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("	</table>");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("<br>");

				reportFileOutputStream.write("<table class=\"tborder\" width=\"10%\" align=\"right\" cellspacing=\"0\" cellpadding=\"0\" >");
				reportFileOutputStream.write(newline);
				idAllArray.append("[");
				idDiffArray.append("[");
				
				createResourceXMLFile(root ,resources.getChildren(),deployInfo,null,"Modified",true);
				cnt = 0;

				idAllArray.append("]");
				idDiffArray.append("]");
				reportFileOutputStream.write("<td class=\"alt2Active\"  width=\"50\">");
				reportFileOutputStream.write("<a href=\"javascript:toggleBoxArray(" +idAllArray.toString()+ ")\"> + </a>All<td>");
				reportFileOutputStream.write("<td class=\"alt2Active\"  width=\"50\">");
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("<a href=\"javascript:toggleBoxArray(" +idDiffArray.toString()+ ")\"> + </a>Diff<td>");

				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("</tr>");
				reportFileOutputStream.write(newline);

				reportFileOutputStream.write("</table>");  
				reportFileOutputStream.write(newline);
				reportFileOutputStream.write("<br>");

				//		root.setAttribute(new org.jdom.Attribute("noNamespaceSchemaLocation", "resources.xsd"));
						
				createResourceXMLFile(root ,resources.getChildren(),deployInfo,reportFileOutputStream,"Modified",false);
				Document doc = new Document(root);
				
				/** 
				 * 	Not supported by WAS JDOM version 
					Document doc = new Document(root);
					doc.setRootElement(root);
				**/
		
				XMLOutputter outputter = new XMLOutputter();
				outputter.setIndent(true);
				outputter.setNewlines(true);
			
			// create parent directory
			(new File(syncResourceXML)).getParentFile().mkdir();
			FileOutputStream fos = new FileOutputStream(new File(syncResourceXML));
			BufferedOutputStream fileOutputStream = new BufferedOutputStream(fos );
			outputter.output(doc, fileOutputStream);
			
			fileOutputStream.flush();
			fileOutputStream.close();
			fos.flush();
			fos.close();

			reportFileOutputStream.write("</html>");

			reportFileOutputStream.flush();
			reportFileOutputStream.close();
		}
		catch (IOException e) {
			
			System.err.println(e);
		}finally{
			/** try{
			//	reportFileOutputStream.close();
			}catch(IOException e){
				
			}**/
			
		}
	}

	
	
	
	private void createResourceXMLFile(Element element, Vector resources,
			DeployInfo deployInfo, FileWriter reportFileOutputStream,String changeStatus,boolean arrayStringOnly)
		throws DeployException, IOException{

		Iterator childrenIterator = resources.iterator();
		
		while (childrenIterator.hasNext()){
			
			String bgcolor="alt2Active";
			
			
			
			Resource childResource = (Resource)childrenIterator.next();
			if (!ResourceHelper.isResourceDummy(childResource,childResource.getResourceMetaData())){
				cnt++;
				boolean isDiff = false;
				ResourceStatsHelper resourceStatsHelper = new ResourceStatsHelper ();
				resourceStatsHelper.setResourceAttributeStats(childResource);
	
				int modifiedCnt = childResource.getResourceStats().getModifiedAttributeCnt() ;
				int newCnt = childResource.getResourceStats().getNewAttributeCnt() ;
				int incomingCnt = childResource.getResourceStats().getIncomingAttributeCnt() ;
				int attrCnt = childResource.getAttributeList().size() ;
	
				int diffChildCnt = childResource.getDifferentChildCount();
				
				if (childResource.getInvalidResource()!=null){
					bgcolor="tinvalid";
					isDiff = true;
				}else if ((newCnt==attrCnt)&& (attrCnt!=0)&& (changeStatus.equalsIgnoreCase(ResourceConstants.ATTRIBUTE_CHANGE_TYPE_MODIFIED))){
					bgcolor="tnew";
					isDiff = true;
				}else if (changeStatus.equalsIgnoreCase(ResourceConstants.ATTRIBUTE_CHANGE_TYPE_INCOMING)){
					bgcolor="textra";
					incomingCnt = childResource.getAttributeList().size()  ;
					isDiff = true;
				}else if ((modifiedCnt>0) && (changeStatus.equalsIgnoreCase(ResourceConstants.ATTRIBUTE_CHANGE_TYPE_MODIFIED))){
					bgcolor="tdiff";
					isDiff = true;
					
				}else if ((childResource.getInvalidAttributes()!=null) &&(childResource.getInvalidAttributes().size()>0 )){
						bgcolor="tinvalid";
						isDiff = true;
						
				}
				
				boolean anyChildPresent = false;
				/**
				 * This is the logic to show + sign depending on children present and count 
				 */
				if (((childResource.getChildren()!=null) && (childResource.getChildren().size()>0)) || ((childResource.getInComingChildren()!=null) && (childResource.getInComingChildren().size()>0))){
					anyChildPresent = true;

				}
				Element newElement = new Element(childResource.getName() );

				if (arrayStringOnly){
					if (anyChildPresent){
						idAllArray.append(cnt *1000 + ","); 
						if (childResource.getDifferentChildCount()>0){
							idDiffArray.append(cnt *1000 + ","); 

						}
					}
					
				}else{
					
					reportFileOutputStream.write("<ul>");
					/**
					 * This is to display only Cell table and hide all other tables.
					 */
					
	
					if (cnt > 1){
	
						reportFileOutputStream.write("<table id='" + cnt + "' class=\"tborder\" width=\"100%\" align=\"center\" class=\""+ bgcolor + 
								"\" cellspacing=\"0\" cellpadding=\"0\" >");
						reportFileOutputStream.write(newline);
						
					}else{
	
						reportFileOutputStream.write("<table id='" + cnt +"' class=\"tborder\" width=\"100%\" align=\"center\" class=\""+ bgcolor + 
							"\" cellspacing=\"0\" cellpadding=\"0\" >");
						reportFileOutputStream.write(newline);
					}
	
					reportFileOutputStream.write("<tr>");
	
					reportFileOutputStream.write("<td class=\""+ bgcolor+"\">");
					String resourceIndentifierName = ResourceHelper.getResourceIdentifierName(childResource);
					String invalidMessage = "";
					
					if ((resourceIndentifierName == null) || (resourceIndentifierName.equalsIgnoreCase("null"))){  
						resourceIndentifierName = ""; 
					}else{
						resourceIndentifierName  = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; ["	+ resourceIndentifierName + "]";
					}
					
					if (childResource.getInvalidResource()!=null  ){  
						invalidMessage = childResource.getInvalidResource().getMessage() ; 
					}
					/**
					 * This is the logic to show + sign depending on children present and count 
					 */
					if (anyChildPresent){
						reportFileOutputStream.write("<a href=\"javascript:toggleBox(" + cnt *1000 +" )\"> + </a>&nbsp; <strong> <a name=\"" + childResource.getContainmentPath() + "\" href=\" javascript:toggleBox(" + (cnt * 100) +" )\"> " + childResource.getName() + "</a> </strong>" + resourceIndentifierName + "&nbsp;" + invalidMessage );
	
					}else{
						reportFileOutputStream.write(" <strong> <a name=\"" + childResource.getContainmentPath() + "\" href=\" javascript:toggleBox(" + (cnt * 100) +" )\"> " + childResource.getName() + "</a> </strong>" + resourceIndentifierName + "&nbsp;" + invalidMessage ) ;
	
					}
	
					
					reportFileOutputStream.write("</td>");
					reportFileOutputStream.write(newline);
					reportFileOutputStream.write("<td title=\"Immediate Child Difference Count\" width=\"10\" align=\"right\">[" + diffChildCnt + "]</td>");

					reportFileOutputStream.write(newline);
					reportFileOutputStream.write("<td  title=\"Different Attributes Count\" class=\"tdiff\" width=\"10\" align=\"right\">[" + modifiedCnt + "]</td>");
					reportFileOutputStream.write(newline);
					reportFileOutputStream.write("<td title=\"Missing in Target Attributes Count\" class=\"tnew\" width=\"10\" align=\"right\">[" + newCnt +"]</td>");
					reportFileOutputStream.write(newline);
					reportFileOutputStream.write("<td title=\"Missing in Source Attributes Count\" class=\"textra\" width=\"10\" align=\"right\">[" + incomingCnt + "]</td>");
					reportFileOutputStream.write(newline);
					reportFileOutputStream.write(newline);
					reportFileOutputStream.write(newline);
		
					
					reportFileOutputStream.write("</tr>");
					reportFileOutputStream.write(newline);
					reportFileOutputStream.write("<tr>");
					reportFileOutputStream.write(newline);
					reportFileOutputStream.write("<td class=\"crumbrow\">");
					reportFileOutputStream.write(newline);
					reportFileOutputStream.write("&nbsp;" + getBreadCrumb(childResource,""));
					reportFileOutputStream.write(newline);
					reportFileOutputStream.write("</td>");
					reportFileOutputStream.write(newline);
					reportFileOutputStream.write("</tr>");
					reportFileOutputStream.write(newline);
					
					reportFileOutputStream.write(newline);
	
					reportFileOutputStream.write("</table>");
					reportFileOutputStream.write(newline);
				
					processAttributes(newElement,childResource,deployInfo,reportFileOutputStream);
				
					if (anyChildPresent){
						reportFileOutputStream.write("<table id=\"" + cnt *1000 + "\" width=\"100%\" align=\"center\" cellspacing=\"0\" "+ 
						"cellpadding=\"0\" width=\"100%\" border=\"0\" style=\"display: "+ displayStatus+ ";\">");
						reportFileOutputStream.write(newline);
						reportFileOutputStream.write("<tr>");
						reportFileOutputStream.write(newline);
						reportFileOutputStream.write("<td>");
						reportFileOutputStream.write(newline);
					}
	
				}

				if ((childResource.getChildren()!=null) && (childResource.getChildren().size()>0)){
					createResourceXMLFile(newElement,childResource.getChildren(),deployInfo,reportFileOutputStream,"Modified",arrayStringOnly);

				}
	
				/**
				 * If the report is of normal(commit) or report only mode  then we don't need incoming to show in report
				 *  
				 */
				if ((childResource.getInComingChildren()!=null) && (childResource.getInComingChildren().size()>0) && (syncOperation) ){
					newElement.addContent(new Comment("Start - Incoming changes from WAS Repository"));
					createResourceXMLFile(newElement,childResource.getInComingChildren(),deployInfo,reportFileOutputStream,"Incoming",arrayStringOnly);
					newElement.addContent(new Comment("End - Incoming changes from WAS Repository"));
				}
				if (!arrayStringOnly){
					if (anyChildPresent){
	
						reportFileOutputStream.write("</td>");
						reportFileOutputStream.write(newline);
						reportFileOutputStream.write("</tr>");
						reportFileOutputStream.write(newline);
						reportFileOutputStream.write("</table>");
						reportFileOutputStream.write(newline);
					}
	
					
					reportFileOutputStream.write("</ul>");
					reportFileOutputStream.write(newline);
					
					element.addContent(newElement);
				}
			}
		}
		
		
	}

	
	private String getBreadCrumb(Resource resource, String crumbString){
		StringBuffer newCrumb = new StringBuffer();
		Resource currentResource = resource;
		
		while (currentResource.getParent()!=null){
			String resourceCrumb = getBreadCrumbForResource(currentResource);
			newCrumb.insert(0,resourceCrumb );
			newCrumb.insert(resourceCrumb.length(),  ">");
			currentResource = currentResource.getParent();
		}
		
		return newCrumb.toString().substring(0, newCrumb.length()-1);
	}
	
	private String getBreadCrumbForResource(Resource resource){
		String type = resource.getName();
		String identifier = ResourceHelper.getResourceIdentifierName(resource);

		StringBuffer newCrumb = new StringBuffer( );
		newCrumb.append("<a href=\"#");
		newCrumb.append(resource.getContainmentPath());
		newCrumb.append("\">");
		newCrumb.append(type); 		

		if (identifier != null){
			newCrumb.append("=");
			newCrumb.append(identifier);
		}
		newCrumb.append("</a> &nbsp;");

		
		return newCrumb.toString();
	}
	
	
	private String getJavaScriptArrayListChildIds(Resource resource){
		StringBuffer array = new StringBuffer("[");
		if (resource.getChildren()!=null){
			Vector children =  resource.getChildren();
			for (int i=0, n = children.size() ;i < n ; i++){
				Resource child = (Resource )children.get(i); 
				array.append( "'" );
				array.append( cnt + i+1 );
				array.append( "'" );
				array.append(",");
			}
		}
		
		if (resource.getInComingChildren() !=null){
			Vector children =  resource.getInComingChildren();
			for (int i=0, n = children.size() ;i < n ; i++){
				array.append( "'" );
				array.append( cnt + i+1 );
				array.append( "'" );
				array.append(",");
			}
		}
		
		if (array.length() > 1 ){
			array.deleteCharAt(array.length()-1 );
		}
		System.out.println( array.toString());
		array.append("]");
		return array.toString();
	}	
	private Vector getVectorFromHashMap(HashMap map){
		Vector newVector = new Vector();
		Iterator iterator = map.keySet().iterator();
		while(iterator.hasNext()){
			String key = (String)iterator.next();
			Resource value = (Resource)map.get(key);
			newVector.add(value );
		}
		return newVector;
	}
	
	
	private void processAttributes(Element element,Resource childResource,DeployInfo deployInfo,
			FileWriter reportFileOutputStream)
		throws IOException,DeployException {
		
		ResourceHelper resourceHelper = new ResourceHelper();
		HashMap map = childResource.getAttributeList();
		Iterator<DiffAttribute> modifiedAttrs = null;
		Iterator<InvalidAttribute> invalidAttrs = null;
		
		if (childResource.getModifiedAttributes()!=null){
			modifiedAttrs = childResource.getModifiedAttributes().iterator();
			//SDLog.log( childResource.getName() + " " +  childResource.getModifiedAttributes().size());
		}
	
		if (childResource.getInvalidAttributes()!=null){
			invalidAttrs = childResource.getInvalidAttributes().iterator();
			//SDLog.log( childResource.getName() + " " +  childResource.getModifiedAttributes().size());
		}
		if (map !=null){
			Iterator<String> keyIterator = map.keySet().iterator();
			
			reportFileOutputStream.write("<table class=\"tborder\"  width=\"100%\" align=\"center\" " +
					" id=\"" + (cnt * 100) + "\" style=\"display: "+ displayStatus+";\">");
			reportFileOutputStream.write(newline);
			reportFileOutputStream.write("<thead>");
			reportFileOutputStream.write(newline);
			reportFileOutputStream.write("<td>&nbsp;</td>");
			reportFileOutputStream.write(newline);
			reportFileOutputStream.write("<td><b>Attribute Name</b></td>");
			reportFileOutputStream.write(newline);
			if (deployInfo.getSourceDeployInfo()!=null){
				reportFileOutputStream.write("<td><b>Source Value ("+ deployInfo.getSourceDeployInfo().getHost() +")</b></td>");
				reportFileOutputStream.write(newline);
			}else{
				reportFileOutputStream.write("<td><b>Source Value</b></td>");
				reportFileOutputStream.write(newline);
			}
			reportFileOutputStream.write("<td class=\"thead\"><b>Target Value (" + deployInfo.getHost() + ")</b></td>");
			reportFileOutputStream.write(newline);
			reportFileOutputStream.write("</thead>");
			reportFileOutputStream.write(newline);
			reportFileOutputStream.write("<tbody>");
			reportFileOutputStream.write(newline);
			
			
			while(keyIterator.hasNext()){
				
				String key = (String)keyIterator.next();
				Object value = map.get(key);
				String htmlValue = "null"; 
				
				if (value!=null){
					htmlValue = value.toString();
				}

				String wasValue = htmlValue; 
				
				DiffAttribute diffAttribute =  resourceHelper.isAttributeModified(childResource,key);
				if ((diffAttribute!=null)){
					if 	(diffAttribute.getConfigValue()!=null){
						wasValue = diffAttribute.getConfigValue().toString();
					}else {
						wasValue = "null";
					}
				}

				InvalidAttribute invalidAttribute =  resourceHelper.isAttributeValid(childResource,key);

				/**
				 * logic is to exclude not changes and incoming attributes to produce a commit report.
				 */
				
				  
				
				if ( syncOperation || ((diffAttribute!=null ) && !diffAttribute.getChangeType().equalsIgnoreCase(ResourceConstants.ATTRIBUTE_CHANGE_TYPE_INCOMING))){ 

					/**
					 * if invalid then 
					 */
					String rowClassName;
					if (childResource.isIncoming()){
						rowClassName = "textra";
					
						htmlValue = "-"; 

					}else if ((diffAttribute==null )){
						rowClassName = null;
						reportFileOutputStream.write(newline);
					 
					}else if ((diffAttribute.getChangeType().equalsIgnoreCase(ResourceConstants.ATTRIBUTE_CHANGE_TYPE_INCOMING)) ){
						rowClassName = "textra";
						htmlValue = "-"; 
					}else if (diffAttribute.getChangeType().equalsIgnoreCase(ResourceConstants.ATTRIBUTE_CHANGE_TYPE_MODIFIED)){
						rowClassName = "tdiff";
						reportFileOutputStream.write(newline);
					}else if (diffAttribute.getChangeType().equalsIgnoreCase(ResourceConstants.ATTRIBUTE_CHANGE_TYPE_NEW)){
						rowClassName = "tnew";
						reportFileOutputStream.write(newline);
						wasValue = "-";
					
					}else {
						rowClassName = null;	
						reportFileOutputStream.write(newline);
					}					
					
					if (invalidAttribute!=null){
						rowClassName = "tinvalid";
					}
					/**
					 * This is done last so that if attr invalid it is over ridden
					 */
					if (rowClassName !=null){
						reportFileOutputStream.write("<tr class=\"" + rowClassName + "\" >");
					}else{
						reportFileOutputStream.write("<tr>");
					}
					reportFileOutputStream.write(newline);
					
					reportFileOutputStream.write("<td>&nbsp;</td>");
					reportFileOutputStream.write(newline);
					if (invalidAttribute !=null){
						reportFileOutputStream.write("<td>" + key + " " + invalidAttribute.getMessage() + "</td>");
					}else{
						reportFileOutputStream.write("<td>" + key + "</td>");
					}
					reportFileOutputStream.write(newline);
					
					
					
					//String unresolvedSourceValue= PropertyHelper.replaceVariableValueWithVariable(key,deployInfo,childResource.getUnresolvedAttributeList());
					String sourceVariableName = PropertyHelper.replaceVariableValueWithVariable(key,htmlValue,deployInfo,childResource.getUnresolvedAttributeList());
					String targetVariableName = PropertyHelper.replaceVariableValueWithVariable(key,wasValue,deployInfo,childResource.getUnresolvedAttributeList());
					
			/**		String targetVariableName;
					String sourceVariableName;
					
					if (unresolvedValue==null){
						sourceVariableName = htmlValue;
						targetVariableName = wasValue;
					}else{
						sourceVariableName = unresolvedValue;
						targetVariableName = unresolvedValue;
						
					}
			**/
					// If the resource is incoming or attribute is incoming then value in the xml must be the target value. For e.g. if running an extract on NameSpace then value in the xml must the value in WAS 	
					if(childResource.isIncoming() || ((diffAttribute!=null) && (diffAttribute.getChangeType().equalsIgnoreCase(ResourceConstants.ATTRIBUTE_CHANGE_TYPE_INCOMING))) ){
						element.setAttribute(new org.jdom.Attribute(key, targetVariableName));
					}else{
						element.setAttribute(new org.jdom.Attribute(key, sourceVariableName));
					}
					
					if (!htmlValue.equalsIgnoreCase("-")){
						if (deployInfo.getSourceDeployInfo()!=null){
								reportFileOutputStream.write("<td title=\"" + PropertyHelper.replaceVariable(sourceVariableName,deployInfo.getSourceDeployInfo()) + "\">" + sourceVariableName + "</td>");								
						}else{

							reportFileOutputStream.write("<td title=\"" + htmlValue + "\">" + sourceVariableName + "</td>");
						}
					}else{
						reportFileOutputStream.write("<td title=\"" + htmlValue + "\">" + htmlValue  + "</td>");
					}
					
					reportFileOutputStream.write(newline);
					
	//				System.out.println(" targetVariableName " + targetVariableName );
	//				System.out.println(" wasValue " + wasValue );
					
					if (!wasValue.equalsIgnoreCase("-")){
						reportFileOutputStream.write("<td title=\"" + wasValue + "\">"  + targetVariableName + "</td>");
					}else{
						reportFileOutputStream.write("<td title=\"" + wasValue + "\">"  + wasValue + "</td>");
					}
					reportFileOutputStream.write(newline);
						
					reportFileOutputStream.write("</tr>");

					reportFileOutputStream.write(newline);
				}
			
			}		
			
			reportFileOutputStream.write("</tbody>");
			reportFileOutputStream.write(newline);
			reportFileOutputStream.write("</table>");
			reportFileOutputStream.write(newline);
			reportFileOutputStream.write("<p>");
			reportFileOutputStream.write(newline);
			
			
				

			StringBuffer commentString = new StringBuffer();

			while ((modifiedAttrs!=null)  && modifiedAttrs.hasNext()){
				DiffAttribute  attr =  (DiffAttribute)modifiedAttrs.next();
				//if (attr.getName().equalsIgnoreCase(key )){
					//value = attr.getConfigValue();
					if (attr.getChangeType().equalsIgnoreCase("Added")){
						commentString.append("[Attribute Name: ");
						commentString.append(attr.getName() );
						commentString.append(" Change Type: ") ;
						commentString.append("Added") ;
						commentString.append( "]");
						
						
					}else if (attr.getChangeType().equalsIgnoreCase("Incoming")){
//						element.addContent(new Comment("Incoming Attribute Name: " + attr.getName()));
						commentString.append(" [Attribute Name: ");
						commentString.append(attr.getName() );
						commentString.append(" Change Type: ") ;
						commentString.append("Incoming") ;
						commentString.append( "]");

					}else{ 
						commentString.append(" [Attribute Name: ");
						commentString.append(attr.getName() );
						commentString.append(" Change Type: ") ;
						commentString.append("Different") ;
						commentString.append(" WAS Attribute Value: ") ;
						commentString.append( attr.getConfigValue());
						commentString.append( "]");

					//	element.addContent(new Comment("Different in WAS Repository Attribute Name: " + attr.getName()+ " Attribute Value: "+ attr.getConfigValue()));
					}
					logger.trace(" Adding attribute name " + attr.getName() + " as modified attribute to XML");	
				//}
			}
			if (commentString.toString().trim().length() > 0 )
				element.addContent(new Comment(commentString.toString() ));

		}

	}
	
	public static void main(String[] args) {
	/**
		try {

			Resource resources = ResourceXMLParser.getResourcesFromXML("C:\\jatin\\eclipse\\DAKS\\resources\\resources.xml","C:\\jatin\\eclipse\\DAKS\\resources\\resources-metadata.xml",true);
			ResourceXMLWriter resourceXMLWriter = new ResourceXMLWriter ();
			resourceXMLWriter.createResourceXMLFile(resources,"C:\\jatin\\eclipse\\DAKS\\resources\\syncresources.xml");
			Document doc = new Document();
			Element root = new Element("GREETING");
			root.setText("Hello JDOM!");
			doc.setRootElement(root);
			
			XMLOutputter outputter = new XMLOutputter();
			outputter.output(doc, System.out);       
		}catch (IOException e) {
			System.err.println(e);
		}catch (DeployException e) {
			System.err.println(e);
		}catch (JDOMException e) {
			System.err.println(e);
		}
		**/
	}

}
