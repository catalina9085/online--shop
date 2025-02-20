async function getProducts(){
		const response=await fetch("/user/wishList/getProducts");
		const products=await response.json();
		
		const productList=document.getElementById("productList");
		
		products.forEach(product=>{
			const newItem=document.createElement("div");
					newItem.classList.add("item");
			const PRODUCT=document.createElement("div");
			PRODUCT.classList.add("PRODUCT");
					const img=document.createElement("img");
					if(product.images &&product.images.length>0){
						const image=product.images[0];
						if(image.savingOption==0) img.src=image.url;
						else img.src=`data:image/jpeg;base64,${image.imageBase64}`;

					}
					img.alt="couldn't load image";
					img.onclick=function(){
						window.location.href=`/user/showProduct/${product.id}`;
					}
					PRODUCT.appendChild(img);
		
					
					const productInfo=document.createElement("div");
					if(product.discount>0)
						productInfo.innerHTML=`<h4 style="margin:0;">${product.name}</h4>${product.description}<br><span style="color:red;">${product.finalPrice} RON </span><span style="font-size:16px;text-decoration:line-through;">(${product.price})</span><br>${product.stock} left`;
					else 	productInfo.innerHTML=`<h4 style="margin:0;">${product.name}</h4>${product.description}<br>${product.price} RON<br>${product.stock} left`;

					const deleteButton=document.createElement("button");
					deleteButton.innerHTML='<i class="fa-solid fa-trash"></i>';
					deleteButton.classList.add("deleteButton");
					deleteButton.style.margin="20px";
					deleteButton.onclick=function(){
						fetch(`/user/wishList/delete/${product.id}`,{
							method:"DELETE"
						})
						.then(response=>{
							if(response.ok) window.location.href="/user/wishList";
							else{
								return response.text().then(errorMessage=>{throw new Error(errorMessage);});
							}
						})
						.catch(error=>{addError("We couldn't load products!");});
						
					};
					
					PRODUCT.appendChild(productInfo);
					newItem.appendChild(PRODUCT);
					newItem.appendChild(deleteButton);
					productList.appendChild(newItem);
		});
		
}

getProducts();

function addError(message){
	document.getElementById("errorMessage").innerHTML=message;
}