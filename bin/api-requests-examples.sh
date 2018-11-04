#create user
curl -d '{"email":"user@gmail.com", "password":"pass", "role":"ROLE_NORMAL"}' -H "Content-Type: application/json" -X POST http://localhost:8080/users
curl -d '{"email":"countriesuser@gmail.com", "password":"countriespass", "role":"ROLE_COUNTRIES"}' -H "Content-Type: application/json" -X POST http://localhost:8080/users
#authenticate
curl -d '{"email":"user@gmail.com", "password":"pass"}' -H "Content-Type: application/json" -X POST http://localhost:8080/auth
curl -d '{"email":"countriesuser@gmail.com", "password":"countriespass"}' -H "Content-Type: application/json" -X POST http://localhost:8080/auth
#create country
curl -d '{"country_name":"Spain"}' -H "Content-Type: application/json" -H "Authorization: token" -X POST http://localhost:8080/countries
#get countries
curl -H "Content-Type: application/json" -H "Authorization: token" http://localhost:8080/countries
