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
	                <col style="width: 30px" />
				</colgroup>
	            
	            <thead>
	            	<tr>
	                	<th colspan="2">Position</th>
	                    <th>From</th>
	                    <th>To</th>
	                    <th>&nbsp;</th>
	                </tr>
				</thead>
	            
	            <tbody>
	            
	            	<#list applicationForm.employmentPositions as position>
		            	<tr>
		                    <td><a class="row-arrow" name="positionEditButton" id="position_${position.id?string('#######')}">-</a></td>
		                    <td>${position.position_title!}</td>
		                    <td>${(position.position_startDate?string('dd-MMM-yyyy'))!}</td>
		                    <td>${(position.position_endDate?string('dd-MMM-yyyy'))!}</td>
		                    
		                    <input type="hidden" id="${position.id?string('#######')}_positionId" value="${position.id?string('#######')}"/>
                            <input type="hidden" id="${position.id?string('#######')}_employer" value="${(position.position_employer?html)!}"/>
                            <input type="hidden" id="${position.id?string('#######')}_remit" value="${(position.position_remit?html)!}"/>
                            <input type="hidden" id="${position.id?string('#######')}_language" value="${(position.position_language?html)!}"/>
                            <input type="hidden" id="${position.id?string('#######')}_positionTitle" value="${position.position_title}"/>
                            <input type="hidden" id="${position.id?string('#######')}_positionStartDate" value="${(position.position_startDate?string('dd-MMM-yyyy'))!}"/>
                            <input type="hidden" id="${position.id?string('#######')}_positionEndDate" value="${(position.position_endDate?string('dd-MMM-yyyy'))!}"/>
		               		<input type="hidden"  id="${position.id?string('#######')}_positionCompleted" value="${position.completed}"/> 
						
		                </tr>
		            </#list>
				</tbody>
				
			</table>
		</#if>
		
		  <input type="hidden" id="positionId" name="positionId"/>
        
        <form>
        	
        	<div>
                
            	<!-- Employer (company name) -->
                <div class="row">
                	<span class="label">Employer</span>                 
                    <div class="field">
                                    <div class="field">&nbsp; </div>
                   	</div>
                </div>
                
                <!-- Position -->
                <div class="row">
                	<span class="label">Position</span>
       
                    <div class="field">
                                     <div class="field">&nbsp; </div>
                    </div>
                </div>
                
                <!-- Remit (job description) -->
                <div class="row">
                    <span class="label">Remit</span>

                                      <div class="field">&nbsp; </div>
             	</div>
                
                <!-- Start date -->
                <div class="row">
                    <span class="label">Start Date</span>
                                     <div class="field">&nbsp; </div>
                </div>
                <div class="row">
                       <span class="label">Current position</span>
    
                                      <div class="field">&nbsp; </div>
               		
               			 </div>
                
                <!-- End date -->
                <div class="row">
                    <span class="label">End date</span>
                  
                                      <div class="field">&nbsp; </div>
               	</div>
                
                <!-- Language -->
                <div class="row">
                    <span class="label">Language of work</span>
                                    <div class="field">&nbsp; </div>
               	</div>
                
                <!-- Document -->
                <div class="row">
                	<!-- Add freemarker expression to get the content -->
              	</div>

			</div>

			<div class="buttons">
                <button class="blue" id="positionCloseButton" type="button">Close</button>
            </div>

		</form>
	</div>
	
<script type="text/javascript" src="<@spring.url '/design/default/js/application/employmentPosition.js'/>"></script>