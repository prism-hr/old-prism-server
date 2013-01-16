<#if applicationForm.referees?has_content> 
    <#assign hasReferees = true> 
<#else> 
    <#assign hasReferees = false> 
</#if> 
<#import "/spring.ftl" as spring /> 
<#setting locale = "en_US">

<h2 id="referee-H2" class="no-arrow empty">References</h2>

<div class="open">
<#if hasReferees>

    <table class="existing">
        <colgroup>
            <col style="width: 30px" />
            <col />
            <col style="width: 90px" />
            <col style="width: 30px" />
            <col style="width: 30px" />
        </colgroup>
        <thead>
            <tr>
                <th id="primary-header" colspan="2">References</th>
                <th>Responded</th>
                <th>&nbsp;</th>
                <th id="last-col">&nbsp;</th>
            </tr>
        </thead>
        <tbody>
            <#list applicationForm.referees as existingReferee>
            <#assign encRefereeId = encrypter.encrypt(existingReferee.id) />
            <tr>
                <td>
                    <input type="checkbox" name="refereeSendToUcl" value="${encRefereeId}"
                    <#if existingReferee.sendToUCL>checked="checked"</#if> 
                    <#if !existingReferee.hasResponded()>disabled="disabled"</#if> 
                    data-desc="<#if existingReferee.hasResponded()>Send reference to UCL Admissions<#else>Reference not provided</#if>"
                    />
                </td>
                <td>
                    ${(existingReferee.firstname?html)!} ${(existingReferee.lastname?html)!} (${(existingReferee.email?html)!})
                </td>
                <td>
                    <#if existingReferee.hasResponded()>
                        ${(existingReferee.reference.lastUpdated?string('dd MMM yyyy'))!}
                    <#else>
                        Not Provided
                    </#if>
                </td>
                <td>
                    <a name="showRefereeLink" id="showRefereeLink_${encRefereeId}" 
                    toggles="referee_${encRefereeId}" 
                    class="<#if !existingReferee.hasResponded()>button-edit<#else>button-show</#if> button-hint" 
                    data-desc="<#if !existingReferee.hasResponded()>Provide Reference<#else>Show</#if>">edit</a>
                </td>
                <td></td>
            </tr>
            </#list>
        </tbody>
    </table>

    <div class="section-info-bar">
        <b>You must select two completed references to submit for offer processing.</b>
    </div>
    
    <#list applicationForm.referees as referee>
    <#assign encRefereeId = encrypter.encrypt(referee.id) />
    <div id="referee_${encRefereeId}" style="display:none">
        <div class="row-group">
            <!-- Header -->
            <div class="admin_row">
                <label class="admin_header">Reference <#if referee.declined> - Declined</#if></label>
                <div class="field">&nbsp</div>
            </div>
        
            <!-- First name -->
            <div class="admin_row">
                <span class="admin_row_label">First Name</span>
                <div class="field" id="ref_firstname">${(referee.firstname?html)!"Not Provided"}</div>
            </div>
        
            <!-- Last name -->
            <div class="admin_row">
                <span class="admin_row_label">Last Name</span>
                <div class="field" id="ref_lastname">${(referee.lastname?html)!"Not Provided"}</div>
            </div>
        
            <!-- Employer / company name -->
            <div class="admin_row">
                <span class="admin_row_label">Employer</span>
                <div class="field" id="ref_employer">${(referee.jobEmployer?html)!"Not Provided"}</div>
            </div>
        
            <!-- Position title -->
            <div class="admin_row">
                <span class="admin_row_label">Position</span>
                <div class="field" id="ref_position">${(referee.jobTitle?html)!"Not Provided"}</div>
            </div>
        
            <!-- Address body -->
            <div class="admin_row">
                <span class="admin_row_label">Address</span>
                <div class="field" id="ref_address_location">${(referee.addressLocation.locationString?html)!"Not Provided"}</div>
            </div>
        
            <!-- Country -->
            <div class="admin_row">
                <span class="admin_row_label">Country</span>
                <div class="field" id="ref_address_country">${(referee.addressLocation.country.name?html)!"Not Provided"}</div>
            </div>
        
            <!-- Email address -->
            <div class="admin_row">
                <span class="admin_row_label">Email</span>
                <div class="field" id="ref_email">${(referee.email?html)!"Not Provided"}</div>
            </div>
        
            <!-- Telephone -->
            <div class="admin_row">
                <span class="admin_row_label">Telephone</span>
                <div class="field" id="ref_phone">${(referee.phoneNumber?html)!"Not Provided"}</div>
            </div>
        
            <!-- Skype address -->
            <div class="admin_row">
                <span class="admin_row_label">Skype</span>
                <div class="field" id="ref_messenger">${(referee.messenger?html)!"Not Provided"}</div>
            </div>
        </div>
        
        <#if referee.hasResponded()>
        <div class="row-group">
            <div class="admin_row">
                <label class="admin_header">Reference Comment</label>
                <div class="field">&nbsp</div>
            </div>
            
            <div class="admin_row">
                <span class="admin_row_label">Comment</span>
                <div class="field">${(referee.reference.comment?html)!"Not Provided"}</div>
            </div>
            
            <div class="admin_row">
                <span class="admin_row_label">Is the applicant suitable for postgraduate study at UCL?</span>
                <div class="field">
                    <#if referee.reference.suitableForUCL>Yes<#else>No</#if>
                </div>
            </div>
            
            <div class="admin_row">
                <span class="admin_row_label">Is the applicant suitable for their chosen postgraduate study programme?</span>
                <div class="field">
                    <#if referee.reference.suitableForProgramme>Yes<#else>No</#if>
                </div>
            </div>
            
            <div class="admin_row">
                <span class="admin_row_label">Attached Document</span>
                <div class="field">
                    <#if referee.reference.documents?has_content>
                        <a href="<@spring.url '/download?documentId=${encrypter.encrypt(referee.reference.documents[0].id)}'/>" class="button-hint" target="_blank">${referee.reference.documents[0].fileName?html}</a>
                    <#else>
                        Not Provided
                    </#if>
                </div>
            </div>
        </div>
        </#if>
    
        <#if !referee.hasResponded()>
        <div class="row-group">
            <h3>Contact Details</h3>
            <div class="row">
                <span class="plain-label">Email<em>*</em></span>
                <span class="hint" data-desc="<@spring.message 'referee.email'/>"></span>
                <div class="field">
                    <input type="text" class="full" name="refereeEmail" id="refereeEmail_${encRefereeId}" value="${(refereesAdminEditDTO.email?html)!}" />
                </div>
            </div>
            <@spring.bind "refereesAdminEditDTO.email" />
            <#list spring.status.errorMessages as error>
                <div class="row"><div class="field"><span class="invalid">${error}</span></div></div>
            </#list>
            
            <div class="row">
                <span class="plain-label">Telephone<em>*</em></span>
                <span class="hint" data-desc="<@spring.message 'referee.telephone'/>"></span>
                <div class="field">
                    <input type="text" class="full" name="refereeTelephone" id="refereeTelephone_${encRefereeId}" placeholder="e.g. +44 (0) 123 123 1234" value="${(refereesAdminEditDTO.telephone?html)!}" />
                </div>
            </div>
            <@spring.bind "refereesAdminEditDTO.telephone" />
            <#list spring.status.errorMessages as error>
                <div class="row"><div class="field"><span class="invalid">${error}</span></div></div>
            </#list>
            
            <div class="row">
                <span class="plain-label">Skype</span>
                <span class="hint" data-desc="<@spring.message 'referee.skype'/>"></span>
                <div class="field">
                    <input type="text" class="full" name="refereeSkype" id="refereeSkype_${encRefereeId}" value="${(refereesAdminEditDTO.skype?html)!}" />
                </div>
            </div>
            <@spring.bind "refereesAdminEditDTO.skype" />
            <#list spring.status.errorMessages as error>
                <div class="row"><div class="field"><span class="invalid">${error}</span></div></div>
            </#list>
        </div>
        
        <div class="row-group">
            <div class="row">
                <span class="plain-label">Comment<em>*</em></span>
                <span class="hint" data-desc="<@spring.message 'interviewOutcome.comment'/>"></span>
                <div class="field">
                    <textarea name="refereeComment" id="refereeComment_${encRefereeId}" class="max" rows="6" cols="80">${(refereesAdminEditDTO.comment?html)!}</textarea>
                </div>
            </div>
            <@spring.bind "refereesAdminEditDTO.comment" />
            <#list spring.status.errorMessages as error>
                <div class="row"><div class="field"><span class="invalid">${error}</span></div></div>
            </#list>
        </div>
        
        <div class="row-group">
            <div class="row">
                <span class="plain-label">Attach Document<em>*</em></span>
                <span class="hint" data-desc="<@spring.message 'validateApp.document'/>"></span>
                <div class="field">
                    <input type="file" value="" name="file" data-reference="" data-type="PERSONAL_STATEMENT" class="full" id="psDocument">
                </div>
            </div>
        </div>
        
        <div class="row-group">
            <h3>Applicant Suitability</h3>
            <div class="row">
                <span class="plain-label">Is the applicant suitable for postgraduate study at UCL?</span>
                <span class="hint" data-desc="<@spring.message 'interviewOutcome.suitsPG'/>"></span>
                <div class="field">
                    <label><input type="radio" id="suitableUCL_true" value="TRUE" name="suitableUCL_${encRefereeId}" 
                    <#if refereesAdminEditDTO.isSuitableForUCLSet() && refereesAdminEditDTO.getSuitableForUCL()> checked="checked"</#if> > Yes</label>
                    <label><input type="radio" id="suitableUCL_false" value="FALSE" name="suitableUCL_${encRefereeId}" 
                    <#if refereesAdminEditDTO.isSuitableForUCLSet() && !refereesAdminEditDTO.getSuitableForUCL()> checked="checked"</#if> > No</label>
                </div>
            </div>
            <div class="row">
                <span class="plain-label">Is the applicant suitable for their chosen postgraduate study programme?</span>
                <span class="hint" data-desc="<@spring.message 'interviewOutcome.suitsPGP'/>"></span>
                <div class="field">
                    <label><input type="radio" id="suitableProgramme_true" value="TRUE" name="suitableProgrammeRadio_${encRefereeId}" 
                    <#if refereesAdminEditDTO.isSuitableForProgrammeSet() && refereesAdminEditDTO.getSuitableForProgramme()> checked="checked"</#if> > Yes</label>
                    <label><input type="radio" id="suitableProgramme_false" value="FALSE" name="suitableProgrammeRadio_${encRefereeId}"
                    <#if refereesAdminEditDTO.isSuitableForProgrammeSet() && !refereesAdminEditDTO.getSuitableForProgramme()> checked="checked"</#if> > No</label>
                </div>
            </div>
        </div>
        </#if>
    </div>
    </#list>
    <input type="hidden" name="editedRefereeId" id="editedRefereeId" value="${(editedRefereeId)!}" />
    <div class="buttons">
        <button name="refereeClearButton" type="button" id="refereeClearButton" class="clear">Clear</button>
        <button type="button" id="refereeCloseButton" class="blue">Close</button>
        <button type="button" id="refereeSaveButton" class="blue">Save</button>
    </div>
</div>
</#if>

