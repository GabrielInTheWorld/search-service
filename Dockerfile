
FROM node:13

WORKDIR /app

RUN rm -rf node_modules
RUN rm -rf package-lock.json

COPY node/package.json package.json
RUN npm install

# Application lays in /app/src
COPY node ./

CMD [ "npm", "start" ]