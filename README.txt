О проекте:
UPD: Проект недоделан, полный проект в другом репрозитории
Реализация согласно ТЗ, в большинстве своем.
сайт на Java Spring Boot


Примечание: возникли сложности с разграничением прав при Oauth2 авторизации. так и не смог исправить.
(конкретно для спрингового разграничения, вроде аннотации @PreAuthorize)

Сайт опубликован(скорее всего) тут: http://initiatives.domen-test-urfu.ru:8181
управление пользователями по адресу /users для админа


Папки:
    files - тут хранятся все прикрепленные файлы
    Scripts - скрипты для запуска
остальное стандартно

При создании использовалось:
start.spring.io
spring.io/guides
https://www.youtube.com/watch?v=FyZFK4LBjj0
https://www.youtube.com/watch?v=7uxROJ1nduk
и много других источников

mysql-8.0.23
sudo apt install mysql-client-core-8.0

надо самому создать бд, если ещё нет
sudo mysql -u root -p
[PASSWORD]
CREATE DATABASE [NAME];

БД - databaseSpring
на user@localhost pass: user

java 15
https://www.javahelps.com/2020/09/install-oracle-jdk-15-on-linux.html
