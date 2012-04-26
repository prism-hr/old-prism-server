<#assign errorCode = RequestParameters.errorCode! />
<#if applicationForm.qualifications?has_content>
	<#assign hasQualifications = true>
<#else>
	<#assign hasQualifications = false>
</#if> 
 
<#import "/spring.ftl" as spring />
	
<h2 id="qualifications-H2" class="empty open">
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
                <th id="last-col">&nbsp;</th>
              </tr>
            </thead>
            
            <tbody>
            
            	<#list applicationForm.qualifications as existingQualification>
                	<tr>
	                  	<td><a class="row-arrow">-</a>
	                  	</td>
	                  	<td>${(existingQualification.qualificationType?html)!}</td>
	                  	<td>${(existingQualification.qualificationGrade?html)!}</td>
	                  	<td>${(existingQualification.qualificationInstitution?html)!}</td>
	                  	<td>${(existingQualification.qualificationAwardDate?string('dd-MMM-yyyy'))!}</td>
	                  	  
	                  	  	   <td>
	                  	  	   		<a name="editQualificationLink" <#if !applicationForm.isDecided()>data-desc="Edit" <#else>data-desc="Show"</#if> id="qualification_${existingQualification.id?string('#######')}" class="button-edit button-hint">edit</a>
	                  	  	   </td>
	                  	  	<#if !applicationForm.isDecided()>
	                  	  	   <td>
		                			<a name="deleteQualificationButton" data-desc="Delete" id="qualification_${existingQualification.id?string('#######')}" class="button-delete button-hint">delete</a>
		                	   </td>
		                	<#else>
		                		<td></td><td></td>		                		
		                	</#if>
			        		
                  	</tr>
				</#list>
							
            </tbody>
      	</table>
  		
  	</#if>
  
  	<input type="hidden" id="qualificationId" name="qualificationId" value="${(qualification.id?html)!}"/>
  	
  	<form>
		<div class="section-info-bar">
			<div class="row">
				<span class="info-text"> &nbsp
					<@spring.message 'education.qualifications.sectionInfo'/>
					<b><@spring.message 'education.qualifications.sectionInfoBold'/></b> 
				</span>
			</div>
		</div>
		
		<div>
		
       		<div class="row">
        		<span class="plain-label">Institution Country<em>*</em></span>
        		<span class="hint" data-desc="<@spring.message 'education.qualifications.institutionCountry'/>"></span>
        		<div class="field">
          			<select class="full" id="institutionCountry" name="institutionCountry"
          			 	<#if applicationForm.isDecided()>
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
	
          	<!-- Provider -->
        	<div class="row">
              	<span class="plain-label">Institution / Provider Name<em>*</em></span>
                <span class="hint" data-desc="<@spring.message 'education.qualifications.institutionName'/>"></span>
                <div class="field">
                	<#if !applicationForm.isDecided()>
                		<input id="qualificationInstitution" class="full" type="text" placeholder="e.g. UCL"  value="${(qualification.qualificationInstitution?html)!}" />
                		 <@spring.bind "qualification.qualificationInstitution" /> 
                	 	<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>                	
        			<#else>
        			     <input readonly="readonly" id="qualificationInstitution" class="full" type="text" placeholder="e.g. UCL" value="${(qualification.qualificationInstitution?html)!}" />
        			</#if>
                    									
                </div>
          	</div>

      		<!-- Qualification type -->
      		<div class="row">
        		<span class="plain-label">Qualification Type<em>*</em></span>
        		<span class="hint" data-desc="<@spring.message 'education.qualifications.qualificationType'/>"></span>
        		<div class="field">
        			<#if !applicationForm.isDecided()>
          				<input id="qualificationType" class="full" type="text" value="${(qualification.qualificationType?html)!}"/>
						<@spring.bind "qualification.qualificationType" /> 
                		 <#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>
        			<#else>
        			 	<input readonly="readonly" id="qualificationType" class="full" type="text" value="${(qualification.qualificationType?html)!}"/>
        			</#if>
        		</div>
      		</div>
			
          	<!-- Name (of programme) -->
        	<div class="row">
              	<span class="plain-label">Title / Subject<em>*</em></span>
               <span class="hint" data-desc="<@spring.message 'education.qualifications.subject'/>"></span>
                <div class="field">
                	<#if !applicationForm.isDecided()>
                		<input id="qualificationSubject" class="full" type="text" placeholder="e.g. Civil Engineering"  value="${(qualification.qualificationSubject?html)!}"/>
   					 	<@spring.bind "qualification.qualificationSubject" /> 
                	 	<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>
   					<#else>
   					  	<input readonly="readonly" id="qualificationSubject" class="full" type="text" placeholder="e.g. Civil Engineering" value="${(qualification.qualificationSubject?html)!}"/>
   					</#if>					
                </div>
     		</div>
		
		    <!-- Language (in which programme was undertaken) -->
      		<div class="row">
        		<span class="plain-label">Language of Study<em>*</em></span>
        		<span class="hint" data-desc="<@spring.message 'education.qualifications.language'/>"></span>
        		<div class="field">
          			<select class="full" id="qualificationLanguage" name="qualificationLanguage" value="${qualification.qualificationLanguage!}"
          			 	<#if applicationForm.isDecided()>
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
		
          	<!-- Start date -->
        	<div class="row">
              	<span class="plain-label">Start Date<em>*</em></span>
                <span class="hint" data-desc="<@spring.message 'education.qualifications.startDate'/>"></span>
                <div class="field">                    
                    <input id="qualificationStartDate" class="half date" type="text" value="${(qualification.qualificationStartDate?string('dd-MMM-yyyy'))!}" 
                    	<#if applicationForm.isDecided()>
                        	disabled="disabled"
                         </#if>>
                	</input>
               		<@spring.bind "qualification.qualificationStartDate" /> 
                	<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>
                </div>
         	</div>
		</div>

		<div>

			<div class="row">
	           <span class="plain-label">Has this qualification been awarded?</span>
	           	<span class="hint" data-desc="<@spring.message 'education.qualifications.hasBeenAwarded'/>"></span>
	           	 <div class="field">        
	           		<input type="checkbox" name="currentQualificationCB" id="currentQualificationCB"
	           			<#if qualification.isQualificationCompleted()>
									checked ='checked'
	                    </#if>
	           			<#if applicationForm.isDecided()>
	                              disabled="disabled"
	                    </#if>
	                    />
	           		<input type="hidden" name="currentQualification" id="currentQualification"/>
	   			 </div>
        	</div>

      		
      		<!-- Qualification grade -->
      		<div class="row">
        		<span id="quali-grad-id" class="plain-label">Expected Grade / Result / GPA<em>*</em></span>
        		<span class="hint" data-desc="<@spring.message 'education.qualifications.grade'/>"></span>
        		<div class="field">
	        		<#if !applicationForm.isDecided()>
	          			<input id="qualificationGrade" class="full" type="text" placeholder="e.g. 2.1, Distinction" value="${(qualification.qualificationGrade?html)!}"/>
						<@spring.bind "qualification.qualificationGrade" /> 
	                	 <#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>
	        		<#else>
	        			  <input readonly="readonly" id="qualificationGrade" class="full" type="text" placeholder="e.g. 2.1, Distinction" value="${(qualification.qualificationGrade?html)!}"/>
	        		</#if>
        		</div>
      		</div>
      		
      		<!-- Award date -->
      		<div class="row">
        		<span id="quali-award-date-lb" class="plain-label grey-label">Award Date</span>
        		<span class="hint" data-desc="<@spring.message 'education.qualifications.awardDate'/>"></span>
				
        		<div class="field" id="awardDateField">
        			<input type="text" class="half date" id="qualificationAwardDate" name="qualificationAwardDate" value="${(qualification.qualificationAwardDate?string('dd-MMM-yyyy'))!}"
        				 <#if !qualification.isQualificationCompleted()>disabled="disabled"</#if>>
                	</input>
               	 	<@spring.bind "qualification.qualificationAwardDate" /> 
                	<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>
        		</div>
      		</div>

      		<!-- Attachment / supporting document -->
      		<div class="row">
        		<span id="quali-proof-of-award-lb" class="plain-label grey-label">Proof of award (PDF)</span>
        		<span class="hint" data-desc="<@spring.message 'education.qualifications.proofOfAward'/>"></span>
        		<div class="field" id="uploadFields">         		       	
          			<input id="proofOfAward" class="full" type="file" name="file" value=""  <#if applicationForm.isDecided()>disabled="disabled"</#if>/>					
					<span id="qualUploadedDocument" >
						<input type="hidden" id="document_PROOF_OF_AWARD" value = "${(qualification.proofOfAward.id?string('######'))!}"/>
						<#if qualification.proofOfAward??> 
							<a href="<@spring.url '/download?documentId=${(qualification.proofOfAward.id?string("#######"))!}'/>">${(qualification.proofOfAward.fileName?html)!}</a>
						</#if>
					</span>
					<span id="progress" style="display: none;" ></span>					
        		</div>          		
      		</div>
      		
      		<!-- Add another button -->
      		<#if !applicationForm.isDecided()>
	            <div class="row">
	            	<div class="field">
	                	<a id="addQualificationButton" class="button blue">Add Qualification</a>
	                </div>
	            </div>
			</#if>
		</div>
		
    	<div class="buttons">
	    	<#if !applicationForm.isDecided()>
	        	<a class="button" id="qualificationCancelButton" name="qualificationCancelButton">Cancel</a>
	        	<button class="blue" type="button" id="qualificationsCloseButton" name="qualificationsCloseButton">Close</button>
	            <button id="qualificationsSaveButton" class="blue" type="button" value="add">Save</button>
	         <#else>
	              <a id="qualificationsCloseButton" class="button blue">Close</a>   
	        </#if>  
        </div>
  </form>
</div>

<script type="text/javascript" src="<@spring.url '/design/default/js/application/ajaxfileupload.js'/>"></script>		
<script type="text/javascript" src="<@spring.url '/design/default/js/application/qualifications.js'/>"></script>		
 <@spring.bind "qualification.*" /> 
 
<#if (errorCode?? && errorCode=='false') || (message?? && message='close' && !spring.status.errorMessages?has_content)>
<script type="text/javascript">
	$(document).ready(function(){
		$('#qualifications-H2').trigger('click');
	});
</script>
</#if>