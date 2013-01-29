<#import "/spring.ftl" as spring />
<section id="accountDetailsSection" class="form-rows">
    <h2 class="no-arrow">Account Details</h2>
    <div>
        <form autocomplete="off">

            <#if RequestParameters.messageCode??>
            <div class="section-info-bar"><@spring.message '${RequestParameters.messageCode}'/></div>
            <#else>
            <div class="section-info-bar">Edit your account details.</div>
            </#if>

            <div class="row-group">
                <div class="row">
                    <span id="email-lbl" class="plain-label">First Name</span> <span class="hint" data-desc="<@spring.message 'myaccount.firstName'/>"></span>
                    <div class="field">
                        <input class="full" type="text" id="firstName" value="${updatedUser.firstName}" />
                    </div>
                    <@spring.bind "updatedUser.firstName" /> <#list spring.status.errorMessages as error>
                    <div class="field">
                        <span class="invalid">${error}</span>
                    </div>
                    </#list>
                </div>

                <div class="row">
                    <span id="email-lbl" class="plain-label">Last Name</span> <span class="hint" data-desc="<@spring.message 'myaccount.lastName'/>"></span>
                    <div class="field">
                        <input class="full" type="text" id="lastName" value="${updatedUser.lastName}" />
                    </div>
                    <@spring.bind "updatedUser.lastName" /> <#list spring.status.errorMessages as error>
                    <div class="field">
                        <span class="invalid">${error}</span>
                    </div>
                    </#list>
                </div>

                <div class="row">
                    <span id="email-lbl" class="plain-label">Email</span> <span class="hint" data-desc="<@spring.message 'myaccount.email'/>"></span>
                    <div class="field">
                        <input class="full" type="text" id="email" value="${updatedUser.email}" />
                    </div>
                    <@spring.bind "updatedUser.email" /> <#list spring.status.errorMessages as error>
                    <div class="field">
                        <span class="invalid">${error}</span>
                    </div>
                    </#list>
                </div>
            </div>

            <div class="row-group">
                <h3>Change Password</h3>
                <div class="row">
                    <span class="plain-label">Current Password</span> <span class="hint" data-desc="<@spring.message 'myaccount.currentPw'/>"></span>
                    <div class="field">
                        <input class="full" id="currentPassword" type="password" />
                    </div>
                    <@spring.bind "updatedUser.password" /> <#list spring.status.errorMessages as error>
                    <div class="field">
                        <span class="invalid">${error}</span>
                    </div>
                    </#list>
                </div>

                <div class="row">
                    <span class="plain-label">New Password</span> <span class="hint" data-desc="<@spring.message 'myaccount.newPw'/>"></span>
                    <div class="field">
                        <input class="full" id="newPassword" type="password" />
                    </div>
                    <@spring.bind "updatedUser.newPassword" /> <#list spring.status.errorMessages as error>
                    <div class="field">
                        <span class="invalid">${error}</span>
                    </div>
                    </#list>
                </div>

                <div class="row">
                    <span class="plain-label">Re-enter new Password</span> <span class="hint" data-desc="<@spring.message 'myaccount.confirmPw'/>"></span>
                    <div class="field">
                        <input class="full" id="confirmNewPass" type="password" />
                    </div>
                    <@spring.bind "updatedUser.confirmPassword" /> <#list spring.status.errorMessages as error>
                    <div class="field">
                        <span class="invalid">${error}</span>
                    </div>
                    </#list>
                </div>
            </div>
            <!-- .row-group -->
            <div class="buttons">
                <button class="blue" id="saveChanges" type="button">Submit</button>
            </div>
        </form>
    </div>
</section>

<section id="linkAcountDetailsSection" class="form-rows">
    <h2 class="no-arrow">Linked Accounts</h2>
    <div id="linkAccountsSection">             
            <#if RequestParameters.messageCodeLink??>
                <div class="section-info-bar"><@spring.message '${RequestParameters.messageCodeLink}'/></div>
            <#else>
                <div class="section-info-bar">Link <span style="text-decoration: underline">${user.email!}</span> to another account that you own.</div>
            </#if>

            <div class="row-group">
                <div class="row">
                    <span class="plain-label">Password</span> <span class="hint" data-desc="<@spring.message 'myaccount.link.currentPw' />"></span>
                    <div class="field">
                        <input class="full" id="linkCurrentPassword" type="password" value="${switchAndLinkUserAccountDTO.currentPassword!}" />
                    </div>
                </div>
                <@spring.bind "switchAndLinkUserAccountDTO.currentPassword" /> 
                <#list spring.status.errorMessages as error>
                <div class="field">
                    <span class="invalid">${error}</span>
                </div>
                </#list>
            </div>

            <div class="row-group">
                <div class="row">
                    <span class="group-heading-label">Account to be Linked</span>
                    <div class="field">
                        <#if user.allLinkedAccounts?has_content>
                            <table id="linkedAccountsTable" class="data-table">
                                <colgroup>
                                    <col />
                                    <col style="width: 60px;" />
                                </colgroup>
                                <tbody>
                                    <#list user.allLinkedAccounts as linkedAccount>
                                    <tr>
                                        <td>${linkedAccount.email?html}</td>
                                        <td><button type="button" class="button-delete" data-desc="Delete" email="${linkedAccount.email?html}">Delete</button></td>
                                    </tr>
                                    </#list>
                                </tbody>
                            </table>
                        </#if>
                    </div>
                </div>

                <div class="row">
                    <span class="plain-label">Email</span> <span class="hint" data-desc="<@spring.message 'myaccount.link.email' />"></span>
                    <div class="field">
                        <input class="full" id="linkEmail" type="text" value="${switchAndLinkUserAccountDTO.email!}" />
                    </div>
                </div>
                <@spring.bind "switchAndLinkUserAccountDTO.email" /> <#list spring.status.errorMessages as error>
                <div class="field">
                    <span class="invalid">${error}</span>
                </div>
                </#list>

                <div class="row">
                    <span class="plain-label">Password</span> <span class="hint" data-desc="<@spring.message 'myaccount.link.password' />"></span>
                    <div class="field">
                        <input class="full" id="linkPassword" type="password" value="${switchAndLinkUserAccountDTO.password!}" />
                    </div>
                </div>
                <@spring.bind "switchAndLinkUserAccountDTO.password" /> <#list spring.status.errorMessages as error>
                <div class="field">
                    <span class="invalid">${error}</span>
                </div>
                </#list>
            </div>
            <div class="buttons">
                <button class="blue" id="linkAccounts" type="button">Submit</button>
            </div>
    </div>
    <a href="#linkAcountDetailsSection"> </a>
</section>