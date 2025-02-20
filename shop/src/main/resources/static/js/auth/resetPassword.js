document.getElementById("resetForm").addEventListener("submit",function(event){
	event.preventDefault();
	const email=document.getElementById("email").value;
	localStorage.setItem('email',email);
	fetch("/auth/resetPassword",{
		method:"POST",
		headers:{
			"Content-type":"application/json"
		},
		body:JSON.stringify({email})
	})
	.then(response=>{
		if(response.ok) addPart2();
		else{
			return response.text().then(errorMessage => {
				throw new Error(errorMessage);
			});
		}
	})
	.catch((error) => {
			    addError(error.message);
	});
});
document.getElementById("codeForm").addEventListener("submit",function(event){
	event.preventDefault();
	const email=localStorage.getItem("email");
	const code=document.getElementById("code").value;
	const newPassword=document.getElementById("newPassword").value;
	const confirmPassword=document.getElementById("confirmPassword").value;
	fetch("/auth/resetPassword/request",{
			method:"POST",
			headers:{
				"Content-type":"application/json"
			},
			body:JSON.stringify({email,code,newPassword,confirmPassword})
		})
		.then(response=>{
			if(response.ok){
				localStorage.removeItem("email");
				localStorage.setItem("message","Your password has been successfully changed.You can now log in!");
				window.location.href="/auth/login";
			}
			else{
				return response.text().then((errorMessage) => {
				        throw new Error(errorMessage);
				});
			}
		})
		.catch((error) => {
		    addError(error.message);
		});
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
		if(response.ok)
			document.getElementById("message").innerHTML="We sent a new email!";
	})
	.catch((error) => {
	    addError("Something went wrong: " + error.message);
	});
});
function addPart2(){
	const part2=document.getElementById("part2");
	const part1=document.getElementById("part1");
	part2.style.display="block";
	part1.style.display="none";
	document.getElementById("error").innerHTML="";
}

function addError(error){
	const errorElement=document.getElementById("error");
	errorElement.innerHTML=error;
}