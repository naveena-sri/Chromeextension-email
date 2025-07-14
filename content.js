const BACKEND = "http://localhost:8080/api/email/generate";

// 1. Observe the DOM for any new Gmail reply textbox
const observer = new MutationObserver(muts => {
  for (let m of muts) {
    for (let node of m.addedNodes) {
      if (!(node instanceof HTMLElement)) continue;
      const box = node.matches('div[role="textbox"][contenteditable="true"]') 
        ? node 
        : node.querySelector('div[role="textbox"][contenteditable="true"]');
      if (box && !box.dataset.geminiReady) {
        box.dataset.geminiReady = "1";
        attachButton(box);
      }
    }
  }
});
observer.observe(document.body, { childList: true, subtree: true });

// 2. Robust function to get the last visible email text
function getLastEmailText() {
  const bodies = Array.from(document.querySelectorAll(".a3s"));
  const visibleBodies = bodies.filter(el => el.offsetParent !== null); // only visible ones
  const last = visibleBodies[visibleBodies.length - 1];

  if (!last) return "";

  // Extract text with fallback
  let text = last.innerText?.trim();
  if (!text) {
    text = last.textContent?.trim();
  }

  return text || "";
}

// 3. Insert the button next to the reply box
function attachButton(textbox) {
  const toolbar = textbox.parentElement.querySelector('[aria-label="Formatting options"]')
                || textbox.parentElement;
  const btn = document.createElement("button");
  btn.textContent = "Generate Reply";
  btn.style.cssText = `
    margin: 8px; padding: 6px 10px;
    background:#1a73e8;color:#fff;border:none;border-radius:4px;
    cursor:pointer;
  `;
  btn.addEventListener("click", () => onGenerate(textbox));
  toolbar.parentNode.insertBefore(btn, toolbar);
}

// 4. When clicked, call your backend
async function onGenerate(textbox) {
  const prompt = getLastEmailText();
  if (!prompt) {
    alert("Couldn't find the email content to reply to.");
    return;
  }

  try {
    const res = await fetch(BACKEND, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        contents: [
          { role: "user", parts: [{ text: prompt }] }
        ]
      })
    });
    const json = await res.json();
    const reply = json?.candidates?.[0]?.content?.parts?.[0]?.text;
    console.log("Backend response:", json);

    if (reply) {
      textbox.focus();
      document.execCommand("selectAll", false, null);
      document.execCommand("insertText", false, reply.trim());
    } else {
      alert("No reply generated.");
    }
  } catch (e) {
    console.error(e);
    alert("Error generating reply. See console.");
  }
}
