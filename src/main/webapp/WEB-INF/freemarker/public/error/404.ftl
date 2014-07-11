
<!DOCTYPE HTML>
<#import "/spring.ftl" as spring />
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<meta name="prism.version" content="<@spring.message 'prism.version'/>" >
		
		<title>UCL Postgraduate Admissions</title>
		
		<!-- Always force latest IE rendering engine (even in intranet) & Chrome Frame -->
		<meta http-equiv="X-UA-Compatible" content="IE=9,chrome=1" />
		
		<link type="text/css" rel="stylesheet" href="<@spring.url '/design/default/css/public/global_public.css' />"/>
		<link type="text/css" rel="stylesheet" href="<@spring.url '/design/default/css/actions.css' />"/>
		<link rel="shortcut icon" type="text/css" href="<@spring.url '/design/default/images/favicon.ico' />"/>

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
		  <div id="topBar">
                <h1><span class="logoico">Prism</span> <span id="version"><@spring.message 'prism.version'/></span> <strong>Research Student Recruitment</strong></h1>
                <div class="logotext">Prism</div>
            </div>
		  <!-- Header. -->
		  <div id="header">
		  </div>
		  
		  <!-- Middle. -->
		  <div id="middle">
		  
		    <!-- Main content area. -->
		    <article id="content" role="main">
		          
		      <!-- content box -->
		      <div class="content-box">
		        <div class="content-box-inner">

							<div id="site-message" class="error" style="height:170px">
								<div class="content">
									<h1>Whoops</h1>
									<p>Sorry, the URL you requested does not exist.</p>
								</div>
							</div>

		        </div><!-- .content-box-inner -->
		      </div><!-- .content-box -->
		      
		    </article>
		    
		  </div>
		  
<#include "/private/common/global_footer.ftl"/>
		
		</div>
		
	</body>
</html>
