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
			      		<font face="Arial, Helvetica, sans-serif" size="2">${applicant.firstName?html} ${applicant.lastName?html} has submitted an Application ${application.applicationNumber} for postgraduate research study at 
			      			<a href="http://www.ucl.ac.uk/">University College London (UCL)</a> in 
							<#if application.researchHomePage??><a href="${application.researchHomePage}">${application.program.title}</a><#else>${application.program.title}</#if>.
						</font>
			      	</p>
			      	<p>
			      		<font face="Arial, Helvetica, sans-serif" size="2">You have been nominated as one of their referees.</font>
			      	</p>
			      	<p>
			      		<font face="Arial, Helvetica, sans-serif" size="2">You are asked to complete a short questionnaire confirming their suitability for postgraduate research study. If you feel unable to do this, you may also decline. <b>Be aware that declining to provide a reference may reduce the applicant's chances of securing a study place.</b></font>
			      	</p>
			      	<#if !referee.user?? || !referee.user.enabled>
					<p>
		      			<font face="Arial, Helvetica, sans-serif" size="2">If you have not previously registered with UCL Prism, please do so by clicking the link below:</font>
		      		</p>
			      	<p>
			      		<font face="Arial, Helvetica, sans-serif" size="2">
			      			<a href="${host}/pgadmissions/register?activationCode=${referee.user.activationCode!}">Register</a>
			      		</font>
			      	</p>
			      	</#if>
			      	<p>
			      		<font face="Arial, Helvetica, sans-serif" size="2">
			      			<a href="${host}/pgadmissions/referee/addReferences?applicationId=${application.applicationNumber}">Provide Reference</a>
			      		</font>
			      	</p>
			      	<p>
			      		<font face="Arial, Helvetica, sans-serif" size="2">
			      			<a href="${host}/pgadmissions/decline/reference?applicationId=${application.applicationNumber}&activationCode=${referee.user.activationCode}">Decline</a>
			      		</font>
			      	</p>
			      	<p>
			      		<font face="Arial, Helvetica, sans-serif" size="2">We will send reminders until you respond to this request.</font>
			      	</p>
			      	<p>
			      		<font face="Arial, Helvetica, sans-serif" size="2">With best regards,<br />UCL Prism</font>
			      	</p>
		    	</td>
		    	<td width="50"><img src="${host}/pgadmissions/design/default/images/shim.gif" width="50" height="10" alt="" /></td>
		  	</tr>
		  	<tr>
		    	<td colspan="3"><img src="${host}/pgadmissions/design/default/images/shim.gif" width="50" height="30" alt="" /></td>
		  	</tr>
		  	<tr>
		    	<td colspan="3" bgcolor="#6D6E71" style="background-color: #6D6E71; line-height: 1; white-space: nowrap;">
            <table width="600" border="0" cellspacing="0" cellpadding="0">
              <tr>
                <td colspan="5"><img src="${host}/pgadmissions/design/default/images/shim.gif" width="600" height="9" alt="" /></td>
              </tr>
              <tr>
                <td width="48"><img src="${host}/pgadmissions/design/default/images/shim.gif" width="48" height="45" alt="" /></td>
                <td width="288">
                  <font face="Arial, Helvetica, sans-serif" size="1" color="#BEBEC0" style="font-size: 7pt;">
                    University College London, Gower Street, London, WC1E 6BT<br />
                    Tel: +44 (0) 20 7679 2000<br />
                    &copy; UCL 1999&ndash;2012
                  </font>
                </td>
                <td width="50"><img src="${host}/pgadmissions/design/default/images/shim.gif" width="50" height="45" alt="" /></td>
                <td width="214"><img src="${host}/pgadmissions/design/default/images/email/footer_logo.gif" width="214" height="45" alt="" /></td>
              </tr>
              <tr>
                <td colspan="5"><img src="${host}/pgadmissions/design/default/images/shim.gif" width="600" height="9" alt="" /></td>
              </tr>
            </table>
		    	</td>
		  	</tr>
		</table>
	
	</body>
</html>