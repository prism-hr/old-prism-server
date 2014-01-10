<!DOCTYPE HTML>
<#import "/spring.ftl" as spring />
<#setting locale = "en_US">
<html>
	<head>
	
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<meta name="prism.version" content="<@spring.message 'prism.version'/>" >
		
		<title>UCL Postgraduate Admissions</title>
		
		<!-- Styles for Login List Page -->
		<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/public/global_public.css' />"/>
		<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/public/register.css' />"/>
		<link rel="shortcut icon" type="text/css" href="<@spring.url '/design/default/images/favicon.ico' />"/>
		
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
		    </aside>
		
		    <!-- Login form. -->
		    <section id="registration-box">
		    
		    	<form method="post" action= "/pgadmissions/refereeRegistration/submit">
		      		<p>&gt; Register Today...</p>
		            <input id="encryptedRecordId" type="hidden" name="encryptedRecordId"  value="${(encrypter.encrypt(referee.id))!}" />
        			<#if result?? && result.getFieldError('firstName')??>                    		
                    	<div class="alert alert-error">
                                <i class="icon-warning-sign"></i> <@spring.message  result.getFieldError('firstName').code /></div>                    		
                    </#if>
		            <input id="firstName" type="text" name="firstName" value="${(referee.firstName?html)!}" readonly/>
        			<#if result?? && result.getFieldError('firstName')??>                    		
                    	<div class="alert alert-error">
                                <i class="icon-warning-sign"></i> <@spring.message  result.getFieldError('firstName').code /></div>                    		
                    </#if>
		            <input id="lastName" type="text" name="lastName" value="${(referee.lastName?html)!}" readonly/>
        			<#if result?? && result.getFieldError('lastName')??>                    		
                    	<div class="alert alert-error">
                                <i class="icon-warning-sign"></i> <@spring.message  result.getFieldError('lastName').code /></div>                    		
                    </#if>
		            <input id="email" type="text" name="email" value="${(referee.email?html)!}" readonly/>
        			<#if result?? && result.getFieldError('email')??>                    		
                    	<div class="alert alert-error">
                                <i class="icon-warning-sign"></i> <@spring.message  result.getFieldError('email').code /></div>                    		
                    </#if>
		            <input id="password" type="password" name="password" value="Password""/>
        			<#if result?? && result.getFieldError('password')??>                    		
                    	<div class="alert alert-error">
                                <i class="icon-warning-sign"></i><@spring.message  result.getFieldError('password').code /></div>                    		
                    </#if>
		            <input id="confirmPassword" type="password" name="confirmPassword" value="Password"/>
                	<#if result?? && result.getFieldError('confirmPassword')??>                    		
                    	<div class="alert alert-error">
                                <i class="icon-warning-sign"></i><@spring.message  result.getFieldError('confirmPassword').code /></div>                    		
                    </#if>
		            <input id="currentReferee" type="hidden" name="currentReferee" value="${(encrypter.encrypt(referee.currentReferee.id))}"/>
		        	<button name ="commit" type="submit" value="Submit" class="btn btn-primary">GO</button>
		      	</form>
		      	
		      	<a href="pgadmissions/referee/login?activationCode=${referee.currentReferee.activationCode!}">&gt; Back</a>
		      	
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
