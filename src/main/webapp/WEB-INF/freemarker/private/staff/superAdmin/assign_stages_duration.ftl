<section id="assignstagessection" >					          	
<!DOCTYPE HTML>
<#import "/spring.ftl" as spring />
<html>

	<head>
	
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<title>UCL Postgraduate Admissions</title>

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
	    <script type="text/javascript" src="<@spring.url '/design/default/js/superAdmin/assignStagesDuration.js' />"></script> 
	    
	    
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
							    <h1>Application form Stages duration</h1>
							    <br/>
							     <form>
							     <#list stages as stage>
	   								<div class="row"> 
			           					<span id="${stage.displayValue()}-lbl" class="plain-label">${stage.displayValue()} Duration</span>
			            				<div class="field">	
			            					<input type="hidden" id="${stage.displayValue()}_Stage" value="${stage}" />	            				
			            					<input type = "text" name="${stage.displayValue()}_Duration" id="validationDuration" />
		            							<select name="units" id="units">
													<option value="">Select...</option>
														<#list units as unit>
												 		 	<option value="${unit}" >${unit.displayValue()}</option>               
														</#list>
												 </select>	
		            						</div>
									</div>
									</#list>
									
			            			<div class="buttons">						        		
			            				<button type="button" id="cancelDurationBtn" value="cancel">Cancel</button>
							       		<button class="blue" id="submitDurationStages" type="button" value="Submit">Submit</button>						        
									</div>
									</form>
			  					<hr/>
		  					
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
</section>