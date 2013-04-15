<!DOCTYPE HTML>
<#import "/spring.ftl" as spring />

<#assign primarySupervisor = applicationForm.latestApprovalRound.primarySupervisor />
<#assign secondarySupervisor = applicationForm.latestApprovalRound.secondarySupervisor />
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta name="prism.version" content="<@spring.message 'prism.version'/>" >
<title>UCL Postgraduate Admissions</title>

<!-- Always force latest IE rendering engine (even in intranet) & Chrome Frame -->
<meta http-equiv="X-UA-Compatible" content="IE=9,chrome=1" />
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/pgStudents/form/qualifications.css' />"/>
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/pgStudents/form/references.css' />"/>
<link type="text/css" rel="stylesheet" href="<@spring.url '/design/default/css/private/global_private.css'/>" />
<link type="text/css" rel="stylesheet" href="<@spring.url '/design/default/css/private/application.css'/>" />
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

<div id="wrapper">
<#include "/private/common/global_header.ftl"/> 

<!-- Middle. -->
<div id="middle"> <#include "/private/common/parts/nav_with_user_info_toggle.ftl"/>
  <@header />
  <!-- Main content area. -->
  <article id="content" role="main">
  
  <!-- "Tools"
              <div id="tools">
                <ul class="left">
                  <li class="icon-print"><a target="_blank" title="Click to Download" href="<@spring.url '/print?applicationFormId=${applicationForm.applicationNumber}'/>">Download PDF</a></li>
                  <li class="icon-feedback"><a title="Send Feedback" href="mailto:prism@ucl.ac.uk?subject=Feedback" target="_blank">Send Feedback</a></li>
                </ul>
              </div> --> 
  
  <!-- FLOATING TOOLBAR 
  <ul id="view-toolbar" class="toolbar">
    <li class="top"><a href="javascript:backToTop();" title="Back to top">Back to top</a></li>
    <li class="print"><a target="_blank" href="<@spring.url '/print?applicationFormId=${applicationForm.applicationNumber}'/>" title="Click to Download">Print</a></li>
  </ul>-->
  
  <!-- content box -->
  <div class="content-box">
    <div class="content-box-inner"> <#include "/private/common/parts/application_info.ftl"/>
      <div id="approve-content">
        <section class="form-rows" id="approvalsection">
          <h2 class="no-arrow">Intention to Provide Primary Supervision</h2>
          <div>
            <form id="confirmSupervisionForm" method ="POST" action="<@spring.url '/confirmSupervision/applyConfirmSupervision' />" >
              <input type="hidden" id="applicationId" name="applicationId" value="${applicationForm.applicationNumber}"/>
              <div class="alert alert-info">
          			<i class="icon-info-sign"></i> Confirm your intention to provide primary supervision.</div>
              <div class="row-group">
                <div class="row">
                  <label class="plain-label">Confirm that you are willing to provide primary supervision<em>*</em></label>
                  <span class="hint" data-desc="<@spring.message 'confirmSupervision.confirm'/>"></span>
                  <div class="field">
                    <label><input type="radio" value="true" id="confirmSupervision" name="confirmedSupervision" <#if confirmSupervisionDTO.confirmedSupervision?? && confirmSupervisionDTO.confirmedSupervision>checked="checked"</#if>> Confirm</label>
                    <label><input type="radio" value="false" id="declineSupervision" name="confirmedSupervision" <#if confirmSupervisionDTO.confirmedSupervision?? && !confirmSupervisionDTO.confirmedSupervision>checked="checked"</#if>> Decline</label>
                    <@spring.bind "confirmSupervisionDTO.confirmedSupervision" />
                    <#list spring.status.errorMessages as error >
                    <div class="alert alert-error"> <i class="icon-warning-sign"></i>
                      ${error}
                    </div>
                    </#list> </div>
                </div>
                <div class="row">
                  <label for="declinedSupervisionReason" id="lbl_declinedSupervisionReason" class="plain-label">Reason<em>*</em></label>
                  <span class="hint" data-desc="<@spring.message 'confirmSupervision.reason'/>"></span>
                  <div class="field">
                    <textarea id="declinedSupervisionReason" name="declinedSupervisionReason"   class="max" cols="80" rows="6">${(confirmSupervisionDTO.declinedSupervisionReason?html)!}
</textarea>
                    <@spring.bind "confirmSupervisionDTO.declinedSupervisionReason" />
                    <#list spring.status.errorMessages as error >
                    <div class="alert alert-error"> <i class="icon-warning-sign"></i>
                      ${error}
                    </div>
                    </#list> </div>
                </div>
              </div>
              <div class="row-group">
                <h3 id="lbl_secondarySupervisor">Secondary Supervisor</h3>
                <div class="row">
                  <label class="plain-label" for="secondarySupervisorFirstname">Supervisor First Name<em>*</em></label>
                  <span class="hint" data-desc="<@spring.message 'confirmSupervision.secondSupervisor.firstName'/>"></span>
                  <div class="field">
                    <input type="text" value="${(secondarySupervisor.user.firstName?html)!}" disabled="disabled" id="secondarySupervisorFirstname" class="full" />
                  </div>
                </div>
                <div class="row">
                  <label class="plain-label" for="secondarySupervisorLastname">Supervisor Last Name<em>*</em></label>
                  <span class="hint" data-desc="<@spring.message 'confirmSupervision.secondSupervisor.lastName'/>"></span>
                  <div class="field">
                    <input type="text" value="${(secondarySupervisor.user.lastName?html)!}" disabled="disabled" id="secondarySupervisorLastname" class="full" />
                  </div>
                </div>
                <div class="row">
                  <label class="plain-label" for="secondarySupervisorEmail">Supervisor Email<em>*</em></label>
                  <span class="hint" data-desc="<@spring.message 'confirmSupervision.secondSupervisor.email'/>"></span>
                  <div class="field">
                    <input type="email" value="${(secondarySupervisor.user.email?html)!}" disabled="disabled" id="secondarySupervisorEmail" class="full"/>
                  </div>
                </div>
              </div>
              <div class="row-group">
                <h3 id="lbl_projectDescription">Project Description</h3>
                <div class="row">
                  <label for="projectTitle" id="lbl_projectTitle" class="plain-label">Project Title<em>*</em></label>
                  <span class="hint" data-desc="<@spring.message 'assignSupervisor.projectTitle'/>"></span>
                  <div class="field">
                    <input type="text" value="${(confirmSupervisionDTO.projectTitle?html)!}" id="projectTitle" name="projectTitle" class="full" />
                    <@spring.bind "confirmSupervisionDTO.projectTitle" />
                    <#list spring.status.errorMessages as error >
                    <div class="alert alert-error"> <i class="icon-warning-sign"></i>
                      ${error}
                    </div>
                    </#list> </div>
                </div>
                <div class="row">
                  <label id="lbl_projectAbstract" class="plain-label" for="projectAbstract">Project Abstract (ATAS)<em>*</em></label>
                  <span class="hint" data-desc="<@spring.message 'assignSupervisor.projectAbstract'/>"></span>
                  <div class="field">
                    <textarea id="projectAbstract" name="projectAbstract" class="max" cols="80" rows="6"  >${(confirmSupervisionDTO.projectAbstract?html)!}
</textarea>
                    <@spring.bind "confirmSupervisionDTO.projectAbstract" />
                    <#list spring.status.errorMessages as error >
                    <div class="alert alert-error"> <i class="icon-warning-sign"></i>
                      ${error}
                    </div>
                    </#list> </div>
                </div>
              </div>
              <div class="row-group">
                <h3 id="lbl_recommendedOffer">Recommended Offer</h3>
                <div class="row">
                  <label for="recommendedStartDate" id="lbl_recommendedStartDate" class="plain-label">Provisional Start Date<em>*</em></label>
                  <span class="hint" data-desc="<@spring.message 'assignSupervisor.offerStartDate'/>"></span>
                  <div class="field">
                    <input type="text" value="${(confirmSupervisionDTO.recommendedStartDate?string('dd MMM yyyy'))!}" id="recommendedStartDate" name="recommendedStartDate" class="half date" readonly>
                    <@spring.bind "confirmSupervisionDTO.recommendedStartDate" />
                    <#list spring.status.errorMessages as error >
                    <div class="alert alert-error"> <i class="icon-warning-sign"></i>
                      ${error}
                    </div>
                    </#list> </div>
                </div>
                <div class="row">
                  <label id="lbl_recommendedConditionsAvailable" class="plain-label">Recommended Offer Type<em>*</em></label>
                  <span class="hint" data-desc="<@spring.message 'assignSupervisor.offerType'/>"></span>
                  <div class="field">
                    <label><input type="radio" value="false" id="recommendedConditionsUnavailable" name="recommendedConditionsAvailable" <#if confirmSupervisionDTO.recommendedConditionsAvailable?? && !confirmSupervisionDTO.recommendedConditionsAvailable>checked="checked"</#if>> Unconditional</label>
                    <label><input type="radio" value="true" id="recommendedConditionsAvailable" name="recommendedConditionsAvailable" <#if confirmSupervisionDTO.recommendedConditionsAvailable?? && confirmSupervisionDTO.recommendedConditionsAvailable>checked="checked"</#if>> Conditional</label>
                    <@spring.bind "confirmSupervisionDTO.recommendedConditionsAvailable" />
                    <#list spring.status.errorMessages as error >
                    <div class="alert alert-error"> <i class="icon-warning-sign"></i>
                      ${error}
                    </div>
                    </#list> </div>
                </div>
                <div class="row">
                  <label for="recommendedConditions" id="lbl_recommendedConditions" class="plain-label">Recommended Conditions<em>*</em></label>
                  <span class="hint" data-desc="<@spring.message 'assignSupervisor.offerConditions'/>"></span>
                  <div class="field">
                    <textarea id="recommendedConditions" name="recommendedConditions" class="max" cols="80" rows="6"  >${(confirmSupervisionDTO.recommendedConditions?html)!}</textarea>
                    <@spring.bind "confirmSupervisionDTO.recommendedConditions" />
                    <#list spring.status.errorMessages as error >
                    <div class="alert alert-error"> <i class="icon-warning-sign"></i>
                      ${error}
                    </div>
                    </#list> </div>
                </div>
              </div>
              <div class="buttons">
                <button id="confirmSupervisionBtn" type="submit" class="btn btn-primary">Submit</button>
              </div>
            </form>
          </div>
        </section>
      </div>
      <#include "/private/staff/admin/comment/timeline_application.ftl"/> </div>
    </article>
  </div>
  <#include "/private/common/global_footer.ftl"/> </div>
<script type="text/javascript" src="<@spring.url '/design/default/js/supervisor/confirm_supervision.js'/>"></script>
</body>
</html>
</section>
