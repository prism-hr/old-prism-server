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
						Dear ${userFirstName},
					</font>
				</h1>
				<p>
					<font face="Arial, Helvetica, sans-serif" size="2">
						We can confirm the arrangements for your interview of ${applicant.firstName?html} ${applicant.lastName?html} in connection with Application ${applicationCode} for ${application.programAndProjectTitle}.
					</font>
				</p>
				<p>
					<font face="Arial, Helvetica, sans-serif" size="2">
						The interview will take place at ${interviewer.interview.interviewTime} on ${interviewer.interview.interviewDueDate?string("dd MMM yyyy")}.
					</font>
				</p>
				<p>
					<#if interviewer.interview.furtherInterviewerDetails??>
						<font face="Arial, Helvetica, sans-serif" size="2">
							${interviewer.interview.furtherInterviewerDetails?html}
						</font>
					</#if>
				</p>
				<p>
					<#if interviewer.interview.locationURL?has_content>
						<!-- Button -->
						<a style="text-decoration:none;" href="${interviewer.interview.locationURL}" title="Get Directions">
							<img alt="Get directions to interview" title="Get directions to interview" width="119" height="33" src="${host}/pgadmissions/design/default/images/email/get_directions.png" />
						 </a>
					</#if>
					<!-- Button -->
					<a style="text-decoration:none;"
						<#if !interviewer.user.enabled>
							href="${host}/pgadmissions/register?activationCode=${interviewer.user.activationCode}&directToUrl=${"/application?view=view&applicationId=${applicationCode}"?url('ISO-8859-1')}">
						<#else>
							href="${host}/pgadmissions/application?view=view&applicationId=${applicationCode}&activationCode=${interviewer.user.activationCode}">
						</#if>
						<img alt="View application" title="View application" width="131" height="33" src="${host}/pgadmissions/design/default/images/email/view_app.jpg" />
					</a>
				</p>
				<p>
					<font face="Arial, Helvetica, sans-serif" size="2">
						Yours sincerely,<br />
						UCL Prism
					</font>
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
