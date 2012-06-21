<header>
			
	<!-- App logo and tagline -->
	<div class="logo"><img src="<@spring.url '/design/default/images/ph_logo_app.png'/>" alt="" /></div>
			
	<div class="tagline">A Spectrum of Postgraduate Research Opportunities</div>
			
	<!-- Main tabbed menu -->
	<nav>
		<ul>
			<li><a href="<@spring.url '/myAccount'/>">My Account</a></li>			
			<li class="current"><a href="<@spring.url '/applications'/>">My Applications </a></li>    
			<#if user?? && (user.isInRole('SUPERADMINISTRATOR') || user.isInRole('ADMINISTRATOR'))>
			<li><a href="<@spring.url '/manageUsers/edit'/>">Manage Users</a></li>
			<li><a href="<@spring.url '/badge'/>">Badge</a></li>
			</#if>
			<#if user?? && (user.isInRole('SUPERADMINISTRATOR'))>
			<li><a href="<@spring.url '/configuration'/>">Configuration</a></li>
			</#if>
			<li><a href="#">Help</a></li>    
		</ul>
			        
		<div class="user">
			<#if model?? && model.user??>
			${model.user.firstName!} ${' '} ${model.user.lastName!}
			<#elseif user??>
			${user.firstName!} ${' '} ${user.lastName!}
			</#if>
			<a class="button blue user-logout" href="<@spring.url '/j_spring_security_logout'/>">Logout</a>
		</div>
	</nav>
			      
</header>
