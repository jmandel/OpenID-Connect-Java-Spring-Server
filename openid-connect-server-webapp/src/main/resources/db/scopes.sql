--
-- Turn off autocommit and start a transaction so that we can use the temp tables
--

SET AUTOCOMMIT FALSE;

START TRANSACTION;

--
-- Insert scope information into the temporary tables.
-- 

INSERT INTO system_scope_TEMP (scope, description, icon, allow_dyn_reg, default_scope, structured, structured_param_description) VALUES
  ('openid', 'log in using your identity', 'user', true, true, false, null),
  ('profile', 'basic profile information', 'list-alt', true, true, false, null),
  ('email', 'email address', 'envelope', true, true, false, null),
  ('address', 'physical address', 'home', true, true, false, null),
  ('phone', 'telephone number', 'bell', true, true, false, null),
  ('search', 'FHIR search', 'user', true, false, true, 'Search for resources belonging to patient'),
  ('summary', 'FHIR summary', 'user', true, false, true, 'Access clinical summary for patient'),
  ('smart', 'SMART', 'user', true, false, true, 'Details of SMART authorization'),
  ('orchestrate-launch', 'Orchestrate a launch with EHR context', 'user', true, false, false, null),
  ('fhir-complete', 'all FHIR permissions for user', 'user', true, false, false, null),
  ('launch/id', 'Launch with an existing context', 'user', true, false, true, 'Launch from existing context'),
  ('launch/patient', 'Launch with patient context', 'user', true, false, true, 'Launch patient'),
  ('launch/encounter', 'Launch with encounter context', 'user', true, false, true, 'Launch encounter'),
  ('launch/resource', 'Launch with resource context', 'user', true, false, true, 'Launch resource'),
  ('launch/other', 'Launch with other context', 'user', true, false, true, 'Launch other'), 
  ('user/Patient.read', 'Read all Patient data that you can access', 'user', true, false, false, null), 
  ('offline_access', 'offline access', 'time', true, true, false, null);
  
--
-- Merge the temporary scopes safely into the database. This is a two-step process to keep scopes from being created on every startup with a persistent store.
--

MERGE INTO system_scope
	USING (SELECT scope, description, icon, allow_dyn_reg, default_scope, structured, structured_param_description FROM system_scope_TEMP) AS vals(scope, description, icon, allow_dyn_reg, default_scope, structured, structured_param_description)
	ON vals.scope = system_scope.scope
	WHEN NOT MATCHED THEN
	  INSERT (scope, description, icon, allow_dyn_reg, default_scope, structured, structured_param_description) VALUES(vals.scope, vals.description, vals.icon, vals.allow_dyn_reg, vals.default_scope, vals.structured, vals.structured_param_description);

COMMIT;

SET AUTOCOMMIT TRUE;
