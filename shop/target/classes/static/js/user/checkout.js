const pathSegments = window.location.pathname.split('/');
const orderId = pathSegments[pathSegments.length - 1];
console.log(orderId);
async function getShipmentDetails(){
	const response=await fetch("/user/cart/getShipmentDetails");
	if(response.ok){
		const shipment=await response.json();
		document.getElementById("firstName").value=shipment.firstName;
		document.getElementById("lastName").value=shipment.lastName;	
		document.getElementById("country").value=shipment.country;
		document.getElementById("province").value=shipment.province;
		document.getElementById("town").value=shipment.town;
		document.getElementById("addressLine1").value=shipment.addressLine1;
		document.getElementById("addressLine2").value=shipment.addressLine2;
		document.getElementById("phoneNumber").value=shipment.phoneNumber;
		document.getElementById("postalCode").value=shipment.postalCode;
		document.getElementById("2firstName").innerHTML=shipment.firstName;
		document.getElementById("2lastName").innerHTML=shipment.lastName;	
		document.getElementById("2country").innerHTML=shipment.country;
		document.getElementById("2province").innerHTML=shipment.province;
		document.getElementById("2town").innerHTML=shipment.town;
		document.getElementById("2addressLine1").innerHTML=shipment.addressLine1;
		document.getElementById("2adressLine2").innerHTML=shipment.addressLine2;
		document.getElementById("2phoneNumber").innerHTML=shipment.phoneNumber;
		document.getElementById("2postalCode").innerHTML=shipment.postalCode;
	}
	else{
		document.getElementById("paymentInfo").style.display="none";
		//document.getElementById("paymentForm").style.display="block";
		let form = document.getElementById("paymentForm");
		    form.style.opacity = "1";
		    form.style.height = "auto";
	}
}
async function getOrder(){
	try{
		const response=await fetch(`/user/cart/getOrder/${orderId}`);
		const order=await response.json();
		console.log(order);
		const productList=document.getElementById("productList");
		order.products.forEach(product=>{
			const productContainer=document.createElement("div");
			productContainer.id="productContainer";
			const img=document.createElement("img");
			if(product.savingOption==0) img.src=product.url;
			else img.src=`data:image/jpeg;base64,${product.imageBase64}`;
			img.alt="Couldnt't load image!";
			
			const info=document.createElement("div");
			info.innerHTML=`<strong>${product.productName}</strong><br>${product.price} RON x ${product.quantity}`;
			
			productContainer.appendChild(img);
			productContainer.appendChild(info);
			productList.appendChild(productContainer);
		});
		
	}catch(error){
		console.log(error.message);
	}
}
getOrder();
getShipmentDetails();

document.getElementById("paymentForm").addEventListener("submit",function(event){
	event.preventDefault();
	const firstName=document.getElementById("firstName").value;
	const lastName=document.getElementById("lastName").value;	
	const country=document.getElementById("country").value;
	const province=document.getElementById("province").value;
	const town=document.getElementById("town").value;
	const addressLine1=document.getElementById("addressLine1").value;
	const addressLine2=document.getElementById("addressLine2").value;
	const phoneNumber=document.getElementById("phoneNumber").value;
	const postalCode=document.getElementById("postalCode").value;
	
	fetch("/user/settings/shipment",{
		method:"POST",
		headers:{
			"Content-type":"application/json",
		},
		body:JSON.stringify({firstName,lastName,country,province,town,addressLine1,addressLine2,phoneNumber,postalCode})
	})
	.then(response=>{
		if(response.ok) window.location.href=`/user/cart/checkout/${orderId}`;
	});
});

document.getElementById("editInfo").addEventListener("click",function(){
	document.getElementById("paymentInfo").style.display="none";
	//document.getElementById("paymentForm").style.display="block";
	let form = document.getElementById("paymentForm");
	    form.style.opacity = "1";
	    form.style.height = "auto";
});
document.getElementById("placeOrder").addEventListener("click",function(event){
	event.preventDefault();
	fetch(`/user/cart/placeOrder/${orderId}`,{method:"POST",})
	.then(response=>{
		if(response.ok) return response.text();
		else{
			//fetch(`/user/cart/removeOrder/${orderId}`,{method:"POST",});
			throw new Error("comanda nu a putut fi plasata");
		}
	})
	.then(link=>{
		adaugaLink(link);
	})
	.catch(error=>{
		document.getElementById("message").innerHTML=error.message;
	});
});

function adaugaLink(link){
	const container=document.getElementById("container");
	container.innerHTML=`
		<p><strong>Click on the link below to confirm your payment!</strong></p>
		<a href=${encodeURI(link)}>Confirm Payment</a>
	`;
	container.style.display="flex";
	container.style.justifyContent="center";
	container.style.alignItems="center";
	container.style.flexDirection="column";
	container.style.background="transparent";
}

