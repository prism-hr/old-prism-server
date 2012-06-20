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
		    		<img src="${host}/pgadmissions/design/default/images/email/header.jpg" width="600" height="160" alt="Prism: A Spectrum of Postgraduate Research Opportunities" />
		    	</td>
		  	</tr>
		  	<tr>
		    	<td width="50">
		    		<img src="${host}/pgadmissions/design/default/images/shim.gif" width="50" height="10" alt="" />
		    	</td>
		    	<td width="500">
		      		<h1 style="font-size: 12pt;">
		      			<font face="Arial, Helvetica, sans-serif" color="#0055A1">Dear ${referee.firstname?html},</font>
		      		</h1>
			      	<p>
			      		<font face="Arial, Helvetica, sans-serif" size="2">We recently informed you that ${applicant.firstName?html} ${applicant.lastName?html} has submitted an Application ${application.applicationNumber} for PhD study at University College London (UCL) in ${application.program.title}.</font>
			      	</p>
			      	<p>
			      		<font face="Arial, Helvetica, sans-serif" size="2">You have been nominated as one of their referees.</font>
			      	</p>
			      	<p>
			      		<font face="Arial, Helvetica, sans-serif" size="2">You are asked to upload a short PDF document confirming their suitability for PhD study. You may create your own document, or work from our template (attached). If you feel unable to provide a reference, you may also decline.</font>
			      	</p>
			      	<#if !referee.user?? || !referee.user.enabled>
					<p>
		      			<font face="Arial, Helvetica, sans-serif" size="2">If you have not previously registered with the UCL Portal, please do so by clicking the link below:</font>
		      		</p>
			      	<p>
			      		<font face="Arial, Helvetica, sans-serif" size="2">
			      			<a href="${host}/pgadmissions/referee/register?activationCode=${referee.activationCode!}">Register</a>
			      		</font>
			      	</p>
			      	</#if>
			      	<p>
			      		<font face="Arial, Helvetica, sans-serif" size="2">
			      			<a href="${host}/pgadmissions/referee/addReferences?application=${application.applicationNumber}">Add Reference</a>
			      		</font>
			      	</p>
			      	<p>
			      		<font face="Arial, Helvetica, sans-serif" size="2"> 		encrypt
			      			<a href="${host}/pgadmissions/decline/reference?refereeId=${referee.id?string('#######')}">Decline</a>
			      		</font>
			      	</p>
			      	<p>
			      		<font face="Arial, Helvetica, sans-serif" size="2">We will continue to send reminders until you respond to this request.</font>
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