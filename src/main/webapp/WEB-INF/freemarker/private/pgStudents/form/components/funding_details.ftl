<#if model.applicationForm.fundings?has_content>
	<#assign hasFundings = true>
<#else>
	<#assign hasFundings = false>
</#if>

<#import "/spring.ftl" as spring />

	<h2 id="funding-H2" class="empty">
		<span class="left"></span><span class="right"></span><span class="status"></span>
			Funding
	</h2>

	<div>
	
		<#if hasFundings>
			<table class="existing">
				
				<colgroup>
	            	<col style="width: 30px" />
	                <col style="width: 120px" />
	                <col />
	                <col style="width: 120px" />
	                <col style="width: 30px" />
	        	</colgroup>
	            
	            <thead>
					<tr>
	                	<th colspan="2">Funding Type</th>
	                    <th>Description</th>
	                    <th>Award Date</th>
	                    <th colspan="1">&nbsp;</th>
					</tr>
				</thead>
	                
				<tbody>
				
					<#list model.applicationForm.fundings as funding>		
						<tr>
		                  	<td><a class="row-arrow" name="fundingEditButton" id="funding_${funding.id?string('#######')}">-</a></td>
		                  	<td>${funding.type.displayValue}</td>
		                  	<td>${funding.description}</td>
		                  	<td>${funding.awardDate?string('dd-MMM-yyyy')}</td>
		                  	      <td>
		                  	      	 <#if !model.applicationForm.isSubmitted()>
				                  	<form method="Post" action="<@spring.url '/deleteentity/funding'/>" style="padding:0">
			                			<input type="hidden" name="id" value="${funding.id?string('#######')}"/>		                		
			                			<a name="deleteButton" class="button-delete">delete</a>
			                		</form>
			                		</#if>
				        		</td>
		                  	
		                  	<!-- Non-rendering data -->
							<input type="hidden" id="${funding.id?string('#######')}_fundingIdDP" value="${funding.id?string('#######')}"/>
	                        <input type="hidden" id="${funding.id?string('#######')}_fundingTypeDP" value="${funding.type?html}"/>
	                        <input type="hidden" id="${funding.id?string('#######')}_fundingValueDP" value="${funding.value?html}"/>
	                        <input type="hidden" id="${funding.id?string('#######')}_fundingDescriptionDP" value="${funding.description?html}"/>
	                        <input type="hidden" id="${funding.id?string('#######')}_fundingAwardDateDP" value="${funding.awardDate?string('dd-MMM-yyyy')}"/>
		                  	
		                </tr>
					</#list>				               
				</tbody>
			
			</table>
        </#if>
        <!-- Non-rendering data -->
        <input type="hidden" id="fundingId" name="fundingId"/>
              
		<form>
                
			<div>
				<!-- Award type -->
                <div class="row">
                  	<span class="label">Funding Type<em>*</em></span>
                    <span class="hint" data-desc="Tooltip demonstration."></span>
                	
                	<div class="field">
                		<select id="fundingType" name="fundingType" class="full" value="${model.funding.fundingType!}" 
                		<#if model.applicationForm.isSubmitted()>
                                                disabled="disabled"
                                            </#if>>
                        	<option value="">Select...</option>
                        	<#list model.fundingTypes as type>
                             	<option value="${type}"
                             	<#if model.funding.fundingType?? && model.funding.fundingType == type>
                                        selected="selected"
                                        </#if>
                             	>${type.displayValue}</option>               
                        	</#list>
                      	</select>
                		<#if model.hasError('fundingType')>
                        	<span class="invalid"><@spring.message  model.result.getFieldError('fundingType').code /></span>                           
                        </#if>
					</div>
				</div>

                <!-- Award description -->
                <div class="row">
                	<span class="label">Description<em>*</em></span>
                    <span class="hint" data-desc="Tooltip demonstration."></span>
					<div class="field">
				    <#if !model.applicationForm.isSubmitted()>
                    	<input id="fundingDescription" name="fundingDescription" class="full" type="text" value="${(model.funding.fundingDescription?html)!}" />
	                    <#if model.hasError('fundingDescription')>                           
	                    	<span class="invalid"><@spring.message  model.result.getFieldError('fundingDescription').code /></span>                           
	                    </#if>
                    <#else>
                        <input id="fundingDescription" name="fundingDescription" readonly="readonly" class="full" type="text" value="${(model.funding.fundingDescription?html)!}" />
                    </#if>
                    </div>
                    
				</div>
                  
                <!-- Value of award -->
                <div class="row">
                  	<span class="label">Value of Award<em>*</em></span>
                    <span class="hint" data-desc="Tooltip demonstration."></span>
                    <div class="field">
                    <#if !model.applicationForm.isSubmitted()>
                    	<input id="fundingValue" name="fundingValue" class="full" type="text" value="${(model.funding.fundingValue?html)!}" />
	                    <#if model.hasError('fundingValue')>
	                    	<span class="invalid"><@spring.message  model.result.getFieldError('fundingValue').code /></span>
	                    </#if>
                    <#else>
                       <input id="fundingValue" readonly="readonly" name="fundingValue" class="full" type="text" value="${(model.funding.fundingValue?html)!}" />
                    </#if>
                    </div>
				</div>
                  
                <!-- Award date -->
                <div class="row">
                  	<span class="label" data-desc="Tooltip demonstration.">Award Date<em>*</em></span>
                    <span class="hint"></span>
                    <div class="field">
	                    <input id="fundingAwardDate" name="fundingAwardDate" class="half date" type="text" value="${(model.funding.fundingAwardDate?string('dd-MMM-yyyy'))!}"
	                       <#if model.applicationForm.isSubmitted()>
                                                disabled="disabled"
                                            </#if>>
                                    </input>
                    	<#if model.hasError('fundingAwardDate')>                           
                    		<span class="invalid""><@spring.message  model.result.getFieldError('fundingAwardDate').code /></span>                           
                    	</#if>
                    </div>
                    
                </div>
               
               <!--   
                <div class="row">
                  	<span class="label">Supporting Document</span>
                    <span class="hint" data-desc="Tooltip demonstration."></span>
                    <div class="field">
                    	<input class="full" type="text" value="" />
                      	<a class="button" href="#">Browse</a>
                      	<a class="button" href="#">Upload</a>
                      	<a class="button plus" href="#">Add Another</a>
                    </div>	
				</div> -->

			</div>

		<div class="buttons">
        <#if !model.applicationForm.isSubmitted()>
            	
            	<a class="button" type="button" id="fundingCancelButton" name="fundingCancelButton">Cancel</a>
            	<button class="blue" type="button" id="fundingCloseButton" name="fundingCloseButton">Close</button>
				<button class="blue" type="button" id="fundingSaveCloseButton" name="fundingSaveCloseButton" value="close">Save and Close</button>
                <button class="blue" type="button" id="fundingSaveAddButton" name="fundingSaveAddButton" value="add">Save and Add</button>
              <#else>
                    <a id="fundingCloseButton" class="button blue">Close</a>  
	   </#if>
	   </div>

		</form>
	</div>
<script type="text/javascript" src="<@spring.url '/design/default/js/libraries.js'/>"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/application/common.js'/>"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/application/funding.js'/>"></script>
	
<#if (model.result?? && model.result.hasErrors() ) || add?? >

<#else >
<script type="text/javascript">
	$(document).ready(function(){
		$('#funding-H2').trigger('click');
	});
</script>
</#if>