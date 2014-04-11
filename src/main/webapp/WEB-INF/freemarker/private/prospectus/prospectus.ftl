<!DOCTYPE HTML>
<#import "/spring.ftl" as spring />
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta name="prism.version" content="<@spring.message 'prism.version'/>">
<title>UCL Postgraduate Admissions</title>
<!-- Always force latest IE rendering engine (even in intranet) & Chrome Frame -->
<meta http-equiv="X-UA-Compatible" content="IE=9,chrome=1" />
<!-- Styles for Application List Page -->
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/global_private.css' />" />
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/application.css' />" />
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/staff/prospectus.css' />" />
<link rel="shortcut icon" type="text/css" href="<@spring.url '/design/default/images/favicon.ico' />" />
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/bootstrap.min.css' />" />
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/font-awesome.min.css' />" />
<!-- Styles for Application List Page -->
<script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/libraries.js' />"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/tinymce/tinymce.min.js' />"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/tinymce/jquery.tinymce.min.js' />"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/bootstrap.min.js' />"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/prospectus/prospectus.js' />"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/script.js' />"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/underscore-min.js' />"></script>
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
     <!-- Modal Start-->
  <div id="resourcesModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-header">
      <h3 id="myModalLabel">Share your advert</h3>
    </div>
    <div class="modal-body">
      <p><strong>The link to apply.</strong> Use this in emails and on websites.</p>
      <input type="text" readonly="" aria-haspopup="true" role="textbox" class="input-xxlarge" id="modalLinkToApply">
      <p><strong>The button to apply.</strong> Use this in on websites.</p>
      <textarea readonly="" cols="150" rows="6" class="input-xxlarge" id="modalButtonToApply"></textarea>
      <p><strong>Or share this in social networks</strong> 
       <a href="#" target="_blank" id="sharethis" title="View more services" style="margin-left: 6px;"><img src="//s7.addthis.com/static/btn/v2/lg-share-en.gif" alt="Share"/></a>
      </p>
      
    </div>
    <div class="modal-footer">
      <button class="btn" data-dismiss="modal" aria-hidden="true">Close</button>
    </div>
  </div>
  <!-- Modal End-->
    <!-- Wrapper Starts -->

    <div id="wrapper">
        <#include "/private/common/global_header.ftl"/>
        <!-- Middle Starts -->
        <div id="middle">
            <#include "/private/common/parts/nav_with_user_info_toggle.ftl"/> <@header activeTab="prospectus"/>
            <!-- Main content area. -->
            <article id="content" role="main">
                <!-- content box -->
                <div class="content-box">
                    <div class="content-box-inner">
                      <div id="configBox" class="tabbox">
                        <ul class="tabs">
                        
                          <li><a href="#programmeConfiguration">Manage Programmes</a></li>
                          
                          <#assign canManageProjects = permissionsService.canManageProjects()>
                          <#if canManageProjects>
                            <li><a href="#projectConfiguration">Manage Projects</a></li>
                          </#if>
                          
                          <#if canManageProjects>
                            <li><a href="#researchOpportunitiesFeedSection">Research Opportunities Feed</a></li>
                          </#if>
                          
                          <#if canManageProjects>
                            <li><a href="#irisSection">Link to UCL IRIS</a></li>
                          </#if>
                          
                        </ul>
                    
                        <div id="programmeConfiguration" class="tab-page">
                          <#include "/private/prospectus/program_configuration.ftl"/>
                        </div>
                        
                        <#if canManageProjects>
                          <div id="projectConfiguration" class="tab-page">
                            <#include "/private/prospectus/project_configuration.ftl"/>
                          </div>
                        </#if>
                        
                        <#if canManageProjects>
                          <div id="researchOpportunitiesFeedSection" class="tab-page">
                            <#include "/private/prospectus/research_opportunities_feed_configuration.ftl"/>
                          </div>
                        </#if>
                        
                        <#if canManageProjects>
                          <div id="irisSection" class="tab-page">
                            <#include "/private/prospectus/iris_configuration.ftl"/>
                          </div>
                        </#if>
                      </div>
                        <!-- .content-box-inner -->
                    </div>
                    <!-- .content-box -->
                </div>
            </article>
        </div>
    <!-- Middle Ends -->
    <#include "/private/common/global_footer.ftl"/>
    </div>
    <!-- Wrapper Ends -->

</body>
</html>