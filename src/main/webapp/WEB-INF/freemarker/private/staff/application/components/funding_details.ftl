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
		                  	<td>${funding.description?html}</td>
		                  	<td>${funding.awardDate?string('dd-MMM-yyyy')}</td>
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
                  	<span class="label">Funding Type</span>
                    <span class="hint" data-desc="Tooltip demonstration."></span>
                	
                	<div class="field">
                	<select id="fundingType" name="fundingType" class="full" value="${(model.funding.fundingType?html)!}" 
                                                disabled="disabled">
                            <option value="">Select...</option>
                            <#list model.fundingTypes as type>
                                <option value="${type}"
                                <#if model.funding.fundingType?? && model.funding == type>
                                        selected="selected"
                                        </#if>
                                >${type.displayValue}</option>               
                            </#list>
                        </select>
					</div>
				</div>

                <!-- Award description -->
                <div class="row">
                	<span class="label">Description</span>
                    <span class="hint" data-desc="Tooltip demonstration."></span>
					<div class="field">
					<input id="fundingDescription" name="fundingDescription" readonly="readonly" class="full" type="text" value="${(model.funding.fundingDescription?html)!}" />
					</div>
				</div>
                  
                <!-- Value of award -->
                <div class="row">
                  	<span class="label">Value of Award</span>
                    <span class="hint" data-desc="Tooltip demonstration."></span>
                    <div class="field">
                    <input id="fundingValue" readonly="readonly" name="fundingValue" class="full" type="text" value="${(model.funding.fundingValue?html)!}" />
					</div>
				</div>
                  
                <!-- Award date -->
                <div class="row">
                  	<span class="label" data-desc="Tooltip demonstration.">Award Date</span>
                    <span class="hint"></span>
                    <div class="field">
                     <input id="fundingAwardDate" name="fundingAwardDate" class="half date" type="text" value="${(model.funding.fundingAwardDate?string('dd-MMM-yyyy'))!}"
                                                disabled="disabled"/>
					</div>
                </div>
                
                <!--  
                <div class="row">
                  	<span class="label">Supporting Document</span>
                    <span class="hint" data-desc="Tooltip demonstration."></span>
                    <div class="field">
                    </div>	
				</div> -->

			</div>

			<div class="buttons">
				<button class="blue" id="fundingCloseButton"  type="button">Close</button>
			</div>

		</form>
	</div>

	<script type="text/javascript" src="<@spring.url '/design/default/js/application/funding.js'/>"></script>