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
		      			<font face="Arial, Helvetica, sans-serif" color="#0055A1">Dear ${admin.firstName?html},</font>
		      		</h1>
		      		<p>
			      		  	<font face="Arial, Helvetica, sans-serif" size="2">${approver.firstName?html} ${approver.lastName?html} has approved ${application.applicant.firstName?html} ${application.applicant.lastName?html} Application ${application.applicationNumber} for 
				      		  <#if application.researchHomePage??>
				      		  	<a href="${application.researchHomePage}">${application.program.title}</a>.
				      		  <#else>
				      		  	${application.program.title}.
				      		  </#if>
				      		</font>
			      	</p>
			      	
			      	<p>
			      		  <font face="Arial, Helvetica, sans-serif" size="2">The application has been advanced to UCL Admissions for verification. If this is successful, an offer of study will be issued. UCL Admissions aim to be contact with their decision within ${admissionOfferServiceLevel}.</font>
			      	</p>
			      	
			      	<p>
			      		  <font face="Arial, Helvetica, sans-serif" size="2">Should you have any further questions on the application, you should refer them to one of the following UCL Admissions contacts:
				      		<ul>			
				      		<#list registryContacts as contact>      		
								<li><a href="mailto: ${contact.email}">${contact.firstname} ${contact.lastname}</a></li>
							</#list>
				      		</ul>			      		  
			      		  </font>
			      	</p>

			      	<p>
			      		  <font face="Arial, Helvetica, sans-serif" size="2"><b>The candidate number for UCL Admissions is --SitsApplicationId--. Please quote this number in all correspondence with them.</b></font>
			      	</p>

			      	<p>
			      		<font face="Arial, Helvetica, sans-serif" size="2">Yours sincerely,<br />UCL Prism</font>
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