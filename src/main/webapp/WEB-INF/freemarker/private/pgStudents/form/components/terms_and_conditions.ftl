<#import "/spring.ftl" as spring />
<#assign errorCode = RequestParameters.errorCode! />

	<form> 
	
		<div class="row-group" <#if errorCode?? && errorCode=="true">style="border-color: red" </#if>  >
	
	  
			<div class="row">
				<input type="hidden" id="ATapplicationFormId" name="applicationId" 	value="${applicationForm.applicationNumber}"/>
				<span class="terms-label"  <#if errorCode?? && errorCode=="true">style="color: red" </#if>>
			    	I understand that in accepting this declaration I am confirming
					that the information contained in this application is true and accurate. 
					I am aware that any subsequent offer of study may be retracted at any time
					if any of the information contained is found to be misleading or false.
				</span>
				<div class="terms-field" >
					<input type="checkbox" name="acceptTermsCB" id="acceptTermsCB"<#if applicationForm.isDecided() || applicationForm.isWithdrawn()> disabled="disabled"</#if> />
				</div>
				<input type="hidden" name="acceptTermsValue" id="acceptTermsValue"/>
			</div>
		</div>
	</form>
        					
<script type="text/javascript" src="<@spring.url '/design/default/js/application/termsAndConditions.js'/>"></script>