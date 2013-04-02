<!DOCTYPE HTML>
<#import "/spring.ftl" as spring />
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<meta name="prism.version" content="<@spring.message 'prism.version'/>" >
		<title>UCL Postgraduate Admissions</title>
		
		<!-- Always force latest IE rendering engine (even in intranet) & Chrome Frame -->
		<meta http-equiv="X-UA-Compatible" content="IE=9,chrome=1" />
		
		<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/global_private.css' />"/>
		<link rel="shortcut icon" type="text/css" href="<@spring.url '/design/default/images/favicon.ico' />"/>
		
		<script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
		<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/bootstrap.min.css' />"/>
		<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/font-awesome.min.css' />"/>
		<script type="text/javascript" src="<@spring.url '/design/default/js/bootstrap.min.js' />"></script>
		
		<!--[if lt IE 9]>
		<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
		<![endif]-->
	</head>
	
	<body>
	
		<div id="wrapper">
		
			<#include "/private/common/global_header.ftl"/>
		  
		  	<!-- Middle. -->
		  	<div id="middle">
		  	   <#include "/private/common/parts/nav_with_user_info_toggle.ftl"/>
		  	    <@header/>
		    	<!-- Main content area. -->
		    	<article id="content" role="main">
		      
		      		<div class="content-box">
		      			<div class="content-box-inner">
		              
                  <div class="alert alert-info">
          			<i class="icon-info-sign"></i> 
                    Sorry, this program is no longer available.
                  </div>
                  
		        		</div><!-- .content-box-inner -->
		      		</div><!-- .content-box -->
		      
		    	</article>
		    
		  	</div>
		  
		  	<!-- Footer. -->
		  	<#include "/private/common/global_footer.ftl"/>
		
		</div>
		
		<!-- Scripts -->
		<script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/libraries.js' />"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/script.js' />"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/help.js' />"></script>
		
	</body>
</html>