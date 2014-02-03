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
      
      <!-- content box -->
      <div class="content-box">
        <div class="content-box-inner">
        <#include "/private/common/parts/application_info.ftl"/>
          <section class="form-rows">
            <h2 class="no-arrow">Confirm Offer Recommendation</h2>
            <div>
              <form id="offerRecommendationForm" method="POST" action="<@spring.url '/offerRecommendation'/>">				
          		  <@spring.bind "offerRecommendedComment.*" />
                <#if spring.status.errors.hasErrors()>
                  <div class="alert alert-error"> <i class="icon-warning-sign"></i> 
                <#else>
           	  	  <div class="alert alert-info"> <i class="icon-info-sign"></i> 
                </#if>
                    Confirm the details of the study offer that you wish to recommend.
                </div>
                
                <!-- Assign supervisors --> 
                <#assign supervisors = offerRecommendedComment.supervisors>
                <#assign supervisorsEntityName = 'offerRecommendedComment'>
                <#include "/private/staff/supervisors/components/assign_supervisors_section.ftl"/>
                                
                <div class="row-group">
                    <h3 id="lbl_projectDescription">Project Description</h3>
                    <div class="row">
                        <label for="projectTitle" id="lbl_projectTitle" class="plain-label">Project Title<em>*</em></label>
                        <span class="hint" data-desc="<@spring.message 'assignSupervisor.projectTitle'/>"></span>
                        <div class="field">
                            <input type="text" value="${(offerRecommendedComment.projectTitle?html)!}" id="projectTitle" name="projectTitle" class="full" />
                            <@spring.bind "offerRecommendedComment.projectTitle" />
                            <#list spring.status.errorMessages as error >
                              <div class="alert alert-error"> <i class="icon-warning-sign"></i>
                                ${error}
                              </div>
                            </#list>
                        </div>
                    </div>
                    <div class="row">
                        <label id="lbl_projectAbstract" class="plain-label" for="projectAbstract">Project Abstract (ATAS)<em>*</em></label>
                        <span class="hint" data-desc="<@spring.message 'assignSupervisor.projectAbstract'/>"></span>
                        <div class="field">
                            <textarea id="projectAbstract" name="projectAbstract" class="max" cols="80" rows="6">${(offerRecommendedComment.projectAbstract?html)!}</textarea>
                            <@spring.bind "offerRecommendedComment.projectAbstract" />
                            <#list spring.status.errorMessages as error >
                              <div class="alert alert-error"> <i class="icon-warning-sign"></i>
                                ${error}
                              </div>
                            </#list>                                  
                        </div>
                    </div>
                </div>
                      
                <div class="row-group">
                    <h3 id="lbl_recommendedOffer">Recommended Offer</h3>
                    <div class="row">
                        <label for="recommendedStartDate" id="lbl_recommendedStartDate" class="plain-label">Provisional Start Date<em>*</em></label>
                        <span class="hint" data-desc="<@spring.message 'assignSupervisor.offerStartDate'/>"></span>
                        <div class="field">
                            <input type="text" value="${(offerRecommendedComment.recommendedStartDate?string('dd MMM yyyy'))!}" id="recommendedStartDate" name="recommendedStartDate" class="half date" readonly>
                            <@spring.bind "offerRecommendedComment.recommendedStartDate" />
                            <#list spring.status.errorMessages as error >
                              <div class="alert alert-error"> <i class="icon-warning-sign"></i>
                                ${error}
                              </div>
                            </#list>
                        </div>
                    </div>
                    <div class="row">
                        <label id="lbl_recommendedConditionsAvailable" class="plain-label">Recommended Offer Type<em>*</em></label>
                        <span class="hint" data-desc="<@spring.message 'assignSupervisor.offerType'/>"></span>
                        <div class="field">
                            <label>
                            <input <#if offerRecommendedComment.recommendedConditionsAvailable?? && !offerRecommendedComment.recommendedConditionsAvailable>checked="checked"</#if>
                              type="radio" value="false" id="recommendedConditionsUnavailable" name="recommendedConditionsAvailable"> Unconditional</label>
                            <label>
                            <input <#if offerRecommendedComment.recommendedConditionsAvailable?? && offerRecommendedComment.recommendedConditionsAvailable>checked="checked"</#if>
                            type="radio" value="true" id="recommendedConditionsAvailable" name="recommendedConditionsAvailable"> Conditional</label>
                            
                            <@spring.bind "offerRecommendedComment.recommendedConditionsAvailable" />
                            <#list spring.status.errorMessages as error >
                            <div class="alert alert-error"> <i class="icon-warning-sign"></i>
                              ${error}
                            </div>
                            </#list>
                        </div>
                    </div>
                    <div class="row">
                        <label for="recommendedConditions" id="lbl_recommendedConditions" class="plain-label">Recommended Conditions<em>*</em></label>
                        <span class="hint" data-desc="<@spring.message 'assignSupervisor.offerConditions'/>"></span>
                        <div class="field">
                          <textarea id="recommendedConditions" name="recommendedConditions" class="max" cols="80" rows="6">${(offerRecommendedComment.recommendedConditions?html)!}</textarea>
                          <@spring.bind "offerRecommendedComment.recommendedConditions" />
                          <#list spring.status.errorMessages as error >
                            <div class="alert alert-error"> <i class="icon-warning-sign"></i>
                              ${error}
                            </div>
                          </#list>
                        </div>
                    </div>
                </div>

                <div class="buttons">
                  <button type="submit" id="offerRecommendationSubmitButton" class="btn btn-primary">Submit</button>
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

<script type="text/javascript" src="<@spring.url '/design/default/js/approver/offer_recommendation_page.js'/>"></script>
</body>
</html>