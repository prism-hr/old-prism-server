<#import "/spring.ftl" as spring />
<#assign termsError = RequestParameters.termsError! />


<input type="hidden" id="ATapplicationFormId" name="applicationId" 	value="${applicationForm.id?string("######")}"/>
			          			I understand that in accepting this declaration I am confirming
											that the information contained in this application is true and accurate. 
											I am aware that any subsequent offer of study may be retracted at any time
											if any of the information contained is found to be misleading or false.
		          	      					<input type="checkbox" name="acceptTermsCB" id="acceptTermsCB"
	           								<#if applicationForm.isDecided() || applicationForm.isWithdrawn()>
	                              				disabled="disabled"
	                    					</#if>
	                    					/>
	                    					<input type="hidden" name="acceptTermsValue" id="acceptTermsValue"/>
	                    		<span class="invalid" name="nonAccepted"></span>
            					
<script type="text/javascript" src="<@spring.url '/design/default/js/application/termsAndConditions.js'/>"></script>