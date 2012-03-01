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
	
	</head>

	<body>

		<div id="wrapper">
			
			  <#include "/common/header.ftl"/>
			  
			  <!-- Middle. -->
			  <div id="middle">
			  
			    <#include "/common/nav_with_user_info.ftl"/>
			    
			    <!-- Main content area. -->
			    <article id="content" role="main">
			      
			      <#include "/common/tools.ftl"/>
			      
			      <!-- content box -->
			      <div class="content-box">
			        <div class="content-box-inner">
			
						<div id="programme-details">
			          
				          	<div class="row">
				            	<label>Programme Name</label>
				              <input disabled size="109" value="${model.applicationForm.project.program.code} - ${model.applicationForm.project.program.title}" />
				            </div>
				            
				          	<div class="row half">
				            	<label>Application Number</label>
				              <input id="applicationNumber" disabled size="20" value="${model.applicationForm.id}" />
				            </div>
				            
				            <#include "/application/supervisor_section.ftl"/>
				            
			          	</div>
			          
			          <hr />
			
			          <section class="folding violet">
			            <h2 class="tick open">
			              <span class="left"></span><span class="right"></span><span class="status"></span>
			              Programme
			            </h2>
			            <div>
			            	<p>Testing...</p>
			            </div>
			          </section>
			          
			          <#include "/application/personal_details_internal.ftl"/>
			          
			          <!-- Address -->
			          <section class="folding red">
                        <#include "/application/address_internal.ftl"/>
			          </section>
			          
			           <section class="folding orange">
			            <h2 class="open">
			              <span class="left"></span><span class="right"></span><span class="status"></span>
			              Qualifications
			            </h2>
			            <div>
			              Testing...
			            </div>
			          </section>
			          
			           <section class="folding yellow">
			            <h2 class="open">
			              <span class="left"></span><span class="right"></span><span class="status"></span>
			              Employment
			            </h2>
			            <div>
			              coming soon.
			            </div>
			          </section>
			          
			           <section class="folding green">
			             <#include "/application/funding_internal.ftl"/>
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
			             <form id="submitApplicationForm" action="<@spring.url "/apply/submit"/>" method="POST">
			          	      <input type="hidden" id="applicationFormId" name="applicationForm" value="${model.applicationForm.id}"/>
			             </form>
			    	 </#if>
			        </div><!-- .content-box-inner -->
			      </div><!-- .content-box -->
			  
			    </article>
		
			  </div>
			  
			<#include "/common/footer.ftl"/>
		
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
