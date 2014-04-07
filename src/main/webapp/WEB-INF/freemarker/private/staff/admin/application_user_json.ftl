{
	"id": "${encrypter.encrypt(user.id)}",
	"firstname" : "${user.firstName?html}",
	"lastname" : "${user.lastName?html}",
	"email" : "${user.email?html}",
	"isNew": ${isNew?string},
	"toString" :  "${user.firstName?html} ${user.lastName?html} (${user.email?html})"
}