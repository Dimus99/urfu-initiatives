#!/usr/bin/env bash
# create random password
PASSWD="user"
USERNAME="user"
# replace "-" with "_" for database username
MAINDB="databaseSpring"

if [ -f /root/.my.cnf ]; then
    mysql -e "CREATE DATABASE ${MAINDB};"
    mysql -e "CREATE USER ${USERNAME}@localhost IDENTIFIED BY '${PASSWD}';"
    mysql -e "GRANT ALL PRIVILEGES ON ${MAINDB}.* TO '${USERNAME}'@'localhost';"
    mysql -e "FLUSH PRIVILEGES;"

# If /root/.my.cnf doesn't exist then it'll ask for root password
else
    echo "Please enter root user MySQL password!"
    echo "Note: password will be hidden when typing"
    read -sp rootpasswd
    mysql -uroot -p${rootpasswd} -e "CREATE DATABASE ${MAINDB};"
    mysql -uroot -p${rootpasswd} -e "CREATE USER ${USERNAME}@localhost IDENTIFIED BY '${PASSWD}';"
    mysql -uroot -p${rootpasswd} -e "GRANT ALL PRIVILEGES ON ${MAINDB}.* TO '${USERNAME}'@'localhost';"
    mysql -uroot -p${rootpasswd} -e "FLUSH PRIVILEGES;"
fi