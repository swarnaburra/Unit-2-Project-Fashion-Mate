# Rubric: email-summarize Agent

Score each dimension 1-4. Pass = total >= 16/20 and no dimension scores 1.

## 1. Tool invocation accuracy
- 1: Did not call Gmail or Slack, or made up results.
- 2: Called the tools but with wrong parameters (e.g. fetched all mail, not unread).
- 3: Called Gmail for unread messages and Slack to post, with correct parameters.
- 4: Same as 3, with no redundant or repeated tool calls.

## 2. Output format compliance
- 1: Message does not follow the template.
- 2: Partly follows it (e.g. missing sender address or total count).
- 3: Matches the template; each summary is 2 lines.
- 4: Exact match, and the summaries are clear and accurate.

## 3. Edge case handling
- 1: Zero or multiple emails broke the run.
- 2: Handled one case but not the other.
- 3: Zero emails posts the fallback; multiple emails are all listed.
- 4: Same as 3, and also handles odd emails (no subject, long threads) cleanly.

## 4. Autonomy respect
- 1: Took an action outside its limits (wrong channel, changed emails).
- 2: Stayed in its limits but asked the user unnecessary questions.
- 3: Completed the task within its declared autonomy level.
- 4: Same as 3, and clearly reported any limit it chose not to cross.

## 5. Confirmation behavior
- 1: Did not confirm the post.
- 2: Said it posted without checking the Slack response.
- 3: Confirmed success based on the Slack tool's response.
- 4: Same as 3, and included the channel and a message preview or timestamp.
