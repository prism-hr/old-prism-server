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
			        <td>${model.applicationForm.applicant.firstName}</td>
			        <td>${model.applicationForm.applicant.lastName}</td>
			        <td>${model.applicationForm.applicant.email}</td>
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
                	<div class="row">
                  	<label class="label">First Name</label>
                    <span class="hint"></span>
                    <div class="field">                    	
                    <#if !model.applicationForm.isSubmitted()>
                    	<input class="full" type="text" value="${model.personalDetails.firstName}" name="firstName" id="firstName"/>
                    	<#if model.hasError('firstName')>                    		
                    			<span style="color:red;"><@spring.message  model.result.getFieldError('firstName').code /></span>                    		
                    	</#if>
                    	<#else>
                    		<input class="full" readonly="readonly" type="email" value="${model.personalDetails.firstName}" name="email" id="email" />	          
                    	</#if>
                    </div>
                  </div>
                	<div class="row">
                  	<label class="label">Last Name</label>
                    <span class="hint"></span>
                    <div class="field">
                     <#if !model.applicationForm.isSubmitted()>
                    	<input class="full" type="text" value="${model.personalDetails.lastName}" name="lastName" id="lastName"/>
                    	<#if model.hasError('lastName')>                    		
                    			<span style="color:red;"><@spring.message  model.result.getFieldError('lastName').code /></span>                    		
                    	</#if>
                    <#else>
                    		<input class="full" readonly="readonly" type="email" value="${model.personalDetails.lastName}" name="email" id="email" />	          
                    </#if>
                    </div>
                  </div>
                	<div class="row">
                  	<label class="label">Gender</label>
                    <div class="field">
                      <label><input class="disabledEle" type="radio" name="gender" /> Male</label>
                      <label><input class="disabledEle" type="radio" name="gender" /> Female</label>
                    </div>
                  </div>
                	<div class="row">
                  	<label class="label">Date of Birth</label>
                    <span class="hint"></span>
                    <input class="half disabledEle" type="date" value="" />
                  </div>
                </div>

              	<div>
                	<div class="row">
                  	<label class="label">Country of Birth</label>
                    <span class="hint"></span>
                    <div class="field">
                      <select class="full disabledEle">
                        <option>United Kingdom</option>
                      </select>
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
                	<strong>UK Visa</strong>
                	<div class="row">
                  	<span class="label">Type</span>
                    <span class="hint"></span>
                    <div class="field">
                      <select class="full disabledEle">
                        <option>Student</option>
                      </select>
                    </div>
                  </div>
                	<div class="row">
                  	<span class="label">Date of Issue</span>
                    <span class="hint"></span>
                    <input class="half disabledEle" type="date" value="" />
                  </div>
                	<div class="row">
                  	<span class="label">Date of Expiry</span>
                    <span class="hint"></span>
                    <input class="half disabledEle" type="date" value="" />
                  </div>
                	<div class="row">
                  	<span class="label">Supporting Document</span>
                    <span class="hint"></span>
                    <div class="field">
                      <input class="full disabledEle" type="text" value="" />
                      <a class="button" href="#">Browse</a>
                      <a class="button" href="#">Upload</a>
                    </div>
                  </div>
                	<div class="row">
                  	<div class="field"><a class="button blue disabledEle" href="#">Add a visa</a></div>
                  </div>
                </div>

              	<div>
                	<strong>Contact Details</strong>
                	<div class="row">
                		<span class="label">Email</span>
                    <span class="hint"></span>
                    <div class="field">
                        <#if !model.applicationForm.isSubmitted()>
	                    	<input class="full" type="email" value="${model.personalDetails.email}" name="email" id="email" />	                    
	                     	<#if model.hasError('email')>                    		
                    			<span style="color:red;"><@spring.message  model.result.getFieldError('email').code /></span>                    		
                    		</#if>
                    	<#else>
                    		<input class="full" readonly="readonly" type="email" value="${model.personalDetails.email}" name="email" id="email" />	          
                    	</#if>
                      <a class="button disabledEle" href="#" style="width: 110px;">Add Email</a>
                    </div>
                  </div>
                </div>
                
              	<div>
                	<div class="row">
                		<span class="label">Telephone</span>
                    <span class="hint"></span>
                    <div class="field">
                    	<select class="half disabledEle">
                      	<option>Home</option>
                      </select>
	                    <input class="half disabledEle" type="text" placeholder="Number" />
                      <a class="button" href="#" style="width: 110px;">Add Phone</a>
                    </div>
                  </div>
                </div>
                
              	<div>
                	<div class="row">
                		<span class="label">Messenger</span>
                    <span class="hint"></span>
                    <div class="field">
                    	<select class="half disabledEle">
                      	<option>Skype</option>
                      </select>
	                    <input class="half disabledEle" type="text" placeholder="Address" />
                      <a class="button disabledEle" href="#" style="width: 110px;">Add Messenger</a>
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
	
		<script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>		
		<script type="text/javascript" src="<@spring.url '/design/default/js/application/personalDetails.js'/>"></script>