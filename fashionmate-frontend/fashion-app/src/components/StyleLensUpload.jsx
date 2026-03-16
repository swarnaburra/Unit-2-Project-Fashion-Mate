import { useState, useEffect, useCallback } from "react";
import { useUser } from "../context/UserContext";

export default function StyleLensUpload() {
  const { userId } = useUser();
  const [image, setImage] = useState(null);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [formData, setFormData] = useState({
    occasion: '',
    age: '',
    preference: ''
  });
  const [result, setResult] = useState("");

  useEffect(() => {
    return () => {
      if (image && image.url) URL.revokeObjectURL(image.url);
    };
  }, [image]);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
    setError("");
  };

  const handleFile = useCallback(
    (file) => {
      if (!file) return;

      // Validate image type
      if (!file.type.startsWith("image/")) {
        setError("Please select a valid image file.");
        return;
      }

      setError("");

      if (image && image.url) URL.revokeObjectURL(image.url);

      setImage({ file, url: URL.createObjectURL(file) });
    },
    [image]
  );

  const handleChange = (e) => handleFile(e.target.files && e.target.files[0]);

  const handleDrop = (e) => {
    e.preventDefault();
    e.stopPropagation();
    const dt = e.dataTransfer;
    if (dt && dt.files && dt.files[0]) handleFile(dt.files[0]);
  };

  const handleDragOver = (e) => {
    e.preventDefault();
    e.stopPropagation();
  };

  const removeImage = () => {
    if (image && image.url) URL.revokeObjectURL(image.url);
    setImage(null);
    setError("");
  };

  const validateForm = () => {
    if (!image) {
      setError("Please select an image.");
      return false;
    }
    if (!formData.occasion.trim()) {
      setError("Please enter an occasion.");
      return false;
    }
    if (!formData.age.trim()) {
      setError("Please enter your age.");
      return false;
    }
    if (!formData.preference) {
      setError("Please select a preference.");
      return false;
    }
    return true;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!validateForm()) {
      return;
    }

    if (!userId) {
      setError("User not authenticated.");
      return;
    }

    setLoading(true);
    setError("");

    // convert image file to base64 string
    const toBase64 = (file) => new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onload = () => resolve(reader.result.split(",")[1]);
      reader.onerror = reject;
      reader.readAsDataURL(file);
    });

    try {
      const imageBase64 = await toBase64(image.file);

      const payload = {
        contents: imageBase64,
        mimetype: image.file.type,
        occasion: formData.occasion,
        age: formData.age,
        preference: formData.preference
      };

      const response = await fetch(`http://localhost:8080/api/stylelens/processImage/${userId}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(payload)
      });

      if (!response.ok) {
        throw new Error('Failed to process image');
      }

      const result = await response.text();
      setResult(result);

      console.log('Success:', result);
      // Handle success - maybe show a success message or redirect

    } catch (err) {
      setError("Failed to process image. Please try again.");
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ maxWidth: 600, margin: "1rem auto", fontFamily: "sans-serif" }}>
      <h2 style={{ textAlign: "center", marginBottom: "2rem" }}>StyleLens Upload</h2>

      <form onSubmit={handleSubmit}>
        {/* Image Upload Section */}
        <div style={{ marginBottom: "2rem" }}>
          <label
            htmlFor="image-input"
            style={{
              display: "block",
              padding: "1rem",
              border: "2px dashed #bbb",
              borderRadius: 8,
              textAlign: "center",
              cursor: "pointer",
              marginBottom: 8,
            }}
            onDrop={handleDrop}
            onDragOver={handleDragOver}
          >
            <input
              id="image-input"
              type="file"
              accept="image/*"
              onChange={handleChange}
              style={{ display: "none" }}
            />
            Click to select an image or drag & drop here
          </label>

          {image ? (
            <div style={{ textAlign: "center" }}>
              <img
                src={image.url}
                alt={image.file.name}
                style={{
                  maxWidth: "100%",
                  height: "auto",
                  borderRadius: 6,
                  boxShadow: "0 2px 8px rgba(0,0,0,0.15)"
                }}
              />
              <div style={{ marginTop: 8 }}>
                <small>
                  {image.file.name} — {(image.file.size / 1024).toFixed(1)} KB
                </small>
              </div>
              <button
                type="button"
                onClick={removeImage}
                style={{
                  padding: "0.5rem 1rem",
                  marginTop: 8,
                  cursor: "pointer",
                  background: "#f44336",
                  color: "white",
                  border: "none",
                  borderRadius: 4
                }}
              >
                Remove image
              </button>
            </div>
          ) : (
            <div style={{ color: "#666", textAlign: "center", padding: "1rem 0" }}>
              No image selected
            </div>
          )}
        </div>

        {/* Form Fields */}
        <div style={{ marginBottom: "2rem" }}>
          <div style={{ marginBottom: "1rem" }}>
            <label htmlFor="occasion" style={{ display: "block", marginBottom: "0.5rem", fontWeight: "bold" }}>
              Occasion:
            </label>
            <input
              type="text"
              id="occasion"
              name="occasion"
              value={formData.occasion}
              onChange={handleInputChange}
              placeholder="e.g., wedding, office, casual outing"
              style={{
                width: "100%",
                padding: "0.75rem",
                border: "1px solid #ccc",
                borderRadius: 4,
                fontSize: "1rem"
              }}
              disabled={loading}
            />
          </div>

          <div style={{ marginBottom: "1rem" }}>
            <label htmlFor="age" style={{ display: "block", marginBottom: "0.5rem", fontWeight: "bold" }}>
              Age:
            </label>
            <input
              type="text"
              id="age"
              name="age"
              value={formData.age}
              onChange={handleInputChange}
              placeholder="e.g., 25"
              style={{
                width: "100%",
                padding: "0.75rem",
                border: "1px solid #ccc",
                borderRadius: 4,
                fontSize: "1rem"
              }}
              disabled={loading}
            />
          </div>

          <div style={{ marginBottom: "1rem" }}>
            <label htmlFor="preference" style={{ display: "block", marginBottom: "0.5rem", fontWeight: "bold" }}>
              Style Preference:
            </label>
            <select
              id="preference"
              name="preference"
              value={formData.preference}
              onChange={handleInputChange}
              style={{
                width: "100%",
                padding: "0.75rem",
                border: "1px solid #ccc",
                borderRadius: 4,
                fontSize: "1rem"
              }}
              disabled={loading}
            >
              <option value="">Select preference</option>
              <option value="feminine">Feminine</option>
              <option value="masculine">Masculine</option>
              <option value="neutral">Neutral</option>
            </select>
          </div>
        </div>

        {error && <div style={{ color: "crimson", marginBottom: "1rem", textAlign: "center" }}>{error}</div>}

        <button
          type="submit"
          disabled={loading}
          style={{
            width: "100%",
            padding: "1rem",
            background: loading ? "#d19cbada" : "rgb(83, 39, 13)",
            color: "white",
            border: "none",
            borderRadius: 4,
            fontSize: "1rem",
            fontWeight: "bold",
            cursor: loading ? "not-allowed" : "pointer"
          }}
        >
          {loading ? "Processing..." : "Submit"}
        </button>
      </form>

      {result && (
        <div
          style={{
            marginTop: 15,
            padding: "1rem",
            background: "#f4f4f4",
            borderRadius: 8,
            textAlign: "left",
          }}
        >
          <h3>StyleLens Result:</h3>
          <p>{result}</p>
        </div>
      )}


    </div>
  );
}
