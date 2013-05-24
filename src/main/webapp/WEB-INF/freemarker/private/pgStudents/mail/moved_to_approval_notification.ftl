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
		    		<img src="${host}/pgadmissions/design/default/images/email/header.jpg" width="600" height="100" alt="Prism: A Spectrum of Postgraduate Research Opportunities" />
		    	</td>
		  	</tr>
		  	<tr>
		    	<td width="50">
		    		<img src="${host}/pgadmissions/design/default/images/shim.gif" width="50" height="10" alt="" />
		    	</td>
		    	<td width="500">
		      		<h1 style="font-size: 12pt;">
		      			<font face="Arial, Helvetica, sans-serif" color="#0055A1">Dear ${application.applicant.firstName},</font>
		      		</h1>
			      	<p>
			      		  <font face="Arial, Helvetica, sans-serif" size="2">Our <#if previousStage == 'VALIDATION'>recruitment office<#elseif previousStage == 'REVIEW'>academic review panel<#elseif previousStage == 'INTERVIEW'>interview panel</#if> have completed their assessment of your Application ${application.applicationNumber} for <#if application.researchHomePage??><a href="${application.researchHomePage}">${application.program.title}</a><#else>${application.program.title}</#if>. We are pleased to inform you that they have recommended that you be recruited to the programme.</font>
			      	</p>
		      		
			      	<p>
			      		  <font face="Arial, Helvetica, sans-serif" size="2"><b>Please note that this email does not constitute a legally binding offer of study and no offer of study is valid until provided in writing by UCL Admissions.</b></font>
			      	</p>
		      		 
			      	<p>
			      		  <font face="Arial, Helvetica, sans-serif" size="2">Our Programme Director will now make a final assessment of your application and determine whether to advance it to UCL Admissions for verification.</font>
			      	</p>
		      		 
			      	<p>
			      		  <font face="Arial, Helvetica, sans-serif" size="2">We aim to be back in contact with the outcome of this assessment by close of business on ${application.dueDate?string("dd MMM yyyy")}.</font>
			      	</p>
			      	
							<p>
				            	<!-- Button -->
								<a style="text-decoration:none;" href="${host}/pgadmissions/application?view=view&applicationId=${application.applicationNumber}&activationCode=${application.applicant.activationCode}" title="View/Update Application">
									<img border="0" style="border: none;" width="193" height="36" alt="View/Update Application" src="${host}/pgadmissions/design/default/images/email/view_update_app.png">
								</a>
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