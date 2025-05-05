podman run \
	--name docker_cas \
	-v ./services:/etc/cas/services:Z \
	-v ./config:/etc/cas/config:Z \
	--rm \
	-p 8080:8080 \
	-d \
	--name cas \
	apereo/cas:6.6.7 \
		--cas.standalone.configuration-directory=/etc/cas/config \
		--server.ssl.enabled=false \
		--server.port=8080 \
		--management.server.port=10000 \
		--cas.service-registry.core.init-from-json=true \
		--cas.service-registry.json.location=file:/etc/cas/services
