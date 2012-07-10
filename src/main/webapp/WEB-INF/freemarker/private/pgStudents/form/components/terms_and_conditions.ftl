<#import "/spring.ftl" as spring />
<#assign errorCode = RequestParameters.errorCode! />

	<form> 
	
		<div class="row-group" <#if errorCode?? && errorCode=="true">style="border-color: red" </#if>  >
	
	  
			<div class="row">
				<input type="hidden" id="ATapplicationFormId" name="applicationId" 	value="${applicationForm.applicationNumber}"/>
				<span class="terms-label"  <#if errorCode?? && errorCode=="true">style="color: red" </#if>>
					Confirm that the information that you have provided in this form is true and correct. 
					Failure to provide true and correct information may result in a subsequent offer of study being withdrawn.				
				</span>
				<div class="terms-field" >
					<input type="checkbox" name="acceptTermsCB" id="acceptTermsCB"<#if applicationForm.isDecided() || applicationForm.isWithdrawn()> disabled="disabled"</#if> />
				</div>
				<input type="hidden" name="acceptTermsValue" id="acceptTermsValue"/>
			</div>
		</div>
	</form>
        					
<script type="text/javascript" src="<@spring.url '/design/default/js/application/termsAndConditions.js'/>"></script>