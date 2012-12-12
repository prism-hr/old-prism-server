<#import "/spring.ftl" as spring />

<!DOCTYPE HTML>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<title>UCL Postgraduate Admissions</title>
		
		<!-- Always force latest IE rendering engine (even in intranet) & Chrome Frame -->
		<meta http-equiv="X-UA-Compatible" content="IE=9,chrome=1" />
		
		<!-- Let Internet Explorer 8 and below recognise HTML5 tags. -->
		<!--[if lt IE 9]>
		<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
		<![endif]-->
		
		<link rel="shortcut icon" type="text/css" href="<@spring.url '/design/default/images/favicon.ico' />"/>
		<link type="text/css" rel="stylesheet" href="<@spring.url '/design/default/css/public/global_public.css' />" />
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
	
			<!-- Header. -->
			<div id="header">
				<p>LONDON'S GLOBAL UNIVERSITY</p>
			</div>
	  
			<!-- Middle. -->
			<div id="middle">
	  
       	 		<div id="site-message" class="notice">
		          <div class="header">
		            	<div class="logo"><img src="<@spring.url '/design/default/images/ph_logo_app.png'/>" alt="" /></div>
		            	<div class="tagline">Your Gateway to<br />Research Opportunities</div>
		          </div>
		          <div class="error">
		            <p>The system was unable to process the activation request.</p>
		          </div>
       		</div>
        
     	</div>
	  	
		<#include "/public/common/global_footer.ftl"/>
	
		</div>
		<!-- Scripts -->
		<script type="text/javascript" src="http://code.jquery.com/jquery-1.7.1.min.js"></script>
	</body>
</html>