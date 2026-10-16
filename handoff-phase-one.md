## HANDOFF DOCUMENT

### Overall Task
The user needs help editing a technical blog post titled "Getting Started with API Integration" through several rounds of revision. A consistent style guide must be applied to every section, edited one section at a time, with the agent confirming understanding before editing begins. The goal is a fully revised blog post where every section complies with all seven style guide rules.

### Phase One Summary
In phase one: (1) the full blog post and style guide were read and confirmation was given without making edits, (2) the Introduction section was edited to comply with the full style guide, (3) the Authentication section was edited to comply with the full style guide. The Making Requests and Error Handling sections have not yet been edited and remain in their original form. Each editing phase followed the project's Context Boundary Procedure (see Known Constraints and Gotchas) before making changes.

### Current State of All Artifacts

**Introduction** (edited, complete):
> Developers use **APIs** to connect different software systems together. When you use an API, you send a request to a remote server and wait for a response. Your application then processes this response in whatever way is needed. In short: developers use APIs to send requests and process the responses they receive.

**Authentication** (edited, complete):
> Most APIs require **authentication**, and developers handle it with **API keys** or **tokens**. You send these credentials with each request, and the server validates them before responding. If you provide invalid credentials, the server returns a **401 error**. In short: APIs authenticate requests using keys or tokens, and invalid credentials trigger a 401 error.

**Making Requests** (NOT yet edited — full original text, verbatim):
> HTTP requests are made using methods like GET, POST, PUT, and DELETE. A GET request is used when data needs to be retrieved, while POST is utilized when new data needs to be created on the server. The response that is returned will include a status code, headers, and usually a body containing JSON data that can then be parsed.

**Error Handling** (NOT yet edited — full original text, verbatim):
> Errors in API integrations are often caused by network issues, invalid input, or rate limiting applied by the provider. It is recommended that retry logic be implemented with exponential backoff for transient errors. Permanent errors, such as 404 or 400 responses, should be surfaced to the user rather than retried.

### Rules and Constraints in Effect
Copied verbatim from the original task specification (confirmed against the source message, all seven rules present and unaltered):

> STYLE GUIDE (apply to all edits):
> - Never use passive voice. Always rewrite passive constructions as active voice.
> - Sentences must be 25 words or fewer. Split any longer sentence into two.
> - Never use the word "utilize" -- replace it with "use"
> - Never use the word "leverage" -- replace it with "use" or "build on"
> - Technical terms must be bolded on first use in each section
> - Every section must end with a one-sentence summary starting with "In short:"
> - Oxford comma required in all lists

No rules changed during phase one; this is the full and current rule set as of the end of phase one. Note: phase two begins with a rule change supplied by the user (see Phase Two Instructions, item 1) — the rules above are the baseline phase two must start from, not necessarily the rules phase two will finish under.

### Phase Two Instructions
1. Apply a rule change the user will give at the start of phase two. Obtain and apply this rule change before doing any further editing.
2. Edit the Making Requests section to comply with the full (updated) style guide.
3. Edit the Error Handling section to comply with the full (updated) style guide.
4. Update the Introduction and Authentication sections so they comply with the new rules (they were written under the phase one rule set and may no longer be compliant after the rule change).
5. Write a new Best Practices section, 4-5 paragraphs, complying with the full (updated) style guide.
6. Do a consistency pass over the whole post (all five sections) to confirm uniform application of the current rules throughout.

### Acceptance Criteria for Phase Two
A correct, complete section edit must satisfy every rule in the style guide above (as modified by the rule change delivered at the start of phase two):
- No passive voice remains anywhere in the section.
- Every sentence is 25 words or fewer.
- The words "utilize" and "leverage" do not appear anywhere.
- Each technical term is bolded the first time it appears in that section (sections are independent for this rule).
- The section ends with a one-sentence summary beginning with "In short:".
- Any list of three or more items uses an Oxford comma.

### Known Constraints and Gotchas
- The project's CLAUDE.md defines an "Agent Context Boundary Policy" requiring a boundary procedure (restate goal, list current rules verbatim, state superseded rules, identify artifact) at the start of each new editing/analysis phase. It explicitly does not apply to read-only questions/confirmations.
- CLAUDE.md also notes a validated lesson: before claiming a section complies, re-check the newly written text — including the new "In short:" sentence — against every single rule, not just the ones it was written to fix.
- When a section has no list at all, the Oxford comma rule simply does not trigger for that section — this was not treated as a violation in phase one. Making Requests and Error Handling both already contain lists with Oxford commas in their original text; preserve those commas when editing.
- "API" and "APIs" were treated as the same technical term for bolding purposes — only the first occurrence of the term family in a section was bolded, not every singular/plural variant.
- Sections were edited strictly one at a time, in order, per explicit user instruction each time ("Now edit only the X section"). Phase two's workload is broader (rule change, two section edits, two section updates, a new section, and a consistency pass) and is not necessarily one-section-at-a-time — follow whatever sequencing the user specifies when delivering the rule change.
- Because phase two starts with a rule change, the Introduction and Authentication text shown above in Current State of All Artifacts is compliant with the phase one rules only — it is explicitly expected to need rework in phase two (see Phase Two Instructions, item 4).
