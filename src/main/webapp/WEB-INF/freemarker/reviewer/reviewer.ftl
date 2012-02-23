<html>
    <head>
    <#import "/spring.ftl" as spring />
    </head>
    <body>
        <h2>Assign reviewers:</h2>
        <h3>Application ${model.application.id}</h3>
        <#list model.reviewers as reviewer>
            <form action="<@spring.url '/reviewer/reviewerSuccess'/>" method = "POST">
                <input type="hidden" value="${model.application.id}" name="id"/>
                <input type="hidden" value="${reviewer.id}" name="reviewerId"/>
                <p>${reviewer.username}
                    <button>Add</button>
                </p>
            </form>
        </#list>
         <p><a href="<@spring.url '/j_spring_security_logout'/>">Log out</a></p>
    </body>
   
</html>