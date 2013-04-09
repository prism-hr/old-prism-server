<#assign errorCode = RequestParameters.errorCode! />

<#import "/spring.ftl" as spring />
<#setting locale = "en_US">
<a name="additional-info"></a>
<h2 id="additional-H2" class="empty open">
	<span class="left"></span><span class="right"></span><span class="status"></span>
	Additional Information<em>*</em>
</h2>

<div>
	<form>
	    <#if errorCode?? && errorCode=="true">
        <div class="alert alert-error"> 
        <i class="icon-warning-sign" data-desc="Please provide all mandatory fields in this section."></i> 
          <@spring.message 'addressDetails.sectionInfo'/>
        </div>
        <#else>
        <div class="alert alert-info"> <i class="icon-info-sign"></i>
          <@spring.message 'addressDetails.sectionInfo'/>
        </div>
        </#if>
		<div class="row-group">
		
			<!-- Radio buttons for convictions. -->
			<div class="row">
				<label class="plain-label">Do you have any unspent Criminial Convictions?<em>*</em></label>
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
					<div class="alert alert-error"> <i class="icon-warning-sign"></i> ${error} </div>
				</div>
			</div>
			</#list>
	
			<!-- Free text field for convictions. -->
			<div class="row">
				<label for="convictionsText" id="convictions-details-lbl" class="plain-label<#if !additionalInformation.convictions?? || !additionalInformation.convictions> grey-label</#if>">
					Description<#if additionalInformation.convictions?? && additionalInformation.convictions><em>*</em></#if>
				</label>
				<span class="hint" data-desc="<@spring.message 'additionalInformation.convictionstext'/>"></span>
				<div class="field">
					<#if !applicationForm.isDecided()>
					<textarea id="convictionsText" name="convictionsText" 
					<#if additionalInformation.convictions?? && !additionalInformation.convictions> disabled="disabled"</#if>
					class="max" rows="6" cols="80"    >${(additionalInformation.convictionsText?html)!}</textarea>
					<#else>
					<textarea readonly id="convictionsText" name="convictionsText" class="max" rows="10" cols=80">${(additionalInformation.convictionsText?html)!}</textarea>
					</#if>
                    <@spring.bind "additionalInformation.convictionsText" />
                    <#list spring.status.errorMessages as error>
                    <div class="alert alert-error"> <i class="icon-warning-sign"></i>
                    ${error}
                    </div>
                    </#list>
				</div>
			</div>
		</div>

		<#if applicationForm.isModifiable() && !applicationForm.isInState('UNSUBMITTED')>
					<@spring.bind "additionalInformation.acceptedTerms" />
		       	<#if spring.status.errorMessages?size &gt; 0>        
				     <div class="alert alert-error tac" >
                    <#else>
                        <div class="alert tac" >
                    </#if>
			<div class="row">
				<label class="terms-label" for="acceptTermsAIDCB">
					Confirm that the information that you have provided in this application is true 
					and correct. Failure to provide true and correct information may result in a 
					subsequent offer of study being withdrawn.				
				</label>
				<div class="terms-field">
					<input type="checkbox" name="acceptTermsAIDCB" id="acceptTermsAIDCB"/>
				</div>
				<input type="hidden" name="acceptTermsAIDValue" id="acceptTermsAIDValue"/>
			</div>	        
		</div>
		</#if>  
	
		<div class="buttons">
			<#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()>
			<button class="btn" id="informationClearButton" type="button" name="informationClearButton">Clear</button>
			</#if>                
			<button class="btn" id="additionalCloseButton" type="button">Close</button>
			<#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()>
			<button class="btn btn-primary" id="informationSaveButton" type="button">Save</button>
			</#if>   
		</div>
	
	</form>
</div>

<script type="text/javascript" src="<@spring.url '/design/default/js/application/additional_information.js'/>"></script>
