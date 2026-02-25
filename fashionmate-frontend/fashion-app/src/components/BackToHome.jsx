import React from "react";
import { Link } from "react-router-dom";

export default function BackToHome () {
    return (
        <Link to="/"
        style={{
          display: "inline-block",
          marginTop: "1rem",
          padding: "0.5rem 1rem",
          background: "#8e131bff",
          color: "white",
          borderRadius: 5,
          textDecoration: "none",
          zIndex: 9999
        }}
      >
        ← Back to Home
        
      </Link>
    )

}
