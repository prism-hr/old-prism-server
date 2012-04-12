<#import "/spring.ftl" as spring />
<h2 id="additional-H2" class="empty">
	<span class="left"></span><span class="right"></span><span class="status"></span>
    Additional Information
</h2>

<div>
	<form>
          
    	<!-- Free text field for info. -->
        <div class="row">
       		<span class="plain-label">Additional information</span>
    		<span class="hint" data-desc="<@spring.message 'additionalInformation.content'/>"></span>
    		<div class="field">
      		 <#if !applicationForm.isSubmitted()>
        		<textarea id="additionalInformation" name="additionalInformation" class="max" rows="6" cols="80" >${(applicationForm.additionalInformation?html)!}</textarea>
            <#else>
                <textarea readonly="readonly" id="additionalInformation" name="additionalInformation" class="max" rows="10" cols=80" >${(applicationForm.additionalInformation?html)!}</textarea>
            </#if>
            </div>
		</div>

        <div class="buttons">
        	<a class="button" id="informationCancelButton" name="informationCancelButton">Cancel</a>
        	<button class="blue" type="button" id="additionalCloseButton">Close</button>
            <button class="blue" type="button" id="informationSaveButton">Save</button>                
		</div>

	</form>
</div>

<script type="text/javascript" src="<@spring.url '/design/default/js/application/additional_information.js'/>"></script>
	
<script type="text/javascript">
	$(document).ready(function(){
		$('#additional-H2').trigger('click');
	});
</script>