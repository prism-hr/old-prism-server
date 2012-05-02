<!DOCTYPE HTML>
<#import "/spring.ftl" as spring />
<html>

	<head>
	
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<title>Shell template</title>

		<!-- Always force latest IE rendering engine (even in intranet) & Chrome Frame -->
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
		
		<!-- Styles for Application List Page -->
		<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/global_private.css' />"/>
		<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/staff/state_transition.css' />"/>
				<!-- Styles for Application List Page -->

		<!--[if lt IE 9]>
		<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
		<![endif]-->
	
	    <script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
	        <script type="text/javascript" src="<@spring.url '/design/default/js/admin/comment/genericComment.js' />"></script> 
	    <script type="text/javascript" src="<@spring.url '/design/default/js/admin/changeState.js' />"></script> 
	    
	    
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
			
				<#include "/private/common/parts/nav_with_user_info.ftl"/>
				
				    <!-- Main content area. -->
				    <article id="content" role="main">		    
				      
				      <!-- content box -->				      
				      <div class="content-box">
				        <div class="content-box-inner">
				        		<input type="hidden" id="applicationId" value =  "${(applicationForm.id?string('#####'))!}"/>
				        		<input type="hidden" id="status" value =  "${(applicationForm.status)!}"/>
							    <h1>Validate application ${(applicationForm.id?string('#####'))!}</h1>
							    <br/><br/>
		            			<div class="row">
		            				<span class="plain-label">Comment</span>
		            				<div class="field">		            				
		            					<textarea id="comment" name="comment" class="max" rows="6" cols="80" maxlength='5000'></textarea>
		            				</div>
		            			</div>
		        
		            			<div class="row">
		            				<label class="plain-label">Next stage</label>
		            				<div class="field">		            				
		            					<select class="max" name="status" id="status">
											<option value="">Select...</option>
											<#list stati as status>
												  <option value="${status}" >${status.displayValue()}</option>               
											</#list>
										 </select>	
		            				</div>
		            			</div>
		            			<div class="buttons">						        		
		            				<button type="reset" value="cancel">Cancel</button>
						       		<button class="blue" type="button" id="changeStateButton" value="save">Submit</button>						        
								</div>
							
							<hr/>	
							<div id= "timeline">
		  						
		  					</div>
		  
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
