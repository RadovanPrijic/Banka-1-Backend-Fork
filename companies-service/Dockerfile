FROM node as builder
# Create app directory
WORKDIR /usr/src/app
# Install app dependencies
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM node:slim
ENV NODE_ENV production
USER node
# Create app directory
WORKDIR /usr/src/app
# Install app dependencies
COPY package*.json ./
RUN npm ci --production
COPY --from=builder /usr/src/app/dist ./dist

EXPOSE 8083
CMD [ "node", "dist/service.js" ]

# docker login harbor.k8s.elab.rs -u <username>

# docker build -t harbor.k8s.elab.rs/banka-1/companies-service:latest .
# docker push harbor.k8s.elab.rs/banka-1/companies-service:latest