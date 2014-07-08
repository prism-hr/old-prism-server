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
				<img alt="Header image" title="Header image" width="600" height="100" src="${host}/pgadmissions/design/default/images/email/header.jpg"/>
			</td>
        </tr>
        <tr>
            <td width="50">
				<img alt="Spacer image" title="Spacer image" width="1" height="1" src="${host}/pgadmissions/design/default/images/shim.gif"/>
			</td>
			<td width="500">
				<h1 style="font-size: 12pt;">
					<font face="Arial, Helvetica, sans-serif" color="#0055A1">
						Dear ${referee.firstname?html},
					</font>
				</h1>
				<p>
					<font face="Arial, Helvetica, sans-serif" size="2">
						This is a gentle reminder that ${applicant.firstName?html} ${applicant.lastName?html} has nominated you as their referee for Application ${application.applicationNumber}, postgraduate research study at 
						<a href="http://www.ucl.ac.uk/">University College London (UCL)</a> in ${application.projectOrProgramTitle}.
					</font>
				</p>
				<p>
					<font face="Arial, Helvetica, sans-serif" size="2">
						The Programme Admissions Panel would still very much appreciate your feedback on their suitability for postgraduate research study.  
					</font>
				</p>
				<p>
					<font face="Arial, Helvetica, sans-serif" size="2">
						<b>If you are able to kindly assist us on this occasion:</b><br /><ul><li>Please follow the <i>Provide Reference</i> link below to access our admissions portal.</li><li>Enter a password of your choice on the registration form, (or simply login if you have already registered).</li><li>Then follow the onscreen guidance to access a short feedback form, (or upload a document if you have already prepared one).</li></ul><b><br />If you feel unable to do this, or if the applicant is unknown to you:</b><br /><ul><li>Please let us know by clicking on the alternative, <i>Decline</i>, link. (Please note that declining may reduce the chance of this applicant securing a place).</li></ul></font>
				</p>
				
				<p>
					<!-- Button -->
					<a style="text-decoration:none;" 
						<#if !referee.user.enabled>
							href="${host}/pgadmissions/register?activationCode=${referee.user.activationCode!}&directToUrl=${"/referee/addReferences?applicationId=${application.applicationNumber}"?url('ISO-8859-1')}">
						<#else>
							href="${host}/pgadmissions/referee/addReferences?applicationId=${application.applicationNumber}&activationCode=${referee.user.activationCode!}">
						</#if>
						<img alt="Provide your reference" title="Provide your reference" width="147" height="33" src="${host}/pgadmissions/design/default/images/email/provide_reference.jpg"/>
					</a>
					<!-- Button -->
					<a style="text-decoration:none;" href="${host}/pgadmissions/decline/reference?applicationId=${application.applicationNumber}&activationCode=${referee.user.activationCode!}">
						<img alt="Decline to provide a reference" title="Decline to provide a reference" width="76" height="34" src="${host}/pgadmissions/design/default/images/email/decline.jpg"/>
					</a>
				</p>
				<p>
					<font face="Arial, Helvetica, sans-serif" size="2">
						<p>The timely collection of references is crucial for us to consider our applicants, therefore we hope you understand the need for us to send reminders.&nbsp;&nbsp;&nbsp; Should you run into any difficulty, please see our dedicated <a external="1" title="Opens external link in new window" class="external-link-new-window" target="_blank" href="http://uclprism.freshdesk.com/support/solutions/63169">online helpdesk</a> to access further assistance.<br /><br /><b>Thank you in advance for your time. &nbsp;</b><br /><br />Yours sincerely, <br /><br /></p><p><br /><b>UCL Prism Team</b><br /><br /></p></font>
				</p>

			</td>
            <td width="50">
				<img alt="Spacer image" title="Spacer image" width="1" height="1" src="${host}/pgadmissions/design/default/images/shim.gif"/>
			</td>
		  	</tr>
		  	<tr>
		    	<td colspan="3">
					<img alt="Spacer image" title="Spacer image" width="1" height="1" src="${host}/pgadmissions/design/default/images/shim.gif"/>
				</td>
		  	</tr>
		  	<tr>
		    	<td colspan="3" bgcolor="#6D6E71" style="background-color: #6D6E71; line-height: 1; white-space: nowrap;">
					<table width="600" border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td colspan="5">
								<img alt="Spacer image" title="Spacer image" width="1" height="1" src="${host}/pgadmissions/design/default/images/shim.gif"/>
							</td>
						</tr>
						<tr>
							<td width="48">
								<img alt="Spacer image" title="Spacer image" width="1" height="1" src="${host}/pgadmissions/design/default/images/shim.gif"/>
							</td>
							<td width="288">
								<font face="Arial, Helvetica, sans-serif" size="1" color="#BEBEC0" style="font-size: 7pt;">
									University College London, Gower Street, London, WC1E 6BT
									<br/>
									Tel: +44 (0) 20 7679 2000
									<br/>
									&copy; UCL 1999&ndash;2012
								</font>
							</td>
							<td width="50">
								<img alt="Spacer image" title="Spacer image" width="1" height="1" src="${host}/pgadmissions/design/default/images/shim.gif"/>
							</td>
							<td width="214">
								<img alt="Footer logo" title="Footer logo" width="214" height="50" src="${host}/pgadmissions/design/default/images/email/footer_logo.gif"/>
							</td>
						</tr>
						<tr>
							<td colspan="5">
								<img alt="Spacer image" title="Spacer image" width="1" height="1" src="${host}/pgadmissions/design/default/images/shim.gif"/>
							</td>
						</tr>
					</table>
				</td>
		  	</tr>
		</table>
	</body>
</html>
