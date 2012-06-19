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
			    <td width="50" bgcolor="#1A171B" style="background-color: #1A171B;">
			    	<img src="${host}/pgadmissions/design/default/images/shim.gif" width="50" height="30" alt="" />
			    </td>
			    <td width="500" bgcolor="#1A171B" style="background-color: #1A171B;">			    
			    </td>
			    <td width="50" bgcolor="#1A171B" style="background-color: #1A171B;">
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
		      			<font face="Arial, Helvetica, sans-serif" color="#0055A1">Dear ${interviewer.user.firstName?html},</font>
		      		</h1>
			      	<p>
			      		<font face="Arial, Helvetica, sans-serif" size="2">This is to confirm the arrangements for your interview of ${applicant.firstName?html} ${applicant.lastName?html} for Application ${application.applicationNumber} for UCL ${application.program.title}.</font>
			      	</p>
			      	<#if !interviewer.user.enabled>
			      		<p>
			      			<font face="Arial, Helvetica, sans-serif" size="2">If you have not previously registered with the UCL Portal, please do so by clicking the link below:</font>
			      		</p>
				      	<p>
				      		<font face="Arial, Helvetica, sans-serif" size="2">remove?
				      			<a href="${host}/pgadmissions/register?userId=${interviewer.user.id?string('#######')}">Register</a>
				      		</font>
				      	</p>
			      	</#if>
			      	<p>
			      		<font face="Arial, Helvetica, sans-serif" size="2">The interview will take place at ${interviewer.interview.interviewTime} on ${interviewer.interview.interviewDueDate?string("dd MMM yyyy")}.</font>
			      	</p>
			      	<p>
			      		<font face="Arial, Helvetica, sans-serif" size="2">${interviewer.interview.furtherDetails?html}</font>
			      	</p>
			      	<#if interviewer.interview.locationURL??>
				      	<p>
				      		<font face="Arial, Helvetica, sans-serif" size="2"><a href="${interviewer.interview.locationURL}">Location details</a></font>
				      	</p>
				    </#if>
			        <p>
			      		<font face="Arial, Helvetica, sans-serif" size="2">
			      			<a href="${host}/pgadmissions/application?view=view&applicationId=${application.applicationNumber}">View application</a>
			      		</font>
			      	</p>
			      	<p>
			      	  <font face="Arial, Helvetica, sans-serif" size="2">Please let us know by <a href="mailto: ${adminsEmails}">e-mail</a> if you are unable to attend.</font>
			      	</p>
			      	<p>
			      		<font face="Arial, Helvetica, sans-serif" size="2">With best regards,<br />UCL Elect</font>
			      	</p>
		    	</td>
		    	<td width="50"><img src="${host}/pgadmissions/design/default/images/shim.gif" width="50" height="10" alt="" /></td>
		  	</tr>
		  	<tr>
		    	<td colspan="3"><img src="${host}/pgadmissions/design/default/images/shim.gif" width="50" height="30" alt="" /></td>
		  	</tr>
		  	<tr>
		    	<td width="50" bgcolor="#1A171B" style="background-color: #1A171B;">
		    		<img src="${host}/pgadmissions/design/default/images/shim.gif" width="50" height="10" alt="" />
		    	</td>
		    	<td width="500" bgcolor="#1A171B" style="background-color: #1A171B;">
		    		<img src="${host}/pgadmissions/design/default/images/email/logo_ucl.gif" width="80" height="30" alt="UCL" />
		    	</td>
		    	<td width="50" bgcolor="#1A171B" style="background-color: #1A171B;">
		    		<img src="${host}/pgadmissions/design/default/images/shim.gif" width="50" height="10" alt="" />
		    	</td>
		  	</tr>
		</table>
	
	</body>
</html>