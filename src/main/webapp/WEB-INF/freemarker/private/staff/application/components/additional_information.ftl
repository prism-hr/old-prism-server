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
				<div class="field">${(applicationForm.additionalInformation?html)!"Not Provided"}</div>
			</div>
            <div class="buttons">
                <button class="blue" id="additionalCloseButton" type="button">Close</button>
			</div>

		</form>
	</div>
	
	<script type="text/javascript" src="<@spring.url '/design/default/js/application/additional_information.js'/>"></script>
	



	