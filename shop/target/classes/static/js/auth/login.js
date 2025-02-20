document.getElementById("myForm").addEventListener("submit",function(event){
	event.preventDefault();
	const identifier=document.getElementById("identifier").value;
	const password=document.getElementById("password").value;
	
	fetch("/auth/login",{
		method:"POST",
		headers: {
		            "Content-Type": "application/x-www-form-urlencoded", 
		        },
		        body: `username=${encodeURIComponent(identifier)}&password=${encodeURIComponent(password)}`
	})
	.then(response=>{
		if(response.ok){
			window.location.href="/user/main";
		}
		else{
			return response.text().then((errorMessage) => {
				throw new Error(errorMessage);
			});
		}
	})
	.catch(error=>{
				addError(error.message);
	});
	
});
document.getElementById("error").innerHTML="";
function addError(errorMessage){
	document.getElementById("info").innerHTML="";
	document.getElementById("error").innerHTML=errorMessage;
}

const info=document.getElementById("info");
info.innerHTML="";
const message=localStorage.getItem("message");
if(message){
	info.innerHTML=message;
	localStorage.removeItem("message");
}