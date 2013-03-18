<!DOCTYPE HTML>
<#import "/spring.ftl" as spring />
<html>
	<head>
	
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<meta name="prism.version" content="<@spring.message 'prism.version'/>" >
		<title>UCL Postgraduate Admissions</title>
		
		<!-- Styles for Login List Page -->
		<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/bootstrap.min.css' />"/>
		<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/font-awesome.min.css' />"/>
		<script type="text/javascript" src="<@spring.url '/design/default/js/bootstrap.min.js' />"></script>
		<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/public/global_public.css' />"/>
		<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/public/register.css' />"/>
		<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/jquery-ui-1.8.23.custom.css' />"/>
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
		          <p>Welcome to <b>UCL Prism</b>, the gateway<br />
							to postgraduate research opportunities<br />
							in UCL Engineering.</p>
		    </aside>
		
		    <!-- Login form. -->
		    <section id="registration-box">
		    
		    	<form method="post" action= "/pgadmissions/register/submit" class="form-horizontal">
		      		<div id="legend">
		                <legend >Register Today</legend>
		              </div>

		            <input id="activationCode" type="hidden" name="activationCode" value="${pendingUser.activationCode!}"/>
		            <div class="control-group">
		                <label class="control-label" for="firstName">First Name <em>*</em></label>
		                <span class="hint" data-desc="Please enter your First Name"></span>
		                <div class="controls">
			            <input id="firstName" type="text" name="firstName" value='${(pendingUser.firstName?html)!""}' <#if RequestParameters.activationCode?has_content>disabled="disabled"</#if> />
			            <@spring.bind "pendingUser.firstName" /> 
	              		<#list spring.status.errorMessages as error>	
	              		 	<div class="alert alert-error">	                                		
	                    	<i class="icon-warning-sign"></i> ${error}
	                    	</div>                 		
	                    </#list>
						</div>
                	</div>

		            <div class="control-group">
		                <label class="control-label" for="lastName">Last Name <em>*</em></label>
		                <span class="hint" data-desc="Please enter your Last Name"></span>
		                <div class="controls">
			            <input id="lastName" type="text" name="lastName" value='${(pendingUser.lastName?html)!""}' <#if RequestParameters.activationCode?has_content>disabled="disabled"</#if> />
	                    <@spring.bind "pendingUser.lastName" /> 
	                    <#list spring.status.errorMessages as error>		                                		
	                        <div class="alert alert-error"><i class="icon-warning-sign"></i> ${error}</div>                     		
	                    </#list>
		           		</div>
                	</div>
					
					<div class="control-group">
		                <label class="control-label" for="email">Email <em>*</em></label>
		                <span class="hint" data-desc="Please enter your Email Address"></span>
		                <div class="controls">
			            <#if RequestParameters.activationCode?has_content>
			            <div id="email_tooltip_input" name="email_tooltip_input" data-desc="<@spring.message 'registration.email'/>" style="cursor: pointer;">
			            </#if>
			            <input id="email" type="text" placeholder="Email Address" name="email" value='${(pendingUser.email?html)!""}' <#if RequestParameters.activationCode?has_content>disabled="disabled"</#if> />
			            <#if RequestParameters.activationCode?has_content>
	                    </div>
	                    </#if>
			            <@spring.bind "pendingUser.email" /> 
	                    <#list spring.status.errorMessages as error>		                                		
	                        <div class="alert alert-error"><i class="icon-warning-sign"></i> ${error}</div>                   		
	                    </#list>
		            	</div>
                	</div>
					
					<div class="control-group">
		                <label class="control-label" for="password">Password <em>*</em></label>
		                <span class="hint" data-desc="Please enter a password wiht a minimum of 8 characters"></span>
		                <div class="controls">
			            <input id="password" type="password" name="password" placeholder="Password"/>
	                    <@spring.bind "pendingUser.password" /> 
			        	<#list spring.status.errorMessages as error>		                                		
	                        <div class="alert alert-error"><i class="icon-warning-sign"></i> ${error}</div>                     		
	                    </#list>
	                	</div>
	                </div>
	                <div class="control-group">
	                	<label class="control-label" for="password"> </label>
	                	<span class="hint" data-desc="Please confirm your Password"></span>
			            <div class="controls">
			            <input id="confirmPassword" type="password" name="confirmPassword" placeholder="Confirm password"/>
	                    <@spring.bind "pendingUser.confirmPassword" /> 
			            <#list spring.status.errorMessages as error>		                                		
	                    	<div class="alert alert-error"> <i class="icon-warning-sign"></i> ${error}</div>                      		
	                    </#list>
	                  	</div>
                	</div>  
		            <div class="control-group">
		                <div class="controls">
		                 <button type="submit" class="btn btn-primary">Register</button>
		                 </div>
		            </div>
		        	<div class="control-group">
               			<div class="controls">  
               				<#if Session.applyRequest?has_content>
                        		<a href="/pgadmissions/login">&gt; Already Registered?</a>
		                    <#else>
		                  
		                    </#if>
               			 </div>
		            </div>	
		      	</form>
		      	
		      	<#if RequestParameters.activationCode?has_content>
		      	   <p>&nbsp;</p>
                <#else>
                    
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
