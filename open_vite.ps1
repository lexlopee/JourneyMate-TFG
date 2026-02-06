# Esperar a que Vite genere el archivo con el puerto
$portFile = "journeymate-frontend/.vite/port.json"

Write-Host "Esperando a que Vite genere el puerto..."

while (!(Test-Path $portFile)) {
    Start-Sleep -Milliseconds 300
}

# Leer el puerto real
$port = Get-Content $portFile | ConvertFrom-Json | Select-Object -ExpandProperty port

$url = "http://localhost:$port"
Write-Host "Abriendo navegador en: $url"
Start-Process $url
