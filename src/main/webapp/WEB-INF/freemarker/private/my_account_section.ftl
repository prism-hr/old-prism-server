<#import "/spring.ftl" as spring />
<ul class="tabs">
  <li><a href="#accountDetails">Account Details</a></li>
  <li><a href="#linkAccountstab">Link Accounts</a></li>
</ul>
<div id="accountDetails" class="tab-page">
	<section id="accountDetailsSection" class="form-rows">
    <h2 class="no-arrow">Account Details</h2>
    <div>
        <form autocomplete="off">
            <#if RequestParameters.messageCode??>
            <div class="alert alert-info">
                <i class="icon-info-sign"></i> <@spring.message '${RequestParameters.messageCode}'/></div>
            <#else>
            <div class="alert alert-info">
                <i class="icon-info-sign"></i> Edit your account details.</div>
            </#if>

            <div class="row-group">
                <div class="row">
                    <label id="firstName-lbl" class="plain-label" for="firstName">First Names<em>*</em></label> <span class="hint" data-desc="<@spring.message 'myaccount.firstName'/>"></span>
                    <div class="field">
                        <input class="full" type="text" id="firstName" value="${(updatedUser.firstName?html)!}" />
                        <@spring.bind "updatedUser.firstName" /> <#list spring.status.errorMessages as error>
                        <div class="alert alert-error">
                               <i class="icon-warning-sign"></i> ${error}
                        </div>
                        </#list>
                    </div>
                </div>
                
                <div class="row">
                    <label class="plain-label" for="firstName2"></label> 
                    <div class="field">
                        <input class="full" type="text" id="firstName2" value="${(updatedUser.firstName2?html)!}" />
                        <@spring.bind "updatedUser.firstName2" /> <#list spring.status.errorMessages as error>
                            <div class="alert alert-error">
                                <i class="icon-warning-sign"></i> ${error}
                            </div>
                        </#list>
                    </div>
                </div>
                
                <div class="row">
                    <label class="plain-label" for="firstName3"></label> 
                    <div class="field">
                        <input class="full" type="text" id="firstName3" value="${(updatedUser.firstName3?html)!}" />
                            <@spring.bind "updatedUser.firstName3" /> <#list spring.status.errorMessages as error>
                            <div class="alert alert-error">
                                <i class="icon-warning-sign"></i> ${error}
                            </div>
                        </#list>
                    </div>
                </div>

                <div class="row">
                    <label id="lastName-lbl" class="plain-label" for="lastName">Last Name<em>*</em></label> <span class="hint" data-desc="<@spring.message 'myaccount.lastName'/>"></span>
                    <div class="field">
                        <input class="full" type="text" id="lastName" value="${(updatedUser.lastName?html)!}" />
                        <@spring.bind "updatedUser.lastName" /> <#list spring.status.errorMessages as error>
                            <div class="alert alert-error">
                                <i class="icon-warning-sign"></i> ${error}
                            </div>
                        </#list>
                    </div>
                </div>

                <div class="row">
                    <label id="email-lbl" class="plain-label" for="email">Email<em>*</em></label> <span class="hint" data-desc="<@spring.message 'myaccount.email'/>"></span>
                    <div class="field">
                        <input class="full" type="email" id="email" value="${(updatedUser.email?html)!}" />
                        <@spring.bind "updatedUser.email" /> <#list spring.status.errorMessages as error>
                            <div class="alert alert-error">
                                <i class="icon-warning-sign"></i> ${error}
                             </div>
                        </#list>
                    </div>
                </div>
            </div>

            <div class="row-group">
                <h3>Change Password</h3>
                <div class="row">
                    <label class="plain-label" for="currentPassword">Current Password<em>*</em></label> 
                    <span class="hint" data-desc="<@spring.message 'myaccount.currentPw'/>"></span>
                    <div class="field">
                        <input class="full" id="currentPassword" type="password" />
                            <@spring.bind "updatedUser.password" /> <#list spring.status.errorMessages as error>
                            <div class="alert alert-error">
                                <i class="icon-warning-sign"></i> ${error}
                            </div>
                        </#list>
                    </div>
                </div>

                <div class="row">
                    <label class="plain-label" for="newPassword">New Password<em>*</em></label> 
                    <span class="hint" data-desc="<@spring.message 'myaccount.newPw'/>"></span>
                    <div class="field">
                        <input class="full" id="newPassword" type="password" />
                        <@spring.bind "updatedUser.newPassword" /> <#list spring.status.errorMessages as error>
                            <div class="alert alert-error">
                                <i class="icon-warning-sign"></i> ${error}
                            </div>
                        </#list>
                    </div>
                </div>

                <div class="row">
                    <label class="plain-label" for="confirmNewPass">Confirm new Password<em>*</em></label> 
                    <span class="hint" data-desc="<@spring.message 'myaccount.confirmPw'/>"></span>
                    <div class="field">
                        <input class="full" id="confirmNewPass" type="password" />
                        <@spring.bind "updatedUser.confirmPassword" /> <#list spring.status.errorMessages as error>
                            <div class="alert alert-error">
                                <i class="icon-warning-sign"></i> ${error}
                            </div>
                        </#list>
                    </div>
                </div>
            </div>
            <!-- .row-group -->
            <div class="buttons">
                <button class="btn btn-primary" id="saveChanges" type="button">Submit</button>
            </div>
        </form>
    </div>
    </section>
    </div>

	<div id="linkAccountstab" class="tab-page <#if RequestParameters.messageCodeLink??>preselected</#if>">
    <section id="linkAcountDetailsSection" class="form-rows">
        <h2 class="no-arrow">Linked Accounts</h2>
        <div id="linkAccountsSection">   
            <form>          
                <#if RequestParameters.messageCodeLink??>
                    <div class="alert alert-info">
                    <i class="icon-info-sign"></i> <@spring.message '${RequestParameters.messageCodeLink}'/></div>
                <#else>
                    <div class="alert alert-info">
                    <i class="icon-info-sign"></i> Link <span style="text-decoration: underline">${user.email!}</span> to another account that you own.</div>
                </#if>
                <#if user.allLinkedAccounts?has_content>
                    <div class="row-group">               
                            <table class="table table-striped table-condensed table-bordered table-hover">
                                <colgroup>
                                    <col />
                                    <col style="width: 10px;" />
                                </colgroup>
                                <tbody>
                                    <#list user.allLinkedAccounts as linkedAccount>
                                    <tr>
                                        <td>${linkedAccount.firstName?html} ${linkedAccount.lastName?html} (${linkedAccount.email?html})</td>
                                        <td><button type="button" class="button-delete" data-desc="Delete" email="${linkedAccount.email?html}">Delete</button></td>
                                    </tr>
                                    </#list>
                                </tbody>
                            </table>
                    </div>
                </#if>
                 <div class="row-group">
                    <h3 >Link new account</h3>
                    <div class="row">
                        <label class="plain-label grey-label" for="linkCurrentPassword">Account 1 Email<em>*</em></label> 
                        <span class="hint grey"></span>
                        <div class="field">
                            <input class="full" disabled type="text" value="${user.email!}" />
                        </div>
                    </div>
                    <div class="row">
                        
                        <label class="plain-label" for="linkCurrentPassword">Account 1 Password<em>*</em></label> 
                        <span class="hint" data-desc="<@spring.message 'myaccount.link.currentPw' />"></span>
                        <div class="field">
                            <input class="full" id="linkCurrentPassword" type="password" value="${switchAndLinkUserAccountDTO.currentPassword!}" />
                            <@spring.bind "switchAndLinkUserAccountDTO.currentPassword" /> 
                                <#list spring.status.errorMessages as error>
                                <div class="alert alert-error">
                                    <i class="icon-warning-sign"></i> ${error}
                                </div>
                            </#list>
                        </div>
                    </div>
                    <div class="row">
                         
                        <label class="plain-label" for="linkEmail">Account 2 Email<em>*</em></label> 
                        <span class="hint" data-desc="<@spring.message 'myaccount.link.email' />"></span>
                        <div class="field">
                            <input class="full" id="linkEmail" type="text" value="${switchAndLinkUserAccountDTO.email!}" />
                            <@spring.bind "switchAndLinkUserAccountDTO.email" /> 
                            <#list spring.status.errorMessages as error>
                                <div class="alert alert-error">
                                    <i class="icon-warning-sign"></i> ${error}
                                </div>
                            </#list>
                        </div>
                    </div>
                    
                    <div class="row">
                        <label class="plain-label" for="linkPassword"> Account 2 Password<em>*</em></label> 
                        <span class="hint" data-desc="<@spring.message 'myaccount.link.password' />"></span>
                        <div class="field">
                            <input class="full" id="linkPassword" type="password" value="${switchAndLinkUserAccountDTO.password!}" />
                            <@spring.bind "switchAndLinkUserAccountDTO.password" /> 
                            <#list spring.status.errorMessages as error>
                            <div class="alert alert-error">
                                <i class="icon-warning-sign"></i> ${error}
                            </div>
                            </#list>
                        </div>
                    </div>     
                </div>
                <div class="buttons">
                    <button class="btn btn-primary" id="linkAccounts" type="button">Submit</button>
                </div>
            </form>
        </div>
        <a href="#linkAcountDetailsSection"> </a>
    </section>
    </div>