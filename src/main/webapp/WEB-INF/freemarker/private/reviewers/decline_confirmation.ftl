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
<#include "/private/common/modal_window.ftl">
	
			<!-- Header. -->
			<div id="header">
				<p>LONDON'S GLOBAL UNIVERSITY</p>
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
                modalPrompt(message, userOk, userCancel);
                return;                  
            });
            
            function userOk() {
                displayDialogBoxAgainAndShowSpinningWheel();
                var url = window.location + "&confirmation=OK";
                window.location = url;
            }
            
            function userCancel() {
                displayDialogBoxAgainAndShowSpinningWheel();
                var url = window.location + "&confirmation=Cancel";
                window.location = url;
            }
            
            function displayDialogBoxAgainAndShowSpinningWheel() {
                $('#popup-ok-button').attr("disabled", "disabled");
                $('#popup-cancel-button').attr("disabled", "disabled");
                $('#dialog-overlay, #dialog-box').show();
                $('#dialog-box').append('<div class="ajax" />');
            }
            
		</script>
		
	</body>
	
</html>