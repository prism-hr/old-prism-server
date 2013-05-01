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
		
		<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/public/global_public.css' />"/>
		<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/public/forgotPassword.css' />"/>
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
		    <section id="login-box">
		    
		    	<form method="post" action= "/pgadmissions/forgotPassword/resetPassword" class="form-horizontal">
		    		<fieldset>
		                <legend>Recover your password</legend>
		      		    <div class="control-group">
		           		<label class="control-label" for="email">Email <em>*</em></label>
		           		<span class="hint" data-desc="Please enter your e-mail address"></span>
						<div class="controls">
		           			<input id="email" type="email" name="email" placeholder="Email Address"/>
		           			<#if errorMessageCode??>                    		
                    	<div class="alert alert-error">
			               <i class="icon-warning-sign"></i> <@spring.message errorMessageCode />
			            </div>                   		
                    </#if>
		           		</div>
		           	</div>

		        	<div class="control-group">
                		<div class="controls">
		        			<button type="submit" class="btn btn-primary">Get password</button>
			        	</div>
			        </div>
			        <div class="control-group">
		                <div class="controls">
				      	<a href="/pgadmissions/login">Back to Login</a>
				      	</div>
				    </div>
                    </fieldset>
		      	</form>
		      	
		    </section>
		
		  </div>
		  
			<#include "/public/common/global_footer.ftl"/>
		
		</div>
		
		<!-- Scripts -->
		<script type="text/javascript" src="<@spring.url '/design/default/js/bootstrap.min.js' />"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/libraries.js'/>"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/script.js'/>"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/forgot_password.js'/>"></script>
	</body>
</html>
