import { Routes, Route } from 'react-router-dom';
import Header from "./components/Header";
import Footer from "./components/Footer";
import Home from "./components/Home";
import StyleLens from "./components/StyleLens";
import GlamUp from "./components/GlamUp";
import About from "./components/About";
import Signup from "./components/Signup";
import Login from "./components/Login";
import ProtectedRoute from "./components/ProtectedRoute";
import "./App.css"
import FashionQuizForm from "./components/FashionQuizForm";
import { UserProvider } from "./context/UserContext";


  function App() {
    return (
      <UserProvider>
        <div className="App">
          <Header />
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/signup" element={<Signup />} />
            <Route path="/login" element={<Login />} />
            <Route path="/stylelens" element={<ProtectedRoute element={<StyleLens />} />} />
            <Route path="/fashionquiz" element={<ProtectedRoute element={<FashionQuizForm />} />} />
            <Route path="/glamup" element={<ProtectedRoute element={<GlamUp />} />} />
            <Route path="/about" element={<ProtectedRoute element={<About />} />} />
          </Routes>

          <Footer />
        </div>
      </UserProvider>
  );

}

export default App;