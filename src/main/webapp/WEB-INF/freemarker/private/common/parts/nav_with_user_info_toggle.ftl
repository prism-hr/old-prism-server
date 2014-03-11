<#macro header activeTab="applications">
<header>

	<#if model?has_content && model.user?has_content>
		<#assign loggedInUser = model.user>
	<#elseif user?has_content>
		<#assign loggedInUser = user>
	</#if>
          
    <!-- Main tabbed menu -->
    <nav>
        <div class="navlia"> 
            <ul>
                <li
                	<#if activeTab=="account">
                		class="current"
                	</#if>>
                	<a href="<@spring.url '/myAccount'/>">
                		<i class="icon-user"></i>
                		My Account
                	</a>
                </li>           
                <li
                	<#if activeTab=="applications">
                		class="current"
                	</#if>>
                	<a href="<@spring.url '/applications'/>">
                		<i class="icon-file"></i>
                		My Applications
                	</a>
                </li>    
                <#if permissionsService.canSeeOpportunityRequests() >
                  	<li
                  		<#if activeTab=="requests">
                  			class="current"
                  		</#if>>
                  		<a href="<@spring.url '/requests'/>">
                  			<i class="icon-rocket"></i>
                  			New Programme Requests
                  		</a>
                  	</li>
                </#if>
                <#if loggedInUser.isInRole('SUPERADMINISTRATOR') || loggedInUser.programsOfWhichAdministrator?has_content || loggedInUser.isInRole('ADMITTER')>
                  	<li
                  		<#if activeTab=="users">
                  			class="current"
                  		</#if>>
                  		<a href="<@spring.url '/manageUsers/edit'/>">
                  			<i class="icon-pencil"></i>
                  			Manage Users
                  		</a>
                  	</li>
                </#if>
                <#if loggedInUser.isInRole('SUPERADMINISTRATOR') || loggedInUser.programsOfWhichAdministrator?has_content>
                	<li
                		<#if activeTab=="config">
                			class="current"
                		</#if>>
                		<a href="<@spring.url '/configuration'/>">
                			<i class="icon-wrench"></i>
                			Configuration
                		</a>
                	</li>
                </#if>
              	<li
              		<#if activeTab=="prospectus">
              			class="current"
              		</#if>>
              		<a href="<@spring.url '/prospectus'/>">
              			<i class="icon-tasks"></i>
              			Prospectus
              		</a>
              	</li>
            </ul>
        </div>
     
        <div class="user">
			<span class="dropdown">
                <button class="btn dropdown-toggle" data-toggle="dropdown">${loggedInUser.email?html}<span class="caret"></span></button>
                <ul id="switchUserList" class="dropdown-menu">
                    <#list loggedInUser.allLinkedAccounts as linkedAccount>
                        <li><a href="javascript:void(0)">${linkedAccount.email?html}</a></li>
                    </#list>
                    <#if loggedInUser.allLinkedAccounts?has_content>
                        <li class="divider"></li>
                    </#if>
                    <li><a href="javascript:void(0)">Link Accounts...</a></li>
                    <li class="divider"></li>
                    <li><a href="<@spring.url '/j_spring_security_logout'/>">Logout</a></li>
                </ul>
            </span>
        </div>
        
    </nav>
 </header>
</#macro>