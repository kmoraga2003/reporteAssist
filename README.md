# ReportAssist – App Android de Reportes de Asistencia

App Android que se conecta vía REST API al backend **ControllerAssist** (Spring Boot) para visualizar reportes de asistencia en tiempo real. Soporta dos roles: administrador (ve todos los reportes) y empleado (solo ve su propio registro).

---

## Tabla de Contenidos

1. [Requisitos Previos](#requisitos-previos)
2. [Estructura del Proyecto](#estructura-del-proyecto)
3. [Configurar y Ejecutar el Backend](#configurar-y-ejecutar-el-backend)
4. [Abrir el Proyecto en Android Studio](#abrir-el-proyecto-en-android-studio)
5. [Configurar la IP del Servidor](#configurar-la-ip-del-servidor)
6. [Usuarios de Prueba](#usuarios-de-prueba)
7. [Endpoints API Consumidos](#endpoints-api-consumidos)
8. [Funcionalidades por Rol](#funcionalidades-por-rol)
9. [Actualización en Tiempo Real](#actualización-en-tiempo-real)
10. [Solución de Problemas Comunes](#solución-de-problemas-comunes)
11. [Tecnologías Usadas](#tecnologías-usadas)

---

## Requisitos Previos

| Herramienta | Versión mínima | Notas |
|---|---|---|
| Android Studio | Ladybug / Preview | Con JDK 21 bundled |
| Android SDK | API 26+ | `minSdk 26` |
| Java (JDK) | 21 | Incluido en Android Studio |
| Backend ControllerAssist | — | Debe estar corriendo en puerto 8080 |

> **Importante:** El backend debe estar encendido **antes** de abrir la app, de lo contrario la pantalla de login no podrá autenticar.

---

## Estructura del Proyecto

```
reporteAssist/
├── app/
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/quimica/reportassist/
│       │   ├── activity/
│       │   │   ├── LoginActivity.java          ← Pantalla de inicio de sesión
│       │   │   ├── AdminDashboardActivity.java ← Panel admin con todos los reportes
│       │   │   └── EmployeeReportActivity.java ← Reporte personal del empleado
│       │   ├── adapter/
│       │   │   ├── ReportAdapter.java          ← Adapter para lista de reportes admin
│       │   │   └── EmployeeAttendanceAdapter.java ← Adapter asistencia empleado
│       │   ├── api/
│       │   │   ├── ApiService.java             ← Definición de endpoints Retrofit
│       │   │   └── ApiClient.java              ← Configuración OkHttp + Retrofit
│       │   ├── model/
│       │   │   ├── LoginRequest.java
│       │   │   ├── LoginResponse.java
│       │   │   ├── ReportItem.java
│       │   │   ├── AttendanceRecord.java
│       │   │   └── DashboardSummary.java
│       │   └── util/
│       │       └── SessionManager.java         ← Manejo de sesión con SharedPreferences
│       └── res/
│           ├── layout/
│           │   ├── activity_login.xml
│           │   ├── activity_admin_dashboard.xml
│           │   ├── activity_employee_report.xml
│           │   ├── item_report_card.xml
│           │   └── item_employee_attendance.xml
│           └── values/
│               ├── colors.xml
│               ├── strings.xml
│               └── themes.xml
├── gradle/
│   ├── libs.versions.toml    ← Versiones centralizadas (AGP, Retrofit, Material)
│   └── wrapper/
│       └── gradle-wrapper.properties
├── build.gradle
├── settings.gradle
└── gradle.properties         ← Incluye org.gradle.java.home apuntando al JDK 21
```

---

## Configurar y Ejecutar el Backend

El backend es una aplicación Spring Boot (ControllerAssist) con base de datos H2 embebida. No requiere instalar MySQL ni Docker para ejecutarse en local.

### Opción A — Ejecutar el JAR compilado

```bash
cd "/Users/scl076pcad005/Desktop/Proyectos /ControllerAssist"
java -jar target/controller-assist-0.0.1-SNAPSHOT.jar
```

### Opción B — Ejecutar con Maven

```bash
cd "/Users/scl076pcad005/Desktop/Proyectos /ControllerAssist"
./mvnw spring-boot:run
```

Cuando el backend esté listo verás en consola algo como:

```
Started ControllerAssistApplication in X.XXX seconds
```

El servidor queda escuchando en: `http://localhost:8080`

> La base de datos H2 se guarda en un archivo local, así que los datos persisten entre reinicios.

---

## Abrir el Proyecto en Android Studio

1. Abre **Android Studio Preview**
2. Selecciona **File → Open**
3. Navega a `/Users/scl076pcad005/Desktop/reporteAssist`
4. Haz clic en **OK** y espera que sincronice Gradle
5. Cuando termine, selecciona un emulador o conecta un dispositivo físico
6. Presiona el botón ▶ **Run** (o `Shift+F10`)

> Si aparece error de Gradle al sincronizar, verifica que `gradle.properties` contenga `org.gradle.java.home` apuntando al JDK 21 de Android Studio.

---

## Configurar la IP del Servidor

La app necesita saber dónde está el backend. Esto se configura en [`ApiClient.java`](app/src/main/java/com/quimica/reportassist/api/ApiClient.java):

```java
private static final String BASE_URL = "http://10.0.2.2:8080/";
```

| Situación | URL a usar |
|---|---|
| Emulador Android (AVD) | `http://10.0.2.2:8080/` |
| Dispositivo físico (misma red Wi-Fi) | `http://192.168.X.X:8080/` ← IP local del Mac |

### Cómo encontrar la IP local del Mac

Abre Terminal y ejecuta:

```bash
ipconfig getifaddr en0
```

Reemplaza la URL en `ApiClient.java` y vuelve a ejecutar la app.

> El `AndroidManifest.xml` ya tiene `android:usesCleartextTraffic="true"` para permitir conexiones HTTP (sin HTTPS) en la red local.

---

## Usuarios de Prueba

Los usuarios vienen precargados automáticamente en el backend al primer arranque gracias a `DataInitializer.java`.

| Email | Contraseña | Rol | Qué puede ver |
|---|---|---|---|
| `admin@quimica.cl` | `admin123` | ADMIN | Todos los reportes y resumen del día |
| `carlos.silva@quimica.cl` | `123456` | EMPLEADO | Solo su propio reporte de asistencia |
| `maria.gonzalez@quimica.cl` | `123456` | EMPLEADO | Solo su propio reporte de asistencia |

---

## Endpoints API Consumidos

Todos los endpoints están definidos en [`ApiService.java`](app/src/main/java/com/quimica/reportassist/api/ApiService.java).

| Método | Endpoint | Descripción | Rol |
|---|---|---|---|
| `POST` | `/api/auth/login` | Autenticación, retorna token y rol | Todos |
| `GET` | `/api/reports/late?startDate=&endDate=` | Empleados con llegadas tarde | ADMIN |
| `GET` | `/api/reports/early-exit?startDate=&endDate=` | Empleados con salidas anticipadas | ADMIN |
| `GET` | `/api/reports/absences?date=` | Ausencias por fecha | ADMIN |
| `GET` | `/api/attendances/summary/today` | Resumen de asistencia del día | ADMIN |
| `GET` | `/api/attendances/my-today/{employeeId}` | Asistencia de hoy del empleado | EMPLEADO |
| `GET` | `/api/attendances?employeeId={id}` | Historial completo del empleado | EMPLEADO |

### Ejemplo de respuesta login

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "role": "ADMIN",
  "employeeId": 1,
  "name": "Administrador"
}
```

---

## Funcionalidades por Rol

### Admin (`AdminDashboardActivity`)

- Resumen del día: total presentes, ausentes, tardanzas
- Pestañas para ver:
  - Reportes de llegadas tarde
  - Reportes de salidas anticipadas
  - Ausencias
- Filtro por rango de fechas
- Botón de cerrar sesión

### Empleado (`EmployeeReportActivity`)

- Ve su registro de asistencia del día actual
- Historial completo de sus asistencias
- Estado de entrada/salida con hora exacta
- Deslizar hacia abajo para refrescar (`SwipeRefreshLayout`)

---

## Actualización en Tiempo Real

La app implementa **polling automático cada 5 segundos** usando `Handler` + `Runnable` en Android. Esto significa que los datos se actualizan solos sin que el usuario tenga que hacer nada.

```java
// En AdminDashboardActivity y EmployeeReportActivity
private Handler refreshHandler = new Handler(Looper.getMainLooper());
private Runnable refreshRunnable = () -> {
    loadData(); // llama a la API
    refreshHandler.postDelayed(refreshRunnable, 5000);
};
```

Adicionalmente, el usuario puede deslizar hacia abajo para forzar una actualización inmediata.

> No se usa WebSockets ni Firebase para simplificar la arquitectura y mantener la compatibilidad con la API REST existente.

---

## Solución de Problemas Comunes

### ❌ Error de conexión al iniciar sesión

**Causa probable:** El backend no está corriendo o la IP es incorrecta.

**Solución:**
1. Verifica que el backend esté corriendo (`http://localhost:8080` debe responder en el Mac)
2. Revisa la `BASE_URL` en `ApiClient.java` según si usas emulador o dispositivo físico

---

### ❌ Error de SSL o `CLEARTEXT not permitted`

**Causa:** `android:usesCleartextTraffic` no está configurado.

**Solución:** Verifica que `AndroidManifest.xml` tenga:

```xml
<application
    android:usesCleartextTraffic="true"
    ...>
```

---

### ❌ Gradle falla con `Cannot find Java installation`

**Causa:** `gradle.properties` no apunta al JDK correcto.

**Solución:** El archivo `gradle.properties` debe contener:

```properties
org.gradle.java.home=/Applications/Android Studio Preview.app/Contents/jbr/Contents/Home
```

---

### ❌ La app muestra datos vacíos

**Causa:** No hay registros de asistencia en la base de datos todavía.

**Solución:** El backend tiene un endpoint para crear registros de asistencia. También se pueden insertar datos de prueba directamente desde la consola H2 en `http://localhost:8080/h2-console`.

---

## Tecnologías Usadas

| Tecnología | Versión | Uso |
|---|---|---|
| Android SDK | API 26–35 | Plataforma base |
| Java | 11 (source/target) | Lenguaje de programación |
| Retrofit 2 | 2.11.0 | Cliente HTTP para consumir la API |
| OkHttp | 4.12.0 | Interceptor de logs HTTP |
| Gson | 2.11.0 | Serialización/deserialización JSON |
| Material Components | 1.12.0 | Componentes visuales (Cards, Tabs, FAB) |
| SwipeRefreshLayout | 1.1.0 | Pull-to-refresh |
| RecyclerView | 1.3.2 | Listas de reportes |
| SharedPreferences | — | Sesión persistente del usuario |
| Spring Boot (backend) | — | API REST con H2 embebido |

---

## Repositorios

- **App Android:** `reporteAssist` (este repositorio)
- **Backend API:** `ControllerAssist` (repositorio separado)

