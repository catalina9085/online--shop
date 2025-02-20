function sendEmail(){
	fetch("/user/settings/sendVerificationCode",{method:"POST"})
	.then(response=>{
		if(!response.ok) throw new Error("Couldn't send email.");
	})
	.catch(error=>{
		document.getElementById("errorMessage").innerHTML=error.message;
		document.getElementById("myForm").style.display="hidden";
	});
}

sendEmail();

document.getElementById("myForm").addEventListener("submit",function(event){
	event.preventDefault();
	const code=document.getElementById("verificationCode").value;
	fetch(`/user/settings/verifyCode/${code}`,{method:"POST",})
	.then(response=>{return response.text()})
	.then(message=>{
		document.getElementById("errorMessage").innerHTML=message;
	});
});

document.getElementById("resendForm").addEventListener("submit",function(){
	event.preventDefault();
	sendEmail();
	document.getElementById("info").innerHTML="We sent a new email!";
});