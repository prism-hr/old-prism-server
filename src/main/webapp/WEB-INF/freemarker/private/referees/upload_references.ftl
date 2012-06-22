
<!DOCTYPE HTML>

<#import "/spring.ftl" as spring />

<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title>UCL Postgraduate Admissions</title>
    
    <!-- Always force latest IE rendering engine (even in intranet) & Chrome Frame -->
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
    
    <link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/global_private.css' />"/>
		<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/application.css' />"/>
    
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
					       <@header/>
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
								<p>You can view ${applicationForm.applicant.firstName}'s application <a href="<@spring.url '/application?applicationId=${applicationForm.applicationNumber}' />" target="_blank">here</a></p>
								
								<section class="form-rows">
									<div>
										<form id="documentUploadForm" method="POST" action="<@spring.url '/referee/submitReference'/>">  

											<div class="row-group">
												<div class="row">
												<span class="plain-label">Comments<em>*</em></span>
												<span class="hint" data-desc="<@spring.message 'reference.comment'/>"></span>
												<div class="field">		            				
													<textarea id="comment" name="comment" class="max" rows="6" cols="80" maxlength='5000'></textarea>
												</div>
											</div>
											
												<div class="row">
											
													<input type="hidden" name="application" value ='${applicationForm.applicationNumber}'/>           
													<div class="field" id="referenceUploadFields">          
														<label for="file">Upload file</label>
														<input id="referenceDocument" class="full" type="file" name="file" value=""/>          
														<span id="referenceUploadedDocument" ><input type="hidden" id="document_REFERENCE" value = "${(encrypter.encrypt(reference.document.id))!}" name="document"/>
															<@spring.bind "reference.document" /> 
															<#list spring.status.errorMessages as error> <span class="invalid">${error}</span></#list>                                  
														</span>
														<span id="referenceDocumentProgress" style="display: none;" ></span>          
														<#if reference.id??>
														<br />
														<div>Previous File: <a href="<@spring.url '/download/reference?referenceId=${encrypter.encrypt(reference.id)}'/>">${reference.document.fileName?html}</a></div>
														</#if>
													</div>  
												</div>
												<div class="buttons">
													<button type="reset" value="cancel">Cancel</button>
													<button class="blue" type="submit" id="referenceSaveButton" value="close">Submit</button>              
												</div>                      
											</div>
										</form>
                    <!---------- End Reference -------------->
                  
                    <#if !reference.id?? >
										<div class="row-group">
											<form id="declineForm" method="POST" action="<@spring.url '/referee/decline'/>">
												<input type="hidden" name="referee" value='<#if reference.referee.id??>${encrypter.encrypt(reference.referee.id)}</#if>'/>                    
												<p>If you are not able to act as a referee in this case, please let us know by clicking the "Decline" button below.</p>
												<div class="buttons">
													<button class="blue" type="button" id="declineReference" value="close">Decline</button>              
				                </div>
        				      </form>
										</div>
                    </#if>
                    
									</div>
								</section>
                    
							</div><!-- .content-box-inner -->
						</div><!-- .content-box -->
          
          </article>
        
        </div>
      
<#include "/private/common/global_footer.ftl"/>
    
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
