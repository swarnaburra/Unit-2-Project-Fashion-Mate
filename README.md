
# <img width="50" height="50" alt="fm-logo" src="https://github.com/user-attachments/assets/a632cf39-5489-424d-8fae-a75803fb379c" /> FashionMate FrontEnd

FashionMate is a simple fashion recommendation app built using React and Google GenAI.
Users can upload an image, get a Yay/Nay rating, and receive outfit suggestions. The app also includes a small fashion quiz and a review form.
Features include:
-Upload an outfit image
-powered fashion feedback
-View style and color suggestions
-Fashion Tips on the HomePage
-User SignUp and Login
-Review form for the user
-Fashion quiz using props
-Simple and clean UI
-Deployed on Netlify

Tech Used
React
Java
SpringBoot
Google GenAI
React Router
CSS
HTML

**HomePage of FashionMate App** showing Get Started button to navigate to SignUp to Get Started Button to access - GlamUp, FMLogo, Hamburger menu (with StyleLens, FashionQuiz, About).FASHIONTIP is an additional feature displayed on the HomePage to make app appealing for the Users.

<img width="2444" height="1398" alt="fashionmate" src="https://github.com/user-attachments/assets/3dd71359-8add-43f1-9e46-56f56c37c9bd" />


**SignUp-Login Page**

<img width="2504" height="1442" alt="SignUp-Login page" src="https://github.com/user-attachments/assets/4667c7fd-0b4c-41f8-9de3-808c531cb42a" />


<img width="2503" height="1455" alt="LoginPage" src="https://github.com/user-attachments/assets/bc6f2043-281e-4b86-abc5-5fd525d2e532" />


**Get Started navigating into GlamUp for seasonal fashion tips **

<img width="2509" height="1450" alt="Glamup" src="https://github.com/user-attachments/assets/e99bf766-6559-4c56-ac3f-1ab165cb5ec3" />

**StyleLens for personalized fashion tips and trends using Gemini AI for analyzing the uploaded photograph and giving suggestions**

<img width="1326" height="1409" alt="StyleLens" src="https://github.com/user-attachments/assets/e7a4add0-dd0d-4dcb-80b0-138e6b13d155" />


**FashionQuiz for getting outfit by user answering quiz based on favorite color and preferred style of outfit**

<img width="3149" height="1451" alt="fashionquiz" src="https://github.com/user-attachments/assets/401d9e24-48c8-4d69-8d48-e5384f1dc42a" />

**About page with details on FashionMateApp Team details, App features and Review from the user**

<img width="1326" height="1116" alt="Aboutpage" src="https://github.com/user-attachments/assets/413c8ae4-1a4a-429f-82d3-c755ee0de786" />

**Review Form**

<img width="1326" height="974" alt="review" src="https://github.com/user-attachments/assets/6f4871ef-165b-4ec6-a7d2-5dd6ef67a88f" />


# <img width="50" height="50" alt="fm-logo" src="https://github.com/user-attachments/assets/d5f8e90b-38be-4560-8af8-c49376a3afbe" /> FashionMate Backend

## 📌 Project Description

FashionMate is a fashion styling application designed to help users discover and manage their personal style. The backend is built using Spring Boot and provides RESTful APIs for user authentication, style preference management, outfit storage, quiz results, and trending fashion data. It connects to a MySQL database and serves as the core system powering the FashionMate application.


---

## 🛠️ Tech Stack

- Java
- Spring Boot
- Spring Web
- Spring Data JPA
- MySQL
- Maven
- Hibernate
- Postman (API testing)
- Spring DevTools

---

## 🚀 Features

- User registration & login  
- Save user fashion style (masculine / feminine / etc.)  
- Generate outfit ideas using **StyleLens**  
- Display trending styles using **GlamUp**  
- Style quiz submission (Fashion Quiz)  
- Reviews done by the user and delete the last submitted review
- Display Fashion-Tip on the homepage  
- REST API endpoints for frontend integration  

---

## 🔌 API Endpoints

- `POST /users/register`
- `POST /users/login`
- `GET /outfits`
- `POST /quiz`

---

## ▶️ Installation & Setup (Run Locally)

### 1️⃣ Clone the Repository

```bash
git clone https://github.com/swarnaburra/fashionmate-backend.git
2️⃣ Open in IntelliJ IDEA

Import as a Maven project.

3️⃣ Create MySQL Database
CREATE DATABASE fashionmate_db;
4️⃣ Configure application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/fashionmate_db
spring.datasource.username=your_mysql_username
spring.datasource.password=your_mysql_password
spring.jpa.hibernate.ddl-auto=update
5️⃣ Reload Maven Dependencies
6️⃣ Run the Spring Boot Application
7️⃣ Test Using Postman
http://localhost:8080

🎨 Wireframes

Figma Design:
https://www.figma.com/design/FCoH5rkNKswlUQsyZqWQ26/FashionMate

🗂️ ER Diagram

Lucidchart ERD:
https://lucid.app/lucidchart/2d663294-6ded-4e80-a7de-dc7e11b01d04

🚧 Future Improvements

Add Two Factor Authentication for the Security of the User profile safety

Add role-based access (Admin/User)

Add Recommend Me Button in StyleLens for user with New Outfit Ideas

Add marketplace and pricing for the recommended outfits in case User wants to buy similar outfits.


👩‍💻 Author

Swarna Burra
Unit 2 Project – Backend Development
