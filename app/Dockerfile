# Build stage
FROM node:18-alpine AS builder

# Install build dependencies
RUN apk add --no-cache python3 make g++

# Set working directory
WORKDIR /app

# Copy package files
COPY package*.json ./

# Install dependencies
RUN npm ci

# Copy source code
COPY . .

# Development stage
FROM node:18-alpine AS development

WORKDIR /app

# Copy package files and dependencies from builder
COPY --from=builder /app/package*.json ./
COPY --from=builder /app/node_modules ./node_modules

# Copy source code
COPY . .

# Expose development port
EXPOSE 19000
EXPOSE 19001
EXPOSE 19002

# Start development server
CMD ["npm", "start", "--", "--port", "19000"]

# Production stage
FROM node:18-alpine AS production

WORKDIR /app

# Copy package files and dependencies from builder
COPY --from=builder /app/package*.json ./
COPY --from=builder /app/node_modules ./node_modules

# Copy source code
COPY . .

# Build the app
RUN npm run build

# Expose production port
EXPOSE 80

# Start production server
CMD ["npm", "run", "web"] 