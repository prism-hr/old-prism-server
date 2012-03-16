	<#import "/spring.ftl" as spring />
	<h2 id="additional-H2" class="empty">
    	<span class="left"></span><span class="right"></span><span class="status"></span>
        Additional Information
	</h2>
    
    <div>
    	<form>
              
        	<!-- Free text field for info. -->
            <div class="row">
            <#if !model.applicationForm.isSubmitted()>
            	<textarea id="additionalInformation" name="additionalInformation" class="max" rows="5" cols="90" >${model.applicationForm.additionalInformation!}</textarea>
            	<#if model.hasError('additionalInformation')>                           
                    <span class="invalid"><@spring.message  model.result.getFieldError('additionalInformation').code /></span><br/>
                </#if>
                <#else>
                    <textarea readonly="readonly" id="additionalInformation" name="additionalInformation" class="max" rows="5" cols="90" >${model.applicationForm.additionalInformation!}</textarea>
                </#if>
			</div>

            <div class="buttons">
            	<a class="button" id="informationCancelButton" name="informationCancelButton">Cancel</a>
                <button class="blue" type="button" id="informationSaveButton">Save</button>
                <button class="blue" type="button" id="additionalCloseButton">Close</button>
			</div>

		</form>
	</div>
	
	<script type="text/javascript" src="<@spring.url '/design/default/js/application/additional_information.js'/>"></script>
	
<#if model.result?? && model.result.hasErrors()  >

<#else >
<script type="text/javascript">
	$(document).ready(function(){
		$('#additional-H2').trigger('click');
	});
</script>
</#if>