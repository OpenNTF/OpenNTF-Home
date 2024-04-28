# Migration

When this app is deployed, we'll have to take a couple steps to account for structural changes:

- Modify the Web Site document to point to the app's main.nsf/xsp/app landing page instead of just main.nsf to avoid an unnecessary redirect
- Add "attachments" column to the "AM" view in the "OLD OpenNTF Home" database with the formula `@AttachmentNames`
- Modify "Members" page to include the list-building JavaScript