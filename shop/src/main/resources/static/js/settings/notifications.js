
  
async function getNotificationsPreferences(){
	const response=await fetch("/user/settings/getNotificationsPreferences");
	const preferences=await response.json();
	console.log(preferences);
	document.getElementById("offers").checked=preferences.offers;
	document.getElementById("orderStatus").checked=preferences.orderStatus;
	document.getElementById("newArrivals").checked=preferences.newArrivals;
	document.getElementById("email").checked=preferences.viaEmail;
	document.getElementById("push").checked=preferences.viaPush;
}

getNotificationsPreferences();

document.getElementById("checkForm").addEventListener("submit",function(event){
	event.preventDefault();
	console.log("success");
	
	const preferences={
		offers:document.getElementById("offers").checked,
		orderStatus:document.getElementById("orderStatus").checked,
		newArrivals:document.getElementById("newArrivals").checked,
		viaEmail:document.getElementById("email").checked,
		viaPush:document.getElementById("push").checked
	};
	

	fetch("/user/settings/setNotificationsPreferences",{
		method:"POST",
		headers:{
			"Content-Type":"application/json"
		},
		body:JSON.stringify(preferences),
	})
	.then(response=>{
		if(response.ok){return response.text();}
		else{
			return response.text().then((errorMessage) => {throw new Error(errorMessage);});
		}
	})
	.then(message=>{document.getElementById("message").innerHTML=message;})
	.catch(errror=>{document.getElementById("message").innerHTML=errror.message;});	
});


