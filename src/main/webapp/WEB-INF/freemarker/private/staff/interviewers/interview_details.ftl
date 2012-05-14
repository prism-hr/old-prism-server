<section id="interviewSection" >	
<!DOCTYPE HTML>
<#import "/spring.ftl" as spring />
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>UCL Postgraduate Admissions</title>

<!-- Always force latest IE rendering engine (even in intranet) & Chrome Frame -->
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />

<link rel="stylesheet" type="text/css"
	href="<@spring.url '/design/default/css/private/global_private.css' />" />
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/application.css' />"/>
<link type="text/css" rel="stylesheet"
	href="<@spring.url '/design/default/css/actions.css' />" />

<!--[if lt IE 9]>
		<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
		<![endif]-->
</head>

<body>

	<div id="wrapper">

		<!-- Header. -->
		<div id="header">
			<p>Postgraduate Research Admissions Tool</p>
		</div>

		<!-- Middle. -->
		<div id="middle">

			<header>

				<!-- App logo and tagline -->
				<div class="logo">
					<img src="<@spring.url '/design/default/images/ph_logo_app.png'/>"
						alt="" />
				</div>

				<div class="tagline">Your Gateway to Research Opportunities</div>

				<!-- Main tabbed menu -->
				<nav>
					<ul>
						<li><a href="#">My account</a></li>
						<li class="current"><a href="<@spring.url '/applications'/>">My
								applications</a></li>
						<li><a href="#">Help</a></li>
					</ul>

					<div class="user">
						${user.firstName?html} ${user.lastName?html} <a
							class="button blue user-logout"
							href="<@spring.url '/j_spring_security_logout'/>">Logout</a>
					</div>
				</nav>

			</header>


			<!-- Main content area. -->
			<article id="content" role="main">

				<!-- content box -->
				<div class="content-box">
					<div class="content-box-inner">
						<@spring.bind "applicationForm.*" />
						<@spring.bind "programmeInterviewers.*" />
						<@spring.bind "applicationInterviewers.*" />
						<@spring.bind "programme.*" />
						<@spring.bind "unsavedInterviewers.*" />
						<div id="messageSection"></div>
						Application ID: ${(applicationForm.id?string('#####'))!} 
						<br></br>
						Program name: ${(programme.title?string)!} 
						<br></br> 
						Available interviewers in programme: 
						<select id="programInterviewers" multiple="multiple">
							<#list programmeInterviewers as interviewer>
							  <option value="${interviewer.id?string('#####')}">${interviewer.firstName?html} ${interviewer.lastName?html} <#if !interviewer.enabled> - Pending</#if></option>
							</#list>
							
						</select>
						<div class="buttons">
							 <#if assignOnly?? && assignOnly == false><button class="blue" type="button" id="removeInterviewerBtn">Remove</button></#if>
							<button type="button" id="addInterviewerBtn">Add interviewer</button>
						</div>
						<br></br> 
						Already interviewers of this application: 
						<select id="applicationInterviewers" multiple="multiple">
							<#list applicationInterviewers as interviewer>
								<option value="${interviewer.id?string('#####')}">${interviewer.firstName?html} ${interviewer.lastName?html} <#if !interviewer.enabled> - Pending</#if></option>
							</#list>
							<#list unsavedInterviewers as unsaved>
							   <#if applicationInterviewers?seq_index_of(unsaved) < 0>
								<option value="${unsaved.id?string('#####')}">${unsaved.firstName?html} ${unsaved.lastName?html} <#if !unsaved.enabled> - Pending</#if></option>
							   </#if>
							</#list>
						</select>
						<p>${message!}</p>
						  <div class="row">
                               <label class="label">First Name<em>*</em></label>
                                   <div class="field">
                                   <input class="full" type="text" name="newInterviewerFirstName" id="newInterviewerFirstName"/>
                              		<@spring.bind "interviewer.firstName" /> 
	                			   <#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>
                             
                               </div>
                               </div>
                                <div class="row">
                                    <label class="label">Last Name<em>*</em></label>
                                    <div class="field">
                                        <input class="full" type="text" name="newInterviewerLastName" id="newInterviewerLastName"/>
                                        <@spring.bind "interviewer.lastName" /> 
	                			   		<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>
                                    </div>
                                </div>
                                
                             <div class="row">
                                <label class="label">Email<em>*</em></label>
                                        <div class="field">
                                         <input class="full" type="text"  name="newInterviewerEmail" id="newInterviewerEmail"/>
                                          <@spring.bind "interviewer.email" /> 
	                			   		  <#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>
                                        </div>
                             </div>
						
						<div class="buttons">
							<button type="button" id="createInterviewer">Create Interviewer</button>
						</div>
						
						
						  <div class="row">
                               <label  class="label">Interview Date<em>*</em></label>
                                   <div class="field">
                                     <#if assignOnly?? && assignOnly>
                                      <input class="full date hasDatepicker" readonly="readonly" disabled="disabled" type="text" name="interviewDate" id="interviewDate" value="${(interview.interviewDueDate?string('dd-MMM-yyyy'))!}" />
        							<#else>
        			     		   		<input class="full date hasDatepicker" type="text" name="interviewDate" id="interviewDate" value="${(interview.interviewDueDate?string('dd-MMM-yyyy'))!}" />
        							</#if>
                                   <@spring.bind "interview.interviewDueDate" /> 
	                			   <#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>
                               </div>
                               </div>
                                <div class="row">
                                    <label class="label">Further Details<em>*</em></label>
                                    <div class="field">
                                     <#if assignOnly?? && assignOnly>
                                     <textarea id="furtherDetails" readonly="readonly" disabled="disabled" name="furtherDetails" class="max" rows="6" cols="80" maxlength='5000'>${interview.furtherDetails!}</textarea>
        							<#else>
        			     		   	<textarea id="furtherDetails" name="furtherDetails" class="max" rows="6" cols="80" maxlength='5000'>${interview.furtherDetails!}</textarea>
                                  </#if>
                                    <@spring.bind "interview.furtherDetails" /> 
	                			    <#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>
                                    </div>
                                </div>
                                
                             <div class="row">
                                <label class="label">Location (Link)<em>*</em></label>
                                        <div class="field">
                                        <#if assignOnly?? && assignOnly>
                                         <textarea id="interviewLocation" readonly="readonly" disabled="disabled" name="interviewLocation" class="max" rows="1" cols="80" maxlength='5000'>${interview.locationURL!}</textarea>
        							<#else>
        			     		  		 <textarea id="interviewLocation" name="interviewLocation" class="max" rows="1" cols="80" maxlength='5000'>${interview.locationURL!}</textarea>
                                       </#if>
                                             <@spring.bind "interview.locationURL" /> 
	                			  			 <#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>
                                        </div>
                             </div>
							
						<div class="buttons">
							<button type="button" id="moveToInterviewBtn">Save</button>
						</div>
						<input type="hidden" id="applicationId" name="applicationId" value="${applicationForm.id?string("######")}"/> 
					</div>
					<!-- #actions -->

					<#include "/private/common/feedback.ftl"/>
				</div>
				<!-- .content-box-inner -->
		</div>
		<!-- .content-box -->

		</article>

	</div>

	<!-- Footer. -->
	<div id="footer">
		<ul>
			<li><a href="#">Privacy</a></li>
			<li><a href="#">Terms &amp; conditions</a></li>
			<li><a href="#">Contact us</a></li>
			<li><a href="#">Glossary</a></li>
		</ul>
	</div>

	</div>

	<script type="text/javascript"
		src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
	<script type="text/javascript"
		src="<@spring.url '/design/default/js/libraries.js'/>"></script>
	<script type="text/javascript"
		src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
	<script type="text/javascript"
		src="<@spring.url '/design/default/js/interviewer/interview.js'/>"></script>
</body>
</html>
</section>
