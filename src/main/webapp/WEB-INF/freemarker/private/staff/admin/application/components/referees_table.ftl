<table class="existing  table table-striped table-condensed table-bordered table-hover">
    <colgroup>
        <col style="width: 30px" />
        <col style="width: 650px" />
        <col style="width: 90px" />
        <col style="width: 36px" />
        <col />
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
        <tr>
            <td colspan="5" class="scrollparent">
                <div class="scroll">
                    <table>
                        <colgroup>
                            <col style="width: 30px" />
                            <col style="width: 650px" />
                            <col style="width: 90px" />
                            <col style="width: 33px" />
                            <col />
                        </colgroup>
                        <tbody>
                            <#list applicationForm.referees as existingReferee>
                            <#assign encRefereeId = encrypter.encrypt(existingReferee.id) />
                            <tr>
                                <td>
                                    <input type="checkbox" name="refereeSendToUcl" value="${encRefereeId}"
                                    <#if existingReferee.sendToUCL?? && existingReferee.sendToUCL>checked="checked"</#if> 
                                    <#if !existingReferee.hasResponded()>disabled="disabled"</#if> 
                                    data-desc="<#if existingReferee.hasResponded()>Send reference for offer processing<#else>Reference not provided</#if>"
                                    />
                                </td>
                                <td>
                                    ${(existingReferee.firstname?html)!} ${(existingReferee.lastname?html)!} (${(existingReferee.email?html)!})
                                </td>
                                <td>
                                    <#if existingReferee.hasProvidedReference()>
                                        ${(existingReferee.reference.lastUpdated?string('dd MMM yyyy'))!}
                                    <#elseif existingReferee.isDeclined()>
                                        Declined
                                    <#else>
                                        Not Provided
                                    </#if>
                                </td>
                                <td>
                                    <a name="showRefereeLink" id="showRefereeLink_${encRefereeId}" 
                                    toggles="referee_${encRefereeId}" 
                                    <#if existingReferee.hasResponded()>responded="responded"</#if>
                                    class="<#if !existingReferee.hasResponded()>button-edit<#else>button-show</#if> button-hint"
                                    data-desc="<#if !existingReferee.hasResponded()>Provide reference<#else>Show</#if>">edit</a>
                                </td>
                                <td></td>
                            </tr>
                            </#list>
                        </tbody>
                    </table>
                </div>
            </td>
        </tr>
    </tbody>
</table>