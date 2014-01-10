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
	
	<!-- Scripts -->
		<script type="text/javascript" src="<@spring.url '/design/default/js/bootstrap.min.js' />"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/libraries.js'/>"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/script.js'/>"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/forgot_password.js'/>"></script>
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
		  	
            <!-- Prospectus container -->
      
        <div id="pContainer">
           <#include "/private/prospectus/adverts.ftl"/>
        </div>
        <!-- ENd Prospectus container -->
		
		    <!-- Login form. -->
		    <section id="login-box">
		    
		    	<form method="post" action= "/pgadmissions/forgotPassword/resetPassword" class="form-horizontal">
		    		<fieldset>
		                <legend>Recover your password</legend>
		      		    <div class="control-group">
		           		<label class="control-label" for="email">Email <em>*</em></label>
		           		<span class="hint" data-desc="Please enter your e-mail address"></span>
						<div class="controls">
		           			<input id="email" type="email" name="email" placeholder="Email Address" autofocus/>
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
            <ul id="conections">
                    <li><a href="https://www.youtube.com/user/UCLPRISM/" target="_blank"><img src="<@spring.url '/design/default/images/youtube.png'/>" alt="" /><span>Online demonstrations</span></a></li>
                	<li><a href="http://uclprism.freshdesk.com/support/home" target="_blank"><img src="<@spring.url '/design/default/images/freshdesk.png'/>" alt="" /><span>Help for UCL PRiSM users</span></a></li>
            </ul>

		  </div>
		  
			<#include "/public/common/global_footer.ftl"/>
		
		</div>
		
		
	</body>
</html>
