<#import "/spring.ftl" as spring />
<section class="form-rows">
	<h2>Configuration</h2>
	
	<div>
		<form id="configForm">

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
						<#if stageDurations[stage]?? && stageDurations[stage].duration??>  				
						<input class="numeric" type="text" size="4" id="${stage}_duration" name="${stage}_duration" value="${stageDurations[stage].duration?string("######")}" />
						<#else>
						<input class="numeric" type="text" size="4" id="${stage}_duration" name="${stage}_duration"  />
						</#if>
						<select name="${stage}_unit" id="${stage}_unit">
							<option value="">Select...</option>
							<#list units as unit>
							<option value="${unit}"
							<#if  stageDurations[stage]?? && stageDurations[stage].unit?? && stageDurations[stage].unit == unit>
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

		<!-- Configure Reminder Interval -->
			
			<div class="row-group" id="section-reminders">
				<h3>Task Notifications</h3>

				<div class="row">
					<span id="reminder-lbl" class="plain-label">Reminder Frequency<em>*</em></span>
					<span class="hint" data-desc="<@spring.message 'configuration.reminderFrequency'/>"></span>
					<div class="field">	
						<input type="hidden" name="reminderIntervalId" id="reminderIntervalId" value="1"/> 
						<input class="numeric" type="text" size="4" id="reminderIntervalDuration" name="reminderIntervalDuration" value="${(reminderInterval.duration?string("######"))!}" />
						<select name="reminderUnit" id="reminderUnit">
							<option value="">Select...</option>
						<#list units as unit>
							<option value="${unit}"
							<#if  reminderInterval?? && reminderInterval.unit?? && reminderInterval.unit == unit>
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
			
		<!-- Add Registry Users -->
			<div class="row-group" id="section-registryusers">

				<div class="row">
					<span class="label"><b>Admissions Contacts</b></span>
					<div class="field">
						<table id="registryUsers">
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
				<div id = "regContactData"></div>
			</div>
			
			<div class="buttons">						        		
					<button class="blue" id="submitRUBtn" type="button" value="Submit">Submit</button>						        
			</div>
			
		</form>
	</div>
</section>
<script type="text/javascript" src="<@spring.url '/design/default/js/superAdmin/configuration_section.js' />"></script> 