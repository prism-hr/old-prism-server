<#if model.applicationForm.employmentPositions?has_content>
	<#assign hasEmploymentPositions = true>
<#else>
	<#assign hasEmploymentPositions = false>
</#if>

<#import "/spring.ftl" as spring />
	
	<h2 class="empty">
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
	            
	            	<#list model.applicationForm.employmentPositions as position>
		            	<tr>
		                    <td><a class="row-arrow" name="positionEditButton" id="position_${position.id}">-</a></td>
		                    <td>${position.position_title}</td>
		                    <td>${position.position_startDate?string('dd-MMM-yyyy')}</td>
		                    <td>${position.position_endDate?string('dd-MMM-yyyy')}</td>
		                </tr>
		            </#list>
				</tbody>
				
			</table>
		</#if>
        
        <form>
        	
        	<div>
                
            	<!-- Employer (company name) -->
                <div class="row">
                	<span class="label">Employer</span>
                    <span class="hint"></span>
                    <div class="field">
                    	${model.employmentPosition.position_employer!}
                   	</div>
                </div>
                
                <!-- Position -->
                <div class="row">
                	<span class="label">Position</span>
                    <span class="hint"></span>
                    <div class="field">
						${model.employmentPosition.position_title!}
                    </div>
                </div>
                
                <!-- Remit (job description) -->
                <div class="row">
                    <span class="label">Remit</span>
                    <span class="hint"></span>
                    <div class="field">
                    	${model.employmentPosition.position_remit!}
                    </div>
             	</div>
                
                <!-- Start date -->
                <div class="row">
                    <span class="label">Start Date</span>
                    <span class="hint"></span>
                    <div class="field">
                    	${(model.employmentPosition.position_startDate?string('dd-MMM-yyyy'))!}
                    </div>
                </div>
                
                <!-- End date -->
                <div class="row">
                    <span class="label">End date</span>
                    <span class="hint"></span>
                    <div class="field">
						${(model.employmentPosition.position_endDate?string('dd-MMM-yyyy'))!}
                    </div>
               	</div>
                
                <!-- Language -->
                <div class="row">
                    <span class="label">Language of work</span>
                    <span class="hint"></span>
                    <div class="field">
						${model.employmentPosition.position_language!}
                    </div>
               	</div>
                
                <!-- Document -->
                <div class="row">
                	<!-- Add freemarker expression to get the content -->
              	</div>

			</div>

			<div class="buttons">
                <button class="blue" type="button">Close</button>
            </div>

		</form>
	</div>
	
<script type="text/javascript" src="<@spring.url '/design/default/js/application/employmentPosition.js'/>"></script>