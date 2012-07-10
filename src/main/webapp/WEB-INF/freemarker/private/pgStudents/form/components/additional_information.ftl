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
				<span id="convictions-details-lbl" class="plain-label<#if !additionalInformation.convictions?? || !additionalInformation.convictions> grey-label</#if>">
					Description<#if additionalInformation.convictions?? && additionalInformation.convictions><em>*</em></#if>
				</span>
				<span class="hint" data-desc="<@spring.message 'additionalInformation.convictionstext'/>"></span>
				<div class="field">
					<#if !applicationForm.isDecided()>
					<textarea id="convictionsText" name="convictionsText" 
					<#if additionalInformation.convictions?? && !additionalInformation.convictions> disabled="disabled"</#if>
					class="max" rows="6" cols="80" >${(additionalInformation.convictionsText?html)!}</textarea>
					<#else>
					<textarea readonly="readonly" id="convictionsText" name="convictionsText" class="max" rows="10" cols=80">${(additionalInformation.convictionsText?html)!}</textarea>
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
					<@spring.bind "additionalInformation.acceptedTerms" />
		       	<#if spring.status.errorMessages?size &gt; 0>        
				    <div class="row-group terms-box invalid" >
		
		      	<#else>
		    		<div class="row-group terms-box" >
		     	 </#if>
			<div class="row">
				<span class="terms-label">
					Confirm that the information that you have provided in this section is true 
					and correct. Failure to provide true and correct information may result in a 
					subsequent offer of study being withdrawn.				
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
			<button class="clear" id="informationClearButton" type="button" name="informationClearButton">Clear</button>
			</#if>                
			<button class="blue" id="additionalCloseButton" type="button">Close</button>
			<#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()>
			<button class="blue" id="informationSaveButton" type="button">Save</button>
			</#if>   
		</div>
	
	</form>
</div>

<script type="text/javascript" src="<@spring.url '/design/default/js/application/additional_information.js'/>"></script>
