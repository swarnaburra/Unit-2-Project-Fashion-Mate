# 👗 FashionMate Backend

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
