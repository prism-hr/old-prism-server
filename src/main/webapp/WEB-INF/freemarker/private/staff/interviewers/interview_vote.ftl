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
            	<div>
            		<form method="post">
		              <h2>Accept Interview Time Slot(s)</h2>
		              <#assign interview = applicationForm.latestInterview>
		              <div>
		              	<div class="row-group">
			              	<div class="row" >
							    <label class="plain-label" for="programInterviewers">Interview Location</label>
							    <div class="field">
							    	<#if interview.locationURL?has_content>
										<a style="text-decoration:none;" href="${interview.locationURL}" title="Get Directions">${interview.locationURL}</a>
							        <#else>
							        	<i>Not provided</i>
							    	</#if>
							    </div>
							</div>
							<#if user == applicationForm.applicant>
								<div class="row">
								    <label class="plain-label" for="programInterviewers">Interview Instructions for Candidate</label>
								    <div class="field">
								    	<#if interview.furtherDetails?has_content>${interview.furtherDetails!}<#else><i>Not provided</i></#if>
								    </div>
								</div>
							<#else>
								<div class="row">
								    <label class="plain-label" for="programInterviewers">Interview Instructions for Interviewer</label>
								    <div class="field">
								    	<#if interview.furtherInterviewerDetails?has_content>${interview.furtherInterviewerDetails!}<#else><i>Not provided</i></#if>
								    </div>
								</div>
							</#if>
		              	</div>
		              	<div class="row-group">
			              	<div class="row">
			              		<#assign responded = 0>
			              		<#list interview.participants as participant>
			              			<#if participant.responded == true>
			              				<#assign responded = responded + 1>
			              			</#if>
			              		</#list>
			              		<p class="votes-summary">${responded} of ${interview.participants?size } participants have responded</p>
		            	  	
		            	  		<div id="add-info-bar-div" class="alert alert-info"> <i class="icon-info-sign"></i> Please select the times below that you would can attend the interview.</div>
		            	  		
		            	  		<div class="timeslots-wrapper">
			            	  		<div class="timeslots-scrollable">
				            	  		<table class="table timeslots">
				            	  			<thead>
				            	  				<th class="participant">&nbsp;</th>
				            	  				<th class="check-all-cell">Check All</th>
				            	  				<#list interview.timeslots as timeslot>
				            	  					<th>${timeslot.dueDate?string("dd MMM yy")}<br />${timeslot.startTime}</th>
				            	  				</#list>
				            	  			</thead>
				            	  			<tbody>
				            	  				<#list interview.participants as participant>
				        	  						<#if user == participant.user>
				        	  							<tr class="info">
				        	  								<td class="participant">
				        	  									<span class="icon-role <#if participant.user == applicationForm.applicant>applicant<#else>interviewer</#if>"></span>
				        	  									${participant.user.firstName!} ${participant.user.lastName!}
			        	  									</td>
			        	  									<td class="check-all-cell"><input type="checkbox" class="check-all"></td>
					        	  							
					        	  							<#assign acceptedTimeslots = participant.acceptedTimeslots>
					            	  						<#list interview.timeslots as timeslot>
					            	  							<td class="timeslot">
					            	  								<#if acceptedTimeslots?seq_contains(timeslot)>
					            	  								<input type="checkbox" name="acceptedTimeslotIds" class="timeslot-to-accept" value="${timeslot.id}" checked="checked" />
						            	  							<#else>
					            	  								<input type="checkbox" name="acceptedTimeslotIds" class="timeslot-to-accept" value="${timeslot.id}" />
						            	  							</#if>
					            	  							</td>
					            	  						</#list>
				        	  							</tr>
				        	  						<#else>
				        	  							<tr>
				        	  								<td class="participant">
				        	  									<span class="icon-role <#if participant.user == applicationForm.applicant>applicant<#else>interviewer</#if>"></span>
				        	  									${participant.user.firstName!} ${participant.user.lastName!}
			        	  									</td>
			        	  									<td class="check-all-cell">&nbsp;</td>
					            	  						
					            	  						<#if participant.responded>
						            	  						<#assign acceptedTimeslots = participant.acceptedTimeslots>
						            	  						<#list interview.timeslots as timeslot>
						            	  							<td class="timeslot">
						            	  								<#if acceptedTimeslots?seq_contains(timeslot)>
							            	  								<i class="icon-ok-sign sign-tooltip"></i>
							            	  							<#else>
							            	  								<i class="icon-remove-sign sign-tooltip"></i>
							            	  							</#if>
						            	  							</td>
						            	  						</#list>
						            	  					<#else>
						            	  						<#list interview.timeslots as timeslot>
						            	  							<td class="timeslot">
				            	  										<i class="icon-circle sign-tooltip" data-desc="<@spring.message 'interviewVote.notvotedyet'/>"></i>
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
		            	  	<@spring.bind "interviewParticipant.canMakeIt" />
  							<#list spring.status.errorMessages as error>
		            	  		<br />
					    		<div class="alert alert-error"> <i class="icon-warning-sign"></i> ${error}</div>
					    	</#list>
		            	</div>
		            	<div class="row-group">
		        			<div class="row">
							  	<label class="plain-label normal" for="comments">Comments</label>
							    <span class="hint" data-desc="<@spring.message 'interviewVote.comments'/>"></span>
							    <div class="field">
								  	<textarea id="comments" name="comments" class="max" rows="6" cols="80" maxlength="2000"></textarea>			
							  	</div>	
						  	</div>
		            	</div>
		              	<div class="buttons no-bottom-margin">
					        <button id="submitVoteYes" type="submit" class="btn btn-danger" name="canMakeIt" value="false">I can't make it</button>
					        <button id="submitVoteYes" type="submit" class="btn btn-primary" name="canMakeIt" value="true">Submit</button>
					    </div>
		              </div>
	              </form>
            	</div>
            </section>
        </div>
        
      </article>
      </section>
    </div>
    <!-- Middle Ends --> 
    
    <#include "/private/common/global_footer.ftl"/> </div>
  <!-- Wrapper Ends -->
  
  </body>
  </html>
