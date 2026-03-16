import React, { useState, useEffect } from "react";

export default function OutfitTip() {
  const [outfittipData, setOutfittipData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchTip();
  }, []);

  const fetchTip = async () => {
    try {
      const response = await fetch("http://localhost:8080/api/outfit-tip");

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.text();
      setOutfittipData(data);
    } catch (err) {
      setError(err.message);
      console.error("Error fetching outfit tip:", err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <p>Loading fashion tip...</p>;
  if (error) return <p>Error: {error}</p>;
  if (!outfittipData) return <p>No tip available</p>;

  return (
    <div className="outfittip-container">
      <p className="fashion-tip">
        <strong>Fashion Tip:</strong>{" "}
        <strong>
          <em>"{outfittipData}"</em>
        </strong>
      </p>
    </div>
  );
}
