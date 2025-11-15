#!/bin/bash
# Script para actualizar la versión de la aplicación localmente
# Uso: ./update-version.sh [version]

VERSION=${1:-"dev-$(date +%Y%m%d%H%M%S)"}

echo "=== ACTUALIZANDO VERSION LOCAL ==="
echo "Nueva versión: $VERSION"

# Actualizar application.yml
if grep -q "app:" src/main/resources/application.yml; then
    # Si ya existe la sección app, actualizar version
    sed -i.bak "/^app:/,/^[[:alpha:]]/ s/version:.*/version: \"$VERSION\"/" src/main/resources/application.yml
else
    # Si no existe, agregar la sección completa
    echo "" >> src/main/resources/application.yml
    echo "app:" >> src/main/resources/application.yml
    echo "  version: \"$VERSION\"" >> src/main/resources/application.yml
fi

echo "✅ Versión actualizada en application.yml"
echo ""
echo "Contenido actual:"
grep -A2 "^app:" src/main/resources/application.yml

# Limpiar archivo de backup
rm -f src/main/resources/application.yml.bak 2>/dev/null || true
