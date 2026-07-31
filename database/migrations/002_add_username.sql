-- Run this once in Supabase SQL Editor.
-- Adds an optional username so users can log in with either email or username.

alter table users add column if not exists username text unique;

-- Optional: give the demo admin a username too, so you can test logging in
-- with it. Skip or edit this if you don't want it.
update users set username = 'admin' where email = 'admin@buildcrew.com' and username is null;
