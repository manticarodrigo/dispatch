SELECT id,
    message,
    time,
    author
FROM comments
WHERE comments.id = $1