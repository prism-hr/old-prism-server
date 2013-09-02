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
  <div id="wrapper"> <#include "/private/common/global_header.ftl"/> 
    
    <!-- Middle Starts -->
    <div id="middle"> <#include "/private/common/parts/nav_with_user_info_toggle.ftl"/>
      <@header/>
      <!-- Main content area. -->
      <section id="reviewcommentsectopm" >
      <article id="content" role="main"> 
        
        <div class="content-box">
          <div class="content-box-inner"> <#include "/private/common/parts/application_info.ftl"/>
            <section class="interview-votes form-rows">
            	<h2>Provide Interview Availability</h2>
            	<div>
		      		<form method="post" action= "<@spring.url '/interviewVote'/>" />
		      		<input type="hidden" id="applicationId" name ="applicationId" value ="${(applicationForm.applicationNumber)!}"/>
	              <#assign interview = applicationForm.latestInterview>
                <@spring.bind "interviewParticipant.canMakeIt" />
  							<#if spring.status.errors.hasErrors()>
					    		<div class="alert alert-error"> <i class="icon-warning-sign"></i> 
                <#else>
                	<div id="add-info-bar-div" class="alert alert-info"> <i class="icon-info-sign"></i>
					    	</#if>
                  Please select your interview preferences.</div>
                        
            	    <div class="row-group">
                        
		              	<div class="row">
		              		<#assign responded = 0>
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
				            	  					<th class="timeslot-header"><strong>${timeslot.dueDate?string("dd MMM yy")}</strong> <br /><span class="start-time">${timeslot.startTime}</span> - <span class="end-time"></span></th>
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
  				        	  						<#if user == participant.user>
  				        	  							<tr class="info">
  				        	  								<td class="participant">
  				        	  									<div><span data-desc="${userTypeName}" class="icon-role ${userTypeClass}"></span>
    				        	  									${participant.user.firstName!} ${participant.user.lastName!} 
    				        	  									<#assign allSelected = RequestParameters.selectAll?? && RequestParameters.selectAll == 'true'> 
    				        	  									<input type="checkbox" <#if allSelected>checked</#if> class="check-all sign-tooltip" data-desc="Select/Deselect all">
                                        </div>
  			        	  									</td>
  					        	  							
  					        	  							<#assign acceptedTimeslots = participant.acceptedTimeslots>
  					            	  						<#list interview.timeslots as timeslot>
  					            	  							<td class="timeslot">
  					            	  							  <div>
    					            	  								
    					            	  								<input type="checkbox" name="acceptedTimeslots" class="timeslot-to-accept" value="${timeslot.id}" 
    					            	  								  <#if allSelected || acceptedTimeslots?seq_contains(timeslot)>checked="checked"</#if> /> 
  						            	  							</div>
  					            	  							</td>
  					            	  						</#list>
  				        	  							</tr>
  				        	  						<#else>
  				        	  							<tr>
  				        	  								<td class="participant">
  				        	  									<div>
  				        	  									  <span data-desc="${userTypeName}" class="icon-role ${userTypeClass}"></span>
  				        	  									  ${participant.user.firstName!} ${participant.user.lastName!}
  				        	  									</div>
  			        	  									</td>
  			        	  									
  					            	  						
  					            	  						<#if participant.responded>
  						            	  						<#assign acceptedTimeslots = participant.acceptedTimeslots>
  						            	  						<#list interview.timeslots as timeslot>
  						            	  							<td class="timeslot"> <div>
  						            	  								<#if acceptedTimeslots?seq_contains(timeslot)>
  							            	  								<i class="icon-ok-sign sign-tooltip"></i>
  							            	  							<#else>
  							            	  								<i class="icon-remove-sign sign-tooltip"></i>
  							            	  							</#if> </div>
  						            	  							</td>
  						            	  						</#list>
  						            	  					<#else>
  						            	  						<#list interview.timeslots as timeslot>
  						            	  							<td class="timeslot"><div>
  				            	  										<i class="icon-time sign-tooltip" data-desc="<@spring.message 'interviewVote.notvotedyet'/>"></i> </div>
  						            	  							</td>
  						            	  						</#list>
  						            	  					</#if>
  				        	  							</tr>
  				        	  						</#if>
				            	  				</#list>
				            	  			</tbody>
				            	  		</table>
				            	  	</div>
		            	  		</div>
		            	  	</div>
                            <div class="field">
		            	  	<@spring.bind "interviewParticipant.canMakeIt" />
  							<#list spring.status.errorMessages as error>
		            	  		
					    		<div class="alert alert-error"> <i class="icon-warning-sign"></i> ${error}</div>
					    	</#list>
                            </div>
		            	</div>
		            	<div class="row-group">
		        			<div class="row">
							  	<label class="plain-label normal" for="comments">Comments</label>
							    <span class="hint" data-desc="<@spring.message 'interviewVote.comments'/>"></span>
							    <div class="field">
								  	<textarea id="comment" name="comment" class="max" rows="6" cols="80"></textarea>			
							  	</div>	
						  	</div>
		            	</div>
		              	<div class="buttons">
		              		<button id="submitVoteYes" type="submit" class="btn btn-danger" name="canMakeIt" value="false">I cannot make it</button>
					        <button id="submitVoteYes" type="submit" class="btn btn-primary" name="canMakeIt" value="true">Submit Preferences</button>
					    </div>
	              </form>
            	</div>
            </section>
        </div>
        
        <div class="content-box">
          <div class="content-box-inner">
            <#include "/private/staff/admin/comment/timeline_application.ftl"/>
          </div>
        </div>
        
      </article>
      </section>
    </div>
    <!-- Middle Ends --> 
    

    <#include "/private/common/global_footer.ftl"/> </div>
  <!-- Wrapper Ends -->
  
  </body>
  </html>
