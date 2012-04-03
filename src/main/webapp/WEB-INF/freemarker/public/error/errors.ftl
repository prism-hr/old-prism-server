<#import "/spring.ftl" as spring />
<html>
   <body>
        <h2>Errors</h2>
        <table>
           <#list model.errorObjs as error>
                  <td>- ${error.code}</td><tr>
            </#list>
        </table>   
        <p><a href="<@spring.url '/j_spring_security_logout'/>">Log out</a></p>
    </body>
    
</html>
