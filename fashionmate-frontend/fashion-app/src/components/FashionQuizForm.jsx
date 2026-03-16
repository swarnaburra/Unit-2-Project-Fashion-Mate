import { useState } from "react";
import FashionQuiz from "./FashionQuiz";

export default function FashionQuizForm() {
  const [selectedStyle, setSelectedStyle] = useState("");
  const [selectedColor, setselectedColor] = useState("");
  const [submitted, setSubmitted] = useState(false);
  const [warning, setWarning] = useState("");

  // Handle submit button click
  const handleSubmit = (e) => {
    e.preventDefault();
    if (!selectedStyle || !selectedColor) {
      setWarning("Please select both style and color before submitting.");
      return;
    }
    setSubmitted(true);
  };
  return (
    <div
      style={{
        minHeight: "100vh",
        backgroundImage:
          "linear-gradient(rgba(182, 155, 178, 0.9), rgba(182, 155, 178, 0.9)), url('/homepage.jpeg')",
        backgroundSize: "cover",
        backgroundPosition: "center",
        backgroundAttachment: "fixed",
        backgroundRepeat: "no-repeat",
        padding: "2rem",
        fontFamily: "sans-serif",
      }}
    >
      <form onSubmit={handleSubmit}>
        <h3>1. Choose your preferred Style</h3>

        <div className="quiz-grid">
          {["Casual", "Business", "Trendy", "Chic"].map((style) => (
            <div
              key={style}
              className={`quiz-tile ${selectedStyle === style ? "selected" : ""}`}
              onClick={() => setSelectedStyle(style)}
            >
              <p>{style}</p>
            </div>
          ))}
        </div>

        <h3>2. Choose your favorite color</h3>
        <div className="quiz-grid">
          {["Black", "Beige", "Bright Colors"].map((color) => (
            <div
              key={color}
              className={`quiz-tile ${selectedColor === color ? "selected" : ""}`}
              onClick={() => setselectedColor(color)}
            >
              <p>{color}</p>
            </div>
          ))}
        </div>

        <button
          type="submit"
          style={{
            marginTop: "20px",
            padding: "10px 20px",
            fontSize: "16px",
            borderRadius: "8px",
            cursor: "pointer",
            backgroundColor: "rgb(120, 90, 102)",
            color: "white",
            border: "none",
          }}
        >
          Submit
        </button>

        {warning && (
          <p
            style={{ color: "crimson", fontWeight: "bold", marginTop: "10px" }}
          >
            {warning}
          </p>
        )}
      </form>

      {submitted && <FashionQuiz style={selectedStyle} color={selectedColor} />}
      
    </div>
    
  );
}
