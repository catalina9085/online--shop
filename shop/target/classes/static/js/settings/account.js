document.getElementById("deleteAccount").addEventListener("click",function(){
	const hidden2=document.getElementById("hidden2");
	if(hidden2.style.display==="none")
		hidden2.style.display="block";
	
	else if(hidden2.style.display==="block")
		hidden2.style.display="none";
});
document.getElementById("hidden2").addEventListener("click", function (event) {
    event.stopPropagation();
});
document.getElementById("changePassword").addEventListener("click",function(){
	const hidden1=document.getElementById("hidden1");
	if(hidden1.style.display==="none")
		hidden1.style.display="block";
	
	else if(hidden1.style.display==="block")
		hidden1.style.display="none";
});

document.getElementById("deleteForm").addEventListener("submit",function(event){
	event.preventDefault();
	fetch("/user/settings/deleteAccount",{method:"DELETE",})
	.then(response=>{
		if(response.ok){
			localStorage.setItem("message","Your account has been successfully deleted!");
			window.location.href="/auth/login";
		}
	})
	.catch(error=>{
		console.log("Error:"+error.message);
	});
});

document.getElementById("changeForm").addEventListener("submit",function(event){
	event.preventDefault();
	const currentPassword=document.getElementById("currentPassword").value;
	const newPassword=document.getElementById("newPassword").value;
	const confirmPassword=document.getElementById("confirmPassword").value;
	fetch("/user/settings/changePassword",{
		method:"POST",
		headers:{
			"Content-type":"application/json"
		},
		body: JSON.stringify({currentPassword,newPassword,confirmPassword})
	})
	.then(response=>{
		if(response.ok){
			localStorage.setItem("message","Your password has been successfully changed!");
			window.location.href="/user/settings/account";
		}
		else{
			return response.text().then(errorMessage=>{
				document.getElementById("error").innerHTML=errorMessage;
			});
		}
	});
});
document.getElementById("hidden1").addEventListener("click", function (event) {
    event.stopPropagation();
});
const info=document.getElementById("info");
info.innerHTML="";
const message=localStorage.getItem("message");
if(message){
	info.innerHTML=message;
	localStorage.removeItem("message");
}
