	<#import "/spring.ftl" as spring />
	<h2 id="additional-H2" class="empty">
    	<span class="left"></span><span class="right"></span><span class="status"></span>
        Additional Information
	</h2>
    
    <div>
    	<form>
          <div class="sub_section_amdin">
        	<!-- Free text field for info. -->
            <div class="admin_row">
    	        <span class="admin_row_label">Additional Information</span>
				<div class="field">${(applicationForm.additionalInformation.informationText?html)!"Not Provided"}</div>
			</div>

			<div class="admin_row">
				<label class="admin_row_label">Prior convictions</label>
				<div class="field">
					<#if applicationForm.additionalInformation.convictions>
						Yes
	                <#else>
	                	No
	                </#if>
				</div>
			</div>
			
			<#if applicationForm.additionalInformation.convictions?? && applicationForm.additionalInformation.convictions >
				<!-- Free text field for convictions. -->
				<div class="admin_row">
					<span class="admin_row_label">Details of the convictions</span>
					<div class="field">
						${(applicationForm.additionalInformation.convictionsText?html)!"Not Provided"}
					</div>
				</div>
			</#if> 

            <div class="buttons">
                <button class="blue" id="additionalCloseButton" type="button">Close</button>
			</div>

          </div>    
		</form>
	</div>
	
	<script type="text/javascript" src="<@spring.url '/design/default/js/application/additional_information.js'/>"></script>
	