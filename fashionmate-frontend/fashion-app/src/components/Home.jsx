import React from "react";
import { useNavigate } from "react-router-dom";
import "./Home.css";

function Home() {
  const navigate= useNavigate();
  return (
    <div className="home-container">
      <div className="hero-content">
        <h1 className="app-name">FashionMate</h1>
        <p className="tagline">Discover your perfect style.</p>

        <button className="cta-button"
        onClick={() => navigate("/glamup")}
        >
          Get Started
          
          </button>
      </div>
    </div>
  );
}

export default Home;
