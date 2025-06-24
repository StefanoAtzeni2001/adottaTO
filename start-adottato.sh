#!/bin/bash
set -e

echo "🟢🟢🟢 Avvio di Minikube..."
minikube start

echo "🟢🟢🟢  Configurazione del Docker Engine di Minikube..."
eval $(minikube -p minikube docker-env)

echo "🟢🟢🟢  Build delle immagini Docker con Docker Compose..."
docker compose build

echo "🟢🟢🟢  Applicazione del namespace..."
kubectl apply -f k8s/namespace.yml

echo "🟢🟢🟢 Impostazione del namespace 'adottato' come default..."
kubectl config set-context --current --namespace=adottato

echo "🟢🟢🟢  Applicazione dei manifest nella cartella k8s..."
kubectl apply -R -f k8s/

echo "🟡🟡🟡 Attesa che tutti i pods siano in stato 'Running'"
while true; do
    STATUS=$(kubectl get pods --no-headers | awk '{print $3}' | grep -vE 'Running' || true)
    if [[ -z "$STATUS" ]]; then
        echo "🟢🟢🟢 Tutti i pods sono pronti."
        break
    fi
    echo "🟡 In attesa... 🟡 "
    sleep 5
done

echo "🟢🟢🟢  Avvio del tunnel Minikube in un nuovo terminale..."
if command -v gnome-terminal &> /dev/null; then
    gnome-terminal -- bash -c "minikube tunnel; exec bash"
else
    echo "🔴🔴🔴 Terminale grafico non trovato, avvia manualmente: minikube tunnel"
fi

echo "🟢🟢🟢  Apro http://localhost:3000 nel browser..."
xdg-open http://localhost:3000 &> /dev/null || open http://localhost:3000 || echo "➡️ Apri manualmente: http://localhost:3000"