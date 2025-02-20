document.getElementById("myForm").addEventListener("submit",function(event){
	event.preventDefault();
	const verificationCode=document.getElementById("verificationCode").value;
	const email=localStorage.getItem("email");
	
	fetch("/auth/verify?code="+verificationCode+"&email="+email,{
		method:"POST",
		headers: {
		    "Content-Type": "application/json",
		}
	})
	.then(response=>{
		if(response.ok){
			localStorage.setItem("message", "Your account is now verified.You can log in.");
			localStorage.removeItem("email");
			window.location.href="/auth/login";	
		}
		else{
			localStorage.setItem("verifyError", "Invalid code.");
		}
			
	})
});

document.getElementById("resendForm").addEventListener("submit",function(event){
	event.preventDefault();
	const email=localStorage.getItem("email");
	fetch("/auth/resetPassword/resend",{
		method:"POST",
		headers:{
			"Content-type":"application/json"
		},
		body:JSON.stringify({email})
	})
	.then(response=>{
		if(response.ok){
			document.getElementById("info").innerHTML="We sent a new email!";
			document.getElementById("errorMessage").innerHTML="";
		}
	});
})

const message=localStorage.getItem("verifyError");
if(message){
	document.getElementById("errorMessage").textContent=message;
	localStorage.removeItem("verifyError");
}