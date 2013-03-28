<#import "/spring.ftl" as spring />
<#assign errorCode = RequestParameters.errorCode! />

	<form> 
		<div id="termscond" class="alert <#if errorCode?? && errorCode=="true">alert-error <#else> alert-info </#if>"   >
			<div class="row">
				<input type="hidden" id="ATapplicationFormId" name="applicationId" 	value="${applicationForm.applicationNumber}"/>
				<label class="terms-label" for="acceptTermsCB">
					Confirm that the information that you have provided in this form is true and correct. 
					Failure to provide true and correct information may result in a subsequent offer of study being withdrawn.				
				</label>
				<div class="terms-field" >
					<input type="checkbox" name="acceptTermsCB" id="acceptTermsCB"<#if applicationForm.isDecided() || applicationForm.isWithdrawn()> disabled="disabled"</#if> />
				</div>
				<input type="hidden" name="acceptTermsValue" id="acceptTermsValue"/>
			</div>
		</div>
	</form>
        					
<script type="text/javascript" src="<@spring.url '/design/default/js/application/termsAndConditions.js'/>"></script>