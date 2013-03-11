<!DOCTYPE HTML>
<#import "/spring.ftl" as spring />
<html>
  <head>
  
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta name="prism.version" content="<@spring.message 'prism.version'/>" >
    
    <title>UCL Postgraduate Admissions</title>
    
    <!-- Styles for Login List Page -->
    <link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/bootstrap.min.css' />"/>
    <link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/public/global_public.css' />"/>
    <link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/public/forgotPassword.css' />"/>
    <link rel="shortcut icon" type="text/css" href="<@spring.url '/design/default/images/favicon.ico' />"/>
    <link rel="shortcut icon" type="text/css" href="<@spring.url '/design/default/images/favicon.ico' />"/>
    
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
        
          <form id="loginForm" method="post" action="/pgadmissions/j_spring_security_check" class="form-horizontal">
              <div id="legend">
                <legend class="">Login</legend>
              </div>
              <#if RequestParameters.login_error??>
              <div class="alert alert-error">
                Incorrect Username or Password!
              </div>
              </#if>
              <div class="control-group">
                <label class="control-label" for="inputEmail">Email</label>
                <div class="controls">
                  <#if Session.loginUserEmail?has_content>
                    <input type="text" id="username_or_email" name="username_or_email" value="${Session.loginUserEmail}" disabled="disabled" />
                    <input type="hidden" id="j_username" name="j_username" value="${Session.loginUserEmail}" />
                  <#else>
                    <input type="text" id="username_or_email" name="j_username" placeholder="Email address" />
                  </#if>
                </div>
              </div>
              <div class="control-group">
                <label class="control-label" for="password">Password</label>
                <div class="controls">
                 <input type="password" id="password" name="j_password" placeholder="Password"/>
                </div>
              </div>
              <div class="control-group">
                <div class="controls">
                 <button type="submit" class="btn btn-primary">Sign in</button>
                 </div>
              </div>
              <div class="control-group">
                <div class="controls">
              <#if Session.applyRequest?has_content>
                    <a href="/pgadmissions/register">Not Registered?</a>
                </#if>
                    
                <a href="/pgadmissions/forgotPassword">Forgot Password</a>
                </div>
              </div>
        </form>
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
