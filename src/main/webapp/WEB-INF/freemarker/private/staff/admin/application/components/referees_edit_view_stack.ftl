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
        
        <#if referee.hasResponded() && !referee.isDeclined()>
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
    
        <#if !referee.hasResponded() && !referee.isDeclined()>
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
                <span class="plain-label">Attach Document</span>
                <span class="hint" data-desc="<@spring.message 'validateApp.document'/>"></span>
                <div class="field <#if refereesAdminEditDTO.referenceDocument??>uploaded</#if>" id="psUploadFields">
                    <input id="referenceDocument_${encRefereeId}" type="file" value="" name="file" data-reference="" data-type="COMMENT" class="full">
                    <span id="psUploadedDocument">
                        <input type="hidden" class="file" id="document_COMMENT" value="${(encrypter.encrypt(refereesAdminEditDTO.referenceDocument.id))!}"/>
                        <#if refereesAdminEditDTO.referenceDocument??> 
                            <a id="lqLink" class="uploaded-filename" href="<@spring.url '/download?documentId=${(encrypter.encrypt(personalDetails.referenceDocument.id))!}'/>" target="_blank">
                            ${(refereesAdminEditDTO.referenceDocument.fileName?html)!}</a>
                            <a id="deleteLq" class="button-delete button-hint" data-desc="Change reference document">delete</a> 
                        </#if>
                    </span>
                    <span id="referenceDocumentProgress" class="progress" style="display: none;"></span>
                </div>
            </div>
                
            <@spring.bind "refereesAdminEditDTO.referenceDocument" /> 
            <#list spring.status.errorMessages as error>
                <div class="row"><div class="field"><span class="invalid">${error}</span></div></div>
            </#list>

        </div>
        
        <div class="row-group">
            <h3>Applicant Suitability</h3>
        
            <div class="row">
                <span id="suitable-lbl" class="plain-label">Is the applicant suitable for postgraduate study at UCL?<em>*</em></span>
                <span class="hint" data-desc="<@spring.message 'interviewOutcome.suitsPG'/>"></span>
                <div class="field" id="field-issuitableucl">
                    <label><input type="radio" name="suitableForUCL_${encRefereeId}" value="true" id="suitableRB_true"
                    <#if refereesAdminEditDTO.isSuitableForUCLSet() && refereesAdminEditDTO.suitableForUCL> checked="checked"</#if>
                    /> Yes</label> 
                    <label><input type="radio" name="suitableForUCL_${encRefereeId}" value="false" id="suitableRB_false"
                    <#if refereesAdminEditDTO.isSuitableForUCLSet() && !refereesAdminEditDTO.suitableForUCL> checked="checked"</#if>
                    /> No</label>
                    <@spring.bind "refereesAdminEditDTO.suitableForUCL" /> 
                        <#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>
                    </div>
            </div>
            <div class="row multi-line" id="field-issuitableprog">
                <span id="supervise-lbl" class="plain-label">Is the applicant suitable for their chosen postgraduate study programme?<em>*</em></span>
                <span class="hint" data-desc="<@spring.message 'interviewOutcome.suitsPGP'/>"></span>
                <div class="field">
                    <label><input type="radio" name="suitableForProgramme_${encRefereeId}" value="true" id="willingRB_true"
                    <#if refereesAdminEditDTO.isSuitableForProgrammeSet() && refereesAdminEditDTO.suitableForProgramme> checked="checked"</#if> 
                    /> Yes</label> 
                    <label><input type="radio" name="suitableForProgramme_${encRefereeId}" value="false" id="willingRB_false"
                    <#if refereesAdminEditDTO.isSuitableForProgrammeSet() && !refereesAdminEditDTO.suitableForProgramme> checked="checked"</#if>
                    /> No</label> 
                    <@spring.bind "refereesAdminEditDTO.suitableForProgramme" /> 
                    <#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>
                </div>
            </div>
            
            <@spring.bind "refereesAdminEditDTO.*" />
             
            <#assign anyReferenceErrors = spring.status.errorMessages?size &gt; 0>
            <input type="hidden" name="anyReferenceErrors" id="anyReferenceErrors" value="${anyReferenceErrors?string}" />
            
            <!-- Add reference add button -->
            <div class="row">
                <div class="field">
                    <button id="addReferenceButton" type="button" class="blue">Add</button>
                </div>
            </div>
            
        </div>        


        </#if>
    </div>
</#list>