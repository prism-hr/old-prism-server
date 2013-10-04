        <table class="existing table table-striped table-condensed table-bordered table-hover">
            <colgroup>
                <col style="width: 30px" />
                <col />
                <col style="width: 90px" />
                <col style="width: 30px" />
            </colgroup>
            <thead>
                <tr>
                	<th>&nbsp;</th>
                    <th>Qualification</th>
                    <th>Date</th>
                    <th>&nbsp;</th>
                </tr>
            </thead>
            <tbody>
                <#assign anyQualificationEnabled = false>
                <#list applicationForm.qualifications as existingQualification>
                <#assign encQualificationId = encrypter.encrypt(existingQualification.id) />
                <tr>
                    <td>
                        <input type="checkbox" name="qualificationSendToUcl" value="${encQualificationId}"  
                        <#if existingQualification.sendToUCL?? && existingQualification.sendToUCL>checked="checked"</#if> 
                        <#if !(existingQualification.proofOfAward?? && existingQualification.proofOfAward.id??)>
                            disabled="disabled"
                        <#else>
                            <#assign anyQualificationEnabled = true>
                        </#if>
                        data-desc="<#if existingQualification.proofOfAward?? && existingQualification.proofOfAward.id??>Send transcript for offer processing<#else>Transcript not provided</#if>" 
                        />
                    </td>
        
                    <td>
                        <#if existingQualification.proofOfAward?? && existingQualification.proofOfAward.id??>
                            <#assign encProofOfAwardId = encrypter.encrypt(existingQualification.proofOfAward.id) /> 
                            <a href="<@spring.url '/download?documentId=${encProofOfAwardId}'/>" data-desc="Proof Of Award" class="button-hint" target="_blank"> 
                                <#if existingQualification.otherQualificationInstitution?has_content> 
                                    ${(existingQualification.otherQualificationInstitution?html)!}
                                <#else>
                                    ${(existingQualification.qualificationInstitution?html)!} 
                                </#if> 
                            ${(existingQualification.qualificationTitle?html)!} ${(existingQualification.qualificationSubject?html)!} (${(existingQualification.qualificationGrade?html)!}) </a>  
                        <#else> 
                            <#if existingQualification.otherQualificationInstitution?has_content> 
                                ${(existingQualification.otherQualificationInstitution?html)!} 
                            <#else> 
                                ${(existingQualification.qualificationInstitution?html)!} 
                            </#if> 
                            ${(existingQualification.qualificationTitle?html)!} ${(existingQualification.qualificationSubject?html)!} (${(existingQualification.qualificationGrade?html)!})  
                        </#if>
                    </td>
        
                    <td>
                        <#if existingQualification.isQualificationCompleted()> 
                            ${(existingQualification.qualificationAwardDate?string('dd MMM yyyy'))!} 
                        <#else> 
                            Expected 
                        </#if>
                    </td> 
                    <td><a name="showQualificationLink" id="showQualificationLink_${encQualificationId}" toggles="qualification_${encQualificationId}" class="button-show button-hint" data-desc="Show">edit</a></td>
                </tr>
                </#list>
            </tbody>
        </table>