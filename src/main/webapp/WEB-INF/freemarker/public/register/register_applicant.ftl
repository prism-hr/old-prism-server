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
		
		<link type="text/css" rel="stylesheet" href="<@spring.url '/design/default/css/global_public.css' />" />
		<link type="text/css" rel="stylesheet" href="<@spring.url '/design/default/css/register.css' />" />
		
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
		    	<form action="" method="post">
		      		<section id="form-box">
		        		<h1>New User? Please register</h1>
		        
		        		<div class="row">
		        			<label for="field_first_name">First name</label>
		          			<div class="field">
		            			<input id="field_first_name" type="text" name="firstname" />
		          			</div>
		        		</div>
		        
		        		<div class="row">
		        			<label for="field_surname">Surname</label>
		          			<div class="field">
		            			<input id="field_surname" type="text" name="surname" />
		          			</div>
		        		</div>
		        
		        		<div class="row">
		        			<label for="field_email">Email Address</label>
		          			<div class="field">
		            			<input id="field_email" type="text" name="email" />
		          			</div>
		        		</div>
		        
		        		<div class="row">
		        			<label for="field_phone">Phone Number</label>
		          			<div class="field">
		            			<input id="field_phone" type="text" name="phone" />
		          			</div>
		        		</div>
		        
		        		<div class="row">
		        			<label for="field_home_1">Home</label>
		          			<div class="field">
		            			<input id="field_home_1" type="text" name="home_1" /><br>
		            			<input id="field_home_2" type="text" name="home_2" /><br>
		            			<input id="field_home_3" type="text" name="home_3" />
		          			</div>
		        		</div>
		        
		        		<div class="row">
		        			<label for="field_postcode">Post / ZIP code</label>
		          			<div class="field">
		            			<input id="field_postcode" type="text" name="postcode" />
		          			</div>
		        		</div>
		        
		        		<div class="row">
		        			<label for="field_country">Country of Birth</label>
		          			<div class="field">
		            			<select id="field_country">
		              				<option>Azerbaijan</option>
		            			</select>
		          			</div>
		        		</div>
		        
		        		<div class="row">
		        			<label for="field_password" id="field_country">Password</label>
		          			<div class="field">
		            			<input id="field_password" type="password" name="password" />
		          			</div>
		        		</div>
		        
		        		<div class="row">
		        			<label for="field_password_confirm">Confirm Password</label>
		          			<div class="field">
		            			<input id="field_password_confirm" type="password" name="password_confirm" />
		          			</div>
		        		</div>
		        
		      		</section>
		  
		      		<!-- Registration button. -->
		      		<button id="big-button" type="submit">Submit</button>
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
		
	</body>
</html>