async function getOrders(){
	const response=await fetch("/user/cart/getHistoryOrders");
	const orders=await response.json();
	console.log(orders);
	const paidOrderList=document.getElementById("paidOrderList");
	const pendingOrderList=document.getElementById("pendingOrderList");
	let paidCnt=0;
	let pendingCnt=0;
	orders.forEach(order=>{
		const newOrder=document.createElement("div");
		newOrder.id="order";
		
		const productList=document.createElement("div");
		productList.id="productList";
		
		order.products.forEach(product=>{
			const newProduct=document.createElement("div");
			newProduct.id="product";
			
			const img=document.createElement("img");
			if(product.savingOption==0)
				img.src=product.url;
			else
				img.src=`data:image/jpeg;base64,${product.imageBase64}`;
			img.alt="couldn't load image";
			img.onclick=function(){
				if(product.productId)
					window.location.href=`/user/showProduct/${product.productId}`;
				else{
					document.getElementById("message").innerHTML="That product is no longer available!";
					setTimeout(() => {
					       document.getElementById("message").style.display = 'none';
					    }, 10000);
					
				}
			}
			
			const productInfo=document.createElement("div");
			productInfo.id="productInfo";
			productInfo.innerHTML=`<span>${product.productName}</span><br><span>${product.price} RON</span>`;
			
			newProduct.appendChild(img);
			newProduct.appendChild(productInfo);
			productList.appendChild(newProduct);
		});
		const product=document.createElement("div");
		product.id="product";
		
		const orderInfo=document.createElement("div");
		orderInfo.id="orderInfo";
		orderInfo.innerHTML=`<div><strong>Total: </strong><span>${order.total} RON</span><br><strong>Order number:</strong>${999+order.id}</div>`;
		
		const buttons=document.createElement("div");
		buttons.classList.add("buttons");
		
		const removeOrder=document.createElement("button");
		removeOrder.innerHTML="remove from history";
		removeOrder.onclick=function(){
			fetch(`/user/cart/removeOrderFromHistory/${order.id}`,{method:"POST",})
			.then(response=>{
				if(response.ok) window.location.href="/user/allOrders";
			});
		}
		buttons.appendChild(removeOrder);
		orderInfo.appendChild(buttons);
		
		newOrder.appendChild(productList);
		newOrder.appendChild(orderInfo);
		
		if(order.status && order.status=="PENDING"){
			pendingCnt++;
			pendingOrderList.style.display="block";
			const finishOrder=document.createElement("button");
			finishOrder.innerHTML="finish order";
			finishOrder.onclick=function(){
				window.location.href=`/user/cart/checkout/${order.id}`;
			}
			buttons.appendChild(finishOrder);	
			const cancelOrder=document.createElement("button");
			cancelOrder.innerHTML="cancel order";
			cancelOrder.onclick=function(){
				fetch(`/user/cart/removeOrder/${order.id}`,{method:"POST",})
				.then(response=>{
					if(response.ok)
						window.location.href=`/user/allOrders`;
				});
			}
			buttons.appendChild(cancelOrder);			
			pendingOrderList.appendChild(newOrder);
		}
		else{
			paidCnt++;
			paidOrderList.style.display="block";
			paidOrderList.appendChild(newOrder);
		}
	});
	if(paidCnt==0){
			const paidMessage=document.createElement("div");
			paidMessage.classList.add("message");
			paidMessage.innerHTML="There are no paid orders at the moment!";
			paidOrderList.appendChild(paidMessage);
		}
		if(pendingCnt==0){
			const pendingMessage=document.createElement("div");
			pendingMessage.classList.add("message");
			pendingMessage.innerHTML="There are no pending orders at the moment!";
			pendingOrderList.appendChild(pendingMessage);
		}
	
}

getOrders();