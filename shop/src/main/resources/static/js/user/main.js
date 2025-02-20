

document.getElementById("keywordForm").addEventListener("submit",function(event){
	event.preventDefault();
	const keyword=document.getElementById("keyword").value.trim();
	fetch(`/user/products/getProductsByKeyword/${keyword}`)
	.then(response=>{
		if(response.ok){
			return response.json();
		}
		else{
			return response.text().then(errorMessage=>{throw new Error(errorMessage);});
		}
	})
	.then(products=>{
		createMainPage(products);
	})
	.catch(error=>{addError("We couldn't load products!");});
});

function createMainPage(products){
	console.log(products);
	const productList = document.getElementById('productList');
	if(products.length==0)
		productList.innerHTML="There are no products at the moment!"
	else{
		const productList = document.getElementById('productList');
		productList.innerHTML="";
	        products.forEach(product => {

	            const listItem = document.createElement('div');
	            listItem.classList.add("item");	        
	            
	            if (product.images && product.images.length > 0) {
	                const imgElement = document.createElement('img');
	                if(product.images[0].savingOption==0) imgElement.src = product.images[0].url;
					else imgElement.src=`data:image/jpeg;base64,${product.images[0].imageBase64}`;
	                imgElement.alt ="couldn't load image";
	                imgElement.onclick=function(){
							 window.location.href=`/user/showProduct/${product.id}`;
						}
					listItem.appendChild(imgElement);
	
					}
	     
				const productAction=document.createElement("div");
				productAction.classList.add("productAction");
				const productInfo=document.createElement("div");
				productInfo.classList.add("productInfo");
			
	            const productName = document.createElement("div");
				productName.onclick=function(){
						window.location.href=`/user/showProduct/${product.id}`;
				}
				const productPrice = document.createElement("div");
	            productName.innerHTML = `<div id="productName">${product.name}</div>`;
				if(product.discount>0){
					productPrice.innerHTML=`<span style="color:red;">${product.finalPrice} RON </span><span style="font-size:13px;text-decoration:line-through;">(${product.price})</span>`;
				}
				else
					productPrice.innerHTML = `${product.price} RON`;
	            productInfo.appendChild(productName);
				productInfo.appendChild(productPrice);
				productAction.appendChild(productInfo);
	            
	            const cartButton = document.createElement("button");
	            cartButton.innerHTML = '<i class="fas fa-shopping-cart"></i>';
	            cartButton.onclick = function() {
	                fetch("/user/cart/add/" + product.id, { method: "POST" });
	            };
	            
	            const wishListButton = document.createElement("button");
				wishListButton.classList.add("wishListButton");
	            wishListButton.innerHTML = '<i class="fas fa-heart"></i>';
				fetch(`/user/wishList/existsById/${product.id}`)
				.then(response=>{return response.text();})
				.then(text=>{if(text=="Found"){
					wishListButton.classList.add("added")
				}
				});		
	            wishListButton.onclick = function() {
					wishListButton.classList.toggle("added");
					if (wishListButton.classList.contains("added")) {
					    fetch("/user/wishList/add/" + product.id, { method: "POST" });
					}
					else{
						fetch("/user/wishList/delete/" + product.id, { method: "DELETE" });
					}
	            };
				
	            productAction.appendChild(wishListButton);
				listItem.appendChild(productAction);
	            productList.appendChild(listItem);
});
}
}

async function getProducts(){
    try{
        const response = await fetch('/user/products/getAllProducts');
        const products = await response.json(); 
        createMainPage(products);
   
    } catch (error) {
        addError(error.message);
    }
}

function addError(message){
	document.getElementById("errorMessage").innerHTML=message;
}
getProducts();


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



document.getElementById("productCategory").addEventListener("change",function(){
	document.getElementById("up").style.color="black";
	document.getElementById("down").style.color="black";
	const productCategory=document.getElementById("productCategory").value;
	if(productCategory!="")
	fetch("/user/products/getProductsByCategory/"+productCategory)
	.then(response=>{
		if(response.ok) return response.json();
	})
	.then(products=>{
		createMainPage(products);
	});
	
	else getProducts();
});


document.getElementById("up").addEventListener("click",function(){
	const up=document.getElementById("up");
	const down=document.getElementById("down");
	up.style.color="black";
	down.style.color="gray";
	
	const productCategory=document.getElementById("productCategory").value;
	if(productCategory && productCategory!=""){
		fetch("/user/products/getProductsByCategoryAndPrice",{
			method:"POST",
			headers:{
				"Content-type":"application/json"
			},
			body:JSON.stringify({sortDirection:"cresc",productCategory:productCategory})
		})
		.then(response=>{
					if(response.ok) return response.json();
				})
				.then(products=>{
					createMainPage(products);
				});
	}
	else{
	fetch("/user/products/getSortedProducts/cresc")
	.then(response=>{
			if(response.ok) return response.json();
		})
		.then(products=>{
			createMainPage(products);
		});
		}
});

document.getElementById("down").addEventListener("click",function(){
	const up=document.getElementById("up");
	const down=document.getElementById("down");
	up.style.color="gray";
	down.style.color="black";
	const productCategory=document.getElementById("productCategory").value;
		if(productCategory && productCategory!=""){
			fetch("/user/products/getProductsByCategoryAndPrice",{
				method:"POST",
				headers:{
					"Content-type":"application/json"
				},
				body:JSON.stringify({sortDirection:"desc",productCategory:productCategory})
			})
			.then(response=>{
						if(response.ok) return response.json();
					})
					.then(products=>{
						createMainPage(products);
					});
		}
		else{
	fetch("/user/products/getSortedProducts/desc")
	.then(response=>{
			if(response.ok) return response.json();
		})
		.then(products=>{
			createMainPage(products);
		});
		}
});



document.getElementById("recommendSelect").addEventListener("change",function(){
	document.getElementById("up").style.color="black";
	document.getElementById("down").style.color="black";
	const recommendSelect=document.getElementById("recommendSelect").value;
	if(recommendSelect!="")
		fetch("/user/products/getRecommendedProducts/"+recommendSelect)
	.then(response=>{
		if(response.ok) return response.json();
	})
	.then(products=>{
		createMainPage(products);
	});
	else getProducts();
});



document.getElementById("menu").addEventListener("click",function(){
	document.getElementById("menuContainer").style.display="block";
});
document.getElementById("closeMenu").addEventListener("click",function(){
	document.getElementById("menuContainer").style.display="none";
});

const admin=document.getElementById("admin");
if(admin){
	admin.addEventListener("click",function(){
		const hiddenAdminSection=document.getElementById("hiddenAdminSection");
		if(hiddenAdminSection.style.display=="none")
			hiddenAdminSection.style.display="block";
		else hiddenAdminSection.style.display="none";
	});
	}

document.getElementById("settings").addEventListener("click",function(){
	const hiddenSettingsSection=document.getElementById("hiddenSettingsSection");
	if(hiddenSettingsSection.style.display=="none")
		hiddenSettingsSection.style.display="block";
	else hiddenSettingsSection.style.display="none";
});

document.getElementById("manageAccount").addEventListener("click",function(){
	const hiddenManageAccount=document.getElementById("hiddenManageAccount");
	if(hiddenManageAccount.style.display=="none")
		hiddenManageAccount.style.display="block";
	else hiddenManageAccount.style.display="none";
});

function addVerify(){
	fetch("/user/products/getUser1")
	.then(response=>{if(response.ok) return response.json();})
	.then(user=>{
		console.log(user.verified);
		if(user.verified==false){
			const verify=document.createElement("a");
			verify.textContent="> Verify your account";
			verify.href="/user/verify";
			document.getElementById("hiddenManageAccount").appendChild(verify);
		}
	})
}
addVerify();
