<div class="row-group aSDisplay"> 
  
  <!-- First name -->
  <div class="row">
    <label class="plain-label" for="firstname_${encRefereeId}">First Name<em>*</em></label>
    <span class="hint" data-desc="<@spring.message 'referee.firstname'/>"></span>
    <div class="field">
      <input type="text" class="full" id="firstname_${encRefereeId}" name="ref_firstname" value="${(refereesAdminEditDTO.firstname?html)!}"/>
      <@spring.bind "refereesAdminEditDTO.firstname" />
      <#list spring.status.errorMessages as error>
      <div class="alert alert-error"> <i class="icon-warning-sign"></i>
        ${error}
      </div>
      </#list> </div>
  </div>
  
  <!-- Last name -->
  <div class="row">
    <label class="plain-label" for="lastname_${encRefereeId}">Last Name<em>*</em></label>
    <span class="hint" data-desc="<@spring.message 'referee.lastname'/>"></span>
    <div class="field">
      <input type="text" class="full" id="lastname_${encRefereeId}" name="ref_lastname" value="${(refereesAdminEditDTO.lastname?html)!}"/>
      <@spring.bind "refereesAdminEditDTO.lastname" />
      <#list spring.status.errorMessages as error>
      <div class="alert alert-error"> <i class="icon-warning-sign"></i>
        ${error}
      </div>
      </#list> </div>
  </div>
</div>
<div class="row-group"> 
  
  <!-- Employer / company name -->
  <div class="row">
    <label class="plain-label" for="employer_${encRefereeId}">Employer<em>*</em></label>
    <span class="hint" data-desc="<@spring.message 'referee.employer'/>"></span>
    <div class="field">
      <input type="text" class="full" id="employer_${encRefereeId}" name="ref_employer" value="${(refereesAdminEditDTO.jobEmployer?html)!}"/>
      <@spring.bind "refereesAdminEditDTO.jobEmployer" />
      <#list spring.status.errorMessages as error>
      <div class="alert alert-error"> <i class="icon-warning-sign"></i>
        ${error}
      </div>
      </#list> </div>
  </div>
  
  <!-- Position title -->
  <div class="row">
    <label class="plain-label" for="position_${encRefereeId}">Position<em>*</em></label>
    <span class="hint" data-desc="<@spring.message 'referee.position'/>"></span>
    <div class="field">
      <input type="text" class="full" id="position_${encRefereeId}" name="ref_position" value="${(refereesAdminEditDTO.jobTitle?html)!}"/>
      <@spring.bind "refereesAdminEditDTO.jobTitle" />
      <#list spring.status.errorMessages as error>
      <div class="alert alert-error"> <i class="icon-warning-sign"></i>
        ${error}
      </div>
      </#list> </div>
  </div>
</div>
<div class="row-group"> 
  
  <!-- Address body -->
  
  <h3 class="group-heading-label">Address</h3>
  <div class="row">
    <label class="plain-label" for="address_location1_${encRefereeId}">House name / number & street<em>*</em></label>
    <span class="hint" data-desc="<@spring.message 'addressDetails.house'/>"></span>
    <div class="field">
      <input type="text" class="max" id="address_location1_${encRefereeId}" name="ref_address_location1" value="${(refereesAdminEditDTO.addressLocation.address1?html)!}"/>
      <input type="text" class="max" id="address_location2_${encRefereeId}" name="ref_address_location2" value="${(refereesAdminEditDTO.addressLocation.address2?html)!}"/>
      <@spring.bind "refereesAdminEditDTO.addressLocation.address1" />
      <#list spring.status.errorMessages as error>
      <div class="alert alert-error"> <i class="icon-warning-sign"></i>
        ${error}
      </div>
      </#list>
      <@spring.bind "refereesAdminEditDTO.addressLocation.address2" />
      <#list spring.status.errorMessages as error>
      <div class="alert alert-error"> <i class="icon-warning-sign"></i>
        ${error}
      </div>
      </#list> </div>
  </div>
  <div class="row">
    <label class="plain-label" for="address_location3_${encRefereeId}">Town / city / suburb<em>*</em></label>
    <span class="hint" data-desc="<@spring.message 'addressDetails.town'/>"></span>
    <div class="field">
      <input type="text" class="max" id="address_location3_${encRefereeId}" name="ref_address_location3" value="${(refereesAdminEditDTO.addressLocation.address3?html)!}"/>
      <@spring.bind "refereesAdminEditDTO.addressLocation.address3" />
      <#list spring.status.errorMessages as error>
      <div class="alert alert-error"> <i class="icon-warning-sign"></i>
        ${error}
      </div>
      </#list> </div>
  </div>
  <div class="row">
    <label class="plain-label" for="address_location4_${encRefereeId}">State / county / region</label>
    <span class="hint" data-desc="<@spring.message 'addressDetails.state'/>"></span>
    <div class="field">
      <input type="text" class="max" id="address_location4_${encRefereeId}" name="ref_address_location4" value="${(refereesAdminEditDTO.addressLocation.address4?html)!}"/>
      <@spring.bind "refereesAdminEditDTO.addressLocation.address4" />
      <#list spring.status.errorMessages as error>
      <div class="alert alert-error"> <i class="icon-warning-sign"></i>
        ${error}
      </div>
      </#list> </div>
  </div>
  <div class="row">
    <label class="plain-label" for="address_location5_${encRefereeId}">Post / zip / area code</label>
    <span class="hint" data-desc="<@spring.message 'addressDetails.zip'/>"></span>
    <div class="field">
      <input type="text" class="max" id="address_location5_${encRefereeId}" name="ref_address_location5" value="${(refereesAdminEditDTO.addressLocation.address5?html)!}"/>
      <@spring.bind "refereesAdminEditDTO.addressLocation.address5" />
      <#list spring.status.errorMessages as error>
      <div class="alert alert-error"> <i class="icon-warning-sign"></i>
        ${error}
      </div>
      </#list> </div>
  </div>
  
  <!-- Country -->
  <div class="row">
    <label class="plain-label" for="address_country_${encRefereeId}">Country<em>*</em></label>
    <span class="hint" data-desc="<@spring.message 'referee.country'/>"></span>
    <div class="field">
      <select class="full" name="ref_address_country" id="address_country_${encRefereeId}">
        <option value="">Select...</option>
        <#list countries as country> <option value="${encrypter.encrypt(country.id)}" 
            <#if refereesAdminEditDTO.addressLocation.country?? && refereesAdminEditDTO.addressLocation.country.id == country.id> 
              selected="selected"</#if>>
        ${country.name?html}
        </option>
        </#list>
      </select>
      <@spring.bind "refereesAdminEditDTO.addressLocation.country" />
      <#list spring.status.errorMessages as error>
      <div class="alert alert-error"> <i class="icon-warning-sign"></i>
        ${error}
      </div>
      </#list> </div>
  </div>
</div>
<div class="row-group"> 
  
  <!-- Email address -->
  
  <h3 class="group-heading-label">Contact Details</h3>
  <div class="row">
    <label class="plain-label" for="email_${encRefereeId}">Email<em>*</em></label>
    <span class="hint" data-desc="<@spring.message 'referee.email'/>"></span>
    <div class="field">
      <input type="text" class="full" type="email" id="email_${encRefereeId}" name="ref_email" value="${(refereesAdminEditDTO.email?html)!}"/>
      <@spring.bind "refereesAdminEditDTO.email" />
      <#list spring.status.errorMessages as error>
      <div class="alert alert-error"> <i class="icon-warning-sign"></i>
        ${error}
      </div>
      </#list> </div>
  </div>
  
  <!-- Telephone -->
  <div class="row">
    <label class="plain-label" for="phoneNumber_${encRefereeId}">Telephone<em>*</em></label>
    <span class="hint" data-desc="<@spring.message 'referee.telephone'/>"></span>
    <div class="field">
      <input type="text" class="full" id="phoneNumber_${encRefereeId}" name="refPhoneNumber" placeholder="e.g. +44 (0) 123 123 1234" value="${(refereesAdminEditDTO.phoneNumber?html)!}"/>
      <@spring.bind "refereesAdminEditDTO.phoneNumber" />
      <#list spring.status.errorMessages as error>
      <div class="alert alert-error"> <i class="icon-warning-sign"></i>
        ${error}
      </div>
      </#list> </div>
  </div>
  
  <!-- Skype address -->
  <div class="row">
    <label class="plain-label" for="messenger_${encRefereeId}">Skype Name</label>
    <span class="hint" data-desc="<@spring.message 'referee.skype'/>"></span>
    </label>
    <div class="field">
      <input type="text" class="full" id="messenger_${encRefereeId}" name="ref_messenger" value="${(refereesAdminEditDTO.messenger?html)!}"/>
      <@spring.bind "refereesAdminEditDTO.messenger" />
      <#list spring.status.errorMessages as error>
      <div class="alert alert-error"> <i class="icon-warning-sign"></i>
        ${error}
      </div>
      </#list> </div>
  </div>
</div>

<script type="text/javascript" src="<@spring.url '/design/default/js/autosuggest.js' />"></script>
<script type="text/javascript">
    $(document).ready(function() {
        autosuggest($("#firstname_${encRefereeId}"), $("#lastname_${encRefereeId}"), $("#email_${encRefereeId}"));
    });
</script>

