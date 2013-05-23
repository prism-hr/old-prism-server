<#macro header activeTab="applications">
<header>
            
    <!-- App logo and tagline -->
    <div class="logo"><img src="<@spring.url '/design/default/images/ph_logo_app.png'/>" alt="" /></div>
            
    <div class="tagline">A Spectrum of Postgraduate Research Opportunities</div>
            
    <!-- Main tabbed menu -->
    <nav>
        <div class="navlia"> 
            <ul>
                <li <#if activeTab=="account">class="current"</#if>><a href="<@spring.url '/myAccount'/>"><i class="icon-user"></i> My Account</a></li>           
                <li <#if activeTab=="applications">class="current"</#if>><a href="<@spring.url '/applications'/>"><i class="icon-file"></i> My Applications </a></li>    
                <#if user?? && (user.isInRole('SUPERADMINISTRATOR') || user.isInRole('ADMINISTRATOR'))>
                <li <#if activeTab=="users">class="current"</#if>><a href="<@spring.url '/manageUsers/edit'/>"><i class="icon-pencil"></i> Manage Users</a></li>
                <li <#if activeTab=="config">class="current"</#if>><a href="<@spring.url '/configuration'/>"><i class="icon-wrench"></i> Configuration</a></li>
                </#if>
                <li <#if activeTab=="prospectus">class="current"</#if>><a href="<@spring.url '/prospectus'/>"><i class="icon-tasks"></i> Prospectus</a></li>
            </ul>
        </div>
     
        <div class="user">
            <#if model?? && model.user??>
                <span class="dropdown">
                    <button class="btn dropdown-toggle" data-toggle="dropdown">${model.user.email?html}<span class="caret"></span></button>
                    <ul id="switchUserList" class="dropdown-menu">
                        <#list model.user.allLinkedAccounts as linkedAccount>
                            <li><a href="javascript:void(0)">${linkedAccount.email?html}</a></li>
                        </#list>
                        <#if model.user.allLinkedAccounts?has_content>
                            <li class="divider"></li>
                        </#if>
                        <li><a href="javascript:void(0)">Link Accounts...</a></li>
                        <li class="divider"></li>
                        <li><a href="<@spring.url '/j_spring_security_logout'/>">Logout</a></li>
                    </ul>
                </span>
            <#elseif user??>
                <span class="dropdown">
                    <button class="btn dropdown-toggle" data-toggle="dropdown">${user.email?html}<span class="caret"></span></button>
                    <ul id="switchUserList" class="dropdown-menu">
                        <#list user.allLinkedAccounts as linkedAccount>
                            <li><a href="javascript:void(0)">${linkedAccount.email?html}</a></li>
                        </#list>
                        <#if user.allLinkedAccounts?has_content>
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