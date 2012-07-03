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
							

								<section class="form-rows">
									<h2>Configuration</h2>
									
									<div>
										<form>

											<div class="section-info-bar">
												Edit the system configuration. <strong>Be aware that this will change the system behaviour for all programmes.</strong>
											</div>
										
											<div class="row-group" id="section-stages">
												<h3>Service Level Commitments</h3>
												
												<select id="stages" style="display: none;">
													<#list stages as stage>
													<option value="${stage}"></option>
													</#list>
												</select>
							
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

												<div class="buttons">						        		
	<#--										<button type="button" id="cancelDurationBtn" value="cancel" class="clear">Clear</button> -->
													<button class="blue" id="submitDurationStages" type="button" value="Submit">Submit</button>						        
												</div>
											</div><!-- .row-group -->
										
										</form>

										<!-- Configure Reminder Interval -->
										<form>
											<div class="row-group" id="section-reminders">
												<h3>Task Notifications</h3>

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
												
												<div class="buttons">						        		
	<#--										<button type="button" id="cancelReminderBtn" value="cancel" class="clear">Clear</button> -->
													<button class="blue" id="submitRIBtn" type="button" value="Submit">Submit</button>						        
												</div>
											
											</div><!-- .row-group -->
											
										</form>
										

										<!-- Add Registry Users -->
										<form id="registryUsersForm" id="section-users">
											<div class="row-group">
												<h3>Admissions Contacts</h3>

												<div class="row">
													<div class="field">
														<table id="registryUsers">
															<thead>
																<tr>
																	<th>Name</th>
																</tr>
															</thead>
															<tbody>
																<tr>
																	<td>
																		<div class="scroll">
																			<table>
																				<colgroup>
																					<col />
																					<col style="width: 30px;" />
																				</colgroup>
																				<tbody>
																				<#list allRegistryUsers! as regUser>
																					<tr>
																						<td>
																							${regUser.firstname?html} ${regUser.lastname?html} (${regUser.email?html})
																						</td>
																						<td>
																							<button class="button-delete" type="button" data-desc="Remove">Remove</button>
																							<input type="hidden" name="firstname" value="${regUser.firstname!}" />
																							<input type="hidden" name="lastname" value="${regUser.lastname!}" />
																							<input type="hidden" name="email" value="${regUser.email!}" />
																							<input type="hidden" name="id" value="<#if regUser.id??>${encrypter.encrypt(regUser.id)}</#if>" />
																						</td>
																					</tr>
																				</#list>
																				</tbody>
																			</table>
																		</div>
																	</td>
																</tr>
															</tbody>
														</table>
													</div>
												</div>

												<!-- Entry form. -->
												<div class="row">
													<span class="plain-label">First Name<em>*</em></span>
													<span class="hint" data-desc="<@spring.message 'configuration.firstName'/>"></span>
													<div class="field">	
														<input type="text" class="full" id="reg-firstname" name="regUserFirstname" />
													</div>
												</div><!-- .row -->
												
												<div class="row">
													<span class="plain-label">Last Name<em>*</em></span>
													<span class="hint" data-desc="<@spring.message 'configuration.lastName'/>"></span>
													<div class="field">	
														<input type="text" class="full" id="reg-lastname" name="regUserLastname" />
													</div>
												</div><!-- .row -->
												
												<div class="row">
													<span class="plain-label">Email Address<em>*</em></span>
													<span class="hint" data-desc="<@spring.message 'configuration.email'/>"></span>
													<div class="field">	
														<input type="text" class="full" id="reg-email" name="regUserEmail" />
													</div>
												</div><!-- .row -->

												<div class="row">
													<div class="field">	
														<button class="blue" type="button" id="registryUserAdd">Add</button>
													</div>
												</div><!-- .row -->

											
												<div class="buttons">						        		
													<button class="blue" id="submitRUBtn" type="button" value="Submit">Submit</button>						        
												</div>

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