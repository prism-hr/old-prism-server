<!DOCTYPE HTML>
<#import "/spring.ftl" as spring />
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>UCL Postgraduate Admissions</title>

<!-- Always force latest IE rendering engine (even in intranet) & Chrome Frame -->
<meta http-equiv="X-UA-Compatible" content="IE=9,chrome=1" />
<meta name="prism.version" content="<@spring.message 'prism.version'/>" >
<link type="text/css" rel="stylesheet" href="<@spring.url '/design/default/css/private/global_private.css'/>" />
<link type="text/css" rel="stylesheet" href="<@spring.url '/design/default/css/private/application.css'/>" />
<link type="text/css" rel="stylesheet" href="<@spring.url '/design/default/css/private/staff/reject.css'/>" />
<link type="text/css" rel="stylesheet" href="<@spring.url '/design/default/css/actions.css' />" />
<link rel="shortcut icon" type="text/css" href="<@spring.url '/design/default/images/favicon.ico' />"/>
<script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/libraries.js'/>"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/script.js'/>"></script>
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/bootstrap.min.css' />"/>
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/font-awesome.min.css' />"/>
<script type="text/javascript" src="<@spring.url '/design/default/js/bootstrap.min.js' />"></script>

<!--[if lt IE 9]>
<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
<![endif]-->
</head>

<!--[if IE 9]>
<body class="ie9">
<![endif]-->
<!--[if lt IE 9]>
<body class="old-ie">
<![endif]-->
<!--[if (gte IE 9)|!(IE)]><!-->
<body>
<!--<![endif]-->

<div id="wrapper"> <#include "/private/common/global_header.ftl"/> 
  
  <!-- Middle. -->
  <div id="middle"> <#include "/private/common/parts/nav_with_user_info_toggle.ftl"/>
    <@header/>
    <!-- Main content area. -->
    <article id="content" role="main"> 
      
      <!-- "Tools" 
		  <div id="tools">
			<ul class="left">
			  <li class="icon-print"><a target="_blank" title="Click to Download" href="<@spring.url '/print?applicationFormId=${applicationForm.applicationNumber}'/>">Download PDF</a></li>
			  <li class="icon-feedback"><a title="Send Feedback" href="mailto:prism@ucl.ac.uk?subject=Feedback" target="_blank">Send Feedback</a></li>
			</ul>
		  </div>--> 
      
      <!-- FLOATING TOOLBAR -->
      <ul id="view-toolbar" class="toolbar">
        <li class="top"><a href="javascript:backToTop();" title="Back to top">Back to top</a></li>
        <li class="print"><a target="_blank" href="<@spring.url '/print?applicationFormId=${applicationForm.applicationNumber}'/>" title="Click to Download">Print</a></li>
      </ul>
      
      <!-- content box -->
      <div class="content-box">
        <div class="content-box-inner"> <#include "/private/common/parts/application_info.ftl"/>
          <@spring.bind "applicationForm.*" />
          <@spring.bind "availableReasons.*" />
          <section class="form-rows">
            <h2 class="no-arrow">Reject Applicant</h2>
            <div>
              <form method="POST" action="<@spring.url '/rejectApplication/moveApplicationToReject'/>">
                <div class="alert alert-info"> <i class="icon-info-sign"></i> Reject the applicant. They will be prevented from reapplying for their chosen study programme in the current academic year. </div>
                <div class="row-group">
                  <div class="row">
                    <label class="plain-label">Reasons for Rejection<em>*</em></label>
                    <span class="hint" data-desc="<@spring.message 'rejection.reason'/>"></span>
                    <div id="reasonList" class="field">
                      <ul>
                        <#list availableReasons as reason>
                        <li>
                          <label> <#if RequestParameters.rejectionId?? && RequestParameters.rejectionId = reason.id?string >
                            <#assign checked = "checked">
                            <#else>
                            <#assign checked = "">
                            </#if>
                            <input type="radio" name="rejectionReason" value="${encrypter.encrypt(reason.id)}" class="reason" ${checked} />
                            ${reason.text}
                          </label>
                        </li>
                        </#list>
                      </ul>
                      <@spring.bind "rejection.rejectionReason" />
                      <#list spring.status.errorMessages as error>
                      <div class="alert alert-error"> <i class="icon-warning-sign"></i>
                        ${error}
                      </div>
                      </#list> </div>
                  </div>
                </div>
                <div class="row-group">
                  <div class="row">
                    <label class="plain-label" for="includeProspectusLink">Include a link to the UCL Postgraduate Prospectus in the rejection message?</label>
                    <span class="hint" data-desc="<@spring.message 'rejection.uclLink'/>"></span>
                    <div class="field">
                      <input type="checkbox" name="includeProspectusLink" id="includeProspectusLink" class="reason"/>
                    </div>
                  </div>
                </div>
                <div class="buttons">
                  <button type="submit" id="rejectButton" class="btn btn-primary">Submit</button>
                </div>
                <input type="hidden" id="applicationId" name="applicationId" value="${applicationForm.applicationNumber}"/>
              </form>
            </div>
          </section>
          <#include "/private/staff/admin/comment/timeline_application.ftl"/> </div>
        <!-- .content-box-inner --> 
      </div>
      <!-- .content-box --> 
      
    </article>
  </div>
  <!-- #middle --> 
  
  <#include "/private/common/global_footer.ftl"/> </div>
<!-- #wrapper --> 

<script type="text/javascript" src="<@spring.url '/design/default/js/approver/reject_page.js'/>"></script>
</body>
</html>
