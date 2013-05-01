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
			    
		</ul>
			        
		<div class="user">
			<#if model?? && model.user??>
            ${model.user.email!}
			<#elseif user??>
			${user.email!}
			</#if>
			<a class="button user-logout" href="<@spring.url '/j_spring_security_logout'/>">Logout</a>
		</div>
	</nav>
			      
</header>
</#macro>