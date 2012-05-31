
<!DOCTYPE HTML>
<#import "/spring.ftl" as spring />
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<title>UCL Postgraduate Admissions</title>
		
		<!-- Always force latest IE rendering engine (even in intranet) & Chrome Frame -->
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
		
		<link type="text/css" rel="stylesheet" href="<@spring.url '/design/default/css/public/global_public.css' />"/>
		<link type="text/css" rel="stylesheet" href="<@spring.url '/design/default/css/actions.css' />"/>

		<!--[if lt IE 9]>
		<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
		<![endif]-->
	</head>

	<body>
		
		<div id="wrapper">
		
		  <!-- Header. -->
		  <div id="header">
		    <p>Postgraduate Research Admissions Tool</p>
		  </div>
		  
		  <!-- Middle. -->
		  <div id="middle">
		  
		    <header>
		
		      <!-- Main tabbed menu -->
		      <nav>
		        <ul>
		          <li><a href="#">My account</a></li>    
		          <li class="current"><a href="<@spring.url '/applications'/>">My applications</a></li>    
		          <li><a href="#">Help</a></li>    
		        </ul>
		        
		        <div class="user">
		           ${model.user.firstName} ${model.user.lastName} 
		          <a class="button blue user-logout" href="<@spring.url '/j_spring_security_logout'/>">Logout</a>
		        </div>
		      </nav>
		      
		    </header>
		    
		    
		    <!-- Main content area. -->
		    <article id="content" role="main">
		          
		      <!-- content box -->
		      <div class="content-box">
		        <div class="content-box-inner">

							<div id="site-message" class="error">
								<div class="header">
									<div class="logo"><img src="images/ph_logo_login.png" alt="" /></div>
									<div class="tagline">Your Gateway to<br />Research Opportunities</div>
								</div>
								<div class="content">
									<h1>Whoops</h1>
									<p>Sorry, an error occurred while processing your request.</p>
								</div>
							</div>

		        </div><!-- .content-box-inner -->
		      </div><!-- .content-box -->
		      
		    </article>
		    
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
		
	</body>
</html>
