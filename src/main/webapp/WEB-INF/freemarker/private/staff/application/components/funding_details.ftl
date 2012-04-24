<#if applicationForm.fundings?has_content>
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
	
	
		<form>
			<#if hasFundings>
		            
				<#list applicationForm.fundings as funding>		
				
					<!-- All hidden input - Start -->
					
			    	<input type="hidden" id="${funding.id?string('#######')}_fundingIdDP" value="${funding.id?string('#######')}"/>
	                <input type="hidden" id="${funding.id?string('#######')}_fundingTypeDP" value="${funding.type?html}"/>
	                <input type="hidden" id="${funding.id?string('#######')}_fundingValueDP" value="${funding.value?html}"/>
	                <input type="hidden" id="${funding.id?string('#######')}_fundingDescriptionDP" value="${funding.description?html}"/>
	                <input type="hidden" id="${funding.id?string('#######')}_fundingAwardDateDP" value="${funding.awardDate?string('dd-MMM-yyyy')}"/>
	                <input type="hidden" id="${funding.id?string('#######')}_docname" value="${(funding.document.fileName?html)!}"/>
	                <input type="hidden" id="${funding.id?string('#######')}_docurl" value="/pgadmissions/download?documentId=${(funding.document.id?string("#######"))!}"/>
	                
	        		<input type="hidden" id="fundingId" name="fundingId"/>                
					
			    	<!-- All hidden input - End --> 
	                		
	                <!-- Rendering part - Start -->
		        	<div class="sub_section_amdin">

						<!-- Header -->
					    <div class="admin_row">
					    	<label class="admin_header">Funding (${funding_index + 1})</label>
					         <div class="field">&nbsp</div>
						</div>

						<!-- Award type -->
		                <div class="admin_row">
		                	<span class="admin_row_label">Funding Type</span>             
		                	<div class="field" id="fundingType">${(funding.type?html)!"Not Available"}</div>
						</div>
		
		                <!-- Award description -->
		                <div class="admin_row">
		                	<span class="admin_row_label" >Description</span>
		                  	<div class="field" id="fundingDescription">${(funding.description?html)!"Not Available"}</div>
						</div>
		                  
		                <!-- Value of award -->
		                <div class="admin_row">
		                  	<span class="admin_row_label">Value of Award</span>
		                 	<div class="field"  id="fundingValue">${(funding.value?html)!"Not Available"}</div>
						</div>
		                  
		                <!-- Award date -->
		                <div class="admin_row">
		                    <span class="admin_row_label">Award Date</span>
		                    <div class="field" id="fundingAwardDate">${funding.awardDate?string('dd-MMM-yyyy')!"Not Available"}</div>
		                </div>
		                
		                  <!-- Award date -->
		                <div class="admin_row">
							<span class="admin_row_label">Proof of award</span>
							<#if funding.document?has_content>
								<div class="field"  id="proofOfAward">
					        		<a href="<@spring.url '/pgadmissions/download?documentId=${(funding.document.id?string("#######"))!}'/>">
					            		${(funding.document.fileName?html)!}</a>
					            </div>
					        <#else> 
					        	<div class="field" id="referenceDocument">Not Provided.</div> 
					        </#if>
							
		                </div>
		                
					</div>
	                
				</#list>
				
			<#else>
			
	                <!-- Rendering part - Start -->
		        	<div class="sub_section_amdin">

						<!-- Award type -->
		                <div class="admin_row">
		                	<span class="admin_row_label">Funding Type</span>             
		                	<div class="field" id="fundingType">Not Available</div>
						</div>
		
		                <!-- Award description -->
		                <div class="admin_row">
		                	<span class="admin_row_label" >Description</span>
		                  	<div class="field" id="fundingDescription">Not Available</div>
						</div>
		                  
		                <!-- Value of award -->
		                <div class="admin_row">
		                  	<span class="admin_row_label">Value of Award</span>
		                 	<div class="field"  id="fundingValue">Not Available</div>
						</div>
		                  
		                <!-- Award date -->
		                <div class="admin_row">
		                    <span class="admin_row_label">Award Date</span>
		                    <div class="field" id="fundingAwardDate">Not Available</div>
		                </div>
		                
		                  <!-- Award date -->
		                <div class="admin_row">
							<span class="admin_row_label">Proof of award</span>
					        	<div class="field" id="referenceDocument">Not Available</div> 
		                </div>
		                
					</div>
							               
	        </#if>

			<div class="buttons">
				<button class="blue" id="fundingCloseButton"  type="button">Close</button>
			</div>

		</form>
	</div>

	<script type="text/javascript" src="<@spring.url '/design/default/js/application/staff/funding.js'/>"></script>