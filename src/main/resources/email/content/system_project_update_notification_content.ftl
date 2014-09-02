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
						One or more of the postgraduate research applications assigned to you have recently been updated.
					</font>
				</p>
				<p>
					<font face="Arial, Helvetica, sans-serif" size="2">
						Please
						<#if user.enabled>
							login
						<#else>
							register
						</#if>
						to view the applications and see the updates. Updates can be found in the application timeline.
					</font>
				</p>
				<p>
					<!-- Button -->
					<a style="text-decoration:none;"
						<#if user.enabled>
							href="${host}/pgadmissions/applications?applyFilters=my&activationCode=${user.activationCode!}">
							<img alt="Login" title="Login" width="64" height="33" src="${host}/pgadmissions/design/default/images/email/login.jpg"/>
						<#else>
							href="${host}/pgadmissions/register?activationCode=${user.activationCode!}&directToUrl=${"/applications?applyFilters=my"?url('ISO-8859-1')}">
							<img alt="Register" title="Register" width="82" height="33" src="${host}/pgadmissions/design/default/images/email/register.jpg"/>
						</#if>
					</a>
				</p>
				<p>
					<font face="Arial, Helvetica, sans-serif" size="2">Yours sincerely,<br />UCL Prism</font>
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
