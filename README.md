# Online Shop

A simple and easy-to-use online shop where users can browse products, add them to their wishlist or cart, and place orders. The admin can manage products and users to keep the store up to date.

## Table of Contents
- [Live Demo](#live-demo)
- [Usage](#usage)
- [User Features](#user-features)
- [Admin Features](#admin-features)
- [Technologies Used](#technologies-used)

## Live Demo

You can access the live demo of the application here:  
[Live Demo on Heroku](https://online--shop-485507e5e9cf.herokuapp.com/)

## Usage

### Test Accounts  
To test the application, you can use the following demo accounts:

**Admin account:**  
- **Username:** `admin`  
- **Password:** `admin`  

**PayPal Sandbox (for test transactions only):**  
- **Email:** demo101@personal.com  
- **Password:** psj@^I2I  

## User Features  

As a regular user, you can:  
- **Search for products** using keywords.  
- **Sort products** by price (ascending/descending), top-rated, new arrivals, or category.  
- **Leave reviews and ratings** on products.  
- **Customize your notification preferences** (email or in-app notifications).  
- **Add products to your wishlist** for future reference.  
- **Add products to your cart** and proceed to checkout.  
- **Place orders** and pay securely using PayPal.  
- **View your order history** to track past purchases.  
- **Manage your account** by changing your password or deleting it.  
- **Enter your shipment details** for accurate delivery.  
- **Verify your email using a code** for added security. 

## Admin Features  

As an admin, you can:  
- **Manage products:** Add, edit, or delete products in the store.  
- **Apply discounts** directly to products.  
- **Manage users:** Delete users or change their roles (e.g., promote to admin).  
- **View store analytics**, including:  
  - Total revenue generated.  
  - Total number of orders placed.  
  - Total number of products sold.  
  - Top 5 best-selling products.  
  - A list of all customer orders with details.  
- **Moderate product reviews:** Delete any customer reviews if necessary.  

## Technologies Used  

This project was built using the following technologies:

### Backend:
- **Spring Boot** – for the core backend functionality.  
- **Spring Security** – for authentication and user management.  
- **Spring Mail** – for sending email notifications.  
- **MySQL (Heroku Database)** – for storing application data.  
- **WebSockets** – for real-time notifications.  
- **PayPal Sandbox** – for processing test payments.  
- **Cloudinary** – for cloud-based image storage.  

### Frontend:
- **HTML, CSS, JavaScript** – for the user interface and interactions.  
