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
		<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/application.css' />"/>
		<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/staff/state_transition.css' />"/>
				<!-- Styles for Application List Page -->

		<!--[if lt IE 9]>
		<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
		<![endif]-->
	
	    <script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
	    <script type="text/javascript" src="<@spring.url '/design/default/js/libraries.js' />"></script>
	    <script type="text/javascript" src="<@spring.url '/design/default/js/script.js' />"></script>
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
				<@header activeTab="config"/>
					<!-- Main content area. -->
					<article id="content" role="main">		    
						
						<!-- content box -->				      
						<div class="content-box">
							<div class="content-box-inner">
							
								<div class="section-info-bar">
									Edit the system configuration. <strong>Be aware that this will change the system behaviour for all programmes.</strong>
								</div>

								<section id="section-slc" class="form-rows">
									<div>
										<form>
										
											<div class="row-group">
												<select id="stages" style="display: none;">
													<#list stages as stage>
													<option value="${stage}"></option>
													</#list>
												</select>
							
												<div class="row">
													<span id="reminder-lbl" class="plain-label">Service Level Commitments</span>
													<span class="hint" data-desc="<@spring.message 'configuration.reminderFrequency'/>"></span>
												</div><!-- .row -->
							
												<#list stages as stage>
												
												<div class="row"> 
													<span id="${stage.displayValue()}-lbl" class="plain-label">${stage.displayValue()} Stage Duration<em>*</em></span>
													<span class="hint" data-desc="<@spring.message 'configuration.validationDuration'/> ${stage.displayValue()} stage."></span>
													<div class="field">	
														<input type="hidden" id="stage" name="stage" value="${stage}" />
														<#if durationDAO.getByStatus(stage)?? && durationDAO.getByStatus(stage).duration??>  				
														<input type="text" size="4" id="${stage}_duration" name="${stage}_duration" value="${durationDAO.getByStatus(stage).duration?string("######")}" />
														<#else>
														<input type="text" size="4" id="${stage}_duration" name="${stage}_duration"  />
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
														<span class="invalid" name="${stage}_invalidDuration" style="display:none;"></span>
														<span class="invalid" name="${stage}_invalidUnit" style="display:none;"></span>
													</div>
												</div>
												</#list>
												<input type="hidden" name="stagesDuration" id= "stagesDuration" />
											</div><!-- .row-group -->
										
											<div class="buttons">						        		
<#--										<button type="button" id="cancelDurationBtn" value="cancel" class="clear">Clear</button> -->
												<button class="blue" id="submitDurationStages" type="button" value="Submit">Submit</button>						        
											</div>
											
										</form>
									</div>

									<!-- Configure Reminder Interval -->
									<div>
										<form>
											<div class="row-group">

												<div class="row">
													<span id="reminder-lbl" class="plain-label">Task Notifications</span>
													<span class="hint" data-desc="<@spring.message 'configuration.reminderFrequency'/>"></span>
												</div><!-- .row -->

												<div class="row">
													<span id="reminder-lbl" class="plain-label">Reminder Frequency<em>*</em></span>
													<span class="hint" data-desc="<@spring.message 'configuration.reminderFrequency'/>"></span>
													<div class="field">	
														<input type="hidden" name="reminderIntervalId" id="reminderIntervalId" value="1"/> 
														<input type="text" size="4" id="reminderIntervalDuration" name="reminderIntervalDuration" value="${(intervalDAO.getReminderInterval().duration?string("######"))!}" />
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
														<span class="invalid" name="invalidDurationInterval" style="display:none;"></span>
														<span class="invalid" name="invalidUnitInterval" style="display:none;"></span>
													</div>
												</div><!-- .row -->
												
											</div><!-- .row-group -->
											
											<div class="buttons">						        		
<#--										<button type="button" id="cancelReminderBtn" value="cancel" class="clear">Clear</button> -->
												<button class="blue" id="submitRIBtn" type="button" value="Submit">Submit</button>						        
											</div>
											
										</form>
									</div>

									<!-- Add Registry Users -->
									<div>
										<form id="addRegistryForm">
											<span class="invalid" name="threeMaxMessage"></span>

											<!-- First registry user -->
											<div class="row-group" id="firstRegistryUser">
											
												<div class="row">
													<span id="reminder-lbl" class="plain-label">Admissions Contacts</span>
													<span class="hint" data-desc="<@spring.message 'configuration.reminderFrequency'/>"></span>
												</div><!-- .row -->
											
												<input type="hidden" name="1_regUserId" id= "1_regUserId" value="<#if allRegistryUsers[0]?? && allRegistryUsers[0].id??>${encrypter.encrypt(allRegistryUsers[0].id)}</#if>" />
												
												<div class="row"> 
													<span id="ru-firstname-lbl" class="plain-label">Fist Name<em>*</em></span>
													<span class="hint" data-desc="<@spring.message 'configuration.firstName'/>"></span>
													<div class="field">	
														<input type="text" class="full" id="1_regUserfirstname" name="regUserFirstname" value="${(allRegistryUsers[0].firstname)!}" />
													</div>
												</div><!-- .row -->
												
												<div class="row"> 
													<span id="ru-lastname-lbl" class="plain-label">Last Name<em>*</em></span>
													<span class="hint" data-desc="<@spring.message 'configuration.lastName'/>"></span>
													<div class="field">	
														<input type="text" class="full" id="1_regUserLastname" name="regUserLastname" value="${(allRegistryUsers[0].lastname)!}" />
													</div>
												</div><!-- .row -->
												
												<div class="row"> 
													<span id="ru-email-lbl" class="plain-label">Email<em>*</em></span>
													<span class="hint" data-desc="<@spring.message 'configuration.email'/>"></span>
													<div class="field">	
														<input type="text" class="full" id="1_regUserEmail" name="regUserEmail" value="${(allRegistryUsers[0].email)!}"/>
														<span class="invalid" name="firstuserInvalid" style="display:none;"></span>
													</div>
												</div><!-- .row -->
												
												<!-- Second registry user -->
												<input type="hidden" name="2_regUserId" id= "2_regUserId" value="<#if allRegistryUsers[1]?? && allRegistryUsers[1].id??>${encrypter.encrypt(allRegistryUsers[1].id)}</#if>"/>

												<div class="row"> 
													<span id="ru-firstname-lbl" class="plain-label">Fist Name<em>*</em></span>
													<span class="hint" data-desc="<@spring.message 'configuration.firstName'/>"></span>
													<div class="field">	
														<input type="text" class="full" id="2_regUserfirstname" name="regUserFirstname" value="${(allRegistryUsers[1].firstname)!}"/>
													</div>
												</div><!-- .row -->
									
												<div class="row"> 
													<span id="ru-lastname-lbl" class="plain-label">Last Name<em>*</em></span>
													<span class="hint" data-desc="<@spring.message 'configuration.lastName'/>"></span>
													<div class="field">	
														<input type="text" class="full" id="2_regUserLastname" name="regUserLastname" value="${(allRegistryUsers[1].lastname)!}"/>
													</div>
												</div><!-- .row -->
								
												<div class="row"> 
													<span id="ru-email-lbl" class="plain-label">Email<em>*</em></span>
													<span class="hint" data-desc="<@spring.message 'configuration.email'/>"></span>
													<div class="field">	
														<input type="text" class="full" id="2_regUserEmail" name="regUserEmail" value="${(allRegistryUsers[1].email)!}"/>
														<span class="invalid" name="seconduserInvalid" style="display:none;"></span>
													</div>
												</div><!-- .row -->
												
												<!-- Third registry user -->
												<input type="hidden" name="3_regUserId" id= "3_regUserId" value="<#if allRegistryUsers[2]?? && allRegistryUsers[2].id??>${encrypter.encrypt(allRegistryUsers[2].id)}</#if>"/>

												<div class="row"> 
													<span id="ru-firstname-lbl" class="plain-label">Fist Name<em>*</em></span>
													<span class="hint" data-desc="<@spring.message 'configuration.firstName'/>"></span>
													<div class="field">	
														<input type="text" class="full" id="3_regUserfirstname" name="regUserFirstname" value="${(allRegistryUsers[2].firstname)!}"/>
													</div>
												</div><!-- .row -->
											
												<div class="row"> 
													<span id="ru-lastname-lbl" class="plain-label">Last Name<em>*</em></span>
													<span class="hint" data-desc="<@spring.message 'configuration.lastName'/>"></span>
													<div class="field">	
														<input type="text" class="full" id="3_regUserLastname" name="regUserLastname" value="${(allRegistryUsers[2].lastname)!}"/>
													</div>
												</div><!-- .row -->
											
												<div class="row"> 
													<span id="ru-email-lbl" class="plain-label">Email<em>*</em></span>
													<span class="hint" data-desc="<@spring.message 'configuration.email'/>"></span>
													<div class="field">	
														<input type="text" class="full" id="3_regUserEmail" name="regUserEmail" value="${(allRegistryUsers[2].email)!}"/>
														<span class="invalid" name="thirduserInvalid" style="display:none;"></span>
													</div>
												</div><!-- .row -->

											</div><!-- .row-group -->
									
											<input type="hidden" name="registryUsers" id= "registryUsers" />
											
											<div class="buttons">						        		
<#--										<button type="button" id="cancelRegistryBtn" value="cancel" class="clear">Clear</button> -->
												<button class="blue" id="submitRUBtn" type="button" value="Submit">Submit</button>						        
											</div>
									
										</form>
									</div>
										
								</section>
							
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