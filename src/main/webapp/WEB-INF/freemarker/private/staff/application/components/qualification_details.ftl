<#if model.applicationForm.qualifications?has_content>
	<#assign hasQualifications = true>
<#else>
	<#assign hasQualifications = false>
</#if> 
 
 <#import "/spring.ftl" as spring />
			
			<h2 class="empty">
				<span class="left"></span><span class="right"></span><span class="status"></span>
				Qualifications
        	</h2>
            <div>
            
            	<#if hasQualifications>
            
	            	<table class="existing">
		              	
		              	<colgroup>
		                	<col style="width: 30px" />
		                	<col />
		                	<col style="width: 80px" />
		                	<col />
		                	<col />
		                	<col style="width: 30px" />
		                </colgroup>
		              	
		              	<thead>
		                	<tr>
		                  	<th colspan="2">Qualification</th>
		                    <th>Grade</th>
		                    <th>Awarding Body</th>
		                    <th>Date Completed</th>
		                    <th>&nbsp;</th>
		                  </tr>
		                </thead>
		                
		                <tbody>
		                
		                	<#list model.applicationForm.qualifications as qualification>
			                	<tr>
				                  	<td><a class="row-arrow" id="qualification_${qualification.id}" name ="editQualificationLink">-</a></td>
				                  	<td>${qualification.qualificationType}</td>
				                  	<td>${qualification.qualificationGrade}</td>
				                  	<td>${qualification.qualificationInstitution}</td>
				                  	<td>${(qualification.qualificationAwardDate?string('dd-MMM-yyyy'))!}</td>
			                  	</tr>
							</#list>
										
		                </tbody>
	              	</table>
              		
              	</#if>
              
              	<input type="hidden" id="qualificationId" name="qualificationId"/>
              	
              	<form>

	              	<div>
	                  
	                  	<!-- Provider -->
	                	<div class="row">
		                  	<span class="label">Provider</span>
		                    <span class="hint" data-desc="Tooltip demonstration."></span>
		                    <div class="field">
								${model.qualification.qualificationInstitution!}		                    	
		                    </div>
	                  	</div>
	                  
	                  	<!-- Name (of programme) -->
	                	<div class="row">
		                  	<span class="label">Programme</span>
		                    <span class="hint" data-desc="Tooltip demonstration."></span>
		                    <div class="field">
		                    	${model.qualification.qualificationProgramName!}
		                    </div>
	             		</div>
	                  
	                  	<!-- Start date -->
	                	<div class="row">
		                  	<span class="label">Start Date</span>
		                    <span class="hint" data-desc="Tooltip demonstration."></span>
		                    <div class="field">
		                    	${(model.qualification.qualificationStartDate?string('dd-MMM-yyyy'))!}
		                    </div>
	                 	</div>
	                
                  		<!-- Language (in which programme was undertaken) -->
                  		<div class="row">
                    		<span class="label">Language of Study</span>
                    		<span class="hint" data-desc="Tooltip demonstration."></span>
                    		<div class="field">
                    			${model.qualification.qualificationLanguage!}
                    		</div>
                  		</div>
                  
                  		<!-- Qualification level -->
	                  	<div class="row">
	                    	<span class="label">Level</span>
	                    	<span class="hint" data-desc="Tooltip demonstration."></span>
	                    	<div class="field">
	                    		${model.qualification.qualificationLevel!}
	                    	</div>
	                  	</div>

                  		<!-- Qualification type -->
                  		<div class="row">
                    		<span class="label">Type</span>
                    		<span class="hint" data-desc="Tooltip demonstration."></span>
                    		<div class="field">
                    			${model.qualification.qualificationType!}
                    		</div>
                  		</div>

                  		<!-- Qualification grade -->
                  		<div class="row">
                    		<span class="label">Grade</span>
                    		<span class="hint" data-desc="Tooltip demonstration."></span>
                    		<div class="field">
                    			${model.qualification.qualificationGrade!}
                    		</div>
                  		</div>

                  		<!-- Qualification score -->
                  		<div class="row">
                    		<span class="label">Score</span>
                    		<span class="hint" data-desc="Tooltip demonstration."></span>
                    		<div class="field">
                    			${model.qualification.qualificationScore!}
                    		</div>
                  		</div>
                  
                  		<!-- Award date -->
                  		<div class="row">
                    		<span class="label">Award Date</span>
                    		<span class="hint" data-desc="Tooltip demonstration."></span>
                    		<div class="field">
                    			${(model.qualification.qualificationAwardDate?string('dd-MMM-yyyy'))!}
                    		</div>
                  		</div>

                  		<!-- Attachment / supporting document 
                  		<div class="row">
                    		<span class="label">Supporting Document</span>
                    		<span class="hint" data-desc="Tooltip demonstration."></span>
                    		<div class="field">
                    			<!-- Add freemarker expression to pull contnet for supporting document -->
                    		</div>  
                  		</div> -->
	                
	                </div>

		        	<div class="buttons">
		                <button class="blue" type="button">Close</button>
	                </div>

			  </form>
		</div>
		
		<script type="text/javascript" src="<@spring.url '/design/default/js/application/qualifications.js'/>"></script>
 