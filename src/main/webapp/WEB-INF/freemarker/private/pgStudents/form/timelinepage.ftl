<!DOCTYPE HTML>
<#import "/spring.ftl" as spring />
<#setting locale = "en_US">
<html>
  <head>
    
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta name="prism.version" content="<@spring.message 'prism.version'/>" >
    
    <title>UCL Postgraduate Admissions</title>
    
    <!-- Styles for Application Page -->    
    <link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/global_private.css' />"/>
    <link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/application.css' />"/>
    <link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/field_controls.css' />"/>
    <link rel="shortcut icon" type="text/css" href="<@spring.url '/design/default/images/favicon.ico' />"/>
    
    <script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
    <link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/bootstrap.min.css' />"/>
    <link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/font-awesome.min.css' />"/>
    <script type="text/javascript" src="<@spring.url '/design/default/js/bootstrap.min.js' />"></script>
    
    <!--[if lt IE 9]>
    <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->
    
    <!-- Styles for Application Page -->

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
      
       <#include "/private/common/global_header.ftl"/>
        
        <!-- Middle. -->
        <div id="middle">
            
        <#include "/private/common/parts/nav_with_user_info_toggle.ftl"/>
                 <@header/>
            <!-- Main content area. -->
            <article id="content" role="main">
            
			  <!-- "Tools" -->
			  <div id="tools">
				<ul class="left">
				  <li class="icon-print"><a target="_blank" title="Click to Download" href="<@spring.url '/print?applicationFormId=${applicationForm.applicationNumber}'/>">Download PDF</a></li>
				  <li class="icon-feedback"><a title="Send Feedback" href="mailto:prism@ucl.ac.uk?subject=Feedback" target="_blank">Send Feedback</a></li>
				</ul>
			  </div>
            
							<!-- content box -->
              	<div class="content-box">
					<div class="content-box-inner">
                        <input type="hidden" id="applicationId" name="applicationId" value="${applicationForm.applicationNumber}"/>
                        <div id="timeline">
                        
                        </div>      
                  	</div><!-- .content-box-inner -->
            
                </div><!-- .content-box -->
        
            </article>
    
        </div>
        
        <#include "/private/common/global_footer.ftl"/>
    
    </div> 
    
    <!-- Scripts -->
    <script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
    <script type="text/javascript" src="<@spring.url '/design/default/js/libraries.js'/>"></script>
    <script type="text/javascript" src="<@spring.url '/design/default/js/script.js'/>"></script>
    <script type="text/javascript" src="<@spring.url '/design/default/js/application/timeline.js'/>"></script>
    

  </body>
  
</html>

