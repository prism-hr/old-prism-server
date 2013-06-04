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
<script type="text/javascript" src="<@spring.url '/design/default/js/script.js' />"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/superAdmin/badge.js'/>"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/prospectus/prospectus.js' />"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/bootstrap.min.js' />"></script>
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
                          <li><a href="#projectConfiguration">Manage Projects</a></li>
                          <li><a href="#irisSection">Link to UCL IRIS</a></li>
                        </ul>
                    
                        <div id="programmeConfiguration" class="tab-page">
                          <#include "/private/prospectus/program_configuration.ftl"/>
                        </div>
                        
                        <div id="projectConfiguration" class="tab-page">
                          <#include "/private/prospectus/project_configuration.ftl"/>
                        </div>

                        <div id="irisSection" class="tab-page">
                            <section class="form-rows">
                                <h2>Link to UCL IRIS</h2>
                                <div>
                                    <form>
                                        <div class="alert alert-info" id="iris-account-not-linked-message" style="display:none">
                                            <i class="icon-info-sign"></i> Link your UCL Prism profile to UCL IRIS. This will display your Research Opportunities Feed on your UCL IRIS profile.
                                        </div>
    									<div class="alert alert-success" id="iris-account-linked-message" style="display:none">
                                            <i class="icon-ok-sign"></i> Your account is linked to UCL UPI: <span></span>
                                        </div>
                                        <div class="row-group">
                                            <div class="row">
                                                <label for="upi" class="plain-label">UCL Staff Indentifier (UPI)</label> 
                                                <span class="hint" data-desc="<@spring.message 'prospectus.iris.upi'/>"></span>
                                                <div class="field">
                                                    <input id="upi" name="upi" class="input-small" type="text" />
                                                </div>
                                                
                                                <div class="row">
                                                    <div class="field">
                                                        <!--
                                                        <div class="alert alert-error">
                                                            <i class="icon-warning-sign"></i>
                                                        </div>
                                                        -->
                                                    </div>
                                                </div>
                                                
                                            </div>
                                        </div>
                                        <div class="buttons">
                                            <button class="btn btn-primary" type="button" id="save-upi-go">Submit</button>
                                        </div>
                                    </form>
                                </div>
                            </section>

                        </div>
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


    <div id="iris-profile-modal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">X</button>
            <h3 id="myModalLabel">Confirm Identity in IRIS</h3>
        </div>
        <div class="modal-body" id="iris-profile-modal-body">
            <iframe id="iris-profile-modal-iframe"> </iframe>
        </div>
        <div class="modal-footer">
            <button class="btn" data-dismiss="modal" aria-hidden="true">I made a mistake</button>
            <button class="btn btn-primary" id="iris-profile-modal-confirm-btn">I confirm this is me</button>
        </div>
    </div>




</body>
</html>