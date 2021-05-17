build-dev:
	docker build -t search-service:latest .

run-dev: | build-dev
	docker-compose -f docker-compose.yml up