	<#import "/spring.ftl" as spring />
	<h2 id="additional-H2" class="empty">
    	<span class="left"></span><span class="right"></span><span class="status"></span>
        Additional Information
	</h2>
    
    <div>
    	<form>
              
        	<!-- Free text field for info. -->
            <div class="admin_row">
    	        <span class="admin_row_label">Additional Information</span>
				<div class="field">${(additionalInformation.informationText?html)!"Not Provided"}</div>
			</div>

			<div class="admin_row">
				<label class="admin_row_label">Prior convictions</label>
				<div class="field">
					${(applicationForm.additionalInformation.convictions?"Yes")!"Not Provided"}
				</div>
			</div>
			
	    	<!-- Free text field for convictions. -->
			<div class="admin_row">
	       		<span class="admin_row_label">Details of the convictions</span>
	    		<div class="field">
	    			${(applicationForm.additionalInformation.convictionsText?html)!"Not Provided"}
	    		</div>
			</div>

            <div class="buttons">
                <button class="blue" id="additionalCloseButton" type="button">Close</button>
			</div>

		</form>
	</div>
	
	<script type="text/javascript" src="<@spring.url '/design/default/js/application/additional_information.js'/>"></script>
	



	