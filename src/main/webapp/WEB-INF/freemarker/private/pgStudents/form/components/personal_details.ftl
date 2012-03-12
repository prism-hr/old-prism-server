<#-- Assignments -->
<#import "/spring.ftl" as spring />
<#-- Personal Details Rendering -->
<!-- Personal details -->
	<h2 class="open">
		<span class="left"></span><span class="right"></span><span class="status"></span>
		Personal Details
	</h2>
	
    <div id="personal-details-section" class="open">
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
				<tr>
			    	<td><a class="row-arrow" href="#">-</a></td>
			        <td>${model.applicationForm.personalDetails.firstName!}</td>
			        <td>${model.applicationForm.personalDetails.lastName!}</td>
			        <td>${model.applicationForm.personalDetails.email!}</td>
                  	<td><a class="button-edit" href="#">edit</a></td>
                  	<td><a class="button-close" href="#">close</a></td>
			    </tr>
			</tbody>
		</table>
		<form id ="documentUploadForm" enctype="multipart/form-data" method="post" action="/pgadmissions/documents" target="myframe">		
				<input type="hidden" name="id" id="id" value="${(model.applicationForm.personalDetails.id?string("######"))!}"/>
				<input type="hidden" id="appId" name="appId" value="${model.applicationForm.id?string("######")}"/>
                <input type="hidden" id="form-display-state" value="${formDisplayState}"/>
              	<div>
        <#if model.hasError('personalDetails')>                           
                <p style="color:red;"><@spring.message  model.result.getFieldError('personalDetails').code /></p>                        
        </#if>
                	<div class="row">
                  	<label class="label">First Name</label>
                    <span class="hint"></span>
                    <div class="field">                    	
                    <#if !model.applicationForm.isSubmitted()>
                    	<input class="full" type="text" value="${model.applicationForm.personalDetails.firstName!}" name="firstName" id="firstName"/>
                    	<#if model.hasError('firstName')>                    		
                    			<span style="color:red;"><@spring.message  model.result.getFieldError('firstName').code /></span>                    		
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
                    			<span style="color:red;"><@spring.message  model.result.getFieldError('lastName').code /></span>                    		
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
                          		/> ${gender.displayValue}</label>
                                             
                        </#list>                  		
            
                      <#if model.hasError('gender')>                         
                                <span style="color:red;"><@spring.message  model.result.getFieldError('gender').code /></span>                           
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
                                <span style="color:red;"><@spring.message  model.result.getFieldError('dateOfBirth').code /></span>                           
                        </#if>
                    <#else>
                        <input class="full" readonly="readonly" type="date" value="${(model.applicationForm.personalDetails.dateOfBirth?string('dd-MMM-yyyy'))!}" name="dateOfBirth" id="dateOfBirth" />             
                    </#if>    
                    </div>
                  </div>
                </div>

              	<div>
                	<div class="row">
                  	<label class="label">Country of Birth</label>
                    <span class="hint"></span>
                    <div class="field">
                      <select name="country" id="country">
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
                                <span style="color:red;"><@spring.message  model.result.getFieldError('country').code /></span>                           
                        </#if>
                    </div>
                  </div>
                </div>

              	<div>
                	<strong>Nationality</strong>
                	<div class="row">
                  	<span class="label">Country</span>
                    <span class="hint"></span>
                    <div class="field">
                      <select class="full disabledEle">
                        <option>British</option>
                      </select>
                      <label><input class="disabledEle" type="radio" /> This is my primary nationality</label>
                    </div>
                  </div>
                             <!-- Document -->
                  <div class="row">
                    <span class="label">Supporting Document</span>
                    <span class="hint"></span>
                     <iframe id="myframe" name="myframe" src="#" style=""><script language="javascript" type="text/javascript">
						
					</script>
					</iframe>
                    
						<input type="file" id="file" name="file">
						<input type="submit" value="Upload"/>
				
                      <a class="button" href="#">Add Document</a>                  
                  
                	<div class="row">
                  		<div class="field"><a class="button blue disabledEle" href="#">Add a nationality</a></div>
                  </div>
                
              	
              	<div>
                	<div class="row">
                  	<label class="label">Language</label>
                    <span class="hint"></span>
                    <div class="field">
                      <select class="full disabledEle">
                        <option>English</option>
                      </select>
                      <label><input class="disabledEle" type="radio" /> This is my primary language</label>
                    </div>
                  </div>
                	<div class="row">
                  	<span class="label">Aptitude</span>
                    <span class="hint"></span>
                    <div class="field">
                      <select class="full disabledEle">
                        <option>Native Speaker</option>
                      </select>
                    </div>
                  </div>
                	<div class="row">
                  	<div class="field"><a class="button blue disabledEle" href="#">Add a language</a></div>
                  </div>
                </div>

              	<div>
                	<strong>Residence</strong>
                	<div class="row">
                  	<span class="label">Country</span>
                    <span class="hint"></span>
                    <div class="field">
                      <select name="residenceCountry" id="residenceCountry">
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
                                <span style="color:red;"><@spring.message  model.result.getFieldError('residenceCountry').code /></span>                           
                        </#if>
                    </div>
                  </div>
                  <div class="row">
                    <span class="label">Status</span>
                    <span class="hint"></span>
                    <div class="field">
                      <select name="residenceStatus" id="residenceStatus">
                         <#list model.residenceStatuses as residenceStatus>
                              <option value="${residenceStatus}">${residenceStatus.displayValue}</option>               
                        </#list>
                      </select>
                      <#if model.hasError('residenceStatus')>                         
                                <span style="color:red;"><@spring.message  model.result.getFieldError('residenceStatus').code /></span>                           
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
                    			<span style="color:red;"><@spring.message  model.result.getFieldError('email').code /></span>                    		
                    		</#if>
                    	<#else>
                    		<input class="full" readonly="readonly" type="email" value="${model.applicationForm.personalDetails.email!}" name="email" id="email" />	          
                    	</#if>
                    </div>
                  </div>
                </div>
                
              	<div>
                	<div class="row">
                		<span class="label">Telephone</span>
                    <span class="hint"></span>
                    <div id="phonenumbers"  class="field">
                    <#list model.applicationForm.personalDetails.phoneNumbers! as phoneNumber>
                    <span name="phone_number">
                   		 ${phoneNumber.telephoneType.displayValue} ${phoneNumber.telephoneNumber} <a class="button">delete</a>
						<input type="hidden" name="phoneNumbers" value='{"type" :"${phoneNumber.telephoneType}", "number":"${phoneNumber.telephoneNumber}"}' />								
						<br/>
					</span>
                    </#list>
                  	</div>
                    <div class="field">
                    	<select class="full" id="phoneType">
                    	 <#list model.phoneTypes as phoneType >
                      		<option value="${phoneType}">${phoneType.displayValue}</option>
                      	</#list>
                      </select>
	                    <input type="text" placeholder="Number" id="phoneNumber"/>
                      <a id="addPhoneButton" class="button" style="width: 110px;">Add Phone</a>
                    </div>
                  </div>
                  
                </div>
                
              	<div>
                	<div class="row">
                		<span class="label">Messenger</span>
                    <span class="hint"></span>
                    <div class="field">
                    	<select class="full">
                      	<option>Skype</option>
                      </select>
	                    <input type="text" placeholder="Address" />
                      <a href="#" class="button" style="width: 110px;">Add Messenger</a>
                    </div>
                  </div>
                </div>

              	<div class="buttons">
                  <a id="close-section-button"class="button blue">Close</a>
                  <#if !model.applicationForm.isSubmitted()>
                    <a class="button blue" id="personalDetailsSaveButton">Save</a>
                  </#if>
                </div>
           </form>
	</div>

		<script type="text/javascript" src="<@spring.url '/design/default/js/application/personalDetails.js'/>"></script>
		