<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title>Untitled Document</title>
	</head>

	<body>
		<table width="600" border="0" cellspacing="0" cellpadding="0">
			<tr>
		    	<td colspan="3">
		    		<img src="${host}/pgadmissions/design/default/images/email/header.jpg" width="600" height="80" alt="Portal: Your Gateway to Research Opportunities" />
		    	</td>
		  	</tr>
		  	<tr>
			    <td width="50" bgcolor="#141215" style="background-color: #141215;">
			    	<img src="${host}/pgadmissions/design/default/images/shim.gif" width="50" height="30" alt="" />
			    </td>
			    <td width="500" bgcolor="#141215" style="background-color: #141215;">			    
			    </td>
			    <td width="50" bgcolor="#141215" style="background-color: #141215;">
			    	<img src="${host}/pgadmissions/design/default/images/shim.gif" width="50" height="30" alt="" />
			    </td>
		  	</tr>
		  	<tr>
		    	<td colspan="3"><img src="${host}/pgadmissions/design/default/images/shim.gif" width="50" height="30" alt="" /></td>
		  	</tr>
		  	<tr>
		    	<td width="50">
		    		<img src="${host}/pgadmissions/design/default/images/shim.gif" width="50" height="10" alt="" />
		    	</td>
		    	<td width="500">
		      		<h1 style="font-size: 12pt;">
		      			<font face="Arial, Helvetica, sans-serif" color="#0055A1">Dear ${reviewer.user.firstName?html},</font>
		      		</h1>
			      	<p>
			      		<font face="Arial, Helvetica, sans-serif" size="2">You have been nominated to interview ${applicant.firstName?html} ${applicant.lastName?html} in connection with their application ${application.id?string('#####')} to join the Postgraduate research Programme in ${application.program.title} at UCL.</font>
			      	</p>
			      	<p>
			      		<font face="Arial, Helvetica, sans-serif" size="2">You can view the application by clicking the following link:</font>
			      	</p>
			         <p>
			      		<font face="Arial, Helvetica, sans-serif" size="2">
			      			<a href="${host}/pgadmissions/application?view=view&applicationId=${application.id?string("######")}">View the application</a>
			      		</font>
			      	</p>
			      	  <p>
			      		<font face="Arial, Helvetica, sans-serif" size="2">The interview will take place on ${application.interview.interviewDueDate?string("dd MMM yyyy")}.</font>
			      	</p>
			      	<p>
			      		<font face="Arial, Helvetica, sans-serif" size="2">The details of the interview are as follows:</font>
			      	</p>
			      	<p>
			      		<font face="Arial, Helvetica, sans-serif" size="2">${application.interview.furtherDetails?html}</font>
			      	</p>
			      	<#if application.interview.locationURL??>
				      	<p>
				      		<font face="Arial, Helvetica, sans-serif" size="2">Please click the following link for more details about the location at which the interview will take place:</font>
				      	</p>
				      	<p>
				      		<font face="Arial, Helvetica, sans-serif" size="2"><a href="${application.interview.locationURL}">Location details</a></font>
				      	</p>
				      </#if>
			      	<p>
			      		<font face="Arial, Helvetica, sans-serif" size="2">In the meantime, for further assistance <a href="mailto: ${adminsEmails}">email the administrator</a></font>
			      	</p>
			      	<p>
			      		<font face="Arial, Helvetica, sans-serif" size="2">Many Thanks, <br />UCL Portal</font>
			      	</p>
		    	</td>
		    	<td width="50"><img src="${host}/pgadmissions/design/default/images/shim.gif" width="50" height="10" alt="" /></td>
		  	</tr>
		  	<tr>
		    	<td colspan="3"><img src="${host}/pgadmissions/design/default/images/shim.gif" width="50" height="30" alt="" /></td>
		  	</tr>
		  	<tr>
		    	<td width="50" bgcolor="#141215" style="background-color: #141215;">
		    		<img src="${host}/pgadmissions/design/default/images/shim.gif" width="50" height="10" alt="" />
		    	</td>
		    	<td width="500" bgcolor="#A2A3A5" style="background-color: #A2A3A5;">
		    		<img src="${host}/pgadmissions/design/default/images/email/logo_ucl.gif" width="80" height="30" alt="UCL" />
		    	</td>
		    	<td width="50" bgcolor="#A2A3A5" style="background-color: #A2A3A5;">
		    		<img src="${host}/pgadmissions/design/default/images/shim.gif" width="50" height="10" alt="" />
		    	</td>
		  	</tr>
		</table>
	
	</body>
</html>