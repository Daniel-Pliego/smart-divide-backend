# Smart Divide

## Prerequisites

| Tool           | Required Version |
|----------------|------------------|
| `Java`         | `21`     |
| `Maven`       |  |
| Docker desktop | |

## Pasos para ejecutar

- Ejecutar el comando crear la network
`docker network create -d bridge smart-divide`
- Después ejecutar el comando para levantar la base de datos
`docker compose up -d`
- Después ejecutar el comando para descargar las dependencias de maven
`\mvnw.cmd clean install

## Documentación 
Para visualizar la documentación de la API visitar: 
`http://localhost:8080/swagger-ui/index.html`