docker run -d -p 33061:3306 --name sampledb -e MYSQL_DATABASE=sampledb -e MYSQL_ROOT_PASSWORD=pass mysql --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci
docker start sampledb
docker exec -it  sampledb mysql -uroot -ppass
