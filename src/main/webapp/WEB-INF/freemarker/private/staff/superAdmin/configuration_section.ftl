<#import "/spring.ftl" as spring />
<section class="form-rows">
	<h2>Configuration</h2>
	
	<div>
		<form id="configForm">

			<div class="alert alert-info">
				<i class="icon-info-sign"></i> Edit the system configuration. <strong>Be aware that this will change the system behaviour for all programmes.</strong>
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
					<label for="${stage}_duration" id="${stage.displayValue()}-lbl" class="plain-label">${stage.displayValue()} Stage Duration<em>*</em></label>
					<span class="hint" data-desc="<@spring.message 'configuration.validationDuration'/> ${stage.displayValue()} stage."></span>
					<div class="field">	
						<input type="hidden" id="stage" name="stage" value="${stage}" />
						<#if stageDurations[stage]?? && stageDurations[stage].duration??>  				
						<input class="numeric input-small" type="text" size="4" id="${stage}_duration" name="${stage}_duration" value="${stageDurations[stage].duration?string("######")}" />
						<#else>
						<input class="numeric" type="text" size="4" id="${stage}_duration" name="${stage}_duration"  />
						</#if>
						<select name="${stage}_unit" id="${stage}_unit" class="input-small">
							<option value="">Select...</option>
							<#list units as unit>
                                <option value="${unit}" <#if  stageDurations[stage]?? && stageDurations[stage].unit?? && stageDurations[stage].unit == unit> selected="selected"</#if>>${unit.displayValue()}</option>
							</#list>
						</select>
                        <div class="alert alert-error" id="${stage}_invalidDuration"  style="display:none;">
                               <i class="icon-warning-sign"></i> <span></span>	
                        </div>
                        <div class="alert alert-error" id="${stage}_invalidUnit"  style="display:none;">
                               <i class="icon-warning-sign"></i> <span></span>	
                        </div>

					</div>
				</div>
				</#list>
				<input type="hidden" name="stagesDuration" id= "stagesDuration" />

			</div><!-- .row-group -->

		<!-- Configure Reminder Interval -->
			
			<div class="row-group" id="section-reminders">
				<h3>Task Notifications</h3>

				<div class="row">
					<label for="reminderIntervalDuration" id="reminder-lbl" class="plain-label">Reminder Frequency<em>*</em></label>
					<span class="hint" data-desc="<@spring.message 'configuration.reminderFrequency'/>"></span>
					<div class="field">	
						<input type="hidden" name="reminderIntervalId" id="reminderIntervalId" value="1"/> 
						<input class="numeric input-small" type="text" size="4" id="reminderIntervalDuration" name="reminderIntervalDuration" value="${(reminderInterval.duration?string("######"))!}" />
						<select name="reminderUnit" id="reminderUnit" class="input-small">
							<option value="">Select...</option>
						<#list units as unit>
							<option value="${unit}" <#if  reminderInterval?? && reminderInterval.unit?? && reminderInterval.unit == unit> selected="selected" </#if>> ${unit.displayValue()}</option>               
						</#list>
						</select>
						<div class="alert alert-error" id="invalidDurationInterval"  style="display:none;">
                               <i class="icon-warning-sign"></i> <span></span>	
                        </div>
                        <div class="alert alert-error" id="invalidUnitInterval"  style="display:none;">
                               <i class="icon-warning-sign"></i> <span></span>	
                        </div>	
						<span class="invalid" name="invalidDurationInterval" style="display:none;"></span>
						<span class="invalid" name="invalidUnitInterval" style="display:none;"></span>
					</div>
				</div><!-- .row -->
				
			</div><!-- .row-group -->
			
		<!-- Add Registry Users -->
			<div class="row-group" id="section-registryusers">

				<div class="row">
					<h3 >Admissions Contacts</h3>


											<table id="registryUsers" class="table table-striped table-condensed table-bordered table-hover ">
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
				<!-- Entry form. -->
				<div class="row">
					<label for="reg-firstname" class="plain-label">First Name<em>*</em></label>
					<span class="hint" data-desc="<@spring.message 'configuration.firstName'/>"></span>
					<div class="field">	
						<input type="text" class="full" id="reg-firstname" autocomplete="off" name="regUserFirstname" />
					</div>
				</div><!-- .row -->
				
				<div class="row">
					<label for="reg-lastname" class="plain-label">Last Name<em>*</em></label>
					<span class="hint" data-desc="<@spring.message 'configuration.lastName'/>"></span>
					<div class="field">	
						<input type="text" class="full" id="reg-lastname" autocomplete="off" name="regUserLastname" />
					</div>
				</div><!-- .row -->
				
				<div class="row">
					<label for="reg-email" class="plain-label">Email Address<em>*</em></label>
					<span class="hint" data-desc="<@spring.message 'configuration.email'/>"></span>
					<div class="field">	
						<input type="email" class="full" id="reg-email" autocomplete="off" name="regUserEmail" />
					</div>
				</div><!-- .row -->

				<div class="row">
					<div class="field">	
						<button class="btn" type="button" id="registryUserAdd">Add</button>
					</div>
				</div><!-- .row -->
				<div id = "regContactData"></div>
			</div>
			
			<div class="buttons">						        		
					<button class="btn btn-primary" id="submitRUBtn" type="button" value="Submit">Submit</button>						        
			</div>
			
		</form>
	</div>
</section>
<script type="text/javascript" src="<@spring.url '/design/default/js/superAdmin/configuration_section.js' />"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/autosuggest.js'/>"></script>
<script type="text/javascript">
    $(document).ready(function() {
        autosuggest($("#reg-firstname"), $("#reg-lastname"), $("#reg-email"));
    });
</script>
 