import { useState, useEffect, useCallback } from "react";
import { GoogleGenAI } from "@google/genai";


export default function ImageViewer() {
  const [image, setImage] = useState(null);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState(""); // Yay/Nay + recommendations
  const [isnay, setNay] = useState(false);
  


  const geminiApiKey = localStorage.getItem("geminiApiKey");
  const ai = new GoogleGenAI({ apiKey: geminiApiKey });

  useEffect(() => {
    return () => {
      if (image && image.url) URL.revokeObjectURL(image.url);
    };
  }, [image]);

  const toBase64 = (file) =>
    new Promise((resolve) => {
      const reader = new FileReader();
      reader.onloadend = () => resolve(reader.result.split(",")[1]);
      reader.readAsDataURL(file);
    });

  // ⬇️ GEMINI OUTFIT ANALYSIS
  const analyzeWithGemini = async (file) => {
    setLoading(true);
    setResult("");

    try {
      const base64 = await toBase64(file);

      const prompt = `
                  You are a fashion expert.
                  Step 1: Determine if the image contains an outfit. Make sure it's an outfit and not an object like building, car etc
                  Step 2: If yes, say either "YAY" or "NAY" about the outfit quality.
                  Step 3: If NAY, give 2–3 specific improvement suggestions.
                  Keep response short, friendly, and easy to read. 
              `;

      const contents = [
        {
          inlineData: {
            mimeType: file.type,
            data: base64,
          },
        },
        { text: prompt },
      ];

      const response = await ai.models.generateContent({
        model: "gemini-2.5-flash",
        contents: contents,

      });

      let text = response.candidates[0].content.parts[0].text;
      if (text.includes("NAY")) {
        setNay(true);
      }
      else {
        setNay(false);
      }

      setResult(text)
    } catch (err) {
      setError("AI analysis failed. Try again.");
      console.error(err);
    }

    setLoading(false);
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

      //Call Gemini after selecting the file
      analyzeWithGemini(file);
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
    setResult("");
  };

  return (

    <div style={{ maxWidth: 600, margin: "1rem auto", fontFamily: "sans-serif" }}>

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

      {error && <div style={{ color: "crimson", marginBottom: 8 }}>{error}</div>}

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
            onClick={removeImage}
            style={{ padding: "0.5rem 1rem", marginTop: 8, cursor: "pointer" }}
          >
            Remove image
          </button>

          {loading && (
            <div>
              <div className="spinner"></div>
              <p style={{ marginTop: 10, fontWeight: "bold" }}>
                Analyzing outfit...
              </p>
            </div>
          )}

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
      ) : (
        <div style={{ color: "#666", textAlign: "center", padding: "1rem 0" }}>
          No image selected
        </div>
      )}
    </div>

  );
}
