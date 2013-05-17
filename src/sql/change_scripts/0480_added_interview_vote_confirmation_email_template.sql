INSERT INTO `email_template` (name, content, active, subject) VALUES ('INTERVIEW_VOTE_CONFIRMATION','<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n<html xmlns=\"http://www.w3.org/1999/xhtml\">\n	<head>\n		<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n		<title>Untitled Document</title>\n	</head>\n	<body>\n		<table width=\"600\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n			<tr>\n		    	<td colspan=\"3\">\n		    		<img src=\"${host}/pgadmissions/design/default/images/email/header.jpg\" width=\"600\" height=\"160\" alt=\"Prism: A Spectrum of Postgraduate Research Opportunities\" />\n		    	</td>\n		  	</tr>\n		  	<tr>\n		    	<td width=\"50\">\n		    		<img src=\"${host}/pgadmissions/design/default/images/shim.gif\" width=\"50\" height=\"10\" alt=\"\" />\n		    	</td>\n		    	<td width=\"500\">\n		      		<h1 style=\"font-size: 12pt;\">\n		      			<font face=\"Arial, Helvetica, sans-serif\" color=\"#0055A1\">\n							Dear ${administrator.firstName},\n						</font>\n		      		</h1>\n			      	<p>\n			      		<font face=\"Arial, Helvetica, sans-serif\" size=\"2\">\n							An interview participant ${participant.user.displayName} for application ${application.applicationNumber} has provided a preference.\n						</font>\n			      	</p>\n			      	<p>\n			      		<font face=\"Arial, Helvetica, sans-serif\" size=\"2\">\n							You may consider confirming the interview time.\n						</font>\n			      	</p>\n			        <p>\n						<!-- Button -->\n						<a style=\"text-decoration:none;\" \n							<#if  !participant.user.enabled>\n		                      	href=\"${host}/pgadmissions/register?activationCode=${participant.user.activationCode!}&directToUrl=%2FinterviewConfirm%3FapplicationId%3D${application.applicationNumber}\"\n		                    <#else>\n		                      	href=\"${host}/pgadmissions/interviewConfirm?applicationId=${application.applicationNumber}&activationCode=${participant.user.activationCode!}\"\n		                    </#if>\n							title=\"Vote for a interview time\">\n			            	<img border=\"0\" style=\"border: none;\" width=\"149\" height=\"36\" alt=\"Vote for a interview\" src=\"${host}/pgadmissions/design/default/images/email/confirm_availability.jpg\" />\n			          	</a>\n			        </p>\n			      	<p>\n			      		<font face=\"Arial, Helvetica, sans-serif\" size=\"2\">\n							Yours sincerely,\n							<br/>\n							UCL Prism\n						</font>\n			      	</p>\n		    	</td>\n		    	<td width=\"50\">\n					<img src=\"${host}/pgadmissions/design/default/images/shim.gif\" width=\"50\" height=\"10\" alt=\"\" />\n				</td>\n		  	</tr>\n		  	<tr>\n		    	<td colspan=\"3\"><img src=\"${host}/pgadmissions/design/default/images/shim.gif\" width=\"50\" height=\"30\" alt=\"\" /></td>\n		  	</tr>\n		  	<tr>\n		    	<td colspan=\"3\" bgcolor=\"#6D6E71\" style=\"background-color: #6D6E71; line-height: 1; white-space: nowrap;\">\n					<table width=\"600\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n						<tr>\n							<td colspan=\"5\">\n								<img src=\"${host}/pgadmissions/design/default/images/shim.gif\" width=\"600\" height=\"9\" alt=\"\" />\n							</td>\n						</tr>\n						<tr>\n							<td width=\"48\">\n								<img src=\"${host}/pgadmissions/design/default/images/shim.gif\" width=\"48\" height=\"45\" alt=\"\" />\n							</td>\n							<td width=\"288\">\n								<font face=\"Arial, Helvetica, sans-serif\" size=\"1\" color=\"#BEBEC0\" style=\"font-size: 7pt;\">\n									University College London, Gower Street, London, WC1E 6BT\n									<br/>\n									Tel: +44 (0) 20 7679 2000\n									<br/>\n									&copy; UCL 1999&ndash;2012\n								</font>\n							</td>\n							<td width=\"50\">\n								<img src=\"${host}/pgadmissions/design/default/images/shim.gif\" width=\"50\" height=\"45\" alt=\"\" />\n							</td>\n							<td width=\"214\">\n								<img src=\"${host}/pgadmissions/design/default/images/email/footer_logo.gif\" width=\"214\" height=\"45\" alt=\"\" />\n							</td>\n						</tr>\n						<tr>\n							<td colspan=\"5\">\n								<img src=\"${host}/pgadmissions/design/default/images/shim.gif\" width=\"600\" height=\"9\" alt=\"\" />\n							</td>\n						</tr>\n					</table>\n		    	</td>\n		  	</tr>\n		</table>\n	</body>\n</html>',1,'%3$s %4$s Application %1$s for UCL %2$s - Interview Preference Confirmation')
;
