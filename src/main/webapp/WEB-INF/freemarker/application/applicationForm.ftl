<!DOCTYPE HTML>
<#import "/spring.ftl" as spring />
<html>
	<head>
	
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<title>Shell template</title>
		<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/style.css' />"/>
		<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/application.css' />"/>
	
	</head>

	<body onload="fetchPersonalDetails()">

		<div id="wrapper">
			
			  <#include "/application/app_form_header.ftl"/>
			  
			  <!-- Middle. -->
			  <div id="middle">
			  
			    <#include "/application/app_form_mid_header_nav.ftl"/>
			    
			    <!-- Main content area. -->
			    <article id="content" role="main">
			      
			      <!-- content box -->
			      <div class="content-box">
			        <div class="content-box-inner">
			
						<div id="programme-details">
			          
				          	<div class="row">
				            	<label>Programme Name</label>
				              <input disabled size="109" value="${model.applicationForm.project.program.title}" />
				            </div>
				            
				          	<div class="row half">
				            	<label>Application Number</label>
				              <input disabled size="20" value="${model.applicationForm.project.program.code}" />
				            </div>
			          	</div>
			          
			          <hr />
			
			          <section class="folding violet">
			            <h2>
			              <span class="left"></span><span class="right"></span><span class="status"></span>
			              Programme
			            </h2>
			            <div>
			            	<p>Testing...</p>
			            </div>
			          </section>
			          
			          <#include "/application/personal_details_form.ftl"/>
			          
			          <!-- Address -->
			          <section class="folding red">
			            <h2>
			              <span class="left"></span><span class="right"></span><span class="status"></span>
			              Address
			            </h2>
			            <div>
			            	Testing...
			            </div>
			          </section>
			          
			           <section class="folding orange">
			            <h2>
			              <span class="left"></span><span class="right"></span><span class="status"></span>
			              Qualifications
			            </h2>
			            <div>
			              Testing...
			            </div>
			          </section>
			          
			           <section class="folding yellow">
			            <h2>
			              <span class="left"></span><span class="right"></span><span class="status"></span>
			              Employment
			            </h2>
			            <div>
			              coming soon.
			            </div>
			          </section>
			          
			           <section class="folding green">
			            <h2>
			              <span class="left"></span><span class="right"></span><span class="status"></span>
			              Funding
			            </h2>
			            <div>
			              Testing...
			            </div>
			          </section>
			          
			           <section class="folding navy">
			            <h2>
			              <span class="left"></span><span class="right"></span><span class="status"></span>
			              References
			            </h2>
			            <div>
			              Testing...
			            </div>
			          </section>
			          
			           <section class="folding blue">
			            <h2>
			              <span class="left"></span><span class="right"></span><span class="status"></span>
			              Documents
			            </h2>
			            <div>
			              Testing...
			            </div>
			          </section>
			          
			          <hr />
			          
			          <a class="button" href="#">Close</a>
			          <button type="submit" onclick="location.href='/pgadmissions/apply/success?id=${model.applicationForm.id}'">Submit</button>
			          
			        </div><!-- .content-box-inner -->
			      </div><!-- .content-box -->
			      
			    </article>
			    
			  </div>
			  
			<#include "/application/app_form_footer.ftl"/>
		
		</div>
		
		<!-- Scripts -->
		
		<script type='text/javascript' language="javascript" src="<@spring.url '/dwr/engine.js'/>"></script>
    	<script type='text/javascript' language="javascript" src="<@spring.url '/dwr/util.js'/>"></script>
    	<script type='text/javascript' language="javascript" src="<@spring.url '/dwr/interface/acceptDWR.js'/>"></script>
		
		<script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/libraries.js'/>"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/script.js'/>"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/application/formActions.js'/>"></script>
	</body>
</html>
