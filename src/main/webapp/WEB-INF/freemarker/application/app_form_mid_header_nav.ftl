<header>
			
	<!-- App logo and tagline -->
	<div class="logo"><img src="<@spring.url '/design/default/images/ph_logo_app.png'/>" alt="" /></div>
			
	<div class="tagline">Your Gateway to Research Opportunities</div>
			
	<!-- Main tabbed menu -->
	<nav>
		<ul>
			<li><a href="#">My account</a></li>    
			<li class="current"><a href="#">My applications <span class="indicator blue">1</span></a></li>    
			<li><a href="#">Messages</a></li>    
			<li><a href="#">Help</a></li>    
		</ul>
			        
		<div class="user">
			${model.applicationForm.user.firstName!} ${' '} ${model.applicationForm.user.lastName!}
			<a class="button user-logout">Logout</a>
		</div>
	</nav>
			      
</header>
