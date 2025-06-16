# Stage 1 - build
FROM node:20 as builder

WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .
RUN npm run build

# Stage 2 - serve build
FROM node:20 as runner

WORKDIR /app

# Copia solo i file necessari per l'esecuzione
COPY --from=builder /app/public ./public
COPY --from=builder /app/.next ./.next
COPY --from=builder /app/package.json ./package.json

# (Opzionale) se usi un server personalizzato Next.js (es. con `server.js`)
# COPY --from=builder /app/node_modules ./node_modules
# COPY --from=builder /app/server.js ./server.js

RUN npm install --omit=dev

EXPOSE 3000

CMD ["npm", "start"]
