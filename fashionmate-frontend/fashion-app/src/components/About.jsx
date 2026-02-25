import React, { useState } from "react";
import BackToHome from "./BackToHome";

export default function About() {
  const [reviewMessage, setReviewMessage] = useState("");

  const team = [
    { id: 1, name: "Swarna Burra", role: "Founder & Designer" },
    { id: 2, name: "Loralai Gilmore", role: "Frontend Developer" },
    { id: 3, name: "Walter White", role: "Backend Developer" },
  ];

  const features = [
    "Seasonal outfit recommendations",
    "Fashion quiz to discover your style",
    "Save your favorite looks",
  ];

  const handleReviewSubmit = (e) => {
    e.preventDefault();
    setReviewMessage("Thank you for your review!");
    e.target.reset();
  };

  return (
    <div style={{ maxWidth: 800, margin: "2rem auto", fontFamily: "sans-serif" }}>
      <h2>About FashionMate</h2>

      <h3>Meet Our Team</h3>
      <table style={{ width: "100%", borderCollapse: "collapse", marginBottom: "2rem" }}>
        <thead>
          <tr style={{ background: "#8e131bff", color: "white" }}>
            <th style={{ padding: "0.5rem", border: "1px solid #ddd" }}>#</th>
            <th style={{ padding: "0.5rem", border: "1px solid #ddd" }}>Name</th>
            <th style={{ padding: "0.5rem", border: "1px solid #ddd" }}>Role</th>
          </tr>
        </thead>
        <tbody>
          {team.map((member) => (
            <tr key={member.id}>
              <td style={{ padding: "0.5rem", border: "1px solid #ddd" }}>{member.id}</td>
              <td style={{ padding: "0.5rem", border: "1px solid #ddd" }}>{member.name}</td>
              <td style={{ padding: "0.5rem", border: "1px solid #ddd" }}>{member.role}</td>
            </tr>
          ))}
        </tbody>
      </table>

      <h3>App Features</h3>
      <ul>
        {features.map((feature, idx) => (
          <li key={idx} style={{ marginBottom: "0.5rem" }}>
            {feature}
          </li>
        ))}
      </ul>

      {/* Review Form */}
      <section id="review" style={{ marginTop: "20px" }}>
        <h3>Leave A Review</h3>
        <form onSubmit={handleReviewSubmit} style={{ maxWidth: 400 }}>
          <label htmlFor="name">Name</label><br />
          <input
            type="text"
            id="name"
            name="name"
            placeholder="Your Name"
            required
            style={{ width: "100%", padding: "5px", marginBottom: "10px" }}
          /><br />

          <label htmlFor="rating">Rating:</label><br />
          <select
            id="rating"
            name="rating"
            required
            style={{ width: "100%", padding: "5px", marginBottom: "10px" }}
          >
            <option value="">Select Rating</option>
            <option value="5">5 - Excellent</option>
            <option value="4">4 - Good</option>
            <option value="3">3 - Average</option>
            <option value="2">2 - Slightly Better</option>
            <option value="1">1 - Needs Improvement</option>
          </select><br />

          <label htmlFor="reviewText">Review:</label><br />
          <textarea
            id="reviewText"
            name="reviewText"
            rows="3"
            placeholder="Write your review here..."
            required
            style={{ width: "100%", padding: "5px", marginBottom: "10px" }}
          ></textarea><br />

          <button type="submit" style={{ padding: "5px 10px" }}>Submit</button>
        </form>
        {reviewMessage && <p style={{ color: "green", marginTop: "10px" }}>{reviewMessage}</p>}
      </section>

      <BackToHome />
    </div>
  );
}
