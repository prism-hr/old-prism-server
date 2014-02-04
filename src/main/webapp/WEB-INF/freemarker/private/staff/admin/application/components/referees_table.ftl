<table class="existing  table table-condensed table-bordered">
    <colgroup>
        <col style="width: 30px" />
        <col />
        <col style="width: 155px" />
        <col style="width: 33px" />
        <col />
    </colgroup>
    <tbody>
        <tr>
            <td colspan="5" class="scrollparent">
                <div class="scroll">
                    <table class="table-striped table-hover">
                        <colgroup>
                            <col style="width: 30px" />
                            <col />
                            <col style="width: 155px" />
                            <col style="width: 33px" />
                            <col />
                        </colgroup>
                        <tbody>
                            <#list applicationForm.referees as existingReferee>
                            <#assign encRefereeId = encrypter.encrypt(existingReferee.id) />
                            <tr>
                                <td>
                                    <input id="refereeSendToUcl_${encRefereeId}" type="checkbox" name="refereeSendToUcl" value="${encRefereeId}"
                                    <#if existingReferee.sendToUCL?? && existingReferee.sendToUCL && existingReferee.hasProvidedReference()>checked="checked"</#if> 
                                    <#if !existingReferee.hasProvidedReference()>disabled="disabled"</#if> 
                                    data-desc="<#if existingReferee.hasProvidedReference()>Send reference for offer processing<#else>Reference not provided</#if>"
                                    />
                                </td>
                                <td>
                                    ${(existingReferee.firstname?html)!} ${(existingReferee.lastname?html)!} (${(existingReferee.email?html)!})
                                </td>
                                <td>
                                    <#if existingReferee.hasProvidedReference()>
                                        Responded: <strong>${(existingReferee.reference.lastUpdated?string('dd MMM yyyy'))!}</strong>
                                    <#elseif existingReferee.isDeclined()>
                                        <strong>Declined</strong>
                                    <#else>
                                        <strong>Not Provided</strong>
                                    </#if>
                                </td>
                                <td>
                                    <a name="showRefereeLink" id="showRefereeLink_${encRefereeId}" 
                                    toggles="referee_${encRefereeId}" responded="responded" class="button-edit button-hint"
                                    data-desc="Provide or edit the reference">edit</a>
                                </td>
                            </tr>
                            </#list>
                        </tbody>
                    </table>
                </div>
            </td>
        </tr>
    </tbody>
</table>