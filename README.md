
RUN Client -

\todo

1. docker build -t todo-app .
2. docker run -d -p 4200:80 todo-app
3. ng test

RUN API -

\springboot-testing

1. /mvnw clean install -DskipTests
2. docker-compose build
3. docker-compose up 

==to run tests with coverage==
\springboot-testing\src\test\java\com\ntloc\demo
1. right click
2. more run debug -> run with covergae (in intelij)

