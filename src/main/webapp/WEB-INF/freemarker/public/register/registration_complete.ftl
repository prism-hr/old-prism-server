<#import "/spring.ftl" as spring />

<!DOCTYPE HTML>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>UCL Postgraduate Admissions</title>

<!-- Always force latest IE rendering engine (even in intranet) & Chrome Frame -->
<meta http-equiv="X-UA-Compatible" content="IE=9,chrome=1" />
<meta name="prism.version" content="<@spring.message 'prism.version'/>" >

<!-- Let Internet Explorer 8 and below recognise HTML5 tags. -->
<!--[if lt IE 9]>
<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
<![endif]-->
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/bootstrap.min.css' />"/>
<link type="text/css" rel="stylesheet" href="<@spring.url '/design/default/css/public/global_public.css' />" />
<link type="text/css" rel="stylesheet" href="<@spring.url '/design/default/css/public/register.css' />" />
<link rel="shortcut icon" type="text/css" href="<@spring.url '/design/default/images/favicon.ico' />"/>


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

		<div id="site-message" class="success">
			
			<div class="content">
				<h1>Almost there...</h1>
				<p>To activate your account please check your emails and click on the activation link.</p>
				<p class="buttons"><a id="resend" class="btn btn-large" href="/pgadmissions/register/resendConfirmation?activationCode=${pendingUser.activationCode}">Resend email</a></p>
			</div>
		</div>
		
	</div>
	  	
	<#include "/public/common/global_footer.ftl"/>
	
</div>

<!-- Scripts -->
<script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/register_applicant.js'/>"></script>

</body>
</html>