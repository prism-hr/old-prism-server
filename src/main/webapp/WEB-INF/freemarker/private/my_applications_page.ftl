<!DOCTYPE HTML>
<#import "/spring.ftl" as spring />
<html>

	<head>
	
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<title>Shell template</title>

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
				      <#if !model.user.isInRole('APPLICANT')>
				      	<#include "/private/common/parts/tools.ftl"/>
				      </#if>
				      
				      <!-- content box -->
				      <input type="hidden" id="appList" name="appList" />
				      <div class="content-box">
				        <div class="content-box-inner">
							<!-- confirmation message if application just submitted with email coming soon confirmation -->
							<p style="color:red;">${model.message}</p>
							<table class="data" border="0" >
					          	<colgroup>
					            	<col style="width: 30px" />
					            </colgroup>
					          	<thead>
					              <tr>
					                <th scope="col">&nbsp;</th>
					                <th scope="col">App. No.</th>
					                <th scope="col">First Name</th>
					                <th scope="col">Surname</th>
					                <th scope="col">Programme</th>					
					            	<th scope="col" class="centre">Status</th>
					                <th scope="col">Actions</th>
					                <th class="centre" scope="col">Date Submitted</th>					                
					                <th class="centre" scope="col">Select</th>
					              </tr>
					            </thead>
					            <tbody>
					            	<#list model.applications as application>
							        	<tr id="row_${application.id?string("######")}" name="applicationRow">
							                <td><a class="row-arrow" href="#">&gt;</a></td>
							                <td name="idColumn">${application.id?string("######")}</td>
							                <td>${application.applicant.firstName}</td>
							                <td>${application.applicant.lastName}</td>
							                <td>${application.program.code} - ${application.program.title}</td>								                
							               	<td id="statusColumn" name="statusColumn">${application.status.displayValue()}</td>
							                <td>
							                	<select class="actionType" name="app_[${application.id?string("######")}]">
							                		<option>Select...</option>
							                		<option value="view">View</option>
							                		<option value="print">Print</option>
							                	    <#if (model.user.isInRole('ADMINISTRATOR') || model.user.isInRole('REVIEWER')) && application.isModifiable()>
      													<option value="assignReviewer">Assign Reviewer</option>
        		  									</#if>
							                	    <#if model.user.isInRole('APPROVER') && application.isModifiable()>
							                	    	<option value="approve">Approve</option>
      												</#if>
									    			<#if (model.user.isInRole('APPROVER') || model.user.isInRole('ADMINISTRATOR'))  && application.isModifiable()>
									    				<option value="reject">Reject</option>
									      			</#if>
								      				<#if (((model.user.isInRole('APPROVER') || model.user.isInRole('ADMINISTRATOR') || 
								      											model.user.isInRole('REVIEWER'))) && application.isModifiable() )>
								    					<option value="comment">Comments</option>
								      				</#if>      												
								      				<#if (model.user.isRefereeOfApplicationForm(application) && application.isSubmitted() && !application.isDecided() )>
								    					<option value="reference">Add Reference</option>
								      				</#if>      												
								      				<#if (model.user.isInRole('APPLICANT') && application.isSubmitted() && !application.isDecided() && !application.isWithdrawn())>
								    					<option value="withdraw">Withdraw</option>
								      				</#if>      												
							                  	</select>
							                </td>
							                <td> <#if application.isSubmitted()>
							                	${(application.submittedDate?string("dd-MMM-yyyy hh:mm a"))!} 
							               		 </#if>
							               	</td>							  
							                <td class="centre"><input type="checkbox" name="appDownload" id="appDownload_${application.id?string("######")}"/></td>
						              	</tr>
					              	</#list>
					            </tbody>
				          </table>
				          
				          <p class="right">
				            <#if (model.user.isInRole('SUPERADMINISTRATOR') || model.user.isInRole('ADMINISTRATOR'))>
                                <a id="manageUsersButton" class="button">Manage Users</a>
                            </#if>
                            <#if (model.hasApplications())>
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
