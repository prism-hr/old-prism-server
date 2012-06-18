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
			      		<font face="Arial, Helvetica, sans-serif" size="2">${applicant.firstName?html} ${applicant.lastName?html} has recently submitted an Application ${application.applicationNumber} for PhD study at University College London in ${application.program.title}.</font>
			      	</p>
			      	<p>
			      		<font face="Arial, Helvetica, sans-serif" size="2">You have been selected to review their application.</font>
			      	</p>
			      	<p>
			      		<font face="Arial, Helvetica, sans-serif" size="2">You are asked to complete a short questionnaire confirming their suitability for PhD study. If you feel unable to provide a review, you may also decline.</font>
			      	</p>
			      	<#if !reviewer.user?? || !reviewer.user.enabled>
			      		<p>
			      			<font face="Arial, Helvetica, sans-serif" size="2">If you have not previously registered with the UCL Portal, please do so by clicking the link below:</font>
			      		</p>
				      	<p>
				      		<font face="Arial, Helvetica, sans-serif" size="2">   encrypt
				      			<a href="${host}/pgadmissions/register?userId=${reviewer.user.id?string('#######')}">Register</a>
				      		</font>
				      	</p>
			      	</#if>
				      	<p>
				      		<font face="Arial, Helvetica, sans-serif" size="2">
				      			<a href="${host}/pgadmissions/reviewFeedback?applicationId=${application.applicationNumber}">Provide feedback</a>
				      		</font>
				      	</p>
			      	<p>
			      		<font face="Arial, Helvetica, sans-serif" size="2">encrypt
			      			<a href="${host}/pgadmissions/decline/review?applicationId=${application.applicationNumber}&userId=${reviewer.user.id?string('#######')}">Decline</a>
			      		</font>
			        <p>
			      	</p>
			      	<p>
			      		<font face="Arial, Helvetica, sans-serif" size="2">We will send reminders until you respond to this request.</font>
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
		    	<td width="50" bgcolor="#141215" style="background-color: #1A171B;">
		    		<img src="${host}/pgadmissions/design/default/images/shim.gif" width="50" height="10" alt="" />
		    	</td>
		    	<td width="500" bgcolor="#A2A3A5" style="background-color: #1A171B;">
		    		<img src="${host}/pgadmissions/design/default/images/email/logo_ucl.gif" width="80" height="30" alt="UCL" />
		    	</td>
		    	<td width="50" bgcolor="#A2A3A5" style="background-color: #1A171B;">
		    		<img src="${host}/pgadmissions/design/default/images/shim.gif" width="50" height="10" alt="" />
		    	</td>
		  	</tr>
		</table>
	
	</body>
</html>