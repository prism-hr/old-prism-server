<!DOCTYPE HTML>
<#import "/spring.ftl" as spring />
<#setting locale = "en_US">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta name="prism.version" content="<@spring.message 'prism.version'/>" >
<title>UCL Postgraduate Admissions</title>
<!-- Always force latest IE rendering engine (even in intranet) & Chrome Frame -->
<meta http-equiv="X-UA-Compatible" content="IE=9,chrome=1" />
<!-- Styles for Application List Page -->
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/global_private.css' />"/>
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/application.css' />"/>
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/staff/timeline.css' />"/>
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/staff/state_transition.css' />"/>
<link rel="shortcut icon" type="text/css" href="<@spring.url '/design/default/images/favicon.ico' />"/>
<!-- Styles for Application List Page -->
<!--[if lt IE 9]>
    <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->
<script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/libraries.js' />"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/script.js'/>"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/superAdmin/edit_opportunity_request.js' />"></script>
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/bootstrap.min.css' />"/>
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/font-awesome.min.css' />"/>
<script type="text/javascript" src="<@spring.url '/design/default/js/bootstrap.min.js' />"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/tinymce/tinymce.min.js' />"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/tinymce/jquery.tinymce.min.js' />"></script>
</head>
<style type="text/css">
#advertisingDuration {
  width: 80px !important;
}
#studyOptions {
  width: 170px !important;
}
span.count {
  display: none;
}
</style>
<!--[if IE 9]>
  <body class="ie9">
  <![endif]-->
<!--[if lt IE 9]>
  <body class="old-ie">
  <![endif]-->
<!--[if (gte IE 9)|!(IE)]><!-->
<body>
<!--<![endif]-->
<div id="rejectOpportunityRequestModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
  <div class="modal-header">
    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
    <h3 id="myModalLabel">Respond to this opportunity request</h3>
  </div>
  <div id="rejectOpportunityRequestReasonDiv" class="modal-body">
    <p>Please provide any comment to approve or reject this opportunity request</p>
    <textarea cols="150" rows="6" class="input-xxlarge" id="rejectOpportunityRequestReasonText"></textarea>
    </p>
  </div>
  <div class="modal-footer">
    <input id="rejectOpportunityRequestUrl" type="hidden" value="${requestContext.requestUri}" />
    <button id="approve-button" class="btn btn-success">Approve</button>
    <button id="do-reject-opportunity-button" class="btn btn-danger" aria-hidden="true">Reject</button>
  </div>
</div>
<!-- Wrapper Starts -->
<div id="wrapper"> <#include "/private/common/global_header.ftl"/> 
  <!-- Middle Starts -->
  <div id="middle"> <#include "/private/common/parts/nav_with_user_info_toggle.ftl"/>
    <@header activeTab="requests"/>
    <!-- Main content area. -->
    <article id="content" role="main"> 
      <!-- content box -->
      <div class="content-box">
        <div class="content-box-inner">
          <div class="requestDetails">
            <div class="rsbox">
              <div class="authorBox bs-status">
                <span class="label label-info">Status</span>
                <div class="icon">
                    <#if opportunityRequest.status == "NEW">
                    New Request <span class="icon-status validation"></span>
                    <#elseif opportunityRequest.status == "REJECTED">
                    Rejected <span class="icon-status rejected"></span>
                    <#elseif opportunityRequest.status == "APPROVED">
                    Approved <span class="icon-status offer-recommended"></span>
                    </#if>
                </div>
                <div class="row authname"><strong>Author:</strong> ${(opportunityRequest.author.firstName)!} ${(opportunityRequest.author.lastName)!}
                </div>
                <div class="row"><strong>Email:</strong> ${(opportunityRequest.author.email)!}</div>
              </div>
            </div>
            <div class="requestinfo">
            <#if opportunityRequest.status == "NEW"><i class="icon-bell-alt"></i></#if> Code | ${(opportunityRequest.programTitle?html)!}
            </div>
            <div class="row">
            <label>Submitted</label> ${opportunityRequest.createdDate?string("dd MMM yyyy")}
          </div>
          </div>
          <div class="tabsContent">
            <ul class="tabs">
              <li class="current"><a id="requestBtn" href="#request">Request</a></li>
              <li class=""><a id="commentsBtn" href="#comments">Comments</a></li>
            </ul>
            <div class="tab-page" id="requestTab">
              <section class="form-rows">
                <div>
                  <form id="opportunityRequestEditForm" method="POST">
                    <input type="hidden" name="action" value="approve">
                    <div class="row-group">
                      <h3 class="no-arrow"> Opportunity Details  </h3>
                      <#include "/private/prospectus/opportunity_details_part.ftl"/> 
                    </div>
                    <div class="buttons">
                      <button id="reject-button" class="btn btn-primary">Respond</button>
                    </div>
                  </form>
                </div>
              </section>
            </div>
            <div class="tab-page" id="commentsTab"> 
              <section class="form-rows">
                <div>
                  <div class="row-group">
                    <ul id="timeline-statuses">
                      <li class="rejected"> 
                        <!-- Box start -->
                        <div class="box">
                          <div class="title"> <span data-desc="Pouyan Khalili (zcemg43@live.ucl.ac.uk) as: Administrator" class="icon-role administrator" data-hasqtip="35" aria-describedby="qtip-35"></span> <span class="name">Pouyan Khalili</span> <span class="datetime"><span class="datetime">  at </span></span> </div>
                          <p class="highlight">Request Rejected.</p> <i class="icon-minus-sign"></i> 
                        </div>
                        <div class="excontainer">
                          <ul class="status-info">
                            <li class="rejected">
                              <div class="box">
                                <div class="title">
                                  <span data-desc="Pouyan Khalili (zcemg43@live.ucl.ac.uk) as: Administrator" class="icon-role administrator" data-hasqtip="35" aria-describedby="qtip-35"></span> <span class="name">Pouyan Khalili</span>
                                  <em>Commented:</em>
                                </div>
                                <div class="textContainer">
                                  <p>This is the comment, if it doesn't exist dont display this block sarting for the div excontainer </p>
                                </div>
                              </div>
                            </li>
                          </ul>
                        </div>
                        <!-- Box end -->
                      </li>
                      <li class="offer_recommended"> 
                        <!-- Box start -->
                        <div class="box">
                          <div class="title"> <span data-desc="Pouyan Khalili (zcemg43@live.ucl.ac.uk) as: Administrator" class="icon-role administrator" data-hasqtip="35" aria-describedby="qtip-35"></span> <span class="name">Pouyan Khalili</span> <span class="datetime"><span class="datetime">  at </span></span> </div>
                          <p class="highlight">Request Approved. </p> <i class="icon-minus-sign"></i> 
                        </div>
                        <div class="excontainer">
                          <ul class="status-info">
                            <li class="offer_recommended">
                              <div class="box">
                                <div class="title">
                                  <span data-desc="Pouyan Khalili (zcemg43@live.ucl.ac.uk) as: Administrator" class="icon-role administrator" data-hasqtip="35" aria-describedby="qtip-35"></span> <span class="name">Pouyan Khalili</span>
                                  <em>Commented:</em>
                                </div>
                                <div class="textContainer">
                                  <p>This is the comment, if it doesn't exist dont display this block sarting for the div excontainer </p>
                                </div>
                              </div>
                            </li>
                          </ul>
                        </div>
                        <!-- Box end -->
                      </li>
                      <li class="not_submitted">
                        <!-- Box start -->
                        <div class="box">
                          <div class="title">
                            <span data-desc="${(opportunityRequest.author.firstName)!} ${(opportunityRequest.author.lastName)!} (${(opportunityRequest.author.email)!}) as: Requester" class="icon-role applicant" data-hasqtip="41"></span>
                            <span class="name">${(opportunityRequest.author.firstName)!} ${(opportunityRequest.author.lastName)!}</span>
                            <span class="datetime"><span class="datetime">${opportunityRequest.createdDate?string("dd MMM yyyy")} at ${opportunityRequest.createdDate?string('HH:mm')}</span></span>
                          </div>
                          <p class="highlight">Request created.</p>  
                       </div>
                      <!-- Box end -->
                      </li>
                    </ul>
                  </div>
                </div>
              </section>
            </div>
          </div>
        </div>
        <!-- .content-box-inner --> 
      </div>
      <!-- .content-box --> 
    </article>
  </div>
  <!-- Middle Ends --> 
  <#include "/private/common/global_footer.ftl"/> </div>
<!-- Wrapper Ends -->
</body>
</html>