podman run \
	--cgroup-manager=cgroupfs  \
	-d \
	-e POSTGRES_USER=postgres \
	-e POSTGRES_PASSWORD=CHANGEPASS \
	--privileged \
	--mount type=bind,source=./DatabaseFolder,target=/var/lib/postgresql/data  \
	-p 5432:5432 \
	--name dblearnstar_maindb \
	postgres:17
