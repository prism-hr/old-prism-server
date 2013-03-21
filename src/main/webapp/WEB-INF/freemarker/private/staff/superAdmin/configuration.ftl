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
		<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/staff/state_transition.css' />"/>
		<link rel="shortcut icon" type="text/css" href="<@spring.url '/design/default/images/favicon.ico' />"/>
		
        <!-- Styles for Application List Page -->

        <link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/jquery-ui-1.8.23.custom.css' />"/>

		<!--[if lt IE 9]>
		<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
		<![endif]-->
	
	    <script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
	    <script type="text/javascript" src="<@spring.url '/design/default/js/jquery-ui-1.8.23.custom.min.js' />"></script>
	    <script type="text/javascript" src="<@spring.url '/design/default/js/libraries.js' />"></script>
	    <script type="text/javascript" src="<@spring.url '/design/default/js/script.js' />"></script>
	    <script type="text/javascript" src="<@spring.url '/design/default/js/superAdmin/configuration.js' />"></script> 
	    <script type="text/javascript" src="<@spring.url '/design/default/js/superAdmin/badge.js'/>"></script>
	    <script type="text/javascript" src="<@spring.url '/design/default/js/templateEdit/actions.js'/>"></script>
	    
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
				<@header activeTab="config"/>
					<!-- Main content area. -->
					<article id="content" role="main">		    
						
						<!-- content box -->				      
						<div class="content-box">
							<div class="content-box-inner">
							
								<div id="configsection">		
								
								</div>
								
								<div id="edit-template-section">
									<section class="form-rows">
										<h2>Email templates</h2>
									
									<div>
									<div class="alert alert-info">
										<i class="icon-info-sign"></i> 
										Customise the e-mail templates
									</div>

										<div class="row-group">
											<div class="row">
											<label class="plain-label" for="emailTemplateType">Template</label>
											<span data-desc="Select the template to edit" class="hint" ></span>
											<div class="field">
							            		<select name="emailTemplateType" id="emailTemplateType" class="templateType">
							                		<option value="default">Template...</option>
							                  		<#list templateTypes as type>
								              			<option value="${type}">${type.displayValue()}
								               			</option>
							                  		</#list>
							             		</select>
							             	</div>
						             	</div>
						             	<div class="row">
						             		<label class="plain-label" for="emailTemplateVersion">Template Version</label>
						             		<span data-desc="Select the version template to edit" class="hint" ></span>
						             		<div class="field">
						             			<select name="emailTemplateVersion" id="emailTemplateVersion" class="templateVersion"></select>
						             		</div>
						             	</div>
						            	</div>
						            	<div class="row-group">
						            		<div class="row">
												<label class="plain-label" for="templateContentId">Edit template</label>
												<span data-desc="HTML template" class="hint" ></span>
												<div class="field">
													<textarea disabled name="templateContent" id="templateContentId" class="input-xxlarge" rows="15" cols="150">
														<#if template??>${template.content}</#if>
													</textarea>
												</div>
											</div>
											<div class="field"><button class="btn" type="button" id="preview-go">Preview</button></div>
										</div>
										<div class="buttons">
										<button class="btn btn-danger" type="button" id="delete-go">Delete Draft</button>
										<button class="btn" type="button" id="save-go">Save As Draft</button>
										<button class="btn btn-primary" type="button" id="enable-go">Save as Default</button>
										</div>
									</div>
									</section>
								</div>
								
								<section class="form-rows">
									<h2>Badge</h2>
									
									<div>
										<div class="alert alert-info">
										<i class="icon-info-sign"></i> 
											Configure your 'apply now' badge. You embed this on external web pages to enable your programme to accept applications.
										</div>
										<form id = "badgeSection">
										
											
										
										</form>
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