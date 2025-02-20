
var socket = new SockJS('/chat');
var stompClient = Stomp.over(socket);

stompClient.connect({}, function (frame) {
    console.log('Connected: ' + frame);
    
 
    fetch('/user/products/getUser1')
        .then(response => response.json())
        .then(user => {
			if(!user) throw new Error();
            subscribeToChannels(user);
        })
        .catch(error => console.error('Error fetching user info:', error));
});


function subscribeToChannels(user) {
    const preferences = user.notifications; 
    if(preferences && preferences.viaPush && preferences.viaPush==true){
    	if(preferences.offers==true)
			stompClient.subscribe("/topic/offers",function(message){
		displayMessage(message.body);
		});
		if(preferences.newArrivals==true)
			stompClient.subscribe("/topic/newArrivals",function(message){
			displayMessage(message.body);
		});
		if(preferences.orderStatus==true)
			stompClient.subscribe(`/topic/orderStatus/${user.id}`,function(message){
			displayMessage(message.body);
		});
	}
}

function displayMessage(message) {
    const notification = document.getElementById('notification');
    notification.innerHTML = message;
	const notifContainer=document.getElementById("notifContainer");
	notifContainer.style.display="flex";
	notifContainer.style.justifyContent="space-between";
	notifContainer.style.alignItems="center";

}

document.getElementById("hideNotification").addEventListener("click",function(){
	const notifContainer=document.getElementById("notifContainer");
	notifContainer.style.display="none";
});