
<!DOCTYPE HTML>

<#import "/spring.ftl" as spring />

<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<title>Shell template</title>
		
		<!-- Always force latest IE rendering engine (even in intranet) & Chrome Frame -->
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
		
		<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/global_private.css' />"/>
		
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
		      
		      		<div class="content-box">
		      			<div class="content-box-inner">
		      			
		      			
		      				<!---------- Reference -------------->
		      			
		      			
		              		<h2>Thank you for agreeing to provide a reference for ${applicationForm.applicant.firstName} ${applicationForm.applicant.lastName}.</h2>
		              		
		              		<br/>
		              		<#if reference.id?? >
		              			<p>You have already provided a reference. You may use the fields below to upload a different file if you wish.</p>							
			        		<#else>
			        			<p>Please upload your reference as a PDF document using the field below.</p>		              				          		
			        		</#if>
			        		<p>You can view ${applicationForm.applicant.firstName}'s application <a href="<@spring.url '/application?applicationId=${applicationForm.id?string("######")}' />" target="_blank">here</a></p>
							<form id="documentUploadForm" method="POST" action="<@spring.url '/referee/submitReference'/>">	
								<input type="hidden" name="application" value ='${applicationForm.id?string("######")}'/>           
					            <div>
		
					    		                						
		                			<div class="field" id="referenceUploadFields">        	
		                			 	<label for="file">Upload file</label>
					            		<input id="referenceDocument" class="full" type="file" name="file" value=""/>					
										<span id="referenceUploadedDocument" ><input type="hidden" id="document_REFERENCE" value = "${(reference.document.id?string('######'))!}" name="document"/>
											<@spring.bind "reference.document" /> 
					                		<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>			
					                		<a href="<@spring.url '/download?documentId=${(reference.document.id?string("#######"))!}'/>">${(reference.document.fileName)!}</a></span>								
										 </span>
										<span id="referenceDocumentProgress" style="display: none;" ></span>					
					        		</div>  
        		
								</div>
								
								<div class="buttons">
									<button type="reset" value="cancel">Cancel</button>
					                <button class="blue" type="submit" id="referenceSaveButton" value="close">Submit</button>              
								</div>			          			
							</form>		     
		          			<!---------- End Reference -------------->
		          					          			
		          			
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
		
		<!-- Scripts -->
		<script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/libraries.js' />"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/script.js' />"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/help.js' />"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/application/ajaxfileupload.js'/>"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/referee/reference.js'/>"></script>
	</body>
</html>


<html>
<#import "/spring.ftl" as spring />

    
    <div>
    	

	</div>
	

</html>