<!DOCTYPE HTML>
<#import "/spring.ftl" as spring />
<html>
	<head>
	
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<title>Shell template</title>
		
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
		
		      <div class="tagline">Your Gateway to<br />Research Opportunities</div>
		
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
		            <input id="recordId" type="hidden" name="recordId" />
		            
		            <input id="firstname" type="text" name="firstname" value="${model.record.firstname!}" placeholder="First Name"/>
		            <#if model.hasError('firstname')>                    		
                    	<span class="invalid"><@spring.message  model.result.getFieldError('firstname').code /></span>                    		
                    </#if>
		            
		            <input id="lastname" type="text" name="lastname" value="${model.record.lastname!}" placeholder="Last Name"/>
		            <#if model.hasError('lastname')>                    		
                    	<span class="invalid"><@spring.message  model.result.getFieldError('lastname').code /></span>                    		
                    </#if>
		            
		            <input id="email" type="text" name="email" value="${model.record.email!}" placeholder="Email Address"/>
		            <#if model.hasError('email')>                    		
                    	<span class="invalid"><@spring.message  model.result.getFieldError('email').code /></span>                    		
                    </#if>
		            
		            <input id="password" type="password" name="password" placeholder="Password"/>
		            <#if model.hasError('password')>                    		
                    	<span class="invalid"><@spring.message  model.result.getFieldError('password').code /></span>                    		
                    </#if>
		            
		            <input id="confirmPassword" type="password" name="confirmPassword" placeholder="Confirm Password"/>
		            <#if model.hasError('confirmPassword')>                    		
                    	<span class="invalid"><@spring.message  model.result.getFieldError('confirmPassword').code /></span>                    		
                    </#if>
		            
		            <input type="hidden" name="projectId" value="${RequestParameters.project!}"/>
		            
		        	<button name ="commit" type="submit" value="Submit" class="blue">GO</button>
		      	</form>
		      	
		      	<a href="/pgadmissions/login">&gt; Back</a>
		      	
		    </section>
		
		  </div>
		  
			<#include "/public/common/global_footer.ftl"/>
		
		</div>
		
		<!-- Scripts -->
		<script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/libraries.js'/>"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/script.js'/>"></script>
	</body>
</html>
