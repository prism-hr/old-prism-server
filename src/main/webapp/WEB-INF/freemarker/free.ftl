	<head>
		<link rel="stylesheet" href="#springTheme('styleSheet')" type="text/css"/>
   </head>
   <body>
		<h1> Welcome ${user.name}! </h1>
		<h2>Zuehlke - project holding page</h2>
		<h3>Velocity style</h3>
		<h3>User details</h3>
		<p>first name: $user.firstName</p>
		<p>last name: $user.lastName</p>		
		<p>Contact Numbers</p>
		<ul>
		<#list user.phoneNumbers as phoneNumber>
    		<li>${phoneNumber.name}: ${phoneNumber.number}</li>
		</#list>
		</ul>		
		<img alt="" src="#springTheme('image')">
	</body>
</html>