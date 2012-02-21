<!DOCTYPE HTML>
<#import "/spring.ftl" as spring />
<html>
	<head>
	
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<title>Shell template</title>
		<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/style.css' />"/>
	
	</head>

	<body id="login">

		<div id="wrapper">
		
			<#include "/login/login_header.ftl"/>
			
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
		    <section id="register">
		    	<a href="#">Register Today...</a>
		    </section>
		    
		  </div>
		  
			<#include "/login/login_footer.ftl"/>
		
		</div>
		
		<!-- Scripts -->
		<script type="text/javascript" src="http://code.jquery.com/jquery-1.7.1.min.js"></script>
		<script type="text/javascript" src="js/modernizr.js"></script>
		<script type="text/javascript" src="js/plugins.js"></script>
	</body>
</html>
