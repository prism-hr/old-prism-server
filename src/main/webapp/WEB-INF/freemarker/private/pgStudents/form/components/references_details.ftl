<#import "/spring.ftl" as spring />
<#setting locale = "en_US">
<#assign errorCode = RequestParameters.errorCode! />
<#if applicationForm.referees?has_content>
  <#assign hasReferees = true>
<#else>
  <#assign hasReferees = false>
</#if>

<a name="references-details"></a> 
<h2 id="referee-H2" class="empty open">
  <span class="left"></span><span class="right"></span><span class="status"></span>
  References<em>*</em>
</h2>

<div class="open">
  
  <#if hasReferees>
  <table class="existing">
  
    <colgroup>
      <col style="width: 30px" />
      <col/>
      <col style="width: 60px" />
    </colgroup>
  
    <thead>
      <tr>
        <th id="primary-header">&nbsp;</th>
        <th>Reference</th>          
        <th id="last-col">&nbsp;</th>
      </tr>
    </thead>

    <tbody>
      <#list applicationForm.referees as existingReferee>
      <tr>
        <td><a class="row-arrow">-</a></td>
        <td>${(existingReferee.firstname?html)!} ${(existingReferee.lastname?html)!} 
        (${(existingReferee.email?html)!})</td>
        <#assign encExistingRefereeId = encrypter.encrypt(existingReferee.id) />
				<td>
					<#if existingReferee.editable>
						<a name="editRefereeLink" data-desc="Edit" id="referee_${encExistingRefereeId}" class="button-edit button-hint">edit</a>
          <#elseif existingReferee.declined || existingReferee.hasProvidedReference()>
						<a name="editRefereeLink" id="referee_${encExistingRefereeId}" class="button-responded" data-desc="Responded">Responded</a>
					<#else>
						<a name="editRefereeLink" data-desc="Show" id="referee_${encExistingRefereeId}" class="button-show button-hint">show</a>
          </#if>
          <#if applicationForm.isInState('UNSUBMITTED')>
            <a name="deleteRefereeButton" data-desc="Delete" id="referee_${encExistingRefereeId}" class="button-delete button-hint">delete</a>        
          </#if>
				</td>
      </tr>    
      </#list>
    </tbody>
  </table>
  </#if>

  <input type="hidden" id="refereeId" name="refereeId" value="<#if referee.id??>${encrypter.encrypt(referee.id)}</#if>" />

  <form>
  
  <#if errorCode?? && errorCode=="true">
    <div class="section-error-bar">
      <span class="error-hint" data-desc="Please provide all mandatory fields in this section."></span>               
      <@spring.message 'referencesDetails.sectionInfo'/>
    </div>
  <#else>
    <div id="ref-info-bar-div" class="section-info-bar">
      <@spring.message 'referencesDetails.sectionInfo'/> 
    </div>  
  </#if>

  <div class="row-group">
    <!-- First name -->
    <div class="row">
    <#if referee.editable && (applicationForm.referees?size &lt; 3 || referee.id??) >
      <span class="plain-label">First Name<em>*</em></span>
      <span class="hint" data-desc="<@spring.message 'referee.firstname'/>"></span>
      <div class="field">
        <input class="full" id="ref_firstname" name="ref_firstname" value="${(referee.firstname?html)!}"/>  
      </div>
    <#else>
      <span class="plain-label grey-label">First Name</span>
      <span class="hint"></span>
      <div class="field">
        <input readonly="readonly" class="full" id="ref_firstname" name="ref_firstname" value="${(referee.firstname?html)!}" disabled="disabled"/>
      </div>
    </#if>
    </div>

    <@spring.bind "referee.firstname" />    
    <#list spring.status.errorMessages as error>
    <div class="row">
      <div class="field">
        <span class="invalid">${error}</span>
      </div>
    </div>
    </#list>
  
    <!-- Last name -->
    <div class="row">
      <#if referee.editable && (applicationForm.referees?size &lt; 3 || referee.id??) >
        <span class="plain-label">Last Name<em>*</em></span>
        <span class="hint" data-desc="<@spring.message 'referee.lastname'/>"></span>
        <div class="field">
          <input class="full" id="ref_lastname" name="ref_lastname" value="${(referee.lastname?html)!}"/>
        </div>            
      <#else>
        <span class="plain-label grey-label">Last Name</span>
        <span class="hint"></span>
        <div class="field">
          <input readonly="readonly" class="full" id="ref_lastname" name="ref_lastname" value="${(referee.lastname?html)!}" disabled="disabled"/>
        </div>
      </#if>
    </div>
  
    <@spring.bind "referee.lastname" /> 
    <#list spring.status.errorMessages as error>
    <div class="row">
      <div class="field">
        <span class="invalid">${error}</span>
      </div>
    </div>
    </#list>
  
  </div>

  <div class="row-group">
  
    <!-- Employer / company name -->
    <div class="row">
      <#if referee.editable && (applicationForm.referees?size &lt; 3 || referee.id??) >
        <span class="plain-label">Employer<em>*</em></span>
        <span class="hint" data-desc="<@spring.message 'referee.employer'/>"></span>
        <div class="field">
          <input class="full" id="ref_employer" name="ref_employer" value="${(referee.jobEmployer?html)!}"/>
        </div>            
      <#else>
        <span class="plain-label grey-label">Employer</span>
        <span class="hint"></span>
        <div class="field">
          <input readonly="readonly" class="full" id="ref_employer" name="ref_employer" value="${(referee.jobEmployer?html)!}" disabled="disabled"/>
        </div>
      </#if>
    </div>

    <@spring.bind "referee.jobEmployer" />     
    <#list spring.status.errorMessages as error>
    <div class="row">
      <div class="field">
        <span class="invalid">${error}</span>
      </div>
    </div>
    </#list>

    <!-- Position title -->
    <div class="row">
      <#if referee.editable && (applicationForm.referees?size &lt; 3 || referee.id??) >
        <span class="plain-label">Position<em>*</em></span>
        <span class="hint" data-desc="<@spring.message 'referee.position'/>"></span>
        <div class="field">
          <input class="full" id="ref_position" name="ref_position" value="${(referee.jobTitle?html)!}"/>
        </div>            
      <#else>
        <span class="plain-label grey-label">Position</span>
        <span class="hint"></span>
        <div class="field">
          <input readonly="readonly" class="full" id="ref_position" name="ref_position" value="${(referee.jobTitle?html)!}" disabled="disabled"/>
        </div>
      </#if>
    </div>
  
    <@spring.bind "referee.jobTitle" />
    <#list spring.status.errorMessages as error>
    <div class="row">
      <div class="field">
        <span class="invalid">${error}</span>
      </div>
    </div>
    </#list>
  
  </div>

  <div class="row-group">

    <!-- Address body -->
    
      <div class="row">
        <label class=
        <#if referee.editable && (applicationForm.referees?size &lt; 3 || referee.id??) >
        "group-heading-label"
        <#else>
        "group-heading-label grey-label"
        </#if>
        >Address</label>
      </div> 
      <div class="row">
        <span class=
        <#if referee.editable && (applicationForm.referees?size &lt; 3 || referee.id??) >
        "plain-label"
        <#else>
        "plain-label grey-label"
        </#if>
        >House name / number & street<em>*</em></span>
        <span class="hint" data-desc="<@spring.message 'addressDetails.house'/>"></span>
        <div class="field">
          <input class="max" id="ref_address_location1" name="ref_address_location1" value="${(referee.addressLocation.address1?html)!}"
          <#if !referee.editable || !(applicationForm.referees?size &lt; 3 || referee.id??) >
          readonly="readonly" disabled="disabled"
          </#if>
          /> 
        </div>
      </div>          
    
    <div class="row">
        <div class="field">
          <input class="max" id="ref_address_location2" name="ref_address_location2" value="${(referee.addressLocation.address2?html)!}"
          <#if !referee.editable || !(applicationForm.referees?size &lt; 3 || referee.id??) >
          readonly="readonly" disabled="disabled"
          </#if>
          /> 
        </div>
      </div>          
      <@spring.bind "referee.addressLocation.address1" />         
    <#list spring.status.errorMessages as error>
      <div class="row">
        <div class="field">
          <span class="invalid">${error}</span>
        </div>
      </div>
    </#list>
    
    <@spring.bind "referee.addressLocation.address2" />         
    <#list spring.status.errorMessages as error>
      <div class="row">
        <div class="field">
          <span class="invalid">${error}</span>
        </div>
      </div>
    </#list>
    
    <div class="row">
        <span class=
        <#if referee.editable && (applicationForm.referees?size &lt; 3 || referee.id??) >
        "plain-label"
        <#else>
        "plain-label grey-label"
        </#if>
        >Town / city / suburb<em>*</em></span>
        <span class="hint" data-desc="<@spring.message 'addressDetails.town'/>"></span>
        <div class="field">
          <input class="max" id="ref_address_location3" name="ref_address_location3" value="${(referee.addressLocation.address3?html)!}"
          <#if !referee.editable || !(applicationForm.referees?size &lt; 3 || referee.id??) >
          readonly="readonly" disabled="disabled"
          </#if>
          /> 
        </div>
      </div>          

    <@spring.bind "referee.addressLocation.address3" />         
    <#list spring.status.errorMessages as error>
      <div class="row">
        <div class="field">
          <span class="invalid">${error}</span>
        </div>
      </div>
    </#list>
    
    <div class="row">
        <span class=
        <#if referee.editable && (applicationForm.referees?size &lt; 3 || referee.id??) >
        "plain-label"
        <#else>
        "plain-label grey-label"
        </#if>
        >State / county / region</span>
        <span class="hint" data-desc="<@spring.message 'addressDetails.state'/>"></span>
        <div class="field">
          <input class="max" id="ref_address_location4" name="ref_address_location4" value="${(referee.addressLocation.address4?html)!}"
          <#if !referee.editable || !(applicationForm.referees?size &lt; 3 || referee.id??) >
          readonly="readonly" disabled="disabled"
          </#if>
          /> 
        </div>
      </div>          

    <@spring.bind "referee.addressLocation.address4" />         
    <#list spring.status.errorMessages as error>
      <div class="row">
        <div class="field">
          <span class="invalid">${error}</span>
        </div>
      </div>
    </#list>
    
    <div class="row">
        <span class=
        <#if referee.editable && (applicationForm.referees?size &lt; 3 || referee.id??) >
        "plain-label"
        <#else>
        "plain-label grey-label"
        </#if>
        >Post / zip / area code</span>
        <span class="hint" data-desc="<@spring.message 'addressDetails.zip'/>"></span>
        <div class="field">
          <input class="max" id="ref_address_location5" name="ref_address_location5" value="${(referee.addressLocation.address5?html)!}"
          <#if !referee.editable || !(applicationForm.referees?size &lt; 3 || referee.id??) >
          readonly="readonly" disabled="disabled"
          </#if>
          /> 
        </div>
      </div>          

    <@spring.bind "referee.addressLocation.address5" />         
    <#list spring.status.errorMessages as error>
      <div class="row">
        <div class="field">
          <span class="invalid">${error}</span>
        </div>
      </div>
    </#list>

    <!-- Country -->
    <div class="row">
      <#if referee.editable && (applicationForm.referees?size &lt; 3 || referee.id??) >
        <span class="plain-label">Country<em>*</em></span>
        <span class="hint" data-desc="<@spring.message 'referee.country'/>"></span>
        <div class="field">
          <select class="full" name="ref_address_country" id="ref_address_country">
            <option value="">Select...</option>
            <#list countries as country>
            <option value="${encrypter.encrypt(country.id)}" 
            <#if referee.addressLocation.country?? && referee.addressLocation.country.id == country.id> 
              selected="selected"</#if>>${country.name?html}</option>               
            </#list>
          </select>
        </div>
      <#else>
        <span class="plain-label grey-label">Country</span>
        <span class="hint"></span>
        <div class="field">
          <select class="full" name="ref_address_country" id="ref_address_country" disabled="disabled">
          </select>
        </div>
      </#if>
    </div>

    <@spring.bind "referee.addressLocation.country" /> 
    <#list spring.status.errorMessages as error>
      <div class="row">
        <div class="field">
          <span class="invalid">${error}</span>
        </div>
      </div>
    </#list>

  </div>

  <div class="row-group">

    <!-- Email address -->
    <#if referee.editable && (applicationForm.referees?size &lt; 3 || referee.id??) >
      <div class="row">
        <label class="group-heading-label">Contact Details</label>
      </div> 
      <div class="row">    
        <span class="plain-label">Email<em>*</em></span>
        <span class="hint" data-desc="<@spring.message 'referee.email'/>"></span>
        <div class="field">
          <input class="full" type="email" id="ref_email" name="ref_email" value="${(referee.email?html)!}"/>                
        </div>
      </div>            
    <#else>
      <div class="row">
        <label class="group-heading-label grey-label">Contact Details</label>
      </div> 
      <div class="row">                
        <span class="plain-label grey-label">Email</span>
        <span class="hint"></span>
        <div class="field">
          <input readonly="readonly" class="full" type="email" id="ref_email" name="ref_email" value="${(referee.email?html)!}" disabled="disabled"/>                
        </div>
      </div>
    </#if>

    <@spring.bind "referee.email" />           
    <#list spring.status.errorMessages as error>
      <div class="row">
        <div class="field">
          <span class="invalid">${error}</span>
        </div>
      </div>
    </#list>

    <!-- Telephone -->
    <div class="row">
      <#if referee.editable && (applicationForm.referees?size &lt; 3 || referee.id??) >
        <span class="plain-label">Telephone<em>*</em></span>
        <span class="hint" data-desc="<@spring.message 'referee.telephone'/>"></span>
        <div class="field">
          <input class="full" id="refPhoneNumber" name="refPhoneNumber" placeholder="e.g. +44 (0) 123 123 1234" value="${(referee.phoneNumber?html)!}"/>                
        </div>            
      <#else>
        <span class="plain-label grey-label">Telephone</span>
        <span class="hint"></span>
        <div class="field">
          <input readonly="readonly" class="full" id="refPhoneNumber" name="refPhoneNumber" value="${(referee.phoneNumber?html)!}" disabled="disabled"/>                
        </div>
      </#if>
    </div> 

    <@spring.bind "referee.phoneNumber" />
    <#list spring.status.errorMessages as error>
      <div class="row">
        <div class="field">
          <span class="invalid">${error}</span>
        </div>
      </div>
    </#list>

    <!-- Skype address -->
    <div class="row">
      <#if referee.editable && (applicationForm.referees?size &lt; 3 || referee.id??) >
        <span class="plain-label">Skype Name</span>
        <span class="hint" data-desc="<@spring.message 'referee.skype'/>"></span>
        <div class="field">
          <input class="full" id="ref_messenger" name="ref_messenger" value="${(referee.messenger?html)!}"/>                
        </div>            
      <#else>
        <span class="plain-label grey-label">Telephone</span>
        <span class="hint"></span>
        <div class="field">
          <input readonly="readonly" class="full" id="ref_messenger" name="ref_messenger" value="${(referee.messenger?html)!}" disabled="disabled"/>                
        </div>
      </#if>
    </div>
    
    <@spring.bind "referee.messenger" />         
    <#list spring.status.errorMessages as error>
      <div class="row">
        <div class="field">
          <span class="invalid">${error}</span>
        </div>
      </div>
    </#list>

    <#if referee.editable >
      <!-- Add another button -->
      <div class="row">
        <div class="field">
          <#if referee.id?? || applicationForm.referees?size &lt; 3>
            <button type="button" id="addReferenceButton" class="blue"><#if referee.id??>Update<#else>Add</#if></button>
          <#else>
            <button type="button" class="blue" style="cursor:default">Add</button>
          </#if>
        </div>
      </div>
    </#if>    
  </div>

  <#if applicationForm.isModifiable() && !applicationForm.isInState('UNSUBMITTED')>
  	<@spring.bind "referee.acceptedTerms" />
   	<#if spring.status.errorMessages?size &gt; 0>        
	    <div class="row-group terms-box invalid" >

  	<#else>
		<div class="row-group terms-box" >
 	 </#if>
    <div class="row">
      <span class="terms-label<#if applicationForm.referees?size &gt;= 3> grey-label</#if>">
			Confirm that the information that you have provided in this section is true 
			and correct. Failure to provide true and correct information may result in a 
			subsequent offer of study being withdrawn.				
      </span>
      <div class="terms-field">
        <input type="checkbox" name="acceptTermsRDCB" id="acceptTermsRDCB"<#if applicationForm.referees?size &gt; 3> disabled="disabled"</#if> />
      </div>
      <input type="hidden" name="acceptTermsRDValue" id="acceptTermsRDValue"/>
    </div>          
  </div>
  </#if>  

  <div class="buttons">
    <#if applicationForm.modifiable>
      <button class="clear" type="button" id="refereeClearButton" name="refereeClearButton">Clear</button>
      <button class="blue" type="button" id="refereeCloseButton" name="refereeCloseButton">Close</button>
      <button class="blue" type="button" id="refereeSaveAndCloseButton" value="close">Save</button>
    <#else>
      <button type="button" id="refereeCloseButton">Close</a>
    </#if>   
  </div>

</form>

</div>

<script type="text/javascript" src="<@spring.url '/design/default/js/application/referee.js'/>"></script>
