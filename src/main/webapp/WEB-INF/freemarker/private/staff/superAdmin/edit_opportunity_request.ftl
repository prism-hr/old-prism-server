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
      
        <#include "/private/common/parts/nav_with_user_info_toggle.ftl"/>
        <@header/>
            <!-- Main content area. -->
            <article id="content" role="main">        
              
              <!-- content box -->              
              <div class="content-box">
                <div class="content-box-inner">
                
                  <section class="form-rows">
                    <h2 class="no-arrow">Opportunity Request</h2>
                    <div>
                      <form id="opportunityRequestEditForm" method="POST">
                        <input type="hidden" name="editAction" id="editAction" value=""/>
                        
                        <div class="row-group">
                          <h3 class="no-arrow">Opportunity Details</h2>
                          <div class="row">
                          
                            <label class="plain-label" for="institutionCountry">Institution Country<em>*</em></label>
                            <span class="hint" data-desc="<@spring.message 'opportunityRequest.institutionCountry'/>"></span>
                            <div class="field">
                              <select class="full selectpicker" data-live-search="true" data-size="6" id="institutionCountry" name="institutionCountry">
                                <option value="">Select...</option>
                                <#list countries as country>
                                  <option value="${encrypter.encrypt(country.id)}"
                                    <#if opportunityRequest.institutionCountry?? && opportunityRequest.institutionCountry.id == country.id> selected="selected"</#if>
                                    >${country.name?html}
                                  </option>
                                </#list>
                              </select>
                              <@spring.bind "opportunityRequest.institutionCountry" /> 
                              <#list spring.status.errorMessages as error> 
                                <div class="alert alert-error"><i class="icon-warning-sign"></i>${error}</div>
                              </#list>
                            </div>
                          </div>

                          <div class="row">
                            <label class="plain-label" for="institution">Institution Name<em>*</em></label>
                            <span class="hint" data-desc="<@spring.message 'opportunityRequest.programDescription'/>"></span>
                            <div class="field">
                              <#assign anyCountrySelected = opportunityRequest.institutionCountry?? && opportunityRequest.institutionCountry != "">
                              <select class="full selectpicker"
                                <#if !anyCountrySelected>disabled="disabled"</#if>
                                data-live-search="true" data-size="6"  id="institution" name="institutionCode">
                                <option value="">Select...</option>
                                <#if opportunityRequest.institutionCountry??>
                                  <#list institutions as inst>
                                    <option value="${inst.code}" <#if opportunityRequest.institutionCode?? && opportunityRequest.institutionCode == inst.code> selected="selected"</#if>>
                                      ${inst.name?html}
                                    </option>
                                  </#list>
                                  <option value="OTHER" <#if opportunityRequest.institutionCode?? && opportunityRequest.institutionCode == "OTHER">selected="selected"</#if>>Other
                                  </option>
                                </#if>
                              </select>
                              <@spring.bind "opportunityRequest.institutionCode" /> 
                              <#list spring.status.errorMessages as error> 
                                <div class="alert alert-error"><i class="icon-warning-sign"></i>${error}</div>
                              </#list>
                            </div>
                          </div>

                          <div class="row">
                            <label class="plain-label" for="otherInstitution">Please Specify<em>*</em></label>
                            <span class="hint" data-desc="<@spring.message 'opportunityRequest.programDescription'/>"></span>
                            <div class="field">
                              <#assign otherInstitutionSelected = opportunityRequest.institutionCode?? && opportunityRequest.institutionCode == "OTHER">
                              <input
                                <#if !otherInstitutionSelected>readonly disabled="disabled"</#if>
                                id="otherInstitution" name="otherInstitution" class="full" type="text" value="${(opportunityRequest.otherInstitution?html)!}" />
                              <@spring.bind "opportunityRequest.otherInstitution" /> 
                              <#list spring.status.errorMessages as error> 
                                <div class="alert alert-error"><i class="icon-warning-sign"></i>${error}</div>
                              </#list>
                            </div>
                          </div>
                          
                          <div class="row">
                            <label class="plain-label" for="programTitle">Program Title<em>*</em></label>
                            <span class="hint" data-desc="<@spring.message 'opportunityRequest.otherInstitution'/>"></span>
                            <div class="field">
                              <input class="full" type="text" name="programTitle" id="programTitle" value="${(opportunityRequest.programTitle)!}"/>
                              <@spring.bind "opportunityRequest.programTitle" /> 
                              <#list spring.status.errorMessages as error> 
                                <div class="alert alert-error"><i class="icon-warning-sign"></i>${error}</div>
                              </#list>
                            </div>
                          </div>
                          
                          <div class="row">
                            <label class="plain-label" for="programDescription">Program Description<em>*</em></label>
                            <span class="hint" data-desc="<@spring.message 'opportunityRequest.programDescription'/>"></span>
                            <div class="field">
                              <textarea id="programDescription" name="programDescription" class="max" cols="70" rows="6">${(opportunityRequest.programDescription?html)!}</textarea>
                              <@spring.bind "opportunityRequest.programDescription" /> 
                              <#list spring.status.errorMessages as error> 
                                <div class="alert alert-error"><i class="icon-warning-sign"></i>${error}</div>
                              </#list>
                            </div>
                          </div>
                          
                          <div class="control-group">
                            <label for="studyDurationNumber" class="plain-label">Duration of Study<em>*</em></label>
                            <span class="hint" data-desc="<@spring.message 'prospectus.durationOfStudy'/>"></span>
                            <div class="field">
                                <input class="numeric input-small" type="text" size="4" id="studyDurationNumber" name="studyDurationNumber" value="${(opportunityRequest.studyDurationNumber?string)!}" />
                                <select id="studyDurationUnit" name="studyDurationUnit" class="input-small">
                                    <#assign unit = opportunityRequest.studyDurationUnit!>
                                    <option value="">Select...</option>
                                    <option value="MONTHS" <#if unit?? && unit == "MONTHS">selected="selected"</#if>>Months</option>
                                    <option value="YEARS" <#if unit?? && unit == "YEARS">selected="selected"</#if>>Years</option>
                                </select>
                                <@spring.bind "opportunityRequest.studyDurationNumber" />
                                <#list spring.status.errorMessages as error>
                                  <div class="alert alert-error"> <i class="icon-warning-sign"></i>
                                    ${error}
                                  </div>
                                </#list>
                                <@spring.bind "opportunityRequest.studyDurationUnit" />
                                <#list spring.status.errorMessages as error>
                                  <div class="alert alert-error"> <i class="icon-warning-sign"></i>
                                    ${error}
                                  </div>
                                </#list>
                            </div>
                          </div>
                          
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