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
	    <script type='text/javascript' language="javascript" src="<@spring.url '/dwr/interface/acceptDWR.js'/>"></script>
	    
	    <script type="text/javascript" src="<@spring.url '/design/default/js/application/formActions.js'/>"></script>
	    
	</head>
	
	<body>
		<!-- Wrapper Starts -->
		<div id="wrapper">

			<#include "/application/app_form_header.ftl"/>
			
			 <!-- Middle Starts -->
			<div id="middle">
			
				<#include "/application/app_form_mid_header_nav.ftl"/>
				
				    <!-- Main content area. -->
				    <article id="content" role="main">
				      
				      <!-- content box -->
				      <div class="content-box">
				        <div class="content-box-inner">
				
							<table class="data" border="0">
					          	<colgroup>
					            	<col width="30" />
					            	<col width="90" />
					            	<col width="120" />
					            	<col width="120" />
					            	<col width="*" />
					            	<col width="180" />
					            	<col width="40" />
					            </colgroup>
					          	<thead>
					              <tr>
					                <th scope="col">&nbsp;</th>
					                <th scope="col">App. No.</th>
					                <th scope="col">First Name</th>
					                <th scope="col">Surname</th>
					                <th scope="col">Programme</th>
					                <th scope="col">Actions</th>
					                <th class="centre" scope="col">Select</th>
					              </tr>
					            </thead>
					            <tbody>
					            	<#list model.applications as application>
							        	<tr>
							                <td><a class="row-arrow" href="#">&gt;</a></td>
							                <td id="row_app_id">${application.id}</td>
							                <td>${application.user.firstName}</td>
							                <td>${application.user.lastName}</td>
							                <td>${application.project.program.code} - ${application.project.program.title}</td>
							                <td>
							                	<select class="actionType" name="app_[${application.id}]" onchange="takeAction(this);">
							                		<option>View</option>
							                	    <#if model.user.isInRole('ADMINISTRATOR') || model.user.isInRole('REVIEWER')>
      													<option>AssignReviewer</option>
        		  									</#if>
							                	    <#if model.user.isInRole('APPROVER')>
							                	    	<option>Approve</option>
      												</#if>
									    			<#if ((model.user.isInRole('APPROVER') || model.user.isInRole('ADMINISTRATOR')) )>
									    				<option>Reject</option>
									      			</#if>
								      				<#if (((model.user.isInRole('APPROVER') || model.user.isInRole('ADMINISTRATOR') || 
								      											model.user.isInRole('REVIEWER'))) && application.isActive() )>
								    					<option>Comment</option>
								      				</#if>      												
								      				<#if (((model.user.isInRole('APPROVER') || model.user.isInRole('ADMINISTRATOR') || 
								      										model.user.isInRole('REVIEWER'))) && application.isActive() )>
								    					<option>Show Comments</option>
								      				</#if>
								      											                	
							                  	</select>
							                </td>
							                <td class="centre"><input type="checkbox" name="select" /></td>
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
			
			<#include "/application/app_form_footer.ftl"/>
			
		</div>
		<!-- Wrapper Ends -->
		   
	</body>
</html>
