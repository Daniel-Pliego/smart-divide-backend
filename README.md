# Smart Divide

## Prerequisites

| Tool           | Required Version |
| -------------- | ---------------- |
| `Java`         | `21`             |
| `Maven`        |                  |
| Docker desktop |                  |

## Pasos para ejecutar

- Ejecutar el comando crear la network
  `docker network create -d bridge smart-divide`
- Después ejecutar el comando para levantar la base de datos
  `docker compose up -d`
- Después ejecutar el comando para descargar las dependencias de maven
  `\mvnw.cmd clean install`

## Estilo de código y formateo

Este proyecto utiliza la convenciones descritas por `Google java style guide`. Para mantener un formato de código consistente, se recomienda lo siguiente:

### Agregar pre-commit a githooks

Para instalar el `pre-commit` en el repositorio, ejecuta el siguiente comando:

```bash
git config core.hooksPath .githooks
```

Este comando indica a Git que utilice los hooks ubicados en la carpeta `.githooks` del repositorio actual.
Dentro de esta carpeta se encuentra el hook `pre-commit`, que realiza las siguientes acciones:

- Verifica los archivos .java modificados.
- Si el formato de los archivos es correcto, continúa con el commit.
- Si detecta formato incorrecto, formatea los archivos y continua el commit


## Documentación 
Para visualizar la documentación de la API visitar: 
`http://localhost:8080/swagger-ui/index.html`