async function getCartItems(){
	const response =await fetch("/user/cart/getCartItems");
	if (!response.ok) {
	            console.error("Failed to fetch cart items:", response.statusText);
	            return;
	}
	const items=await response.json();
	console.log(items);
	const cartItems=document.getElementById("cartItems");
	items.forEach(item=>{
		const newItem=document.createElement("div");
		newItem.classList.add("item");
		
		const PRODUCT=document.createElement("div");
		PRODUCT.classList.add("PRODUCT");
		const img=document.createElement("img");
		if(item.product.images && item.product.images.length>0){
			const image=item.product.images[0];
			if(image.savingOption==0) img.src=image.url;
			else img.src=`data:image/jpeg;base64,${image.imageBase64}`;
		}
		img.alt="couldn't load image";
		img.onclick=function(){
			window.location.href=`/user/showProduct/${item.product.id}`;
	}
		PRODUCT.appendChild(img);
		
		const productInfo=document.createElement("div");
		productInfo.innerHTML=`<strong>${item.product.name}</strong><br>${item.product.stock} left`;
		PRODUCT.appendChild(productInfo);
		
		const PRICE=document.createElement("div");
		if(item.product.discount>0){
			PRICE.innerHTML=`<span style="color:red;">${item.product.finalPrice} RON </span><span style="font-size:16px;text-decoration:line-through;">(${item.product.price})</span>`;
		}
		else PRICE.innerHTML=`${item.product.price} RON`;
						
		const QTY=document.createElement("div");
		QTY.classList.add("buttons");
		
		
		const deleteButton=document.createElement("button");
		deleteButton.innerHTML='<i class="fa-solid fa-trash"></i>';
		deleteButton.classList.add("deleteButton");
		deleteButton.onclick=function(){
			fetch(`/user/cart/delete/${item.product.id}`,{
				method:"DELETE"
			})
			.then(response=>{
				if(response.ok) window.location.href="/user/cart";
			})
		};
		
		const quantity=document.createElement("input");
		
		quantity.type = "number";
		quantity.value = item.quantity;
		quantity.onchange=function(){
			const newQuantity=quantity.value;
			fetch(`/user/cart/updateItemQuantity/${item.id}`,{
				method:"POST",
				headers: {
					"Content-Type": "application/json"
				},
				body:JSON.stringify(newQuantity)
			})
			.then(response => {
			        if (response.ok) {
						updateCartDetails();
						updateTOTAL(item.id);
			            console.log("Quantity updated successfully!");
			        } else {
			            console.error("Failed to update quantity");
			        }
			    });
		};
		
		const decrementButton = document.createElement("button");
		       decrementButton.textContent = "-";
			   decrementButton.classList.add("quantityButton");
		       decrementButton.onclick = function () {
		           if (quantity.value > 1) {
		               quantity.value =parseInt(quantity.value)-1;
					   quantity.onchange();
		           }			   
		       };
		const incrementButton = document.createElement("button");
			   	incrementButton.textContent = "+";
				incrementButton.classList.add("quantityButton");
			   	incrementButton.onclick = function () {
					if(quantity.value<item.product.stock){
			   		  quantity.value =parseInt(quantity.value)+1;
					  quantity.onchange();
					  }
			   	};
		QTY.appendChild(decrementButton);
		QTY.appendChild(quantity);
		QTY.appendChild(incrementButton);
		
		const TOTAL=document.createElement("div");
		const totalPrice=document.createElement("span");
		totalPrice.id="totalPrice"+item.id;
		totalPrice.innerHTML=`${item.quantity*item.product.finalPrice} RON`;
		TOTAL.appendChild(totalPrice);
		TOTAL.appendChild(deleteButton);
		
		newItem.appendChild(PRODUCT);
		newItem.appendChild(PRICE);
		newItem.appendChild(QTY);
		newItem.appendChild(TOTAL);
		cartItems.appendChild(newItem);
	});
}

async function getCartDetails(){
	const finish=document.getElementById("finish");
	const response=await fetch("/user/cart/total");
	const total=await response.json();
	console.log(total);
	const totalInfo=document.createElement("div");
	totalInfo.id="totalInfo";
	totalInfo.innerHTML=`<h4>Total: ${total} RON<h4>`;
	
	const checkoutButton=document.createElement("button");
	checkoutButton.innerHTML="CHECKOUT";
	checkoutButton.onclick=function(){
		if(total>0){
			fetch("/user/cart/addOrder",{method:"POST",})
			.then(response=>{
				console.log(response);
				if(response.ok) return response.text();
			})
			.then(orderId=>{
				console.log(orderId);
				window.location.href=`/user/cart/checkout/${orderId}`;
			});
			}
		else
		document.getElementById("message").innerHTML="Your cart is empty!";
	};
	
	finish.appendChild(totalInfo);
	finish.appendChild(checkoutButton);
	
}
async function updateCartDetails(){
	const totalInfo=document.getElementById("totalInfo");
	const response=await fetch("/user/cart/total");
	const total=await response.json();
	totalInfo.innerHTML=`Total: ${total} RON`;
}
getCartItems();
getCartDetails();

function updateTOTAL(itemId){
	fetch("/user/cart/itemTotalPrice/"+itemId)
	.then(response=>{if(response.ok) return response.text();})
	.then(itemTotal=>{
		document.getElementById("totalPrice"+itemId).innerHTML=`${itemTotal} RON`;
	});	
}