<#-- Assignments -->

<#if model.applicationForm.personalDetails?has_content>
	<#assign hasPersonalDetails = true>
<#else>
	<#assign hasPersonalDetails = false>
</#if>


<#import "/spring.ftl" as spring />
<input type="hidden" id="submissionStatus" value="${model.applicationForm.submissionStatus}"/>
<#-- Personal Details Rendering -->
<!-- Personal details -->
	<h2 id="personalDetails-H2" class="open">
		<span class="left"></span><span class="right"></span><span class="status"></span>
		Personal Details
	</h2>
	
    <div id="personal-details-section" class="open">
    
    	<#--
    	<#if hasPersonalDetails>
			<table class="existing">
	              	<colgroup>
	                	<col style="width: 30px" />
	                	<col style="width: 170px" />
	                	<col style="width: 200px" />
	                	<col />
	                	<col style="width: 30px" />
	                	<col style="width: 30px" />
	                </colgroup>
	              	<thead>
	                	<tr>
	                  	<th colspan="2">First name</th>
	                    <th>Surname</th>
	                    <th>Email</th>
	                    <th colspan="2">&nbsp;</th>
	                  </tr>
	                </thead>
				<tbody>
					<#list model.applicationForm.personalDetails as personalDetails>
					<tr>
				    	<td><a class="row-arrow" href="#">-</a></td>
				        <td>${model.applicationForm.personalDetails.firstName!}</td>
				        <td>${model.applicationForm.personalDetails.lastName!}</td>
				        <td>${model.applicationForm.personalDetails.email!}</td>
	                  	<td><a class="button-edit" href="#">edit</a></td>
	                  	<td><a class="button-close" href="#">close</a></td>
				    </tr>
				    </#list>
				</tbody>
			</table>
		</#if>
		-->
		<form id ="UploadForm" enctype="multipart/form-data" method="post" action="">		
				<input type="hidden" name="id" id="id" value="${(model.applicationForm.personalDetails.id?string("######"))!}"/>
				<input type="hidden" id="appId" name="appId" value="${model.applicationForm.id?string("######")}"/>
                <input type="hidden" id="form-display-state" value="${formDisplayState}"/>
              	<div>
        <#if model.hasError('personalDetails')>                           
        <div class="row">      	
                <span class="invalid"><@spring.message  model.result.getFieldError('personalDetails').code /></span>
        </div>                                
        </#if>
                	<div class="row">
                  	<label class="label">First Name</label>
                    <span class="hint"></span>
                    <div class="field">                    	
                    <#if !model.applicationForm.isSubmitted()>
                    	<input class="full" type="text" value="${model.applicationForm.personalDetails.firstName!}" name="firstName" id="firstName"/>
                    	<#if model.hasError('firstName')>                    		
                    			<span class="invalid"><@spring.message  model.result.getFieldError('firstName').code /></span>                    		
                    	</#if>
                    	<#else>
                    		<input class="full" readonly="readonly" type="text" value="${model.applicationForm.personalDetails.firstName!}" name="firstName" id="firstName" />	          
                    	</#if>
                    </div>
                  </div>
                	<div class="row">
                  	<label class="label">Last Name</label>
                    <span class="hint"></span>
                    <div class="field">
                     <#if !model.applicationForm.isSubmitted()>
                    	<input class="full" type="text" value="${model.applicationForm.personalDetails.lastName!}" name="lastName" id="lastName"/>
                    	<#if model.hasError('lastName')>                    		
                    			<span class="invalid"><@spring.message  model.result.getFieldError('lastName').code /></span>                    		
                    	</#if>
                    <#else>
                    		<input class="full" readonly="readonly" type="text" value="${model.applicationForm.personalDetails.lastName!}" name="lastName" id="lastName" />	          
                    </#if>
                    </div>
                  </div>
                	<div class="row">
                  	<label class="label">Gender</label>
                    <div class="field">
                          <#list model.genders as gender>
                          		<label><input type="radio" name="genderRadio" value="${gender}"
                          			<#if model.applicationForm.personalDetails.gender?? &&  model.applicationForm.personalDetails.gender == gender >
										checked="checked"
									</#if> 
									 <#if model.applicationForm.isSubmitted()>disabled="disabled"</#if>									   
                          		/> ${gender.displayValue}</label>
                                             
                        </#list>                  		
            
                      <#if model.hasError('gender')>                         
                                <span class="invalid"><@spring.message  model.result.getFieldError('gender').code /></span>                           
                      </#if>
                    </div>
                  </div>
                	<div class="row">
                  	<label class="label">Date of Birth</label>
                    <span class="hint"></span>
                    <div class="field">
                    <#if !model.applicationForm.isSubmitted()>
                        <input class="half date" value="${(model.applicationForm.personalDetails.dateOfBirth?string('dd-MMM-yyyy'))!}" name="dateOfBirth" id="dateOfBirth"/>
                        <#if model.hasError('dateOfBirth')>                           
                                <span class="invalid"><@spring.message  model.result.getFieldError('dateOfBirth').code /></span>                           
                        </#if>
                    <#else>
                        <input class="full" readonly="readonly" type="text" value="${(model.applicationForm.personalDetails.dateOfBirth?string('dd-MMM-yyyy'))!}" name="dateOfBirth" id="dateOfBirth" />             
                    </#if>    
                    </div>
                  </div>
                </div>

              	<div>
                	<div class="row">
                  	<label class="label">Country of Birth</label>
                    <span class="hint"></span>
                    <div class="field">
                      <select class="full" name="country" id="country" <#if model.applicationForm.isSubmitted()>disabled="disabled"</#if> >
                      		<option value="">Select...</option>
                        <#list model.countries as country>
                              <option value="${country.id}"
								<#if model.applicationForm.personalDetails.country?? &&  model.applicationForm.personalDetails.country.id == country.id >
								selected="selected"
								</#if>   
                              >${country.name}</option>               
                        </#list>
                      </select>
                      <#if model.hasError('country')>                         
                                <span class="invalid"><@spring.message  model.result.getFieldError('country').code /></span>                           
                        </#if>
                    </div>
                  </div>
                </div>

              	<div>    

                 	 <div class="row" id="existingCandidateNationalities">
                  	  <#list model.applicationForm.personalDetails.candidateNationalities as nationality >
                  	  	<span name="existingCandidateNationality">
                  	  	 	<div class="row">
                  	  	 		<label class="label">Nationality</label>    
                  				<div class="field">
                  					<label class="full">${nationality.country.name}</label>  
                  	  				<input type="hidden" name="candidateNationalities" value='${nationality.asJson}'/>
                  	  				<#if nationality.primary><label>This is my primary nationality</label>"</#if>
                  	  			<#if !model.applicationForm.isSubmitted()><a class="button-delete">Delete</a></#if>
                  	  			</div>
                  	  		</div>
                  	  	</span>                  		
                  	  </#list>
                  </div>
            	    
                	<div class="row">                    	
                  		 <label class="label">Nationality</label>       
	                  	 <div class="field">
	                     	 <select class="full" name="candidateNationalityCountry" id="candidateNationalityCountry" <#if model.applicationForm.isSubmitted()>disabled="disabled"</#if>>
	                      		<option value="">Select...</option>
	                        	<#list model.countries as country>
	                              <option value="${country.id}">${country.name}</option>               
	                       	 </#list>
	                     	 </select>             	 
	                       <#if model.hasError('candidateNationalities')>                         
                                <span class="invalid"><@spring.message  model.result.getFieldError('candidateNationalities').code /></span>                           
                       	 </#if>
	                      	 <label><input type="radio" id="primaryCandidateNationality" name="primaryCandidateNationality"/> This is my primary nationality</label>              
	                   	 </div>
                	</div>
                	
                      
                  
                	<div class="row">
                  		<div class="field"><a class="button blue" id="addCandidateNationalityButton">Add nationality</a></div>
                  	</div>
        
                </div>


				<div>   	
				

            	    <div class="row" id="existingMaternalNationalities">
                  	  <#list model.applicationForm.personalDetails.maternalGuardianNationalities as nationality >
                  	  	<span>
                  	  		<div class="row">
                  	  	 		<label class="label">Maternal Guardian Nationality</label>    
                  				<div class="field">
                  					<label class="full">${nationality.country.name}</label>  
                  	  				<input type="hidden" name="maternalGuardianNationalities" value='${nationality.asJson}'/>
                  	  				<#if nationality.primary><label>This is her primary nationality</label></#if>
                  	  				<#if !model.applicationForm.isSubmitted()><a class="button-delete">Delete</a></#if>
                  	  			</div>
                  	  		</div>            
                  	  	</span>
                  	  </#list>
                  </div>
                  
                	<div class="row">                     	
                  		<label class="label">Maternal Guardian Nationality</label>        
	                  	 <div class="field">
	                     	 <select class="full" name="maternalNationalityCountry" id="maternalNationalityCountry" <#if model.applicationForm.isSubmitted()>disabled="disabled"</#if>>
	                      		<option value="">Select...</option>
	                        	<#list model.countries as country>
	                              <option value="${country.id}">${country.name}</option>               
	                       	 </#list>
	                     	 </select>             	 
	                      <#if model.hasError('maternalGuardianNationalities')>                         
                                <span class="invalid"><@spring.message  model.result.getFieldError('maternalGuardianNationalities').code /></span>                           
                       	 </#if>
	                      	 <label><input type="radio" id="primaryMaternalNationality"/> This is her primary nationality</label>              
	                   	 </div>
                	</div>
                	
                      
                  
                	<div class="row">
                  		<div class="field"><a class="button blue" id="addMaternalNationalityButton">Add nationality</a></div>
                  	</div>
                  	 
                </div>
              	
              	
              	<div>       
            	     <div class="row" id="existingPaternalNationalities">
                  	  <#list model.applicationForm.personalDetails.paternalGuardianNationalities as nationality >
                  	  	<span>
                  	  		<div class="row">
                  	  	 		<label class="label">Paternal Guardian Nationality</label>    
                  				<div class="field">
                  					<label class="full">${nationality.country.name}</label>  
                  	  				<input type="hidden" name="paternalGuardianNationalities" value='${nationality.asJson}'/>
                  	  				<#if nationality.primary><label>This is his primary nationality</label></#if>
                  	  				<#if !model.applicationForm.isSubmitted()><a class="button-delete">Delete</a></#if>
                  	  			</div>
                  	  		</div>            
                  	  	</span>
                  	  </#list>
                  </div>
                <div class="row">                      	
                 <label class="label">Paternal Guardian Nationality</label> 
	                  	 <div class="field">
	                     	 <select class="full" name="paternalNationalityCountry" id="paternalNationalityCountry" <#if model.applicationForm.isSubmitted()>disabled="disabled"</#if>>
	                      		<option value="">Select...</option>
	                        	<#list model.countries as country>
	                              <option value="${country.id}">${country.name}</option>               
	                       	 </#list>
	                     	 </select>             	 
	                          <#if model.hasError('paternalGuardianNationalities')>                         
                                <span class="invalid"><@spring.message  model.result.getFieldError('paternalGuardianNationalities').code /></span>                           
                       	 </#if>
	                      	 <label><input type="radio" id="primaryPaternalNationality"/> This is his primary nationality</label>              
	                   	 </div>
                	</div>
                	                     
                  
                	<div class="row">
                  		<div class="field"><a class="button blue" id="addPaternalNationalityButton">Add nationality</a></div>
                  	</div>
                  	 
                </div>
              	
              	<div>
                              
                    <div class="row" id="existingProficiencies">
                  	  <#list model.applicationForm.personalDetails.languageProficiencies as prof >
                  	  	<span>
                  	  		<div class="row">
                  	  	 		<label class="label">Language</label>    
                  				<div class="field">
                  					<label class="full"> ${prof.language.name}</label> 
                  	  				<#if !model.applicationForm.isSubmitted()><a class="button-delete">Delete</a></#if> 
                  	  				<#if prof.primary>This is my primary language</#if>              
                  	  			</div>
                  	  			<span class="label">Aptitude</span>    
                  				<div class="field">
                  					<label class="full"> ${prof.aptitude.displayValue}</label>                   	  				
                  	  				
                  	  			</div>
                  	  		</div>   
                            <input type="hidden" name="languageProficiencies" value='${prof.asJson}'/>   
                  	  	</span>
                  	  </#list>
                  </div>
                  
                	<div class="row">
                  	<label class="label">Language</label>
                    <span class="hint"></span>                    
                    <div class="field">
                      <select class="full" id="languageSelect" <#if model.applicationForm.isSubmitted()>disabled="disabled"</#if>>
                        <option value="">Select...</option>
                         <#list model.languages as language>
                         	<option value="${language.id}">${language.name}</option>
                         </#list>
                      </select>
                      
                      <label><input type="radio" name="primaryLanguage" id="primaryLanguage"/> This is my primary language</label>
                    </div>
                  </div>
                <div class="row">
                  	<span class="label">Aptitude</span>
                    <span class="hint"></span>
                    <div class="field">
                      <select class="full" id="aptitude" name="aptitude" <#if model.applicationForm.isSubmitted()>disabled="disabled"</#if>>                      	
                       <#list model.languageAptitudes as aptitude >
                      		<option value="${aptitude}">${aptitude.displayValue}</option>
                      	</#list>
                      </select>
                        <#if model.hasError('languageProficiencies')>                         
                                <span class="invalid"><@spring.message  model.result.getFieldError('languageProficiencies').code /></span>                           
                       	 </#if>
                    </div>
                  </div>
                	<div class="row">
                  		<div class="field"><a class="button blue" id="addLanguageButton">Add language</a></div>
                  </div>
                
                </div>

              	<div>
                	<strong>Residence</strong>
                	<div class="row">
                  	<span class="label">Country</span>
                    <span class="hint"></span>
                    <div class="field">
                      <select class="full" name="residenceCountry" id="residenceCountry" <#if model.applicationForm.isSubmitted()>disabled="disabled"</#if>>
                      	<option value="">Select...</option>
                        <#list model.countries as country>
                              <option value="${country.id}"
                              <#if model.applicationForm.personalDetails.residenceCountry?? &&  model.applicationForm.personalDetails.residenceCountry.id == country.id >
								selected="selected"
								</#if>  
                              >${country.name}</option>               
                        </#list>
                      </select>
                      <#if model.hasError('residenceCountry')>                         
                                <span class="invalid"><@spring.message  model.result.getFieldError('residenceCountry').code /></span>                           
                        </#if>
                    </div>
                  </div>
                  <div class="row">
                    <span class="label">Status</span>
                    <span class="hint"></span>
                    <div class="field">
                      <select class="full"  name="residenceStatus" id="residenceStatus" <#if model.applicationForm.isSubmitted()>disabled="disabled"</#if>>
                         <#list model.residenceStatuses as residenceStatus>
                              <option value="${residenceStatus}">${residenceStatus.displayValue}</option>               
                        </#list>
                      </select>
                      <#if model.hasError('residenceStatus')>                         
                                <span class="invalid"><@spring.message  model.result.getFieldError('residenceStatus').code /></span>                           
                        </#if>
                    </div>
                  </div>
                </div>

              	<div>
                	<strong>Contact Details</strong>
                	<div class="row">
                		<span class="label">Email</span>
                    <span class="hint"></span>
                    <div class="field">
                        <#if !model.applicationForm.isSubmitted()>
	                    	<input class="full" type="email" value="${model.applicationForm.personalDetails.email!}" name="email" id="email" />	                    
	                     	<#if model.hasError('email')>                    		
                    			<span class="invalid"><@spring.message  model.result.getFieldError('email').code /></span>                    		
                    		</#if>
                    	<#else>
                    		<input class="full" readonly="readonly" type="email" value="${model.applicationForm.personalDetails.email!}" name="email" id="email" />	          
                    	</#if>
                    </div>
                  </div>
                </div>
                
              	<div>
	              	 <div id="phonenumbers"  class="row">
	                    <#list model.applicationForm.personalDetails.phoneNumbers! as phoneNumber>          
							<span>
	                  	  		<div class="row">
	                  	  	 		<span class="label">Telephone</span>    
	                  				<div class="field">
	                  					<label class="full"> ${phoneNumber.telephoneType.displayValue}</label>
	                  					<label class="half"> ${phoneNumber.telephoneNumber}</label> 
	                  	  				<#if !model.applicationForm.isSubmitted()><a class="button-delete">Delete</a></#if>           
	                  	  			</div>
	                  	  			
	                  	  		</div>   
	                            <input type="hidden" name="phoneNumbers" value='${phoneNumber.asJson}'/>   
	                  	  	</span>
	                    </#list>
	                  </div>
              	
              	
              	
                	<div class="row">
                		<span class="label">Telephone</span>
                    <span class="hint"></span>
                   
                    <div class="field">
                    	<select class="half" id="phoneType" <#if model.applicationForm.isSubmitted()>disabled="disabled"</#if>>
                    	 <#list model.phoneTypes as phoneType >
                      		<option value="${phoneType}">${phoneType.displayValue}</option>
                      	</#list>
                      </select>
	                    <input type="text" placeholder="Number" id="phoneNumber" class="half" <#if model.applicationForm.isSubmitted()>readonly="readonly"</#if>/>
                      <a id="addPhoneButton" class="button blue" style="width: 110px;">Add Phone</a>
                    </div>
                  </div>
                  
                </div>
                
              	<div>
              	           
                    <div class="row" id="existingMessengers">
                  	  <#list model.applicationForm.personalDetails.messengers as messenger >
                  	  	<span>
                  	  		<div class="row">
                  	  	 		<span class="label">Skype</span>    
                  				<div class="field">
                  					<label class="full">${messenger.messengerAddress}</label> 
                  	  				<#if !model.applicationForm.isSubmitted()><a class="button-delete">Delete</a></#if>      
                  	  			</div>                  	  			
                  	  		</div>   
                            <input type="hidden" name="messengers" value='${messenger.asJson}'/>   
                  	  	</span>
                  	  </#list>
                  </div>
                	<div class="row">
                		<span class="label">Skype</span>
                    <span class="hint"></span>
                    <div class="field">                    
	                    <input type="text" placeholder="Address"  id="messenger" class="half" <#if model.applicationForm.isSubmitted()>readonly="readonly"</#if>/>
                      <a id="addMessengerButton" class="button blue" style="width: 110px;">Add Skype</a>
                    </div>
                  </div>
                </div>
                <!--
                <div>
					<div class="row">
                  		<span class="label">Supporting Document</span>
                   	 	<span class="hint"></span>
                   		 <div class="field">
                     	 	<input id ="primaryNationalityDocument" type="file" name="file"/>         
                     		<a class="button" id="primaryNationalityUploadButton">Upload</a>                     		  
                 	  	</div>
                 	  </div>
                </div>-->
              	<div class="buttons">
                  <#if !model.applicationForm.isSubmitted()>
						<a class="button" type="button" id="" name="fundingCancelButton">Cancel</a>
	                    <a id="personalDetailsCloseButton"class="button blue">Close</a>
						<button class="blue" type="button" id="personalDetailsSaveButton" value="close">Save and Close</button>
                  <#else>
                  		<a id="personalDetailsCloseButton"class="button blue">Close</a>			
                  </#if>
                </div>
           </form>
	</div>
		<script type="text/javascript" src="<@spring.url '/design/default/js/libraries.js'/>"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/application/common.js'/>"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/application/personalDetails.js'/>"></script>

		<#if model.result?? && model.result.hasErrors()  >

<#else >
<script type="text/javascript">
	$(document).ready(function(){
		$('#personalDetails-H2').trigger('click');
	});
</script>
</#if>