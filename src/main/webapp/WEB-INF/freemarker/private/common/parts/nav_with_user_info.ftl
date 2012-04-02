<header>
			
	<!-- App logo and tagline -->
	<div class="logo"><img src="<@spring.url '/design/default/images/ph_logo_app.png'/>" alt="" /></div>
			
	<div class="tagline">Your Gateway to Research Opportunities</div>
			
	<!-- Main tabbed menu -->
	<nav>
		<ul>
			<li><a href="#">My account</a></li>			
			<li class="current"><a href="<@spring.url '/applications'/>">My applications </a></li>    
			<li><a href="#">Messages</a></li>    
			<li><a href="#">Help</a></li>    
		</ul>
			        
		<div class="user">
			<#if model??>
			${model.user.firstName!} ${' '} ${model.user.lastName!}
			<#elseif user??>
			${user.firstName!} ${' '} ${user.lastName!}
			</#if>
			<a class="button user-logout" href="<@spring.url '/j_spring_security_logout'/>">Logout</a>
		</div>
	</nav>
			      
</header>
