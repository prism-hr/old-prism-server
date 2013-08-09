<#import "/spring.ftl" as spring />

<!DOCTYPE HTML>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<meta name="prism.version" content="<@spring.message 'prism.version'/>" >
		<title>UCL Postgraduate Admissions</title>
		
		<!-- Always force latest IE rendering engine (even in intranet) & Chrome Frame -->
		<meta http-equiv="X-UA-Compatible" content="IE=9,chrome=1" />
		
		<!-- Let Internet Explorer 8 and below recognise HTML5 tags. -->
		<!--[if lt IE 9]>
		<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
		<![endif]-->
		
		<link rel="shortcut icon" type="text/css" href="<@spring.url '/design/default/images/favicon.ico' />"/>
		<link type="text/css" rel="stylesheet" href="<@spring.url '/design/default/css/public/global_public.css' />" />
		<link type="text/css" rel="stylesheet" href="<@spring.url '/design/default/css/public/enquiry.css' />" />
		
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
	     <div id="topBar">
            <h1><span class="logoico">Prism</span> <span id="version"><@spring.message 'prism.version'/></span> <strong>Research Student Recruitment</strong></h1>
            <div class="logotext">Prism</div>
        </div>
			<!-- Header. -->
			<div id="header">
				
			</div>
	  
			<!-- Middle. -->
			<div id="middle">
	  
			    <header>
					<!-- App logo and tagline. -->
			      	<div class="logo"><img src="images/ph_logo_login.png" alt="" /></div>
              <div class="tagline">A Spectrum of Postgraduate<br />Research Opportunities</div>
			    </header>
	    
			    <!-- Enquiry form. -->
			    <form action="" method="post">
			    	<section id="form-box">
			        	<label for="field_programme"><strong>I would like to submit an enquiry related to</strong></label>
			        
			        	<div class="programme row">
			        		<select id="field_programme">
			          			<option>Programme Name</option>
			          		</select>
			          		<button type="button">Browse Programmes</button>
			        	</div>
			        
			        	<div class="row">
			        		<label for="field_message">Your Message</label>
			          		<div class="field">
			            		<textarea id="field_message" name="message" placeholder="Enter Text"></textarea>
			          		</div>
			        	</div>
			        	
			      	</section>
			  
			      	<!-- Registration button. -->
			      	<button id="big-button" type="submit">Submit</button>
			      	
		    	</form>
		    	
	  		</div>
	  
			<#include "/public/common/global_footer.ftl"/>
	
		</div>
	
		<!-- Scripts -->
		<script type="text/javascript" src="http://code.jquery.com/jquery-1.7.1.min.js"></script>
		
	</body>
</html>