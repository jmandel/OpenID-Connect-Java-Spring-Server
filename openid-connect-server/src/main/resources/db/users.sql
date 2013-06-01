--
-- Turn off autocommit and start a transaction so that we can use the temp tables
--

SET AUTOCOMMIT FALSE;

START TRANSACTION;

--
-- Insert user information into the temporary tables. To add users to the HSQL database, edit things here.
-- 

INSERT INTO trusted_registry_TEMP (uri) VALUES
  ('http://blue-button.github.com/static-registry-example');
 
INSERT INTO users_TEMP (username, password, enabled) VALUES
  ('jricher', 'password', true),
  ('j', 'j', true),
  ('aanganes','password',true),
  ('mfranklin','password',true),
  ('srmoore','password',true);

INSERT INTO authorities_TEMP (username, authority) VALUES
  ('jricher', 'ROLE_ADMIN'),
  ('aanganes','ROLE_ADMIN'),
  ('jricher', 'ROLE_USER'),
  ('aanganes','ROLE_USER'),
  ('mfranklin','ROLE_USER'),
  ('srmoore','ROLE_USER');
    
-- By default, the username column here has to match the username column in the users table, above
INSERT INTO user_info_TEMP (sub, preferred_username, name, email, email_verified) VALUES
  ('jricher', 'jricher', 'Justin Richer', 'jricher@mitre.org', false),
  ('aanganes', 'aanganes', 'Amanda Anganes', 'aanganes@mitre.org', false),
  ('mfranklin', 'mfranklin', 'Matt Franklin', 'mfranklin@mitre.org', false),
  ('srmoore', 'srmoore', 'Steve Moore', 'srmoore@mitre.org', false);

 
--
-- Merge the temporary users safely into the database. This is a two-step process to keep users from being created on every startup with a persistent store.
--

MERGE INTO users 
  USING (SELECT username, password, enabled FROM users_TEMP) AS vals(username, password, enabled)
  ON vals.username = users.username
  WHEN NOT MATCHED THEN 
    INSERT (username, password, enabled) VALUES(vals.username, vals.password, vals.enabled);

MERGE INTO authorities 
  USING (SELECT username, authority FROM authorities_TEMP) AS vals(username, authority)
  ON vals.username = authorities.username AND vals.authority = authorities.authority
  WHEN NOT MATCHED THEN 
    INSERT (username,authority) values (vals.username, vals.authority);

MERGE INTO user_info 
  USING (SELECT sub, preferred_username, name, email, email_verified FROM user_info_TEMP) AS vals(sub, preferred_username, name, email, email_verified)
  ON vals.preferred_username = user_info.preferred_username
  WHEN NOT MATCHED THEN 
    INSERT (sub, preferred_username, name, email, email_verified) VALUES (vals.sub, vals.preferred_username, vals.name, vals.email, vals.email_verified);

MERGE INTO trusted_registry
  USING (SELECT uri FROM trusted_registry_TEMP) AS vals(uri)
  ON vals.uri = trusted_registry.uri
  WHEN NOT MATCHED THEN
    INSERT (uri) VALUES(vals.uri);

-- 
-- Close the transaction and turn autocommit back on
-- 
    
COMMIT;

SET AUTOCOMMIT TRUE;

