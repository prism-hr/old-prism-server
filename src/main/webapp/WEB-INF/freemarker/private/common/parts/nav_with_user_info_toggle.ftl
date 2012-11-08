<#macro header activeTab="applications">
<header>
            
    <!-- App logo and tagline -->
    <div class="logo"><img src="<@spring.url '/design/default/images/ph_logo_app.png'/>" alt="" /></div>
            
    <div class="tagline">A Spectrum of Postgraduate Research Opportunities</div>
            
    <!-- Main tabbed menu -->
    <nav>
        <ul>
            <li <#if activeTab=="account">class="current"</#if>><a href="<@spring.url '/myAccount'/>">My Account</a></li>           
            <li <#if activeTab=="applications">class="current"</#if>><a href="<@spring.url '/applications'/>">My Applications </a></li>    
            <#if user?? && (user.isInRole('SUPERADMINISTRATOR') || user.isInRole('ADMINISTRATOR'))>
            <li <#if activeTab=="users">class="current"</#if>><a href="<@spring.url '/manageUsers/edit'/>">Manage Users</a></li>
            <li <#if activeTab=="config">class="current"</#if>><a href="<@spring.url '/configuration'/>">Configuration</a></li>
            </#if>
            <li <#if activeTab=="help">class="current"</#if>><a href="http://www.prism.cs.ucl.ac.uk/help/" target="_blank">Help</a></li>    
        </ul>
                    
        <div class="user">
            <#if model?? && model.user??>
                <#if model.user.linkedAccounts?has_content>
                    <select id="linkedUserAccountsDrop" name="linkedUserAccountsDrop">
                        <option value="${model.user.email!}" selected="selected">${model.user.email?html}</option>
                        <#list model.user.linkedAccounts as linkedAccount>
                            <option value="${linkedAccount.email!}">${linkedAccount.email?html}</option>
                        </#list>
                            <option value="LINK">Link two accounts...</option>
                    </select>
                <#else>
                    ${model.user.email!}
                </#if>
            <#elseif user??>
                <#if user.linkedAccounts?has_content>
                    <select id="linkedUserAccountsDrop" name="linkedUserAccountsDrop">
                        <option value="${user.email!}" selected="selected">${user.email?html}</option>
                        <#list user.linkedAccounts as linkedAccount>
                            <option value="${linkedAccount.email!}">${linkedAccount.email?html}</option>
                        </#list>
                            <option value="LINK">Link two accounts...</option>
                    </select>
                <#else>
                    ${user.email!}
                </#if>
            </#if>
            <a class="button user-logout" href="<@spring.url '/j_spring_security_logout'/>">Logout</a>
        </div>
    </nav>
                  
</header>
</#macro>