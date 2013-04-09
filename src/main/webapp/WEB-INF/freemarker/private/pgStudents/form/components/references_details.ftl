<#import "/spring.ftl" as spring />
<#setting locale = "en_US">
<#assign errorCode = RequestParameters.errorCode! />
<#if applicationForm.referees?has_content>
  <#assign hasReferees = true>
<#else>
  <#assign hasReferees = false>
</#if> <a name="references-details"></a>
<h2 id="referee-H2" class="empty open"> <span class="left"></span><span class="right"></span><span class="status"></span> Referees<em>*</em> </h2>
<div class="open"> <#if hasReferees>
  <table class="existing table table-striped table-condensed table-bordered table-hover">
    <colgroup>
    <col/>
    <col style="width: 30px" />
    <col style="width: 30px" />
    </colgroup>
    <thead>
      <tr>
        <th>Referees</th>
        <th colspan="2">&nbsp;</th>
      </tr>
    </thead>
    <tbody>
    <#list applicationForm.referees as existingReferee>
    <tr>
      <td>${(existingReferee.firstname?html)!} ${(existingReferee.lastname?html)!} (${(existingReferee.email?html)!})</td>
      <#assign encExistingRefereeId = encrypter.encrypt(existingReferee.id) />
      <#if existingReferee.editable> 
       <td><a name="editRefereeLink" data-desc="Edit" id="referee_${encExistingRefereeId}" class="button-edit button-hint">edit</a> 
      </td>
      <#elseif existingReferee.declined || existingReferee.hasProvidedReference()> 
       <td>
      <a name="editRefereeLink" id="referee_${encExistingRefereeId}" class="button-responded" data-desc="Responded">Responded</a> 
      </td>
      <#else> 
      <td><a name="editRefereeLink" data-desc="Show" id="referee_${encExistingRefereeId}" class="button-show button-hint">show</a></td>
      
      </#if>
      <#if applicationForm.isInState('UNSUBMITTED')> 
      <td><a name="deleteRefereeButton" data-desc="Delete" id="referee_${encExistingRefereeId}" class="button-delete button-hint">delete</a> 
      </td>
      <#else>
       <td>
       <a name="" id="" class="button-delete grey button-hint">delete</a> 
       </td>
      </#if> 
    </tr>
    </#list>
      </tbody>
    
  </table>
  </#if>
  <form id="refereeForm">
    <input type="hidden" id="refereeId" name="refereeId" value="<#if referee.id??>${encrypter.encrypt(referee.id)}</#if>" />
    <#if errorCode?? && errorCode=="true">
    <div class="alert alert-error"> <i class="icon-warning-sign" data-desc="Please provide all mandatory fields in this section."></i> 
      <@spring.message 'referencesDetails.sectionInfo'/>
    </div>
    <#else>
    <div class="alert alert-info"> <i class="icon-info-sign"></i>
      <@spring.message 'referencesDetails.sectionInfo'/>
    </div>
    </#if>
    <div class="row-group"> 
      <!-- First name -->
      <div class="row">
        <label for="ref_firstname" class=
        <#if referee.editable && (applicationForm.referees?size &lt; 4 || referee.id??) > "plain-label"
          <#else>
          "plain-label grey-label"
          </#if>
          > First Name<em>*</em></label>
        <span class="hint" data-desc="<@spring.message 'referee.firstname'/>"></span>
        <div class="field"> <#if referee.editable && (applicationForm.referees?size &lt; 4 || referee.id??) >
          <input type="text" class="full" id="ref_firstname" name="ref_firstname" value="${(referee.firstname?html)!}"/>
          <#else>
          <input type="text" readonly class="full" id="ref_firstname" name="ref_firstname" value="${(referee.firstname?html)!}" disabled="disabled"/>
          </#if>
          <@spring.bind "referee.firstname" />
          <#list spring.status.errorMessages as error>
          <div class="alert alert-error"> <i class="icon-warning-sign"></i>
            ${error}
          </div>
          </#list> </div>
      </div>
      
      <!-- Last name -->
      <div class="row">
        <label for="ref_lastname" class=
        <#if referee.editable && (applicationForm.referees?size &lt; 4 || referee.id??) > "plain-label"
          <#else>
          "plain-label grey-label"
          </#if>
          >Last Name<em>*</em></label>
        <span class="hint" data-desc="<@spring.message 'referee.lastname'/>"></span>
        <div class="field"> <#if referee.editable && (applicationForm.referees?size &lt; 4 || referee.id??) >
          <input type="text" class="full" id="ref_lastname" name="ref_lastname" value="${(referee.lastname?html)!}"/>
          <#else>
          <input type="text" readonly class="full" id="ref_lastname" name="ref_lastname" value="${(referee.lastname?html)!}" disabled="disabled"/>
          </#if>
          <@spring.bind "referee.lastname" />
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
      <label for="ref_employer" class=
        <#if referee.editable && (applicationForm.referees?size &lt; 4 || referee.id??) > "plain-label"
        <#else>
        "plain-label grey-label"
        </#if>
        >Employer<em>*</em></label>
      <span class="hint" data-desc="<@spring.message 'referee.employer'/>"></span>
      <div class="field"> <#if referee.editable && (applicationForm.referees?size &lt; 4 || referee.id??) >
        <input type="text" class="full" id="ref_employer" name="ref_employer" value="${(referee.jobEmployer?html)!}"/>
        <#else>
        <input type="text" readonly class="full" id="ref_employer" name="ref_employer" value="${(referee.jobEmployer?html)!}" disabled="disabled"/>
        </#if>
        <@spring.bind "referee.jobEmployer" />
        <#list spring.status.errorMessages as error>
        <div class="alert alert-error"> <i class="icon-warning-sign"></i>
          ${error}
        </div>
        </#list> </div>
    </div>
    
    <!-- Position title -->
    <div class="row">
      <label for="ref_position" class=
        <#if referee.editable && (applicationForm.referees?size &lt; 4 || referee.id??) > "plain-label"
        <#else>
        "plain-label grey-label"
        </#if>
        >Position<em>*</em></label>
      <span class="hint" data-desc="<@spring.message 'referee.position'/>"></span>
      <div class="field"> 
        <#if referee.editable && (applicationForm.referees?size &lt; 4 || referee.id??) >
        <input type="text" class="full" id="ref_position" name="ref_position" value="${(referee.jobTitle?html)!}"/>
        <#else>
          <input type="text" readonly class="full" id="ref_position" name="ref_position" value="${(referee.jobTitle?html)!}" disabled="disabled"/>
          </#if>
          <@spring.bind "referee.jobTitle" />
          <#list spring.status.errorMessages as error>
          <div class="alert alert-error"> <i class="icon-warning-sign"></i>
            ${error}
          </div>
          </#list> </div>
      </div>
    </div>
    <div class="row-group"> 
      
      <!-- Address body -->
      
      <h3 class=
        <#if referee.editable && (applicationForm.referees?size &lt; 4 || referee.id??) > "group-heading-label"
        <#else>
        "group-heading-label grey-label"
        </#if>
        >Address</h3>
      <div class="row">
        <label for="ref_address_location1" class=
        <#if referee.editable && (applicationForm.referees?size &lt; 4 || referee.id??) > "plain-label"
          <#else>
          "plain-label grey-label"
          </#if>
          >House name / number & street<em>*</em></label>
        <span class="hint" data-desc="<@spring.message 'addressDetails.house'/>"></span>
        <div class="field"> <input type="text" class="max" id="ref_address_location1" name="ref_address_location1" value="${(referee.addressLocation.address1?html)!}"
          <#if !referee.editable || !(applicationForm.referees?size &lt; 4 || referee.id??) >
          readonly="readonly" disabled="disabled"
          </#if>
          /> <input type="text" class="max" id="ref_address_location2" name="ref_address_location2" value="${(referee.addressLocation.address2?html)!}"
          <#if !referee.editable || !(applicationForm.referees?size &lt; 4 || referee.id??) >
          readonly="readonly" disabled="disabled"
          </#if>
          />
          <@spring.bind "referee.addressLocation.address1" />
          <#list spring.status.errorMessages as error>
          <div class="alert alert-error"> <i class="icon-warning-sign"></i>
            ${error}
          </div>
          </#list>
          <@spring.bind "referee.addressLocation.address2" />
          <#list spring.status.errorMessages as error>
          <div class="alert alert-error"> <i class="icon-warning-sign"></i>
            ${error}
          </div>
          </#list> </div>
      </div>
      <div class="row">
        <label for="ref_address_location3" class=
        <#if referee.editable && (applicationForm.referees?size &lt; 4 || referee.id??) > "plain-label"
          <#else>
          "plain-label grey-label"
          </#if>
          >Town / city / suburb<em>*</em></label>
        <span class="hint" data-desc="<@spring.message 'addressDetails.town'/>"></span>
        <div class="field"> <input type="text" class="full" id="ref_address_location3" name="ref_address_location3" value="${(referee.addressLocation.address3?html)!}"
          <#if !referee.editable || !(applicationForm.referees?size &lt; 4 || referee.id??) >
          readonly="readonly" disabled="disabled"
          </#if>
          />
          <@spring.bind "referee.addressLocation.address3" />
          <#list spring.status.errorMessages as error>
          <div class="alert alert-error"> <i class="icon-warning-sign"></i>
            ${error}
          </div>
          </#list> </div>
      </div>
      <div class="row">
        <label for="ref_address_location4" class=
        <#if referee.editable && (applicationForm.referees?size &lt; 4 || referee.id??) > "plain-label"
          <#else>
          "plain-label grey-label"
          </#if>
          >State / county / region</label>
        <span class="hint" data-desc="<@spring.message 'addressDetails.state'/>"></span>
        <div class="field"> <input type="text" class="full" id="ref_address_location4" name="ref_address_location4" value="${(referee.addressLocation.address4?html)!}"
          <#if !referee.editable || !(applicationForm.referees?size &lt; 4 || referee.id??) >
          readonly="readonly" disabled="disabled"
          </#if>
          />
          <@spring.bind "referee.addressLocation.address4" />
          <#list spring.status.errorMessages as error>
          <div class="alert alert-error"> <i class="icon-warning-sign"></i>
            ${error}
          </div>
          </#list> </div>
      </div>
      <div class="row">
        <label for="ref_address_location5" class=
        <#if referee.editable && (applicationForm.referees?size &lt; 4 || referee.id??) > "plain-label"
          <#else>
          "plain-label grey-label"
          </#if>
          >Post / zip / area code</label>
        <span class="hint" data-desc="<@spring.message 'addressDetails.zip'/>"></span>
        <div class="field"> <input type="text" class="full" id="ref_address_location5" name="ref_address_location5" value="${(referee.addressLocation.address5?html)!}"
          <#if !referee.editable || !(applicationForm.referees?size &lt; 4 || referee.id??) >
          readonly="readonly" disabled="disabled"
          </#if>
          />
          <@spring.bind "referee.addressLocation.address5" />
          <#list spring.status.errorMessages as error>
          <div class="alert alert-error"> <i class="icon-warning-sign"></i>
            ${error}
          </div>
          </#list> </div>
      </div>
      
      <!-- Country -->
      <div class="row">
        <label for="ref_address_country" class=
        <#if referee.editable && (applicationForm.referees?size &lt; 4 || referee.id??) > "plain-label"
          <#else>
          "plain-label grey-label"
          </#if>
          >Country<em>*</em></label>
        <span class="hint" data-desc="<@spring.message 'referee.country'/>"></span>
        <div class="field"> <#if referee.editable && (applicationForm.referees?size &lt; 4 || referee.id??) >
          <select class="full" name="ref_address_country" id="ref_address_country">
            <option value="">Select...</option>
            <#list countries as country> <option value="${encrypter.encrypt(country.id)}" 
            <#if referee.addressLocation.country?? && referee.addressLocation.country.id == country.id> 
              selected="selected"</#if>>
            ${country.name?html}
            </option>
            </#list>
          </select>
          <#else>
          <select class="full" name="ref_address_country" id="ref_address_country" disabled="disabled">
          </select>
          </#if>
          <@spring.bind "referee.addressLocation.country" />
          <#list spring.status.errorMessages as error>
          <div class="alert alert-error"> <i class="icon-warning-sign"></i>
            ${error}
          </div>
          </#list> </div>
      </div>
    </div>
    <div class="row-group"> 

      <h3 class=
        <#if referee.editable && (applicationForm.referees?size &lt; 4 || referee.id??) > "group-heading-label"
        <#else>
        "group-heading-label grey-label"
        </#if>
        >Contact Details</h3>
        
      <!-- Email address -->
      <div class="row">
        <label for="ref_email" class=
        <#if referee.editable && (applicationForm.referees?size &lt; 4 || referee.id??) > "plain-label"
          <#else>
          "plain-label grey-label"
          </#if>
          >Email<em>*</em></label>
        <span class="hint" data-desc="<@spring.message 'referee.email'/>"></span>
        <div class="field"> <#if referee.editable && (applicationForm.referees?size &lt; 4 || referee.id??) >
          <input class="full" type="email" id="ref_email" name="ref_email" value="${(referee.email?html)!}"/>
          <#else>
          <input readonly class="full" type="email" id="ref_email" name="ref_email" value="${(referee.email?html)!}" disabled="disabled"/>
          </#if>
          <@spring.bind "referee.email" />
          <#list spring.status.errorMessages as error>
          <div class="alert alert-error"> <i class="icon-warning-sign"></i>
            ${error}
          </div>
          </#list> </div>
      </div>
      
      <!-- Telephone -->
      <div class="row"> 
       <label for="refPhoneNumber" class=
        <#if referee.editable && (applicationForm.referees?size &lt; 4 || referee.id??) > "plain-label"
          <#else>
          "plain-label grey-label"
          </#if>
          >Telephone<em>*</em></label>
          <span class="hint" data-desc="<@spring.message 'referee.telephone'/>"></span>
          <div class="field">
          <#if referee.editable && (applicationForm.referees?size &lt; 4 || referee.id??) >       
          <input type="text" class="full" id="refPhoneNumber" name="refPhoneNumber" placeholder="e.g. +44 (0) 123 123 1234" value="${(referee.phoneNumber?html)!}"/>
          <#else> 
          <input type="text" readonly class="full" id="refPhoneNumber" name="refPhoneNumber" value="${(referee.phoneNumber?html)!}" disabled="disabled"/>
        </#if> 
            <@spring.bind "referee.phoneNumber" />
          <#list spring.status.errorMessages as error>
          <div class="alert alert-error"> <i class="icon-warning-sign"></i>
            ${error}
          </div>
          </#list> 
        </div>
       </div>
      
      
      <!-- Skype address -->
      <div class="row"> 
      
      <label for="ref_messenger" class=
        <#if referee.editable && (applicationForm.referees?size &lt; 4 || referee.id??) > "plain-label"
          <#else>
          "plain-label grey-label"
          </#if>
          >Skype Name</label>
        <span class="hint" data-desc="<@spring.message 'referee.skype'/>"></span>
        <div class="field">
        <#if referee.editable && (applicationForm.referees?size &lt; 4 || referee.id??) > 
          <input type="text" class="full" id="ref_messenger" name="ref_messenger" value="${(referee.messenger?html)!}"/>
        <#else> 
          <input type="text" readonly class="full" id="ref_messenger" name="ref_messenger" value="${(referee.messenger?html)!}" disabled="disabled"/>
        </#if>
         <@spring.bind "referee.messenger" />
          <#list spring.status.errorMessages as error>
          <div class="alert alert-error"> <i class="icon-warning-sign"></i>
            ${error}
          </div>
          </#list> 
        </div>
      </div>
     
      
      <#if referee.editable > 
      <!-- Add another button -->
      <div class="row">
        <div class="field"> <#if referee.id?? || applicationForm.referees?size &lt; 3>
          <button type="button" id="addReferenceButton" class="btn"><#if referee.id??>Update<#else>Add</#if></button>
          <#else>
          <button type="button" class="btn" style="cursor:default" disabled>Add</button>
          </#if> </div>
      </div>
      </#if> </div>
    <#if applicationForm.isModifiable() && !applicationForm.isInState('UNSUBMITTED')>
    <@spring.bind "referee.acceptedTerms" />
    <#if spring.status.errorMessages?size &gt; 0>
    <div class="alert alert-error tac" >
      <#else>
        <div class="alert tac" >
      </#if>
      <div class="row"> 
      <label for="acceptTermsRDCB" class="terms-label"> Confirm that the information that you have provided in this section is true 
        and correct. Failure to provide true and correct information may result in a 
        subsequent offer of study being withdrawn. </label>
		<div class="terms-field">
          <input type="checkbox" name="acceptTermsRDCB" id="acceptTermsRDCB" />
        <input type="hidden" name="acceptTermsRDValue" id="acceptTermsRDValue"/>
        </div>
      </div>
    </div>
    </#if>
    <div class="buttons">
    <#if applicationForm.modifiable>
    <button class="btn" type="button" id="refereeClearButton" name="refereeClearButton">Clear</button>
    <button class="btn" type="button" id="refereeCloseButton" name="refereeCloseButton">Close</button>
    <button class="btn btn-primary" type="button" id="refereeSaveAndCloseButton" value="close">Save</button>
    <#else>
    <button type="button" id="refereeCloseButton">
    Close</a> </#if>
    </div>
  </form>
</div>
<script type="text/javascript" src="<@spring.url '/design/default/js/application/referee.js'/>"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/autosuggest.js'/>"></script>
<script type="text/javascript">
    $(document).ready(function() {
        autosuggest($("#ref_firstname"), $("#ref_lastname"), $("#ref_email"));
    });
</script>
 