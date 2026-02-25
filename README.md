
# <img width="50" height="50" alt="fm-logo" src="https://github.com/user-attachments/assets/a632cf39-5489-424d-8fae-a75803fb379c" /> FashionMate FrontEnd

FashionMate is a simple fashion recommendation app built using React and Google GenAI.
Users can upload an image, get a Yay/Nay rating, and receive outfit suggestions. The app also includes a small fashion quiz and a review form.
Features include:
-Upload an outfit image
-powered fashion feedback
-View style and color suggestions
-Fashion quiz using props
-Simple and clean UI
-Deployed on Netlify

Tech Used
React
Google GenAI
React Router
CSS

HomePage of FashionMate app showing Get Started button to navigate to GlamUp, FMLogo, Hamburger menu (with StyleLens, FashionQuiz, About)
<img width="3057" height="1399" alt="Untitled" src="https://github.com/user-attachments/assets/10a27239-25e6-4e82-93ba-ba7925d7bcf9" />

Get Started navigating into GlamUp for seasonal fashion tips 
<img width="3126" height="1437" alt="GlamUp" src="https://github.com/user-attachments/assets/06c61457-aa8c-42b2-b6cb-274214b36981" />


StyleLens for personalized fashion tips and trends using Gemini AI for analyzing the uploaded photograph and giving suggestions
<img width="3135" height="1395" alt="STYLELENS" src="https://github.com/user-attachments/assets/7d81cdd1-5bf8-4009-aaaf-da72976b4f7d" />

FashionQuiz for getting outfit by user answering quiz based on favorite color and preferred style of outfit
<img width="3149" height="1451" alt="fashionquiz" src="https://github.com/user-attachments/assets/401d9e24-48c8-4d69-8d48-e5384f1dc42a" />

About page with details on FashionMateApp Team details, App features and Review from the user
<img width="3144" height="1388" alt="About" src="https://github.com/user-attachments/assets/30053914-eb1d-4c6f-9d5f-65797807268b" />
# <img width="50" height="50" alt="fm-logo" src="https://github.com/user-attachments/assets/d5f8e90b-38be-4560-8af8-c49376a3afbe" /> FashionMate Backend

## 📌 Project Description

FashionMate is a fashion styling application designed to help users discover and manage their personal style. The backend is built using Spring Boot and provides RESTful APIs for user authentication, style preference management, outfit storage, quiz results, and trending fashion data. It connects to a MySQL database and serves as the core system powering the FashionMate application.![homepage](https://github.com/user-attachments/assets/5e64b1e2-5b7d-48a2-973a-385f72b461f7)


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
- Display reviews and ratings on homepage  
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

Implement JWT authentication

Add role-based access (Admin/User)

Improve validation and exception handling


👩‍💻 Author

Swarna Burra
Unit 2 Project – Backend Development
