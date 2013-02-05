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
		<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/staff/superadmin.css' />"/>
		<link rel="shortcut icon" type="text/css" href="<@spring.url '/design/default/images/favicon.ico' />"/>
		
		<!-- Styles for Application List Page -->

		<!--[if lt IE 9]>
		<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
		<![endif]-->
	
		<script type='text/javascript' language="javascript" src="<@spring.url '/dwr/engine.js'/>"></script>
	    <script type='text/javascript' language="javascript" src="<@spring.url '/dwr/util.js'/>"></script>
	    <script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
	    <script type="text/javascript" src="<@spring.url '/design/default/js/applicationList/formActions.js'/>"></script>
	    
		<script type="text/javascript" src="<@spring.url '/design/default/js/libraries.js' />"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/script.js' />"></script>
	    
	    
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
			
				<!-- Main content area. -->
				<article id="content" role="main">
				      
					<div class="content-box">
				      	<div class="content-box-inner">
				        
				          	<form>
				          
					          	<div class="row">
					            	<label>Select host Department / Institute / Centre</label>
					             	<select>
					              		<option>UCL Energy Institute</option>
					              	</select>
					            </div>
					
					          	<div class="row">
					            	<label>Select programme</label>
					              	<select>
					              		<option>LoLo EngD</option>
					              	</select>
					            </div>
				
				        	</form>
				          
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
