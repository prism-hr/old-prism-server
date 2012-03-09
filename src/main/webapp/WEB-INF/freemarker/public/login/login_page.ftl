<!DOCTYPE HTML>
<#import "/spring.ftl" as spring />
<html>
	<head>
	
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<title>Shell template</title>
		
		<!-- Styles for Login List Page -->
		<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/public/global_public.css' />"/>
		<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/public/login.css' />"/>
		
		<!-- Styles for login List Page -->
		
		<!--[if lt IE 9]>
		<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
		<![endif]-->
	
	</head>

	<body id="bg">

		<div id="wrapper">
		
			<#include "/public/common/global_header.ftl"/>
			
		  	<!-- Middle. -->
		  	<div id="middle">
		  
		    <header>
		
		      <!-- App logo and tagline -->
		      <div class="logo"><img src="<@spring.url '/design/default/images/ph_logo_login.png'/>" alt="" /></div>
		
		      <div class="tagline">Your Gateway to<br />Research Opportunities</div>
		
		    </header>
		    
		    
		    <!-- Blurb. -->
		    <aside id="blurb">
			    <h2>Welcome to your new gateway to<br />UCL Postgraduate Research programmes<br />in Engineering Sciences.</h2>
					<p>Register today and begin your application to join some of the world's most highly regarded researchers and academics at the frontiers of discovery.</p>
		    </aside>
		
		    <!-- Login form. -->
		    <section id="login-box">
		    
		    	<form id="loginForm" method="post" 
		    					action="/pgadmissions/j_spring_security_check">
		    	<!--form method="post" action="w00pw00p"-->
		      		<p>&gt; Login</p>
		        	
		        	<input type="text" id ="username_or_email" name="j_username" placeholder="Email address" />
		        	<input type="password" id ="password" name="j_password" placeholder="Password" />
		        	
		        	<button name ="commit" type="submit" value="Sign In" class="blue">Go</button>
		      	</form>
		      	
		    </section>
		
		    <!-- Registration button. -->
    		<a id="big-button" href="#">Register Today...</a>
		    
		  </div>
		  
			<#include "/public/common/global_footer.ftl"/>
		
		</div>
		
		<!-- Scripts -->
		<script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/libraries.js'/>"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/script.js'/>"></script>
	</body>
</html>
