import { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import "./GlamUp.css";
import BackToHome from "./BackToHome";

export default function GlamUp() {
  const [glamupData, setGlamupData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchImages();
  }, []);

  const fetchImages = async () => {
    try {
      setLoading(true);
      const response = await fetch("http://localhost:8080/api/glamup");
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      
      const data = await response.json();
      setGlamupData(data);
      setError(null);
    } catch (err) {
      setError(err.message);
      console.error("Error fetching images:", err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <div className="glamup-container"><p>Loading images...</p></div>;
  if (error) return <div className="glamup-container"><p>Error: {error}</p></div>;
  if (!glamupData) return <div className="glamup-container"><p>No data available</p></div>;

  return (
    <div className="glamup-container">
      <h2 className="title" style={{ color: "rgb(31, 15, 27)" }}>GlamUp</h2>
      <p className="tagline">{glamupData.trendingStyle}</p>

      <div className="tiles">
        <div className="tile">
          <img 
            src={glamupData.imageUrl1} 
            alt={glamupData.altText1} 
            className="tile-image" 
          />
        </div>
        <div className="tile">
          <img 
            src={glamupData.imageUrl2} 
            alt={glamupData.altText2} 
            className="tile-image" 
          />
        </div>
        <div className="tile">
          <img 
            src={glamupData.imageUrl3} 
            alt={glamupData.altText3} 
            className="tile-image" 
          />
        </div>
      </div>

      <BackToHome />
    </div>
  );
}