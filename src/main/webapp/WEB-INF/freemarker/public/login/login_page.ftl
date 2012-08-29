<!DOCTYPE HTML>
<#import "/spring.ftl" as spring />
<html>
  <head>
  
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title>UCL Postgraduate Admissions</title>
    
    <!-- Styles for Login List Page -->
    <link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/public/global_public.css' />"/>
    <link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/public/forgotPassword.css' />"/>
    
    <!-- Styles for login List Page -->
    
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
    
      <#include "/public/common/global_header.ftl"/>
      
        <!-- Middle. -->
        <div id="middle">
      
				<!-- App logo and tagline -->
        <header>
          <div class="logo"><img src="<@spring.url '/design/default/images/ph_logo_login.png'/>" alt="" /></div>
          <div class="tagline">A Spectrum of Postgraduate Research Opportunities</div>
        </header>
        
        <!-- Blurb. -->
        <aside id="blurb">
          <p>Welcome to <b>UCL Prism</b>, the gateway<br />
					to postgraduate research opportunities<br />
					in UCL Engineering.</p>
        </aside>
    
        <!-- Login form. -->
        <section id="login-box">
        
          <form id="loginForm" method="post" action="/pgadmissions/j_spring_security_check">
              <p>&gt; Login</p>
              
              <input type="text" id="username_or_email" name="j_username" placeholder="Email address" value="Email address" />
              <input type="password" id="password" name="j_password" placeholder="Password" value="Password" />
              <#if RequestParameters.login_error??>
              <span class="invalid">Invalid username/password combination.</span>
              </#if>
              <button name="commit" type="submit" value="Sign In" class="blue">Go</button>
            </form>
            
            <a href="/pgadmissions/register">&gt; Register Today...</a>
            <a href="/pgadmissions/forgotPassword">&gt; Forgot Password</a>
            
        </section>
    
      </div>
      
      <#include "/public/common/global_footer.ftl"/>
    
    </div>
    
    <!-- Scripts -->
    <script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
    <script type="text/javascript" src="<@spring.url '/design/default/js/libraries.js'/>"></script>
    <script type="text/javascript" src="<@spring.url '/design/default/js/script.js'/>"></script>
    <script type="text/javascript" src="<@spring.url '/design/default/js/login.js'/>"></script>
  </body>
</html>
