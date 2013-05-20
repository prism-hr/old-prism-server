<!DOCTYPE HTML>
<#import "/spring.ftl" as spring />
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
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/staff/prospectus.css' />"/>
<link rel="shortcut icon" type="text/css" href="<@spring.url '/design/default/images/favicon.ico' />"/>
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/bootstrap.min.css' />"/>
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/font-awesome.min.css' />"/>
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
<div id="wrapper"> <#include "/private/common/global_header.ftl"/> 
  
  <!-- Middle Starts -->
  <div id="middle"> 
    <#include "/private/common/parts/nav_with_user_info_toggle.ftl"/>
    <@header activeTab="prospectus"/>
    
    <!-- Main content area. -->
    <article id="content" role="main"> 
      
      <!-- content box -->
      <div class="content-box">
        <div class="content-box-inner">
        	<div id="prospectus">
				 <div>
		            <section class="form-rows">
		              <h2>Manage Research Programmes</h2>
		                <div class="alert alert-info"> <i class="icon-info-sign"></i>A specific guidance note for the context of use goes here. </div>
						
						<div class="row-group">
						<div class="row">
								<label for="programme" class="plain-label">Programme<em>*</em></label>
								<span class="hint" data-desc="<@spring.message 'badge.programme'/>"></span>
								<div class="field">
									<select name="programme" id="programme" class="max">
										<option value="">Please select a program</option>
										<#--
										<#list programs as program>
											<option value="${program.code}" <#if badge.program?? && badge.program.id == program.id> selected="selected"</#if>>${program.title?html}</option>
										</#list>
										-->
									</select>
								</div>
							</div>
							<#--
							<@spring.bind "badge.program" /> 
						        <#list spring.status.errorMessages as error>
						        <div class="row">
						            <div class="field">
						                <div class="alert alert-error">
						                    <i class="icon-warning-sign"></i> ${error}
						                </div>
						            </div>
						        </div>
						    </#list>
						    -->
						</div>

						<div class="row-group">
						<h3>Programme Advert</h3>
		              	<div class="row">
							<label for="programmeTitle" class="plain-label">Title <em>*</em></label>
							<span class="hint" data-desc="title here"></span>
							<div class="field">
							    <input id="programmeTitle" name="project" class="full" type="text" value="${(badge.projectTitle?html)!}" role="textbox" aria-haspopup="true">
							</div>
						</div>
		              	<div class="row">
							<label for="programmeDescription" class="plain-label">Description <em>*</em></label>
							<span class="hint" data-desc="description here"></span>
							<div class="field">
			                    <textarea name="programmeDescription" id="programmeDescription" class="input-xlarge" rows="6" cols="100"></textarea>
			                </div>
						</div>
		              	<div class="row">
							<label for="programmeDurationOfStudy" class="plain-label">Duration of Study <em>*</em></label>
							<span class="hint" data-desc="description here"></span>
							<div class="field">
								<intput id="programmeDurationOfStudy" class="full" type="text" role="textbox" aria-haspopup="true">
			                    <select id="timeUnit">
										<option value="">Select...</option>
								</select>
			                </div>
						</div>
		              	<div class="row">
							<label for="fundingInformation" class="plain-label">Funding Information</label>
							<span class="hint" data-desc="Funding Information here"></span>
							<div class="field">
			                    <textarea name="fundingInformation" id="fundingInformation" class="input-xlarge" rows="6" cols="100"></textarea>
			                </div>
						</div>
						</div>
						
		            </section>
		          </div>
  
             </div>
          </div>
        <!-- .content-box-inner --> 
      </div>
      <!-- .content-box --> 
      
    </article>
  </div>
  <!-- Middle Ends --> 
  
  <#include "/private/common/global_footer.ftl"/> </div>
<!-- Wrapper Ends --> 
</body>
</html>