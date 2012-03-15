	<#import "/spring.ftl" as spring />
	<h2 class="empty">
    	<span class="left"></span><span class="right"></span><span class="status"></span>
        Additional Information
	</h2>
    
    <div>
    	<form>
              
        	<!-- Free text field for info. -->
            <div>
                <textarea readonly="readonly" id="additionalInformation" name="additionalInformation" class="max" rows="5" cols="90" >${model.applicationForm.additionalInformation!}</textarea>
			</div>

            <div class="buttons">
                <button class="blue" type="button">Close</button>
			</div>

		</form>
	</div>
	
	<script type="text/javascript" src="<@spring.url '/design/default/js/application/additional_information.js'/>"></script>