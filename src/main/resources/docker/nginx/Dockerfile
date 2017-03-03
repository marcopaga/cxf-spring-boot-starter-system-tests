FROM nginx:alpine
EXPOSE 80
RUN apk add --no-cache curl
COPY nginx.conf /etc/nginx/nginx.conf
HEALTHCHECK --interval=10s --timeout=5s \
  CMD curl -f http://localhost/proxied-weather/Weather?wsdl || exit 1
