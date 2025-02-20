async function getUsers(){
	const response=await fetch("/admin/getAllUsers");
	const users=await response.json();
	const userList=document.getElementById("userList");
	
	users.forEach(user=>{
		console
		const newUser=document.createElement("div");
		newUser.classList.add("newUser");
		const USERNAME=document.createElement("div");
		USERNAME.innerHTML=`${user.username}`;
		const EMAIL=document.createElement("div");
		EMAIL.innerHTML=`${user.email}`;
		const ROLE=document.createElement("div");
		ROLE.innerHTML=`${user.role==="ROLE_USER" ? "USER" : "ADMIN"}`;
		const ACTION=document.createElement("div");
		const userButtons=document.createElement("div");
		const roleButton=document.createElement("button");
		if(user.role==="ROLE_USER") roleButton.innerHTML="change role to admin";
		else if(user.role==="ROLE_ADMIN")roleButton.innerHTML="change role to user";
		roleButton.onclick=function(){
			fetch(`/admin/changeUserRole/${user.id}`,{method:"POST",})
			.then(response=>{
				if(response.ok) window.location.href="/admin/users";
			});
		};
		
		const deleteButton=document.createElement("button");
		deleteButton.innerHTML='<i class="fa-solid fa-trash"></i>';
		deleteButton.onclick=function(){
			fetch(`/admin/deleteUser/${user.id}`,{
				method:"DELETE",
			})
			.then(response=>{
				if(response.ok) window.location.href="/admin/users";
			});
		};
		ACTION.appendChild(roleButton);
		ACTION.appendChild(deleteButton);
		newUser.appendChild(USERNAME);
		newUser.appendChild(EMAIL);
		newUser.appendChild(ROLE);
		newUser.appendChild(ACTION);
		userList.appendChild(newUser);
	});
}

getUsers();