const pathSegments = window.location.pathname.split('/');
const picId = pathSegments[pathSegments.length - 2];
const productId = pathSegments[pathSegments.length - 1];
async function getPicture(){
	const response=await fetch("/user/products/getPictureRequest/"+picId);
	const pic=await response.json();
	console.log(pic);
	const imgElement = document.createElement('img');
	if(pic.savingOption==0) imgElement.src = pic.url;
	else imgElement.src=`data:image/jpeg;base64,${pic.imageBase64}`;
	imgElement.alt ="couldn't load image";
	
	document.getElementById("imageContainer").appendChild(imgElement);
	
}

getPicture();

document.getElementById("close").addEventListener("click",function(){
	window.location.href="/user/showProduct/"+productId;
});