<!DOCTYPE HTML>
<#import "/spring.ftl" as spring />
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
        <title>UCL Postgraduate Admissions</title>
    </head>
    <body>
	   <script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
	   <input type="hidden" id="feedId" value="${feedId}"/>
	   <#include "/private/prospectus/adverts.ftl"/>
    </body>
</html>
