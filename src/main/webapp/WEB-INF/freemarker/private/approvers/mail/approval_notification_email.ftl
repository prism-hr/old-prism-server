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
          <font face="Arial, Helvetica, sans-serif" color="#0055A1">Dear ${user.firstName?html},</font>
        </h1>
<#if user.isInRoleInProgram('APPROVER', application.program)> 
        <p>
          <font face="Arial, Helvetica, sans-serif" size="2">It is recommended that ${application.applicant.firstName?html} ${application.applicant.lastName?html} be recruited to UCL <#if application.researchHomePage??><a href="${application.researchHomePage}">${application.program.title}</a><#else>${application.program.title}</#if>.</font>
        </p>
        <p>
          <font face="Arial, Helvetica, sans-serif" size="2"><b>You must now evaluate their Application ${application.applicationNumber} and select the next action.</b></font>
        </p>
        <p>
          <!-- Button -->
			<a style="text-decoration:none;" href="${host}/pgadmissions/progress/getPage?applicationId=${application.applicationNumber}&activationCode=${user.activationCode}" title="Evaluate Application">
            	<img border="0" style="border: none;" width="168" height="36" alt="Evaluate Application" src="${host}/pgadmissions/design/default/images/email/evaluate_app.png" />
          	</a>
        </p>
        <p>
          <font face="Arial, Helvetica, sans-serif" size="2">We will send reminders until you respond to this request.</font>
        </p>				      	
<#else>
        <p>
          <font face="Arial, Helvetica, sans-serif" size="2">The Application ${application.applicationNumber} has now been moved to approval. You can view the Application below.</font>
        </p>
        <p>
          <!-- Button -->
          <a href="${host}/pgadmissions/application?view=view&applicationId=${application.applicationNumber}&activationCode=${user.activationCode}" title="View Application">
            <img width="150" height="36" alt="View Application" src="${host}/pgadmissions/design/default/images/email/view_app.png" />
          </a>
        </p>
</#if>
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
              <font face="Arial, Helvetica, sans-serif" size="1" color="#BEBEC0" style="font-size: 7pt; line-height: 1.2;">
                <!-- &#173; allegedly prevents GMail from linking the telephone number and years. -->
                University College London, Gower Street, London, WC1E 6BT<br />
                Tel: +44 (0) 20&#173; 7679&#173; 2000<br />
                &copy; UCL 1999&#173;&ndash;2012&#173;
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