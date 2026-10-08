---
name: post-standup
description: Posts a formatted daily standup update to a Slack channel using the Slack MCP server. Use when the user wants to share what they worked on yesterday, what they're working on today, and any blockers.
---

## Steps
Ask the user for the following information if it has not already been provided:
- What did you work on yesterday?
- What are you working on today?
- Any blockers?
- Which Slack channel to post to (default: #bot-testing)

Format the message as:
*Standup Update*
- Yesterday: [yesterday's work]
- Today: [today's work]
- Blockers: [blockers, or "None"]

Post the formatted message to the specified channel using the Slack MCP tool. Confirm to the user that the message was sent, including the channel name and a preview of the first line.

## Troubleshooting
If the Slack MCP server is unavailable, tell the user that SLACK_BOT_TOKEN and SLACK_TEAM_ID may not be set and suggest restarting the container.
