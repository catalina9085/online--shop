const stars=document.getElementsByClassName("star");
let ratingValue=0;
function rating(n){
	reset();
	let cls;
	if (n == 1) cls = "one";
	else if (n == 2) cls = "two";
	else if (n == 3) cls = "three";
	else if (n == 4) cls = "four";
	else if (n == 5) cls = "five";
	
	for (let i = 0; i < n; i++) {
	   stars[i].className = "star " + cls;
	}
	ratingValue=n;
}
function reset(){
	let i=0;
	while(i<5){
		stars[i].className="star";
		i++;
	}
}

async function getProduct(){
	
	const pathSegments = window.location.pathname.split('/');
	const productId = pathSegments[pathSegments.length - 1];
	
	const response =await fetch(`/user/products/getProduct/${productId}`);
	const product=await response.json();
	console.log(product);
	const productContainer=document.getElementById("productContainer");
	
	const imagesContainer=document.createElement("div");
	imagesContainer.classList.add("imagesContainer");
	
	product.images.forEach(image=>{
		console.log(image);
		const img=document.createElement("img");
		if(image.savingOption==0) img.src=image.url;
		else img.src=`data:image/jpeg;base64,${image.imageBase64}`;
		img.alt="Couldn't load image";
		imagesContainer.appendChild(img);
		img.onclick=function(){
			window.location.href="/user/picture/"+image.id+"/"+product.id;
		}
	});
	
	productContainer.appendChild(imagesContainer);
	
	const productInfo=document.createElement("div");
	productInfo.innerHTML=`<h4>${product.name}</h4><strong>Description: </strong>${product.description}<br>
							${product.category?"<strong>Category: </strong>"+product.category.name+"<br>":""}
							<strong>Stock: </strong>${product.stock}<br>`;
							const productPrice = document.createElement("span");
											if(product.discount>0){
												productPrice.innerHTML=`<span style="color:red;">${product.finalPrice} RON </span><span style="font-size:16px;text-decoration:line-through;">(${product.price})</span>`;
											}
											else
												productPrice.innerHTML = `${product.price} RON`;	
	productInfo.appendChild(productPrice);					
	productContainer.appendChild(productInfo);
	
	const cartButton=document.createElement("button");
		cartButton.innerHTML= 'add to cart';
		cartButton.style.display="inline";
		cartButton.onclick=function(){
			fetch("/user/cart/add/"+product.id,{
				method:"POST"
			});
		}
				
	const wishListButton=document.createElement("button");
		wishListButton.innerHTML= 'add to wishlist';
		wishListButton.style.display="inline";
		wishListButton.onclick=function(){
			fetch("/user/wishList/add/"+product.id,{
				method:"POST",
			});
		}
	
	const buttons=document.createElement("div");
	buttons.classList.add("buttons");
	buttons.appendChild(cartButton);
	buttons.appendChild(wishListButton); 
	productContainer.appendChild(buttons);

	const reviewsInfo=document.getElementById("reviewsInfo");
	reviewsInfo.innerHTML = `(${product.reviews.length})&nbsp;&nbsp;&nbsp;&nbsp;<span class="star2">★</span> ${product.rating.toFixed(2)}`;

	const reviewContainer=document.getElementById("reviews");
	product.reviews.forEach(review=>{
		const reviewElement=document.createElement("div");
		reviewElement.classList.add("reviewElement");
		const line1=document.createElement("div");
		line1.classList.add("line1");
		
		const ratingAndUsername=document.createElement("div");
		ratingAndUsername.classList.add("ratingAndUsername");
		const postedBy=document.createElement("div")
		postedBy.innerHTML=`<strong>Username</strong>:${review.postedBy}`;
		
		const ratingStars=document.createElement("div");
		for(let i=0;i<review.rating;i++){
			const star=document.createElement("span");
			star.classList.add("star2");
			star.innerHTML="★";
			ratingStars.appendChild(star);
		}
		ratingAndUsername.appendChild(postedBy);
		ratingAndUsername.appendChild(ratingStars);
		
		const date = new Date(review.createdAt);
		const dateInfo=document.createElement("div");
		dateInfo.id="date";
		dateInfo.innerHTML=`${date.toLocaleDateString("ro-RO")}`;
		
		line1.appendChild(ratingAndUsername);
		line1.appendChild(dateInfo);
		
		const line2=document.createElement("div");
		line2.classList.add("line2");
		line2.innerHTML=`<div>${review.text}</div>`;
		
		fetch("/user/products/getUser")
		.then(response=>{
			if(response.ok) return response.text();
		})
		.then(username=>{
			if(username==review.postedBy){
				const deleteButton=document.createElement("button");
				deleteButton.innerHTML='<i class="fa-solid fa-trash"></i>';
				deleteButton.onclick=function(){
					fetch(`/admin/deleteReview/${review.id}`,{
						method:"DELETE"
					})
					.then(response=>{
						if(response.ok) window.location.href=`/user/showProduct/${productId}`;
					});
				};
				line2.appendChild(deleteButton);
			}
		});
		reviewElement.appendChild(line1);
		reviewElement.appendChild(line2);
		reviewContainer.appendChild(reviewElement);
	});
}

document.getElementById("addReview").addEventListener("submit",function(){
	event.preventDefault();
	const pathSegments = window.location.pathname.split('/');
	const productId = pathSegments[pathSegments.length - 1];
	const review=document.getElementById("review").value;
	
	fetch("/user/products/addReview",{
		method:"POST",
		headers:{
			"Content-type":"application/json"
		},
		body:JSON.stringify({review,productId,ratingValue})
	})
	.then(response=>{
		if(response.ok) window.location.href=`/user/showProduct/${productId}`;
		else{
			return response.text().then(errorMessage=>{throw new Error(errorMessage);});
		}
	})
	.catch(error=>{addError("Couldn't post review.")});
});

getProduct();

function addError(message){
	document.getElementById("errorMessage").innerHTML=message;
}

