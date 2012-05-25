<#if applicationForm.employmentPositions?has_content>
	<#assign hasEmploymentPositions = true>
<#else>
	<#assign hasEmploymentPositions = false>
</#if>

<#import "/spring.ftl" as spring />
	
	<h2 id="position-H2" class="empty">
		<span class="left"></span><span class="right"></span><span class="status"></span>
		Employment
	</h2>

	<div>
	
		<form>
			<#if hasEmploymentPositions>
		    	<#list applicationForm.employmentPositions as position>
			         
			        <!-- All hidden input - Start -->
			                    
					<input type="hidden" id="${position.id?string('#######')}_positionId" value="${position.id?string('#######')}"/>
	                <input type="hidden" id="${position.id?string('#######')}_employerName" value="${(position.employerName?html)!}"/>
	                <input type="hidden" id="${position.id?string('#######')}_employerAddress" value="${(position.employerAddress?html)!}"/>
	                <input type="hidden" id="${position.id?string('#######')}_employerCountry" value="${(position.employerCountry.name?html)!}"/>
	                <input type="hidden" id="${position.id?string('#######')}_positionTitle" value="${position.position}"/>
	                <input type="hidden" id="${position.id?string('#######')}_remit" value="${(position.remit?html)!}"/>
	                <input type="hidden" id="${position.id?string('#######')}_language" value="${(position.language.name?html)!}"/>
	                            
	                <input type="hidden" id="${position.id?string('#######')}_positionStartDate" value="${(position.startDate?string('dd-MMM-yyyy'))!}"/>
	                <input type="hidden" id="${position.id?string('#######')}_positionEndDate" value="${(position.endDate?string('dd-MMM-yyyy'))!}"/>
			        <input type="hidden" id="${position.id?string('#######')}_positionCurrent" value="<#if position.current>Yes<#else>No</#if>"/>
					
					<input type="hidden" id="positionId" name="positionId" value=""/>    
			    
			    	<!-- All hidden input - End --> 
	                		
	                <!-- Rendering part - Start -->
		        	<div class="row-group">
		                     
						<!-- Header -->
					    <div class="admin_row">
					    	<label class="admin_header">Position (${position_index + 1})</label>
					         <div class="field">&nbsp</div>
						</div>
		                        
		                <div class="admin_row">
		                	<span class="admin_row_label">Country</span>                 
		                    <div class="field" id="emp_country">${(position.employerCountry.name?html)!"Not Provided"}</div>
		                </div>
		            	
		            	<!-- Employer (company name) -->
		                <div class="admin_row">
		                	<span class="admin_row_label">Employer Name</span>                 
		                   	<div class="field" id="emp_name">${(position.employerName?html)!"Not Provided"}</div>
		                </div>
		                
		                <div class="admin_row">
		                	<span class="admin_row_label">Employer Address</span>                 
		                   	<div class="field" id="emp_address">${(position.employerAddress?html)!"Not Provided"}</div>
		                </div>
		                
		                <!-- Position -->
		                <div class="admin_row">
		                	<span class="admin_row_label">Position</span>
		              		<div class="field" id="emp_position">${(position.position)!"Not Provided"}</div>
		                </div>
		                
		                <!-- Position -->
		                <div class="admin_row">
		                	<span class="admin_row_label">Roles and Responsibilities</span>
		              		<div class="field" id="emp_description">${(position.remit?html)!"Not Provided"}</div>
		                </div>
		                
		                <!-- Language -->
		                <div class="admin_row">
		                    <span class="admin_row_label">Language of work</span>
		                    <div class="field" id="empl_language">${(position.language.name?html)!"Not Provided"}</div>
		               	</div>  
		                             
		                <!-- Start date -->
		                <div class="admin_row">
		                    <span class="admin_row_label">Start Date</span>
		                    <div class="field" id="emp_startDate">${(position.startDate?string('dd-MMM-yyyy'))!"Not Provided"}</div>
		                </div>
		                <div class="admin_row">
		                    <span class="admin_row_label">Is this your current position?</span>
		                    <div class="field" id="emp_current"><#if position.current>Yes<#else>No</#if> </div>
		               	</div>
		                
		                <!-- End date -->
		                <div class="admin_row">
		                    <span class="admin_row_label">End date</span>
							<div class="field" id="emp_endDate">${(position.endDate?string('dd-MMM-yyyy'))!"Not Provided"}</div>
		               	</div>
		                
					</div>
			    
			    </#list>
			    
			<#else>
			
	                <!-- Rendering part - Start -->
		        	<div class="row-group">
		                     
		                <div class="row">
		                	<span class="admin_header">Employment</span>                 
		                    <div class="field">Not Provided</div>
		                </div>
		            	
					</div>
			
			</#if>
        	
			<div class="buttons">
                <button class="blue" id="positionCloseButton" type="button">Close</button>
            </div>

		</form>
	</div>
	
	<script type="text/javascript" src="<@spring.url '/design/default/js/application/staff/employmentPosition.js'/>"></script>