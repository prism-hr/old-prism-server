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
		<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/bootstrap.min.css' />"/>
		<link type="text/css" rel="stylesheet" href="<@spring.url '/design/default/css/public/register.css' />" />
		<link rel="shortcut icon" type="text/css" href="<@spring.url '/design/default/images/favicon.ico' />"/>
		
	</head>
    <body id="bg">
	
	
		<div id="wrapper">
<#include "/private/common/modal_window.ftl">
	
			<div id="topBar">
                <h1><span class="logoico">Prism</span> <span id="version"><@spring.message 'prism.version'/></span> <strong>Research Student Recruitment</strong></h1>
                <div class="logotext">Prism</div>
            </div>
			<div id="header">
				
			</div>
	  
			<!-- Middle. -->
			<div id="middle">
	       </div>
	  	
<#include "/private/common/global_footer.ftl"/>
	
		</div>
		<!-- Scripts -->
		<script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/libraries.js' />"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/script.js' />"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/bootstrap.min.js' />"></script>
		<script>
		      
            $(document).ready(function() {
                $('#popup-ok-button').html("${okButton}");
                $('#popup-cancel-button').html("${cancelButton}");
                $('#dialog-box a.default-button').css('width', '110px');
            
                var message = '${message}';
                modalPrompt(message, userOk, userCancel, new function() {});
                return;                  
            });
            
            function userOk() {
                var url = window.location + "&confirmation=OK";
                window.location = url;
            }
            
            function userCancel() {
                var url = window.location + "&confirmation=Cancel";
                window.location = url;
            }
            
		</script>
		
	</body>
	
</html>