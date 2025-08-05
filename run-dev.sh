#!/bin/bash

# Script para executar o plugin em modo de desenvolvimento

echo "Iniciando o plugin Dart Barrel File Generator em modo de desenvolvimento..."
echo "Compilando o plugin..."

# Compilar o plugin
./gradlew buildPlugin

# Verificar se a compilação foi bem-sucedida
if [ $? -ne 0 ]; then
    echo "Erro na compilação do plugin. Verifique os erros acima."
    exit 1
fi

echo "Compilação concluída com sucesso."
echo "Iniciando a IDE com o plugin..."

# Executar a IDE com o plugin
./gradlew runIde

echo "IDE encerrada."
