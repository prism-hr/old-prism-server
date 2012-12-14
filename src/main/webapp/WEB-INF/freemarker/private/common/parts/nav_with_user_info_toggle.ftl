<#macro header activeTab="applications">
<header>
            
    <!-- App logo and tagline -->
    <div class="logo"><img src="<@spring.url '/design/default/images/ph_logo_app.png'/>" alt="" /></div>
            
    <div class="tagline">A Spectrum of Postgraduate Research Opportunities</div>
            
    <!-- Main tabbed menu -->
    <nav>
        <div class="navlia">
            <ul>
                <li <#if activeTab=="account">class="current"</#if>><a href="<@spring.url '/myAccount'/>">My Account</a></li>           
                <li <#if activeTab=="applications">class="current"</#if>><a href="<@spring.url '/applications'/>">My Applications </a></li>    
                <#if user?? && (user.isInRole('SUPERADMINISTRATOR') || user.isInRole('ADMINISTRATOR'))>
                <li <#if activeTab=="users">class="current"</#if>><a href="<@spring.url '/manageUsers/edit'/>">Manage Users</a></li>
                <li <#if activeTab=="config">class="current"</#if>><a href="<@spring.url '/configuration'/>">Configuration</a></li>
                </#if>
                <li <#if activeTab=="help">class="current"</#if>><a href="http://www.prism.cs.ucl.ac.uk/help/" target="_blank">Help</a></li>    
            </ul>
        </div>
     
        <div class="user">
            <#if model?? && model.user??>
                <span class="dropdown">
                    <button class="btn btn-small dropdown-toggle" data-toggle="dropdown">${model.user.email?html}<span class="caret"></span></button>
                    <ul id="switchUserList" class="dropdown-menu">
                        <#list model.user.linkedAccounts as linkedAccount>
                            <li><a href="javascript:void(0)">${linkedAccount.email?html}</a></li>
                        </#list>
                        <#if model.user.linkedAccounts?has_content>
                            <li class="divider"></li>
                        </#if>
                        <li><a href="javascript:void(0)">Link Accounts...</a></li>
                        <li class="divider"></li>
                        <li><a href="<@spring.url '/j_spring_security_logout'/>">Logout</a></li>
                    </ul>
                </span>
            <#elseif user??>
                <span class="dropdown">
                    <button class="btn btn-small dropdown-toggle" data-toggle="dropdown">${user.email?html}<span class="caret"></span></button>
                    <ul id="switchUserList" class="dropdown-menu">
                        <#list user.linkedAccounts as linkedAccount>
                            <li><a href="javascript:void(0)">${linkedAccount.email?html}</a></li>
                        </#list>
                        <#if user.linkedAccounts?has_content>
                            <li class="divider"></li>
                        </#if>
                        <li><a href="javascript:void(0)">Link Accounts...</a></li>
                        <li class="divider"></li>
                        <li><a href="<@spring.url '/j_spring_security_logout'/>">Logout</a></li>
                    </ul>
                </span>
            </#if>
        </div>
    </nav>
 </header>
</#macro>