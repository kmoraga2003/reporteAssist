# ReportAssist – Aplicación Móvil de Asistencia (Android)

Aplicación móvil desarrollada en **Android (Java)** para la empresa química, conectada a la API REST de **ControllerAssist** en Spring Boot. Permite visualizar informes de asistencia en **tiempo real** con control de acceso por roles.

---

## 👥 Cuentas de Acceso y Roles

| Rol | Correo | Contraseña | Alcance y Permisos en la App Móvil |
| :--- | :--- | :--- | :--- |
| **ADMIN** | `admin@quimica.cl` | `admin123` | **Panel Global de Reportes:** Visualiza contadores del día y pestañas en tiempo real para: <br>• ⏰ **Atrasos:** Colaboradores con entrada posterior a las 09:30 AM.<br>• 🚪 **Salidas Anticipadas:** Colaboradores con retiro previo a las 17:30 PM.<br>• ❌ **Inasistencias:** Empleados sin registros hoy. |
| **EMPLEADO (1)** | `carlos.silva@quimica.cl` | `123456` | **Mi Reporte Personal:** Visualiza su tarjeta de hoy (Entrada, Salida, Horas cumplidas y Estado) y su historial cronológico de asistencias. |
| **EMPLEADO (2)** | `maria.gonzalez@quimica.cl` | `123456` | **Mi Reporte Personal:** Solo ve sus propias marcas y horas trabajadas. |

> 💡 **Nota:** La pantalla de Login incluye botones de acceso rápido (**Admin**, **Carlos (1)**, **María (2)**) para rellenar credenciales con un solo toque durante demostraciones o evaluaciones.

---

## ⚡ Visualización en Tiempo Real (Real-Time)

Para garantizar que los datos se actualicen al instante cuando un colaborador marque en la plataforma web:
1. **Auto-Polling cada 5 segundos:** Un hilo en segundo plano (`Handler / Runnable`) sincroniza los reportes automáticamente cada 5 segundos mientras la aplicación está visible en pantalla.
2. **Swipe-to-Refresh:** El usuario puede deslizar la lista hacia abajo con el dedo para forzar la actualización manual en cualquier momento.

---

## 🌐 Configuración de Conexión (IP del Servidor)

En la pantalla de Login hay un campo editable **"IP del Servidor"**:
* **En el Emulador de Android Studio:** Usa el valor por defecto `10.0.2.2:8080` (apunta al `localhost` de tu computador).
* **En un Teléfono Móvil Físico:** Ingresa la IP local de tu computador en la red Wi-Fi (por ejemplo, `192.168.1.105:8080`).

---

## 🛠️ Endpoints de la API Consumidos (Retrofit 2)

* `POST /api/auth/login`: Validación de credenciales y obtención de rol (`ADMIN` o `EMPLEADO`).
* `GET /api/reports/late`: Reporte de atrasos (RE-01).
* `GET /api/reports/early-exit`: Reporte de salidas anticipadas (RE-02).
* `GET /api/reports/absences`: Reporte de colaboradores ausentes (RE-03).
* `GET /api/attendances/summary/today`: Resumen de contadores del día para el Administrador.
* `GET /api/attendances/my-today/{id}`: Registro de asistencia de hoy para el empleado conectado.
* `GET /api/attendances?employeeId={id}`: Historial completo de marcas del empleado.

---

## 🚀 Cómo abrir y ejecutar el proyecto en Android Studio

1. Abre **Android Studio**.
2. Selecciona **File -> Open...** (o botón *Open* en la pantalla de bienvenida).
3. Selecciona la carpeta **`reporteAssist`** ubicada en tu Escritorio.
4. Espera que Gradle sincronice las dependencias automáticamente.
5. Selecciona tu emulador o dispositivo físico y presiona **Run (Play ▶)**.
