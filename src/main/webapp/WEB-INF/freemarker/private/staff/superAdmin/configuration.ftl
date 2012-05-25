<section id="assignstagessection" >					          	
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
		<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/staff/state_transition.css' />"/>
				<!-- Styles for Application List Page -->

		<!--[if lt IE 9]>
		<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
		<![endif]-->
	
	    <script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
	    <script type="text/javascript" src="<@spring.url '/design/default/js/superAdmin/configuration.js' />"></script> 
	    
	    
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
				      <div class="content-box">
				        <div class="content-box-inner">
							    <h1> Configure Application form Stages duration</h1>
							    <br/>
							     <form>
							     
							     <select id="stages" style="visibility:hidden;">
									<#list stages as stage>
							  			<option value="${stage}"></option>
									</#list>
								</select>
								
							     <#list stages as stage>
	   								<div class="row"> 
			           					<span id="${stage.displayValue()}-lbl" class="plain-label">${stage.displayValue()} Duration</span>
			            				<div class="field">	
			            					<input type="hidden" id="stage" name="stage" value="${stage}" />
			            					 <#if durationDAO.getByStatus(stage)?? && durationDAO.getByStatus(stage).duration??>  				
			            						<input type = "text" id="${stage}_duration" name="${stage}_duration" value="${durationDAO.getByStatus(stage).duration}" />
			            					<#else>
			            						<input type = "text" id="${stage}_duration" name="${stage}_duration"  />
			            					</#if>
		            							<select name="${stage}_unit" id="${stage}_unit">
													<option value="">Select...</option>
														<#list units as unit>
												 		 	<option value="${unit}"
												 		 	<#if  durationDAO.getByStatus(stage)?? && durationDAO.getByStatus(stage).unit?? && durationDAO.getByStatus(stage).unit == unit>
																selected="selected"
															</#if>>
															${unit.displayValue()}</option>               
														</#list>
												 </select>	
		            						</div>
		            					<span class="invalid" name="${stage}_invalidDuration" style="display:none;"></span>
		            					<span class="invalid" name="${stage}_invalidUnit" style="display:none;"></span>
									</div>
									</#list>
									<input type="hidden" name="stagesDuration" id= "stagesDuration" />
									
			            			<div class="buttons">						        		
			            				<button type="button" id="cancelDurationBtn" value="cancel">Cancel</button>
							       		<button class="blue" id="submitDurationStages" type="button" value="Submit">Submit</button>						        
									</div>
									</form>
			  					<hr/>
			  					
			  					<!-- Configure Reminder Interval -->
			  					
			  					 <h1> Configure Email Reminders Interval</h1>
							    <br/>
							     <form>
							     
	   								<div class="row"> 
			           					<span id="reminder-lbl" class="plain-label">Reminder Interval Duration</span>
			            				<div class="field">	
			            						<input type="hidden" name="reminderIntervalId" id="reminderIntervalId" value="1"/> 
			            						<input type = "text" id="reminderIntervalDuration" name="reminderIntervalDuration" value="${(intervalDAO.getReminderInterval().duration)!}" />
		            							<select name="reminderUnit" id="reminderUnit">
													<option value="">Select...</option>
														<#list units as unit>
												 		 	<option value="${unit}"
												 		 	<#if  intervalDAO.getReminderInterval()?? && intervalDAO.getReminderInterval().unit?? && intervalDAO.getReminderInterval().unit == unit>
																selected="selected"
															</#if>>
															${unit.displayValue()}</option>               
														</#list>
												 </select>	
		            						</div>
		            					<span class="invalid" name="invalidDurationInterval" style="display:none;"></span>
		            					<span class="invalid" name="invalidUnitInterval" style="display:none;"></span>
									</div>
									
			            			<div class="buttons">						        		
			            				<button type="button" id="cancelReminderBtn" value="cancel">Cancel</button>
							       		<button class="blue" id="submitRIBtn" type="button" value="Submit">Submit</button>						        
									</div>
									</form>
			  					<hr/>
			  					
		  					
			  					<!-- Add Registry Users -->
			  					
			  					 <h1> Add Registry Users</h1>
							    <br/>
							    <span name="threeMaxMessage"> </span>
							     <form id="addRegistryForm">
							     
							     <span id="firstRegistryUser">
							     <input type="hidden" name="1_regUserId" id= "1_regUserId" value="${(allRegistryUsers[0].id)!}" />
	   								<div class="row"> 
			           					<span id="ru-firstname-lbl" class="plain-label">Fist Name</span>
			            				<div class="field">	
			            						<input type = "text" id="1_regUserfirstname" name="regUserFirstname" value="${(allRegistryUsers[0].firstname)!}" />
		            					</div>
									</div>
									
	   								<div class="row"> 
			           					<span id="ru-lastname-lbl" class="plain-label">Last Name</span>
			            				<div class="field">	
			            						<input type = "text" id="1_regUserLastname" name="regUserLastname" value="${(allRegistryUsers[0].lastname)!}" />
		            					</div>
									</div>
									
	   								<div class="row"> 
			           					<span id="ru-email-lbl" class="plain-label">Email</span>
			            				<div class="field">	
			            						<input type = "text" id="1_regUserEmail" name="regUserEmail" value="${(allRegistryUsers[0].email)!}"/>
		            					</div>
									</div>
									</span>
									<span class="invalid" name="firstuserInvalid" style="display:none;"></span>
									
									<span id="secondRegistryUser" >
										<input type="hidden" name="2_regUserId" id= "2_regUserId" value="${(allRegistryUsers[1].id)!}"/>
	   									<div class="row"> 
			           						<span id="ru-firstname-lbl" class="plain-label">Fist Name</span>
			            					<div class="field">	
			            							<input type = "text" id="2_regUserfirstname" name="regUserFirstname" value="${(allRegistryUsers[1].firstname)!}"/>
		            						</div>
										</div>
									
	   									<div class="row"> 
			           						<span id="ru-lastname-lbl" class="plain-label">Last Name</span>
			            					<div class="field">	
			            							<input type = "text" id="2_regUserLastname" name="regUserLastname" value="${(allRegistryUsers[1].lastname)!}"/>
		            						</div>
										</div>
									
	   									<div class="row"> 
			           						<span id="ru-email-lbl" class="plain-label">Email</span>
			            					<div class="field">	
			            							<input type = "text" id="2_regUserEmail" name="regUserEmail" value="${(allRegistryUsers[1].email)!}"/>
		            						</div>
										</div>
									</span>
										<span class="invalid" name="seconduserInvalid" style="display:none;"></span>
									
	   								<span id="thirdRegistryUser">
	   								<input type="hidden" name="3_regUserId" id= "3_regUserId" value="${(allRegistryUsers[2].id)!}"/>
	   									<div class="row"> 
			           						<span id="ru-firstname-lbl" class="plain-label">Fist Name</span>
			            					<div class="field">	
			            							<input type = "text" id="3_regUserfirstname" name="regUserFirstname" value="${(allRegistryUsers[2].firstname)!}"/>
		            						</div>
										</div>
									
	   									<div class="row"> 
			           						<span id="ru-lastname-lbl" class="plain-label">Last Name</span>
			            					<div class="field">	
			            							<input type = "text" id="3_regUserLastname" name="regUserLastname" value="${(allRegistryUsers[2].lastname)!}"/>
		            						</div>
										</div>
									
	   									<div class="row"> 
			           						<span id="ru-email-lbl" class="plain-label">Email</span>
			            					<div class="field">	
			            							<input type = "text" id="3_regUserEmail" name="regUserEmail" value="${(allRegistryUsers[2].email)!}"/>
		            						</div>
										</div>
									</span>
									<span class="invalid" name="thirduserInvalid" style="display:none;"></span>
									
									<input type="hidden" name="registryUsers" id= "registryUsers" />
									
			            			<div class="buttons">						        		
			            				<button type="button" id="addAnother">Add Another</button>
			            				<button type="button" id="cancelRegistryBtn" value="cancel">Cancel</button>
							       		<button class="blue" id="submitRUBtn" type="button" value="Submit">Submit</button>						        
									</div>
									</form>
			  					<hr/>
			  					
		  					
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
</section>