<!DOCTYPE HTML>
<#import "/spring.ftl" as spring />
<html>

	<head>
	
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<title>UCL Postgraduate Admissions</title>

		<!-- Always force latest IE rendering engine (even in intranet) & Chrome Frame -->
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
		
		<!-- Styles for Application List Page -->
		<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/global_private.css' />"/>
		<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/application_list.css' />"/>
		<!-- Styles for Application List Page -->

		<!--[if lt IE 9]>
		<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
		<![endif]-->
	
	    <script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>	    
	    <script type="text/javascript" src="<@spring.url '/design/default/js/applicationList/formActions.js'/>"></script>
	    <script type="text/javascript" src="<@spring.url '/design/default/js/script.js' />"></script>
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
			
				<#include "/private/common/parts/nav_with_user_info.ftl"/>
				
				    <!-- Main content area. -->
				    <article id="content" role="main">
				      
				      <!-- content box -->
				      <input type="hidden" id="appList" name="appList" />
				      <div class="content-box">
				        <div class="content-box-inner">
							
							<p style="color:red;">${(message?html)!}</p>
							<table class="data" border="0" >
					          	<colgroup>
					            	<col style="width: 30px" />
					            	<col style="" />
					            	<col/>
					            	<col/>
					            	<col/>
					            	<col style="" />
					            	<col style="" />
					            	<col style="width: 40px" />
					            </colgroup>
					          	<thead>
					              <tr>
					                <th scope="col">&nbsp;</th>
					                <th scope="col"></th>
					                <th scope="col">Name</th>
					                <th scope="col">Programme</th>					
					            	<th scope="col">Status</th>
					                <th scope="col">Actions</th>
					                <th scope="col">Date</th>					                
					                <th class="centre" scope="col">
					                	<input type="checkbox" name="select-all" id="select-all" />
					                </th>
					              </tr>
					            </thead>
					            <tbody>
					            	<#list applications as application>
							        	<tr id="row_${application.applicationNumber}" name="applicationRow">
							                <td><a class="row-arrow" href="#">&gt;</a></td>
							                <td name="idColumn">${application.applicationNumber}</td>
							                <td class="applicant-name">${application.applicant.firstName} ${application.applicant.lastName}</td>
							                <td>${application.program.code} - ${application.program.title}</td>								                
							               	<td id="statusColumn" name="statusColumn">${application.status.displayValue()}</td>
							                <td class="centre">
							                	<select class="actionType" name="app_[${application.applicationNumber}]">
							                		<option>Select...</option>
							                		<option value="view">View</option>
							                		<option value="print">Print</option>
							                	    <#if user.isInRoleInProgram('APPROVER', application.program) && application.isInState('APPROVAL')>
							                	    	<option value="approve">Approve</option>
							                	    	<option value="reject">Reject</option>
      												</#if>
      												<#if  user.hasAdminRightsOnApplication(application) && application.isInState('VALIDATION')> 
									    				<option value="validate">Validate</option>
									      			</#if>
									      			<#if user.hasAdminRightsOnApplication(application) && application.isInState('REVIEW')> 
									    				<option value="validate">Evaluate reviews</option>
									      			</#if>
									      			<#if user.hasAdminRightsOnApplication(application) && application.isInState('INTERVIEW')> 
									    				<option value="validate">Evaluate interview feedback</option>
									      			</#if>
									    			<#if !user.isInRole('APPLICANT') && !user.isRefereeOfApplicationForm(application)>
								    					<option value="comment">Comment</option>								    				
								      				</#if>      												
							                	    <#if (user.isReviewerInLatestReviewRoundOfApplicationForm(application) && user.hasRespondedToProvideReviewForApplication(application))>
      													<option value="assignReviewer">Assign Reviewer</option>
        		  									</#if>							                	   
									    			<#if user.isReviewerInLatestReviewRoundOfApplicationForm(application) && application.isInState('REVIEW') && !user.hasRespondedToProvideReviewForApplication(application)> 
								    					<option value="review">Add Review</option>								    				
								      				</#if>      												
									    			<#if user.isInterviewerOfApplicationForm(application) && application.isInState('INTERVIEW') && !user.hasRespondedToProvideInterviewFeedbackForApplication(application)> 
								    					<option value="interviewFeedback">Add Interview Feedback</option>								    				
								      				</#if>      												
								      				<#if (user.isRefereeOfApplicationForm(application) && application.isSubmitted() && !application.isDecided() )>
								    					<option value="reference">Add Reference</option>
								      				</#if>      												
								      				<#if (user.isInRole('APPLICANT') && application.isSubmitted() && !application.isDecided() && !application.isWithdrawn())>
								    					<option value="withdraw">Withdraw</option>
								      				</#if>      												
							                  	</select>
							                </td>
							                <td class="centre">${(application.submittedDate?string("dd MMM yyyy"))!}</td>							  
							                <td class="centre"><input type="checkbox" name="appDownload" id="appDownload_${application.applicationNumber}"/></td>
						              	</tr>
					              	</#list>
					            </tbody>
				          </table>
				          
				          <p class="right">
				            <#if (user.isInRole('SUPERADMINISTRATOR') || user.isInRole('ADMINISTRATOR'))>
                                <a id="manageUsersButton" class="button">Manage Users</a>
                            </#if>
				            <#if (user.isInRole('SUPERADMINISTRATOR'))>
                                <a id="configuration" class="button">Configuration</a>
                            </#if>
                            <#if (applications?size > 0)>
				          		<a class="button" name="downloadAll" id="downloadAll">Download</a>
				          	</#if>
				          	<#include "/private/common/feedback.ftl"/>
				          </p>
                    	  
				        </div><!-- .content-box-inner -->
				      </div><!-- .content-box -->
				      
				    </article>
				
				
				
			</div>
			<!-- Middle Ends -->
			
			<#include "/private/common/global_footer.ftl"/>
			
		</div>
		<!-- Wrapper Ends -->
		   
	</body>
</html>
