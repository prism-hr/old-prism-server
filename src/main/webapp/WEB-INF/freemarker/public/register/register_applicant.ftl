<!DOCTYPE HTML>
<#import "/spring.ftl" as spring />
<html>
	<head>
	
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<title>UCL Postgraduate Admissions</title>
		
		<!-- Styles for Login List Page -->
		<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/public/global_public.css' />"/>
		<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/public/register.css' />"/>
		
		<!-- Styles for login List Page -->
		
		<!--[if lt IE 9]>
		<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
		<![endif]-->
	
	</head>

	<!--[if IE 9]>
	<body class="ie9">
	<![endif]-->
	<!--[if lt IE 9]>
	<body class="old-ie">
	<![endif]-->
	<!--[if (gte IE 9)|!(IE)]><!-->
	<body>
	<!--<![endif]-->

		<div id="wrapper">
		
			<#include "/public/common/global_header.ftl"/>
			
		  	<!-- Middle. -->
		  	<div id="middle">
		  
		    <header>
		
		      <!-- App logo and tagline -->
		      <div class="logo"><img src="<@spring.url '/design/default/images/ph_logo_login.png'/>" alt="" /></div>
		
          <div class="tagline">A Spectrum of Postgraduate<br />Research Opportunities</div>
		
		    </header>
		    
		    
		    <!-- Blurb. -->
		    <aside id="blurb">
			    <h2>Welcome to your new gateway to<br />UCL Postgraduate Research programmes<br />in Engineering Sciences.</h2>
					<p>Register today and begin your application to join some of the world's most highly regarded researchers and academics at the frontiers of discovery.</p>
		    </aside>
		
		    <!-- Login form. -->
		    <section id="registration-box">
		    
		    	<form method="post" action= "/pgadmissions/register/submit">
		      		<p>&gt; Register Today...</p>
		            <input id="activationCode" type="hidden" name="activationCode" value="${pendingUser.activationCode!}"/>
		            
		            <input id="firstName" type="text" name="firstName" value="${pendingUser.firstName!}" placeholder="First Name" />
		            <@spring.bind "pendingUser.firstName" /> 
              		<#list spring.status.errorMessages as error>		                                		
                    	<span class="invalid">${error}</span>                    		
                    </#list>
		            
		            <input id="lastName" type="text" name="lastName" value="${pendingUser.lastName!}" placeholder="Last Name" />
		             <@spring.bind "pendingUser.lastName" /> 
		           <#list spring.status.errorMessages as error>		                                		
                    	<span class="invalid">${error}</span>                    		
                    </#list>
		            
		            <input id="email" type="text" name="email" value="${pendingUser.email!"Email Address"}"/>
		             <@spring.bind "pendingUser.email" /> 
		           <#list spring.status.errorMessages as error>		                                		
                    	<span class="invalid">${error}</span>                    		
                    </#list>
		            
		            <input id="password" type="password" name="password" value="default"/>
		             <@spring.bind "pendingUser.password" /> 
		        	<#list spring.status.errorMessages as error>		                                		
                    	<span class="invalid">${error}</span>                    		
                    </#list>
		            
		            <input id="confirmPassword" type="password" name="confirmPassword" value="default"/>
		             <@spring.bind "pendingUser.confirmPassword" /> 
		            <#list spring.status.errorMessages as error>		                                		
                    	<span class="invalid">${error}</span>                    		
                    </#list>
		            
		            
		        	<button name ="commit" type="submit" value="Submit" class="blue">GO</button>
		      	</form>
		      	
		      	<a href="/pgadmissions/login">&gt; Back to Login</a>
		      	
		    </section>
		
		  </div>
		  
			<#include "/public/common/global_footer.ftl"/>
		
		</div>
		
		<!-- Scripts -->
		<script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/libraries.js'/>"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/script.js'/>"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/register_applicant.js'/>"></script>
	</body>
</html>
