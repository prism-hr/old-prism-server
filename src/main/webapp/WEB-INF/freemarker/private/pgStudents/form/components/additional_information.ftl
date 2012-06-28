<#assign errorCode = RequestParameters.errorCode! />

<#import "/spring.ftl" as spring />

<a name="additional-info"></a>
<h2 id="additional-H2" class="empty open">
	<span class="left"></span><span class="right"></span><span class="status"></span>
	Additional Information<em>*</em>
</h2>

<div>
	<form>
	
		<#if errorCode?? && errorCode=="true">
		<div class="section-error-bar">
			<span class="error-hint" data-desc="Please provide all mandatory fields in this section."></span>             	
			<span class="invalid-info-text">
				<@spring.message 'additionalInformation.sectionInfo'/>
			</span>
		</div>
		<#else>
		<div id="add-info-bar-div" class="section-info-bar">
			<@spring.message 'additionalInformation.sectionInfo'/> 
		</div>	
		</#if>
	
		<!-- Free text field for info. -->
		<div class="row-group">
			<div class="row">
				<span class="plain-label">Additional information relevant to your application</span>
				<span class="hint" data-desc="<@spring.message 'additionalInformation.infotext'/>"></span>
				<div class="field">
					<#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()>
					<textarea id="informationText" name="informationText" class="max" rows="6" cols="80" >${(additionalInformation.informationText?html)!}</textarea>
					<#else>
					<textarea readonly="readonly" id="informationText" name="informationText" class="max" rows="10" cols=80">${(additionalInformation.informationText?html)!}</textarea>
					</#if>
				</div>
			</div>
			<@spring.bind "additionalInformation.informationText" /> 
			<#list spring.status.errorMessages as error>
			<div class="row">
				<div class="field">
					<span class="invalid">${error}</span>
				</div>
			</div>
			</#list>
		</div>
	
		<div class="row-group">
		
			<!-- Radio buttons for convictions. -->
			<div class="row">
				<label class="plain-label">Do you have any unspent criminal convictions?<em>*</em></label>
				<span class="hint" data-desc="<@spring.message 'additionalInformation.hasconvictions'/>"></span>
				<div class="field">
					<label><input type="radio" name="convictionRadio" value="TRUE" id="convictionRadio_true"
					<#if additionalInformation.convictions?? && additionalInformation.convictions >
					checked="checked"
					</#if> 
					<#if applicationForm.isDecided()>disabled="disabled"</#if>									   
					/> Yes</label>   		
					<label><input type="radio" name="convictionRadio" value="FALSE" id="convictionRadio_false"
					<#if  additionalInformation.convictions?? && !additionalInformation.convictions >
					checked="checked"
					</#if> 
					<#if applicationForm.isDecided()>disabled="disabled"</#if>									   
					/> No</label>
				</div>
			</div>
			<@spring.bind "additionalInformation.convictions" /> 
			<#list spring.status.errorMessages as error>
			<div class="row">
				<div class="field">
					<span class="invalid">${error}</span>
				</div>
			</div>
			</#list>
	
			<!-- Free text field for convictions. -->
			<div class="row">
				<span id="convictions-details-lbl" class="plain-label">
					Description<#if additionalInformation.convictions?? && additionalInformation.convictions><em>*</em></#if>
				</span>
				<span class="hint" data-desc="<@spring.message 'additionalInformation.convictionstext'/>"></span>
				<div class="field">
					<#if !applicationForm.isDecided()>
					<textarea id="convictionsText" name="convictionsText" 
					<#if additionalInformation.convictions?? && !additionalInformation.convictions> disabled="disabled"</#if>
					class="max" rows="6" cols="80" >${(additionalInformation.convictionsText?html)!}</textarea>
					<#else>
					<textarea readonly="readonly" id="convictionsText" name="convictionsText" class="max" rows="10" cols=80" >${(additionalInformation.convictionsText?html)!}</textarea>
					</#if>
				</div>
			</div>
			<@spring.bind "additionalInformation.convictionsText" />
			<#list spring.status.errorMessages as error>
			<div class="row">
				<div class="field">
					<span class="invalid">${error}</span>
				</div>
			</div>
			</#list>
	
		</div>

		<#if applicationForm.isModifiable() && !applicationForm.isInState('UNSUBMITTED')>
		<div class="row-group terms-box">
			<div class="row">
				<span class="terms-label">
				I understand that in accepting this declaration I am confirming
				that the information contained in this section is true and accurate. 
				I am aware that any subsequent offer of study may be retracted at any time
				if any of the information contained is found to be misleading or false.
				</span>
				<div class="terms-field">
					<input type="checkbox" name="acceptTermsAIDCB" id="acceptTermsAIDCB"/>
				</div>
				<input type="hidden" name="acceptTermsAIDValue" id="acceptTermsAIDValue"/>
			</div>	        
		</div>
		</#if>  
	
		<div class="buttons">
			<#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()>
			<button class="clear" id="informationCancelButton" name="informationCancelButton">Clear</button>
			</#if>                
			<button class="blue" type="button" id="additionalCloseButton">Close</button>
			<#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()>
			<button class="blue" type="button" id="informationSaveButton">Save</button>
			</#if>   
		</div>
	
	</form>
</div>

<script type="text/javascript" src="<@spring.url '/design/default/js/application/additional_information.js'/>"></script>
