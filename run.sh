mvn package -Dmaven.javadoc.skip=true \
	    -DskipTests=true && \
java  \
  -DBASE_URL="http://localhost:8001/openid-connect-server-webapp/" \
  -jar ~/Downloads/jetty-runner-9.1.0.RC2.jar \
  --path /openid-connect-server-webapp \
  --port 8001 \
  openid-connect-server-webapp/target/openid-connect-server-webapp.war
