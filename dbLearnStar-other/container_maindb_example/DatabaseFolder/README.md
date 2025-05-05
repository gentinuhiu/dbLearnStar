When starting the container as proposed, it's internal database data directory will be mapped to this external directory. As an effect, all the database files created by the container will persist in this directory instead of insinde the container, so removing or deleting the container will not delete the database.

