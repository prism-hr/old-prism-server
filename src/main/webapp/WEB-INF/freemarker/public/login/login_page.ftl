<!DOCTYPE HTML>
<#import "/spring.ftl" as spring />
<html>
  <head>
  
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta name="prism.version" content="<@spring.message 'prism.version'/>" >
    
    <meta property="og:type" content="website">
    <meta property="og:site_name" content="UCL Postgraduate Admissions">
    <meta property="og:title" content="UCL Postgraduate Admissions">
    <meta property="og:description" content="UCL Postgraduate Admissions">
    <meta property="og:image" content="http://www.prism.ucl.ac.uk/pgadmissions/design/default/images/fbimg.jpg">
    <meta property="og:url" content="http://www.prism.ucl.ac.uk/">
    <meta property="fb:app_id" content="172294119620634">
    
    <title>UCL Postgraduate Admissions</title>
    
    <!-- Styles for Login List Page -->
    <link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/bootstrap.min.css' />"/>
    <link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/font-awesome.min.css' />"/>
    <link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/public/global_public.css' />"/>
    <link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/public/forgotPassword.css' />"/>
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
	
	<!-- Scripts -->
	<script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
	<script type="text/javascript" src="<@spring.url '/design/default/js/libraries.js'/>"></script>
	<script type="text/javascript" src="<@spring.url '/design/default/js/script.js'/>"></script>
	<script type="text/javascript" src="<@spring.url '/design/default/js/login.js'/>"></script>
	<script type="text/javascript" src="<@spring.url '/design/default/js/bootstrap.min.js' />"></script>
    <div id="wrapper">
    
    	<#include "/public/common/global_header.ftl"/>
      
        <!-- Middle. -->
        <div id="middle">
        
        <!-- Prospectus container -->
        <div id="pContainer">
			<#include "/private/prospectus/adverts.ftl"/>
        </div>
        <!-- ENd Prospectus container -->
        
        <!-- Login form. -->
        <section id="login-box">
        
          <form id="loginForm" method="post" action="/pgadmissions/j_spring_security_check" class="form-horizontal">
              <fieldset>
                <legend class="">Login</legend>
                <div class="control-group">
                <label class="control-label" for="inputEmail">Email <em>*</em></label>
                <span class="hint" data-desc="Please enter your e-mail address."></span>
                <div class="controls">
                  <#if Session.loginUserEmail?has_content>
                    <input type="text" id="username_or_email" name="username_or_email" value="${Session.loginUserEmail}" disabled="disabled" />
                    <input type="hidden" id="j_username" name="j_username" value="${Session.loginUserEmail}" />
                  <#else>
                    <input type="email" id="username_or_email" name="j_username" placeholder="Email address" autofocus />
                  </#if>
                </div>
              </div>
              <div class="control-group">
                <label class="control-label" for="password">Password <em>*</em></label>
                <span class="hint" data-desc="Please enter your password."></span>
                <div class="controls">
                 <input type="password" id="password" name="j_password" placeholder="Password"/>
                  <#if RequestParameters.login_error??>
              <div class="alert alert-error">
                <i class="icon-warning-sign"></i> Incorrect Username or Password!
              </div>
              </#if>
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
                  		<input type="hidden" id="selectedId" value=""/>
                        <a href="/pgadmissions/register">Not Registered?</a>
                        <br />
                        <br />
                  	</#if>
                	<a href="/pgadmissions/forgotPassword">Forgot Password</a>
                </div>
              </div>
        	</fieldset>
        </form>
        
        </section>
        <ul id="conections">
        		<li><a href="https://www.youtube.com/user/UCLPRISM/" target="_blank"><img src="<@spring.url '/design/default/images/youtube.png'/>" alt="" /><span>Online demonstrations</span></a></li>
                <li><a href="http://uclprism.freshdesk.com/support/home" target="_blank"><img src="<@spring.url '/design/default/images/freshdesk.png'/>" alt="" /><span>Help for UCL PRiSM users</span></a></li>
            </ul>

      </div>
      <div class="ajax-loader-preloader">
      <!--<img src="/design/default/images/ajax-loader-file.gif" style="display:none"></img>-->
      </div>
      <#include "/public/common/global_footer.ftl"/>
    
    </div>
    
    
  </body>
</html>
