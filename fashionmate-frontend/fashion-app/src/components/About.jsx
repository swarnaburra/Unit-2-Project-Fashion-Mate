import React, { useState, useEffect } from "react";
import BackToHome from "./BackToHome";
import { useUser } from "../context/UserContext";

export default function About() {
  const { userId } = useUser();
  const [reviewMessage, setReviewMessage] = useState("");
  const [feedbackList, setFeedbackList] = useState([]);


  const team = [
    { id: 1, name: "Swarna Burra", role: "Founder & Designer" },
    { id: 2, name: "Swarna Burra", role: "Frontend Developer" },
    { id: 3, name: "Swarna Burra", role: "Backend Developer" },
  ];

  const features = [
    "Seasonal outfit recommendations",
    "Fashion quiz to discover your style",
    "Save your favorite looks",
  ];

  // 🔹 Fetch all feedback for this user
  useEffect(() => {
    if (!userId) return;

    fetch(`http://localhost:8080/api/reviews/${userId}`)
      .then((res) => res.json())
      .then((data) => setFeedbackList(data))
      .catch((err) => console.error("Error loading feedback:", err));
  }, [userId]);

  // 🔹 Submit feedback to backend
  const handleReviewSubmit = async (e) => {
    e.preventDefault();

    const formData = new FormData(e.target);
    const payload = {
      name: formData.get("name"),
      rating: formData.get("rating"),
      comment: formData.get("reviewText"),
    };

    try {
      const response = await fetch(
        `http://localhost:8080/api/reviews/${userId}`,
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(payload),
        }
      );

      if (response.ok) {
        setReviewMessage("Thank you for your review!");

        // Refresh feedback list
        const updatedList = await fetch(
          `http://localhost:8080/api/reviews/${userId}`
        ).then((res) => res.json());

        setFeedbackList(updatedList);
        e.target.reset();
      } else {
        setReviewMessage("Failed to submit review.");
      }
    } catch (error) {
      console.error("Error submitting review:", error);
      setReviewMessage("Error submitting review.");
    }
  };

  // 🔹 Delete review
  const handleDelete = async (reviewId) => {
    try {
      const response = await fetch(
        `http://localhost:8080/api/reviews/${userId}/review/${reviewId}`,
        { method: "DELETE" }
      );

      if (response.ok) {
        setFeedbackList((prev) => prev.filter((fb) => fb.id !== reviewId));
      } else {
        console.error("Failed to delete review");
      }
    } catch (error) {
      console.error("Error deleting review:", error);
    }
  };


  return (
    <div style={{ minHeight: "100vh",
  backgroundImage: "linear-gradient(rgba(182, 155, 178, 0.9), rgba(182, 155, 178, 0.97)), url('/homepage.jpeg')",
  backgroundSize: "cover",
  backgroundPosition: "center",
  backgroundAttachment: "fixed",
  backgroundRepeat: "no-repeat",
  padding: "2rem",
  fontFamily: "sans-serif" }}>
      <h2>About FashionMate</h2>

      {/* TEAM TABLE */}
      <h3>Meet Our Team</h3>
      <table style={{ width: "100%", borderCollapse: "collapse", marginBottom: "2rem" }}>
        <thead>
          <tr style={{ background: "rgb(120, 90, 102)", color: "white" }}>
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

      {/* FEATURES */}
      <h3>App Features</h3>
      <ul>
        {features.map((feature, idx) => (
          <li key={idx} style={{ marginBottom: "0.5rem" }}>
            {feature}
          </li>
        ))}
      </ul>

      {/* REVIEW FORM */}
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

          <button type="submit" style={{ padding: "5px 10px", background: "rgb(83, 39, 13)", color: "white", border: "none" }}>Submit</button>
        </form>

        {reviewMessage && (
          <p style={{ color: "green", marginTop: "10px" }}>{reviewMessage}</p>
        )}
      </section>

      {/* FEEDBACK TABLE */}
      <h3 style={{ marginTop: "2rem" }}>Your Submitted Feedback</h3>
      <table style={{ width: "100%", borderCollapse: "collapse" }}>
        <thead>
          <tr style={{ background: "rgb(120, 90, 102)", color: "white" }}>
            <th style={{ padding: "0.5rem", border: "1px solid #ddd" }}>Name</th>
            <th style={{ padding: "0.5rem", border: "1px solid #ddd" }}>Rating</th>
            <th style={{ padding: "0.5rem", border: "1px solid #ddd" }}>Review</th>
            <th style={{ padding: "0.5rem", border: "1px solid #ddd" }}>Actions</th>
          </tr>
        </thead>
        <tbody>
          {feedbackList.length > 0 ? (
            feedbackList.map((fb, index) => (
              <tr key={index}>
                <td style={{ padding: "0.5rem", border: "1px solid #ddd" }}>{fb.name}</td>
                <td style={{ padding: "0.5rem", border: "1px solid #ddd" }}>{fb.rating}</td>
                <td style={{ padding: "0.5rem", border: "1px solid #ddd" }}>{fb.comment}</td>

                <td
                  style={{
                    padding: "0.5rem",
                    border: "1px solid #a888a5",
                    textAlign: "center",
                    cursor: "pointer",
                    color: "red",
                    fontSize: "1.2rem",
                  }}
                  onClick={() => handleDelete(fb.id)}
                  title="Delete Review"
                >
                  🗑️
                </td>

              </tr>
            ))
          ) : (
            <tr>
              <td colSpan="3" style={{ textAlign: "center", padding: "1rem" }}>
                No feedback submitted yet.
              </td>
            </tr>
          )}
        </tbody>
      </table>

      <BackToHome />
    </div>
  );
}