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
		    		<img src="${host}/pgadmissions/design/default/images/email/header.jpg" width="600" height="160" alt="Portal: Your Gateway to Research Opportunities" />
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
		      			<font face="Arial, Helvetica, sans-serif" color="#0055A1">Dear ${admin.firstName?html},</font>
		      		</h1>
			      	<p>
			      		<font face="Arial, Helvetica, sans-serif" size="2">The application ${application.applicationNumber} was rejected by ${approver.firstName?html} ${approver.lastName?html}.</font>
			      	</p>
			      	<p>
			      		<font face="Arial, Helvetica, sans-serif" size="2">Reason for the rejection:
			      		<ul>			      		  
				      		 <li>${(reason.text?html)!}</li>
	
			      		</ul>
			      		</font>
			      	</p>
			      	<p>
			      		<font face="Arial, Helvetica, sans-serif" size="2">You can see the application here:</font>
			      	</p>
			      	<p>
			      		<font face="Arial, Helvetica, sans-serif" size="2">
			      			<a href="${host}/pgadmissions/application?view=view&applicationId=${application.applicationNumber}">View application</a>
			      		</font>
			      	</p>
			      	<p>
			      		<font face="Arial, Helvetica, sans-serif" size="2">Best Regards, <br />UCL Portal</font>
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