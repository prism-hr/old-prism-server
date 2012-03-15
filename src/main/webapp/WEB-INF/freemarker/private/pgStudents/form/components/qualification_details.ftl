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
				                  	<td><a class="row-arrow" id="qualification_${qualification.id!}" name ="editQualificationLink">-</a></td>
				                  	<td>${qualification.qualificationType!}</td>
				                  	<td>${qualification.qualificationGrade!}</td>
				                  	<td>${qualification.qualificationInstitution!}</td>
				                  	<td>${(qualification.qualificationAwardDate?string('dd-MMM-yyyy'))!}</td>
				                  	<td><a class="button-delete" href="#">delete</a></td>
			                  	</tr>
			                  	
                             	<input type="hidden" id="${qualification.id!}_qualificationIdDP" value="${qualification.id!}"/>
                             	<input type="hidden" id="${qualification.id!}_qualificationInstitutionDP" value="${qualification.qualificationInstitution!}"/> 
                           		<input type="hidden" id="${qualification.id!}_qualificationProgramNameDP" value="${qualification.qualificationProgramName!}"/> 
                             	<input type="hidden"  id="${qualification.id!}_qualificationStartDateDP" value="${(qualification.qualificationStartDate?string('dd-MMM-yyyy'))!}"/> 
                            	<input type="hidden"  id="${qualification.id!}_qualificationLanguageDP" value="${qualification.qualificationLanguage!}"/> 
                            	<input type="hidden"  id="${qualification.id!}_qualificationLevelDP" value="${qualification.qualificationLevel!}"/> 
                             	<input type="hidden"  id="${qualification.id!}_qualificationTypeDP" value="${qualification.qualificationType!}"/> 
                             	<input type="hidden"  id="${qualification.id!}_qualificationGradeDP" value="${qualification.qualificationGrade!}"/> 
                             	<input type="hidden"  id="${qualification.id!}_qualificationScoreDP" value="${qualification.qualificationScore!}"/> 
                             	<input type="hidden"  id="${qualification.id!}_qualificationAwardDateDP" value="${(qualification.qualificationAwardDate?string('dd-MMM-yyyy'))!}"/> 
			                  	
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
		                    	<#if !model.applicationForm.isSubmitted()>
		                    	<input id="qualificationInstitution" class="full" type="text" placeholder="e.g. UCL" 
		                    									value="${model.qualification.qualificationInstitution!}" />
			                    <#if model.hasError('qualificationInstitution')>                    		
                    				<span class="invalid"><@spring.message  model.result.getFieldError('qualificationInstitution').code /></span>                    		
                    			</#if>
                    			<#else>
                    			     <input readonly="readonly" id="qualificationInstitution" class="full" type="text" placeholder="e.g. UCL" 
                                                                value="${model.qualification.qualificationInstitution!}" />
                    			</#if>
			                    									
		                    </div>
	                  	</div>
	                  
	                  	<!-- Name (of programme) -->
	                	<div class="row">
		                  	<span class="label">Programme</span>
		                    <span class="hint" data-desc="Tooltip demonstration."></span>
		                    <div class="field">
		                    	<#if !model.applicationForm.isSubmitted()>
		                    	<input id="qualificationProgramName" class="full" type="text" placeholder="e.g. Civil Engineering" 
		                    									value="${model.qualification.qualificationProgramName!}"/>
		       					<#if model.hasError('qualificationProgramName')>
		       						<span style="color:red;"><@spring.message  model.result.getFieldError('qualificationProgramName').code /></span>
		       					</#if>
		       					<#else>
		       					  <input readonly="readonly" id="qualificationProgramName" class="full" type="text" placeholder="e.g. Civil Engineering" 
                                                                value="${model.qualification.qualificationProgramName!}"/>
		       					</#if>
		       					
		                    </div>
	             		</div>
	                  
	                  	<!-- Start date -->
	                	<div class="row">
		                  	<span class="label">Start Date</span>
		                    <span class="hint" data-desc="Tooltip demonstration."></span>
		                    <div class="field">
			                    
			                    <input id="qualificationStartDate" class="half date" type="text" 
			                    								value="${(model.qualification.qualificationStartDate?string('dd-MMM-yyyy'))!}" 
			                    								<#if model.applicationForm.isSubmitted()>
                                            disabled="disabled"
                                            </#if>>
                            </input>
			                    <#if model.hasError('qualificationStartDate')>
			                    	<span class="invalid"><@spring.message  model.result.getFieldError('qualificationStartDate').code /></span>
			                    </#if>
			                    
		                    </div>
	                 	</div>

	                
                  		<!-- Language (in which programme was undertaken) -->
                  		<div class="row">
                    		<span class="label">Language of Study</span>
                    		<span class="hint" data-desc="Tooltip demonstration."></span>
                    		<div class="field">
                      			<#if !model.applicationForm.isSubmitted()>
                      			<select class="full" id="qualificationLanguage" name="qualificationLanguage" value="${model.qualification.qualificationLanguage!}"
                      			 <#if model.applicationForm.isSubmitted()>
                                                disabled="disabled"
                                            </#if>>
                        		<option value="">Select...</option>
                         			<#list model.languages as language>
                         				<option value="${language.id}">${language.name}</option>
                         			</#list>
                      			</select>
								<#if model.hasError('qualificationLanguage')>                    		
                    				<span class="invalid"><@spring.message  model.result.getFieldError('qualificationLanguage').code /></span>                    		
                    			</#if>
                    			<#else>
                    			 <input readonly="readonly" id="qualificationLanguage" class="full" type="text" 
                                                                value="${model.qualification.qualificationLanguage!}"/>
                    			</#if>

                    		</div>
                  		</div>
                  
                  		<!-- Qualification level -->
	                  	<div class="row">
	                    	<span class="label">Level</span>
	                    	<span class="hint" data-desc="Tooltip demonstration."></span>
	                    	<div class="field">
	                    		<select name="qualificationLevel" id="qualificationLevel" value="${model.qualification.qualificationLevel!}"
	                    		 <#if model.applicationForm.isSubmitted()>
                                                disabled="disabled"
                                            </#if>>
                        			 <option value="">Select...</option>
                        			 <#list model.qualificationLevels as level>
                             			 <option value="${level}"
                             			 <#if model.qualification.qualificationLevel?? &&  model.qualification.qualificationLevel == level >
                                        selected="selected"
                                        </#if>
                                >${level.displayValue}</option>               
                        			</#list>
                      			</select>
								<#if model.hasError('qualificationLevel')>                    		
                    				<span class="invalid"><@spring.message  model.result.getFieldError('qualificationLevel').code /></span>                    		
                    			</#if>
	                    	</div>
	                  	</div>

                  		<!-- Qualification type -->
                  		<div class="row">
                    		<span class="label">Type</span>
                    		<span class="hint" data-desc="Tooltip demonstration."></span>
                    		<div class="field">
                    		<#if !model.applicationForm.isSubmitted()>
                      			<input id="qualificationType" class="full" type="text" 
			                    								value="${model.qualification.qualificationType!}"/>
								<#if model.hasError('qualificationType')>                    		
                    				<span class="invalid"><@spring.message  model.result.getFieldError('qualificationType').code /></span>                    		
                    			</#if>
                    			<#else>
                    			 <input readonly="readonly" id="qualificationType" class="full" type="text" 
                                                                value="${model.qualification.qualificationType!}"/>
                    			</#if>
                    		</div>
                  		</div>

                  		<!-- Qualification grade -->
                  		<div class="row">
                    		<span class="label">Grade</span>
                    		<span class="hint" data-desc="Tooltip demonstration."></span>
                    		<div class="field">
                    		<#if !model.applicationForm.isSubmitted()>
                      			<input id="qualificationGrade" class="full" type="text" placeholder="e.g. 2.1, Distinction"
			                    								value="${model.qualification.qualificationGrade!}"/>
								<#if model.hasError('qualificationGrade')>                    		
                    				<span class="invalid"><@spring.message  model.result.getFieldError('qualificationGrade').code /></span>                    		
                    			</#if>
                    			<#else>
                    			     <input readonly="readonly" id="qualificationGrade" class="full" type="text" placeholder="e.g. 2.1, Distinction"
                                                                value="${model.qualification.qualificationGrade!}"/>
                    			</#if>
                    		</div>
                  		</div>

                  		<!-- Qualification score -->
                  		<div class="row">
                    		<span class="label">Score</span>
                    		<span class="hint" data-desc="Tooltip demonstration."></span>
                    		<div class="field">
                    		<#if !model.applicationForm.isSubmitted()>
                      			<input id="qualificationScore" class="full" type="text" placeholder="e.g. 114"
			                    								value="${model.qualification.qualificationScore!}"/>
								<#if model.hasError('qualificationScore')>                    		
                    				<span class="invalid"><@spring.message  model.result.getFieldError('qualificationScore').code /></span>                    		
                    			</#if>
                    			<#else>
                    			 <input readonly="readonly" id="qualificationScore" class="full" type="text" placeholder="e.g. 114"
                                                                value="${model.qualification.qualificationScore!}"/>
                    			</#if>
                    		</div>
                  		</div>
                  
                  		<!-- Award date -->
                  		<div class="row">
                    		<span class="label">Award Date</span>
                    		<span class="hint" data-desc="Tooltip demonstration."></span>
                    		<div class="field">
                    			<input type="text" class="half date" id="qualificationAwardDate" name="qualificationAwardDate" 
                    							value="${(model.qualification.qualificationAwardDate?string('dd-MMM-yyyy'))!}"
                    							<#if model.applicationForm.isSubmitted()>
                                            disabled="disabled"
                            </#if>>
                            </input>
                    		</div>
                  		</div>


                  		<!-- Attachment / supporting document 
                  		<div class="row">
                    		<span class="label">Supporting Document</span>
                    		<span class="hint" data-desc="Tooltip demonstration."></span>
                    		<div class="field">
                      			<input id="" class="full" type="text" value="" />
                      				<a class="button" href="#">Browse</a>
                      				<a class="button" href="#">Upload</a>
                      				<a class="button" href="#">Add Document</a>
                    		</div>  
                  		</div> -->
	                
	                </div>

		        	<div class="buttons">
		        	<#if !model.applicationForm.isSubmitted()>
		            	<a class="button" id="qualificationCancelButton" name="qualificationCancelButton">Cancel</a>
		                <button class="blue" type="button" id="qualificationSaveCloseButton"  name="id="qualificationSaveCloseButton"" value="close">Save and Close</button>
		                <button id="qualificationsSaveButton" class="blue" type="button" value="add">Save and Add</button>
		                <#else>
                            <a id="qualificationsCloseButton"class="button blue">Close</a>   
                    </#if>  
	                </div>

			  </form>
		</div>
<script type="text/javascript" src="<@spring.url '/design/default/js/libraries.js'/>"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/application/common.js'/>"></script>		
		<script type="text/javascript" src="<@spring.url '/design/default/js/application/qualifications.js'/>"></script>
 