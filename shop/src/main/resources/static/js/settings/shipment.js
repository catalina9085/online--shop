document.getElementById("shipmentForm").addEventListener("submit",function(event){
	event.preventDefault();
	
	const firstName=document.getElementById("firstName").value;
	const lastName=document.getElementById("lastName").value;
	const country=document.getElementById("country").value;
	const province=document.getElementById("province").value;
	const town=document.getElementById("town").value;
	const addressLine1=document.getElementById("addressLine1").value;
	const addressLine2=document.getElementById("addressLine2").value;
	const postalCode=document.getElementById("postalCode").value;
	const phoneNumber=document.getElementById("phoneNumber").value;
	
	fetch("/user/settings/shipment",{
		method:"POST",
		headers:{
			"Content-Type": "application/json"
		},
		body: JSON.stringify({firstName,lastName,country,province,town,addressLine1,addressLine2,phoneNumber,postalCode})
	})
	.then(response=>{
		if(!response.ok) document.getElementById("message").innerHTML="There's been an error in the saving process! Please try again.";
	});
	
});

async function getShipment(){
	const response=await fetch("/user/cart/getShipmentDetails");
	if(response.ok){
		const shipment=await response.json();
		console.log(shipment);
		const firstName=document.getElementById("firstName");
		firstName.value=shipment.firstName;
		const lastName=document.getElementById("lastName");
		lastName.value=shipment.lastName;	
		const country=document.getElementById("country");
		country.value=shipment.country;
		const province=document.getElementById("province");
		province.value=shipment.province;
		const town=document.getElementById("town");
		town.value=shipment.town;
		const addressLine1=document.getElementById("addressLine1");
		addressLine1.value=shipment.addressLine1;
		const addressLine2=document.getElementById("addressLine2");
		addressLine2.value=shipment.addressLine2;
		const postalCode=document.getElementById("postalCode");
		postalCode.value=shipment.postalCode;
		const phoneNumber=document.getElementById("phoneNumber");
		phoneNumber.value=shipment.phoneNumber;
	}
}

getShipment();