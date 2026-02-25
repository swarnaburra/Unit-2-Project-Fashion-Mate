import BackToHome from "./BackToHome";
import "./FashionQuiz.css";

export default function FashionQuiz({style, color}) {
  const styleImages = {
    Casual: "/casual.jpg",
    Business: "/business.jpg",
    Trendy: "/trendy.jpg",
    Chic: "/chic.jpg",
  };

  const colorImages = {
    Black: "/black.jpg",
    Beige: "/beige.jpg",
    "Bright Colors": "/bright.jpg",
  };

  return (
    <div style={{ padding: "1rem" }}>
      <div style={{ marginTop: "20px" }}>
        <h2>Your Fashion Picks</h2>

        <div className="result-grid">
          <div>
            <h3>Style: {style}</h3>
            <img
              src={styleImages[style]}
              alt={style}
              className="result-image"
            />
          </div>

          <div>
            <h3>Color: {color}</h3>
            <img
              src={colorImages[color]}
              alt={color}
              className="result-image"
            />
          </div>
        </div>

        <div style={{ marginTop: "20px" }}>
          <BackToHome />
        </div>
      </div>
    </div>
  );
}
