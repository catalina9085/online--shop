document.getElementById("myForm").addEventListener("submit",function(event){
	event.preventDefault();
	const username=document.getElementById("username").value;
	const email=document.getElementById("email").value;
	const password=document.getElementById("password").value;
	const confirmPassword=document.getElementById("confirmPassword").value;

	fetch("/auth/register",{
		method:"POST",
		headers:{
			"Content-Type": "application/json",
		},
		body: JSON.stringify({ username,email,password,confirmPassword }),
	})
		.then(response=>{
			if(response.ok) {
				localStorage.setItem("email", email);
				window.location.href="/auth/verify";
				}
			else{
				return response.text().then((errorMessage) => {
					if(errorMessage==="This email is associated with an unverified account!")
						adaugaVerify();
					else throw new Error(errorMessage);
				});
			}
		})
		.catch(error=>{
			addError(error.message);
		});
} );
document.getElementById("verify").addEventListener("submit",function(event){
	event.preventDefault();
	const email=document.getElementById("email").value;
	
	fetch("/auth/resetPassword/resend",{
		method:"POST",
		headers:{
			"Content-Type": "application/json",
		},
		body: JSON.stringify({email}),
	})
	.then(response=>{
		if(response.ok){
			localStorage.setItem("email", email);
			window.location.href="/auth/verify";
		}
	})
	.catch(error=>{
				addError(error.message);
	});
	
});

function adaugaVerify(){
	document.getElementById("hidden").style.display="block";
}

document.getElementById("error").innerHTML="";
function addError(errorMessage){
	document.getElementById("error").innerHTML=errorMessage;

}