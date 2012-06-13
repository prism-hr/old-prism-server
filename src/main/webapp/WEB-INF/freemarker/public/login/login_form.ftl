<#import "/spring.ftl" as spring />
    <!-- Blurb. -->
    <aside id="blurb">
      <h2>Welcome to your new gateway to<br />UCL Postgraduate Research programmes<br />in Engineering Sciences.</h2>
      <p>Register today and begin your application to join some of the world's most highly regarded researchers and academics at the frontiers of discovery.</p>
    </aside>

    <!-- Login form. -->
    <section id="login-box">
    
    <form id="loginForm" method="post" action="/pgadmissions/j_spring_security_check">

        <p>&gt; Login</p>
        <input id="username_or_email" type="text" name="j_username" placeholder="Email address" />
        <input id="password" type="password" name="j_password" placeholder="Password" />
        
        <button name="commit" type="submit" value="Sign In" class="blue">Go</button>
        
      </form>
    </section>

    <!-- Registration button. -->
    <a id="big-button" href="#">Register Today...</a>