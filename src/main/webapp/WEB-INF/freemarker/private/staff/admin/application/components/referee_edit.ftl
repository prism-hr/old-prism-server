<div class="row-group">
    
    <!-- First name -->
    <div class="row">
      <span class="plain-label">First Name<em>*</em></span>
      <span class="hint" data-desc="<@spring.message 'referee.firstname'/>"></span>
      <div class="field">
        <input class="full" id="firstname_${encRefereeId}" name="ref_firstname" value="${(refereesAdminEditDTO.firstname?html)!}"/>  
      </div>
    </div>

    <@spring.bind "refereesAdminEditDTO.firstname" />    
    <#list spring.status.errorMessages as error>
    <div class="row">
      <div class="field">
        <span class="invalid">${error}</span>
      </div>
    </div>
    </#list>
  
    <!-- Last name -->
    <div class="row">
        <span class="plain-label">Last Name<em>*</em></span>
        <span class="hint" data-desc="<@spring.message 'referee.lastname'/>"></span>
        <div class="field">
          <input class="full" id="lastname_${encRefereeId}" name="ref_lastname" value="${(refereesAdminEditDTO.lastname?html)!}"/>
        </div>            
    </div>
  
    <@spring.bind "refereesAdminEditDTO.lastname" /> 
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
        <span class="plain-label">Employer<em>*</em></span>
        <span class="hint" data-desc="<@spring.message 'referee.employer'/>"></span>
        <div class="field">
          <input class="full" id="employer_${encRefereeId}" name="ref_employer" value="${(refereesAdminEditDTO.jobEmployer?html)!}"/>
        </div>            
    </div>

    <@spring.bind "refereesAdminEditDTO.jobEmployer" />     
    <#list spring.status.errorMessages as error>
    <div class="row">
      <div class="field">
        <span class="invalid">${error}</span>
      </div>
    </div>
    </#list>

    <!-- Position title -->
    <div class="row">
        <span class="plain-label">Position<em>*</em></span>
        <span class="hint" data-desc="<@spring.message 'referee.position'/>"></span>
        <div class="field">
          <input class="full" id="position_${encRefereeId}" name="ref_position" value="${(refereesAdminEditDTO.jobTitle?html)!}"/>
        </div>            
    </div>
  
    <@spring.bind "refereesAdminEditDTO.jobTitle" />
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
        <label class="group-heading-label">Address</label>
      </div> 
      <div class="row">
        <span class="plain-label">House name / number & street<em>*</em></span>
        <span class="hint" data-desc="<@spring.message 'addressDetails.house'/>"></span>
        <div class="field">
          <input class="max" id="address_location1_${encRefereeId}" name="ref_address_location1" value="${(refereesAdminEditDTO.addressLocation.address1?html)!}"/> 
        </div>
      </div>          
    
    <div class="row">
        <div class="field">
          <input class="max" id="address_location2_${encRefereeId}" name="ref_address_location2" value="${(refereesAdminEditDTO.addressLocation.address2?html)!}"/> 
        </div>
      </div>          
      <@spring.bind "refereesAdminEditDTO.addressLocation.address1" />         
    <#list spring.status.errorMessages as error>
      <div class="row">
        <div class="field">
          <span class="invalid">${error}</span>
        </div>
      </div>
    </#list>
    
    <@spring.bind "refereesAdminEditDTO.addressLocation.address2" />         
    <#list spring.status.errorMessages as error>
      <div class="row">
        <div class="field">
          <span class="invalid">${error}</span>
        </div>
      </div>
    </#list>
    
    <div class="row">
        <span class="plain-label">Town / city / suburb<em>*</em></span>
        <span class="hint" data-desc="<@spring.message 'addressDetails.town'/>"></span>
        <div class="field">
          <input class="max" id="address_location3_${encRefereeId}" name="ref_address_location3" value="${(refereesAdminEditDTO.addressLocation.address3?html)!}"/> 
        </div>
      </div>          

    <@spring.bind "refereesAdminEditDTO.addressLocation.address3" />         
    <#list spring.status.errorMessages as error>
      <div class="row">
        <div class="field">
          <span class="invalid">${error}</span>
        </div>
      </div>
    </#list>
    
    <div class="row">
        <span class="plain-label">State / county / region</span>
        <span class="hint" data-desc="<@spring.message 'addressDetails.state'/>"></span>
        <div class="field">
          <input class="max" id="address_location4_${encRefereeId}" name="ref_address_location4" value="${(refereesAdminEditDTO.addressLocation.address4?html)!}"/> 
        </div>
      </div>          

    <@spring.bind "refereesAdminEditDTO.addressLocation.address4" />         
    <#list spring.status.errorMessages as error>
      <div class="row">
        <div class="field">
          <span class="invalid">${error}</span>
        </div>
      </div>
    </#list>
    
    <div class="row">
        <span class="plain-label">Post / zip / area code</span>
        <span class="hint" data-desc="<@spring.message 'addressDetails.zip'/>"></span>
        <div class="field">
          <input class="max" id="address_location5_${encRefereeId}" name="ref_address_location5" value="${(refereesAdminEditDTO.addressLocation.address5?html)!}"/> 
        </div>
      </div>          

    <@spring.bind "refereesAdminEditDTO.addressLocation.address5" />         
    <#list spring.status.errorMessages as error>
      <div class="row">
        <div class="field">
          <span class="invalid">${error}</span>
        </div>
      </div>
    </#list>

    <!-- Country -->
    <div class="row">
        <span class="plain-label">Country<em>*</em></span>
        <span class="hint" data-desc="<@spring.message 'referee.country'/>"></span>
        <div class="field">
          <select class="full" name="ref_address_country" id="address_country_${encRefereeId}">
            <option value="">Select...</option>
            <#list countries as country>
            <option value="${encrypter.encrypt(country.id)}" 
            <#if refereesAdminEditDTO.addressLocation.country?? && refereesAdminEditDTO.addressLocation.country.id == country.id> 
              selected="selected"</#if>>${country.name?html}</option>               
            </#list>
          </select>
        </div>
    </div>

    <@spring.bind "refereesAdminEditDTO.addressLocation.country" /> 
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
      <div class="row">
        <label class="group-heading-label">Contact Details</label>
      </div> 
      <div class="row">    
        <span class="plain-label">Email<em>*</em></span>
        <span class="hint" data-desc="<@spring.message 'referee.email'/>"></span>
        <div class="field">
          <input class="full" type="email" id="email_${encRefereeId}" name="ref_email" value="${(refereesAdminEditDTO.email?html)!}"/>                
        </div>
      </div>            

    <@spring.bind "refereesAdminEditDTO.email" />           
    <#list spring.status.errorMessages as error>
      <div class="row">
        <div class="field">
          <span class="invalid">${error}</span>
        </div>
      </div>
    </#list>

    <!-- Telephone -->
    <div class="row">
        <span class="plain-label">Telephone<em>*</em></span>
        <span class="hint" data-desc="<@spring.message 'referee.telephone'/>"></span>
        <div class="field">
          <input class="full" id="phoneNumber_${encRefereeId}" name="refPhoneNumber" placeholder="e.g. +44 (0) 123 123 1234" value="${(refereesAdminEditDTO.phoneNumber?html)!}"/>                
        </div>            
    </div> 

    <@spring.bind "refereesAdminEditDTO.phoneNumber" />
    <#list spring.status.errorMessages as error>
      <div class="row">
        <div class="field">
          <span class="invalid">${error}</span>
        </div>
      </div>
    </#list>

    <!-- Skype address -->
    <div class="row">
        <span class="plain-label">Skype Name</span>
        <span class="hint" data-desc="<@spring.message 'referee.skype'/>"></span>
        <div class="field">
          <input class="full" id="messenger_${encRefereeId}" name="ref_messenger" value="${(refereesAdminEditDTO.messenger?html)!}"/>                
        </div>            
    </div>
    
    <@spring.bind "refereesAdminEditDTO.messenger" />         
    <#list spring.status.errorMessages as error>
      <div class="row">
        <div class="field">
          <span class="invalid">${error}</span>
        </div>
      </div>
    </#list>

</div>
