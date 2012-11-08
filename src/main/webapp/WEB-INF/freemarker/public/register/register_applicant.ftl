<!DOCTYPE HTML>
<#import "/spring.ftl" as spring />
<html>
	<head>
	
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<title>UCL Postgraduate Admissions</title>
		
		<!-- Styles for Login List Page -->
		<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/public/global_public.css' />"/>
		<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/public/register.css' />"/>
		<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/jquery-ui-1.8.23.custom.css' />"/>
		
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
		          <p>Welcome to <b>UCL Prism</b>, the gateway<br />
							to postgraduate research opportunities<br />
							in UCL Engineering.</p>
		    </aside>
		
		    <!-- Login form. -->
		    <section id="registration-box">
		    
		    	<form method="post" action= "/pgadmissions/register/submit">
		      		<p>&gt; Register Today...</p>
		            <input id="activationCode" type="hidden" name="activationCode" value="${pendingUser.activationCode!}"/>
		            
		            <#if RequestParameters.activationCode?has_content>
                    <div id="firstName_tooltip_input" name="firstName_tooltip_input" data-desc="<@spring.message 'registration.email'/>" style="cursor: pointer;">
                    </#if>
		            <input id="firstName" type="text" name="firstName" value="<#if pendingUser.firstName?? && pendingUser.firstName?length &gt; 0>${pendingUser.firstName!"First Name"}<#else>First Name</#if>" <#if RequestParameters.activationCode?has_content>disabled="disabled"</#if> />
		            <#if RequestParameters.activationCode?has_content>
                    </div>
                    </#if>
		            <@spring.bind "pendingUser.firstName" /> 
              		<#list spring.status.errorMessages as error>		                                		
                    	<span class="invalid">${error}</span>                    		
                    </#list>
		            
		            <#if RequestParameters.activationCode?has_content>
                    <div id="lastName_tooltip_input" name="lastName_tooltip_input" data-desc="<@spring.message 'registration.email'/>" style="cursor: pointer;">
                    </#if>
		            <input id="lastName" type="text" name="lastName" value="<#if pendingUser.lastName?? && pendingUser.lastName?length &gt; 0>${pendingUser.lastName!"Last Name"}<#else>Last Name</#if>" <#if RequestParameters.activationCode?has_content>disabled="disabled"</#if> />
                    <#if RequestParameters.activationCode?has_content>
                    </div>
                    </#if>
                    <@spring.bind "pendingUser.lastName" /> 
                    <#list spring.status.errorMessages as error>		                                		
                        <span class="invalid">${error}</span>                    		
                    </#list>
		            
		            <#if RequestParameters.activationCode?has_content>
		            <div id="email_tooltip_input" name="email_tooltip_input" data-desc="<@spring.message 'registration.email'/>" style="cursor: pointer;">
		            </#if>
		            <input id="email" type="text" name="email" value="<#if pendingUser.email?? && pendingUser.email?length &gt; 0>${pendingUser.email!"Email Address"}<#else>Email Address</#if>" <#if RequestParameters.activationCode?has_content>disabled="disabled"</#if> />
		            <#if RequestParameters.activationCode?has_content>
                    </div>
                    </#if>
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
		      	
		      	<#if RequestParameters.activationCode?has_content>
		      	   <p>&nbsp;</p>
                <#else>
                    <#if Session.applyRequest?has_content>
                        <a href="/pgadmissions/login">&gt; Already Registered?</a>
                    <#else>
                        <p>&nbsp;</p>
                    </#if>
                </#if>
		    
		    </section>
		
		  </div>
		  
		<#include "/public/common/global_footer.ftl"/>
		
		</div>
		
		<!-- Scripts -->
		<script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/jquery-ui-1.8.23.custom.min.js' />"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/libraries.js'/>"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/script.js'/>"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/register_applicant.js'/>"></script>
	</body>
</html>
