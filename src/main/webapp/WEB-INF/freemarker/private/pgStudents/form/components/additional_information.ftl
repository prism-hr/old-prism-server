<#assign errorCode = RequestParameters.errorCode! />

<#import "/spring.ftl" as spring />

<h2 id="additional-H2" class="empty open">
	<span class="left"></span><span class="right"></span><span class="status"></span>
    Additional Information
</h2>

<div>
	<form>
        
		<div class="section-info-bar">
			<div class="row">
				<span class="info-text"> &nbsp 
					<@spring.message 'additionalInformation.sectionInfo'/> 
				</span>
			</div>
		</div>
          
    	<!-- Free text field for info. -->
        <div class="row">
       		<span class="plain-label">Additional information relevant to your application</span>
    		<span class="hint" data-desc="<@spring.message 'additionalInformation.infotext'/>"></span>
    		<div class="field">
      		 <#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()>
        		<textarea id="informationText" name="informationText" class="max" rows="6" cols="80" maxlength='5000'>${(additionalInformation.informationText?html)!}</textarea>
            <#else>
                <textarea readonly="readonly" id="informationText" name="informationText" class="max" rows="10" cols=80" >${(additionalInformation.informationText?html)!}</textarea>
            </#if>
            </div>
		</div>

		<div>
    		<#if errorCode?? && errorCode=="true">
				<div class="row">              	
					<span class="invalid">Please provide all mandatory fields in this section.<p></p></span>
			     </div>            	
			</#if>
	    	
	    	<!-- Radio buttons for convictions. -->
			<div class="row">
				<label class="plain-label">Do you have any prior convictions?<em>*</em></label>
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
					<@spring.bind "additionalInformation.convictions" /> 
                	<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>    		 
				</div>
			</div>
			
	    	<!-- Free text field for convictions. -->
			<div class="row">
	       		<span id="convictions-details-lbl" class="plain-label">Details of the convictions
	        	  <#if  additionalInformation.convictions?? && additionalInformation.convictions >
	       		    <em>*</em>
	        	  </#if>
	       		</span>
	    		<span class="hint" data-desc="<@spring.message 'additionalInformation.convictionstext'/>"></span>
	    		<div class="field">
	      		 <#if !applicationForm.isDecided()>
	        		<textarea id="convictionsText" name="convictionsText" 
	        		<#if  additionalInformation.convictions?? && !additionalInformation.convictions >
	        		    disabled="disabled"
	        		</#if>
	        		class="max" rows="6" cols="80" maxlength='5000'>${(additionalInformation.convictionsText?html)!}</textarea>
					<@spring.bind "additionalInformation.convictionsText" /> 
                	<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>    		 
	            <#else>
	                <textarea readonly="readonly" id="convictionsText" name="convictionsText" class="max" rows="10" cols=80" >${(additionalInformation.convictionsText?html)!}</textarea>
	            </#if>
	            </div>
			</div>
		</div>
				
        <div class="buttons">
        	<#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()>
	        	<a class="button" id="informationCancelButton" name="informationCancelButton">Cancel</a>
            </#if>                
        	<button class="blue" type="button" id="additionalCloseButton">Close</button>
	        <#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()>
	            <button class="blue" type="button" id="informationSaveButton">Save</button>
	         </#if>   
		</div>
		
	</form>
</div>

<script type="text/javascript" src="<@spring.url '/design/default/js/application/additional_information.js'/>"></script>
 <@spring.bind "additionalInformation.*" /> 
<#if (errorCode?? && errorCode=='false') || (message?? && message='close' && !spring.status.errorMessages?has_content)>	
<script type="text/javascript">
	$(document).ready(function(){
		$('#additional-H2').trigger('click');
	});
</script>
</#if>