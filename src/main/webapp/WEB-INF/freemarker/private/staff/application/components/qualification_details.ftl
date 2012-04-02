<#if model.applicationForm.qualifications?has_content>
	<#assign hasQualifications = true>
<#else>
	<#assign hasQualifications = false>
</#if> 
 
 <#import "/spring.ftl" as spring />
			
			<h2 id="qualifications-H2" class="empty">
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
				                  	<td><a class="row-arrow" id="qualification_${qualification.id?string('#######')}" name ="editQualificationLink">-</a></td>
				                  	<td>${qualification.qualificationType}</td>
				                  	<td>${qualification.qualificationGrade?html}</td>
				                  	<td>${qualification.qualificationInstitution?html}</td>
				                  	<td>${(qualification.qualificationAwardDate?string('dd-MMM-yyyy'))!}</td>
				                  	
				                  	     
                                <input type="hidden" id="${qualification.id?string('#######')}_qualificationIdDP" value="${qualification.id?string('#######')}"/>
                                <input type="hidden" id="${qualification.id?string('#######')}_qualificationInstitutionDP" value="${(qualification.qualificationInstitution?html)!}"/> 
                                <input type="hidden" id="${qualification.id?string('#######')}_qualificationProgramNameDP" value="${(qualification.qualificationProgramName?html)!}"/> 
                                <input type="hidden"  id="${qualification.id?string('#######')}_qualificationStartDateDP" value="${(qualification.qualificationStartDate?string('dd-MMM-yyyy'))!}"/> 
                                <input type="hidden"  id="${qualification.id?string('#######')}_qualificationLanguageDP" value="${qualification.qualificationLanguage.id!}"/> 
                                <input type="hidden"  id="${qualification.id?string('#######')}_qualificationLevelDP" value="${(qualification.qualificationLevel?html)!}"/> 
                                <input type="hidden"  id="${qualification.id?string('#######')}_qualificationTypeDP" value="${(qualification.qualificationType?html)!}"/> 
                                <input type="hidden"  id="${qualification.id?string('#######')}_qualificationGradeDP" value="${(qualification.qualificationGrade?html)!}"/> 
                                <input type="hidden"  id="${qualification.id?string('#######')}_qualificationScoreDP" value="${(qualification.qualificationScore?html)!}"/> 
                                <input type="hidden"  id="${qualification.id?string('#######')}_qualificationAwardDateDP" value="${(qualification.qualificationAwardDate?string('dd-MMM-yyyy'))!}"/> 
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
		                     <input readonly="readonly" id="qualificationInstitution" class="full" type="text" placeholder="e.g. UCL" 
                                                                value="${(model.qualification.qualificationInstitution?html)!}" />
		                    </div>
	                  	</div>
	                  
	                  	<!-- Name (of programme) -->
	                	<div class="row">
		                  	<span class="label">Programme</span>
		                    <span class="hint" data-desc="Tooltip demonstration."></span>
		                    <div class="field">
		                    <input readonly="readonly" id="qualificationProgramName" class="full" type="text" placeholder="e.g. Civil Engineering" 
                                                                value="${(model.qualification.qualificationProgramName?html)!}"/>
		                    </div>
	             		</div>
	                  
	                  	<!-- Start date -->
	                	<div class="row">
		                  	<span class="label">Start Date</span>
		                    <span class="hint" data-desc="Tooltip demonstration."></span>
		                    <div class="field">
		                    <input id="qualificationStartDate" class="half date" type="text" 
                                                                value="${(model.qualification.qualificationStartDate?string('dd-MMM-yyyy'))!}" 
                                            disabled="disabled">
                            </input>
		                    </div>
	                 	</div>
	                
                  		<!-- Language (in which programme was undertaken) -->
                  		<div class="row">
                    		<span class="label">Language of Study</span>
                    		<span class="hint" data-desc="Tooltip demonstration."></span>
                    		<div class="field">
                    		  <select class="full" id="qualificationLanguage" name="qualificationLanguage" value="${(model.qualification.qualificationLanguage?html)!}"
                                                disabled="disabled">
                                <option value="">Select...</option>
                                    <#list model.languages as language>
                                        <option value="${language.id?string('#######')}"  <#if model.qualification.qualificationLanguage?? && model.qualification.qualificationLanguage == language.id> selected="selected"</#if>>${language.name}</option>
                                    </#list>
                                </select>
                    		</div>
                  		</div>
                  
                  		<!-- Qualification level -->
	                  	<div class="row">
	                    	<span class="label">Level</span>
	                    	<span class="hint" data-desc="Tooltip demonstration."></span>
	                    	<div class="field">
	                    	  <select name="qualificationLevel" id="qualificationLevel" value="${(model.qualification.qualificationLevel?html)!}"
                                                disabled="disabled">
                                     <option value="">Select...</option>
                                     <#list model.qualificationLevels as level>
                                         <option value="${level}"
                                         <#if model.qualification.qualificationLevel?? &&  model.qualification.qualificationLevel == level >
                                        selected="selected"
                                        </#if>
                                >${level.displayValue}</option>               
                                    </#list>
                                </select>
	                    	</div>
	                  	</div>

                  		<!-- Qualification type -->
                  		<div class="row">
                    		<span class="label">Type</span>
                    		<span class="hint" data-desc="Tooltip demonstration."></span>
                    		<div class="field">
                    		 <input readonly="readonly" id="qualificationType" class="full" type="text" 
                                                                value="${(model.qualification.qualificationType?html)!}"/>
                    		</div>
                  		</div>

                  		<!-- Qualification grade -->
                  		<div class="row">
                    		<span class="label">Grade</span>
                    		<span class="hint" data-desc="Tooltip demonstration."></span>
                    		<div class="field">
                    		 <input readonly="readonly" id="qualificationGrade" class="full" type="text" placeholder="e.g. 2.1, Distinction"
                                                                value="${(model.qualification.qualificationGrade?html)!}"/>
                    		</div>
                  		</div>

                  		<!-- Qualification score -->
                  		<div class="row">
                    		<span class="label">Score</span>
                    		<span class="hint" data-desc="Tooltip demonstration."></span>
                    		<div class="field">
                    		 <input readonly="readonly" id="qualificationScore" class="full" type="text" placeholder="e.g. 114"
                                                                value="${(model.qualification.qualificationScore?html)!}"/>
                    		</div>
                  		</div>
                  
                  		<!-- Award date -->
                  		<div class="row">
                    		<span class="label">Award Date</span>
                    		<span class="hint" data-desc="Tooltip demonstration."></span>
                    		<div class="field">
                    		<input type="text" class="half date" id="qualificationAwardDate" name="qualificationAwardDate" 
                                                value="${(model.qualification.qualificationAwardDate?string('dd-MMM-yyyy'))!}"
                                            disabled="disabled">
                            </input>
                    		</div>
                  		</div>

                  		<!-- Attachment / supporting document 
                  		<div class="row">
                    		<span class="label">Supporting Document</span>
                    		<span class="hint" data-desc="Tooltip demonstration."></span>
                    		<div class="field">
                    		</div>  
                  		</div> -->
	                
	                </div>

		        	<div class="buttons">
		                <button class="blue" id="qualificationsCloseButton" type="button">Close</button>
	                </div>

			  </form>
		</div>
		
		<script type="text/javascript" src="<@spring.url '/design/default/js/application/qualifications.js'/>"></script>
 