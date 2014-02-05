<!DOCTYPE HTML>
<#import "/spring.ftl" as spring />
<#setting locale = "en_US">
<html>

  <head>
  
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta name="prism.version" content="<@spring.message 'prism.version'/>" >
    
    <title>UCL Postgraduate Admissions</title>

    <!-- Always force latest IE rendering engine (even in intranet) & Chrome Frame -->
    <meta http-equiv="X-UA-Compatible" content="IE=9,chrome=1" />
    
    <!-- Styles for Application List Page -->
    <link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/global_private.css' />"/>
    <link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/application.css' />"/>
    <link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/staff/state_transition.css' />"/>
    <link rel="shortcut icon" type="text/css" href="<@spring.url '/design/default/images/favicon.ico' />"/>
        <!-- Styles for Application List Page -->

    <!--[if lt IE 9]>
    <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->
  
    <script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
    <script type="text/javascript" src="<@spring.url '/design/default/js/libraries.js' />"></script>
    <script type="text/javascript" src="<@spring.url '/design/default/js/script.js'/>"></script>
    <script type="text/javascript" src="<@spring.url '/design/default/js/superAdmin/edit_opportunity_request.js' />"></script>
      
    <link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/bootstrap.min.css' />"/>
    <link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/font-awesome.min.css' />"/>
    <script type="text/javascript" src="<@spring.url '/design/default/js/bootstrap.min.js' />"></script>
    <script type="text/javascript" src="<@spring.url '/design/default/js/tinymce/tinymce.min.js' />"></script>
    <script type="text/javascript" src="<@spring.url '/design/default/js/tinymce/jquery.tinymce.min.js' />"></script>
      
  </head>
  <style type="text/css">
    #advertisingDuration {
      width: 80px !important;
    }
    #studyOptions {
      width: 170px !important;
    }
    span.count {
       display: none;
    }
  </style>
  <!--[if IE 9]>
  <body class="ie9">
  <![endif]-->
  <!--[if lt IE 9]>
  <body class="old-ie">
  <![endif]-->
  <!--[if (gte IE 9)|!(IE)]><!-->
  <body>
  <!--<![endif]-->
  
    <div id="rejectOpportunityRequestModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
      <div class="modal-header">
        <h3 id="myModalLabel">Reject opportunity request</h3>
      </div>
        <div id="rejectOpportunityRequestReasonDiv" class="modal-body">
          <p>Specify why you want to reject the opportunity request</p>
            <textarea cols="150" rows="6" class="input-xxlarge" id="rejectOpportunityRequestReasonText"></textarea>
          </p>
          
        </div>
        <div class="modal-footer">
          <input id="rejectOpportunityRequestUrl" type="hidden" value="${requestContext.requestUri}" />
          <button id="do-reject-opportunity-button" class="btn btn-danger" aria-hidden="true">Reject</button>
          <button class="btn" data-dismiss="modal" aria-hidden="true">Cancel</button>
        </div>
    </div>
  
    <!-- Wrapper Starts -->
    <div id="wrapper">

      <#include "/private/common/global_header.ftl"/>
      
       <!-- Middle Starts -->
      <div id="middle">
      
        <#include "/private/common/parts/nav_with_user_info_toggle.ftl"/>
        <@header activeTab="requests"/>
            <!-- Main content area. -->
            <article id="content" role="main">        
              
              <!-- content box -->              
              <div class="content-box">
                <div class="content-box-inner">
                
                  <section class="form-rows">
                    <h2 class="no-arrow">Opportunity Request</h2>
                    <div>
                      <form id="opportunityRequestEditForm" method="POST">
                        <input type="hidden" name="action" value="approve">
                        <div class="row-group">
                          <h3 class="no-arrow">Opportunity Details</h2>

                          <#include "/private/prospectus/opportunity_details_part.ftl"/>
                        </div>
                        
                      </form>

                        <div class="row-group">
                          <h3 class="no-arrow">Author Details</h2>
                          <div class="row">
                            <label class="plain-label">First Name</label>
                            <div class="field">
                              <input readonly disabled="disabled" class="full" type="text" name="firstName" id="firstName" value="${(opportunityRequest.author.firstName)!}"/>
                            </div>
                          </div>

                          <div class="row">
                            <label class="plain-label">Last Name</label>
                            <div class="field">
                              <input readonly disabled="disabled" class="full" type="text" name="lastName" id="lastName" value="${(opportunityRequest.author.lastName)!}"/>
                            </div>
                          </div>
                          
                          <div class="row">
                            <label class="plain-label">Email</label>
                            <div class="field">
                              <input readonly disabled="disabled" class="full" type="text" name="email" id="email" value="${(opportunityRequest.author.email)!}"/>
                            </div>
                          </div>
                          
                          
                        </div>
                        
                           
                        <div class="buttons">                       
                          <button id="approve-button" class="btn btn-primary">Approve</button>                    
                          <button id="reject-button" class="btn btn-danger">Reject</button>                    
                        </div>
                      
                    </div>
                  </section>
                
                </div><!-- .content-box-inner -->
              </div><!-- .content-box -->
              
          
            </article>
        
        
        
      </div>
      <!-- Middle Ends -->
      
      <#include "/private/common/global_footer.ftl"/>
      
    </div>
    <!-- Wrapper Ends -->
       
  </body>
</html>