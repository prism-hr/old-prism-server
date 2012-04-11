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
                <textarea readonly="readonly" id="additionalInformation" name="additionalInformation" class="max" rows="10" cols="80" >${(model.applicationForm.additionalInformation?html)!}</textarea>
			</div>

            <div class="buttons">
                <button class="blue" id="additionalCloseButton" type="button">Close</button>
			</div>

		</form>
	</div>
	
	<script type="text/javascript" src="<@spring.url '/design/default/js/application/additional_information.js'/>"></script>