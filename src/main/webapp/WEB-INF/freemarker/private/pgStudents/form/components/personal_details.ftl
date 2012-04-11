<#-- Assignments -->

<#if personalDetails?has_content>
	<#assign hasPersonalDetails = true>
<#else>
	<#assign hasPersonalDetails = false>
</#if>

<#if personalDetails.candidateNationalities?has_content>
	<#assign nationalityExist = true/>
<#else>
	<#assign nationalityExist = false>
</#if>

<#if personalDetails.languageProficiencies?has_content>
	<#assign proficiencyExist = true/>
<#else>
	<#assign proficiencyExist = false>
</#if>


<#if personalDetails.candidateNationalities?has_content>
    <#assign candidateNationalitiesExist = true/>
<#else>
    <#assign candidateNationalitiesExist = false>
</#if>

<#if personalDetails.maternalGuardianNationalities?has_content>
    <#assign maternalNationalitiesExist = true/>
<#else>
    <#assign maternalNationalitiesExist = false>
</#if>

<#if personalDetails.paternalGuardianNationalities?has_content>
    <#assign paternalNationalitiesExist = true/>
<#else>
    <#assign paternalNationalitiesExist = false>
</#if>

<#import "/spring.ftl" as spring />
<input type="hidden" id="submissionStatus" value="${applicationForm.submissionStatus}"/>
<#-- Personal Details Rendering -->
<!-- Personal details -->
<h2 id="personalDetails-H2">
	<span class="left"></span><span class="right"></span><span class="status"></span>
	Personal Details<em>*</em>
</h2>

<div>    

	<form>		
			<input type="hidden" name="id" id="personalDetailsId" value="${(personalDetails.id?string("######"))!}"/>			
            <input type="hidden" id="form-display-state" value="${formDisplayState!}"/>
          	<div>
		
				<div class="row">
					<label class="plain-label">First Name<em>*</em></label>
					 <span class="hint" data-desc="<@spring.message 'personalDetails.firstname'/>"></span>
					<div class="field">                    	
							<input class="full" readonly="readonly" type="text" value="${(user.firstName?html)!}"  name="firstName" id="firstName"/>	          
					</div>
				 </div>
             
				<div class="row">
					<label class="plain-label">Last Name<em>*</em></label>
					<span class="hint" data-desc="<@spring.message 'personalDetails.lastname'/>"></span>
					<div class="field">
							<input class="full" readonly="readonly" type="text" value="${(user.lastName?html)!}"   name="lastName" id="lastName"/>	          
					</div>
				 </div>
             
				<div class="row">
					<label class="plain-label">Gender<em>*</em></label>
					<span class="hint" data-desc="<@spring.message 'personalDetails.gender'/>"></span>
					<div class="field">
						  <#list genders as gender>
								<label><input type="radio" name="genderRadio" value="${gender}"
									<#if personalDetails.gender?? &&  personalDetails.gender == gender >
										checked="checked"
									</#if> 
									 <#if applicationForm.isSubmitted()>disabled="disabled"</#if>									   
								/> ${gender.displayValue}</label>
											 
						</#list>          		
            			<@spring.bind "personalDetails.gender" /> 
                		<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>
					</div>
				</div>
            
				<div class="row">
					<label class="plain-label">Date of Birth<em>*</em> </label>
					<span class="hint" data-desc="<@spring.message 'personalDetails.dateOfBirth'/>"></span>
					<div class="field">
					<#if !applicationForm.isSubmitted()>
						<input class="half date" value="${(personalDetails.dateOfBirth?string('dd-MMM-yyyy'))!}" name="dateOfBirth" id="dateOfBirth"/>
						<@spring.bind "personalDetails.dateOfBirth" /> 
                		<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>					
					<#else>
						<input class="full" readonly="readonly" type="text" value="${(personalDetails.dateOfBirth?string('dd-MMM-yyyy'))!}" name="dateOfBirth" id="dateOfBirth" />             
					</#if>    
					</div>                
				</div>
			</div>
		
          	<div>
            	<div class="row">
              		<label class="plain-label">Country of Birth<em>*</em></label>
                	<span class="hint" data-desc="<@spring.message 'personalDetails.countryOfBirth'/>"></span>
                	<div class="field">
                  		<select class="full" name="country" id="country" <#if applicationForm.isSubmitted()>disabled="disabled"</#if> >
                  			<option value="">Select...</option>
                    		<#list countries as country>
                          	<option value="${country.id?string('#######')}"
							<#if personalDetails.country?? &&  personalDetails.country.id == country.id >
							selected="selected"
							</#if>   
                          >${country.name}</option>               
                    	</#list>
                 	 	</select>
						<@spring.bind "personalDetails.country" /> 
                		<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>	
               	 	</div>
              	</div>
         
				<div class="row" id="existingCandidateNationalities">
				<#if candidateNationalitiesExist>
              	  	 <label class="plain-label" id="candidateNationalitiesLabel">My Nationality</label>
              	 </#if> 	 		
              	  <#list personalDetails.candidateNationalities as nationality >
              	  	<span name="existingCandidateNationality">
              	  	 	<div class="row">
              				<div class="field">
              					<label class="full">${nationality.name}</label>  
              	  				<input type="hidden" name="candidateNationalities" value='${nationality.id?html}'/>
								<#if !applicationForm.isSubmitted()><a class="button-delete">Delete</a></#if>
              	  			</div>
              	  		</div>
              	  	</span>                  		
              	  </#list>
              	</div>
        	    
            	<div class="row">                    	
              		 <label class="plain-label">My Nationality
					<#if !nationalityExist>
						<em id="nationality-em">*</em>
					</#if>
					</label>      
					<span class="hint" data-desc="<@spring.message 'personalDetails.nationality'/>"></span>    
					<div class="field">
						 <select class="full" name="candidateNationalityCountry" id="candidateNationalityCountry" <#if applicationForm.isSubmitted()>disabled="disabled"</#if>>
							<option value="">Select...</option>
							<#list countries as country>
							  <option value="${country.id?string('#######')}">${country.name}</option>               
						 </#list>
						 </select>             	 
						<@spring.bind "personalDetails.candidateNationalities" /> 
                		<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>						   
					 </div>
            	</div>    
              
				<div class="row">
					<div class="field"><a class="button blue" id="addCandidateNationalityButton">Add Nationality</a></div>
				</div>
    
        	    <div class="row" id="existingMaternalNationalities">
        	     <#if maternalNationalitiesExist>
                     <label class="plain-label" id="maternalNationalitiesLabel">Mother's Nationality</label>
                 </#if> 
					 <#list personalDetails.maternalGuardianNationalities as nationality >
						<span>
							<div class="row">
								<div class="field">
									<label class="full">${nationality.name}</label>  
									<input type="hidden" name="maternalGuardianNationalities" value='${nationality.id?html}'/>
									<#if !applicationForm.isSubmitted()><a class="button-delete">Delete</a></#if>
								</div>
							</div>            
						</span>
					 </#list>
				</div>
              
            	<div class="row">                     	
              		<label class="plain-label">Mother's Nationality</label>
              		<span class="hint" data-desc="<@spring.message 'personalDetails.maternalGuardianNationality'/>"></span>           
                  	 <div class="field">
                     	 <select class="full" name="maternalNationalityCountry" id="maternalNationalityCountry" <#if applicationForm.isSubmitted()>disabled="disabled"</#if>>
                      		<option value="">Select...</option>
                        	<#list countries as country>
                              <option value="${country.id?string('#######')}">${country.name}</option>               
                       	 </#list>
                     	 </select>             	 
                   	 </div>
            	</div>
              
            	<div class="row">
              		<div class="field"><a class="button blue" id="addMaternalNationalityButton">Add Nationality</a></div>
              	</div>
              	 
        	     <div class="row" id="existingPaternalNationalities">
        	      <#if paternalNationalitiesExist>
                     <label class="plain-label" id="paternalNationalitiesLabel">Father's Nationality</label>
                 </#if> 
					<#list personalDetails.paternalGuardianNationalities as nationality >
						<span>
							<div class="row">
								<div class="field">
									<label class="full">${nationality.name?html}</label>  
									<input type="hidden" name="paternalGuardianNationalities" value='${nationality.id?html}'/>
									<#if !applicationForm.isSubmitted()><a class="button-delete">Delete</a></#if>
								</div>
							</div>            
						</span>
					</#list>
				</div>
				<div class="row">                      	
					<label class="plain-label">Father's Nationality</label> 
					  <span class="hint" data-desc="<@spring.message 'personalDetails.paternalGuardianNationality'/>"></span>      
                  	 <div class="field">
                     	 <select class="full" name="paternalNationalityCountry" id="paternalNationalityCountry" <#if applicationForm.isSubmitted()>disabled="disabled"</#if>>
                      		<option value="">Select...</option>
                        	<#list countries as country>
                              <option value="${country.id?string('#######')}">${country.name?html}</option>               
                       	 </#list>
                     	 </select>             	 
                   	 </div>
            	</div>
            	                     
              
            	<div class="row">
              		<div class="field"><a class="button blue" id="addPaternalNationalityButton">Add Nationality</a></div>
              	</div>
              	 
            </div>
          	
          	<div>
          	     <div class="row">
					<label class="label">Language</label>
					                   
					<div class="row">
                   <label class="plain-label">Is English your first language?<em>*</em></label>
                   		<span class="hint" data-desc="<@spring.message 'personalDetails.language.section'/>"></span>
                   		<input type="checkbox" name="englishFirstLanguageCB" id="englishFirstLanguageCB"
                   			<#if personalDetails.isEnglishFirstLanguage()>
                                      checked="checked"
                            </#if>
                   			<#if applicationForm.isSubmitted()>
                                      disabled="disabled"
                            </#if>
                           />
           			 </div>
				</div>                              
            </div>

          	<div>
            	<strong>Residence</strong>
            	<div class="row">
					<span class="plain-label">Country<em>*</em></span>
						<span class="hint" data-desc="<@spring.message 'personalDetails.residence.country'/>"></span>        
					<div class="field">
						<select class="full" name="residenceCountry" id="residenceCountry" <#if applicationForm.isSubmitted()>disabled="disabled"</#if>>
							<option value="">Select...</option>
							<#list countries as country>
								  <option value="${country.id?string('#######')}"
								  <#if personalDetails.residenceCountry?? &&  personalDetails.residenceCountry.id == country.id >
									selected="selected"
									</#if>  
								  >${country.name}</option>               
							</#list>
						 </select>
						<@spring.bind "personalDetails.residenceCountry" /> 
                		<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>			
						</div>
						<div class="row">
						 <label class="plain-label">Do you require Visa to study in the UK?<em>*</em></label>
                   		<span class="hint"></span>
                   		<input type="checkbox" name="requiresVisaCB" id="requiresVisaCB"
                   			<#if personalDetails.isRequiresVisa()>
                                      checked="checked"
                            </#if>
                   			<#if applicationForm.isSubmitted()>
                                      disabled="disabled"
                            </#if>/>
                   		<input type="hidden" name="requiresVisa" id="requiresVisa"/>
					</div>
				</div>
            </div>

          	<div>
            	<strong>Contact Details</strong>
            	
            	<div class="row">
            		<span class="plain-label">Email<em>*</em></span>
              		<span class="hint" data-desc="<@spring.message 'personalDetails.email'/>"></span> 
                    <div class="field">
                    		<input class="full" readonly="readonly" type="email" value="${(user.email?html)!}"  name="email" id="email" />	          
                    </div>
				</div>
              	
   
          	 	<div class="row">          
              		<span class="plain-label">Telephone<em>*</em></span>
                	<span class="hint" data-desc="<@spring.message 'personalDetails.telephone'/>"></span>
                	<div class="field">                    	
                		<#if !applicationForm.isSubmitted()>
                			<input class="full" type="text" value="${(personalDetails.phoneNumber?html)!}" name="pd_telephone" id="pd_telephone"/>
						<@spring.bind "personalDetails.phoneNumber" /> 
                		<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>	                			
                		<#else>
                			<input class="full" readonly="readonly" type="text" value="${(personalDetails.phoneNumber?html)!}" name="pd_telephone" id="pd_telephone" />	          
                		</#if>
                	</div>
            	</div>
              
           		<div class="row">
              		<label class="plain-label">Skype Name</label>
                	<span class="hint" data-desc="<@spring.message 'personalDetails.skype'/>"></span>
                	<div class="field">                    	
                		<#if !applicationForm.isSubmitted()>
                			<input class="full" type="text" value="${(personalDetails.messenger?html)!}" name="pd_messenger" id="pd_messenger"/>
                		<#else>
                			<input class="full" readonly="readonly" type="text" value="${(personalDetails.messenger?html)!}" name="pd_messenger" id="pd_messenger" />	          
                		</#if>
                	</div>
            	</div>
            </div>
  
          	<div class="buttons">
              <#if !applicationForm.isSubmitted()>
              		<button type="reset" name="personalDetailsCancelButton" id="personalDetailsCancelButton" value="cancel">Cancel</button>
                    <a id="personalDetailsCloseButton" class="button blue">Close</a>
					<button class="blue" type="button" id="personalDetailsSaveButton" value="close">Save</button>
              <#else>
              		<a id="personalDetailsCloseButton"class="button blue">Close</a>			
              </#if>
            </div>
       </form>
</div>

<script type="text/javascript" src="<@spring.url '/design/default/js/application/personalDetails.js'/>"></script>

<@spring.bind "personalDetails.*" /> 
 
<#if !message?? || (!spring.status.errorMessages?has_content && (message=='close'))  >
<script type="text/javascript">
	$(document).ready(function(){
		$('#personalDetails-H2').trigger('click');
	});
</script>
</#if>