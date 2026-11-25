# ReportAssist – Aplicación Android de Control y Reportes de Asistencia

**ReportAssist** es una aplicación móvil nativa para Android desarrollada en Java, diseñada para el control y visualización de asistencia de personal en tiempo real. Se conecta mediante API REST al backend **ControllerAssist** (Spring Boot) e incorpora un **modo offline/demo transparente** para pruebas y demostraciones inmediatas sin requerir un servidor activo.

---

## 📋 Tabla de Contenidos

1. [Características Principales](#-características-principales)
2. [Roles de Usuario y Funcionalidades](#-roles-de-usuario-y-funcionalidades)
3. [Modo Demo / Fallback Offline](#-modo-demo--fallback-offline)
4. [Estructura del Proyecto](#-estructura-del-proyecto)
5. [Requisitos e Instalación](#-requisitos-e-instalación)
6. [Credenciales de Prueba](#-credenciales-de-prueba)
7. [Endpoints de la API REST](#-endpoints-de-la-api-rest)
8. [Tecnologías Utilizadas](#-tecnologías-utilizadas)

---

## ✨ Características Principales

* 🔒 **Autenticación Basada en Roles:** Acceso diferenciado para el Administrador de Recursos Humanos y Empleados de planta/operaciones.
* 📊 **Panel de Control Administrativo (Dashboard):** Indicadores visuales en tarjetas (Atrasos, Salidas Anticipadas, Ausencias) y conmutación rápida entre reportes filtrados.
* 📅 **Control de Asistencia del Empleado:** Tarjeta de estado en jornada actual (Horarios de Entrada/Salida, insignia de cumplimiento) e historial completo de marcajes anteriores.
* ⚡ **Sincronización en Tiempo Real:** Polling automático cada 5 segundos (`Handler` + `Looper`) y soporte para refresco deslizante (*SwipeRefreshLayout*).
* 🔄 **Modo Offline / Demo Automático:** Interceptor de red (`MockFallbackInterceptor`) que conmuta a respuestas JSON simuladas si el servidor local no está disponible, garantizando que la app no colapse ni quede inoperativa.
* 🌐 **Configuración Dinámica de Servidor:** Campo ejecutable en pantalla de login para cambiar la IP/Puerto del servidor (ej. `10.0.2.2:8080` para emulador o `192.168.x.x:8080` para móvil).

---

## 👤 Roles de Usuario y Funcionalidades

### 👑 Panel Administrador (`AdminDashboardActivity`)
- **Resumen Estadístico Diario:** Tarjetas con conteo en vivo de trabajadores con Atraso, Salidas y Ausentes.
- **Pestañas de Reportes:**
  - ⏰ **Reporte de Atrasos (RE-01):** Lista de ingresos posteriores a las 09:30 AM con minutos de retraso.
  - 🚪 **Reporte de Salidas Anticipadas (RE-02):** Lista de retiros previos a las 17:30 PM.
  - ❌ **Reporte de Inasistencias (RE-03):** Personal ausente en la jornada.
- **Auto-refresco:** Sincronización continua cada 5 segundos y botón de cierre de sesión seguro.

### 👷 Registro de Empleado (`EmployeeReportActivity`)
- **Estado de Hoy:** Visualización de horario de entrada, salida proyectada e insignia de estado (**A TIEMPO**, **ATRASADO**, **SIN REGISTRO**).
- **Historial Personal:** Lista ordenada de días trabajados con horas acumuladas y notas.

---

## 🛡️ Modo Demo / Fallback Offline

La aplicación incluye un interceptor de red (`MockFallbackInterceptor`) integrado en el cliente HTTP OkHttp/Retrofit.

```
                  ┌───────────────────────────────┐
                  │   Solicitud HTTP desde App    │
                  └──────────────┬────────────────┘
                                 │
                   ¿Servidor Backend responde?
                       ┌─────────┴─────────┐
                      SÍ                  NO (Offline / Error)
                       │                   │
             ┌─────────▼────────┐  ┌───────▼────────────────────────┐
             │ Respuesta REST   │  │ Respuesta 200 OK Simulada      │
             │ Real del Server  │  │ (Mock Fallback Interceptor)    │
             └──────────────────┘  └────────────────────────────────┘
```

- **Si el backend está ACTIVO:** Consume la API REST real del servidor.
- **Si el backend está OFFLINE / APAGADO:** Genera inmediatamente datos simulados ricos y estructurados, permitiendo evaluar la interfaz y flujos sin ningún error de red.

---

## 📁 Estructura del Proyecto

```
reporteAssist/
├── app/
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/quimica/reportassist/
│       │   ├── activity/
│       │   │   ├── LoginActivity.java          ← Inicio de sesión y botones de prueba rápida
│       │   │   ├── AdminDashboardActivity.java ← Panel del Administrador con pestañas
│       │   │   └── EmployeeReportActivity.java ← Reporte de asistencia del Empleado
│       │   ├── adapter/
│       │   │   ├── ReportAdapter.java          ← Adaptador de tarjetas de reportes admin
│       │   │   └── EmployeeAttendanceAdapter.java ← Adaptador de historial de asistencia
│       │   ├── api/
│       │   │   ├── ApiClient.java              ← Cliente Retrofit con OkHttp
│       │   │   ├── ApiService.java             ← Definición de interfaces/endpoints REST
│       │   │   └── MockFallbackInterceptor.java ← Interceptor offline/demo transparente
│       │   ├── model/
│       │   │   ├── LoginRequest.java
│       │   │   ├── LoginResponse.java
│       │   │   ├── ReportItem.java
│       │   │   ├── AttendanceRecord.java
│       │   │   └── DashboardSummary.java
│       │   └── util/
│       │       └── SessionManager.java         ← Manejo de sesión persistente (SharedPreferences)
│       └── res/
│           ├── layout/                        ← Diseños XML responsivos y Material Design
│           └── values/                        ← Colores, cadenas de texto y temas
├── gradle/
│   └── libs.versions.toml                     ← Version Catalogs (Dependencies)
├── build.gradle
├── settings.gradle
└── README.md
```

---

## 🛠️ Requisitos e Instalación

1. **Entorno Recomendado:** Android Studio (Ladybug / Preview o superior).
2. **Versión de SDK:**
   * `minSdk`: 26 (Android 8.0 Oreo)
   * `targetSdk`: 36 (Android 16 / Baklava)
   * `compileSdk`: 36
3. **Pasos para Ejecutar:**
   1. Clona o abre la carpeta del proyecto en Android Studio.
   2. Deja que Gradle sincronice las dependencias automáticamente.
   3. Selecciona un emulador Android (AVD) o conecta tu dispositivo por USB.
   4. Presiona ▶ **Run** (`Shift + F10`).

---

## 🔑 Credenciales de Prueba

En la pantalla de inicio de sesión se incluyen **botones de prueba rápida** que autocompletan los datos al hacer clic:

| Botón Rápido | Correo | Contraseña | Rol Asignado |
|---|---|---|---|
| **Admin** | `admin@quimica.cl` | `admin123` | **ADMIN** |
| **Carlos (1)** | `carlos.silva@quimica.cl` | `123456` | **EMPLEADO** |
| **María (2)** | `maria.gonzalez@quimica.cl` | `123456` | **EMPLEADO** |

---

## 🌐 Endpoints de la API REST

| Método | Endpoint | Descripción | Acceso |
|---|---|---|---|
| `POST` | `/api/auth/login` | Autenticación y obtención de rol | Público |
| `GET` | `/api/attendances/summary/today` | Resumen de contadores del día | ADMIN |
| `GET` | `/api/reports/late` | Reporte de llegadas con atraso | ADMIN |
| `GET` | `/api/reports/early-exit` | Reporte de salidas anticipadas | ADMIN |
| `GET` | `/api/reports/absences` | Reporte de inasistencias | ADMIN |
| `GET` | `/api/attendances/my-today/{employeeId}` | Asistencia del día del empleado | EMPLEADO |
| `GET` | `/api/attendances?employeeId={id}` | Historial de asistencias pasadas | EMPLEADO |

---

## 💻 Tecnologías Utilizadas

* **Lenguaje:** Java 11
* **Arquitectura:** Android Clean / MVC con componentes Material Design 3
* **Conectividad:** Retrofit 2 (`2.11.0`) + OkHttp 4 (`4.12.0`)
* **Parsing JSON:** Gson (`2.11.0`)
* **UI Components:** RecyclerView, SwipeRefreshLayout, MaterialCardView, TextInputLayout
* **Persistencia Local:** SharedPreferences (`SessionManager`)
