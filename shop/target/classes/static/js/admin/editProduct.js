const pathSegments = window.location.pathname.split('/');
const productId = pathSegments[pathSegments.length - 1];

async function getCategories(){
	const response=await fetch("/user/products/getAllCategories");
	const categories=await response.json();
	const productCategory=document.getElementById("productCategory");
	categories.forEach(category=>{
		const option=document.createElement("option");
		option.value=category.name;
		option.textContent=category.name;
		productCategory.appendChild(option);
	});
}

getCategories();

async function getProduct(){
	
	const response =await fetch(`/user/products/getProduct/${productId}`);
	const product=await response.json();
	const productContainer=document.getElementById("productContainer");
	
	const imagesContainer=document.getElementById("imagesContainer");
	
	if(product.images){
	product.images.forEach(image=>{
		const imgContainer=document.createElement("div");
		imgContainer.classList.add("imgContainer");
		const img=document.createElement("img");
		if(image.savingOption==0) img.src=image.url;
		else img.src=`data:image/jpeg;base64,${image.imageBase64}`;
		img.alt=product.name;

		img.onclick=function(event){
			event.stopPropagation();
			window.location.href=`/user/picture/${image.id}/{product.id}`;
		}
		const imgDeleteButton=document.createElement("button");
			imgDeleteButton.innerHTML='x';
			imgDeleteButton.classList.add("deleteButton");
			imgDeleteButton.onclick=function(event){
				event.stopPropagation();
				fetch("/admin/deleteImage",{
					method:"DELETE",
					headers:{
						"Content-type":"application/json"
					},
					body:JSON.stringify({imageId:image.id,productId})
				})
				.then(response=>{
					if(response.ok) window.location.href=`/admin/editProduct/${productId}`;
				});
			};
		imgContainer.appendChild(img);
		imgContainer.appendChild(imgDeleteButton);
		imagesContainer.appendChild(imgContainer);
		
	});
	
	}
	const productInfo=document.getElementById("productInfo");
	productInfo.innerHTML=`
	<p><strong>Name:</strong>${product.name}</p>
	<p><strong>Description:</strong>${product.description}<p>
	<p><strong>Category:</strong>${product.category?product.category.name:""}<p>
	<p><strong>Stock:</strong>${product.stock}</p>
	<p><strong>Discount:</strong>${product.discount}</p>`;
	
	const productPrice = document.createElement("p");
	if(product.discount>0){
		productPrice.innerHTML=`<strong>Price: </strong><span style="color:red;">${product.finalPrice} RON </span><span style="font-size:16px;text-decoration:line-through;">(${product.price})</span>`;
	}
	else
		productPrice.innerHTML = `<strong>Price: </strong>${product.price} RON`;	
	productInfo.appendChild(productPrice);					
	
	const reviewsInfo=document.getElementById("reviewsInfo");
	reviewsInfo.innerHTML = `(${product.reviews.length})&nbsp;&nbsp;&nbsp;&nbsp;<span class="star2">★</span> ${product.rating.toFixed(2)}`;

	const reviewContainer=document.getElementById("reviews");
	product.reviews.forEach(review=>{
		const reviewElement=document.createElement("div");
		reviewElement.classList.add("reviewElement");
		const line1=document.createElement("div");
		line1.classList.add("line1");
		const date = new Date(review.createdAt);
		let stars="";
		for(let i=0;i<review.rating;i++)
			stars+="★";
		
		line1.innerHTML=`<div><strong>Username</strong>:${review.postedBy}     <span id="stars">${stars}</span></div><div id="date">${date.toLocaleDateString("ro-RO")}</div>`;
		const line2=document.createElement("div");
		line2.classList.add("line2");
		line2.innerHTML=`<div>${review.text}</div>`;
		reviewElement.appendChild(line1);
		reviewElement.appendChild(line2);
		const deleteButton=document.createElement("button");
			deleteButton.innerHTML='<i class="fa-solid fa-trash"></i>';
			deleteButton.onclick=function(){
				fetch(`/admin/deleteReview/${review.id}`,{
					method:"DELETE"
				})
				.then(response=>{
					if(response.ok) window.location.href=`/admin/editProduct/${productId}`;
				});
			};
		line2.appendChild(deleteButton);
		reviewElement.appendChild(line1);
		reviewElement.appendChild(line2);
		reviewContainer.appendChild(reviewElement);
	});
	
	document.getElementById("name").value=product.name;
	document.getElementById("description").value=product.description;
	if(product.category){
		document.getElementById("defaultOption").value=product.category.name;
		document.getElementById("defaultOption").textContent=product.category.name;
	}
	document.getElementById("price").value=product.price;
	document.getElementById("stock").value=product.stock;
	document.getElementById("discount").value=product.discount;
}
getProduct();

document.getElementById("edit").addEventListener("click",function(event){
	event.preventDefault();
	toggle();

});

function toggle(){
	const productInfo=document.getElementById("info");
	const hidden=document.getElementById("hidden");
	if(hidden.style.display==="none"){
		productInfo.style.display="none";
		hidden.style.display="block";
	}
	else{
		productInfo.style.display="block";
		hidden.style.display="none";
	}
}
document.getElementById("editInfoButton").addEventListener("click",function(event){
	event.preventDefault();
	const name=document.getElementById("name").value;
	const description=document.getElementById("description").value;
	const price=document.getElementById("price").value;
	const stock=document.getElementById("stock").value;
	const newCategory=document.getElementById("newCategory").value;
	const productCategory=document.getElementById("productCategory").value;
	const discount=document.getElementById("discount").value;
	if(!name || !description ||!stock||!price){
		alert("Fill all fields!");
		return;
	}
	
	fetch("/admin/editProductInfo",{
		method:"POST",
		headers:{
			'Content-type':"application/json"
		},
		body:JSON.stringify({productId,name,description,price,stock,newCategory,productCategory,discount})
	})
	.then(response=>{
		if(response.ok) window.location.href=`/admin/editProduct/${productId}`;
	});

});

document.getElementById("addNewFilesButton").addEventListener("click",function(event){
	event.preventDefault();
	const files=document.getElementById("files").files;
	
	const cloud=document.getElementById("cloud").checked;
		const database=document.getElementById("database").checked;
		if(!cloud && !database){
			alert("Choose a saving option!");
			return;
		}
		
	const formData=new FormData();
	for(let i=0;i<files.length;i++)
		formData.append("files",files[i]);
	
	formData.append("productId",productId);
	let url="/admin/addNewFiles";
	if(cloud) url+="/0";
	else if(database) url+="/1";
	fetch(url,{
		method:"POST",
		body:formData
	})
	.then(response=>{
		if(response.ok) window.location.href=`/admin/editProduct/${productId}`;
		else{
			return response.text().then(text=>{throw new Error(text);});
		}
	})
	.catch(error=>{
		window.alert("There's been an error! Make sure the files are not too large!");
	});

});

document.getElementById("cloud").addEventListener("click",function(){
	document.getElementById("database").checked=false;
});
document.getElementById("database").addEventListener("click",function(){
	document.getElementById("cloud").checked=false;
});
