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
		
		<link type="text/css" rel="stylesheet" href="<@spring.url '/design/default/css/public/global_public.css' />" />
		<link type="text/css" rel="stylesheet" href="<@spring.url '/design/default/css/public/register.css' />" />
		<link rel="shortcut icon" type="text/css" href="<@spring.url '/design/default/images/favicon.ico' />"/>
		
	</head>
<body id="bg">
	
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
	  
         <div id="site-message" class="success" style="height:170px">
           <div class="content">         
            <h1>Declined</h1>
             <p>${message}</p>  
          </div>
        </div>
        
      </div>
	  	
<#include "/private/common/global_footer.ftl"/>
	
		</div>
		<!-- Scripts -->
		<script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/bootstrap.min.js' />"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/libraries.js' />"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/script.js' />"></script>
		
	</body>
	
</html>