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
	
		<#if hasEmploymentPositions>
	    	<table class="existing">
	        	<colgroup>
	            	<col style="width: 30px" />
	                <col />
	                <col style="width: 140px" />
	                <col style="width: 140px" />

				</colgroup>
	            
	            <thead>
	            	<tr>
	                	<th colspan="2">Position</th>
	                    <th>From</th>
	                    <th>To</th>
	           
	                </tr>
				</thead>
	            
	            <tbody>
	            
	            	<#list applicationForm.employmentPositions as position>
		            	<tr>
		                    <td><a class="row-arrow" name="positionEditButton" id="position_${position.id?string('#######')}">-</a></td>
		                    <td>${position.position!}</td>
		                    <td>${(position.startDate?string('dd-MMM-yyyy'))!}</td>
		                    <td>${(position.endDate?string('dd-MMM-yyyy'))!}</td>
		                    
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
						
		                </tr>
		            </#list>
				</tbody>
				
			</table>
		</#if>
		
		  <input type="hidden" id="positionId" name="positionId" value=""/>
        
        <form>
        	
        	<div>
                        
                <div class="row">
                	<span class="label">Country</span>                 
      
                     <div class="field" id="emp_country">&nbsp; </div>
         
                </div>
            	<!-- Employer (company name) -->
                <div class="row">
                	<span class="label">Employer Name</span>                 
                   <div class="field" id="emp_name">&nbsp; </div>
                </div>
                
                <div class="row">
                	<span class="label">Employer Address</span>                 
                   <div class="field" id="emp_address">&nbsp; </div>
                </div>
                
                <!-- Position -->
                <div class="row">
                	<span class="label">Position</span>
       
              		<div class="field" id="emp_position">&nbsp; </div>
                </div>
                <!-- Position -->
                <div class="row">
                	<span class="label">Roles and Responsibilities</span>
       
              		<div class="field" id="emp_description">&nbsp; </div>
                </div>
                 <!-- Language -->
                <div class="row">
                    <span class="label">Language of work</span>
                       <div class="field" id="empl_language">&nbsp; </div>
               	</div>  
                             
                <!-- Start date -->
                <div class="row">
                    <span class="label">Start Date</span>
                                     <div class="field" id="emp_startDate">&nbsp; </div>
                </div>
                <div class="row">
                       <span class="label">Is this your current position?</span>
    
                                      <div class="field" id="emp_current">&nbsp; </div>
               		
               			 </div>
                
                <!-- End date -->
                <div class="row">
                    <span class="label">End date</span>
                  
                                      <div class="field" id="emp_endDate">&nbsp; </div>
               	</div>
                
             
			</div>

			<div class="buttons">
                <button class="blue" id="positionCloseButton" type="button">Close</button>
            </div>

		</form>
	</div>
	
<script type="text/javascript" src="<@spring.url '/design/default/js/application/staff/employmentPosition.js'/>"></script>