<#assign errorCode = RequestParameters.errorCode! />
<#if applicationForm.fundings?has_content>
	<#assign hasFundings = true>
<#else>
	<#assign hasFundings = false>
</#if>

<#import "/spring.ftl" as spring />

<a name="funding-details"></a>
<h2 id="funding-H2" class="empty open">
	<span class="left"></span><span class="right"></span><span class="status"></span>
		Funding
</h2>

<div>

	<#if hasFundings>
		<table class="existing">
			
			<colgroup>
            	<col style="width: 30px" />
                <col style="width: 220px" />
                <!--
                <col style="width: 120px" /> -->
                <col />
                <col style="width: 120px" />
                <col style="width: 30px" />
        	</colgroup>
            
            <thead>
				<tr>
                	<th id="primary-header" colspan="2">Funding Type</th>
                    <th>Description</th>
                    <#--
                    <th>Supporting Documentation</th> -->
                    <th>Award Date</th>
                    <th colspan="1">&nbsp;</th>
                    <th id="last-col">&nbsp;</th>
				</tr>
			</thead>
                
			<tbody>
			
				<#list applicationForm.fundings as existingFunding>		
					<tr>
	                  	<td><a class="row-arrow">-</a></td>
	                  	<td><a href="<@spring.url '/download'/>?documentId=${existingFunding.document.id?string('#######')}"
	                  		data-desc="Proof of Award" class="button-hint">
	                  	${existingFunding.type.displayValue} ${(existingFunding.value?html)!}</a>
	                  	</td>
	                  	<td>${existingFunding.description}</td>
	                  	<#--
	                  	<td ><a href="<@spring.url '/download'/>?documentId=${existingFunding.document.id?string('#######')}">
                        <#if existingFunding.document.fileName?length <20 >${existingFunding.document.fileName}<#else>${existingFunding.document.fileName?substring(0,17)}...</#if></a></td>-->
	                  	<td>${existingFunding.awardDate?string('dd-MMM-yyyy')}</td>
								<td>				                  		                		
		                			<a name="editFundingLink" <#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()>data-desc="Edit" <#else>data-desc="Show"</#if> id="funding_${existingFunding.id?string('#######')}" class="button-edit button-hint">edit</a>
		                		</td>
		                		<td>
	                  	     <#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()>
		                			<a name="deleteFundingButton" data-desc="Delete" id="funding_${existingFunding.id?string('#######')}" class="button-delete button-hint">delete</a>
		                		</td>
		                	<#else>
		                		<td></td><td></td>		                		
		                	</#if>

	                  	
	                </tr>
				</#list>				               
			</tbody>
		
		</table>
    </#if>
    <!-- Non-rendering data -->
          
	<form >
    <input type="hidden" id="fundingId" name="fundingId" value="${(funding.id?string('#######'))!}"/>    


				<#if errorCode?? && errorCode=="true">
					<div class="section-error-bar">
						<div class="row">
							<span class="error-hint" data-desc="Please provide all mandatory fields in this section."></span>             	
							<span class="invalid-info-text">
								<@spring.message 'fundingDetails.sectionInfo'/>
							</span>
				 		</div>
				 	</div>
			 	<#else>
				 	<div id="fund-info-bar-div" class="section-info-bar">
						<div class="row">
							<span id="fund-info-bar-span" class="info-text">
								<@spring.message 'fundingDetails.sectionInfo'/> 
							</span>
						</div>
					</div>	
				</#if>

		<div>
			<!-- Award type -->
            <div class="row">
              	<span class="plain-label">Funding Type<em>*</em></span>
                <span class="hint" data-desc="<@spring.message 'fundingDetails.award.type'/>"></span>
            	
            	<div class="field">
            		<select id="fundingType" name="fundingType" class="full"
            		<#if applicationForm.isDecided() || applicationForm.isWithdrawn()>
                                            disabled="disabled"
                                        </#if>>
                    	<option value="">Select...</option>
                    	<#list fundingTypes as type>
                         	<option value="${type}"
                         	<#if funding.type?? && funding.type == type>
                                    selected="selected"
                                    </#if>
                         	>${type.displayValue}</option>               
                    	</#list>
                  	</select>
     
                	
				</div>
			</div>
			       		<@spring.bind "funding.type" /> 
				<#list spring.status.errorMessages as error>
					<div class="row">
						<div class="field">
							<span class="invalid">${error}</span>
						</div>
					</div>
				</#list>

            <!-- Award description -->
            <div class="row">
            	<span class="plain-label">Description<em>*</em></span>
                <span class="hint" data-desc="<@spring.message 'fundingDetails.award.description'/>"></span>
				<div class="field">
			    <#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()>		
                	<textarea id="fundingDescription" name="fundingDescription" class="max" cols="70" rows="6" maxlength='2000'>${(funding.description?html)!}</textarea>
            		 
  
                  <#else>
                    <textarea id="fundingDescription" name="fundingDescription" class="full" readonly="readonly">${(funding.description?html)!}</textarea>
                </#if>
                </div>
                
			</div>
			<@spring.bind "funding.description" />
				<#list spring.status.errorMessages as error>
					<div class="row">
						<div class="field">
							<span class="invalid">${error}</span>
						</div>
					</div>
				</#list>
			
              
            <!-- Value of award -->
            <div class="row">
              	<span class="plain-label">Value of Award (GBP)<em>*</em></span>
                <span class="hint" data-desc="<@spring.message 'fundingDetails.award.value'/>"></span>
                <div class="field">
                <#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()>
                	<input id="fundingValue" name="fundingValue" class="full" type="text" value="${(funding.value?html)!}" />
             	 
                	     
                <#else>
                   <input id="fundingValue" readonly="readonly" name="fundingValue" class="full" type="text" value="${(funding.value?html)!}" />
                </#if>
                </div>
			</div>
				<@spring.bind "funding.value" />
				<#list spring.status.errorMessages as error>
					<div class="row">
						<div class="field">
							<span class="invalid">${error}</span>
						</div>
					</div>
				</#list>
              
            <!-- Award date -->
            <div class="row">
              	<span class="plain-label">Award Date<em>*</em></span>
                <span class="hint"  data-desc="<@spring.message 'fundingDetails.award.awardDate'/>"></span>
                <div class="field">
                    <input id="fundingAwardDate" name="fundingAwardDate" class="half date" type="text" value="${(funding.awardDate?string('dd-MMM-yyyy'))!}"
                       <#if applicationForm.isDecided() || applicationForm.isWithdrawn()>
                                            disabled="disabled"
                                        </#if>>
                                </input>
             
                </div>
                
            </div>
            		<@spring.bind "funding.awardDate" />
				<#list spring.status.errorMessages as error>
					<div class="row">
						<div class="field">
							<span class="invalid">${error}</span>
						</div>
					</div>
				</#list>
           
     <!-- Attachment / supporting document -->
      		<div class="row">
        		<span class="plain-label">Proof of Award (PDF)<em>*</em></span>
        		<span class="hint" data-desc="<@spring.message 'fundingDetails.award.proofOfAward'/>"></span>
        		<div class="field" id="fundingUploadFields">        	
          			<input id="fundingDocument" class="full" type="file" name="file" value="" <#if applicationForm.isDecided() || applicationForm.isWithdrawn()>disabled="disabled"</#if>/>					
					<span id="fundingUploadedDocument">
						<input type="hidden" id="document_SUPPORTING_FUNDING" value = "${(funding.document.id?string('######'))!}"/>
                		<!--  
						<a href="<@spring.url '/download?documentId=${(funding.document.id?string("#######"))!}'/>">${(funding.document.fileName)!}</a>-->
					</span>
					<span id="fundingDocumentProgress" style="display: none;" ></span>					
        		</div>  
        		
      		</div>
      		 	<@spring.bind "funding.document" /> 
				<#list spring.status.errorMessages as error>
					<div class="row">
						<div class="field">
							<span class="invalid">${error}</span>
						</div>
					</div>
				</#list>
      		
      		<#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()>
	      		<!-- Add another button -->
	            <div class="row">
	            	<div class="field">
	                	<a id="addFundingButton" class="button blue">Submit</a>
	                </div>
	            </div>
			</#if>
		</div>


       <#if applicationForm.isModifiable() && !applicationForm.isInState('UNSUBMITTED')>
       <div class="terms-box">
			<div class="row">
				<span class="terms-label">
					I understand that in accepting this declaration I am confirming
					that the information contained in this section is true and accurate. 
					I am aware that any subsequent offer of study may be retracted at any time
					if any of the information contained is found to be misleading or false.
				</span>
				<div class="terms-field">
		        	<input type="checkbox" name="acceptTermsFDCB" id="acceptTermsFDCB"/>
		        </div>
	            <input type="hidden" name="acceptTermsFDValue" id="acceptTermsFDValue"/>
			</div>	        
	    </div>
	    </#if>  

	<div class="buttons">
		<#if !applicationForm.isDecided() && !applicationForm.isWithdrawn()>
        	
        	<a class="button" type="button" id="fundingCancelButton" name="fundingCancelButton">Clear</a>
        	<button class="blue" type="button" id="fundingCloseButton" name="fundingCloseButton">Close</button>
            <button class="blue" type="button" id="fundingSaveCloseButton" value="close">Save</button>
  		<#else>
             <a id="fundingCloseButton" class="button blue">Close</a>  
   		</#if>
   </div>

	</form>
</div>

<script type="text/javascript" src="<@spring.url '/design/default/js/application/ajaxfileupload.js'/>"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/application/funding.js'/>"></script>
	

<@spring.bind "funding.*" /> 
 
<#if (errorCode?? && errorCode=='false') || (message?? && message='close' && !spring.status.errorMessages?has_content)>	
<script type="text/javascript">
	$(document).ready(function(){
		$('#funding-H2').trigger('click');
	});
</script>
</#if>