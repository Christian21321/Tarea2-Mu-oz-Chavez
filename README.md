# Tarea 2 - Ingeniería de Software

API en Spring Boot que lee las compras de una pyme desde `compras.csv` y las convierte a pesos chilenos
usando el valor de la UF o del dólar en la fecha de cada compra, obtenido desde [mindicador.cl](https://mindicador.cl).

## Integrantes

| Nombre | Usuario GitHub | Desafíos realizados |
|---|---|---|
| NOMBRE_INTEGRANTE_1 | [@Christian21321](https://github.com/Christian21321) | 1. Compras por unidad · 3. Docker |
| NOMBRE_INTEGRANTE_2 | [@mnomann](https://github.com/mnomann) | 2. Detalle de una compra · 4. Pipeline |

## Endpoints

| Método | URL | Descripción |
|---|---|---|
| GET | `/compras/{unidad}` | Compras de la unidad `uf` o `dolar` convertidas a pesos, con el total |
| GET | `/compras/detalle/{id}` | Una compra convertida a pesos según su unidad |

Respuestas de error (formato `{"status", "error", "mensaje"}`):

| Caso | Código |
|---|---|
| Unidad inexistente (ej. `/compras/euro`) | 404 Not Found |
| Compra inexistente (ej. `/compras/detalle/999`) | 404 Not Found |
| Id no numérico (ej. `/compras/detalle/abc`) | 400 Bad Request |
| mindicador.cl no responde | 502 Bad Gateway |
| `compras.csv` no se puede leer o tiene una línea inválida | 500 Internal Server Error |

`monto_pesos` es `monto × valor_unidad` redondeado al entero más cercano, y `total_pesos` es la suma de los
`monto_pesos`. Las compras se entregan en el mismo orden del CSV.

### Consulta a mindicador.cl

Se usa `GET https://mindicador.cl/api/{uf|dolar}/{dd-mm-yyyy}`, que entrega el valor del indicador en esa fecha.
El dólar no se publica los fines de semana ni feriados: en ese caso la serie viene vacía y se usa el último
valor publicado antes de la fecha (`GET https://mindicador.cl/api/dolar/{yyyy}`).
Los valores ya consultados se guardan en memoria para no repetir llamadas.

## Ejecutar

Requisitos: Java 21 (o solo Docker).

```bash
# Local (lee compras.csv desde la raíz del repositorio, puerto 3001)
./mvnw spring-boot:run

# Docker Compose (un solo comando)
docker compose up --build
```

```bash
curl http://localhost:3001/compras/uf
curl http://localhost:3001/compras/dolar
curl http://localhost:3001/compras/detalle/3
```

### Docker sin Compose

```bash
docker build -t tarea2 .
docker run -p 3001:3001 -v "$(pwd)/compras.csv:/data/compras.csv:ro" tarea2
```

| Variable | Valor por defecto | Descripción |
|---|---|---|
| `APP_PORT` | `3001` | Puerto de la API |
| `CSV_PATH` | `/data/compras.csv` (en Docker) · `compras.csv` (local) | Ruta del archivo de compras |
| `APP_ENV` | `production` | Ambiente (definido en la imagen) |

`compras.csv` no se copia a la imagen (está en `.dockerignore`); se monta como volumen, así se puede modificar
sin reconstruir. La aplicación lee el archivo en cada consulta, por lo que los cambios se ven sin reiniciar.
La imagen compila el proyecto en una etapa de build, usa un JRE reducido con `jlink` sobre Alpine
(≈ 150 MB) y corre con el usuario sin privilegios `app`.

## Pipeline (GitHub Actions)

`.github/workflows/ci.yml` tiene tres jobs encadenados con `needs`, así si uno falla los siguientes no corren:

1. **test**: `mvn test`.
2. **build**: genera el JAR (`mvn package`) y construye la imagen Docker.
3. **publish**: solo en push a `main`. Publica la imagen en `ghcr.io/<owner>/<repo>` con los tags `latest`
   y el SHA corto del commit (7 caracteres). Se autentica con `GITHUB_TOKEN`, sin credenciales en el repositorio.

## Ramas

`main` ← `develop` ← `feature/compras-unidad`, `feature/compra-detalle`, `docker`, `pipeline` (en ese orden de integración).
