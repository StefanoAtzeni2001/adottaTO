# Progetto TAASS: adottaTO

Progetto universitario per il corso di Tecniche e Architetture Avanzate per lo Sviluppo Software (2024/2025).
Sviluppo di un'applicazione a microservizi utilizzando Spring Boot, React, Docker e Kubernetes.

## Prerequisiti
- **Git** - per clonare il repository del progetto
- **Docker** - per la containerizzazione delle applicazioni
- **Minikube** - cluster Kubernetes locale per lo sviluppo (include kubectl integrato)

**Nota**: Non è necessario installare kubectl separatamente, poiché utilizzeremo quello integrato in Minikube.

## Verifica delle Installazioni

```bash
git --version
docker --version
minikube version
```

### Configurazione kubectl (Raccomandato)

Per semplificare l'uso dei comandi Kubernetes, configurare un alias che ci permette di usare `kubectl` direttamente invece di dover scrivere ogni volta `minikube kubectl --`.
```bash
# Apri il file .bashrc
nano ~/.bashrc
# Aggiungi questa linea alla fine del file
alias kubectl="minikube kubectl --"
```
Per verificare che kubectl sia configurato:
```bash
kubectl version --client
```

## Clonazione del Progetto
```bash
git clone https://github.com/StefanoAtzeni2001/adottaTO.git
cd adottaTO
```
---
## Deploy Manuale Passo-Passo

### Passo 1: Avvio di Minikube
```bash
minikube start
```

### Passo 2: Configurazione dell'Ambiente Docker di Minikube
Per costruire immagini Docker che siano direttamente disponibili al cluster Minikube senza doverle pushare su un registry esterno

```bash
eval $(minikube -p minikube docker-env)
```

**NB**: Questo comando imposta le variabili d'ambiente Docker solo per la sessione corrente del terminale.

### Passo 3: Build delle Immagini Docker
Per costruire tutte le immagini Docker definite nel file `docker-compose.yml`:
```bash
docker compose build
```

### Passo 4: Creazione del Namespace Kubernetes
Isolare le risorse della applicazione in un Namespace dedicato applicando il file namespace.yml
```bash
kubectl apply -f k8s/namespace.yml
```

Impostare il namespace `adottato` come default per i comandi kubectl nella sessione corrente:
```bash
kubectl config set-context --current --namespace=adottato
```

### Passo 5: Deploy su Kubernetes

Applicare tutti i manifest Kubernetes presenti nella cartella `k8s/`:
```bash
kubectl apply -R -f k8s/
```

### Passo 6: Verifica dello Stato del Deploy

Dopo aver applicato tutti i file YAML, aspettare che tutti i Pod siano nello stato **Running**:

```bash
kubectl get pods -w
```
### Passo 7: Avvio del Tunnel Minikube
Serve per rendere raggiungibili i servizi LoadBalancer da localhost
, eseguire in un **nuovo terminale**:
```bash
minikube tunnel
```

**NB**: Questo comando **deve rimanere in esecuzione** per tutto il tempo di utilizzo dell'applicazione.

### Passo 8: Accesso all'Applicazione

Aprire il browser e accedere a: **http://localhost:3000**

---
## Deploy Automatico

Per semplificare il processo di deploy, è disponibile uno script automatico che esegue tutti i passaggi sopra descritti.

### Rendere eseguibile lo script:
```bash
chmod +x start-adottato.sh
```

### Eseguire lo script:
```bash
./start-adottato.sh
```

Lo script eseguirà automaticamente tutti i passaggi e aprirà l'applicazione nel browser.

---
## Comandi Utili per il Monitoraggio

- **Visualizzare tutti i pod**: `kubectl get pods`
- **Visualizzare i servizi**: `kubectl get services`
- **Visualizzare i deployment**: `kubectl get deployments`
- **Log di un pod specifico**: `kubectl logs <nome-pod>`
- **Descrizione dettagliata di un pod**: `kubectl describe pod <nome-pod>`
- **Monitoraggio in tempo reale**: `kubectl get pods -w`

## Pulizia dell'Ambiente

Per rimuovere completamente il deploy:

```bash
minikube stop
minikube delete
```
---
## Struttura del Progetto

```
adottaTO/
├── adoption-post-service/     # Microservizio per la gestione degli annunci di adozione
├── chat-service/              # Microservizio per la gestione delle chat
├── email-service/             # Microservizio per l'invio di email
├── frontend/                  # Applicazione frontend Next React
├── gateway-service/           # API Gateway per il routing delle richieste
├── saved-search-service/      # Microservizio per le ricerche salvate
├── user-service/              # Microservizio per la gestione utenti
├── shared-dtos/               # Data Transfer Objects condivisi tra i servizi
├── k8s/                       # Manifest Kubernetes
├── docker-compose.yml         # Definizione dei servizi Docker per produzione
├── start-adottato.sh          # Script di deploy automatico
```

--- 
## API REST

### adoption-post-service:8081
```
/adoption/get/list
/adoption/get/post/{postId}
/adoption/post/create
/adoption/post/delete/{postId}
/adoption/post/update/{postId}
/adoption/my/owned
/adoption/my/adopted
```
### chat-service:8082
```
/chat/send
/chat/chats
/chat/history
/chat/unread
/chat/send-request
/chat/cancel-request
/chat/accept-request
/chat/reject-request
```

### user-service:8083
```
/auth/google-registration
/auth/login
/auth/register
/auth/oauth-jwt
/user/get/profile/{id}
/user/get/email/{id}
/user/my-profile/update
/user/my-profile
```

### saved-search-servcice:8084
```
/search/save
/search/delete/{searchId}
/search/my/saved
```

### gateway-service:8090
Tutte le chiamate alle API elencate sopra passano attraverso il gateway, che effettua un semplice forwarding verso i rispettivi microservizi

