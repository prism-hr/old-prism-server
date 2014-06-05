<!DOCTYPE HTML>
<#import "/spring.ftl" as spring />
<html>

	<head>
	
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<meta name="prism.version" content="<@spring.message 'prism.version'/>" >
		<title>UCL Postgraduate Admissions</title>

		<!-- Always force latest IE rendering engine (even in intranet) & Chrome Frame -->
		<meta http-equiv="X-UA-Compatible" content="IE=7,chrome=1" />
		
		<!-- Styles for Application List Page -->
		<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/global_private.css' />"/>
		<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/application.css' />"/>
		<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/staff/state_transition.css' />"/>
		<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/comingsoon.css' />"/>
		<link rel="shortcut icon" type="text/css" href="<@spring.url '/design/default/images/favicon.ico' />"/>
		
		<script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
		<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/bootstrap.min.css' />"/>
		<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/font-awesome.min.css' />"/>
		<script type="text/javascript" src="<@spring.url '/design/default/js/bootstrap.min.js' />"></script>
        
				<!-- Styles for Application List Page -->

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
	
		<!-- Wrapper Starts -->
		<div id="wrapper">

			<#include "/private/common/global_header.ftl"/>
			
			 <!-- Middle Starts -->
			<div id="middle">
			
				<#include "/private/common/parts/nav_with_user_info_toggle.ftl"/>
				<@header activeTab="help"/>
					<!-- Main content area. -->
					<article id="content" role="main">		    
						
						<!-- content box -->				      
						<div class="content-box">
							<div id="cs-content-box-inner" class="content-box-inner">
							
							<div id="cs-message" class="notice">
						    	<div class="header">
						            <div class="logo"><img src="<@spring.url '/design/default/images/ph_logo_app.png'/>" alt="" /></div>
						            <div class="tagline">A Spectrum of Postgraduate<br />Research Opportunities</div>
						        </div>
						        <div class="content">
						            <h1>Coming soon...</h1>
						            <p>We are working on this page. Check back later.</p>
						            <p class="buttons"><a class="button blue" href="/pgadmissions/">Home</a></p>
						        </div>
							</div>

							</div><!-- .content-box-inner -->
						</div><!-- .content-box -->
							
					</article>
				
				</div>
				<!-- Middle Ends -->
			
			<#include "/private/common/global_footer.ftl"/>
			
		</div>
		<!-- Wrapper Ends -->
		   
		<script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/libraries.js' />"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/script.js' />"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/my-account.js' />"></script>
	</body>
</html>