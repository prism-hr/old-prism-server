<!DOCTYPE HTML>
<#import "/spring.ftl" as spring />
<#setting locale = "en_US">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>UCL Postgraduate Admissions</title>

<!-- Always force latest IE rendering engine (even in intranet) & Chrome Frame -->
<meta http-equiv="X-UA-Compatible" content="IE=9,chrome=1" />
<meta name="prism.version" content="<@spring.message 'prism.version'/>" >
<link type="text/css" rel="stylesheet" href="<@spring.url '/design/default/css/private/global_private.css'/>" />
<link type="text/css" rel="stylesheet" href="<@spring.url '/design/default/css/private/application.css'/>" />
<link type="text/css" rel="stylesheet" href="<@spring.url '/design/default/css/private/staff/add_interviewer.css'/>" />
<link type="text/css" rel="stylesheet" href="<@spring.url '/design/default/css/actions.css' />" />
<link rel="shortcut icon" type="text/css" href="<@spring.url '/design/default/images/favicon.ico' />"/>
<script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/libraries.js'/>"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/script.js'/>"></script>
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/bootstrap.min.css' />"/>
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/font-awesome.min.css' />"/>
<script type="text/javascript" src="<@spring.url '/design/default/js/bootstrap.min.js' />"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/interviewer/interview.js'/>"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/interviewer/jquery-ui.multidatespicker.js'/>"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/interviewer/jquery.maskedinput.js'/>"></script>


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
      
      <!-- FLOATING TOOLBAR 
      <ul id="view-toolbar" class="toolbar">
        <li class="top"><a href="javascript:backToTop();" title="Back to top">Back to top</a></li>
        <li class="print"><a target="_blank" href="<@spring.url '/print?applicationFormId=${applicationForm.applicationNumber}'/>" title="Click to Download">Print</a></li>
      </ul>-->
      
      <!-- content box -->
      <div class="content-box">
        <div class="content-box-inner"> <#include "/private/common/parts/application_info.ftl"/>
          <input type="hidden" id="applicationId" value="${applicationForm.applicationNumber}"/>
          <section class="form-rows" id="interviewsection">
            <h2>Assign Interviewers</h2>
            <div>
              <form>
                <div id="add-info-bar-div"  class="alert alert-info"> <i class="icon-info-sign"></i> Assign interviewers to the application here. You may also create new interviewers. </div>
                <div class="row-group" id="assignInterviewersToAppSection"> </div>
                <div class="row-group" id="createinterviewersection"> </div>
                <div class="row-group" id="interviewdetailsSection"> </div>
                
                <div class="buttons">
                  <button class="btn btn-primary" type="button" id="moveToInterviewBtn">Submit</button>
                </div>
                <input type="hidden" id="applicationId" name="applicationId" value="${applicationForm.applicationNumber}"/>
                <input type="hidden" id="interviewId" name="interviewId" value="<#if interview?? && interview.id?? >${encrypter.encrypt(interview.id)}</#if>"/>
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
  <div id="postInterviewData"></div>
  <#include "/private/common/global_footer.ftl"/> </div>
<!-- #wrapper -->
<div style="display:none" id ="temp"></div>
</html>
</section>
