async function getProducts(){
	const response =await fetch("/admin/getAllProducts");
	const products=await response.json();
	addProducts(products);
}
getProducts();
function addProducts(products){
	const productList=document.getElementById("productList");
	productList.innerHTML='';
	products.forEach(product=>{
			const newProduct=document.createElement("div");
			newProduct.classList.add("product");
			
			const productImage=document.createElement("img");
			if(product.images && product.images.length>0){
				const image=product.images[0];
				if(image.savingOption==0) productImage.src=image.url;
				else productImage.src=`data:image/jpeg;base64,${image.imageBase64}`;
			}
			productImage.alt="Couldn't load image";
			productImage.onclick=function(){
				window.location.href=`/user/showProduct/${product.id}`;
			};
			newProduct.appendChild(productImage);
			
			const productInfo=document.createElement("div");
			productInfo.classList.add("productInfo");
			const info=document.createElement("div");
			if(product.discount>0){
				info.innerHTML=`<span>${product.name}</span><br><span style="color:red;">${product.finalPrice} RON </span><span style="font-size:13px;text-decoration:line-through;">(${product.price})</span>`;
			}
			else info.innerHTML = `<span>${product.name}</span><br>${product.price} RON`;					
			productInfo.appendChild(info);
			const buttons=document.createElement("div");
			buttons.classList.add("buttons");
			
			const deleteButton=document.createElement("button");
			deleteButton.innerHTML='<i class="fa-solid fa-trash"></i>';
			deleteButton.onclick=function(){
				fetch(`/admin/deleteProduct/${product.id}`,{
					method:"DELETE",
				})
				.then(response=>{
					if(response.ok) window.location.href="/admin/products";
				});
			};
			buttons.appendChild(deleteButton);
			const editButton=document.createElement("button");
			editButton.innerHTML='edit';
			editButton.onclick=function(){
				fetch(`/admin/editProduct/${product.id}`)
				.then(response=>{
					if(response.ok) window.location.href=`/admin/editProduct/${product.id}`;
				});
			};
			buttons.appendChild(editButton);
			productInfo.appendChild(buttons);
			newProduct.appendChild(productInfo);
			//newProduct.appendChild(buttons);
			productList.appendChild(newProduct);
		});
}

document.getElementById("searchForm").addEventListener("submit",function(event){
	event.preventDefault();
	const keyword=document.getElementById("keyword").value;
	fetch(`/admin/getProductsByKeyword/${keyword}`)
	.then(response=>{
		if(response.ok) return response.json();
	})
	.then(products=>{
		console.log(products);
		addProducts(products);
	});
});

const message=localStorage.getItem("added");
if(message){
	localStorage.removeItem("added");
	document.getElementById("message").innerHTML=message;
}

