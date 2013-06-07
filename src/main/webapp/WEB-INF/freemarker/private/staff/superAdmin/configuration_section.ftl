<#import "/spring.ftl" as spring />
<section class="form-rows">
	<h2>Manage Service Levels</h2>
	
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
 