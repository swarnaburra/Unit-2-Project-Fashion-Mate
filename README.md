
# <img width="50" height="50" alt="fm-logo" src="https://github.com/user-attachments/assets/a632cf39-5489-424d-8fae-a75803fb379c" /> FashionMate FrontEnd

FashionMate is a simple fashion recommendation app built using React and Google GenAI.
Users can upload an image, get a Yay/Nay rating, and receive outfit suggestions. The app also includes a small fashion quiz and a review form.
Features include:
- Upload an outfit image
- AI-powered fashion feedback (YAY/NAY + improvement tips via Gemini)
- View style and color suggestions
- Fashion Tips on the HomePage
- User SignUp and Login
- Review form for the user
- Fashion quiz using props
- Simple and clean UI
- Deployed on Netlify

Tech Used
React
Java
SpringBoot
Gemini AI
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

<img width="2506" height="1452" alt="FashionQuiz" src="https://github.com/user-attachments/assets/ef13defe-9a41-4c8a-a0e9-a35d8f1f4a8c" />

**About page with details on FashionMateApp Team details, App features and Review from the user**
<img width="3148" height="1454" alt="About-Page" src="https://github.com/user-attachments/assets/f08e4cfb-38d2-44ff-bd9b-980155ec1e68" />

**Review Form**

<img width="1326" height="974" alt="review" src="https://github.com/user-attachments/assets/6f4871ef-165b-4ec6-a7d2-5dd6ef67a88f" />


# <img width="50" height="50" alt="fm-logo" src="https://github.com/user-attachments/assets/d5f8e90b-38be-4560-8af8-c49376a3afbe" /> **FashionMate Backend**

## 📌 **Project Description**

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

- User registration & login (numeric user ID returned and stored client-side)
- **StyleLens**: upload an outfit photo with occasion/age/style preference, get YAY/NAY
  feedback and improvement tips from Gemini
- Display trending styles using **GlamUp**
- Client-side Fashion Quiz that matches a chosen style + color to curated outfit images
  (no backend call)
- Submit a review and delete any submitted review
- Display Fashion-Tip on the homepage
- REST API endpoints for frontend integration

---

## 🔌 API Endpoints

- `POST /api/users` — create a user record directly (no validation)
- `POST /api/users/signup` — register a new user, returns the new numeric user ID
- `PUT /api/users/signin` — log in with email/password, returns the numeric user ID
- `GET /api/reviews/{userId}` — list a user's reviews
- `POST /api/reviews/{userId}` — submit a review for a user
- `DELETE /api/reviews/{userId}/review/{reviewId}` — delete a review by ID
- `POST /api/stylelens/processImage/{userId}` — analyze an outfit image with Gemini
- `GET /api/glamup` — get the trending style + images
- `GET /api/outfit-tip` — get a random fashion tip

---

## ▶️ Installation & Setup (Run Locally)

### 1️⃣ Clone the Repository

```bash
git clone https://github.com/swarnaburra/Unit-2-Project-Fashion-Mate.git
```

2️⃣ Open in IntelliJ IDEA

Import as a Maven project.

3️⃣ Create MySQL Database
CREATE DATABASE fashionmate_db;

4️⃣ Set required environment variables

`application.properties` reads secrets from environment variables — it never contains
real credentials. Before running the app, set:
- `DB_USERNAME` (defaults to `root` if unset)
- `DB_PASSWORD` — your local MySQL password
- `GEMINI_API_KEY` — a Gemini API key from Google AI Studio (https://aistudio.google.com/)

In IntelliJ: Run/Debug Configurations → Environment variables. From a shell:
```bash
export DB_PASSWORD=your_mysql_password
export GEMINI_API_KEY=your_gemini_api_key
```

5️⃣ Reload Maven Dependencies
6️⃣ Run the Spring Boot Application
7️⃣ Test Using Postman
http://localhost:8080

See `TESTING.md` for how to run the project's automated checks, and `FEATURES.md` for the backlog of planned improvements.

🎨 Wireframes

Figma Design:
https://www.figma.com/design/FCoH5rkNKswlUQsyZqWQ26/FashionMate

🗂️ ER Diagram

Lucidchart ERD:
https://lucid.app/lucidchart/2d663294-6ded-4e80-a7de-dc7e11b01d04

🚧 Future Improvements

Add Two Factor Authentication for the Security of the User profile safety

Add Role-based access (Admin/User)

Add Recommend Me Button in StyleLens for user with New Outfit Ideas

Add Marketplace and Pricing for the recommended outfits in case User wants to buy similar outfits.


👩‍💻 Author

Swarna Burra
Unit 2 Project – Backend Development
