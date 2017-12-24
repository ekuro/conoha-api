### 使い方
テナント、ユーザ、パスワードはシステム起動時のプロパティで設定します。
```
$ git clone https://github.com/ekuro/conoha-api.git
$ apt-get install openjdk-8-jdk
$ apt-get install gradle
$ cd conoha-api
$ gradle -Dtenant=xxx -Dusername=xxx -Dpassword=xxx run
```
