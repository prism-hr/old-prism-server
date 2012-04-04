<#if applicationForm.qualifications?has_content>
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
            
            	<#list applicationForm.qualifications as qualification>
                	<tr>
	                  	<td><a class="row-arrow" id="qualification_${qualification.id?string('#######')}" name ="editQualificationLink">-</a></td>
	                  	<td>${(qualification.qualificationType?html)!}</td>
	                  	<td>${(qualification.qualificationGrade?html)!}</td>
	                  	<td>${(qualification.qualificationInstitution?html)!}</td>
	                  	<td>${(qualification.qualificationAwardDate?string('dd-MMM-yyyy'))!}</td>
	                  	  <td>
	                  	  	   <#if !applicationForm.isSubmitted()>
			                  	<form method="Post" action="<@spring.url '/deleteentity/qualification'/>">
		                			<input type="hidden" name="id" value="${qualification.id?string('#######')}"/>		                		
		                			<a name="deleteButton" class="button-delete">delete</a>
		                		</form>
		                		</#if>
			        		</td>
                  	</tr>
                  	
                 	<input type="hidden" id="${qualification.id?string('#######')}_qualificationIdDP" value="${qualification.id?string('#######')}"/>
                 	<input type="hidden" id="${qualification.id?string('#######')}_qualificationInstitutionDP" value="${(qualification.qualificationInstitution?html)!}"/> 
               		<input type="hidden" id="${qualification.id?string('#######')}_qualificationSubjectDP" value="${(qualification.qualificationSubject?html)!}"/> 
                 	<input type="hidden"  id="${qualification.id?string('#######')}_qualificationStartDateDP" value="${(qualification.qualificationStartDate?string('dd-MMM-yyyy'))!}"/> 
                	<input type="hidden"  id="${qualification.id?string('#######')}_qualificationLanguageDP" value="${qualification.qualificationLanguage.id?string('#######')!}"/> 
                	<input type="hidden"  id="${qualification.id?string('#######')}_qualificationLevelDP" value="${(qualification.qualificationLevel?html)!}"/> 
                 	<input type="hidden"  id="${qualification.id?string('#######')}_qualificationTypeDP" value="${(qualification.qualificationType?html)!}"/> 
                 	<input type="hidden"  id="${qualification.id?string('#######')}_qualificationGradeDP" value="${(qualification.qualificationGrade?html)!}"/> 
                 	<input type="hidden"  id="${qualification.id?string('#######')}_qualificationScoreDP" value="${(qualification.qualificationScore?html)!}"/> 
                 	<input type="hidden"  id="${qualification.id?string('#######')}_qualificationAwardDateDP" value="${(qualification.qualificationAwardDate?string('dd-MMM-yyyy'))!}"/> 
                 	<input type="hidden"  id="${qualification.id?string('#######')}_qualificationCompleted" value="${qualification.completed}"/> 
				</#list>
							
            </tbody>
      	</table>
  		
  	</#if>
  
  	<input type="hidden" id="qualificationId" name="qualificationId" value="${(model.qualification.id?html)!}"/>
  	
  	<form>

      	<div>
          
          	<!-- Provider -->
        	<div class="row">
              	<span class="label">Institution<em>*</em></span>
                <span class="hint" data-desc="<@spring.message 'education.qualifications.institutionName'/>"></span>
                <div class="field">
                	<#if !applicationForm.isSubmitted()>
                	<input id="qualificationInstitution" class="full" type="text" placeholder="e.g. UCL"  value="${(qualification.qualificationInstitution?html)!}" />
                	 <@spring.bind "qualification.qualificationInstitution" /> 
                	 <#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>
                	
        			<#else>
        			     <input readonly="readonly" id="qualificationInstitution" class="full" type="text" placeholder="e.g. UCL" 
                                                    value="${(qualification.qualificationInstitution?html)!}" />
        			</#if>
                    									
                </div>
          	</div>
          
       		<div class="row">
        		<span class="label">Institution Country<em>*</em></span>
        		<span class="hint" data-desc="<@spring.message 'education.qualifications.institutionCountry'/>"></span>
        		<div class="field">
          			<select class="full" id="institutionCountry" name="institutionCountry"
          			 <#if applicationForm.isSubmitted()>
                                    disabled="disabled"
                                </#if>>
            		<option value="">Select...</option>
             			<#list countries as country>
             				<option value="${country.id?string('#######')}"  <#if qualification.institutionCountry?? && qualification.institutionCountry.id == country.id> selected="selected"</#if>>${country.name?html}</option>
             			</#list>
          			</select>
					 <@spring.bind "qualification.institutionCountry" /> 
                	 <#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>
        		</div>
      		</div>
      
          	<!-- Name (of programme) -->
        	<div class="row">
              	<span class="label">Title / subject<em>*</em></span>
               <span class="hint" data-desc="<@spring.message 'education.qualifications.subject'/>"></span>
                <div class="field">
                	<#if !applicationForm.isSubmitted()>
                	<input id="qualificationSubject" class="full" type="text" placeholder="e.g. Civil Engineering" 
                									value="${(qualification.qualificationSubject?html)!}"/>
   					 <@spring.bind "qualification.qualificationSubject" /> 
                	 <#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>
   					<#else>
   					  <input readonly="readonly" id="qualificationSubject" class="full" type="text" placeholder="e.g. Civil Engineering" 
                                                    value="${(qualification.qualificationSubject?html)!}"/>
   					</#if>
   					
                </div>
     		</div>
          
          	<!-- Start date -->
        	<div class="row">
              	<span class="label">Start Date<em>*</em></span>
                <span class="hint" data-desc="<@spring.message 'education.qualifications.startDate'/>"></span>
                <div class="field">
                    
                    <input id="qualificationStartDate" class="half date" type="text" 
                    								value="${(qualification.qualificationStartDate?string('dd-MMM-yyyy'))!}" 
                    								<#if applicationForm.isSubmitted()>
                                disabled="disabled"
                                </#if>>
                </input>
               		<@spring.bind "qualification.qualificationStartDate" /> 
                	 <#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>
                </div>
         	</div>
         	
			<div class="row">
           <span class="label">Has been awarded?</span>
           	<span class="hint" data-desc="<@spring.message 'education.qualifications.hasBeenAwarded'/>"></span>
           		<input type="checkbox" name="currentQualificationCB" id="currentQualificationCB"
           		<#if applicationForm.isSubmitted()>
           			<#if model.qualification.isQualificationCompleted()>
                              checked
                    </#if>
           			<#if model.applicationForm.isSubmitted()>
                              disabled="disabled"
                    </#if>
                 </#if>
                    />
           		<input type="hidden" name="currentQualification" id="currentQualification"/>
   			 </div>
        
      		<!-- Language (in which programme was undertaken) -->
      		<div class="row">
        		<span class="label">Language of Study<em>*</em></span>
        		<span class="hint" data-desc="<@spring.message 'education.qualifications.language'/>"></span>
        		<div class="field">
          			<select class="full" id="qualificationLanguage" name="qualificationLanguage" value="${qualification.qualificationLanguage!}"
          			 <#if applicationForm.isSubmitted()>
                                    disabled="disabled"
                                </#if>>
            		<option value="">Select...</option>
             			<#list languages as language>
             				<option value="${language.id?string('#######')}"  <#if qualification.qualificationLanguage?? && qualification.qualificationLanguage.id == language.id> selected="selected"</#if>>${language.name?html}</option>
             			</#list>
          			</select>
					<@spring.bind "qualification.qualificationLanguage" /> 
                	 <#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>

        		</div>
      		</div>
      
      		<!-- Qualification level -->
          	<div class="row">
            	<span class="label">Level<em>*</em></span>
            	<span class="hint" data-desc="<@spring.message 'education.qualifications.qualificationLevel'/>"></span>
            	<div class="field">
            		<select name="qualificationLevel" id="qualificationLevel" value="${qualification.qualificationLevel!}"
            		 <#if applicationForm.isSubmitted()>
                                    disabled="disabled"
                     </#if>>
            			 <option value="">Select...</option>
            			 <#list qualificationLevels as level>
                 			 <option value="${level}"
                 			 <#if qualification.qualificationLevel?? &&  qualification.qualificationLevel == level >
                            selected="selected"
                            </#if>
                    >${level.displayValue?html}</option>               
            			</#list>
          			</select>
					<@spring.bind "qualification.qualificationLevel" /> 
                	 <#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>
            	</div>
          	</div>

      		<!-- Qualification type -->
      		<div class="row">
        		<span class="label">Type<em>*</em></span>
        		<span class="hint" data-desc="<@spring.message 'education.qualifications.qualificationType'/>"></span>
        		<div class="field">
        			<#if !applicationForm.isSubmitted()>
          				<input id="qualificationType" class="full" type="text" 
             										value="${(qualification.qualificationType?html)!}"/>
						<@spring.bind "qualification.qualificationType" /> 
                		 <#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>
        			<#else>
        			 	<input readonly="readonly" id="qualificationType" class="full" type="text" 
                                                    value="${(qualification.qualificationType?html)!}"/>
        			</#if>
        		</div>
      		</div>

      		<!-- Qualification grade -->
      		<div class="row">
        		<span class="label">Grade<em>*</em></span>
        		<span class="hint" data-desc="<@spring.message 'education.qualifications.grade'/>"></span>
        		<div class="field">
        		<#if !applicationForm.isSubmitted()>
          			<input id="qualificationGrade" class="full" type="text" placeholder="e.g. 2.1, Distinction"
                    								value="${(qualification.qualificationGrade?html)!}"/>
					<@spring.bind "qualification.qualificationGrade" /> 
                	 <#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>
        		<#else>
        			   <input readonly="readonly" id="qualificationGrade" class="full" type="text" placeholder="e.g. 2.1, Distinction"
                                                    value="${(qualification.qualificationGrade?html)!}"/>
        		</#if>
        		</div>
      		</div>

      		
      		<!-- Award date -->
      		<div class="row">
        		<span class="label">Award Date</span>
        		<span class="hint" data-desc="<@spring.message 'education.qualifications.awardDate'/>"></span>
				
        		<div class="field" id="awardDateField">
        			<input type="text" class="half date" id="qualificationAwardDate" name="qualificationAwardDate" 
        							value="${(qualification.qualificationAwardDate?string('dd-MMM-yyyy'))!}"
                                disabled="disabled">
                	</input>
               	 	<@spring.bind "qualification.qualificationAwardDate" /> 
                	<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>
        		</div>
      		</div>


      		<!-- Attachment / supporting document -->
      		<div class="row">
        		<span class="label">Proof of award</span>
        		<span class="hint" data-desc="<@spring.message 'education.qualifications.proofOfAward'/>"></span>
        		<div class="field" id="uploadFields">        	
          			<input id="proofOfAward" class="full" type="file" name="file" value="" />				
					<span id="progress" style="display: none;"></span>
					<div id="uploadedDocument" style="display: none;" ></div>					
        		</div>  
        		
      		</div>
        
        </div>

    	<div class="buttons">
    	<#if !applicationForm.isSubmitted()>
        	<a class="button" id="qualificationCancelButton" name="qualificationCancelButton">Cancel</a>
        	<button class="blue" type="button" id="qualificationsCloseButton" name="qualificationsCloseButton">Close</button>
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
<script type="text/javascript" src="<@spring.url '/design/default/js/application/ajaxfileupload.js'/>"></script>		
<script type="text/javascript" src="<@spring.url '/design/default/js/application/qualifications.js'/>"></script>		
 <@spring.bind "qualification.*" /> 
 
<#if !spring.status.errorMessages?has_content && (message?? && message=='close') >
<script type="text/javascript">
	$(document).ready(function(){
		$('#qualifications-H2').trigger('click');
	});
</script>
</#if>