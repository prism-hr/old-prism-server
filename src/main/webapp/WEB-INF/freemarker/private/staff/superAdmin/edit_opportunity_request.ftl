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
                      <#if opportunityRequest.type == "CHANGE">
                        Change Request<span data-desc="Change Request" class="icon-status withdrawn"></span>
                      <#else>
                        New Request<span data-desc="New Request" class="icon-status review"></span>
                      </#if>
                    <#elseif opportunityRequest.status == "REJECTED">
                      Rejected<span data-desc="Rejected" class="icon-status rejected"></span>
                    <#elseif opportunityRequest.status == "APPROVED">
                      Approved<span data-desc="Approved" class="icon-status offer-recommended"></span>
                    <#elseif opportunityRequest.status == "REVISED">
                      Revised<span data-desc="Revised" class="icon-status validation"></span>
                    </#if>
                </div>
                <div class="row authname"><strong>Author:</strong> 
                ${(opportunityRequest.author.firstName)!} ${(opportunityRequest.author.lastName)!}
                </div>
                <div class="row"><strong>Email:</strong> <a href="mailto:${(opportunityRequest.author.email)!}?subject=Question Regarding UCL Prism Program request: ${opportunityRequest.programTitle!opportunityRequest.sourceProgram.title}"> <i class="icon-envelope-alt"></i> ${(opportunityRequest.author.email)!}</a> </div>
              </div>
            </div>
            <div class="requestinfo">
              <#if opportunityRequest.status == "NEW">
                <i class="icon-bell-alt"></i>
              </#if>
              <#if opportunityRequest.sourceProgram??>
                ${opportunityRequest.sourceProgram.code?html}
              </#if>
              ${opportunityRequest.programTitle!opportunityRequest.sourceProgram.title}
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
                    <#assign isRequestEditable = opportunityRequest.status != "APPROVED">
                    <div class="row-group">
                      <h3 class="no-arrow">
                        <#if user.isInRole('SUPERADMINISTRATOR')>
                          Opportunity Details
                        <#else>
                          Revise Opportunity Request
                        </#if> 
                      </h3>
                      
                      <input id="isRequestEditable" type="hidden" value="${isRequestEditable?c}"> 
                      
                      <#if isRequestEditable>
                      
                        <#assign comments = opportunityRequest.comments>
                        <#if user.id == opportunityRequest.author.id && comments?has_content && comments?last.commentType == "REJECT"> 
                          <div class="alert alert-warning">
                            Please revise your request. Recent rejection reason:
                            <p>
                              <i>${comments?last.content}</i>
                            </p>
                          </div>
                        </#if>
                        
                        <#include "/private/prospectus/opportunity_details_part.ftl"/>
                      <#else>
                      
                        <div class="admin_row">
                          <label class="admin_row_label">Institution Country</label>
                          <div class="field">${(opportunityRequest.institutionCountry.name?html)!}</div>
                        </div>
                        
                        <div class="admin_row">
                          <label class="admin_row_label">Institution Name</label>
                          <#if opportunityRequest.institutionCode == "OTHER">
                            <div class="field">${(opportunityRequest.otherInstitution?html)!}</div>
                          <#else>
                            <#list institutions as inst>
                              <#if opportunityRequest.institutionCode == inst.code>
                                <div class="field">${(inst.name?html)!}</div>
                              </#if>
                            </#list>
                          </#if>
                        </div>

                        <div class="admin_row">
                          <label class="admin_row_label">Program Title</label>
                          <div class="field">${(opportunityRequest.programTitle?html)!}</div>
                        </div>

                        <div class="admin_row">
                          <label class="admin_row_label">Program Description</label>
                          <div class="field">${(opportunityRequest.programDescription)!}</div>
                        </div>

                        <div class="admin_row">
                          <#assign selectedOptionsString = opportunityRequest.studyOptions!"">    
                          <#assign selectedOptions = selectedOptionsString?split(",")>
                          <label class="admin_row_label">Study Options</label>
                          <#list studyOptions as studyOption>
                            <#if selectedOptions?seq_contains(studyOption.id)>
                              <div class="field">${(studyOption.id)!}</div>
                            </#if>
                          </#list>
                        </div>
                        
                        <div class="admin_row">
                          <label class="admin_row_label">Duration of Study</label>
                          <#assign unit = opportunityRequest.studyDurationUnit!>
                          <div class="field">${(opportunityRequest.studyDurationNumber?string)!} ${(unit=="MONTHS")?string("Months","Years")}</div>
                        </div>
                        
                        <div class="admin_row">
                          <label class="admin_row_label">Does the opportunity require ATAS?</label>
                          <div class="field">${(opportunityRequest.atasRequired)?string("Yes","No")}</div>
                        </div>
                        
                        <div class="admin_row">
                          <label class="admin_row_label">Advertise deadline</label>
                          <div class="field">30 September ${(opportunityRequest.advertisingDeadlineYear?c)!}</div>
                        </div>
                        
                      </#if>
                      
                    </div>

                    <#if isRequestEditable>
                      <div class="row-group">
                        <h3 class="no-arrow">Revision Details</h3>
  
                        <div class="row">
                          <label id="commentContentLabel" class="plain-label" for="commentContent">
                            <#if user.isInRole('SUPERADMINISTRATOR')>
                              Comment<em>*</em>
                            <#else>
                              Description of Changes<em>*</em>
                            </#if>                             
                          </label>
                          <span class="hint" data-desc="<@spring.message 'opportunityRequestComment.contentTooltip'/>"></span>
                          <div class="field">
                            <textarea id="commentContent" name="content" class="max" cols="70" rows="6">${(comment.content?html)!}</textarea>
                            <@spring.bind "comment.content" />
                            <#list spring.status.errorMessages as error>
                              <div class="alert alert-error"> <i class="icon-warning-sign"></i>
                                ${error}
                              </div>
                            </#list>
                          </div>
                        </div>
                        
                        <#if user.isInRole('SUPERADMINISTRATOR')>
                          <div class="row">
                            <label id="commentTypeLabel" class="plain-label" for="commentType">Review outcome<em>*</em></label>
                            <span class="hint" data-desc="<@spring.message 'opportunityRequestComment.commentType'/>"></span>
                            <div class="field">
                              <select id="commentType" name="commentType">
                                <option value="">Select...</option>
                                <option value="APPROVE" <#if comment.commentType?? && comment.commentType == "APPROVE">selected="selected"</#if>>Approve</option>
                                <option value="REJECT" <#if comment.commentType?? && comment.commentType == "REJECT">selected="selected"</#if>>Reject</option>
                              </select>
                              <@spring.bind "comment.commentType" />
                              <#list spring.status.errorMessages as error>
                                <div class="alert alert-error"> <i class="icon-warning-sign"></i>
                                  ${error}
                                </div>
                              </#list>
                            </div>
                          </div>
                        <#else>
                          <input name="commentType" type="hidden" value="REVISE" />
                        </#if> 
                        
                      </div>
                    
                      <div class="buttons">
                        <button id="submitOpportunityRequestButton" class="btn btn-success">Submit</button>
                      </div>
                    </#if>
                    
                  </form>
                </div>
              </section>
            </div>
            
            <div class="tab-page" id="commentsTab"> 
              <section class="form-rows">
                <div>
                  <div class="row-group">
                    <ul id="timeline-statuses">
                      <#list opportunityRequests as opportunityRequest>
                        <#list opportunityRequest.comments?reverse as comment>     
                          
                          <#assign author = comment.author> 
                          
                          <#if comment.commentType == "REJECT">
                            <#assign elementClass = "rejected">
                            <#assign actionText = "Request Rejected.">
                          <#elseif comment.commentType == "APPROVE">
                            <#assign elementClass = "offer_recommended">
                            <#assign actionText = "Request Approved.">
                          <#else>
                            <#assign elementClass = "approval">
                            <#assign actionText = "Request Revised.">
                          </#if>               
                          
                          <#if comment.author.id == opportunityRequest.author.id>
                            <#assign roleName = "Author">
                            <#assign roleClassName = "applicant">
                          <#else>
                            <#assign roleName = "Administrator">
                            <#assign roleClassName = "administrator">
                          </#if>
                          
                          <li class="${elementClass}"> 
                            <!-- Box start -->
                            <div class="box">
                              <div class="title"> <span data-desc="${author.displayName?html} (${author.email?html}) as: ${roleName}" class="icon-role ${roleClassName}" data-hasqtip="35" aria-describedby="qtip-35"></span> <span class="name">${author.displayName?html}</span> <span class="datetime"><span class="datetime">${comment.createdTimestamp?string("dd MMM yyyy")} at ${comment.createdTimestamp?string('HH:mm')}</span></span> </div>
                              <p class="highlight">${actionText}</p>
                              <#if comment.content??>
                                <i class="icon-minus-sign"></i>
                              </#if> 
                            </div>
                            <#if comment.content??>
                              <div class="excontainer">
                                <ul class="status-info">
                                  <li class="${elementClass}">
                                    <div class="box">
                                      <div class="title">
                                        <span data-desc="${author.displayName?html} (${author.email?html}) as: ${roleName}" class="icon-role ${roleClassName}" data-hasqtip="35" aria-describedby="qtip-35"></span> <span class="name">${author.displayName?html}</span>
                                        <em>Commented:</em>
                                      </div>
                                      <div class="textContainer">
                                        <p>${comment.content?html}</p>
                                      </div>
                                    </div>
                                  </li>
                                </ul>
                              </div>
                            </#if>
                            <!-- Box end -->
                          </li>
                        </#list>
                        <li class="not_submitted">
                          <!-- Box start -->
                          <div class="box">
                            <div class="title">
                              <span data-desc="${(opportunityRequest.author.firstName)!} ${(opportunityRequest.author.lastName)!} (${(opportunityRequest.author.email)!}) as: Requester" class="icon-role applicant" data-hasqtip="41"></span>
                              <span class="name">${(opportunityRequest.author.firstName)!} ${(opportunityRequest.author.lastName)!}</span>
                              <span class="datetime"><span class="datetime">${opportunityRequest.createdDate?string("dd MMM yyyy")} at ${opportunityRequest.createdDate?string('HH:mm')}</span></span>
                            </div>
                            <p class="highlight">${(opportunityRequest.type=="CHANGE")?string("Opportunity change request","New opportunity request")} created.</p>  
                         </div>
                        <!-- Box end -->
                        </li>
                      </#list>
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