import React, { useState } from "react";
import "./Header.css";
import { FaCamera, FaQuestionCircle, FaInfoCircle } from "react-icons/fa";

function Header() {
  const [isOpen, setIsOpen] = useState(false);

  function toggleMenu() {
    return setIsOpen(!isOpen);
  }

  return (
    <header className="header">

      {/*Adding logo to the top left of the webpage */}
      <div className="logo">
        <img src="/fm-logo.png" alt="FashionMate logo" />
      </div>

      {/* Adding Hamburger menu in the right corner of the webpage */}
      <div className="hamburger" onClick={toggleMenu}>
        <div className={`line ${isOpen ? "open" : ""}`}></div>
        <div className={`line ${isOpen ? "open" : ""}`}></div>
        <div className={`line ${isOpen ? "open" : ""}`}></div>
      </div>

      {/* SLIDE-OUT MENU */}
      <nav className={`nav-links ${isOpen ? "active" : ""}`}>
        <a href="/stylelens">
          <FaCamera size={20} /> StyleLens
        </a>
        <a href="/fashionquiz">
          <FaQuestionCircle size={20} /> FashionQuiz
        </a>
        <a href="/about">
        <FaInfoCircle size={20} /> About
        </a>
      </nav>

    </header>
  );
}

export default Header;
