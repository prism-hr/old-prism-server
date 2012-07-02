<#import "/spring.ftl" as spring />

<!DOCTYPE HTML>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<title>UCL Postgraduate Admissions</title>
		
		<!-- Always force latest IE rendering engine (even in intranet) & Chrome Frame -->
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
		
		<!-- Let Internet Explorer 8 and below recognise HTML5 tags. -->
		<!--[if lt IE 9]>
		<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
		<![endif]-->
		
		<link type="text/css" rel="stylesheet" href="<@spring.url '/design/default/css/public/global_public.css' />" />
		<link type="text/css" rel="stylesheet" href="<@spring.url '/design/default/css/public/register.css' />" />
		
	</head>

	<body id="bg">
	
		<div id="wrapper">
	
			<!-- Header. -->
			<div id="header">
				<p>LONDON'S GLOBAL UNIVERSITY</p>
			</div>
	  
			<!-- Middle. -->
			<div id="middle">
	  
        <div id="site-message" class="success">
          <div class="header">
            <div class="logo"><img src="<@spring.url '/design/default/images/ph_logo_app.png'/>" alt="" /></div>
            <div class="tagline">A Spectrum of Postgraduate<br />Research Opportunities</div>
          </div>
          <div class="content">
            <h1>Almost there...</h1>
            <p>To activate your account please check your emails and click on the activation link.</p>
            <p class="buttons"><a class="button blue" href="/pgadmissions/register/resendConfirmation?activationCode=${pendingUser.activationCode}">Resend email</a></p>
          </div>
        </div>
        
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