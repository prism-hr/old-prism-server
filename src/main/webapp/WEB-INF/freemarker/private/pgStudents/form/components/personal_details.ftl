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
			        <td>${model.personalDetails.firstName!}</td>
			        <td>${model.personalDetails.lastName!}</td>
			        <td>${model.personalDetails.email!}</td>
                  	<td><a class="button-edit" href="#">edit</a></td>
                  	<td><a class="button-close" href="#">close</a></td>
			    </tr>
			</tbody>
		</table>
		<form>
				<input type="hidden" name="id" id="id" value="${model.user.id?string("######")}"/>
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
                    	<input class="full" type="text" value="${model.personalDetails.firstName!}" name="firstName" id="firstName"/>
                    	<#if model.hasError('firstName')>                    		
                    			<span style="color:red;"><@spring.message  model.result.getFieldError('firstName').code /></span>                    		
                    	</#if>
                    	<#else>
                    		<input class="full" readonly="readonly" type="text" value="${model.personalDetails.firstName!}" name="firstName" id="firstName" />	          
                    	</#if>
                    </div>
                  </div>
                	<div class="row">
                  	<label class="label">Last Name</label>
                    <span class="hint"></span>
                    <div class="field">
                     <#if !model.applicationForm.isSubmitted()>
                    	<input class="full" type="text" value="${model.personalDetails.lastName!}" name="lastName" id="lastName"/>
                    	<#if model.hasError('lastName')>                    		
                    			<span style="color:red;"><@spring.message  model.result.getFieldError('lastName').code /></span>                    		
                    	</#if>
                    <#else>
                    		<input class="full" readonly="readonly" type="text" value="${model.personalDetails.lastName!}" name="lastName" id="lastName" />	          
                    </#if>
                    </div>
                  </div>
                	<div class="row">
                  	<label class="label">Gender</label>
                    <div class="field">
                      <label><input type="radio" name="genderRadio" id="maleGender" value="MALE"/> Male</label>
                      <label><input type="radio" name="genderRadio" id="femaleGender" value="FEMALE"/> Female</label>
                      <label><input type="radio" name="genderRadio" id="notSaidGender" value="PREFER NOT TO SAY"/> Prefer not to say</label>
                      <input type="hidden" id="gender" name="gender"/>
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
                        <input class="full" type="date" value="${(model.personalDetails.dateOfBirth?string('yyyy/MM/dd'))!}" name="dateOfBirth" id="dateOfBirth"/>
                        <#if model.hasError('dateOfBirth')>                           
                                <span style="color:red;"><@spring.message  model.result.getFieldError('dateOfBirth').code /></span>                           
                        </#if>
                    <#else>
                        <input class="full" readonly="readonly" type="date" value="${(model.personalDetails.dateOfBirth?string('yyyy/MM/dd'))!}" name="dateOfBirth" id="dateOfBirth" />             
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
                        <#list model.countries as country>
                              <option value="${country.name}">${country.name}</option>               
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
                	<div class="row">
                  	<div class="field"><a class="button blue disabledEle" href="#">Add a nationality</a></div>
                  </div>
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
                        <#list model.countries as country>
                              <option value="${country.name}">${country.name}</option>               
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
                              <option value="${residenceStatus.freeVal}">${residenceStatus.freeVal}</option>               
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
	                    	<input class="full" type="email" value="${model.personalDetails.email!}" name="email" id="email" />	                    
	                     	<#if model.hasError('email')>                    		
                    			<span style="color:red;"><@spring.message  model.result.getFieldError('email').code /></span>                    		
                    		</#if>
                    	<#else>
                    		<input class="full" readonly="readonly" type="email" value="${model.personalDetails.email!}" name="email" id="email" />	          
                    	</#if>
                    </div>
                  </div>
                </div>
                
              	<div>
                	<div class="row">
                		<span class="label">Telephone</span>
                    <span class="hint"></span>
                    <div class="field">
                    	<select class="full disabledEle">
                      	<option>Home</option>
                      </select>
	                    <input type="text" placeholder="Number" />
                      <a class="button" href="#" style="width: 110px;">Add Phone</a>
                    </div>
                  </div>
                </div>
                
              	<div>
                	<div class="row">
                		<span class="label">Messenger</span>
                    <span class="hint"></span>
                    <div class="field">
                    	<select class="full disabledEle">
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