<!DOCTYPE HTML>
<#import "/spring.ftl" as spring />
  <#setting locale = "en_US">
  <html>
  <head>
  <meta name="prism.version" content="<@spring.message 'prism.version'/>" >
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
  <title>UCL Postgraduate Admissions</title>
  
  <!-- Always force latest IE rendering engine (even in intranet) & Chrome Frame -->
  <meta http-equiv="X-UA-Compatible" content="IE=9,chrome=1" />
  
  <!-- Styles for Application List Page -->
  <link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/pgStudents/form/terms_and_condition.css' />"/>
  <link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/global_private.css' />"/>
  <link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/application.css' />"/>
  <link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/staff/state_transition.css' />"/>
  <link rel="shortcut icon" type="text/css" href="<@spring.url '/design/default/images/favicon.ico' />"/>
  <!-- Styles for Application List Page -->
  
  <!--[if lt IE 9]>
<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
<![endif]-->
  
  <script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
  <script type="text/javascript" src="<@spring.url '/design/default/js/script.js' />"></script>
  <script type="text/javascript" src="<@spring.url '/design/default/js/interviewer/voting.js' />"></script>
  <script type="text/javascript" src="<@spring.url '/design/default/js/interviewer/jquery.mousewheel.js' />"></script>
  <script type="text/javascript" src="<@spring.url '/design/default/js/libraries.js' />"></script>
  <link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/bootstrap.min.css' />"/>
  <link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/font-awesome.min.css' />"/>
  <script type="text/javascript" src="<@spring.url '/design/default/js/bootstrap.min.js' />"></script>
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
  
  <!-- Wrapper Starts -->
  <div id="wrapper">
  <#include "/private/common/global_header.ftl"/> 
  
  <!-- Middle Starts -->
  <div id="middle">
  <#include "/private/common/parts/nav_with_user_info_toggle.ftl"/>
  <@header/>
  <!-- Main content area. -->
  <section id="reviewcommentsectopm" >
  <article id="content" role="main">
  <div class="content-box">
    <div class="content-box-inner"> <#include "/private/common/parts/application_info.ftl"/>
      <section class="interview-votes form-rows">
      <h2 class="no-arrow">Confirm Interview Arrangements</h2>
      <div>
        <form method="post" action= "<@spring.url '/interviewConfirm'/>" />
        
        <input type="hidden" id="applicationId" name ="applicationId" value ="${(applicationForm.applicationNumber)!}"/>
        <#assign interview = applicationForm.latestInterview>
        <#if timeslotIdError??>
        <div class="alert alert-error"> <i class="icon-warning-sign"></i> <#else>
          <div id="add-info-bar-div" class="alert alert-info"> <i class="icon-info-sign"></i> </#if>
            Please confirm the interview slot. </div>
          <div class="row-group">
            <div class="row"> <#assign responded = 0>
              <#list interview.participants as participant>
              <#if participant.responded == true>
              <#assign responded = responded + 1>
              </#if>
              </#list>
              <input id="interview-duration" hidden="true" value="${interview.duration?html}" />
              <div class="timeslots-wrapper">
                <div class="timeslots-scrollable">
                  <table class="table timeslots">
                    <thead>
                    <th class="participant">&nbsp;</th>
                      <#list interview.timeslots as timeslot>
                      <th class="timeslot-header"><strong>
                        ${timeslot.dueDate?string("dd MMM yy")}
                        </strong> <br />
                        <span class="start-time">
                        ${timeslot.startTime}
                        </span> - <span class="end-time"></span></th>
                      </#list>
                      </thead>
                    <tbody>
                    <#list interview.participants as participant>
                      <#if participant.user == applicationForm.applicant>
                        <#assign userTypeClass = "applicant"> 
                        <#assign userTypeName = "Applicant"> 
                      <#else>
                        <#assign userTypeClass = "interviewer"> 
                        <#assign userTypeName = "Interviewer"> 
                      </#if>                    
                      <tr>
                        <td class="participant"><div><span data-desc="${userTypeName}" class="icon-role ${userTypeClass}"></span>
                            ${participant.user.firstName!}
                            ${participant.user.lastName!}
                          </div></td>
                        <#if participant.responded>
                        <#assign acceptedTimeslots = participant.acceptedTimeslots>
                        <#list interview.timeslots as timeslot>
                        <td class="timeslot"><div> <#if acceptedTimeslots?seq_contains(timeslot)> <i class="icon-ok-sign sign-tooltip" data-desc="Available"></i> <#else> <i class="icon-remove-sign sign-tooltip" data-desc="Not Available"></i> </#if> </div></td>
                        </#list>
                        <#else>
                        <#list interview.timeslots as timeslot>
                        <td class="timeslot"><div> <i class="icon-time sign-tooltip" data-desc="<@spring.message 'interviewVote.notvotedyet'/>"></i> </div></td>
                        </#list>
                        </#if>
                      </tr>
                    </#list>
                    </tbody>
                    
                    <tfoot>
                      <tr>
                        <td class="participant"></td>
                        <#list interview.timeslots as timeslot>
                        <td class="timeslot"><div>
                            <input type="radio" name="timeslotId" value=${timeslot.id} />
                          </div></td>
                        </#list> </tr>
                    </tfoot>
                  </table>
                </div>
              </div>
            </div>
            <@spring.bind "interviewConfirmDTO.timeslotId" />
            <#list spring.status.errorMessages as error >
            <div class="alert alert-error"> <i class="icon-warning-sign"></i>
              ${error}
            </div>
            </#list> </div>
          <div class="row-group">
            <div class="row">
                      <label class="plain-label normal" for="instructionsForInterviewer">Interview Instructions (Interviewer)
              </label>
              <span class="hint" data-desc="<@spring.message 'assignInterviewer.interviewerInstructions'/>"></span>
              <div class="field">
                <textarea id="furtherInterviewerDetails" name="furtherInterviewerDetails" class="max" rows="6" cols="80">${interviewConfirmDTO.furtherInterviewerDetails!}
</textarea>
              </div>
            </div>
            <div class="row">
                      <label class="plain-label normal" for="furtherDetails">Interview Instructions (Applicant)
              </label>
              <span class="hint" data-desc="<@spring.message 'assignInterviewer.candidateInstructions'/>"></span>
              <div class="field">
                <textarea id="furtherDetails" name="furtherDetails" class="max" rows="6" cols="80">${interviewConfirmDTO.furtherDetails!}
</textarea>
              </div>
            </div>
            <div class="row">
              <label class="plain-label normal" for="locationUrl" >Interview Location (URL)</label>
              <span class="hint" data-desc="<@spring.message 'assignInterviewer.location'/>"></span>
              <div class="field">
                <input type="text" id="locationUrl" name="locationUrl" class="input-xxlarge" placeholder="e.g. http://www.ucl.ac.uk/locations/ucl-maps/" value="${(interviewConfirmDTO.locationUrl?html)!}">
              </div>
            </div>
          </div>
          <div class="buttons">
            <button type="button" class="btn btn-danger" id="restart-interview">Start Again</button>
            <button type="submit" class="btn btn-primary">Confirm Interview</button>
          </div>
          </form>
          <form id="restart-interview-form" method="post" action="<@spring.url '/interviewConfirm/restart' />?applicationId=${applicationForm.applicationNumber}">
          </form>
        </div>
        </section>
       <#include "/private/staff/admin/comment/timeline_application.ftl"/> 
     </div>
      </article>
      </section>
    </div>
    <!-- Middle Ends --> 
    
    <#include "/private/common/global_footer.ftl"/> </div>
  <!-- Wrapper Ends -->
  
  </body>
  </html>
  