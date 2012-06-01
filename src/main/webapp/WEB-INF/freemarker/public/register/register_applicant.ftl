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
		
		      <div class="tagline">Your Gateway to<br />Research Opportunities</div>
		
		    </header>
		    
		    
		    <!-- Blurb. -->
		    <aside id="blurb">
			    <h2>Welcome to your new gateway to<br />UCL Postgraduate Research programmes<br />in Engineering Sciences.</h2>
					<p>Register today and begin your application to join some of the world's most highly regarded researchers and academics at the frontiers of discovery.</p>
		    </aside>
		
		    <!-- Login form. -->
		    <section id="registration-box">
		    
		    	<form method="post" <#if (model.isSuggestedUser??)> action= "/pgadmissions/register/submit?isSuggestedUser=${model.isSuggestedUser?string('#######')}" <#else> action= "/pgadmissions/register/submit"</#if>>
		      		<p>&gt; Register Today...</p>
		            <input id="recordId" type="hidden" name="recordId" />
		            
		            <input id="firstName" type="text" name="firstName" value="${model.record.firstName!}" placeholder="First Name" />
		            <#if model.hasError('firstName')>                    		
                    	<span class="invalid"><@spring.message  model.result.getFieldError('firstName').code /></span>                    		
                    </#if>
		            
		            <input id="lastName" type="text" name="lastName" value="${model.record.lastName!}" placeholder="Last Name" />
		            <#if model.hasError('lastName')>                    		
                    	<span class="invalid"><@spring.message  model.result.getFieldError('lastName').code /></span>                    		
                    </#if>
		            
		            <input id="email" type="text" name="email" value="${model.record.email!"Email Address"}"/>
		            <#if model.hasError('email')>                    		
                    	<span class="invalid"><@spring.message  model.result.getFieldError('email').code /></span>                    		
                    </#if>
		            
		            <input id="password" type="password" name="password" value="default"/>
		            <#if model.hasError('password')>                    		
                    	<span class="invalid"><@spring.message  model.result.getFieldError('password').code /></span>                    		
                    </#if>
		            
		            <input id="confirmPassword" type="password" name="confirmPassword" value="default"/>
		            <#if model.hasError('confirmPassword')>                    		
                    	<span class="invalid"><@spring.message  model.result.getFieldError('confirmPassword').code /></span>                    		
                    </#if>
		            
		            <input type="hidden" name="programId" value="${RequestParameters.program!}"/>
		            
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
