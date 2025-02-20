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

document.getElementById("addProductForm").addEventListener("submit", function(event){
    event.preventDefault();

    const name = document.getElementById("name").value;
    const description = document.getElementById("description").value;
    const price = document.getElementById("price").value;
    const stock = document.getElementById("stock").value;
    const files = document.getElementById("files").files;

	const newCategory=document.getElementById("newCategory").value;
	const productCategory=document.getElementById("productCategory").value;
    
	const cloud=document.getElementById("cloud").checked;
	const database=document.getElementById("database").checked;
	if(!cloud && !database){
		alert("Choose a saving option!");
		return;
	}
    const formData = new FormData();
    formData.append('name', name);
    formData.append('description', description);
    formData.append('price', price);
    formData.append('stock', stock);
	if(newCategory) formData.append("newCategory",newCategory);
	else formData.append("productCategory",productCategory);

   
    for (let i = 0; i < files.length; i++) {
        formData.append('files', files[i]);
    }
	let url="/admin/addNewProduct";
	
	if(cloud) url+="/0";
	else if(database) url+="/1";
	
    fetch(url, {
        method: "POST",
        body: formData
    })
    .then(response => {
        if (response.ok) {
			localStorage.setItem("added","Product successfully added!");
            window.location.href = "/admin/products"; 
        }
    })
    .catch(error => {
        document.getElementById("message").innerHTML=error.message;
    });
});

document.getElementById("cloud").addEventListener("click",function(){
	document.getElementById("database").checked=false;
});
document.getElementById("database").addEventListener("click",function(){
	document.getElementById("cloud").checked=false;
});