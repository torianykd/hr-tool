HR Tool API
==============

API provides ability for employees of a certain company to request vacation days.

API documentation could be at: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

#### Local Set Up(Docker)
- Clone code
- Copy and fill `.env` from `.env.example` (contact the [owner](mailto:dimatoryanik@gmail.com) of the repo for secrets)
- Package the App using Maven(`mvn package`) providing environment variables from the `.env` file, otherwise build won't succeed
- Run `docker-compose up -d`
