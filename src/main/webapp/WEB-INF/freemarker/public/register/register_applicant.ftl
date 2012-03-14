<#import "/spring.ftl" as spring />

<!DOCTYPE HTML>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<title>Shell template</title>
		
		<!-- Always force latest IE rendering engine (even in intranet) & Chrome Frame -->
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
		
		<!-- Let Internet Explorer 8 and below recognise HTML5 tags. -->
		<!--[if lt IE 9]>
		<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
		<![endif]-->
		
		<link type="text/css" rel="stylesheet" href="<@spring.url '/design/default/css/public/global_public.css' />" />
		<link type="text/css" rel="stylesheet" href="<@spring.url '/design/default/css/public/register.css' />" />
		
	</head>

	<body id="bg">
	
		<div id="wrapper">
	
			<!-- Header. -->
			<div id="header">
				<p>Postgraduate Research Admissions Tool</p>
			</div>
	  
			<!-- Middle. -->
			<div id="middle">
	  
			    <header>
					<!-- App logo and tagline. -->
			      	<div class="logo"><img src="images/ph_logo_login.png" alt="" /></div>
			      	<div class="tagline">Your Gateway to<br />Research Opportunities</div>
			    </header>
	    
		    	<!-- New user form. -->
		    	<form>
		      		<section id="form-box">
		        		<h1>New User? Please register</h1>
		        
		        		<div class="row">
		        			<label for="field_first_name">First name</label>
		          			<div class="field">
		            			<input id="rec_firstname" type="text" name="firstname" value="${model.record.firstname!}" />
		          			<#if model.hasError('firstname')>                           
                            		<span class="invalid"><@spring.message  model.result.getFieldError('firstname').code /></span>                           
                            </#if>
		          			
		          			</div>
		        		</div>
		        
		        		<div class="row">
		        			<label for="field_surname">Last name</label>
		          			<div class="field">
		            			<input id="rec_lastname" type="text" name="surname" value="${model.record.lastname!}" />
		            		<#if model.hasError('lastname')>                           
                            		<span class="invalid"><@spring.message  model.result.getFieldError('lastname').code /></span>                           
                            </#if>
		          			</div>
		        		</div>
		        
		        		<div class="row">
		        			<label for="field_email">Email Address</label>
		          			<div class="field">
		            			<input id="rec_email" type="text" name="email" value="${model.record.email!}" />
		            		<#if model.hasError('email')>                           
                            		<span class="invalid"><@spring.message  model.result.getFieldError('email').code /></span>                           
                            </#if>
		          			</div>
		        		</div>
		        
		        		<div class="row">
		        			<label for="field_password">Password</label>
		          			<div class="field">
		            			<input id="rec_password" type="password" name="password" />
		            		<#if model.hasError('password')>                           
                            		<span class="invalid"><@spring.message  model.result.getFieldError('password').code /></span>                           
                            </#if>
		          			</div>
		        		</div>
		        
		        		<div class="row">
		        			<label for="field_password_confirm">Confirm Password</label>
		          			<div class="field">
		            			<input id="rec_password_confirm" type="password" name="password_confirm" />
		            		<#if model.hasError('confirmPassword')>                           
                            		<span class="invalid"><@spring.message  model.result.getFieldError('confirmPassword').code /></span>                           
                            </#if>
		          			</div>
		        		</div>
		        
		      		</section>
		  
		      		<!-- Registration button. -->
		      		<button id="registerSubmit" type="button" value ="Submit"> Submit </button>
		    	</form>
	  		</div>
	  
		  	<!-- Footer. -->
		  	<div id="footer">
		    	<ul>
		      		<li><a href="#">Privacy</a></li>
		      		<li><a href="#">Terms &amp; conditions</a></li>
		      		<li><a href="#">Contact us</a></li>
		      		<li><a href="#">Glossary</a></li>
		    	</ul>
		  	</div>
	
		</div>
		<!-- Scripts -->
		<script type="text/javascript" src="http://code.jquery.com/jquery-1.7.1.min.js"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/register.js'/>"></script>   
	</body>
</html>