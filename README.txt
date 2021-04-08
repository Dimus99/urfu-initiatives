При создании использовалось:
start.spring.io
spring.io/guides
https://www.youtube.com/watch?v=FyZFK4LBjj0
https://www.youtube.com/watch?v=7uxROJ1nduk

mysql-8.0.23
sudo apt install mysql-client-core-8.0

надо самому создать бд, если ещё нет
sudo mysql -u root -p
[PASSWORD]
CREATE DATABASE [NAME];
// если нужно, дать права на бд пользователю,
//я создал пользователя user в mysql и дал ему права
а так же в бд нужны 2 таблицы - users и initiatives согласно моделям
CREATE USER 'user'@'localhost' IDENTIFIED BY 'user';
GRANT ALL PRIVILEGES ON databaseSpring . * TO 'user'@'localhost';

java 15
https://www.javahelps.com/2020/09/install-oracle-jdk-15-on-linux.html

start
mvn install
java -jar target/demo-0.0.1-SNAPSHOT.jar

or
mvn spring-boot:run -Drun.jvmArguments="-Dspring.profiles.active=local"