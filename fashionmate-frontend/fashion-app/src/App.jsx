import { Routes, Route } from 'react-router-dom';
import Header from "./components/Header";
import Footer from "./components/Footer";
import Home from "./components/Home";
import StyleLens from "./components/StyleLens";
import GlamUp from "./components/GlamUp";
import About from "./components/About";
import "./App.css"
import FashionQuizForm from "./components/FashionQuizForm";



  function App() {
    return (
<div className="App">
  <Header />
  <Routes>
    <Route path="/" element={<Home />} />
    <Route path="/stylelens" element={<StyleLens />} />
    <Route path="/fashionquiz" element={<FashionQuizForm />} />
    <Route path="/glamup" element={<GlamUp />} />
    <Route path="/about" element={<About />} />
  </Routes>

  <Footer />
</div>
  );

}

export default App;