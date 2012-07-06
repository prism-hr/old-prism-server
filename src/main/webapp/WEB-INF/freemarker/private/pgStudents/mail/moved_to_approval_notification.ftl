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
		      			<font face="Arial, Helvetica, sans-serif" color="#0055A1">Dear ${application.applicant.firstName},</font>
		      		</h1>
			      	<p>
			      		  <font face="Arial, Helvetica, sans-serif" size="2">We are pleased to confirm that your Application ${application.applicationNumber} for <#if application.researchHomePage??><a href="${application.researchHomePage}">${application.program.title}</a><#else>${application.program.title}</#if> has been advanced to approval.  At this stage, your study programme director evaluates and authorises your application.</font>
			      	</p>
			      	<p>
			      		  <font face="Arial, Helvetica, sans-serif" size="2"><b>Please note that this does not constitute a legally binding offer of study.</b></font>
			      	</p>
			      	<p>
			      		  <font face="Arial, Helvetica, sans-serif" size="2">Your study programme director will authorise your application. Following this, your application will be passed to UCL Admissions to generate your offer of study.</font>
			      	</p>
			      	<p>
			      		  <font face="Arial, Helvetica, sans-serif" size="2"><b>You must be in possession of an offer of study in order to enrol.</b></font>
			      	</p>
			      	
				    <br>  	
					<p>
		              <!-- Button -->
		              <table border="0" cellpadding="0" cellspacing="0">
		              	<tr>
		                  <td width="10"><img src="${host}/pgadmissions/design/default/images/email/button-left.gif" width="13" height="29" alt="" /></td>
		                  <td background="button-centre.gif" bgcolor="#003399" style="background: #003399 url(${host}/pgadmissions/design/default/images/email/button-centre.gif) repeat-x;" align="center">
		                    <font face="Arial, Helvetica, sans-serif" size="2">
		                      <a style="color: #FFFFFF; text-decoration: none; font-size:0.9em" 
		                      	href="${host}/pgadmissions/application?view=view&applicationId=${application.applicationNumber}"><b>View Application</b></a>
		                    </font>
		                  </td>
		                  <td width="10"><img src="${host}/pgadmissions/design/default/images/email/button-right.gif" width="13" height="29" alt="" /></td>
		                </tr>
		              </table>
					</p>
					</br>			      	
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