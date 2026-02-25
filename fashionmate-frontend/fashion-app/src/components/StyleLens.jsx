import { Link } from "react-router-dom";
import StyleLensUpload from "./StyleLensUpload";
import BackToHome from "./BackToHome";

export default function StyleLens() {
  return (
    
    <div style={{ textAlign: "center", marginTop: "2rem" }}>
      <h2>StyleLens</h2>
      <p>Get personalized fashion tips and trends here!</p> <StyleLensUpload />

        <BackToHome></BackToHome>
    </div>
    
  );
}
