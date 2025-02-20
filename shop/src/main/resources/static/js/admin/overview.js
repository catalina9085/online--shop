async function getOverviewInfo(){
	try{
		const response=await fetch("/admin/getOrdersInfo");
		const info=await response.json();
		addNumbers(info.soldProductsCnt,info.paidOrdersCnt,info.total);
		addProducts(info.bestSellers);
		addOrders(info.allOrders);
	}catch(error){
		console.log(error.message);
	}
}
getOverviewInfo();

function addNumbers(soldProducts,paidOrders,total){
	document.getElementById("soldProducts").innerHTML=soldProducts;
	document.getElementById("placedOrders").innerHTML=paidOrders;
	document.getElementById("total").innerHTML=Number(total).toFixed(2)+" RON";
}


function addProducts(products){
	const productList=document.getElementById("productList");
	products.forEach(product=>{
		const productContainer=document.createElement("div");
		productContainer.id="productContainer";
		const img=document.createElement("img");
		if(product.images && product.images.length>0){
			const image=product.images[0];
			if(image.savingOption==0) img.src=image.url;
			else img.src=`data:image/jpeg;base64,${image.imageBase64}`;
		}
		img.alt="Couldn't load image!";
		img.onclick=function(){
			window.location.href=`/user/showProduct/${product.id}`;
		};
		
		const info=document.createElement("div");
		info.innerHTML=`<h4 style="margin:0;">${product.name}</h4>${product.description}<br>${product.price} RON`;
		
		productContainer.appendChild(img);
		productContainer.appendChild(info);
		productList.appendChild(productContainer);
	});
}

function addOrders(orders){
	const orderList=document.getElementById("orderList");
	orders.forEach(order=>{
		const orderContainer=document.createElement("div");
		orderContainer.id="orderContainer";
		const imagesContainer=document.createElement("div");
		imagesContainer.id="imagesContainer";
		order.products.forEach(product=>{
			const img=document.createElement("img");
			if(product.savingOptions==0) img.src=product.url;
			else img.src=`data:image/jpeg;base64,${product.imageBase64}`;
			img.alt="Couldn't load image!";
			img.onclick=function(){
				window.location.href=`/user/showProduct/${product.productId}`;
			};
			imagesContainer.appendChild(img);
		});
		const orderInfo=document.createElement("div");
		orderInfo.innerHTML=`
			<strong>Total: </strong>${order.total.toFixed(2)} RON<br>
			<strong>Status: </strong>${order.status}<br>`;
		
		orderContainer.appendChild(imagesContainer);
		orderContainer.appendChild(orderInfo);
		orderList.appendChild(orderContainer);
		
	});
}