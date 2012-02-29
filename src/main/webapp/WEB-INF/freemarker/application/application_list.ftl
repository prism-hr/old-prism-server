<!DOCTYPE HTML>
<#import "/spring.ftl" as spring />
<html>

	<head>
	
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<title>Shell template</title>

		<!-- Always force latest IE rendering engine (even in intranet) & Chrome Frame -->
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />

		<link type="text/css" rel="stylesheet" href="css/style.css" />
		<!--[if lt IE 9]>
		<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
		<![endif]-->
	
		<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/style.css' />"/>
		
		<script type='text/javascript' language="javascript" src="<@spring.url '/dwr/engine.js'/>"></script>
	    <script type='text/javascript' language="javascript" src="<@spring.url '/dwr/util.js'/>"></script>
	    <script type='text/javascript' language="javascript" src="<@spring.url '/dwr/interface/applicationDWR.js'/>"></script>
	    <script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
	    <script type="text/javascript" src="<@spring.url '/design/default/js/applicationList/formActions.js'/>"></script>
	    
	</head>
	
	<body>
		<!-- Wrapper Starts -->
		<div id="wrapper">

			<#include "/common/header.ftl"/>
			
			 <!-- Middle Starts -->
			<div id="middle">
			
				<#include "/common/nav_with_user_info.ftl"/>
				
				    <!-- Main content area. -->
				    <article id="content" role="main">
				      <#if !model.user.isInRole('APPLICANT')>
				      	<#include "/common/tools.ftl"/>
				      </#if>
				      
				      <!-- content box -->
				      <div class="content-box">
				        <div class="content-box-inner">
							<!-- confirmation message if application just submitted with email coming soon confirmation -->
							<p style="color:red;">${model.message}</p>
							<table class="data" border="0" >
					          	<colgroup>
					            	<col style="width: 30px" />
					            	<col style="width: 65px" />
					            	<col style="width: 120px" />
					            	<col style="width: 120px" />
					            	<col />
					            	<col style="width: 100px" />
					            	<col style="width: 160px" />					            
					            	<col style="width: 40px" />
					            </colgroup>
					          	<thead>
					              <tr>
					                <th scope="col">&nbsp;</th>
					                <th scope="col">App. No.</th>
					                <th scope="col">First Name</th>
					                <th scope="col">Surname</th>
					                <th scope="col">Programme</th>
					                <#if model.user.isInRole('APPLICANT')>
					            		<th scope="col">Status</th>
					            	</#if>
					               
					                <th scope="col">Actions</th>
					                <th class="centre" scope="col">Select</th>
					              </tr>
					            </thead>
					            <tbody>
					            	<#list model.applications as application>
							        	<tr id="row_${application.id}" name="applicationRow">
							                <td><a class="row-arrow" href="#">&gt;</a></td>
							                <td name="idColumn">${application.id}</td>
							                <td>${application.applicant.firstName}</td>
							                <td>${application.applicant.lastName}</td>
							                <td>${application.project.program.code} - ${application.project.program.title}</td>			
							                 <#if application.isDecided() >
							               	 <td name="statusColumn">${application.approvalStatus.displayValue()}</td>
							               	 <#else>
							               	  <td name="statusColumn">${application.submissionStatus.displayValue()}</td>
							              	</#if>
							                <td>
							                	<select class="actionType" name="app_[${application.id}]">
							                		<option>Select...</option>
							                		<option value="view">View</option>
							                	    <#if (model.user.isInRole('ADMINISTRATOR') || model.user.isInRole('REVIEWER')) && application.isReviewable()>
      													<option value="assignReviewer">Assign Reviewer</option>
        		  									</#if>
							                	    <#if model.user.isInRole('APPROVER') && application.isReviewable()>
							                	    	<option value="approve">Approve</option>
      												</#if>
									    			<#if (model.user.isInRole('APPROVER') || model.user.isInRole('ADMINISTRATOR'))  && application.isReviewable()>
									    				<option value="reject">Reject</option>
									      			</#if>
								      				<#if (((model.user.isInRole('APPROVER') || model.user.isInRole('ADMINISTRATOR') || 
								      											model.user.isInRole('REVIEWER'))) && application.isReviewable() )>
								    					<option value="comment">Comment</option>
								      				</#if>      												
								      				<#if (((model.user.isInRole('APPROVER') || model.user.isInRole('ADMINISTRATOR') || 
								      										model.user.isInRole('REVIEWER'))) && application.isReviewable() )>
								    					<option value="showComment">Show Comments</option>
								      				</#if>
								      											                	
							                  	</select>
							                </td>
							                <td class="centre"><input type="checkbox" name="select" disabled="disabled" /></td>
						              	</tr>
					              	</#list>
					            </tbody>
				          </table>
				
				          <p class="right">
				          	<a href="#" class="button">Download - coming soon</a>
				          </p>
				        </div><!-- .content-box-inner -->
				      </div><!-- .content-box -->
				      
				    </article>
				
				
				
			</div>
			<!-- Middle Ends -->
			
			<#include "/common/footer.ftl"/>
			
		</div>
		<!-- Wrapper Ends -->
		   
	</body>
</html>
