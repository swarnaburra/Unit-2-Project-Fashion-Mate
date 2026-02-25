import { Link } from "react-router-dom";
import "./GlamUp.css";
import BackToHome from "./BackToHome";

export default function GlamUp() {
  const images = [
    { src: "/fashion1.jpg", description: "Woman wearing a stylish fall outfit with scarf" },
    { src: "/fashion2.jpg", description: "Woman in chic winter fashion with statement necklace" },
    { src: "/fashion3.jpg", description: "Casual outfit for autumn with a hat accessory" }
  ];

  return (
    <div className="glamup-container">
      <h2 className="title">GlamUp</h2>
      <p className="tagline">Get Inspired with Seasons..!</p>

      <div className="tiles">
        {images.map((img, index) => (
          <div className="tile" key={index}>
            <img 
              src={img.src} 
              alt={img.description} 
              className="tile-image" 
            />
          </div>
        ))}
      </div>

      <BackToHome />
    </div>
  );
}
