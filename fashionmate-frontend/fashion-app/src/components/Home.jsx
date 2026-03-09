import React from "react";
import { useNavigate } from "react-router-dom";
import { useUser } from "../context/UserContext";
import "./Home.css";

function Home() {
  const navigate= useNavigate();
  const { userId } = useUser();

  const handleGetStarted = () => {
    if (userId) {
      navigate("/glamup");
    } else {
      navigate("/signup");
    }
  };

  return (
    <div className="home-container">
      <div className="hero-content">
        <h1 className="app-name">FashionMate</h1>
        <p className="tagline">Discover your perfect style.</p>

        <button className="cta-button"
        onClick={handleGetStarted}
        >
          {userId ? "Get Started" : "Sign Up to Get Started"}
          
        </button>
      </div>
    </div>
  );
}

export default Home;
