@echo off
REM Script para actualizar la versión de la aplicación localmente (Windows)
REM Uso: update-version.bat [version]

set VERSION=%1
if "%VERSION%"=="" (
    for /f "tokens=2 delims==" %%I in ('wmic OS Get localdatetime /value') do set datetime=%%I
    set VERSION=dev-%datetime:~0,14%
)

echo === ACTUALIZANDO VERSION LOCAL ===
echo Nueva version: %VERSION%

REM Crear archivo temporal con la nueva versión
powershell -Command "(Get-Content 'src\main\resources\application.yml') -replace 'version:.*', 'version: \"%VERSION%\"' | Set-Content 'src\main\resources\application.yml.tmp'"

REM Si el reemplazo funcionó, mover el archivo
if exist "src\main\resources\application.yml.tmp" (
    move "src\main\resources\application.yml.tmp" "src\main\resources\application.yml"
    echo ✅ Version actualizada en application.yml
    echo.
    echo Contenido actual:
    findstr /C:"app:" /A:2 "src\main\resources\application.yml"
) else (
    echo ❌ Error al actualizar la version
)
