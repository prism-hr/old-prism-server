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
		<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/application.css' />"/>
		<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/staff/state_transition.css' />"/>
				<!-- Styles for Application List Page -->

		<!--[if lt IE 9]>
		<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
		<![endif]-->
	
	   <script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
	<script type="text/javascript" src="<@spring.url '/design/default/js/libraries.js'/>"></script>
	<script type="text/javascript" src="<@spring.url '/design/default/js/script.js'/>"></script>	
	<script type="text/javascript" src="<@spring.url '/design/default/js/application/common.js'/>"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/superAdmin/badge.js'/>"></script>
		    
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
							
								<section id="configuration" class="form-rows">
									<h2>Programme badge</h2>
									<div>
										<form>
										
											<div class="row-group">
												<div class="row">
													<label class="plain-label">Programme</label>
													<div class="field">
														<select name="programme" id="programme">
															<option value="">Please select a program</option>
															<#list programs as program>
																<option value="${program.code}">${program.title?html}</option>
															</#list>
														</select>
													</div>
												</div>
										
												<div class="row">
													<label class="plain-label">Programme home page</label>
													<div class="field">
														<input type="text" name="programhome" id="programhome" class="max" />
													</div>
												</div>		

												<div class="row">
													<label class="plain-label">Project title</label>
													<div class="field">
														<input type="text" name="project" id="project" class="max" />
													</div>
												</div>												
																						
												<div class="row">
													<label class="plain-label">Batch deadline</label>
													<div class="field">
														<input type="text" name="batchdeadline" id="batchdeadline" class="half date" />
													</div>
												</div>
												
											</div><!-- .row-group -->
											
											<div class="row-group">
												<label class="plain-label">HTML</label>
												<div class="field">
													<textarea readonly="readonly" id="html" rows="15" cols="70"></textarea>
												</div>
											</div>
											
											<div class="row-group">
												<label class="plain-label">Badge</label>													
												<div class="field">
													<iframe id="badge" width="535" height="200"></iframe>											
												</div>
											</div>
											
											<div class="buttons">						        		
												<button type="button" id="cancelBadge" value="cancel">Clear</button>				        
											</div>
											
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