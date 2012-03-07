<#-- Assignments -->

<#if model.user.isInRole('APPLICANT')>
	<#assign formDisplayState = "close"/>
<#else>
	<#assign formDisplayState = "open"/>
</#if>

<#-- Personal Details Rendering -->

<!DOCTYPE HTML>
<#import "/spring.ftl" as spring />
<html>
	<head>
	
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<title>Shell template</title>
		<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/style.css' />"/>
		<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/application.css' />"/>
		<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/field_controls.css' />"/>
			<!--[if lt IE 9]>
		<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
		<![endif]-->
	</head>

	<body>

		<div id="wrapper">
			
			  <#include "/private/common/global_header.ftl"/>
			  
			  <!-- Middle. -->
			  <div id="middle">
			  
			    <#include "/private/common/parts/nav_with_user_info.ftl"/>
			    
			    <!-- Main content area. -->
			    <article id="content" role="main">
			      
			      <!-- content box -->
			      <div class="content-box">
			        <div class="content-box-inner">
			
			            <p style="color:red;">${model.message!}</p>
						<div id="programme-details">
			          
				          	<div class="row">
				            	<label>Programme Name</label>
				              <input disabled size="109" value="${model.applicationForm.project.program.code} - ${model.applicationForm.project.program.title}" />
				            </div>
				            
				          	<div class="row half">
				            	<label>Application Number</label>
				              <input id="applicationNumber" disabled size="20" value="${model.applicationForm.id?string("######")}" />
				            </div>
				            
			          	</div>
			          
			          <hr />
			
			          <section id="programmeDetailsSection" class="folding violet">
			          	 <#include "/private/pgStudents/form/components/programme_details.ftl"/>
			          </section>
			          
			          <section id="personalDetailsSection" class="folding purple">
			             <#include "/private/pgStudents/form/components/personal_details.ftl"/>
			          </section>
			          
			          <!-- Address -->
			          <section id="addressSection" class="folding red">
			             <#include "/private/pgStudents/form/components/address_details.ftl"/>
			          </section>
			          
			           <section id="qualificationsSection" class="folding orange">
			            <#include "/private/pgStudents/form/components/qualification_details.ftl"/>
			          </section>
			          
			           <section id="positionSection" class="folding yellow">
			             <#include "/private/pgStudents/form/components/employment_position_details.ftl"/>
			          </section>
			          
			           <section id="fundingSection" class="folding green">
			             <#include "/private/pgStudents/form/components/funding_details.ftl"/>
			          </section>
			          
			           <section class="folding navy">
			            <h2 class="open">
			              <span class="left"></span><span class="right"></span><span class="status"></span>
			              References
			            </h2>
			            <div>
			              Testing...
			            </div>
			          </section>
			          
			           <section class="folding blue">
			            <h2 class="open">
			              <span class="left"></span><span class="right"></span><span class="status"></span>
			              Documents
			            </h2>
			            <div>
			              Testing...
			            </div>
			          </section>
			          
			          <hr />
			          
			          <a class="button" href="<@spring.url '/applications'/>">Close</a>
			          <#if !model.applicationForm.isSubmitted() && model.user.isInRole('APPLICANT')>
			             <a id="submitButton" class="button">Submit</a>
			             <form id="submitApplicationForm" action="<@spring.url "/submit"/>" method="POST">
			          	      <input type="hidden" id="applicationFormId" name="applicationFormId" value="${model.applicationForm.id?string("######")}"/>
			             </form>
			    	 </#if>
			        </div><!-- .content-box-inner -->
			      </div><!-- .content-box -->
			  
			    </article>
		
			  </div>
			  
			<#include "/private/common/global_footer.ftl"/>
		
		</div>
		
		<!-- Scripts -->
		
		<script type='text/javascript' language="javascript" src="<@spring.url '/dwr/engine.js'/>"></script>
    	<script type='text/javascript' language="javascript" src="<@spring.url '/dwr/util.js'/>"></script>
		
		<script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/libraries.js'/>"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/script.js'/>"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/application/formActions.js'/>"></script>
		
		
	</body>
</html>
