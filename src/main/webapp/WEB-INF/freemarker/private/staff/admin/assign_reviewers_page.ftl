<!DOCTYPE HTML>
<#import "/spring.ftl" as spring />
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<title>Shell template</title>
		
		<!-- Always force latest IE rendering engine (even in intranet) & Chrome Frame -->
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
		
		<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/global_private.css' />"/>
		<link type="text/css" rel="stylesheet" href="<@spring.url '/design/default/css/actions.css' />"/>

		<!--[if lt IE 9]>
		<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
		<![endif]-->
	</head>

	<body>
		
		<div id="wrapper">
		
		  <!-- Header. -->
		  <div id="header">
		    <p>Postgraduate Research Admissions Tool</p>
		  </div>
		  
		  <!-- Middle. -->
		  <div id="middle">
		  
		    <header>
		
		      <!-- App logo and tagline -->
		      <div class="logo"><img src="<@spring.url '/design/default/images/ph_logo_app.png'/>" alt="" /></div>
		
		      <div class="tagline">Your Gateway to Research Opportunities</div>
		
		      <!-- Main tabbed menu -->
		      <nav>
		        <ul>
		          <li><a href="#">My account</a></li>    
		          <li class="current"><a href="<@spring.url '/applications'/>">My applications</a></li>    
		          <li><a href="#">Messages</a></li>    
		          <li><a href="#">Help</a></li>    
		        </ul>
		        
		        <div class="user">
		           ${model.user.firstName} ${model.user.lastName} 
		          <a class="button user-logout" href="<@spring.url '/j_spring_security_logout'/>">Logout</a>
		        </div>
		      </nav>
		      
		    </header>
		    
		    
		    <!-- Main content area. -->
		    <article id="content" role="main">
		          
		      <!-- content box -->
		      <div class="content-box">
		        <div class="content-box-inner">
		          
							<div id="programme-details">
		          
		          	<div class="row">
		            	<label>Programme</label>
		              <input disabled size="109" value="${model.applicationForm.project.program.code!} - ${model.applicationForm.project.program.title!}" />
		            </div>
		            
		          	<div class="row half">
		            	<label>Application Number</label>
		              <input disabled size="20" value="${model.applicationForm.id?string("######")!}" />
		            </div>
		          </div>
				<div id ="actions">
				   	<div class="row">
		            	<label>Assigned Reviewers</label>
		               <#if model.applicationForm.isUnderReview()>
		                    <table>
                            <#list model.applicationForm.reviewers as reviewer>
                                <td>- ${reviewer.firstName} ${reviewer.lastName}</td><tr>
                            </#list>
                            </table>
                       <#else>
                                <p>Not yet assigned.</p>
                       </#if>
		            </div>
		        <#if model.applicationForm.isReviewable()  && (model.user.isInRole('ADMINISTRATOR')||model.user.isInRole('REVIEWER') )>
					<form  action="<@spring.url '/reviewer/reviewerSuccess'/>" method = "POST">		                    
			            <input type="hidden" name="id" value="${model.applicationForm.id?string("######")}"/>
			          	<div class="row">
			            	<label>Assign Reviewer</label>
			            	<select name="reviewers" multiple="multiple">
					        
						        <#list model.reviewers as reviewer>
						            <option value="${reviewer.id?string("######")}">${reviewer.firstName} ${reviewer.lastName}</option>               
						        </#list>
				             <select>
						        
						        <#list model.applicationForm.reviewers as reviewer>
						             <input type="hidden" name="reviewers" value="${reviewer.id?string("######")}"/>
                                </#list>
			            	
			            </div>
			            
			            <div class="buttons">
			              <button type="submit">Assign</button>
			            </div>
			          
			        </form>
			        <br />
  				</#if> 

				<#if model.applicationForm.isReviewable() && (model.user.isInRole('ADMINISTRATOR')|| model.user.isInRole('APPROVER')) >
					<form id="approvalForm" action="<@spring.url '/approveOrReject'/>" method = "POST">
						<input type="hidden" name="id" value="${model.applicationForm.id?string("######")!}"/>
			          	<div class="row">
			          		<#if model.user.isInRole('APPROVER')>
			            		<label><input type="radio" name="decision" value="APPROVED"/> Approve</label>
			            	</#if>
			            	<label><input type="radio" name="decision" value="REJECTED"/> Reject</label>
			            </div>
			         </form>
	            </#if>
	            <br />
				</div>
		        <!-- #actions -->
		          
		  <#include "/private/common/feedback.ftl"/>
		        </div><!-- .content-box-inner -->
		      </div><!-- .content-box -->
		      
		    </article>
		    
		  </div>
		  
		  <!-- Footer. -->
		  <div id="footer">
		    <ul>
		      <li><a href="#">Privacy</a></li>
		      <li><a href="#">Terms &amp; conditions</a></li>
		      <li><a href="#">Contact us</a></li>
		      <li><a href="#">Glossary</a></li>
		    </ul>
		  </div>
		
		</div>
		
		<script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/libraries.js'/>"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
	    <script type="text/javascript" src="<@spring.url '/design/default/js/applicationManagement/formAction.js'/>"></script>
	</body>
</html>
